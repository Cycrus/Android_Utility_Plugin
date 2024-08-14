package com.example.unityplugin;

import android.app.Activity;
import android.view.KeyEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerControl {
    private final int port = 9;
    private DatagramSocket serverManager;
    private static Activity unityActivity;
    private boolean serverReachable = false;
    private boolean networkReachable = false;
    private int pingTimeout = 1000;
    private String gatewayAddress;
    private String mainServerAddress;
    private String wolRouterAddress;
    private String mainServerMac;

    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void initializeModule(String gatewayAddress, String mainServerAddress,
                                 String wolRouterAddress, String mainServerMac) throws SocketException {
        this.gatewayAddress = gatewayAddress;
        this.mainServerAddress = mainServerAddress;
        this.wolRouterAddress = wolRouterAddress;
        this.mainServerMac = mainServerMac;
        serverManager = new DatagramSocket();
        startPingWorkers();
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("[ERROR] Server: Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] Server: Invalid hex digit in MAC address.");
        }
        return bytes;
    }

    public void sendWakeOnLan(String macAddress, String broadcastAddress) throws IOException {
        byte[] macBytes = getMacBytes(macAddress);
        byte[] packetBytes = new byte[6 + 16 * macBytes.length];

        // Fill the first 6 bytes with 0xFF
        for (int i = 0; i < 6; i++) {
            packetBytes[i] = (byte) 0xFF;
        }

        // Fill the remaining bytes with the MAC address
        for (int i = 6; i < packetBytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, packetBytes, i, macBytes.length);
        }

        InetAddress address = InetAddress.getByName(broadcastAddress);
        DatagramPacket packet = new DatagramPacket(packetBytes, packetBytes.length, address, port);
        serverManager.send(packet);
    }

    public void sendMacOverTcp(String macAddress, String address, int port) throws IOException {
        Thread thread = new Thread(() -> {
            try {
                Socket tcpSocket = new Socket(address, port);
                OutputStream msgOutput = tcpSocket.getOutputStream();
                msgOutput.write(macAddress.getBytes());
                msgOutput.flush();
                tcpSocket.close();
            } catch (IOException e) {
                System.err.println("[Error] Cannot send TCP message. " + e.toString());
            }
        });
        thread.start();
    }

    public static void executeRemoteCommand(
            String username,
            String password,
            String hostname,
            int port,
            String command) throws Exception {

        Thread thread = new Thread(() -> {
            try {
                JSch jsch = new JSch();
                Session session = jsch.getSession(username, hostname, port);
                session.setPassword(password);

                // Avoid asking for key confirmation
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);

                session.connect(5000);

                // SSH Channel
                ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
                ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                channelExec.setOutputStream(responseStream);

                // Execute command
                channelExec.setCommand(command);
                channelExec.connect();

                // Disconnect the channel and session
                channelExec.disconnect();
                session.disconnect();
            } catch (JSchException e) {
                System.err.println("[Error] Cannot send SSH command. " + e.toString());
            }
        });
        thread.start();
    }

    public void startServer() throws IOException {
        sendMacOverTcp(mainServerMac, wolRouterAddress, 3001);
    }

    public void shutdownServer(String username, String password, String hostname) throws Exception {
        executeRemoteCommand(username, password, hostname,
        22, "sudo shutdown now");
    }

    public boolean isNetworkReachable() {
        return networkReachable;
    }

    public boolean isServerReachable() {
        return serverReachable;
    }

    private boolean isAddressReachable(String address) {
        try {
            InetAddress inet = InetAddress.getByName(address);
            return inet.isReachable(pingTimeout);
        } catch (IOException e) {
            System.out.println("[Ping] Cannot ping address. " + e.toString());
            return false;
        }
    }

    private void startPingWorkers() {
        System.out.println("[Ping] Starting network ping thread.");
        ScheduledExecutorService networkPingScheduler = Executors.newScheduledThreadPool(1);
        Runnable networkPingTask = () -> {
            networkReachable = isAddressReachable(gatewayAddress);
        };
        networkPingScheduler.scheduleAtFixedRate(networkPingTask, 0, pingTimeout, TimeUnit.MILLISECONDS);

        System.out.println("[Ping] Starting server ping thread.");
        ScheduledExecutorService serverPingScheduler = Executors.newScheduledThreadPool(1);
        Runnable serverPingTask = () -> {
            if(networkReachable) {
                serverReachable = isAddressReachable(mainServerAddress);
            }
        };
        serverPingScheduler.scheduleAtFixedRate(serverPingTask, 0, pingTimeout, TimeUnit.MILLISECONDS);
    }
}
