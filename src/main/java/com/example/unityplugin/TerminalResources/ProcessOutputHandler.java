package com.example.unityplugin.TerminalResources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessOutputHandler extends Thread {
    private final Process process;
    private final List<String> outputBuffer;
    private final BufferedReader outputReader;
    private String output;

    public ProcessOutputHandler(Process process, List<String> outputBuffer) {
        this.process = process;
        this.outputBuffer = outputBuffer;

        outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    @Override
    public void run() {
        // TODO: This is currently running forever. You must close this when the process dies.
        readLineByCharacter();
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
                    System.out.println("[Terminal] Output: " + line);
                    outputBuffer.add(line);
                }
            }
        }
    }

    public void readLineByCharacter() {
        System.out.println("[Terminal] Started ProcessOutputHandler thread.");
        while (true) {
            char character;
            output = "";
            try {
                if (!outputReader.ready())
                    continue;
                do {
                    character = (char)outputReader.read();
                    output += character;
                } while(character != '\n' && character != '\r');
            } catch (IOException e) {
                System.out.println("[Terminal] Output Thread error: " + e);
                return;
            }
            if (!output.equals("")) {
                synchronized (outputBuffer) {
                    System.out.println("[Terminal] Output: " + output);
                    outputBuffer.add(output);
                }
            }
        }
    }
}
