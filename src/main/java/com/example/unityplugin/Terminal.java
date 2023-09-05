package com.example.unityplugin;

import com.example.unityplugin.TerminalResources.ProcessContainer;
import com.example.unityplugin.TerminalResources.ProcessOutputHandler;
import com.example.unityplugin.TerminalResources.ProcessWatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Terminal {
    private final ProcessBuilder pb;
    private final List<ProcessContainer> processList;
    private final List<String> commandHistory;
    private final List<String> outputBuffer;
    private final List<String> errorBuffer;

    public Terminal() {
        pb = new ProcessBuilder();
        processList = new ArrayList<>();
        commandHistory = new ArrayList<>();
        outputBuffer = new ArrayList<>();
        errorBuffer = new ArrayList<>();
    }

    public boolean startProcess(String[] startCommand) {
        try {
            System.out.println("[Terminal] Starting process: " + Arrays.toString(startCommand));
            if (startCommand.length < 1)
                return false;

            pb.command(startCommand);
            Process newProcess = pb.start();
            ProcessContainer newContainer = new ProcessContainer(newProcess, startCommand[0], getPid(newProcess));
            ProcessOutputHandler outputHandler = new ProcessOutputHandler(newProcess, outputBuffer);
            ProcessOutputHandler errorHandler = new ProcessOutputHandler(newProcess, errorBuffer);
            outputHandler.start();
            errorHandler.start();
            synchronized (processList) {
                processList.add(newContainer);
            }
            ProcessWatcher processWatcher = new ProcessWatcher(newContainer, processList);
            processWatcher.start();
            return true;
        }
        catch(IOException e) {
            System.out.println("[Terminal] ERROR: " + e);
            return false;
        }
    }

    public void writeInput(String data) {
        stdIn(getActiveProcess(), data);
    }

    public String readOutput() {
        String output;
        synchronized(outputBuffer) {
            if (outputBuffer.size() == 0)
                return null;
            output = outputBuffer.get(0);
            outputBuffer.remove(0);
        }
        return output;
    }

    public String readError() {
        String err;
        synchronized (errorBuffer) {
            if (errorBuffer.size() == 0)
                return null;
            err = errorBuffer.get(0);
            errorBuffer.remove(0);
        }
        return err;
    }

    public void interruptLastProcess() throws IOException {
        sendSignal(getActiveProcess(), "SIGINT");
    }

    public void killLastProcess() throws IOException {
        sendSignal(getActiveProcess(), "SIGKILL");
    }

    public void termLastProcess() throws IOException {
        sendSignal(getActiveProcess(), "SIGTERM");
    }

    private String getCommandHistory() {
        StringBuilder historyBuilder = new StringBuilder();
        for(String command: commandHistory) {
            historyBuilder.append(command).append("\n");
        }
        return historyBuilder.toString();
    }

    private ProcessContainer getActiveProcess() {
        synchronized (processList) {
            return processList.get(processList.size() - 1);
        }
    }

    private ProcessContainer getBaseProcess() {
        synchronized(processList) {
            return processList.get(0);
        }
    }

    private void storeCommand(String command) {
        commandHistory.add(command);
        if(commandHistory.size() > 50) {
            commandHistory.remove(0);
        }
    }

    private void stdIn(ProcessContainer process, String data) {
        String baseCommand = data.split(" ")[0];

        if(isProcessShell(process) && !isBashCommand(baseCommand)) {
            System.out.println("[Terminal] in 1");
            data = "sh -c " + data;
            String[] splitCommand = data.split(" ");
            startProcess(splitCommand);
            System.out.println("[Terminal] in 2");
            storeCommand(data);
            System.out.println("[Terminal] in 3");
            return;
        }

        try {
            System.out.println("[Terminal] Sending <" + data + "> to stdin.");
            data += "\n";
            OutputStream stdin = process.process.getOutputStream();
            System.out.println("[Terminal] in 4");
            stdin.write(data.getBytes());
            System.out.println("[Terminal] in 5");
            stdin.flush();
            System.out.println("[Terminal] in 6");

        } catch (IOException e) {
            System.out.println("[Terminal] In ERROR: " + e);
        }
    }

    private String stdOut(Process process, boolean stdErr) {
        try {
            BufferedReader reader;
            String line;

            if(stdErr)
                reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            else
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            if(!reader.ready()) {
                return null;
            }

            line = reader.readLine();

            return line;
        }
        catch (IOException e)
        {
            return null;
        }
    }

    private void sendSignal(ProcessContainer process, String signal) {
        if(isProcessShell(process))
            return;

        int sigPid = process.pid;
        if(sigPid == -1)
            return;
        System.out.println("[Terminal] " + signal + " to: " + sigPid);
        try {
            Runtime.getRuntime().exec("kill -" + signal + " " + sigPid);
        } catch(IOException e)
        {
            System.out.println("[Terminal] ERROR in sendSignal: " + e);
        }
    }

    private boolean isProcessShell(ProcessContainer process) {
        return Objects.equals(process.name, "sh");
    }

    private int getPid(Process process) {
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

    private boolean isBashCommand(String command) {
        return Objects.equals(command, "echo");
    }
}
