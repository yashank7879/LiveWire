package com.livewire.ui.activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.livewire.R;
import com.livewire.ui.fragments.PostJobHomeFragment;
import com.livewire.utils.PreferenceConnector;

public class ClientMainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private TextView textView;
    private LinearLayout tabbar;
    private LinearLayout myJobLl;
    private ImageView ivMyJob;
    private LinearLayout notificationLl;
    private ImageView ivNotification;
    private LinearLayout addLl;
    private ImageView addImg;
    private LinearLayout chatLl;
    private ImageView ivChat;
    private LinearLayout userSettingLl;
    private ImageView ivUser;
    private int clickId;
    private FrameLayout containerId;
    private android.support.v4.app.FragmentManager fm;
    private boolean doubleBackToExitPressedOnce = false;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);
        intializeViews();


    }

    private void intializeViews() {
        fm = getSupportFragmentManager();

        findViewById(R.id.btn_logout).setOnClickListener(this);
        tabbar = (LinearLayout) findViewById(R.id.tabbar);
        myJobLl = (LinearLayout) findViewById(R.id.my_job_ll);
        ivMyJob = (ImageView) findViewById(R.id.iv_my_job);
        notificationLl = (LinearLayout) findViewById(R.id.notification_ll);
        ivNotification = (ImageView) findViewById(R.id.iv_notification);
        addLl = (LinearLayout) findViewById(R.id.add_ll);
        addImg = (ImageView) findViewById(R.id.add_img);
        chatLl = (LinearLayout) findViewById(R.id.chat_ll);
        ivChat = (ImageView) findViewById(R.id.iv_chat);
        userSettingLl = (LinearLayout) findViewById(R.id.user_setting_ll);
        ivUser = (ImageView) findViewById(R.id.iv_user);
        containerId = findViewById(R.id.fl_container);
        TextView tvLiveWire = findViewById(R.id.tv_live_wire);
        tvLiveWire.setText(liveWireText(this));


        myJobLl.setOnClickListener(this);
        notificationLl.setOnClickListener(this);
        addLl.setOnClickListener(this);
        chatLl.setOnClickListener(this);
        userSettingLl.setOnClickListener(this);

        replaceFragment(new PostJobHomeFragment(), false, R.id.fl_container); // first time replace home fragment

        clickId = R.id.add_ll;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                PreferenceConnector.clear(ClientMainActivity.this);
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                            }
                        });
                PreferenceConnector.clear(this);
                Intent intent = new Intent(this, UserSelectionActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.my_job_ll:
                if (clickId != R.id.my_job_ll) {
                    inActiveTab();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivMyJob.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }

                    clickId = R.id.my_job_ll;
                }
                break;
            case R.id.notification_ll:
                if (clickId != R.id.notification_ll) {
                    inActiveTab();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivNotification.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    clickId = R.id.notification_ll;
                }
                break;

            case R.id.add_ll:
                if (clickId != R.id.add_ll) {
                    inActiveTab();
                    clickId = R.id.add_ll;
                }
                break;

            case R.id.chat_ll:
                if (clickId != R.id.chat_ll) {
                    inActiveTab();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivChat.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    clickId = R.id.chat_ll;
                }
                break;

            case R.id.user_setting_ll:
                if (clickId != R.id.user_setting_ll) {
                    inActiveTab();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivUser.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    clickId = R.id.user_setting_ll;
                }
                break;

            default:
        }
    }

    private void inActiveTab() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivMyJob.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            ivChat.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            ivUser.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            ivNotification.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static SpannableStringBuilder liveWireText(Context mContext) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString Name1 = new SpannableString("Live");
        Name1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorGreen)), 0, Name1.length(), 0);
        builder.append(Name1);
        SpannableString interesString = new SpannableString(" Wire");
        interesString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, interesString.length(), 0);
        builder.append(interesString);
        return builder;
    }

    //*********   replace Fragment  ************//
    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        int i = fm.getBackStackEntryCount();
        while (i > 0) {
            fm.popBackStackImmediate();
            i--;
        }
        if (!fragmentPopped) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(containerId, fragment, backStackName);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }

    }
    @Override
    public void onBackPressed() {
        try {
            if (fm.getBackStackEntryCount() > 0) {
                int backStackEntryCount = fm.getBackStackEntryCount();
                Fragment fragment = fm.getFragments().get(backStackEntryCount - 1);
                if (fragment != null) {
                    fragment.onResume();
                }
                fm.popBackStackImmediate();
            } else {
                Handler handler = new Handler();
                if (!doubleBackToExitPressedOnce) {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(runnable = new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                } else {
                    handler.removeCallbacks(runnable);
                    super.onBackPressed();
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "onBackPressed: " + e.getMessage());
        }
    }
}
