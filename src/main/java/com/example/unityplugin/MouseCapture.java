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
    boolean leftButtonHold = false;
    boolean rightButtonHold = false;
    boolean middleButtonHold = false;
    float verticalWheelOffset = 0f;
    float horizontalWheelOffset = 0f;

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
                        case MotionEvent.BUTTON_PRIMARY:
                            leftButtonDown = true;
                            leftButtonHold = true;
                            break;
                        case MotionEvent.BUTTON_SECONDARY:
                            rightButtonDown = true;
                            rightButtonHold = true;
                            break;
                        case MotionEvent.BUTTON_TERTIARY:
                            middleButtonDown = true;
                            middleButtonHold = true;
                            break;
                    }
                }
                else if(buttonAction == MotionEvent.ACTION_BUTTON_RELEASE) {
                    switch(buttonType)
                    {
                        case MotionEvent.BUTTON_PRIMARY:
                            leftButtonHold = false;
                            break;
                        case MotionEvent.BUTTON_SECONDARY:
                            rightButtonHold = false;
                            break;
                        case MotionEvent.BUTTON_TERTIARY:
                            middleButtonHold = false;
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
        boolean retButton = leftButtonDown;
        leftButtonDown = false;
        return retButton;
    }

    public boolean isRightButtonDown() {
        boolean retButton = rightButtonDown;
        rightButtonDown = false;
        return retButton;
    }

    public boolean isMiddleButtonDown() {
        boolean retButton = middleButtonDown;
        middleButtonDown = false;
        return retButton;
    }

    public boolean isLeftButtonHold() {
        return leftButtonHold;
    }

    public boolean isRightButtonHold() {
        return rightButtonHold;
    }

    public boolean isMiddleButtonHold() {
        return middleButtonHold;
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
