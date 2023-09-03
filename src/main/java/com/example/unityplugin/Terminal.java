package com.example.unityplugin;

import android.app.Activity;
import android.app.Application;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class Terminal {
    private Application application;
    ProcessBuilder processBuilder;
    private Process process;
    private OutputStream stdin;
    private InputStream stdout;
    private InputStream stderr;
    private BufferedReader outStream;
    private BufferedReader errStream;
    private int shPid;
    private String lastCommand;

    public boolean startProcess(String[] processName) {
        try {
            System.out.println("[Terminal] Starting process: " + Arrays.toString(processName));
            if (processName.length == 1)
                process = Runtime.getRuntime().exec(processName[0]);
            else if (processName.length > 1)
                process = Runtime.getRuntime().exec(processName);
            else
                return false;

            shPid = getPid();
            stdin = process.getOutputStream();
            stdout = process.getInputStream();
            stderr = process.getErrorStream();
            outStream = new BufferedReader(new InputStreamReader(stdout));
            errStream = new BufferedReader(new InputStreamReader(stderr));
            return true;
        }
        catch(IOException e)
        {
            System.out.println("[Terminal] ERROR: " + e);
            return false;
        }
    }

    public void stdIn(String data) {
        try {
            System.out.println("[Terminal] Sending <" + data + "> to stdin.");
            data += "\n";
            stdin.write(data.getBytes());
            stdin.flush();
            String[] dataList = data.split(" ", 2);
            lastCommand = dataList[0];
        }
        catch(IOException e)
        {
            System.out.println("[Terminal] In ERROR: " + e);
        }
    }

    public int getChildPid(String procName) {
        Terminal tmpTerminal = new Terminal();
        String[] command = new String[] {"sh", "-c", "ps"};
        tmpTerminal.startProcess(command);
        int childPid = -1;

        for(int i = 0; i < 1000000; i++) {
            String output = tmpTerminal.stdOut();
            if(output == null)
                continue;

            PsOutput currProc = new PsOutput(output);
            if(!currProc.valid) continue;

            if((currProc.PPID == shPid) && procName.contains(currProc.NAME))
            {
                childPid = currProc.PID;
                break;
            }
        }

        tmpTerminal.closeShell();
        return childPid;
    }

    public String stdOut() {
        try {
            if(!outStream.ready()) {
                return null;
            }

            String line;
            String out = "";

            line = outStream.readLine();
            out = out + line;

            return out;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public String stdErr() {
        try {
            if(!errStream.ready()) {
                return null;
            }

            String line;
            String out = "";

            line = errStream.readLine();
            out = out + line;

            return out;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public void interruptLastProcess() throws IOException {
        int interPid = getChildPid(lastCommand);
        if(interPid == -1)
            return;
        System.out.println("[Terminal] SIGINT to: " + interPid);
        Runtime.getRuntime().exec("kill -SIGINT " + interPid);
    }

    public void killLastProcess() throws IOException {
        int killPid = getChildPid(lastCommand);
        if(killPid == -1)
            return;
        System.out.println("[Terminal] SIGKILL to: " + killPid);
        Runtime.getRuntime().exec("kill -SIGKILL " + killPid);
    }

    public void termLastProcess() throws IOException {
        int termPid = getChildPid(lastCommand);
        if(termPid == -1)
            return;
        System.out.println("[Terminal] SIGTERM to: " + termPid);
        Runtime.getRuntime().exec("kill -SIGTERM " + termPid);
    }

    public int getPid() {
        int pid = -1;
        try {
            Field f = process.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            pid = f.getInt(process);
            f.setAccessible(false);
        } catch (Throwable e) {
            pid = -1;
        }
        return pid;
    }

    public void closeShell() {
        System.out.println("[Terminal] Closing down process.");
        try {
            outStream.close();
            stdin.close();
            stdout.close();
            stderr.close();
        } catch(IOException e) {

        }
        process.destroy();
    }
}
