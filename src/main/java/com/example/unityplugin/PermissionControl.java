package com.example.unityplugin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

public class PermissionControl {
    private static Activity unityActivity;
    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void requestApplicationPermissions()
    {
        ActivityCompat.requestPermissions(unityActivity, new String[]{Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.READ_BASIC_PHONE_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.UPDATE_DEVICE_STATS,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_MEDIA,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO}, 1);
    }

    public void requestSystemPermissions()
    {
        if (!Settings.System.canWrite(unityActivity)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            unityActivity.startActivity(intent);
        }
    }
}
