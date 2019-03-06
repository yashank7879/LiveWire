package com.livewire.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.livewire.R;
import com.livewire.utils.Constant;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mindiii on 2/26/19.
 */

public class RotatorExampleActivity extends AppCompatActivity {
    private RotateAnimation rotate;
    private ImageView image;
    Button startButton, pauseButton;
    TextView timerValue;
    Timer timer;
    int seconds = 0, minutes = 0, hour = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        image = findViewById(R.id.image_view);
        image.setVisibility(View.VISIBLE);
       // image.startAnimation(rotate);
        imageRotatorAndTextColorChange();
        bindView();
        startTimer();

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerValue.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerValue.setText("done!");
            }
        }.start();

        timer = new Timer();
       /* startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (seconds == 60) {
                                    timerValue.setText(String.format("%02d", hour) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                                    minutes = seconds / 60;
                                    seconds = seconds % 60;
                                    hour = minutes / 60;
                                }
                                seconds += 1;
                                timerValue.setText(String.format("%02d", hour) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                            }
                        });
                    }
                }, 0, 1000);
            }
        });*/
    }

    public void startTimer() {
        if (timer != null) {
            stopTimer();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(Timer_Tick);
            }

        }, 0, 10000);//10 sec

    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    private boolean isCompleteService;
    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            if (!isCompleteService){
                Toast.makeText(RotatorExampleActivity.this, "hello", Toast.LENGTH_SHORT).show();
            }
             //   getDetailsBookService(bookingId);
        }
    };


    private void bindView() {
        timerValue = (TextView) findViewById(R.id.tv_live_wire);
        //startButton = (Button) findViewById(R.id.startButton);
    }

    private void imageRotatorAndTextColorChange() {
        rotate = new RotateAnimation(0, 4080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(10000);

        rotate.setInterpolator(new LinearInterpolator());
        image.startAnimation(rotate);
     /*   image.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.rotator_one) );*/
    }

}
