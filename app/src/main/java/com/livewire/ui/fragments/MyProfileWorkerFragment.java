package com.livewire.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.FragmentMyProfileWorkerBinding;
import com.livewire.responce.SignUpResponce;
import com.livewire.ui.activity.CompleteProfileActivity;
import com.livewire.ui.activity.PlayVideoActivity;
import com.livewire.ui.activity.SettingActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;


public class MyProfileWorkerFragment extends Fragment implements View.OnClickListener {
    FragmentMyProfileWorkerBinding binding;
    private Context mContext;
    private ProgressDialog progressDialog;
    private String videoUrl;

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
       binding.btnEdit.setOnClickListener(this);
       binding.ivSetting.setOnClickListener(this);
       binding.rlVideoImg.setOnClickListener(this);
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
            AndroidNetworking.get(BASE_URL + "user/getMyProfile")
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
                            SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                           videoUrl = userResponce.getData().getUser_intro_vodeo();
                            Picasso.with(binding.ivProfile.getContext()).load(userResponce.getData().getThumbImage())
                                  .fit().into(binding.ivProfile);
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
                intent = new Intent(mContext, CompleteProfileActivity.class);
                intent.putExtra("EditProfileKey","EditProfile");
                startActivity(intent);
                break;
            case R.id.rl_video_img:
                if (Constant.isNetworkAvailable(mContext,binding.svProfile)) {
                    intent = new Intent(mContext, PlayVideoActivity.class);
                    intent.putExtra("VideoUrlKey", videoUrl);
                    startActivity(intent);
                }
                break;
            case R.id.iv_setting:
                    intent = new Intent(mContext, SettingActivity.class);
                    startActivity(intent);
                break;
            default:
        }
    }
}
