package com.example.unityplugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.LocationRequest;

import androidx.core.app.ActivityCompat;

public class GPSControl {
    private static Activity unityActivity;
    private LocationManager locationManager;
    private boolean permissionGiven = false;
    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void initializeModule()
    {
        locationManager = (LocationManager)unityActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    public void turnOnGPS()
    {
        checkPermission();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            LocationRequest locationRequest = new LocationRequest.Builder(10000).build();
        }
    }

    public void turnOffGPS()
    {
        checkPermission();
    }

    public boolean GPSEnabled()
    {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void checkPermission()
    {
        if (!permissionGiven && ActivityCompat.checkSelfPermission(unityActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(unityActivity, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION,
                                                                            Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            permissionGiven = true;
        }
    }
}
