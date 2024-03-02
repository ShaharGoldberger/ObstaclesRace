package com.example.obstaclesrace.Logic;

import static android.content.Intent.getIntent;

import static com.example.obstaclesrace.EndGameActivity.KEY_SCORE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.obstaclesrace.EndGameActivity;
import com.example.obstaclesrace.GameSound;
import com.example.obstaclesrace.MainActivity;
import com.example.obstaclesrace.Model.Car;
import com.example.obstaclesrace.Model.Direction;
import com.example.obstaclesrace.Model.Element;
import com.example.obstaclesrace.Model.Mode;
import com.example.obstaclesrace.Model.Obstacle;
import com.example.obstaclesrace.Model.Score;
import com.example.obstaclesrace.Model.SingletonPattern;
import com.example.obstaclesrace.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class GameManager {
    private static final int NUMBER_OF_TRACKS = 5;
    private static final int NUMBER_OF_ROWS = 20;
    private static int STEP = 1;
    private static int NUMBER_OF_COINS = 10;
    private Element[][] matrix;
    private Car car;
    private Obstacle[] obstacles;
    private Random random;
    private int life;
    private GridLayout layout;
    private ImageView[] lives;
    private MaterialTextView coins;
    private MaterialTextView main_LBL_odometer;
    private int[] obstacle_rows = new int[NUMBER_OF_TRACKS];
    private boolean[] active_obstacle_lanes = new boolean[NUMBER_OF_TRACKS];
    private int[] activationPeriods = new int[NUMBER_OF_TRACKS];
    private int car_track = NUMBER_OF_TRACKS/2;
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(3);
    // Initialize a period counter
    private final int[] periodCounter = {0};
    private Mode mode;
    private GameSound sounds;
    private int coinsCounter;
    private int odometer; //distance counter
    private AppCompatActivity activity; // Reference to the activity
    private GameOverListener gameOverListener;

    public interface RunOnMain {
        void run(Runnable r);
    }

    public interface GameOverListener {
        void onGameOver();
    }


    public GameManager(
           Mode mode,
           GridLayout layout, ImageView[] lives, MaterialTextView coins,
           MaterialTextView main_LBL_odometer,
           RunOnMain mainRunner) {
        sounds = new GameSound(layout.getContext());
        matrix = new Element[NUMBER_OF_ROWS][NUMBER_OF_TRACKS];
        random = new Random();
        this.mode = mode;
        this.lives = lives;
        this.coins = coins;
        this.life = 3;
        this.layout = layout;
        this.coinsCounter =0;
        this.odometer = 0;
        this.activity = activity;
        this.main_LBL_odometer =main_LBL_odometer;
        //this.scoresForTableScores = new ArrayList<>();
       obstacles = new Obstacle[NUMBER_OF_TRACKS];
        int lotteryResult;
        for(int i = 0; i < NUMBER_OF_TRACKS; i++) {
            lotteryResult = random.nextInt(2); // Generates either 0 or 1
            obstacles[i] = new Obstacle(layout.getContext(), lotteryResult);
            matrix[0][i] = obstacles[i];
            matrix[0][i].setPosition(0, i);
        }

        car = new Car(layout.getContext());
        matrix[NUMBER_OF_ROWS-2][car_track] = car;
        car.setPosition(NUMBER_OF_ROWS-2, car_track);

        for(int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_TRACKS; j++) {
                if(matrix[i][j] == null) {
                    matrix[i][j] = new Element(new ImageView(layout.getContext()));
                    matrix[i][j].setPosition(i, j);
                }
            }
        }

        for(int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_TRACKS; j++) {
                layout.addView(matrix[i][j].getElementImage());
            }
        }

       // Generate random activation periods
       for (int i = 0; i < NUMBER_OF_TRACKS; i++) {
           activationPeriods[i] = 1 + random.nextInt(4); // Random period between 1 and 5
       }
       // Schedule a task that activates obstacles over time and moves active ones
       executorService.scheduleAtFixedRate(() -> {
           mainRunner.run(() -> {
               // Activate obstacles over time
               if (periodCounter[0] == activationPeriods[0]) { // After two periods, activate the second obstacle
                   active_obstacle_lanes[0] = true;
               }
               if (periodCounter[0] == activationPeriods[1]) { // After another period, activate the third obstacle
                   active_obstacle_lanes[1] = true;
               }
               if (periodCounter[0] == activationPeriods[2]) { // After another period, activate the third obstacle
                   active_obstacle_lanes[2] = true;
               }
               if (periodCounter[0] == activationPeriods[3]) { // After another period, activate the third obstacle
                   active_obstacle_lanes[3] = true;
               }
               if (periodCounter[0] == activationPeriods[4]) { // After another period, activate the third obstacle
                   active_obstacle_lanes[4] = true;
               }
               // Move active obstacles
               for (int track = 0; track < NUMBER_OF_TRACKS; track++) {
                   if (active_obstacle_lanes[track]) {
                       moveObstacle(track);
                   }
               }
               periodCounter[0]++;
               updateOdometer();
           });
       }, 0, (int) (1000 / mode.obstacleSpawnDelay()), TimeUnit.MILLISECONDS);
   }


    public int getCoinsCounter() {
        return coinsCounter;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    private void gameOver() {
        executorService.shutdownNow(); // Stop the scheduled tasks
        if (gameOverListener != null) {
            gameOverListener.onGameOver();
        }
    }

    public boolean moveObstacle(int lane) {
        if(obstacle_rows[lane] + 1 >= NUMBER_OF_ROWS) {
            resetObstacle(lane);
            setRandomObstacle((Obstacle) matrix[obstacle_rows[lane]][lane]);
            return false;
        }
        Element temp  = matrix[obstacle_rows[lane]][lane];
        if(matrix[obstacle_rows[lane] + 1][lane] instanceof Car) {
            resetObstacle(lane);
            collision((Obstacle) matrix[0][lane]);
            setRandomObstacle((Obstacle) temp);
            return false;
        }
        swap(obstacle_rows[lane], lane,obstacle_rows[lane] + 1,lane);
        //update
        obstacle_rows[lane]++;
        temp.setPosition(obstacle_rows[lane], lane);
        return true;
    }

    public boolean isHit(int lane){
        if(matrix[obstacle_rows[lane] + 1][lane] instanceof Car)
            return true;
        else
            return false;
    }

    private void renderLife() {
        for(int i = 0; i < life; i++) {
            lives[i].setImageResource(R.drawable.heart);
        }

        for(int i = life; i < 3; i++) {
            lives[i].setImageResource(R.drawable.heart_unfill);
        }
    }

    private void resetObstacle(int lane) {
        Element temp  = matrix[obstacle_rows[lane]][lane];
        if(temp instanceof Obstacle){
            active_obstacle_lanes[lane] = false;
            activationPeriods[lane] = periodCounter[0] + 1 + random.nextInt(4);
            swap(obstacle_rows[lane], lane,0,lane);
            obstacle_rows[lane] = 0;
            temp.setPosition(obstacle_rows[lane], lane);
        }
    }

    private void swap(int row1,int col1, int row2,int col2) {
        Element temp  = matrix[row1][col1];
        matrix[row1][col1] = matrix[row2][col2];
        matrix[row2][col2] = temp;
    }

    public void moveCar(Direction direction /* -1 left or 1 right*/) {
        if(car_track + direction.getValue() < 0 || car_track + direction.getValue() >= NUMBER_OF_TRACKS)
            return; // can't move
        Element temp = matrix[matrix.length - 2][car_track + direction.getValue()];
        if(temp instanceof Obstacle){
            collision((Obstacle) matrix[matrix.length-2][car_track + direction.getValue()]);
            resetObstacle(car_track +direction.getValue());
            //((Obstacle) temp).setFlag(random.nextInt(2));
            setRandomObstacle((Obstacle) temp);
            //return;
        }
        swap(NUMBER_OF_ROWS-2, car_track, NUMBER_OF_ROWS-2, car_track + direction.getValue());
        car_track += direction.getValue();
        car.setPosition(NUMBER_OF_ROWS-2, car_track);
    }

    public void collision(Obstacle obstacle){
        if(obstacle.getFlag() == 0) { // 0 for stone
            sounds.crash();
            SingletonPattern.getInstance(null).vibrate(500); // Vibrate for 500 milliseconds
            SingletonPattern.getInstance(null).toastAndVibrate("Hit!!!!");
            life--;
            if (life == 0)
                gameOver();
            //resetLives();
            renderLife();
        } else if(obstacle.getFlag() == 1) { // 1 for coin
            sounds.crash_coin();
            coinsCounter+=NUMBER_OF_COINS;
            coins.setText("$ " + getCoinsCounter() );
        }
    }

    //update distance counter
    public void updateOdometer(){
        if(this.life != 0){
            odometer+= STEP;
            main_LBL_odometer.setText("Odometer: " + odometer);
        }

    }

    private void setRandomObstacle(Obstacle o){
        o.setFlag(random.nextInt(2));
    }

}
