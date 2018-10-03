package com.livewire.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.livewire.R;
import com.livewire.ui.fragments.HelpOfferedFragment;
import com.livewire.utils.PreferenceConnector;

public class WorkerMainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private TextView textView;
    private LinearLayout tabbar;
    private LinearLayout myJobLl;
    private ImageView ivMyJobs;
    private LinearLayout ongoingJobLl;
    private ImageView ivOngoing;
    private LinearLayout homeLl;
    private ImageView interestImg;
    private LinearLayout chatLl;
    private ImageView ivChat;
    private LinearLayout userSettingLl;
    private ImageView ivUser;
    private GoogleApiClient mGoogleApiClient;
    private int clickId;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_main);

        intializeViews();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void intializeViews() {
        fm = getSupportFragmentManager();

        findViewById(R.id.btn_logout).setOnClickListener(this);
        FrameLayout container = findViewById(R.id.fl_container);
        tabbar =  findViewById(R.id.tabbar);
        myJobLl =  findViewById(R.id.my_job_ll);
        ivMyJobs =  findViewById(R.id.iv_my_jobs);
        ongoingJobLl = findViewById(R.id.ongoing_job_ll);
        ivOngoing = findViewById(R.id.iv_ongoing);
        homeLl = findViewById(R.id.home_ll);
        interestImg =  findViewById(R.id.interest_img);
        chatLl = findViewById(R.id.chat_ll);
        ivChat = findViewById(R.id.iv_chat);
        userSettingLl =  findViewById(R.id.user_setting_ll);
        ivUser = findViewById(R.id.iv_user);

        ongoingJobLl.setOnClickListener(this);
        homeLl.setOnClickListener(this);
        userSettingLl.setOnClickListener(this);
        chatLl.setOnClickListener(this);
        myJobLl.setOnClickListener(this);

        replaceFragment(new HelpOfferedFragment(), false, R.id.fl_container); // first time replace home fragment
        clickId = R.id.home_ll;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_logout:
                PreferenceConnector.clear(WorkerMainActivity.this);
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                            }
                        });

                PreferenceConnector.clear(this);
                Intent intent = new Intent(this,UserSelectionActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.my_job_ll:
                if (clickId != R.id.my_job_ll){
                    inActiveTab();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivMyJobs.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }

                    clickId = R.id.my_job_ll;
                }
                break;
            case R.id.ongoing_job_ll:
                if (clickId != R.id.ongoing_job_ll){
                    inActiveTab();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivOngoing.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }

                    clickId = R.id.ongoing_job_ll;
                }
                break;

            case R.id.home_ll:
                if (clickId != R.id.home_ll){
                    inActiveTab();
                    replaceFragment(new HelpOfferedFragment(), false, R.id.fl_container);
                    clickId = R.id.home_ll;
                }
                break;

            case R.id.chat_ll:
                if (clickId != R.id.chat_ll){
                    inActiveTab();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivChat.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    clickId = R.id.chat_ll;
                }
                break;

            case R.id.user_setting_ll:
                if (clickId != R.id.user_setting_ll){
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
            ivMyJobs.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            ivOngoing.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            ivChat.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            ivUser.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
        }
         }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

}
