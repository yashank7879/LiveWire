package com.livewire.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livewire.R;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;

public class SplashActivity extends AppCompatActivity {
    private ImageView image;
    private RotateAnimation rotate;
    private TextView tvLiveWire;
    private Handler mHandlers = new Handler();
    private Runnable mRunnable;
    private LinearLayout upperLl;
    private Animation zoomOut;
    private FrameLayout upperFl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        upperLl = findViewById(R.id.upper_ll);
        upperFl = findViewById(R.id.fl_upper);
        image = findViewById(R.id.image_view);
        tvLiveWire = findViewById(R.id.tv_live_wire);
        Log.e("authToken",PreferenceConnector.readString(this,PreferenceConnector.AUTH_TOKEN,""));
       // zoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out);

         mRunnable = new Runnable() {
            @Override
            public void run() {

                Intent intent =null;
                        if (PreferenceConnector.readBoolean(SplashActivity.this,PreferenceConnector.IS_LOG_IN,false)){// is login
                            if (PreferenceConnector.readString(SplashActivity.this,PreferenceConnector.USER_TYPE,"").equals("worker")){//if user is worker
                                if (PreferenceConnector.readString(SplashActivity.this,PreferenceConnector.COMPLETE_PROFILE_STATUS,"").equals("0")){//if user not complete profile
                                    intent = new Intent(SplashActivity.this,CompleteProfileActivity.class);
                                }else { //"""" if worker complete profile
                                    intent = new Intent(SplashActivity.this,WorkerMainActivity.class);
                                }
                            }else { //""""""" if user is Client
                                intent = new Intent(SplashActivity.this,ClientMainActivity.class);
                            }
                        }else {//""""" if user not login
                            intent = new Intent(SplashActivity.this,UserSelectionActivity.class);
                        }

                startActivity(intent);
                finish();
            }
        };
         mHandlers.postDelayed(mRunnable,2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        imageRotatorAndTextColorChange();
    }

    private void imageRotatorAndTextColorChange() {
        rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setInterpolator(new LinearInterpolator());
        tvLiveWire.setText(Constant.liveWireText(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        image.startAnimation(rotate);
        image.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHandlers.removeCallbacks(mRunnable);
    }

   /* @Override
    protected void onPostResume() {
        super.onPostResume();
        upperFl.startAnimation(zoomOut);
        zoomOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                upperLl.setVisibility(View.VISIBLE);
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(upperLl, PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                        PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                        PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));
                scaleDown.setDuration(1000);
                scaleDown.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }*/
}
