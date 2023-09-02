package com.example.unityplugin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryControl {
    int batteryLevel = 0;
    int batteryScale = 0;
    float batteryPercentage = 0.0F;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryPercentage = ((float)batteryLevel / (float)batteryScale) * 100.0f;
        }
    };

    private static Activity unityActivity;
    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void registerBatteryListener() {
        unityActivity.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public int getBatteryLevel() { return batteryLevel; }
    public int getBatteryScale() { return batteryScale; }
    public float getBatteryPercentage() { return batteryPercentage; }
}
