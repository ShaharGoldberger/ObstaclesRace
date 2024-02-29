package com.example.obstaclesrace.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.obstaclesrace.Model.Score;
import com.example.obstaclesrace.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private ArrayList<Score> scores;

    private ScoreClickListener listener;

    public interface ScoreClickListener {
        void onScoreClicked(Score score);
    }

    public ScoreAdapter(ArrayList<Score> scores, ScoreClickListener listener) {
        this.scores = scores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horisontal_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        Score score = getItem(position);
        holder.populate(score);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }
    private Score getItem(int position) {
        return scores.get(position);
    }


    public class ScoreViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView score_LBL_number;
        private MaterialTextView score_LBL_name;
        private MaterialTextView score_LBL_location;


        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            score_LBL_name = itemView.findViewById(R.id.score_LBL_name);
            score_LBL_location = itemView.findViewById(R.id.score_LBL_location);
            score_LBL_number = itemView.findViewById(R.id.score_LBL_number);
        }

        public void populate(Score score) {
            itemView.setOnClickListener(v -> listener.onScoreClicked(score));
            score_LBL_name.setText("Name: " + score.getName());
            score_LBL_number.setText("Score: " + score.getScore());
            score_LBL_location.setText("Location: (" + score.getLatitude() +" ," + score.getLongitude() + ")");
        }
    }


}
