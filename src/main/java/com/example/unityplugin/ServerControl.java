package com.example.unityplugin;

import android.app.Activity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;

public class ServerControl {
    private final int port = 9;
    private DatagramSocket serverManager;
    private static Activity unityActivity;

    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void initializeModule() throws SocketException {
        serverManager = new DatagramSocket();
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

    public static String executeRemoteCommand(
            String username,
            String password,
            String hostname,
            int port,
            String command) throws Exception {

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();

        // SSH Channel
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        channelExec.setOutputStream(responseStream);

        // Execute command
        channelExec.setCommand(command);
        channelExec.connect();

        // Read the output from the command
        while (channelExec.isConnected()) {
            Thread.sleep(100);
        }

        String responseString = responseStream.toString();

        // Disconnect the channel and session
        channelExec.disconnect();
        session.disconnect();

        return responseString;
    }

    public void startServer() throws IOException {
        sendWakeOnLan("FC:34:97:A5:F1:3C", "192.168.50.255");
    }

    public void shutdownServer(String username, String password, String hostname) throws Exception {
        executeRemoteCommand(username, password, hostname,
        22, "sudo shutdown now");
    }
}
