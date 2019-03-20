package com.livewire.ui.activity.notification_activity;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ScrollView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivityNotificationJobOngoingDetailWorkerBinding;
import com.livewire.responce.JobDetailWorkerResponce;
import com.livewire.ui.activity.WorkerMainActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_WORKER_JOB_DETAIL_API;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_REQUEST_2_API;

public class NotificationJobOnGoingDetailWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityNotificationJobOngoingDetailWorkerBinding binding;
    private static final String TAG = NotificationJobOnGoingDetailWorkerActivity.class.getName();

    private ProgressDialog progressDialog;
    private ScrollView detailMainLayout;
    private String jobId="";
    private JobDetailWorkerResponce workerResponce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_job_ongoing_detail_worker);
        // setContentView(R.layout.activity_job_ongoing_detail_worker);
        intializeView();
    }

    private void intializeView() {

        progressDialog = new ProgressDialog(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        detailMainLayout = findViewById(R.id.detail_main_layout);

        findViewById(R.id.btn_ignore).setOnClickListener(this);
        findViewById(R.id.btn_accept).setOnClickListener(this);
        binding.btnAcceptRejectLayout.setVisibility(View.VISIBLE);

        Bundle extra = getIntent().getExtras();;
        assert extra != null;
        if (extra.getString("type").equals("Ongoing_job_request")) {
            jobId = extra.getString("reference_id");
            getJobDetailApi();
        }
    }

    private void getJobDetailApi() {

        if (Constant.isNetworkAvailable(this,binding.detailMainLayout)){
            progressDialog.show();
            AndroidNetworking.post(BASE_URL+JOBPOSTSEND_GET_WORKER_JOB_DETAIL_API)
                    .addBodyParameter("job_id",jobId)
                    .addBodyParameter("job_type","2")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            String status = null;
                            try {
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                     workerResponce = new Gson().fromJson(String.valueOf(response),JobDetailWorkerResponce.class);
                                    binding.setWorkerResponcd(workerResponce.getData());
                                    setJobDetailData();
                                }else {
                                    Constant.snackBar(binding.detailMainLayout,message);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
              onBackPressed();
                break;
            case R.id.btn_ignore:
               acceptRejectrequestApi(workerResponce.getData().getUserId(), workerResponce.getData().getJobId(), "2");
                break;
            case R.id.btn_accept:
                acceptRejectrequestApi(workerResponce.getData().getUserId(), workerResponce.getData().getJobId(), "1");
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        Intent intent = new Intent(this, WorkerMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setJobDetailData() {

        binding.tvTime.setText(Constant.getDayDifference(workerResponce.getData().getCrd(), workerResponce.getData().getCurrentDateTime()));

        Picasso.with(binding.ivProfileImg.getContext())
                .load(workerResponce.getData().getProfileImage()).fit().into(binding.ivProfileImg);

        binding.tvStartDate.setText(Constant.DateFomatChange(workerResponce.getData().getJob_start_date()));

        binding.tvEndDate.setText(Constant.DateFomatChange(workerResponce.getData().getJob_end_date()));

        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString minprice = new SpannableString("$ " + workerResponce.getData().getMin_rate());
        minprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, minprice.length(), 0);
        //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(minprice);
        SpannableString toString = new SpannableString(" to ");
        builder.append(toString);


        SpannableString maxprice = new SpannableString("$ " + workerResponce.getData().getMax_rate());
        maxprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, maxprice.length(), 0);
        //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(maxprice);

        binding.tvRangePrice.setText(builder);
    }

    private void acceptRejectrequestApi(String userId, String jobId, String requestStatus) {
        if (Constant.isNetworkAvailable(this, detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + JOBPOSTSEND_REQUEST_2_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("request_by", userId)
                    .addBodyParameter("request_status", requestStatus)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    String status = null;
                    try {
                        status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            Constant.snackBar(detailMainLayout, message);
                            onBackPressed();
                            //"""""' if user successfully created on going post """""""""""//

                            // first time replace home fragment

                        } else {
                            Constant.snackBar(detailMainLayout, message);
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

