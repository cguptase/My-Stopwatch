package com.project.mystopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.NumberPicker;

import com.project.mystopwatch.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    boolean isRunning = false;
    boolean isPaused = false;
    long pausedTime = 0;
    private MediaPlayer mediaPlayer;


    private String minutes = "00:00:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<String> lapsList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lapsList);
        binding.lapList.setAdapter(arrayAdapter);
        binding.lapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    lapsList.add(binding.timerChronometer.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });


        binding.clockImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog);
                NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(60);
                Button setTime = dialog.findViewById(R.id.setTimeBtn);
                setTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        minutes = String.valueOf(numberPicker.getValue());
                        binding.timeTV.setText(numberPicker.getValue() + " minutes");
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        binding.stopwatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRunning) {
                    isRunning = true;
                    if (!isPaused) { // Start or resume the timer based on pause state
                        if (!minutes.equals("00:00:00")) {
                            Long totalmin = Integer.parseInt(minutes) * 60 * 1000L;
                            binding.timerChronometer.setBase(SystemClock.elapsedRealtime() + totalmin);
                            binding.timerChronometer.setFormat("%S %S");
                            // Start playing audio
                            playAudio();
                        } else {
                            // Countdown timer is not set, start stopwatch from zero
                            binding.timerChronometer.setBase(SystemClock.elapsedRealtime());
                        }
                        binding.timerChronometer.start();
                        binding.stopwatchBtn.setText("Pause");
                    } else { // Resume the timer
                        long currentTime = SystemClock.elapsedRealtime();
                        long timeElapsedSincePaused = currentTime - pausedTime;
                        binding.timerChronometer.setBase(binding.timerChronometer.getBase() + timeElapsedSincePaused);
                        binding.timerChronometer.start();
                        binding.stopwatchBtn.setText("Pause");
                        isPaused = false; // Reset pause state
                    }
                } else {
                    pausedTime = SystemClock.elapsedRealtime(); // Store the time when paused
                    binding.timerChronometer.stop();
                    isRunning = false;
                    binding.stopwatchBtn.setText("Start");
                    isPaused = true; // Set pause state
                }
            }
        });


        binding.resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

                if (!minutes.equals("00:00:00")) {
                    // Countdown mode is active
                    binding.timerChronometer.stop();
                    isRunning = false;
                    isPaused = false;
                    Long totalmin = Integer.parseInt(minutes) * 60 * 1000L;
                    binding.timerChronometer.setBase(SystemClock.elapsedRealtime() + totalmin);
                    binding.timerChronometer.setText("00:00:00");
                    binding.timeTV.setText(minutes + " minutes");
                    binding.stopwatchBtn.setText("Start");
                } else {
                    // Stopwatch mode is active
                    binding.timerChronometer.stop();
                    isRunning = false;
                    isPaused = false;
                    binding.timerChronometer.setText("00:00:00");
                    binding.timeTV.setText("00:00:00"); // Reset timeTV to default value
                    binding.stopwatchBtn.setText("Start");
                }
                lapsList.clear();
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void playAudio() {
        mediaPlayer = MediaPlayer.create(this, R.raw.tic);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Calculate total time
        final long totalTime = Integer.parseInt(minutes) * 60 * 1000L; // Total time in milliseconds

        // Start a countdown timer to stop audio when 2 seconds are left
        new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Check if remaining time is less than 2 seconds and stop audio
                if (millisUntilFinished <= 2000 && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }

            @Override
            public void onFinish() {
                // Check if audio is still playing and stop it
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                // Reset UI state
                resetUIState();
            }
        }.start();
    }

    private void resetUIState() {
        // Stop the chronometer
        binding.timerChronometer.stop();
        // Reset running and paused states
        isRunning = false;
        isPaused = false;
        // Reset chronometer text
        binding.timerChronometer.setText("00:00:00");
        // Reset timeTV to its initial position
        binding.timeTV.setText("00:00:00");
        // Set "Start" text on the stopwatch button
        binding.stopwatchBtn.setText("Start");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}