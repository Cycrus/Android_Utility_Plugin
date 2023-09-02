package com.example.unityplugin;

import android.app.Activity;
import android.widget.Toast;

public class SampleMethods {
    private static Activity unityActivity;

    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public int Add(int i, int j) {
        return i + j;
    }

    public void Toast(String msg) {
        Toast.makeText(unityActivity, msg, Toast.LENGTH_SHORT).show();
    }
}
