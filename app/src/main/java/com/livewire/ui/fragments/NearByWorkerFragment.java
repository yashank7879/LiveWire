package com.livewire.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.NearByAdapterWorker;
import com.livewire.databinding.FragmentNearByClientBinding;
import com.livewire.responce.NearByResponce;
import com.livewire.ui.activity.ClientProfileDetailWorkerActivity;
import com.livewire.ui.activity.EditProfileClientActivity;
import com.livewire.ui.activity.EditProfileWorkerActivity;
import com.livewire.ui.activity.MyProfileClientActivity;
import com.livewire.ui.activity.WorkerProfileDetailClientActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.NEAR_BY_USER_API;


public class NearByWorkerFragment extends Fragment implements View.OnClickListener, NearByAdapterWorker.ItemonClickListener {
    private FragmentNearByClientBinding binding;
    private Context mContext;
    private NearByAdapterWorker adapter;
    private ProgressDialog progressDialog;
    private List<NearByResponce.DataBean> nearByList;

    public NearByWorkerFragment() {
     //"""""   Required empty public constructor  """""""""//
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_near_by_client, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actionBarIntialize(view);
        nearByList = new ArrayList<>();
        progressDialog = new ProgressDialog(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        binding.rvNearYou.setLayoutManager(layoutManager);
        adapter = new NearByAdapterWorker(mContext, nearByList,this);
        binding.rvNearYou.setAdapter(adapter);
        binding.rvNearYou.addItemDecoration(new
                DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL));
        binding.btnUpdate.setOnClickListener(this);
        getNearByListApi();
    }

    private void getNearByListApi() {
        if (Constant.isNetworkAvailable(mContext, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + NEAR_BY_USER_API)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            //    mainLayout.startAnimation(mLoadAnimation);
                            String status = null;
                            try {
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                    if (message.equals("Please update your location to know Live Wires near you.")){
                                        binding.rlUpdateProfile.setVisibility(View.VISIBLE);
                                        binding.tvNoData.setVisibility(View.GONE);
                                    }else if (message.equals("No near by user found")){
                                        binding.tvNoData.setVisibility(View.VISIBLE);
                                        binding.rlUpdateProfile.setVisibility(View.GONE);
                                    }else {
                                        //Constant.snackBar(binding.mainLayout, message);
                                        NearByResponce nearByResponce = new Gson().fromJson(String.valueOf(response), NearByResponce.class);
                                        binding.tvNoData.setVisibility(View.GONE);
                                        nearByList.addAll(nearByResponce.getData());
                                        adapter.notifyDataSetChanged();
                                    }

                                } else {
                                    adapter.notifyDataSetChanged();
                                    Constant.snackBar(binding.mainLayout, message);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            progressDialog.dismiss();
                            Constant.errorHandle(anError, getActivity());
                        }
                    });
        }
    }

    private void actionBarIntialize(View view) {
        View actionBar = view.findViewById(R.id.action_bar1);
        actionBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClickClient(String clientId) {
        Intent intent = new Intent(mContext, ClientProfileDetailWorkerActivity.class);
        intent.putExtra("UserIdKey",clientId);
        mContext.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                startActivity(new Intent(mContext, EditProfileWorkerActivity.class));
                break;
            default:
        }
    }
}
