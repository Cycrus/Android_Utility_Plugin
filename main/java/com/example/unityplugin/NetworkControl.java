package com.example.unityplugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class NetworkControl {
    private boolean permissionGiven = false;
    private static Activity unityActivity;
    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    private TelephonyManager telephonyManager;

    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void initializeModule() {
        connectivityManager = (ConnectivityManager) unityActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) unityActivity.getSystemService(Context.WIFI_SERVICE);
        telephonyManager = (TelephonyManager) unityActivity.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public int getSignalStrength() {
        checkPermission();

        return telephonyManager.getSignalStrength().getLevel();
    }

    public String getNetworkType() {
        Network currentNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(currentNetwork);
        if (nc == null) return "OFF";
        if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return "WIFI";
        if (nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return "Ether";
        if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            checkPermission();

            switch (telephonyManager.getDataNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                case TelephonyManager.NETWORK_TYPE_GSM:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                case TelephonyManager.NETWORK_TYPE_IWLAN:
                    return "4G";
                case TelephonyManager.NETWORK_TYPE_NR:
                    return "5G";
            }
        }

        return "???";
    }

    private void checkPermission() {
        if (!permissionGiven && ActivityCompat.checkSelfPermission(unityActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(unityActivity, new String[]{Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE}, 1);
            permissionGiven = true;
        }
    }

    public boolean enableWifi() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            wifiManager.setWifiEnabled(true);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean disableWifi() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            wifiManager.setWifiEnabled(false);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }

    public boolean enableMobileData() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            Method setMobileDataEnabledMethod = Objects.requireNonNull(telephonyManager).getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            setMobileDataEnabledMethod.invoke(telephonyManager, true);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean disableMobileData() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            Method setMobileDataEnabledMethod = Objects.requireNonNull(telephonyManager).getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            setMobileDataEnabledMethod.invoke(telephonyManager, false);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isMobileDataEnabled() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getMobileDataEnabledMethod = Objects.requireNonNull(telephonyManager).getClass().getDeclaredMethod("getDataEnabled");
        return (boolean) (Boolean) getMobileDataEnabledMethod.invoke(telephonyManager);
    }

    public boolean isAirplaneModeEnabled() throws Settings.SettingNotFoundException {
        return Settings.System.getInt(unityActivity.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON) == 1;
    }

    public boolean enableAirplaneMode()
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            Settings.System.putInt(unityActivity.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 1);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean disableAirplaneMode()
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            Settings.System.putInt(unityActivity.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
            return true;
        }
            else
        {
            return false;
        }
    }
}