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
import com.livewire.adapter.NotificationAdapter;
import com.livewire.databinding.ActivityNotificationListWorkerBinding;
import com.livewire.responce.NotificationResponce;
import com.livewire.ui.activity.JobHelpOfferedDetailWorkerActivity;
import com.livewire.ui.activity.JobOnGoingDetailWorkerActivity;
import com.livewire.ui.activity.MyProfileWorkerActivity;
import com.livewire.ui.activity.complete_confirm_job_worker.CompleteJobHelpOfferedDetailWorkerActivity;
import com.livewire.ui.activity.complete_confirm_job_worker.CompleteJobOnGoingDetailWorkerActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.NOTIFICATION_LIST_API;

public class NotificationWorkerFragment extends Fragment implements View.OnClickListener ,NotificationAdapter.NotificationOnItemClickListener{
    ActivityNotificationListWorkerBinding binding;
    private ProgressDialog progressDialog;
    private NotificationAdapter notificationAdapter;
    private NotificationResponce notificationResponse;
    private List<NotificationResponce.DataBean> notifiList = new ArrayList<>();
    public static final String USER_ID = "reference_id";
    public static final String CONSTANTTYPE = "type";
    private Context mContext;

    public NotificationWorkerFragment() {
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
       binding = DataBindingUtil.inflate(inflater,R.layout.activity_notification_list_worker, container, false);
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
        notificationListApi();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    //"""""""""" notification list api """""""""""""//
    private void notificationListApi() {
        if (Constant.isNetworkAvailable(mContext, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + NOTIFICATION_LIST_API)
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
                            Constant.errorHandle(anError,getActivity());
                        }
                    });
        }
    }

    ///"""""""""" Listener """""""""""//
    @Override
    public void notificationOnClick(NotificationResponce.DataBean dataBean) {
        Intent intent = null;
        switch (dataBean.getNotification_message().getType()) {
            case "Ongoing_job_request":
            case "Ongoing_job_accepted":
                intent = new Intent(mContext, JobOnGoingDetailWorkerActivity.class);
                intent.putExtra("JobDetail", dataBean.getNotification_message().getReference_id());
                intent.putExtra("body", dataBean.getNotification_message().getBody());
                intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
                startActivity(intent);
                break;
            case "Once_job_created":
            case "Once_job_rejected":
            case "Once_job_accepted":
                intent = new Intent(mContext, JobHelpOfferedDetailWorkerActivity.class);
                intent.putExtra(USER_ID, dataBean.getNotification_message().getReference_id());
                intent.putExtra("body", dataBean.getNotification_message().getBody());
                intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
                startActivity(intent);
                break;
            case "Ongoing_job_completed":
                intent = new Intent(mContext, CompleteJobOnGoingDetailWorkerActivity.class);
                intent.putExtra("JobIdKey", dataBean.getNotification_message().getReference_id());
                intent.putExtra("CompleteJobKey","OngoingJobComplete");
                intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
                startActivity(intent);
                break;
            case "Once_job_completed":
                intent = new Intent(mContext, CompleteJobHelpOfferedDetailWorkerActivity.class);
                intent.putExtra("JobIdKey", dataBean.getNotification_message().getReference_id());
                intent.putExtra("CompleteJobKey","OnceJobComplete");
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
        builder1.setMessage("You Need To Switch Your Profile");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(mContext, MyProfileWorkerActivity.class);
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
        builder1.setNegativeButton("No",
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
        switch (view.getId()){
            case R.id.iv_back:

                break;
            default:
        }
    }
}
