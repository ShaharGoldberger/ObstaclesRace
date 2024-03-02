package com.example.obstaclesrace.Model;

import java.security.InvalidParameterException;

public enum Mode {
    SENSOR_SLOW, SENSOR_FAST, FAST, SLOW;


    public double obstacleSpawnDelay() {
        switch(this) {
            case SENSOR_FAST:
            case FAST:
                return 5;
            case SENSOR_SLOW:
            case SLOW:
                return 2;
            default:
                throw new InvalidParameterException("Invalid game mode");
        }
    }

    public boolean hasSensor() {
        switch(this) {
            case SLOW: return false;
            case FAST: return false;
            case SENSOR_FAST:
            case SENSOR_SLOW:
                return true;
        }
        return false;
    }
}
