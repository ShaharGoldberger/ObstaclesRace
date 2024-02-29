package com.example.obstaclesrace.Model;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BackgroundSound {

    private Context context;
    private Executor executor;
    private Handler handler;
    private MediaPlayer mediaPlayer;
    private int resID;

    public BackgroundSound(Context context, int resId) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
        this.resID = resId;
    }

    public void playSound(){
        executor.execute(() -> {
            mediaPlayer = MediaPlayer.create(context, this.resID);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(1.0f,1.0f);
            mediaPlayer.start();
        });
    }

    public void stopSound(){
        if (mediaPlayer != null){
            executor.execute(() -> {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            });
        }
    }
}
