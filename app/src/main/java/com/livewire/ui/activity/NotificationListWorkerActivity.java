package com.livewire.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.NotificationAdapter;
import com.livewire.databinding.ActivityNotificationListWorkerBinding;
import com.livewire.responce.NotificationResponce;
import com.livewire.ui.activity.complete_confirm_job_worker.CompleteJobHelpOfferedDetailWorkerActivity;
import com.livewire.ui.activity.complete_confirm_job_worker.CompleteJobOnGoingDetailWorkerActivity;
import com.livewire.ui.activity.notification_activity.NotificationJobOnGoingDetailWorkerActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.NOTIFICATION_LIST_API;

public class NotificationListWorkerActivity extends AppCompatActivity implements View.OnClickListener,NotificationAdapter.NotificationOnItemClickListener{
    ActivityNotificationListWorkerBinding binding;
    private ProgressDialog progressDialog;
    private NotificationAdapter notificationAdapter;
    private NotificationResponce notificationResponse;
    private List<NotificationResponce.DataBean> notifiList = new ArrayList<>();
    public static final String USER_ID = "reference_id";
    public static final String CONSTANTTYPE = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_list_worker);
        progressDialog = new ProgressDialog(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
     //   binding.ivBack.setOnClickListener(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        notificationAdapter = new NotificationAdapter(this, notifiList, this);
        binding.recyclerView.setAdapter(notificationAdapter);
        binding.recyclerView.addItemDecoration(new
                DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        notificationListApi();
    }
    //"""""""""" notification list api """""""""""""//
    private void notificationListApi() {
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + NOTIFICATION_LIST_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
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
                            Constant.errorHandle(anError,NotificationListWorkerActivity.this);
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
                intent = new Intent(this, JobOnGoingDetailWorkerActivity.class);
                intent.putExtra("JobDetail", dataBean.getNotification_message().getReference_id());
                intent.putExtra("body", dataBean.getNotification_message().getBody());
                intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
                startActivity(intent);
                break;
            case "Once_job_created":
            case "Once_job_rejected":
            case "Once_job_accepted":
                intent = new Intent(this, JobHelpOfferedDetailWorkerActivity.class);
                intent.putExtra(USER_ID, dataBean.getNotification_message().getReference_id());
                intent.putExtra("body", dataBean.getNotification_message().getBody());
                intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
                startActivity(intent);
                break;
            case "Ongoing_job_completed":
                intent = new Intent(this, CompleteJobOnGoingDetailWorkerActivity.class);
                intent.putExtra("JobIdKey", dataBean.getNotification_message().getReference_id());
                intent.putExtra("CompleteJobKey","OngoingJobComplete");
                intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
                startActivity(intent);
                break;
                case "Once_job_completed":
                intent = new Intent(this, CompleteJobHelpOfferedDetailWorkerActivity.class);
                intent.putExtra("JobIdKey", dataBean.getNotification_message().getReference_id());
                intent.putExtra("CompleteJobKey","OnceJobComplete");
                intent.putExtra(CONSTANTTYPE, dataBean.getNotification_message().getType());
                startActivity(intent);
                break;

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
