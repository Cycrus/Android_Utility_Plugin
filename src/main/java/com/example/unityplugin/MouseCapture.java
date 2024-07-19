/*
https://developer.android.com/reference/android/view/MotionEvent
 */

package com.example.unityplugin;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MouseCapture {
    float yOffset = 0f;
    float xOffset = 0f;
    boolean leftButtonDown = false;
    boolean rightButtonDown = false;
    boolean middleButtonDown = false;
    float verticalWheelOffset = 0f;
    float horizontalWheelOffset = 0f;

    final int LEFT_BUTTON = 1;
    final int RIGHT_BUTTON = 8;

    private static Activity unityActivity;
    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void requestMouseCapture() {
        View unityView = unityActivity.getCurrentFocus();
        if(unityView.hasPointerCapture()) return;

        unityView.requestPointerCapture();

        unityView.setOnCapturedPointerListener(new View.OnCapturedPointerListener() {
            @Override
            public boolean onCapturedPointer (View view, MotionEvent motionEvent) {
                int buttonAction = motionEvent.getActionMasked();
                int buttonType = motionEvent.getActionButton();

                if(buttonAction == MotionEvent.ACTION_BUTTON_PRESS) {
                    switch(buttonType)
                    {
                        case LEFT_BUTTON:
                            System.out.println("[DEBUG CLICK JAVA] Pressing left button.");
                            leftButtonDown = true;
                            break;
                        case RIGHT_BUTTON:
                            System.out.println("[DEBUG CLICK JAVA] Pressing right button.");
                            rightButtonDown = true;
                            break;
                    }
                }

                else if(buttonAction == MotionEvent.ACTION_BUTTON_RELEASE) {
                    switch(buttonType)
                    {
                        case LEFT_BUTTON:
                            System.out.println("[DEBUG CLICK JAVA] Releasing left button.");
                            leftButtonDown = false;
                            break;
                        case RIGHT_BUTTON:
                            System.out.println("[DEBUG CLICK JAVA] Releasing right button.");
                            rightButtonDown = false;
                            break;
                    }
                }

                xOffset = motionEvent.getX();
                yOffset = motionEvent.getY();
                verticalWheelOffset = motionEvent.getAxisValue(MotionEvent.AXIS_VSCROLL);
                horizontalWheelOffset = motionEvent.getAxisValue(MotionEvent.AXIS_HSCROLL);

                return true;
            }
        });
    }

    public float getXOffset()
    {
        float retOffset = xOffset;
        xOffset = 0f;
        return retOffset;
    }

    public float getYOffset()
    {
        float retOffset = yOffset;
        yOffset = 0f;
        return retOffset;
    }

    public void resetOffset()
    {
        xOffset = 0f;
        yOffset = 0f;
    }

    public boolean isLeftButtonDown() {
        return leftButtonDown;
    }

    public boolean isRightButtonDown() {
        return rightButtonDown;
    }

    public float getVerticalWheelOffset() {
        float retOffset = verticalWheelOffset;
        verticalWheelOffset = 0f;
        return retOffset;
    }

    public float getHorizontalWheelOffset() {
        float retOffset = horizontalWheelOffset;
        horizontalWheelOffset = 0f;
        return retOffset;
    }
}
