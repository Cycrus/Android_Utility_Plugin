package com.example.unityplugin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

public class BrightnessControl {
    private static Activity unityActivity;
    private boolean permissionGiven = false;
    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void initializeModule()
    {
        checkPermission();
    }

    public int getBrightness()
    {
        return Settings.System.getInt(unityActivity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 0);
    }

    public void changeBrightness(int changeValue)
    {
        checkPermission();
        int currBrightness = getBrightness();
        int newBrightness = currBrightness + changeValue;
        if(newBrightness < 1)
            newBrightness = 1;
        Settings.System.putInt(unityActivity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(unityActivity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, newBrightness);
    }

    private void checkPermission()
    {
        if (!permissionGiven && !Settings.System.canWrite(unityActivity)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            unityActivity.startActivity(intent);
            permissionGiven = true;
        }
    }
}
