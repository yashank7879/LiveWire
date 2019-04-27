package com.livewire.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.ShowSkillsAdapter;
import com.livewire.databinding.ActivityMyProfileClientBinding;
import com.livewire.responce.MyProfileResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.loopeer.shadow.ShadowView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.CHANGE_USER_MODE_API;
import static com.livewire.utils.ApiCollection.CONFIRMATION_DATE_OF_BIRTH_API;
import static com.livewire.utils.ApiCollection.GET_MY_USER_PROFILE_API;

public class MyProfileClientActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMyProfileClientBinding binding;
    private ProgressDialog progressDialog;
    private MyProfileResponce userResponce;
    private ArrayList<MyProfileResponce.DataBean.CategoryBean> showSkillBeans = new ArrayList<>();
    private ShowSkillsAdapter showSkillsAdapter;
    private String videoUrl;
    private String fromSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile_client);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvSkillData.setLayoutManager(layoutManager);
        showSkillsAdapter = new ShowSkillsAdapter(this, showSkillBeans);
        binding.rvSkillData.setAdapter(showSkillsAdapter);
        actionBarIntialize();
        if (getIntent().getStringExtra("SignUp") != null){
             fromSignUp = getIntent().getStringExtra("SignUp");
            binding.actionBar1.ivBack.setVisibility(View.GONE);
            binding.llHirer.setOnClickListener(this);
        }

        progressDialog = new ProgressDialog(this);
        binding.btnLogout.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.rlVideoImg.setOnClickListener(this);
       // binding.completed.setOnClickListener(this);
        // binding.cvCompleteJob.setOnClickListener(this);
        binding.ivProfile.setOnClickListener(this);
        binding.rlRatingBar.setOnClickListener(this);
        binding.llWorker.setOnClickListener(this);
        //PreferenceConnector.writeString(this, PreferenceConnector.USER_DOB, "");
        PreferenceConnector.writeString(this, PreferenceConnector.SELECTED_VIDEO, "");
        PreferenceConnector.writeString(this, PreferenceConnector.ABOUT_ME, "");

        myProfileApi();
    }

    private void actionBarIntialize() {
        View actionBar = findViewById(R.id.action_bar1);
        TextView header = actionBar.findViewById(R.id.tv_live_wire);
        header.setText(R.string.my_profile);
        header.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        ImageView ivFilter = actionBar.findViewById(R.id.iv_filter);
        ImageView ivBack = actionBar.findViewById(R.id.iv_back);
        ShadowView svActionBar = actionBar.findViewById(R.id.sv_action_bar);
        svActionBar.setShadowMarginBottom(-3);
        ImageView ivSetting = actionBar.findViewById(R.id.iv_setting);

        ivSetting.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        ivFilter.setVisibility(View.GONE);

        ivSetting.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        myProfileApi();
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

            case R.id.iv_setting:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_edit:
                if (Constant.isNetworkAvailable(this, binding.svProfile)) {
                    intent = new Intent(this, EditProfileClientActivity.class);
                    intent.putExtra("ClientProfileInfo", userResponce);
                    startActivity(intent);
                }
                break;

            case R.id.rl_rating_bar:
                intent = new Intent(this, ReviewListActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_back:
                onBackPressed();

                break;
            case R.id.completed:
                intent = new Intent(this, CompletedJobClientActivity.class);
                startActivity(intent);
                break;

            case R.id.rl_video_img:
                if (Constant.isNetworkAvailable(this, binding.svProfile)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.parse(videoUrl), "video/mp4");
                    startActivity(i);
                  /*  intent = new Intent(mContext, PlayVideoActivity.class);
                    intent.putExtra("VideoUrlKey", videoUrl);
                    startActivity(intent);*/
                }
                break;

            case R.id.ll_worker: {
                String dob = PreferenceConnector.readString(this, PreferenceConnector.USER_DOB, "");
                if (dob.equals("0")) {
                    ageAlertDiaog();
                } else {
                    ChangeModeApi();
                }
                /* if (PreferenceConnector.readString(this, PreferenceConnector.COMPLETE_PROFILE_STATUS, "").equals("0")) {
                    startActivity(new Intent(this, AddYourSkillsActivity.class));
                } else {
                    ChangeModeApi();
                }*/
            }
            break;
            case R.id.ll_hirer: {
                binding.llWorker.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
                binding.tvWorker.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.ivWorker.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorDarkGray)));
                }
                binding.llHirer.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGreen));
                binding.tvHirer.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.ivClient.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorWhite)));
                }

               if (fromSignUp.equals("SignUp")) {
                   finishAffinity();
                    intent = new Intent(MyProfileClientActivity.this, ClientMainActivity.class);
                    startActivity(intent);
                    finish();

                }
                break;
            }
            default:
        }
    }


    private void ChangeModeApi() {
        if (Constant.isNetworkAvailable(this, binding.svProfile)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + CHANGE_USER_MODE_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("user_mode", PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, ""))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progressDialog.dismiss();
                                String status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {

                                    if (PreferenceConnector.readString(MyProfileClientActivity.this, PreferenceConnector.USER_MODE, "").equals("worker")) { //switch worker to clint
                                        PreferenceConnector.writeString(MyProfileClientActivity.this, PreferenceConnector.USER_MODE, "client");
                                        finishAffinity();
                                        Intent intent = new Intent(MyProfileClientActivity.this, ClientMainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {//switch client to worker
                                        PreferenceConnector.writeString(MyProfileClientActivity.this, PreferenceConnector.USER_MODE, "worker");
                                        if (PreferenceConnector.readString(MyProfileClientActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, "").equals("0")) {
                                            startActivity(new Intent(MyProfileClientActivity.this, AddYourSkillsActivity.class));
                                        } else {
                                            finishAffinity();
                                            Intent intent = new Intent(MyProfileClientActivity.this, WorkerMainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                } else {
                                    Constant.snackBar(binding.svProfile, message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Constant.errorHandle(anError, MyProfileClientActivity.this);
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    //"""""""""' my profile worker side""""""""""""""//
    private void myProfileApi() {// help offer api calling
        if (Constant.isNetworkAvailable(this, binding.svProfile)){
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + GET_MY_USER_PROFILE_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progressDialog.dismiss();
                                String status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                    userResponce = new Gson().fromJson(String.valueOf(response), MyProfileResponce.class);
                                    videoUrl = userResponce.getData().getIntro_video();

                                    if (!videoUrl.isEmpty()) {
                                        binding.rlVideoImg.setVisibility(View.VISIBLE);
                                    }
                                    if (!userResponce.getData().getVideo_thumb().isEmpty()) {
                                        Picasso.with(binding.videoThumbImg.getContext()).load(userResponce.getData().getVideo_thumb()).fit()
                                                .error(R.color.colorWhite)
                                                .into(binding.videoThumbImg);
                                    }

                                    if (!userResponce.getData().getRating().isEmpty()) {
                                        binding.ratingBar.setRating(Float.parseFloat(userResponce.getData().getRating()));
                                    }
                                    showSkillBeans.clear();
                                    showSkillBeans.addAll(userResponce.getData().getCategory());
                                    showSkillsAdapter.notifyDataSetChanged();

                                    if (!userResponce.getData().getProfileImage().isEmpty()) {
                                        Picasso.with(binding.ivProfile.getContext())
                                                .load(userResponce.getData().getProfileImage())
                                                .fit().into(binding.ivProfile);
                                    }
                                    binding.ivPlaceholder.setVisibility(View.GONE);
                                    binding.setUserResponce(userResponce.getData());
                                } else {
                                    Constant.snackBar(binding.svProfile, message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Constant.errorHandle(anError, MyProfileClientActivity.this);
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void ageAlertDiaog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyProfileClientActivity.this);
        builder1.setTitle("Alert");
        builder1.setMessage("Are you over 40 Years?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        binding.llWorker.setBackgroundColor(ContextCompat.getColor(MyProfileClientActivity.this, R.color.colorDarkBlack));
                        binding.tvWorker.setTextColor(ContextCompat.getColor(MyProfileClientActivity.this, R.color.colorWhite));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            binding.ivWorker.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(MyProfileClientActivity.this, R.color.colorWhite)));
                        }
                        binding.llHirer.setBackgroundColor(ContextCompat.getColor(MyProfileClientActivity.this, R.color.colorWhite));
                        binding.tvHirer.setTextColor(ContextCompat.getColor(MyProfileClientActivity.this, R.color.colorDarkGray));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            binding.ivClient.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(MyProfileClientActivity.this, R.color.colorDarkGray)));
                        }

                        ageLimitApi();
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
        if (MyProfileClientActivity.this != null)
            alert11.show();
    }

    private void ageLimitApi() {
        if (Constant.isNetworkAvailable(this, binding.svProfile)) {
            AndroidNetworking.post(BASE_URL + CONFIRMATION_DATE_OF_BIRTH_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            String status = null;
                            try {
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                    PreferenceConnector.writeString(MyProfileClientActivity.this, PreferenceConnector.USER_DOB, "1");
                                    ChangeModeApi();
                                } else {
                                    Constant.snackBar(binding.svProfile, message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
