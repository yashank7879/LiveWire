package com.livewire.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.livewire.ui.activity.AddYourSkillsActivity;
import com.livewire.ui.activity.ClientMainActivity;
import com.livewire.ui.activity.EditProfileWorkerActivity;
import com.livewire.ui.activity.MyProfileClientActivity;
import com.livewire.ui.activity.NotificationListWorkerActivity;
import com.livewire.ui.activity.PlayVideoActivity;
import com.livewire.ui.activity.ReviewListActivity;
import com.livewire.ui.activity.SettingActivity;
import com.livewire.ui.activity.WorkerMainActivity;
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


public class MyProfileWorkerFragment extends Fragment implements View.OnClickListener {
    FragmentMyProfileWorkerBinding binding;
    private Context mContext;
    private ProgressDialog progressDialog;
    private String videoUrl;
    private ArrayList<MyProfileResponce.DataBean.CategoryBean> showSkillBeans = new ArrayList<>();
    private ShowSkillsAdapter showSkillsAdapter;
    private List<CategoryBean.SubcatBean> subcatBeanList;
    private MyProfileResponce userResponce;

    public MyProfileWorkerFragment() {
     // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile_worker, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(mContext);
        subcatBeanList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        binding.rvSkillData.setLayoutManager(layoutManager);
        showSkillsAdapter = new ShowSkillsAdapter(mContext, showSkillBeans);
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

        myProfileApi();

    }

    @Override
    public void onResume() {
        super.onResume();
        myProfileApi();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    //"""""""""' my profile worker side""""""""""""""//
    private void myProfileApi() {// help offer api calling
        if (Constant.isNetworkAvailable(mContext, binding.svProfile)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + GET_MY_PROFILE_API)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
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
                            Log.e("getErrorBody: ", anError.getErrorBody());
                            Constant.errorHandle(anError, getActivity());
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
                intent = new Intent(mContext, EditProfileWorkerActivity.class);
                intent.putExtra("EditProfileKey", "EditProfile");
                intent.putExtra("CategoryListKey", userResponce);
                startActivity(intent);
                break;
            case R.id.rl_video_img:
                if (Constant.isNetworkAvailable(mContext, binding.svProfile)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.parse(videoUrl),"video/mp4");
                    startActivity(i);
                  /*  intent = new Intent(mContext, PlayVideoActivity.class);
                    intent.putExtra("VideoUrlKey", videoUrl);
                    startActivity(intent);*/
                }
                break;
            case R.id.iv_setting:
                intent = new Intent(mContext, SettingActivity.class);
                startActivity(intent);
                break;
            /*case R.id.iv_profile:
                intent = new Intent(mContext, ReviewListActivity.class);
                startActivity(intent);
                break;*/
            case R.id.iv_notification:
                intent = new Intent(mContext, NotificationListWorkerActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_rating_bar:
                intent = new Intent(mContext, ReviewListActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_worker: {
                binding.llWorker.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorDarkBlack));
                binding.tvWorker.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.ivWorker.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorWhite)));
                }
                binding.llHirer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                binding.tvHirer.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkGray));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.ivClient.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorDarkGray)));
                }



            }
            break;
            case R.id.ll_hirer: {
                binding.llWorker.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                binding.tvWorker.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkGray));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.ivWorker.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorDarkGray)));
                }
                binding.llHirer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorLightGreen));
                binding.tvHirer.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.ivClient.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorWhite)));
                }
                ChangeModeApi();
            }
            default:
        }
    }

    private void ChangeModeApi() {
        if (Constant.isNetworkAvailable(mContext, binding.svProfile)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + CHANGE_USER_MODE_API)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("user_mode", PreferenceConnector.readString(mContext, PreferenceConnector.USER_MODE, ""))
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
                                    if (PreferenceConnector.readString(mContext, PreferenceConnector.USER_MODE, "").equals("worker")) {
                                        PreferenceConnector.writeString(mContext, PreferenceConnector.USER_MODE, "client");
                                        getActivity().finishAffinity();
                                        Intent intent = new Intent(mContext, ClientMainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    } else {
                                        PreferenceConnector.writeString(mContext, PreferenceConnector.USER_MODE, "worker");
                                        getActivity().finishAffinity();
                                        Intent intent = new Intent(mContext, WorkerMainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
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
                            Constant.errorHandle(anError, getActivity());
                            progressDialog.dismiss();
                        }
                    });


        }
    }
}
