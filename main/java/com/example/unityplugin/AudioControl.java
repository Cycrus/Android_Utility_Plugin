package com.example.unityplugin;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;

public class AudioControl {
    private AudioManager audioManager;
    private static Activity unityActivity;

    public static void receiveUnityActivity(Activity tActivity) {
        unityActivity = tActivity;
    }

    public void initializeModule()
    {
        audioManager = (AudioManager)unityActivity.getSystemService(Context.AUDIO_SERVICE);
    }

    public int getVolume()
    {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public int getMaxVolume()
    {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public void increaseVolume()
    {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
    }

    public void decreaseVolume()
    {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
    }

    public void toggleMuteVolume()
    {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_TOGGLE_MUTE, 0);
    }

    public boolean isMuted()
    {
        return audioManager.isStreamMute(AudioManager.STREAM_MUSIC);
    }
}
