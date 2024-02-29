package com.example.obstaclesrace.Model;

public enum Direction {
    LEFT(-1),
    RIGHT(1);

    Direction(int dir) {
        this.dir = dir;
    }
    private int dir;

    public int getValue() {
        return dir;
    }
}
