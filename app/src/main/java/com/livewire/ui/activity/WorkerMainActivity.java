package com.livewire.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.livewire.R;
import com.livewire.databinding.ActivityWorkerMainBinding;
import com.livewire.ui.fragments.ChatWorkerFragment;
import com.livewire.ui.fragments.HelpOfferedWorkerFragment;
import com.livewire.ui.fragments.MyJobWorkerFragment;
import com.livewire.ui.fragments.MyProfileWorkerFragment;
import com.livewire.ui.fragments.OnGoingWorkerFragment;
import com.livewire.utils.PreferenceConnector;

public class WorkerMainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    ActivityWorkerMainBinding binding;

    private TextView tvHeading;
    private int clickId;
    private android.support.v4.app.FragmentManager fm;
    private boolean doubleBackToExitPressedOnce = false;
    private Runnable runnable;
    private ImageView ivNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_worker_main);

        intializeViews();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void intializeViews() {
        fm = getSupportFragmentManager();

        binding.btnLogout.setOnClickListener(this);
        binding.ongoingJobLl.setOnClickListener(this);
        binding.homeLl.setOnClickListener(this);
        binding.userSettingLl.setOnClickListener(this);
        binding.chatLl.setOnClickListener(this);
        binding.myJobLl.setOnClickListener(this);
        binding.ivSetting.setOnClickListener(this);
        binding.ivNotification.setOnClickListener(this);
        replaceFragment(new HelpOfferedWorkerFragment(), false, R.id.fl_container); // first time replace home fragment
        clickId = R.id.home_ll;

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_logout:
                PreferenceConnector.clear(this);
                intent = new Intent(this, UserSelectionActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.my_job_ll:
                if (clickId != R.id.my_job_ll) {
                    inActiveTab();
                    binding.tvHeading.setText(R.string.my_jobs);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivMyJobs.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    replaceFragment(new MyJobWorkerFragment(), false, R.id.fl_container);
                    clickId = R.id.my_job_ll;
                }
                break;
            case R.id.ongoing_job_ll:
                if (clickId != R.id.ongoing_job_ll) {
                    inActiveTab();
                    binding.tvHeading.setText(R.string.ongoing);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivOngoing.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    replaceFragment(new OnGoingWorkerFragment(), false, R.id.fl_container);
                    clickId = R.id.ongoing_job_ll;
                }
                break;

            case R.id.home_ll:
                if (clickId != R.id.home_ll) {
                    inActiveTab();
                    binding.tvHeading.setText(R.string.help_offered);
                    replaceFragment(new HelpOfferedWorkerFragment(), false, R.id.fl_container);
                    clickId = R.id.home_ll;
                }
                break;

            case R.id.chat_ll:
                if (clickId != R.id.chat_ll) {
                    inActiveTab();
                    binding.tvHeading.setText(R.string.chat);
                    binding.tvHeading.setAllCaps(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivChat.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    replaceFragment(new ChatWorkerFragment(), false, R.id.fl_container);
                    clickId = R.id.chat_ll;
                }
                break;

            case R.id.user_setting_ll:
                if (clickId != R.id.user_setting_ll) {
                    inActiveTab();
                    binding.actionBar.setVisibility(View.GONE);
                    binding.tvHeading.setText(R.string.my_profile);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivUser.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    replaceFragment(new MyProfileWorkerFragment(), false, R.id.fl_container);
                    clickId = R.id.user_setting_ll;
                }
                break;
            case R.id.iv_notification:
                intent = new Intent(this, NotificationListWorkerActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }

    private void inActiveTab() {
        binding.actionBar.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.ivMyJobs.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            binding.ivOngoing.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            binding.ivChat.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            binding.ivUser.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
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
