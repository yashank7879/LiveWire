package com.livewire.ui.fragments.notification_client_tab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.HelpOfferedAdapter;
import com.livewire.adapter.NotificationAdapter;
import com.livewire.databinding.FragmentChatWorkerBinding;
import com.livewire.databinding.FragmentNotificationClientBinding;
import com.livewire.pagination.EndlessRecyclerViewScrollListener;
import com.livewire.responce.NotificationResponce;
import com.livewire.responce.SubCategoryResponse;
import com.livewire.ui.activity.MyOnGoingJobDetailClientActivity;
import com.livewire.ui.activity.MyProfileClientActivity;
import com.livewire.ui.activity.MyProfileWorkerActivity;
import com.livewire.ui.activity.MySingleJobDetailClientActivity;
import com.livewire.ui.activity.WorkerMainActivity;
import com.livewire.ui.activity.notification_activity.NotificationJobHelpOfferedDetailWorkerActivity;
import com.livewire.ui.activity.notification_activity.NotificationJobOnGoingDetailWorkerActivity;
import com.livewire.ui.activity.notification_activity.NotificationMySingleJobDetailClientActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.NOTIFICATION_LIST_API;

public class NotificationClientFragment extends Fragment implements View.OnClickListener, NotificationAdapter.NotificationOnItemClickListener {
    FragmentNotificationClientBinding binding;
    private Context mContext;
    private NotificationAdapter notificationAdapter;
    private ProgressDialog progressDialog;
    private NotificationResponce notificationResponse;
    private List<NotificationResponce.DataBean> notifiList = new ArrayList<>();
    public static final String CURRENTTIME = "currentTime";
    public static final String USER_ID = "reference_id";
    public static final String CONSTANTTYPE = "type";
    private String userNotification="";
    private int limit = 5;
    private int start = 0;

    public NotificationClientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_notification_client, container, false);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        binding.recyclerView.setLayoutManager(layoutManager);
        notificationAdapter = new NotificationAdapter(mContext, notifiList, this);
        binding.recyclerView.setAdapter(notificationAdapter);
        binding.recyclerView.addItemDecoration(new
                DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL));

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

        actionBarIntialize(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void actionBarIntialize(View view) {
        View actionBar = view.findViewById(R.id.action_bar1);
        TextView header = actionBar.findViewById(R.id.tv_live_wire);

        header.setText(R.string.notifications);
        header.setAllCaps(true);
        header.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
        ImageView ivProfile = actionBar.findViewById(R.id.iv_profile);
        ivProfile.setVisibility(View.VISIBLE);

        ivProfile.setOnClickListener(this);




    }

    //"""""""""" notification list api """""""""""""//
    private void notificationListApi() {
        if (Constant.isNetworkAvailable(mContext, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + NOTIFICATION_LIST_API)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("user_type","client")
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

    ///"""""""""" Listener """""""""""//
    @Override
    public void notificationOnClick(NotificationResponce.DataBean dataBean) {
        Intent intent= null;
      /*  if (dataBean.getNotification_message().getType().equals("Ongoing_job_request")){
         intent = new Intent(mContext, NotificationJobOnGoingDetailWorkerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(USER_ID, dataBean.getNotification_message().getReference_id());
        intent.putExtra("body", dataBean.getNotification_message().getBody());
        intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
        startActivity(intent);
    } *//*else if (dataBean.getNotification_message().getType().equals("Once_job_created") ||dataBean.getNotification_message().getType().equals("Once_job_rejected") ||dataBean.getNotification_message().getType().equals("Once_job_accepted")  ){
         intent = new Intent(mContext, NotificationJobHelpOfferedDetailWorkerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(USER_ID, dataBean.getNotification_message().getReference_id());
        intent.putExtra("body", dataBean.getNotification_message().getBody());
        intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
        startActivity(intent);
    }*/
        switch (dataBean.getNotification_message().getType()) {
            case "Once_job_request":
                intent = new Intent(mContext, MySingleJobDetailClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("JobIdKey", dataBean.getNotification_message().getReference_id());
                intent.putExtra("body", dataBean.getNotification_message().getBody());
                intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
                startActivity(intent);
                break;
            case "Ongoing_job_accepted":
            case "Ongoing_job_rejected":
                intent = new Intent(mContext, MyOnGoingJobDetailClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("JobIdKey", dataBean.getNotification_message().getReference_id());
                intent.putExtra("body", dataBean.getNotification_message().getBody());
                intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
                startActivity(intent);
                break;
                default:
                    showAlertWorkerDialog();
                    break;

        }
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
                        Intent intent = new Intent(mContext, MyProfileClientActivity.class);
                        startActivity(intent);
                        /*Intent intent = new Intent(this, WorkerMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("MyProfile", "MyProfile");
                        intent.putExtra(USER_ID, userId);
                        intent.putExtra("body", message);
                        intent.putExtra(CONSTANTTYPE, type);
                        pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(tittle, message, pendingIntent);*/
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_profile:
                Intent intent = new Intent(mContext, MyProfileClientActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }
}

 /*  switch (type) {
           case "Ongoing_job_request": {
           Intent intent = new Intent(this, NotificationJobOnGoingDetailWorkerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(USER_ID, userId);
        intent.putExtra("body", message);
        intent.putExtra(CONSTANTTYPE, type);
        pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);
        break;
        }
         *//*   case "Once_job_created": {
                Intent intent = new Intent(this, NotificationJobHelpOfferedDetailWorkerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(USER_ID, userId);
                intent.putExtra("body", message);
                intent.putExtra(CONSTANTTYPE, type);
                pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);

                break;
            }*//*
        case "Once_job_created":
        case "Once_job_rejected":
        case "Once_job_accepted": {
        Intent intent = new Intent(this, NotificationJobHelpOfferedDetailWorkerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(USER_ID, userId);
        intent.putExtra("body", message);
        intent.putExtra(CONSTANTTYPE, type);
        pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);

        break;
        }
        case "Once_job_request":
        {
        Intent intent = new Intent(this, NotificationMySingleJobDetailClientActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(USER_ID, userId);
        intent.putExtra("body", message);
        intent.putExtra(CONSTANTTYPE, type);
        pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);

        break;
        }
        case "Ongoing_job_accepted":
        case "Ongoing_job_rejected":
        {
        Intent intent = new Intent(this, NotificationMyOnGoingJobDetailClientActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(USER_ID, userId);
        intent.putExtra("body", message);
        intent.putExtra(CONSTANTTYPE, type);
        pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);

        break;
        }*/
