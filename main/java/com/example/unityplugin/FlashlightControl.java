package com.example.unityplugin;


import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;

public class FlashlightControl {
    Boolean flashlightOn = false;

    private static Activity unityActivity;
    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public boolean toggleFlashlight() throws CameraAccessException {
        CameraManager camManager = (CameraManager)unityActivity.getSystemService(Context.CAMERA_SERVICE);
        String cameraId = null;
        cameraId = camManager.getCameraIdList()[0];

        if(flashlightOn)
            flashlightOn = false;
        else
            flashlightOn = true;

        camManager.setTorchMode(cameraId, flashlightOn);

        return flashlightOn;
    }
}
