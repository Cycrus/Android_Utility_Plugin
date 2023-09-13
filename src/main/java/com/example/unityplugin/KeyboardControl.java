package com.example.unityplugin;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardControl {

    private static Activity unityActivity;
    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void disableSoftKeyboard() {
        View unityView = unityActivity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) unityActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(unityView.getWindowToken(), 0);
    }
}