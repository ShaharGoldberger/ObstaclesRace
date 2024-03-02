package com.example.obstaclesrace;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.example.obstaclesrace.Logic.GameManager;
import com.example.obstaclesrace.Logic.SharedPrefManager;
import com.example.obstaclesrace.Model.BackgroundSound;
import com.example.obstaclesrace.Model.Direction;
import com.example.obstaclesrace.Model.Mode;
import com.example.obstaclesrace.Model.SingletonPattern;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textview.MaterialTextView;

import java.util.UUID;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity
        implements GameManager.RunOnMain, SensorEventListener, GameManager.GameOverListener{

    private static int SENSOR_TIME_THRESHOLD = 500;
    private static float SENSOR_TILT_THRESHOLD = 1.0f;
    public static final String MODE_ARG = "mode";
    private ImageButton main_BTL_left;
    private ImageButton main_BTL_right;
    private MaterialTextView main_LBL_coinsCounter;
    private MaterialTextView main_LBL_odometer;
    private GameManager gameManager;
    private SharedPrefManager sharedPrefManager;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Handler handler = new Handler(Looper.getMainLooper());
    private SimpleLocation simpleLocation;
    private Mode mode;


    private final Runnable odometerRunnable = new Runnable() {
        public void run() {
            //gameManager.updateOdometer();
            refreshUI();
            handler.postDelayed(this, 2000); // Schedule the next update after 1000ms
        }
    };


    void requestLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            },101); // request permissions
        } else { // has permissions already
            simpleLocation.beginUpdates();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==101 && resultCode == RESULT_OK)  {
            simpleLocation.beginUpdates();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridLayout layout = findViewById(R.id.gameLayout);
        findView();
        sharedPrefManager = new SharedPrefManager(this);
        simpleLocation = new SimpleLocation(this);
        requestLocationPermission();

        sensorManager = getSystemService(SensorManager.class);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        String mode_as_string = getIntent().getStringExtra(MODE_ARG);
        if(mode_as_string == null) // just in case
             mode_as_string = "FAST";
        mode = Mode.valueOf(mode_as_string);

        if(mode.hasSensor()) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }

        gameManager = new GameManager(
                mode,
                layout,
                new ImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
                },
                findViewById(R.id.main_LBL_coinsCounter),
                findViewById(R.id.main_LBL_odometer),
                this);

        SingletonPattern.getInstance(this); // Initialize the singleton here
        setupButtonListeners(); // Setup button listeners
        handler.post(odometerRunnable); // Start the odometer updates

        gameManager.setGameOverListener(this);

        refreshUI();
    }

    private void setupButtonListeners() {
        main_BTL_left.setOnClickListener(v -> {
            gameManager.moveCar(Direction.LEFT);
        });
        main_BTL_right.setOnClickListener(v -> {
            gameManager.moveCar(Direction.RIGHT);
        });
    }


    //in the next task
    private void refreshUI() {
        //main_LBL_coinsCounter.setText("$ " + gameManager.getCoinsCounter() );
        main_LBL_odometer.setText("Odometer: " + gameManager.getOdometer());
    }

    private void findView() {
        main_BTL_left = findViewById(R.id.main_BTL_left);
        main_BTL_right = findViewById(R.id.main_BTL_right);
        main_LBL_coinsCounter = findViewById(R.id.main_LBL_coinsCounter);
        main_LBL_odometer = findViewById(R.id.main_LBL_odometer);
    }

    @Override
    public void run(Runnable r) {
        runOnUiThread(r);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(odometerRunnable); // Pause the odometer updates
    }

    private int lastMovementTime;
    private int lastX;
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        long now = System.currentTimeMillis();
        if(now - lastMovementTime > SENSOR_TIME_THRESHOLD &&  Math.abs(x - lastX) > SENSOR_TILT_THRESHOLD) {
            if(x > lastX) {
                gameManager.moveCar(Direction.LEFT);
            } else {
                gameManager.moveCar(Direction.RIGHT);
            }
            lastMovementTime = (int) System.currentTimeMillis();
        }
        lastX = (int) x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //pass
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensor != null && mode.hasSensor()) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
        handler.post(odometerRunnable); // Resume the odometer updates
    }

    @Override
    public void onGameOver() {
        SharedPreferences sharedPreferences = getSharedPreferences("gamePrefs", MODE_PRIVATE);
        String name = getIntent().getStringExtra("name");
        if(name == null) {
            name = "Anonymous_" + UUID.randomUUID().toString();
        }
        sharedPrefManager.updateScores(name, gameManager.getCoinsCounter(), new LatLng(simpleLocation.getLatitude(),simpleLocation.getLongitude()));

        Intent intent = new Intent(MainActivity.this, EndGameActivity.class);
        intent.putExtra(EndGameActivity.KEY_SCORE, gameManager.getCoinsCounter()); // Use consistent key
        startActivity(intent);
        finish();
    }
}

