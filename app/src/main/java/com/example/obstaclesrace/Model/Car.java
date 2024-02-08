package com.example.obstaclesrace.Model;

import android.content.Context;
import android.widget.ImageView;

import com.example.obstaclesrace.R;

public class Car extends Element{

    public Car(Context context) {
        super(new ImageView(context));
        getElementImage().setImageResource(R.drawable.racing_car);
    }


}
