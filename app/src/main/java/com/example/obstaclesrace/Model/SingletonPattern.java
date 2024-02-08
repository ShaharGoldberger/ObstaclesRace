package com.example.obstaclesrace.Model;

import static androidx.core.content.ContextCompat.getSystemService;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.widget.Toast;

public class SingletonPattern {

    private static volatile SingletonPattern instance;
    public final Vibrator vibrator;
    private Context context; // Store Context reference


    private SingletonPattern(Context context) {
        this.context = context.getApplicationContext(); // Use application context to avoid memory leaks
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static synchronized SingletonPattern getInstance(Context context) {
        if (instance == null && context != null) {
            instance = new SingletonPattern(context.getApplicationContext());
        }
        return instance;
    }

    public void toastAndVibrate(String txt){
        vibrate(500);
        toast(txt);
    }

    private void toast(String txt) {
        Toast.makeText(this.context, txt, Toast.LENGTH_SHORT).show();
    }

    public void vibrate(int vibrationDuration) {
        if (vibrator == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(vibrationDuration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(vibrationDuration);
        }
    }
}
