package com.example.obstaclesrace.Model;

import android.widget.GridLayout;
import android.widget.ImageView;

public class Element {
    protected ImageView elementImage; // Drawable resource ID for the car image

    public Element(ImageView view) {
        this.elementImage = view;
    }

    public ImageView getElementImage() {
        return elementImage;
    }

    public void setElementImage(ImageView elementImage) {
        this.elementImage = elementImage;
    }

    public void setPosition(int row, int col) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.columnSpec = GridLayout.spec(col,1,1);
        params.rowSpec = GridLayout.spec(row,1,1);
        params.height = 0;
        params.width = 0;
        this.elementImage.setLayoutParams(params);
    }
}
