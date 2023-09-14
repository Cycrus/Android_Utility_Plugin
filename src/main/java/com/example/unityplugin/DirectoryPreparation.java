package com.example.unityplugin;

import android.app.Activity;

import java.io.File;

public class DirectoryPreparation {
    private String baseDirectoryName;
    private String rootDirectoryName;

    public DirectoryPreparation(Activity activity) {
        baseDirectoryName = activity.getApplicationInfo().dataDir;
        rootDirectoryName = baseDirectoryName + File.separator + "root";
    }

    public void createFullDirectoryStructure() {
        createFolder(baseDirectoryName, "root");
        createFolder(rootDirectoryName, "bin");
        createFolder(rootDirectoryName, "etc");
        createFolder(rootDirectoryName, "home");
        createFolder(rootDirectoryName, "opt");
    }

    private void createFolder(String location, String name) {
        File newDirectory = new File(location + File.separator + name);
        if(!newDirectory.exists() && !newDirectory.isDirectory()) {
            newDirectory.mkdirs();
        }
    }
}
