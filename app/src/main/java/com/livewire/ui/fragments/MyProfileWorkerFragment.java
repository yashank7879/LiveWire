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
import com.livewire.responce.NearYouResponce;
import com.livewire.ui.activity.CompleteProfileActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;


public class MyProfileWorkerFragment extends Fragment implements View.OnClickListener {
    FragmentMyProfileWorkerBinding binding;
    private Context mContext;
    private ProgressDialog progressDialog;

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
     //   myProfileApi();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    //"""""""""' near you api at client side""""""""""""""//
    private void myProfileApi() {// help offer api calling
        if (Constant.isNetworkAvailable(mContext, binding.svProfile)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "Jobpost/getNearByWorker")
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))

                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            /*NearYouResponce helpOfferedResponce = new Gson().fromJson(String.valueOf(response), NearYouResponce.class);
                            nearYouList.addAll(helpOfferedResponce.getData());*/
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
            default:
        }
    }
}
