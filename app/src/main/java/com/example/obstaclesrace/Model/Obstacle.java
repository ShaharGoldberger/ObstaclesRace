package com.example.obstaclesrace.Model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.example.obstaclesrace.R;
import java.util.Random;

public class Obstacle extends Element {

    private int flag; //0 for stone, 1 for coin

    public Obstacle(Context context, int flag) {
        super(new ImageView(context));
        setFlag(flag);
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
        if(flag == 0)
            getElementImage().setImageResource(R.drawable.stone_obstacle);
        else
            getElementImage().setImageResource(R.drawable.coin);
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
