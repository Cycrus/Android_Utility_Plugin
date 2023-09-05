package com.example.unityplugin.TerminalResources;

public class ProcessContainer {
    public Process process;
    public String name;
    public int pid;

    public ProcessContainer(Process process, String name, int pid) {
        this.process = process;
        this.name = name;
        this.pid = pid;
    }
}
