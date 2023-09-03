package com.example.unityplugin;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeControl {
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;

    String currentDate = "---";
    String currentTime = "---";

    private static Activity unityActivity;
    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void setDateFormat(String newDateFormat) {
        dateFormat = new SimpleDateFormat(newDateFormat);
    }

    public void setTimeFormat(String newTimeFormat) {
        timeFormat = new SimpleDateFormat(newTimeFormat);
    }

    public void fetchDateTime() {
        currentDate = dateFormat.format(new Date());
        currentTime = timeFormat.format(new Date());
    }

    public String getDateString() {
        return currentDate;
    }

    public String getTimeString() {
        return currentTime;
    }
}
