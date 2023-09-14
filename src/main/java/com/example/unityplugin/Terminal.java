package com.example.unityplugin;

import android.app.Activity;
import android.os.Environment;

import com.example.unityplugin.TerminalResources.EchoReturnControl;
import com.example.unityplugin.TerminalResources.ProcessContainer;
import com.example.unityplugin.TerminalResources.ProcessOutputHandler;
import com.example.unityplugin.TerminalResources.ProcessWatcher;
import com.example.unityplugin.TerminalResources.PsOutput;
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
    private String homeDirectory;

    private List<String> helpString;

    private String currDirectory = "";

    public Terminal(Activity activity) {
        pb = new ProcessBuilder();
        processList = new ArrayList<>();
        commandHistory = new ArrayList<>();
        outputBuffer = new ArrayList<>();
        errorBuffer = new ArrayList<>();
        androidActivity = activity;
        homeDirectory = androidActivity.getApplicationInfo().dataDir + File.separator + "root" + File.separator + "home";
        pb.directory(new File(homeDirectory));
        System.out.println("[Terminal] Working directory set to <" + homeDirectory + ">");
        helpString = Arrays.asList(
                "Here are some of the possible commands.\n",
                "help, exit, echo, clear, ping, curl, pwd, cd, ls.\n"
        );
    }

    public ProcessContainer startProcess(String[] startCommand, boolean attachToShell) {
        try {
            System.out.println("[Terminal] Starting process: " + Arrays.toString(startCommand));
            if (startCommand.length < 1)
                return null;

            pb.command(startCommand);
            Process newProcess = pb.start();
            currDirectory = pb.directory().toString();
            ProcessContainer newContainer = new ProcessContainer(newProcess, startCommand[0], getPid(newProcess));

            ProcessOutputHandler outputHandler;
            ProcessOutputHandler errorHandler;
            if(attachToShell) {
                outputHandler = new ProcessOutputHandler(newContainer, outputBuffer, false, ReadMethod.BY_CHARACTER);
                errorHandler = new ProcessOutputHandler(newContainer, errorBuffer, true, ReadMethod.BY_CHARACTER);
            }
            else {
                outputHandler = new ProcessOutputHandler(newContainer, null, false, ReadMethod.BY_CHARACTER);
                errorHandler = new ProcessOutputHandler(newContainer, null, true, ReadMethod.BY_CHARACTER);
            }
            outputHandler.start();
            errorHandler.start();

            newContainer.outputHandler = outputHandler;
            newContainer.errorHandler = errorHandler;

            if(!attachToShell)
                return newContainer;

            synchronized (processList) {
                processList.add(newContainer);
            }
            ProcessWatcher processWatcher = new ProcessWatcher(newContainer, processList);
            processWatcher.start();

            if(startCommand[0].equals("sh")) {
                writeInput("export HOME=" + homeDirectory, false);
            }

            return newContainer;
        }
        catch(IOException e) {
            System.out.println("[Terminal] ERROR: " + e);
            return null;
        }
    }

    public void writeWelcome() {
        sendToOutput("App specific storage path: " + androidActivity.getApplicationInfo().dataDir + "\n", false);
        sendToOutput("External storage path: " + Environment.getExternalStorageDirectory() + "\n", false);
        sendToOutput("Home directory: " + homeDirectory + "\n", false);
        sendToOutput("Have fun. ;)\n", false);
    }

    public void writeInput(String data, boolean doStoreInputInHistory) {
        stdIn(getActiveProcess(), data, doStoreInputInHistory);
        if(data.split(" ", 2)[0].equals("cd")) {
            setCurrDirectory();
        }
    }

    public String readOutput() {
        String output;
        synchronized(outputBuffer) {
            if (outputBuffer.size() == 0)
                return null;
            output = outputBuffer.get(0);
            outputBuffer.remove(0);
        }

        String[] echoReturnToken = output.split("=", 2);
        echoReturnToken[0] += '=';
        if(echoReturnToken[0].equals(EchoReturnControl.getEchoReturnToken())) {
            String[] keyValuePair = echoReturnToken[1].split("=", 2);
            if(keyValuePair[0].equals("pwd")) {
                currDirectory = keyValuePair[1];
                return null;
            }
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

    public void interruptShellProcess() throws IOException, InterruptedException {
        System.out.println("[Terminal] Interrupting current process running in shell.");
        if(numberProcessAttachedToShell() == 0) {
            System.out.println("[Terminal] Cannot close shell with ctr-c");
            return;
        }

        char sigintChar = (char)3;
        String sigintString = Character.toString(sigintChar);
        boolean interruptedPing = interruptPingProcess();
        if(!interruptedPing)
            writeInput(sigintString, false);
    }

    public boolean interruptPingProcess() throws IOException, InterruptedException {
        ProcessContainer psProcess = startProcess(new String[] {"ps"}, false);

        boolean foundPingProcess = false;

        String line;
        int basePid = getPid(getBaseProcess().process);

        Thread.sleep(200);

        for(int i = 0; i < 10000; i++) {
            line = psProcess.outputHandler.getOutputLine();
            if(line == null)
                continue;

            PsOutput psOutput = new PsOutput(line);
            if(psOutput.NAME == null)
                continue;
            psOutput.NAME = psOutput.NAME.replaceAll((char)10 + "", "");

            if(psOutput.PPID == basePid && psOutput.NAME.equals("ping")) {
                System.out.println("[Terminal] Now killing PID " + psOutput.PID);
                sendSignalByPid(psOutput.PID, "SIGINT");
                foundPingProcess = true;
                break;
            }
        }

        psProcess.process.destroy();
        psProcess.outputReader.close();
        psProcess.errorReader.close();

        return foundPingProcess;
    }

    public int numberProcessAttachedToShell() throws IOException, InterruptedException {
        ProcessContainer psProcess = startProcess(new String[] {"ps"}, false);

        String line;
        int basePid = getPid(getBaseProcess().process);
        int procCount = 0;

        Thread.sleep(100);

        for(int i = 0; i < 10000; i++) {
            line = psProcess.outputHandler.getOutputLine();
            if(line == null)
                continue;
            PsOutput psOutput = new PsOutput(line);
            if(psOutput.PPID == basePid)
                procCount++;
        }

        psProcess.process.destroy();
        psProcess.outputReader.close();
        psProcess.errorReader.close();

        return procCount;
    }

    public void interruptLastProcess() throws IOException {
        sendSignalByProcess(getActiveProcess(), "SIGINT");
    }

    public void killLastProcess() throws IOException {
        sendSignalByProcess(getActiveProcess(), "SIGKILL");
    }

    public void termLastProcess() throws IOException {
        sendSignalByProcess(getActiveProcess(), "SIGTERM");
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

    public int getHistroySize() {
        return commandHistory.size();
    }

    public String getHistroyCommand(int id) {
        if(id > commandHistory.size() - 1)
            return null;
        if(id < 0)
            return null;

        return commandHistory.get(id);
    }

    public String getCurrDirectory() {
        return currDirectory;
    }

    private void setCurrDirectory() {
        writeInput("echo " + EchoReturnControl.getEchoReturnToken() + "pwd=" + "$PWD", false);
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

        if(commandHistory.size() > 0 && commandHistory.get(commandHistory.size() - 1).equals(command))
            return;

        commandHistory.add(command);
        if(commandHistory.size() > 50) {
            commandHistory.remove(0);
        }
    }

    public void closeShell() throws IOException, InterruptedException {
        if(getBaseProcess().name.equals("sh")) {
            interruptShellProcess();
        }
        for(ProcessContainer container: processList) {
            container.process.destroy();
        }
    }

    private void stdIn(ProcessContainer process, String data, boolean doStoreCommand) {
        // First look if the command is custom.
        if(processCustomCommand(data)) {
            System.out.println("[Terminal] Processing custom command: " + data);
            if(doStoreCommand)
                storeCommand(data);
            return;
        }

        String baseCommand = data.split(" ")[0];

        // Spawn Process if possible.
        /*if(isProcessShell(process) && !Objects.equals(baseCommand, "cd")) {
            String[] splitCommand = data.split(" ");
            if(startProcess(splitCommand) != null) {
                System.out.println("[Terminal] Processing new process command: " + data);
                storeCommand(data);
                return;
            }
        }*/

        // If not possible write in standard input.
        try {
            System.out.println("[Terminal] Sending <" + data + "> to stdin of " + process);
            if(doStoreCommand)
                storeCommand(data);
            data += "\n";
            OutputStream stdin = process.process.getOutputStream();
            stdin.write(data.getBytes());
            stdin.flush();
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

    private ProcessContainer getProcessByPid(int pid) {
        for(ProcessContainer container: processList) {
            if(container.pid == pid)
                return container;
        }
        return null;
    }

    private void sendSignalByProcess(ProcessContainer process, String signal) {
        if(isBaseProcess(process)) {
            System.out.println("[Terminal] Base process " + process + " not killable with signal.");
            return;
        }

        int sigPid = process.pid;
        if(sigPid == -1)
            return;

        sendSignalByPid(sigPid, signal);
    }

    private void sendSignalByPid(int pid, String signal) {
        startProcess(new String[] {"kill", "-" + signal, Integer.toString(pid)}, true);
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
