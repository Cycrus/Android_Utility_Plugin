package com.example.unityplugin;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class BluetoothControl {
    private static Activity unityActivity;
    private BluetoothAdapter bluetoothAdapter;
    private boolean permissionGiven = false;
    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void initializeModule()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void turnOnBluetooth()
    {
        checkPermission();
        bluetoothAdapter.enable();
    }

    public void turnOffBluetooth()
    {
        checkPermission();
        bluetoothAdapter.disable();
    }

    public boolean bluetoothEnabled()
    {
        return bluetoothAdapter.isEnabled();
    }

    private void checkPermission()
    {
        if (!permissionGiven && ActivityCompat.checkSelfPermission(unityActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(unityActivity, new String[] { Manifest.permission.BLUETOOTH_CONNECT }, 1);
            permissionGiven = true;
        }
    }
}
