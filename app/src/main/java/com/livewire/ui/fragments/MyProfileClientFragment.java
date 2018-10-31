package com.livewire.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.FragmentMyProfileClientBinding;
import com.livewire.responce.SignUpResponce;
import com.livewire.ui.activity.EditProfileClientActivity;
import com.livewire.ui.activity.SettingActivity;
import com.livewire.ui.activity.UserSelectionActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.loopeer.shadow.ShadowView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;


public class MyProfileClientFragment extends Fragment implements View.OnClickListener {
    FragmentMyProfileClientBinding binding;
    private Context mContext;
    private ProgressDialog progressDialog;

    public MyProfileClientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile_client, container, false);
        return binding.getRoot();
        //return inflater.inflate(R.layout.fragment_my_profile_client, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actionBarIntialize(view);
        progressDialog =  new ProgressDialog(mContext);
        myProfileApi();
        binding.btnLogout.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

    }

    private void actionBarIntialize(View view) {
        View actionBar = view.findViewById(R.id.action_bar1);
        TextView header = actionBar.findViewById(R.id.tv_live_wire);
        header.setText(R.string.my_profile);
        header.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
        ImageView ivFilter = actionBar.findViewById(R.id.iv_filter);
        ShadowView svActionBar = actionBar.findViewById(R.id.sv_action_bar);
        svActionBar.setShadowMarginBottom(-3);
        ivFilter.setVisibility(View.GONE);
        ImageView ivSetting = actionBar.findViewById(R.id.iv_setting);
        ivSetting.setVisibility(View.VISIBLE);
        ivSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_logout:
                PreferenceConnector.clear(mContext);
                intent = new Intent(mContext, UserSelectionActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.iv_setting:
                intent = new Intent(mContext, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_edit:
                intent = new Intent(mContext,EditProfileClientActivity.class);
                startActivity(intent);
                break;
            default:
        }
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
}
