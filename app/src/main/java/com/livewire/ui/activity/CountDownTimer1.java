package com.livewire.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.livewire.R;

/**
 * Created by mindiii on 3/7/19.
 */

public class CountDownTimer1 extends AppCompatActivity {

    private TextView mTextField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_count_down);
        mTextField = findViewById(R.id.tv_timer);
        new CountDownTimer(10000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                mTextField.setText((millisUntilFinished / 60000)+":"+(millisUntilFinished % 60000 / 1000));

            }

            @Override
            public void onFinish() {

            }
        }.start();

    }
}

 /*   int seconds = (int) (milliseconds / 1000) % 60 ;
    int minutes = (int) ((milliseconds / (1000*60)) % 60);
    int hours   = (int) ((milliseconds / (1000*60*60)) % 24);

     Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
  <uses-permission android:name="android.permission.VIBRATE"/> // permission in manifest

   https://stackoverflow.com/questions/17940200/how-to-find-the-duration-of-difference-between-two-dates-in-java
    */