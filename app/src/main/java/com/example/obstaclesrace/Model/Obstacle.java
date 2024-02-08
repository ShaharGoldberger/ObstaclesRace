package com.example.obstaclesrace.Model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.example.obstaclesrace.R;
import java.util.Random;

public class Obstacle extends Element {

    public Obstacle(Context context) {
        super(new ImageView(context));
        getElementImage().setImageResource(R.drawable.stone_obstacle);
    }

    public void setRandomObstacle(Context context) {
        Random r = new Random();
        Drawable stoneObstacle = context.getResources().getDrawable(R.drawable.stone_obstacle, context.getTheme());
        Drawable obstacle = context.getResources().getDrawable(R.drawable.obstacle, context.getTheme());
        // Randomly choose between stoneObstacle and obstacle
        Drawable chosenObstacle = r.nextBoolean() ? stoneObstacle : obstacle;
        getElementImage().setImageDrawable(chosenObstacle);
    }
}
