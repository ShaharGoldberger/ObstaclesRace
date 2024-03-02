package com.example.obstaclesrace.Model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.obstaclesrace.Adapters.ScoreAdapter;
import com.example.obstaclesrace.R;
import com.example.obstaclesrace.ScoreListFragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class RecordScoreTableActivity extends AppCompatActivity
        implements ScoreAdapter.ScoreClickListener,
        OnMapReadyCallback {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_score_table);

        getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.score_list_fragment, new ScoreListFragment())
                        .commit();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

    }


    private void zoom(double latitude, double longitude) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude,longitude))
                .zoom(10.0f)
                .build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(update);
    }

    @Override
    public void onScoreClicked(Score score) {
        if(googleMap != null) {
            zoom(score.getLatitude(), score.getLongitude());
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }






}