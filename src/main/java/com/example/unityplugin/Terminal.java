package com.example.unityplugin;

import android.app.Activity;

import com.example.unityplugin.TerminalResources.ProcessContainer;
import com.example.unityplugin.TerminalResources.ProcessOutputHandler;
import com.example.unityplugin.TerminalResources.ProcessWatcher;
import com.example.unityplugin.TerminalResources.ReadMethod;

import java.io.BufferedReader;
import java.io.File;
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
    private final Activity androidActivity;

    private List<String> helpString;

    public Terminal(Activity activity) {
        pb = new ProcessBuilder();
        processList = new ArrayList<>();
        commandHistory = new ArrayList<>();
        outputBuffer = new ArrayList<>();
        errorBuffer = new ArrayList<>();
        androidActivity = activity;
        pb.directory(new File(androidActivity.getApplicationInfo().dataDir));
        System.out.println("[Terminal] Working directory set to <" + androidActivity.getApplicationInfo().dataDir + ">");
        helpString = Arrays.asList(
                "Here are some of the possible commands.\n",
                "help, exit, echo, clear, ping, curl, pwd, cd, ls.\n"
        );
    }

    public boolean startProcess(String[] startCommand) {
        try {
            System.out.println("[Terminal] Starting process: " + Arrays.toString(startCommand));
            if (startCommand.length < 1)
                return false;

            pb.command(startCommand);
            Process newProcess = pb.start();
            ProcessContainer newContainer = new ProcessContainer(newProcess, startCommand[0], getPid(newProcess));
            ProcessOutputHandler outputHandler = new ProcessOutputHandler(newContainer, outputBuffer, false, ReadMethod.BY_CHARACTER);
            ProcessOutputHandler errorHandler = new ProcessOutputHandler(newContainer, errorBuffer, true, ReadMethod.BY_CHARACTER);
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

    public void sendToOutput(String content, boolean error) {
        String[] splitString = content.split("\n");
        for(int i = 0; i < splitString.length; i++)
        {
            splitString[i] += '\n';
        }

        if(error)
            errorBuffer.addAll(Arrays.asList(splitString));
        else
            outputBuffer.addAll(Arrays.asList(splitString));
    }

    public boolean isCurrentProcBase() {
        return isBaseProcess(getActiveProcess());
    }

    public boolean isCurrentProcShell() {
        return isProcessShell(getActiveProcess());
    }

    private void writeCommandHistory() {
        outputBuffer.addAll(commandHistory);
    }

    private void writeHelp() {
        outputBuffer.addAll(helpString);
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

    private boolean isBaseProcess(ProcessContainer process) {
        synchronized(processList) {
            return process.pid == processList.get(0).pid;
        }
    }

    public void storeCommand(String command) {
        if(command.charAt(command.length() - 1) != '\n')
            command += "\n";
        commandHistory.add(command);
        if(commandHistory.size() > 50) {
            commandHistory.remove(0);
        }
    }

    private void stdIn(ProcessContainer process, String data) {
        // First look if the command is custom.
        if(processCustomCommand(data)) {
            storeCommand(data);
            return;
        }

        String baseCommand = data.split(" ")[0];

        // Spawn Process if possible.
        if(isProcessShell(process) && !Objects.equals(baseCommand, "cd")) {
            String[] splitCommand = data.split(" ");
            if(startProcess(splitCommand)) {
                storeCommand(data);
                return;
            }
        }

        // If not possible write in standard input.
        try {
            System.out.println("[Terminal] Sending <" + data + "> to stdin of " + process);
            if(data.charAt(data.length() - 1) != '\n')
                data += "\n";
            OutputStream stdin = process.process.getOutputStream();
            stdin.write(data.getBytes());
            stdin.flush();
            storeCommand(data);
        } catch (IOException e) {
            System.out.println("[Terminal] In ERROR: " + e);
        }
    }

    private boolean processCustomCommand(String command) {
        switch(command) {
            case "history":
                writeCommandHistory();
                break;
            case "help":
                writeHelp();
                break;
            default:
                return false;
        }
        return true;
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
        if(isBaseProcess(process)) {
            System.out.println("[Terminal] Base process " + process + " not killable with signal.");
            return;
        }

        int sigPid = process.pid;
        if(sigPid == -1)
            return;

        startProcess(new String[] {"kill", "-" + signal, Integer.toString(sigPid)});
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
}
