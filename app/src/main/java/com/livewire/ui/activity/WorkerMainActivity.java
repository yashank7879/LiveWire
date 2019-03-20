package com.livewire.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.livewire.R;
import com.livewire.databinding.ActivityWorkerMainBinding;
import com.livewire.ui.activity.chat.ChattingActivity;
import com.livewire.ui.fragments.ChatWorkerFragment;
import com.livewire.ui.fragments.JobRequestFragment;
import com.livewire.ui.fragments.MyJobWorkerFragment;
import com.livewire.ui.fragments.NearByWorkerFragment;
import com.livewire.ui.fragments.NotificationWorkerFragment;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;

public class WorkerMainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    ActivityWorkerMainBinding binding;
    private TextView tvHeading;
    private int clickId;
    private android.support.v4.app.FragmentManager fm;
    private boolean doubleBackToExitPressedOnce = false;
    private Runnable runnable;
    private ImageView ivNotification;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_worker_main);


        intializeViews();
        checkAvailability();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        binding.btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {// user available
                    availablityUser("1");
                } else {// user unavailable
                    availablityUser("0");
                }
            }
        });
    }

    public void checkAvailability() {
        if (PreferenceConnector.readString(this,PreferenceConnector.AVAILABILITY_1,"").equals("1")){
            binding.btnSwitch.setChecked(true);
        }else {
            binding.btnSwitch.setChecked(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAvailability();
    }


    private void availablityUser(final String s) {
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "user/availability")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("availability", s)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {

                            if (s.equals("1")){
                                PreferenceConnector.writeString(WorkerMainActivity.this,PreferenceConnector.AVAILABILITY_1,"1");
                            }else PreferenceConnector.writeString(WorkerMainActivity.this,PreferenceConnector.AVAILABILITY_1,"0");
                            // Toast.makeText(WorkerMainActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {

                            Constant.snackBar(binding.mainLayout, message);
                        }
                    } catch (JSONException e) {
                        Log.d("Exception", e.getMessage());
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Log.d("EXception", anError.getErrorDetail());
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void intializeViews() {
        fm = getSupportFragmentManager();
        binding.btnLogout.setOnClickListener(this);
        binding.llNearBy.setOnClickListener(this);
        binding.homeLl.setOnClickListener(this);
        binding.llNotificaton.setOnClickListener(this);
        binding.chatLl.setOnClickListener(this);
        binding.myJobLl.setOnClickListener(this);
        binding.ivSetting.setOnClickListener(this);
        binding.ivProfile.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        if (getIntent().getStringExtra("opponentChatId") != null) {
            String oponnetId = getIntent().getStringExtra("opponentChatId");
            String name = getIntent().getStringExtra("titleName");
            Intent intent = new Intent(this, ChattingActivity.class);
            intent.putExtra("otherUID", oponnetId);
            intent.putExtra("titleName", name);
            intent.putExtra("profilePic", "");
            startActivity(intent);
            binding.tvHeading.setText(R.string.work_opportunity);
            replaceFragment(new JobRequestFragment(), false, R.id.fl_container); // first time replace home fragment
            clickId = R.id.home_ll;
        } else if (getIntent().getStringExtra("MyProfile") != null) {
            binding.tvHeading.setText(R.string.work_opportunity);
            replaceFragment(new JobRequestFragment(), false, R.id.fl_container); // first time replace home fragment
            clickId = R.id.home_ll;

            showAlertWorkerDialog();

        } else if (getIntent().getStringExtra("workerMyProfile") != null){
            binding.tvHeading.setText(R.string.work_opportunity);
            replaceFragment(new JobRequestFragment(), false, R.id.fl_container); // first time replace home fragment
            clickId = R.id.home_ll;
            Intent intent = new Intent(this,MyProfileWorkerActivity.class);
            startActivity(intent);
        } else {
            binding.tvHeading.setText(R.string.work_opportunity);
            replaceFragment(new JobRequestFragment(), false, R.id.fl_container); // first time replace home fragment
            clickId = R.id.home_ll;
        }

        Log.e( "inti user mode: ",PreferenceConnector.readString(this,PreferenceConnector.USER_MODE,"") );
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
                    binding.tvHeading.setText(R.string.my_work);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivMyJobs.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorBlack)));
                    }
                    binding.ivMyjobDot.setVisibility(View.VISIBLE);
                    replaceFragment(new MyJobWorkerFragment(), false, R.id.fl_container);
                    clickId = R.id.my_job_ll;
                }
                break;
            case R.id.ll_near_by:
                if (clickId != R.id.ll_near_by) {
                    inActiveTab();
                    binding.tvHeading.setText(R.string.near_you);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivNearBy.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorBlack)));
                    }
                    binding.ivNearDot.setVisibility(View.VISIBLE);
                    replaceFragment(new NearByWorkerFragment(), false, R.id.fl_container);
                    clickId = R.id.ll_near_by;
                }
                break;

            case R.id.home_ll:
                if (clickId != R.id.home_ll) {
                    inActiveTab();
                    binding.tvHeading.setText(R.string.work_opportunity);
                    replaceFragment(new JobRequestFragment(), false, R.id.fl_container);
                    clickId = R.id.home_ll;
                    binding.ivHomeDot.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.chat_ll:
                if (clickId != R.id.chat_ll) {
                    inActiveTab();
                    binding.tvHeading.setText(R.string.chat);
                    binding.tvHeading.setAllCaps(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivChat.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorBlack)));
                    }
                    binding.ivChatDot.setVisibility(View.VISIBLE);
                    replaceFragment(new ChatWorkerFragment(), false, R.id.fl_container);
                    clickId = R.id.chat_ll;
                }
                break;

            case R.id.ll_notificaton:
                if (clickId != R.id.ll_notificaton) {
                    inActiveTab();
                    binding.tvHeading.setText(R.string.notifications);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivNotification.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorBlack)));
                    }
                    binding.ivNotificationDot.setVisibility(View.VISIBLE);
                    replaceFragment(new NotificationWorkerFragment(), false, R.id.fl_container);
                    clickId = R.id.ll_notificaton;
                }
                break;
          /*  case R.id.iv_notification: {
                intent = new Intent(this, NotificationListWorkerActivity.class);
                startActivity(intent);
            }*/
            //break;
            case R.id.iv_profile: {
                intent = new Intent(this, MyProfileWorkerActivity.class);
                startActivity(intent);
            }
            break;
            default:
        }
    }

    private void inActiveTab() {
        binding.actionBar.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.ivMyJobs.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            binding.ivNearBy.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            binding.ivChat.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            binding.ivNotification.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));

            binding.ivMyjobDot.setVisibility(View.INVISIBLE);
            binding.ivNearDot.setVisibility(View.INVISIBLE);
            binding.ivHomeDot.setVisibility(View.INVISIBLE);
            binding.ivChatDot.setVisibility(View.INVISIBLE);
            binding.ivNotificationDot.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void showAlertWorkerDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(WorkerMainActivity.this);
        builder1.setTitle("Alert");
        builder1.setMessage("You Need To Switch Your Profile");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(WorkerMainActivity.this, MyProfileWorkerActivity.class);
                        startActivity(intent);
                        /*Intent intent = new Intent(this, WorkerMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("MyProfile", "MyProfile");
                        intent.putExtra(USER_ID, userId);
                        intent.putExtra("body", message);
                        intent.putExtra(CONSTANTTYPE, type);
                        pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(tittle, message, pendingIntent);*/
                        dialog.cancel();

                    }
                });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        if (WorkerMainActivity.this != null)
            alert11.show();
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
