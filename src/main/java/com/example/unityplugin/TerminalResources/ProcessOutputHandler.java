package com.example.unityplugin.TerminalResources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class ProcessOutputHandler extends Thread {
    private final ProcessContainer process;
    private final List<String> outputBuffer;
    private final BufferedReader outputReader;
    private final ReadMethod readMethod;
    private final long startTime;

    public ProcessOutputHandler(ProcessContainer process, List<String> outputBuffer, boolean errorOutput, ReadMethod readMethod) {
        this.process = process;
        this.outputBuffer = outputBuffer;

        if(errorOutput)
            outputReader = new BufferedReader(new InputStreamReader(process.process.getErrorStream()));
        else
            outputReader = new BufferedReader(new InputStreamReader(process.process.getInputStream()));

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
                if (!outputReader.ready())
                    continue;
                do {
                    character = (char)outputReader.read();
                    output += character;
                } while(character != '\n');
            } catch (IOException e) {
                System.out.println("[Terminal] ProcessOutputHandler " + process + " error: " + e);
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
