package com.example.unityplugin.TerminalResources;

import java.util.ArrayList;
import java.util.List;

public class BashCommands {
    private static List<String> commands = new ArrayList<>(List.of(
            "echo",
            "cd",
            "ls",
            "mkdir",
            "rm"
    ));

    public static List<String> getCommands() {
        return commands;
    }
}
