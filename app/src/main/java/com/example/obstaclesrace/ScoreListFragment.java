package com.example.obstaclesrace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.obstaclesrace.Adapters.ScoreAdapter;
import com.example.obstaclesrace.Logic.SharedPrefManager;
import com.example.obstaclesrace.Model.RecordScoreTableActivity;
import com.example.obstaclesrace.Model.Score;

import java.util.ArrayList;

public class ScoreListFragment extends Fragment {

    private RecyclerView recordTable_LST_scores;
    private SharedPrefManager sharedPrefManager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPrefManager = new SharedPrefManager(getContext());
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.score_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recordTable_LST_scores = view.findViewById(R.id.recordTable_LST_scores);
        initViews();
    }

    private void initViews() {
        ArrayList<Score> scores = sharedPrefManager.getScoresFromPreferences();
        ScoreAdapter movieAdapter = new ScoreAdapter(scores,(RecordScoreTableActivity)getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recordTable_LST_scores.setLayoutManager(linearLayoutManager);
        recordTable_LST_scores.setAdapter(movieAdapter);
    }
}
