package com.example.obstaclesrace.Logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.obstaclesrace.Model.Score;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SharedPrefManager {

    private SharedPreferences sp;

    public SharedPrefManager(Context context) {
        this.sp = context.getSharedPreferences("gameScores",Context.MODE_PRIVATE);
    }


    public void updateScores(String name,
                             int newScore,
                             LatLng location) {
        ArrayList<Score> scores = getScoresFromPreferences();
        Score existing = null;
        for(Score score: scores) {
            if(score.getName().equals(name))
            {
                existing = score;
            }
        }
        if(existing == null) {
            scores.add(new Score(name, newScore, location.latitude, location.longitude));
        }else {
            existing.setScore(Math.max(existing.getScore(), newScore));

        }
        // Optionally sort scores in descending order
         //scores.sort(Collections.reverseOrder())
        // Sort scores in descending order based on the compareTo method in Score
        Collections.sort(scores);

        // Keep only the top 10 scores
        while (scores.size() > 10) {
            scores.remove(scores.size() - 1);
        }

        // Save the updated scores list back to SharedPreferences
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("gameScores", new Gson().toJson(scores));
        editor.apply();
    }


    public ArrayList<Score> getScoresFromPreferences() {
        //String scoresJson = sp.getString("gameScores", null);
        //ArrayList<Score> scores = new ArrayList<>();
        //if (scoresJson != null) {
          //  Gson g = new Gson();
            //Set<String> scoresStrings = sp.getStringSet("gameScores", new HashSet<>());
            //for(String scoreString : scoresStrings) {
              //  scores.add(g.fromJson(scoreString, Score.class));
            //}
        //}
        //return scores;


        ArrayList<Score> scores = new ArrayList<>();
        String scoresJson = sp.getString("gameScores", null); // Correctly retrieve the JSON string
        if (scoresJson != null) {
            Log.d("SharedPrefManager", "Scores JSON: " + scoresJson);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Score>>() {}.getType();
            try {
                scores = gson.fromJson(scoresJson, type);
            } catch (JsonSyntaxException e) {
                Log.e("SharedPrefManager", "JSON Parsing error", e);
            }
        }
        return scores;
    }



}
