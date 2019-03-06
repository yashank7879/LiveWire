package com.livewire.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.livewire.databinding.ActivityWorkerProfileDetailClientActivityBinding;
import com.livewire.responce.MyProfileResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.livewire.utils.ApiCollection.BASE_URL;


/**
 * Created by mindiii on 11/29/18.
 */

public class WorkerProfileDetailClientActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityWorkerProfileDetailClientActivityBinding binding;
    private ProgressDialog progressDialog;
    private String userId = "";
    private MyProfileResponce userResponce;
    private ShowSkillsAdapter showSkillsAdapter;
    private ArrayList<MyProfileResponce.DataBean.CategoryBean> showSkillBeans = new ArrayList<>();
    private String videoUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_worker_profile_detail_client_activity);
        progressDialog = new ProgressDialog(this);

        if (getIntent().getStringExtra("UserIdKey") != null) {
            userId = getIntent().getStringExtra("UserIdKey");
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvSkillData.setLayoutManager(layoutManager);
        showSkillsAdapter = new ShowSkillsAdapter(this, showSkillBeans);
        binding.rvSkillData.setAdapter(showSkillsAdapter);
        binding.rlVideoImg.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);

        profileDetail();
    }


    //"""""""""' my profile worker side""""""""""""""//
    private void profileDetail() {// help offer api calling
        if (Constant.isNetworkAvailable(this, binding.svProfile)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "user/getUserDetail")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("user_id", userId)
                    .addBodyParameter("user_type", PreferenceConnector.readString(this, PreferenceConnector.USER_TYPE, ""))
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
                                  /*  for (MyProfileResponce.DataBean.CategoryBean categoryBean : userResponce.getData().getCategory()) {

                                        CategoryBean catgoryData = new CategoryBean();
                                        catgoryData.setCategoryName(categoryBean.getCategoryName());
                                        catgoryData.setParent_id(categoryBean.getParent_id());
                                        catgoryData.setParentCategoryId(categoryBean.getParentCategoryId());

                                        for (int i = 0; i < categoryBean.getSubcat().size(); i++) {
                                            CategoryBean.SubcatBean subcatBean = new CategoryBean.SubcatBean();
                                            subcatBean.setCategoryId(categoryBean.getSubcat().get(i).getCategoryId());
                                            subcatBean.setCategoryName(categoryBean.getSubcat().get(i).getCategoryName());
                                            subcatBean.setParent_id(categoryBean.getSubcat().get(i).getParent_id());
                                            subcatBean.setMax_rate(categoryBean.getSubcat().get(i).getMax_rate());
                                            subcatBean.setMin_rate(categoryBean.getSubcat().get(i).getMin_rate());
                                            subcatBeanList.add(subcatBean);
                                            catgoryData.setSubcat(subcatBeanList);

                                        }

                                    }*/
                                    showSkillBeans.clear();

                                    if (userResponce.getData().getCategory().size()>0) {
                                        showSkillBeans.addAll(userResponce.getData().getCategory());

                                    }else {
                                        binding.rlSkills.setVisibility(View.GONE);
                                        binding.tvNoSkills.setVisibility(View.VISIBLE);
                                    }
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
                            Constant.errorHandle(anError, WorkerProfileDetailClientActivity.this);
                            progressDialog.dismiss();
                        }
                    });

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_video_img:
                if (Constant.isNetworkAvailable(this, binding.svProfile)) {
                 /*   Intent intent = new Intent(this, PlayVideoActivity.class);
                    intent.putExtra("VideoUrlKey", videoUrl);
                    startActivity(intent);*/
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.parse(videoUrl), "video/mp4");
                    startActivity(i);
                }
                break;

            case R.id.iv_back:
                onBackPressed();
                break;
            default:
        }
    }
}
