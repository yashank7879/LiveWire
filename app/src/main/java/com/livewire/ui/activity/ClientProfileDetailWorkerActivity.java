package com.livewire.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivityClientProfileDetailWorkerBinding;
import com.livewire.responce.MyProfileResponce;
import com.livewire.responce.SignUpResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;

public class ClientProfileDetailWorkerActivity extends AppCompatActivity implements View.OnClickListener{
    ActivityClientProfileDetailWorkerBinding binding;
    private ProgressDialog progressDialog;
    private String userId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client_profile_detail_worker);
        progressDialog = new ProgressDialog(this);

        binding.actionBar1.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar1.ivBack.setOnClickListener(this);
        binding.actionBar1.tvLiveWire.setText(R.string.client_profile);
        binding.actionBar1.tvLiveWire.setTextColor(ContextCompat.getColor(this,R.color.colorGreen));

        if (getIntent().getStringExtra("UserIdKey") != null){
            userId = getIntent().getStringExtra("UserIdKey");
        }
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
                                    SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);


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
                            Constant.errorHandle(anError, ClientProfileDetailWorkerActivity.this);
                            progressDialog.dismiss();
                        }
                    });

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
                default:

        }
    }
}
