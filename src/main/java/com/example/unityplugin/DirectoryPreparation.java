package com.example.unityplugin;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DirectoryPreparation {
    private Activity activity;
    private String baseDirectoryName;
    private String rootDirectoryName;

    public DirectoryPreparation(Activity activity) {
        this.activity = activity;
        baseDirectoryName = activity.getApplicationInfo().dataDir;
        rootDirectoryName = baseDirectoryName + File.separator + "root";
    }

    public void createFullDirectoryStructure() {
        System.out.println("[Directory Structure] Creating system structure.");
        createFolder(baseDirectoryName, "root");
        createFolder(rootDirectoryName, "bin");
        createFolder(rootDirectoryName, "etc");
        createFolder(rootDirectoryName, "home");
        createFolder(rootDirectoryName, "opt");
        createFolder(rootDirectoryName, "usr");
        createFolder(rootDirectoryName + File.separator + "usr", "bin");

        prepareBinary(R.raw.hello, "hello");
    }

    private void createFolder(String location, String name) {
        File newDirectory = new File(location + File.separator + name);
        if(!newDirectory.exists() && !newDirectory.isDirectory()) {
            newDirectory.mkdirs();
        }
    }

    private void prepareBinary(int resourceId, String name) {
        InputStream in = null;
        OutputStream out = null;
        File outFile = null;
        String filename = rootDirectoryName + File.separator + "bin" + File.separator + name;

        System.out.println("[Directory Structure] Preparing binary <" + name + ">.");
        System.out.println("[Directory Structure] Writing file to <" + filename + ">.");

        try {
            in = activity.getResources().openRawResource(resourceId);
            outFile = new File(filename);
            out = new FileOutputStream(filename);
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
        } catch(IOException e) {
            System.out.println("[Directory Structure] Failed to create binary");
        } finally {
            try {
                in.close();
                out.flush();
                out.close();
                in = null;
                out = null;
            } catch (Exception e){}
        }

        outFile.setExecutable(true);
    }
}
