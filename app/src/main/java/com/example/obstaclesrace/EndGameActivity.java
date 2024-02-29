package com.example.obstaclesrace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;

import com.example.obstaclesrace.Model.BackgroundSound;
import com.google.android.material.textview.MaterialTextView;

public class EndGameActivity extends AppCompatActivity {

    private MaterialTextView textView_EndGame;
    private MaterialTextView textView_Score;
    private Button buttonReturnMenu;
    public static final String KEY_STATUS = "KEY_STATUS";
    public static final String KEY_SCORE = "KEY_SCORE";
    private BackgroundSound backgroundSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        findView();
        setupListeners();

        int coinsCollected = getIntent().getIntExtra(KEY_SCORE, 0);
        textView_Score.setText("Coins: " + coinsCollected);

    }

    private void findView() {
        textView_EndGame = findViewById(R.id.textView_EndGame);
        textView_Score = findViewById(R.id.textView_Score);
        buttonReturnMenu = findViewById(R.id.buttonReturnMenu);
    }

    private void setupListeners() {
        buttonReturnMenu.setOnClickListener(v -> {
            // Intent to start MenuActivity
            Intent intent = new Intent(EndGameActivity.this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }


    private void refreshUI(String status, int score) {
        textView_Score.setText(status + "\n" + score);
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundSound.stopSound();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundSound = new BackgroundSound(this, R.raw.lifelike);
        backgroundSound.playSound();
    }

}