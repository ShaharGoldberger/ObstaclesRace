package com.example.obstaclesrace;

import static com.example.obstaclesrace.MainActivity.MODE_ARG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.obstaclesrace.Model.RecordScoreTableActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSensorModeSlow, buttonSensorModeFast;
    private Button buttonFastMode;
    private Button buttonSlowMode;
    private Button buttonHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findView();

        buttonSensorModeSlow.setOnClickListener(this);
        buttonSensorModeFast.setOnClickListener(this);

        buttonFastMode.setOnClickListener(this);
        buttonSlowMode.setOnClickListener(this);
        buttonHighScore.setOnClickListener(this);
    }

    private void findView() {
        buttonSensorModeSlow = findViewById(R.id.buttonSensorModeSlow);
        buttonSensorModeFast = findViewById(R.id.buttonSensorModeFast);
        buttonFastMode = findViewById(R.id.buttonFastMode);
        buttonSlowMode = findViewById(R.id.buttonSlowMode);
        buttonHighScore = findViewById(R.id.buttonHighScore);
    }

    private void askNameDialogAndMove(String mode, Intent intent) {
        View v = LayoutInflater.from(this).inflate(R.layout.name_layout, null, false);

        EditText nameEt = v.findViewById(R.id.name_tv);
        new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("Start game : " + mode)
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEt.getText().toString();
                        if (name.isEmpty()) {
                            Toast.makeText(MenuActivity.this, "Please enter  a valid name", Toast.LENGTH_LONG).show();
                        } else {
                            intent.putExtra("name", name);
                            intent.putExtra(MainActivity.MODE_ARG, mode); // This line ensures the mode is passed
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .show();
    }


    @Override
    public void onClick(View v) {
        /*
        //Intent intent = null;
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        String mode = "";
        if(v == buttonSensorModeSlow) {
            intent = new Intent(this, MainActivity.class);
            mode  = "Sensor Slow";
            bundle.putString(MODE_ARG, "SENSOR_SLOW");
        }
        else if(v == buttonSensorModeFast) {
            intent = new Intent(this, MainActivity.class);
            bundle.putString(MODE_ARG, "SENSOR_FAST");
            mode  = "Sensor fast";
        }
        else if(v == buttonFastMode) {
            intent = new Intent(this, MainActivity.class);
            bundle.putString(MODE_ARG, "FAST");
            mode  = "Fast";
        }
        else if(v== buttonSlowMode) {
            intent = new Intent(this, MainActivity.class);
            bundle.putString(MODE_ARG, "SLOW");
            mode  = "Slow";
        }
        else if(v == buttonHighScore) {
            intent = new Intent(this, RecordScoreTableActivity.class);
        }
        if(intent!=null) {
            if (!mode.isEmpty()) {
                askNameDialogAndMove(mode, intent);
            } else {
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
*/

        Intent intent = new Intent(this, MainActivity.class);
        String mode = null;

        if (v == buttonSensorModeSlow) {
            mode = "SENSOR_SLOW";
        } else if (v == buttonSensorModeFast) {
            mode = "SENSOR_FAST";
        } else if (v == buttonFastMode) {
            mode = "FAST";
        } else if (v == buttonSlowMode) {
            mode = "SLOW";
        } else if (v == buttonHighScore) {
            intent = new Intent(this, RecordScoreTableActivity.class);
            startActivity(intent);
            return; // Early return to skip MainActivity related logic
        }

        if (intent != null) {
            if (!mode.isEmpty()) {
                askNameDialogAndMove(mode, intent);
            } else {
                startActivity(intent);
            }
        }
    }
}