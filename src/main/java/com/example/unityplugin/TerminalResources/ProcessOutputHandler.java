package com.example.unityplugin.TerminalResources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class ProcessOutputHandler extends Thread {
    private final ProcessContainer process;
    private final List<String> outputBuffer;
    private final ReadMethod readMethod;
    private final long startTime;
    private final BufferedReader outputReader;

    public ProcessOutputHandler(ProcessContainer process, List<String> outputBuffer, boolean errorOutput, ReadMethod readMethod) {
        this.process = process;
        this.outputBuffer = outputBuffer;

        if(errorOutput)
            outputReader = process.errorReader;
        else
            outputReader = process.outputReader;

        this.readMethod = readMethod;

        startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        switch(readMethod) {
            case BY_CHARACTER:
                readLineByCharacter();
                break;
            case BY_LINE:
                readLineByLine();
                break;
            default:
                System.out.println("[ERROR] Invalid read method for the process " + process + ".");
                break;
        }
    }

    public void readLineByLine() {
        while (true) {
            String line = null;

            try {
                if (!outputReader.ready())
                    continue;
                line = outputReader.readLine();
            } catch (IOException e) {
                System.out.println("[Terminal] Output Thread error: " + e);
                return;
            }

            if (line != null) {
                synchronized (outputBuffer) {
                    System.out.println("[Terminal] Output " + process + ": " + line);
                    outputBuffer.add(line);
                }
            }
        }
    }

    public void readLineByCharacter() {
        System.out.println("[Terminal] Started ProcessOutputHandler " + process + " thread.");
        String output;

        while (true) {
            long currTime = System.currentTimeMillis();
            if((currTime - startTime) >= 1000) {
                if(!process.process.isAlive()) {
                    System.out.println("[Terminal] Closing down ProcessOutputHandler " + process);
                    return;
                }
            }

            char character;
            output = "";
            try {
                do {
                    character = (char)outputReader.read();
                    output += character;
                } while(character != '\n' && character != '\0');
            } catch (IOException e) {
                System.out.println("[Terminal] ProcessOutputHandler " + process + " " + e);
                System.out.println("[Terminal] Closing down ProcessOutputHandler of" + process);
                return;
            }
            if (!output.equals("")) {
                synchronized (outputBuffer) {
                    outputBuffer.add(output);
                }
            }
        }
    }
}
