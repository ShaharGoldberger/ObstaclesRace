package com.example.obstaclesrace.Logic;

import android.widget.GridLayout;
import android.widget.ImageView;
import com.example.obstaclesrace.Model.Car;
import com.example.obstaclesrace.Model.Element;
import com.example.obstaclesrace.Model.Obstacle;
import com.example.obstaclesrace.Model.SingletonPattern;
import com.example.obstaclesrace.R;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GameManager {

    private static final int NUMBER_OF_TRACKS = 3;
    private static final int NUMBER_OF_ROWS = 12;
    private Element[][] matrix;
    private Car car;
    private Obstacle[] obstacles;
    private Random random;
    private int life;
    private GridLayout layout;
    private ImageView[] lives;
    private int[] obstacle_rows = new int[NUMBER_OF_TRACKS];
    private boolean[] active_obstacle_lanes = new boolean[NUMBER_OF_TRACKS];
    private int[] activationPeriods = new int[NUMBER_OF_TRACKS];
    private int car_track = NUMBER_OF_TRACKS/2;
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(3);

    public interface RunOnMain {
        void run(Runnable r);
    }
   public GameManager(GridLayout layout, ImageView[] lives,  RunOnMain mainRunner) {
        matrix = new Element[NUMBER_OF_ROWS][NUMBER_OF_TRACKS];
        random = new Random();
        this.lives = lives;
        this.life = 3;
        this.layout = layout;
        obstacles = new Obstacle[NUMBER_OF_TRACKS];
        for(int i = 0; i < NUMBER_OF_TRACKS; i++) {
            obstacles[i] = new Obstacle(layout.getContext());
            matrix[0][i] = obstacles[i];
            matrix[0][i].setPosition(0, i);
        }

        car = new Car(layout.getContext());
        matrix[10][car_track] = car;
        car.setPosition(10, car_track);

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

//        ScheduledFuture<?> obstacleTask =
//                executorService.scheduleAtFixedRate(() -> {
//                if(activeCount() >=2)
//                    return;
//                // choose a random obstacle
//                int randomTrack = random.nextInt(NUMBER_OF_TRACKS);
//
//                if(!active_obstacle_lanes[randomTrack]) {
//                    randomTrack = random.nextInt(NUMBER_OF_TRACKS);
//                }
//                //obstacles[randomTrack].setRandomObst
//                    // acle(layout.getContext());
//                int finalRandomTrack = randomTrack;
//                 executorService.scheduleAtFixedRate(new TimerTask() {
//                     @Override
//                     public void run() {
//                         mainRunner.run(() -> {
//                             if(!moveObstacle(finalRandomTrack)) {
//                                 cancel();
//                                 active_obstacle_lanes[finalRandomTrack] = false;
//                             }
//                         });
//                     }
//                 }, 0, 2, TimeUnit.SECONDS);
//        },2,3, TimeUnit.SECONDS);

       // Initialize a period counter
       final int[] periodCounter = {0};
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
               // Move active obstacles
               for (int track = 0; track < NUMBER_OF_TRACKS; track++) {
                   if (active_obstacle_lanes[track]) {
                       moveObstacle(track);
                   }
               }

               periodCounter[0]++;
           });
       }, 0, 1, TimeUnit.SECONDS); // Adjust the delay and period as necessary
   }


    private int activeCount() {
        int count = 0;
        for(int i = 0; i < NUMBER_OF_TRACKS; i++)
            if(active_obstacle_lanes[i])count++;
        return count;
    }

    public int getLife() {
        return life;
    }

    private void resetLives() {
        this.life = 3;
    }

    public void updateGameStatus() {
        if (life == 0) {
            resetLives();
        }
    }

    public Element[][] getImageMatrix() {
        return matrix;
    }

    public boolean moveObstacle(int lane) {
        if(obstacle_rows[lane] + 1 >= NUMBER_OF_ROWS) {
            resetObstacle(lane);
            return false;
        }
        Element temp  = matrix[obstacle_rows[lane]][lane];
        if(matrix[obstacle_rows[lane] + 1][lane] instanceof Car) {
            resetObstacle(lane);
            // hit --> vibrate + toast
            SingletonPattern.getInstance(null).vibrate(500); // Vibrate for 500 milliseconds
            SingletonPattern.getInstance(null).toastAndVibrate("Hit!!!!");
            life--;
            if(life == 0)
                resetLives();
            renderLife();
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

    public void moveCar(int direction /* -1 left or 1 right*/) {
        if(car_track + direction < 0 || car_track + direction >= NUMBER_OF_TRACKS)
            return; // can't move
        if(matrix[matrix.length-2][car_track + direction] instanceof Obstacle){
            collision();
            resetObstacle(car_track +direction);
            //return;
        }
        swap(10, car_track, 10, car_track + direction);
        System.out.println(car_track);
        System.out.println(direction);
        car_track += direction;
        car.setPosition(10, car_track);
    }

    public void collision(){
        SingletonPattern.getInstance(null).vibrate(500); // Vibrate for 500 milliseconds
        SingletonPattern.getInstance(null).toastAndVibrate("Hit!!!!");
        life--;
        if(life == 0)
            resetLives();
        renderLife();
    }

}
