package com.kabunny.app;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundEffectsManager {

    private static final String TAG = "SoundEffectsManager";
    private SoundPool sp;
    private Context context;

    public SoundEffectsManager(Context context) {
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        this.context = context;
    }

    public int load(int resId) {
        return sp.load(context, resId, 1);
    }

    public int play(int sound_id, int priority) {
        // play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        return sp.play(sound_id, 100, 100, priority, 0, 1f);
    }

}
