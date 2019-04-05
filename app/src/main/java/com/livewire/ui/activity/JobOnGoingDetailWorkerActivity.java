package com.livewire.ui.activity;


import android.content.DialogInterface;
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
import com.livewire.databinding.ActivityJobOngoingDetailWorkerBinding;
import com.livewire.responce.JobDetailWorkerResponce;
import com.livewire.responce.OnGoingWorkerResponce;
import com.livewire.ui.activity.chat.ChattingActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_WORKER_JOB_DETAIL_API;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_REQUEST_2_API;

public class JobOnGoingDetailWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityJobOngoingDetailWorkerBinding binding;
    private static final String TAG = JobOnGoingDetailWorkerActivity.class.getName();

    private ProgressDialog progressDialog;
    private ScrollView detailMainLayout;
    private JobDetailWorkerResponce workerResponcd;
    private String jobId="";
    private String userId="";
    private String clientProfileImg="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_ongoing_detail_worker);
        // setContentView(R.layout.activity_job_ongoing_detail_worker);
        intializeView();
    }

    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        detailMainLayout = findViewById(R.id.detail_main_layout);

        findViewById(R.id.btn_ignore).setOnClickListener(this);
        findViewById(R.id.btn_accept).setOnClickListener(this);
        binding.llChat.setOnClickListener(this);
        binding.rlUserData.setOnClickListener(this);

        if (getIntent().getSerializableExtra("JobDetail") != null) {
            jobId =  getIntent().getStringExtra("JobDetail");
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
                                    workerResponcd = new Gson().fromJson(String.valueOf(response),JobDetailWorkerResponce.class);
                                    binding.setWorkerResponcd(workerResponcd.getData());
                                    setJobDetailData(workerResponcd.getData());
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
                acceptRejectrequestApi(workerResponcd.getData().getUserId(), workerResponcd.getData().getJobId(), "2");
                /*if (PreferenceConnector.readString(this,PreferenceConnector.IS_BANK_ACC,"").equals("1")) {
                    acceptRejectrequestApi(workerResponcd.getUserId(), workerResponcd.getJobId(), "2");
                }else {
                    showAddBankAccountDialog();
                }*/
                break;
            case R.id.btn_accept:
                acceptRejectrequestApi(workerResponcd.getData().getUserId(), workerResponcd.getData().getJobId(), "1");
                /* if (PreferenceConnector.readString(this,PreferenceConnector.IS_BANK_ACC,"").equals("1")) {
                    acceptRejectrequestApi(workerResponcd.getUserId(), workerResponcd.getJobId(), "1");
                }else {
                    showAddBankAccountDialog();
                }*/
                break;
            case R.id.ll_chat: {
                Intent intent = new Intent(this, ChattingActivity.class);
                intent.putExtra("otherUID",userId );
                intent.putExtra("titleName", binding.tvName.getText().toString().trim());
                intent.putExtra("profilePic", clientProfileImg);
                startActivity(intent);
            }
             break;
            case R.id.rl_user_data: {
                if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
                    Intent intent = new Intent(this, ClientProfileDetailWorkerActivity.class);
                    intent.putExtra("UserIdKey", userId);
                    startActivity(intent);
                }
            }
                break;
            default:
        }
    }

    private void showAddBankAccountDialog() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Add Bank Account");
        builder.setMessage("Please add your bank details first.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(JobOnGoingDetailWorkerActivity.this,AddBankAccountActivity.class);
                startActivity(intent);
                // logoutApiCalling();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialog;
        dialog.show();
    }

    private void setJobDetailData(JobDetailWorkerResponce.DataBean workerResponcd) {

        userId  = workerResponcd.getUserId();
        clientProfileImg  = workerResponcd.getProfileImage();

        binding.tvTime.setText(Constant.getDayDifference(workerResponcd.getCrd(), workerResponcd.getCurrentDateTime()));

        binding.tvDistance.setText(workerResponcd.getDistance_in_km() + " Km away");

        Picasso.with(binding.ivProfileImg.getContext())
                .load(workerResponcd.getProfileImage()).fit().into(binding.ivProfileImg);

        if (!workerResponcd.getRating().isEmpty()) {
            binding.ratingBar.setRating(Float.parseFloat(workerResponcd.getRating()));
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString minprice = new SpannableString("$ " + workerResponcd.getMin_rate());
        minprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, minprice.length(), 0);
        //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(minprice);
        SpannableString toString = new SpannableString(" to ");
        builder.append(toString);


        SpannableString maxprice = new SpannableString("$ " + workerResponcd.getMax_rate());
        maxprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, maxprice.length(), 0);
        //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(maxprice);

        binding.tvRangePrice.setText(builder);

        switch (workerResponcd.getJob_confirmed()) {
            case "0":
                binding.acceptRejectLayout.setVisibility(View.VISIBLE);

                break;
            case "1":
                binding.acceptRejectLayout.setVisibility(View.GONE);
                break;
            default:
            /*Noting will print*/
                break;
        }


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

