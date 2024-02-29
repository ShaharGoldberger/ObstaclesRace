package com.example.obstaclesrace;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GameSound {

    private SoundPool soundPool;
    private int crashSound;
    private SoundPool soundPool_coin;
    private int crashSound_coin;


    public GameSound(Context context) {
        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .build();

        crashSound = soundPool.load(context,R.raw.explosion,1);


        this.soundPool_coin = new SoundPool.Builder()
                .setMaxStreams(5)
                .build();

        crashSound_coin = soundPool_coin.load(context,R.raw.zapsplat_foley_money_british_coin_20p_set_down_on_other_coins_in_hand_change_002_90493,1);
    }

    public void crash() {
            soundPool.play(crashSound, 1.0f,1.0f,1,0,1);
    }

    public void crash_coin() {
        soundPool_coin.play(crashSound_coin, 1.0f,1.0f,1,0,1);
    }






}
