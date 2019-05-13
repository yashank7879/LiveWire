package com.livewire.ui.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.NotificationAdapter;
import com.livewire.databinding.FragmentNotificationWorkerClientBinding;
import com.livewire.pagination.EndlessRecyclerViewScrollListener;
import com.livewire.responce.NotificationResponce;
import com.livewire.ui.activity.MyProfileClientActivity;
import com.livewire.ui.activity.MyProfileWorkerActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.NOTIFICATION_LIST_API;

public class NotificationWorkerClientFragment extends Fragment implements NotificationAdapter.NotificationOnItemClickListener{
    FragmentNotificationWorkerClientBinding binding;
    private ProgressDialog progressDialog;
    private Context mContext;
    private List<NotificationResponce.DataBean> notifiList = new ArrayList<>();
    private NotificationResponce notificationResponse;
    private NotificationAdapter notificationAdapter;
    private String userNotification="";
    private int limit = 5;
    private int start = 0;

    public NotificationWorkerClientFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_worker_client, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        //binding.ivBack.setOnClickListener(this);
        binding.recyclerView.setLayoutManager(layoutManager);
         notificationAdapter = new NotificationAdapter(mContext, notifiList, this);
        binding.recyclerView.setAdapter(notificationAdapter);
        binding.recyclerView.addItemDecoration(new
                DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL));

        //""""" check for user type and then according to notification  """"""//
        String userType = PreferenceConnector.readString(mContext,PreferenceConnector.USER_MODE,"");
        userNotification = userType.equalsIgnoreCase(Constant.CLIENT) ? Constant.WORKER : Constant.CLIENT;

        //******  Pagination """""""""""""""//
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                limit = limit + 5; //load 5 items in recyclerview
                if (Constant.isNetworkAvailable(mContext, binding.mainLayout)) {
                    // progressDialog.show();
                    notificationListApi();
                }
            }
        };
        binding.recyclerView.addOnScrollListener(scrollListener);


        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { //swipe to refresh rcyclerview data
                binding.swipeRefresh.setRefreshing(false);
                if (Constant.isNetworkAvailable(mContext, binding.mainLayout)) {
                    notificationListApi();
                }
            }
        });

        notificationListApi();
    }


    // """""""at worker side in notification list shows to the hirer side notification """"""""//
    // if click on notification open switch profile alert dialog """"""//
    private void notificationListApi() {
        if (Constant.isNetworkAvailable(mContext, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + NOTIFICATION_LIST_API)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("user_type",userNotification)
                    .addBodyParameter("limit", "" + limit)
                    .addBodyParameter("start", "" + start)
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
                                    notifiList.clear();
                                    notificationResponse = new Gson().fromJson(String.valueOf(response), NotificationResponce.class);
                                    binding.tvNoData.setVisibility(View.GONE);
                                    String currentTime = response.getString("currentDateTime");
                                    notificationAdapter.getCurrentTime(currentTime);
                                    notifiList.addAll(notificationResponse.getData());
                                    notificationAdapter.notifyDataSetChanged();

                                } else {
                                    notifiList.clear();
                                    notificationAdapter.notifyDataSetChanged();
                                    if (notifiList.size() == 0) {
                                        binding.tvNoData.setVisibility(View.VISIBLE);
                                    } else Constant.snackBar(binding.mainLayout, message);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void notificationOnClick(NotificationResponce.DataBean dataBean) {
        showAlertWorkerDialog();
    }

    public void showAlertWorkerDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setTitle("Alert");
        builder1.setMessage(R.string.please_switch_user_mode_to_do_this);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (PreferenceConnector.readString(mContext,PreferenceConnector.USER_MODE,"").equalsIgnoreCase(Constant.CLIENT)) {// open client profile
                            Intent intent = new Intent(mContext, MyProfileClientActivity.class);
                            startActivity(intent);
                        }else {// open Worker profile
                            Intent intent = new Intent(mContext, MyProfileWorkerActivity.class);
                            startActivity(intent);
                        }
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
        alert11.show();
    }
}
