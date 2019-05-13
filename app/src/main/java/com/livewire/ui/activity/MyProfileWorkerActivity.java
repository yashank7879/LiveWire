package com.livewire.ui.activity;

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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.ShowSkillsAdapter;
import com.livewire.databinding.FragmentMyProfileWorkerBinding;
import com.livewire.model.CategoryBean;
import com.livewire.responce.MyProfileResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.CHANGE_USER_MODE_API;
import static com.livewire.utils.ApiCollection.GET_MY_PROFILE_API;
import static com.livewire.utils.Constant.fromProfile;

public class MyProfileWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    FragmentMyProfileWorkerBinding binding;
    private ProgressDialog progressDialog;
    private String videoUrl;
    private ArrayList<MyProfileResponce.DataBean.CategoryBean> showSkillBeans = new ArrayList<>();
    private ShowSkillsAdapter showSkillsAdapter;
    private List<CategoryBean.SubcatBean> subcatBeanList;
    private MyProfileResponce userResponce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_my_profile_worker);
        progressDialog = new ProgressDialog(this);
        subcatBeanList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvSkillData.setLayoutManager(layoutManager);
        showSkillsAdapter = new ShowSkillsAdapter(this, showSkillBeans);
        binding.rvSkillData.setAdapter(showSkillsAdapter);

        binding.ratingBar.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.ivSetting.setOnClickListener(this);
        binding.rlVideoImg.setOnClickListener(this);
        binding.ivProfile.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.rlRatingBar.setOnClickListener(this);
        binding.llHirer.setOnClickListener(this);
        binding.llWorker.setOnClickListener(this);
        fromProfile = true;
        myProfileApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myProfileApi();
    }

    //"""""""""' my profile worker side""""""""""""""//
    private void myProfileApi() {// help offer api calling
        if (Constant.isNetworkAvailable(this, binding.svProfile)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + GET_MY_PROFILE_API)
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
                                    }else binding.rlVideoImg.setVisibility(View.GONE);

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
                            Log.e("getErrorBody: ", anError.getErrorBody());
                            Constant.errorHandle(anError, MyProfileWorkerActivity.this);
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_edit:
                intent = new Intent(this, EditProfileWorkerActivity.class);
                intent.putExtra("EditProfileKey", "EditProfile");
                intent.putExtra("CategoryListKey", userResponce);
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
            case R.id.iv_setting:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            /*case R.id.iv_profile:
                intent = new Intent(mContext, ReviewListActivity.class);
                startActivity(intent);
                break;*/
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.rl_rating_bar:
                intent = new Intent(this, ReviewListActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_worker: {
                binding.llWorker.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDarkBlack));
                binding.tvWorker.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.ivWorker.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorWhite)));
                }
                binding.llHirer.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
                binding.tvHirer.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.ivClient.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorDarkGray)));
                }
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
                ChangeModeApi();
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
                                    if (PreferenceConnector.readString(MyProfileWorkerActivity.this, PreferenceConnector.USER_MODE, "").equals("worker")) {
                                        PreferenceConnector.writeString(MyProfileWorkerActivity.this, PreferenceConnector.USER_MODE, "client");
                                        finishAffinity();
                                        Intent intent = new Intent(MyProfileWorkerActivity.this, ClientMainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        PreferenceConnector.writeString(MyProfileWorkerActivity.this, PreferenceConnector.USER_MODE, "worker");
                                        finishAffinity();
                                        Intent intent = new Intent(MyProfileWorkerActivity.this, WorkerMainActivity.class);
                                        startActivity(intent);
                                        finish();
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
                            Constant.errorHandle(anError, MyProfileWorkerActivity.this);
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}
