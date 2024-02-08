package com.example.obstaclesrace;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.example.obstaclesrace.Logic.GameManager;
import com.example.obstaclesrace.Model.SingletonPattern;

public class MainActivity extends AppCompatActivity implements GameManager.RunOnMain {
    private ImageButton main_BTL_left;
    private ImageButton main_BTL_right;
    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridLayout layout = findViewById(R.id.gameLayout);
        findView();
        gameManager = new GameManager(layout,
                new ImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        },this);

        SingletonPattern.getInstance(this); // Initialize the singleton here
        setupButtonListeners(); // Setup button listeners
        refreshUI();
    }

    private void setupButtonListeners() {
        main_BTL_left.setOnClickListener(v -> {
            gameManager.moveCar(-1);
        });
        main_BTL_right.setOnClickListener(v -> {
            gameManager.moveCar(1);
        });
    }


    //in the next task
    private void refreshUI() {
        // lost:
        if (gameManager.getLife() == 0)
            gameManager.updateGameStatus();
        else {

        }
    }

    private void findView() {
        main_BTL_left = findViewById(R.id.main_BTL_left);
        main_BTL_right = findViewById(R.id.main_BTL_right);
    }

    @Override
    public void run(Runnable r) {
        runOnUiThread(r);
    }

}

