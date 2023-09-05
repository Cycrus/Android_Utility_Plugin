package com.example.unityplugin.TerminalResources;

import java.util.List;

public class ProcessWatcher extends Thread {
    private final ProcessContainer process;
    private final List<ProcessContainer> processList;
    public ProcessWatcher(ProcessContainer process, List<ProcessContainer> processList) {
        this.process = process;
        this.processList = processList;
    }

    @Override
    public void run() {
        try {
            process.process.waitFor();
        } catch (InterruptedException e) {
            if (process.process.isAlive())
                process.process.destroy();
        }

        synchronized(processList) {
            System.out.println("[Terminal] Removing process " + process + " from list.");
            processList.remove(process);
        }
    }
}
