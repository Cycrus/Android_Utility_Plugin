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

public class Terminal {
    private Application application;
    ProcessBuilder processBuilder;
    private Process process;
    private OutputStream stdin;
    private InputStream stdout;
    private InputStream stderr;
    private BufferedReader outStream;
    private BufferedReader errStream;

    public void runTestCommand() throws IOException {
        System.out.println("[Terminal] Running test command.");
        Process process = Runtime.getRuntime().exec("sh");
        OutputStream stdin = process.getOutputStream();
        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        String command = "echo Hello World, I am in VR and am invoked from stdin.";
        stdin.write(command.getBytes());
        stdin.flush();
        stdin.close();

        String line;
        BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(stdout));
        while((line = brCleanUp.readLine()) != null) {
            System.out.println("[Terminal] Stdout: " + line);
        }
        brCleanUp.close();

        brCleanUp = new BufferedReader(new InputStreamReader(stderr));
        while((line = brCleanUp.readLine()) != null) {
            System.out.println("[Terminal] Stderr: " + line);
        }
        brCleanUp.close();
        process.destroy();
    }

    public boolean startProcess(String[] processName) {
        try {
            System.out.println("[Terminal] Starting process: " + Arrays.toString(processName));
            if (processName.length == 1)
                process = Runtime.getRuntime().exec(processName[0]);
            else if (processName.length > 1)
                process = Runtime.getRuntime().exec(processName);
            else
                return false;

            processBuilder = new ProcessBuilder(processName);
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
        }
        catch(IOException e)
        {
            System.out.println("[Terminal] In ERROR: " + e);
        }
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

    public void printChildProcesses() {
        System.out.println("[Terminal] Children processes:");
    }

    public void interruptCurrProcess() throws IOException {
        int pid = getPid();
        System.out.println("[Terminal] SIGINT to: " + pid);
        Runtime.getRuntime().exec("kill -SIGINT " + pid);
    }

    public void killCurrProcess() throws IOException {
        int pid = getPid();
        System.out.println("[Terminal] SIGKILL to: " + pid);
        Runtime.getRuntime().exec("kill -SIGKILL " + pid);
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

    public void closeShell() throws IOException {
        System.out.println("[Terminal] Closing down process.");
        outStream.close();
        stdin.close();
        stdout.close();
        stderr.close();
        process.destroy();
    }
}

/*
import java.io.*;

public class CustomBash {
    private Process process;
    private OutputStream stdin;
    private InputStream stdout;

    public boolean startProcess(String[] processName) {
        try {
            // Start the process
            ProcessBuilder builder = new ProcessBuilder(processName);
            process = builder.start();
            stdin = process.getOutputStream();
            stdout = process.getInputStream();
            return true;
        } catch (IOException e) {
            System.out.println("[Terminal] ERROR: " + e);
            return false;
        }
    }

    public void stdIn(String data) {
        try {
            System.out.println("[Terminal] Sending <" + data + "> to stdin.");
            // Add a newline to the input to simulate pressing Enter
            data += "\n";
            stdin.write(data.getBytes());
            stdin.flush();
        } catch (IOException e) {
            System.out.println("[Terminal] In ERROR: " + e);
        }
    }

    public void interruptCurrProcess() {
        try {
            // Send a SIGINT signal to the child process
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Other methods as before...
}
 */