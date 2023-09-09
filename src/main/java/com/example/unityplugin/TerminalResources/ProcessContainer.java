package com.example.unityplugin.TerminalResources;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessContainer {
    public Process process;
    public String name;
    public int pid;
    public final BufferedReader outputReader;
    public final BufferedReader errorReader;

    public ProcessContainer(Process process, String name, int pid) {
        this.process = process;
        this.name = name;
        this.pid = pid;
        this.errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        this.outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    @NonNull
    @Override
    public String toString() {
        return "<" + name + " | " + pid + ">";
    }
}
