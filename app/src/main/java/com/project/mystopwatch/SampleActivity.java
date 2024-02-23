package com.project.mystopwatch;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SampleActivity extends AppCompatActivity {

        private Chronometer chronometer;
        private Button startButton, lapButton, resetButton;
        private TextView lapsTextView;
        private boolean isRunning = false;
        private long timeElapsed, timeLeft = 60000; // 1 minute countdown
        private int lapCount = 1;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sample);

            chronometer = findViewById(R.id.chronometer);
            startButton = findViewById(R.id.startButton);
            lapButton = findViewById(R.id.lapButton);
            resetButton = findViewById(R.id.resetButton);
            lapsTextView = findViewById(R.id.lapsTextView);

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isRunning) {
                        chronometer.setBase(System.currentTimeMillis() - timeElapsed);
                        chronometer.start();
                        startButton.setText("Stop");
                        isRunning = true;
                        startCountdown();
                    } else {
                        chronometer.stop();
                        timeElapsed = System.currentTimeMillis() - chronometer.getBase();
                        startButton.setText("Start");
                        isRunning = false;
                        stopCountdown();
                    }
                }
            });

            lapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isRunning) {
                        String time = chronometer.getText().toString();
                        String lap = "Lap " + lapCount + ": " + time + "\n";
                        lapsTextView.append(lap);
                        lapCount++;
                    }
                }
            });

            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chronometer.setBase(System.currentTimeMillis());
                    timeElapsed = 0;
                    chronometer.stop();
                    chronometer.setText("00:00:00");
                    startButton.setText("Start");
                    isRunning = false;
                    stopCountdown();
                    lapsTextView.setText("");
                    lapCount = 1;
                }
            });
        }

        private void startCountdown() {
            new CountDownTimer(timeLeft, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeft = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    // Countdown finished, perform actions here
                    // For example:
                    chronometer.stop();
                    timeElapsed = 0;
                    chronometer.setText("00:00:00");
                    startButton.setText("Start");
                    isRunning = false;
                    lapsTextView.setText("");
                    lapCount = 1;
                }
            }.start();
        }

        private void stopCountdown() {
            // Stop the countdown
        }
    }