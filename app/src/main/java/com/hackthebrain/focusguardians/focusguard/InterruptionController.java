package com.hackthebrain.focusguardians.focusguard;

import android.media.AudioManager;

/**
 * Created by thomas on 6-6-15.
 */
public class InterruptionController
{
    protected int preFocusMode;
    protected AudioManager audioManager;

    public void setInterruptionMode(boolean isFocused)
    {
        if (isFocused) {
            preFocusMode = audioManager.getRingerMode();
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else {
            audioManager.setRingerMode(preFocusMode);
        }
    }

    public void setAudioManager(AudioManager audioManager)
    {
        this.audioManager = audioManager;
    }
}
