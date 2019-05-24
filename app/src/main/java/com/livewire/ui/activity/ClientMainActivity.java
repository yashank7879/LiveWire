package com.livewire.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.CompoundButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.livewire.R;
import com.livewire.databinding.ActivityClientMainBinding;
import com.livewire.model.Chat;
import com.livewire.ui.activity.chat.ChattingActivity;
import com.livewire.ui.fragments.ChatClientFragment;
import com.livewire.ui.fragments.MyJobClientFragment;
import com.livewire.ui.fragments.NearByClientFragment;
import com.livewire.ui.fragments.notification_client_tab.NotificationClientBaseFragment;
import com.livewire.ui.fragments.PostJobHomeFragment;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.livewire.utils.ApiCollection.BASE_URL;

public class ClientMainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    ActivityClientMainBinding binding;
    private int clickId;
    private android.support.v4.app.FragmentManager fm;
    private boolean doubleBackToExitPressedOnce = false;
    private Runnable runnable;
    private Map<String, Integer> isMsgFoundMap;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client_main);
        intializeViews();
        actionBarIntialize();
    }

    private void intializeViews() {
        fm = getSupportFragmentManager();
        progressDialog = new ProgressDialog(this);
        Log.e("dob date: ", PreferenceConnector.readString(this, PreferenceConnector.USER_DOB, ""));
        binding.myJobLl.setOnClickListener(this);
        binding.notificationLl.setOnClickListener(this);
        binding.addLl.setOnClickListener(this);
        binding.chatLl.setOnClickListener(this);
        binding.nearyouLl.setOnClickListener(this);

        binding.btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {//// user available
                    availablityUser("1");
                } else {// user unavailable
                    availablityUser("0");
                }
            }
        });

        // binding.actionBar.setVisibility(View.VISIBLE);

        isMsgFoundMap = new HashMap<>();
        //get intent from NearYouClientActivity || from After completing payment
        if (getIntent().getStringExtra("NearYouKey") != null) {
            replaceFragment(new MyJobClientFragment(), false, R.id.fl_container); // first time replace home fragment
            inActiveTab();
            binding.tvHeading.setText(R.string.my_livewire_post);
            // binding.actionBar.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.ivMyJob.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
            }
            clickId = R.id.my_job_ll;
        } else if (getIntent().getStringExtra("opponentChatId") != null) {
            String oponnetId = getIntent().getStringExtra("opponentChatId");
            String name = getIntent().getStringExtra("titleName");
            Intent intent = new Intent(this, ChattingActivity.class);
            intent.putExtra("otherUID", oponnetId);
            intent.putExtra("titleName", name);
            intent.putExtra("profilePic", "");
            startActivity(intent);
            binding.tvHeading.setText(R.string.creat_project);
            replaceFragment(new PostJobHomeFragment(), false, R.id.fl_container); // first time replace home fragment
            clickId = R.id.add_ll;
        } else if (getIntent().getStringExtra("MyProfile") != null) {
            binding.tvHeading.setText(R.string.creat_project);
            replaceFragment(new PostJobHomeFragment(), false, R.id.fl_container); // first time replace home fragment
            clickId = R.id.add_ll;
            showAlertWorkerDialog();
        }else if (getIntent().getStringExtra("ClientMyProfile") != null){// come from the login or signup
            binding.tvHeading.setText(R.string.creat_project);
            replaceFragment(new PostJobHomeFragment(), false, R.id.fl_container); // first time replace home fragment
            clickId = R.id.add_ll;
            Intent intent = new Intent(this, MyProfileClientActivity.class);
            startActivity(intent);
        } else {
            binding.tvHeading.setText(R.string.creat_project);
            replaceFragment(new PostJobHomeFragment(), false, R.id.fl_container); // first time replace home fragment
            clickId = R.id.add_ll;
        }
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            String myId = PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, "");
            checkUREADmsg(myId);
        }
    }

    public void showAlertWorkerDialog() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(ClientMainActivity.this);
        builder1.setTitle("Alert");
        builder1.setMessage(R.string.please_switch_user_mode_to_do_this);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ClientMainActivity.this, MyProfileClientActivity.class);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        if (ClientMainActivity.this != null)
            alert11.show();
    }

    private void actionBarIntialize() {
        binding.ivFilter.setVisibility(View.GONE);
        binding.ivProfile.setVisibility(View.VISIBLE);
        binding.ivProfile.setOnClickListener(this);
        binding.ivFilter.setOnClickListener(this);
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

                            if (s.equals("1")) {
                                PreferenceConnector.writeString(ClientMainActivity.this, PreferenceConnector.AVAILABILITY_1, "1");
                            } else
                                PreferenceConnector.writeString(ClientMainActivity.this, PreferenceConnector.AVAILABILITY_1, "0");
                            // Toast.makeText(WorkerMainActivity.this, message, Toast.LENGTH_SHORT).show();
                            changeAvailabilityOnFirebase(s);
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


    private void checkUREADmsg(String myId) {
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_HISTORY).child(myId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    if (dataSnapshot.getValue(Chat.class) != null) {
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        if (chat.unreadCount > 0) {
                            isMsgFoundMap.put(dataSnapshot.getKey(), chat.unreadCount);
                            binding.ivUnreadMsgTab.setVisibility(View.VISIBLE);
                        /*if (isMsgFoundMap.containsValue(myUserId)) {
                            iv_unread_msg_tab.setVisibility(View.VISIBLE);
                            return;
                        } else iv_unread_msg_tab.setVisibility(View.GONE);*/
                        } else binding.ivUnreadMsgTab.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.d("ex", e.getLocalizedMessage() + "");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue(Chat.class) != null) {
                    int unreadCount = dataSnapshot.getValue(Chat.class).unreadCount;
                    if (unreadCount > 0) {
                        isMsgFoundMap.put(dataSnapshot.getKey(), unreadCount);
                        binding.ivUnreadMsgTab.setVisibility(View.VISIBLE);

                       /* if (isMsgFoundMap.containsValue(myUserId)) {
                            iv_unread_msg_tab.setVisibility(View.VISIBLE);
                            return;
                        } else iv_unread_msg_tab.setVisibility(View.GONE);*/
                    } else binding.ivUnreadMsgTab.setVisibility(View.GONE);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Chat.class) != null) {
                    int unreadCount = dataSnapshot.getValue(Chat.class).unreadCount;
                    if (unreadCount > 0) {
                        isMsgFoundMap.put(dataSnapshot.getKey(), unreadCount);
                        binding.ivUnreadMsgTab.setVisibility(View.VISIBLE);

                       /* if (isMsgFoundMap.containsValue(myUserId)) {
                            iv_unread_msg_tab.setVisibility(View.VISIBLE);
                            return;
                        } else iv_unread_msg_tab.setVisibility(View.GONE);*/
                    } else binding.ivUnreadMsgTab.setVisibility(View.GONE);

                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile: {
                Intent intent = new Intent(this, MyProfileClientActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.my_job_ll:
                if (clickId != R.id.my_job_ll) {
                    inActiveTab();
                    binding.actionBar.setVisibility(View.VISIBLE);
                    binding.tvHeading.setText(R.string.my_livewire_post);
                    binding.tvHeading.setAllCaps(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivMyJob.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    binding.ivMyjobDot.setVisibility(View.VISIBLE);
//                    tvLiveWire.setText("MY JOBS");
                    //  tvLiveWire.setTextColor(ContextCompat.getColor(this,R.color.colorGreen));
                    replaceFragment(new MyJobClientFragment(), false, R.id.fl_container); // first time replace home fragment
                    clickId = R.id.my_job_ll;
                }
                break;
            case R.id.notification_ll:
                if (clickId != R.id.notification_ll) {
                    inActiveTab();
                    binding.actionBar.setVisibility(View.VISIBLE);
                    binding.tvHeading.setText(R.string.notifications);
                    binding.tvHeading.setAllCaps(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivNotification.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    binding.ivNotificatonDot.setVisibility(View.VISIBLE);
                   // replaceFragment(new NotificationClientFragment(), false, R.id.fl_container);
                    replaceFragment(new NotificationClientBaseFragment(), false, R.id.fl_container);
                    clickId = R.id.notification_ll;
                }
                break;

            case R.id.add_ll:
                //  tvLiveWire.setText(liveWireText(this));
                if (clickId != R.id.add_ll) {
                    binding.actionBar.setVisibility(View.VISIBLE);
                    binding.tvHeading.setText(R.string.creat_project);
                    replaceFragment(new PostJobHomeFragment(), false, R.id.fl_container);
                    inActiveTab();
                    binding.ivHomeDot.setVisibility(View.VISIBLE);
                    clickId = R.id.add_ll;
                }
                break;

            case R.id.chat_ll:
                if (clickId != R.id.chat_ll) {
                    inActiveTab();
                    binding.actionBar.setVisibility(View.VISIBLE);
                    binding.tvHeading.setText(R.string.chat);
                    binding.tvHeading.setAllCaps(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivChat.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    binding.ivChatDot.setVisibility(View.VISIBLE);
                    replaceFragment(new ChatClientFragment(), false, R.id.fl_container);
                    clickId = R.id.chat_ll;
                }
                break;

            case R.id.nearyou_ll:
                if (clickId != R.id.nearyou_ll) {
                    inActiveTab();
                    binding.actionBar.setVisibility(View.VISIBLE);
                    binding.tvHeading.setText(R.string.near_you);
                    binding.tvHeading.setAllCaps(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivNearYou.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGreen)));
                    }
                    binding.ivNearyouDot.setVisibility(View.VISIBLE);
                    replaceFragment(new NearByClientFragment(), false, R.id.fl_container);
                    clickId = R.id.nearyou_ll;
                }
                break;

            default:
        }
    }

    private void inActiveTab() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.ivMyJob.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            binding.ivChat.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            binding.ivNearYou.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));
            binding.ivNotification.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorLightGray)));

            binding.ivMyjobDot.setVisibility(View.INVISIBLE);
            binding.ivNearyouDot.setVisibility(View.INVISIBLE);
            binding.ivHomeDot.setVisibility(View.INVISIBLE);
            binding.ivChatDot.setVisibility(View.INVISIBLE);
            binding.ivNotificatonDot.setVisibility(View.INVISIBLE);
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

    private void changeAvailabilityOnFirebase(String avalaible) {
        String myId = PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, "");
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(myId).child("availability").setValue(avalaible);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}