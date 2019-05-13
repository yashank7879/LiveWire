package com.livewire.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivityMySingleJobDetailClientBinding;
import com.livewire.responce.JobDetailClientResponce;
import com.livewire.ui.activity.chat.ChattingActivity;
import com.livewire.ui.activity.credit_card.AddCreditCardActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings;
import com.oppwa.mobile.connect.provider.Connect;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.LinkedHashSet;
import java.util.Set;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.CONFIRM_PAYMENT_API;
import static com.livewire.utils.ApiCollection.DELETE_JOB_API;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API;

public class MySingleJobDetailClientActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMySingleJobDetailClientBinding binding;
    private static final String TAG = MySingleJobDetailClientActivity.class.getName();
    private ProgressDialog progressDialog;
    private String JobId = "";
    private String usetId = "";
    private String budget = "";
    private String workerName = "";
    private String workerProfilePic = "";
    private JobDetailClientResponce dataBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_single_job_detail_client);
        intializeView();
        // jobDetailApi();
    }

    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        binding.ivBack.setOnClickListener(this);
        binding.flMultiImg.setOnClickListener(this);
        binding.btnEndJob.setOnClickListener(this);
        binding.llChat.setOnClickListener(this);
        binding.rlUserData.setOnClickListener(this);
        binding.ivEditPost.setOnClickListener(this);
        binding.ivDeletePost.setOnClickListener(this);
        if (getIntent().getSerializableExtra("JobIdKey") != null) {
            //MyjobResponceClient.DataBean dataBean = (MyjobResponceClient.DataBean) getIntent().getSerializableExtra("MyJobDetail");
            JobId = getIntent().getStringExtra("JobIdKey");
            jobDetailApi();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jobDetailApi();
    }

    private void setMyJobDetails(JobDetailClientResponce.DataBean dataBean) {
        if (dataBean.getJob_type().equals("1")) {/// """"""" SINGLE JOB
            JobId = dataBean.getJobId();
            budget = dataBean.getJob_budget();
            binding.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentDateTime()));
            binding.tvDate1.setText(Constant.DateFomatChange(dataBean.getJob_start_date()).substring(0, 2) + " ");
            binding.tvDateMonth.setText(Constant.DateFomatChange(dataBean.getJob_start_date()).substring(3));

            if (dataBean.getTotal_request().equals("0")) {   // no requested yet
                binding.tvNoRequest.setVisibility(View.VISIBLE);
                binding.rlUserData.setVisibility(View.GONE);
                binding.rlMultiImg.setVisibility(View.GONE);
                binding.ivEditPost.setVisibility(View.VISIBLE);//Visible when no one sent you request and also you not send any request : user can post edit
                binding.ivDeletePost.setVisibility(View.VISIBLE);//Visible when no one sent you request and also you not send any request : user can post edit
                //binding.llChat.setVisibility(View.GONE);
            } else if (/*dataBean.getTotal_request().equals("1") &&*/ dataBean.getJob_confirmed().equals("1")) { // jobconfirmed
                if (!dataBean.getRequestedUserData().get(0).getRating().isEmpty()) {
                    binding.ratingBar.setRating(Float.parseFloat(dataBean.getRequestedUserData().get(0).getRating()));
                }
                binding.rlUserData.setVisibility(View.VISIBLE);
                binding.llChat.setVisibility(View.VISIBLE);
                binding.btnEndJob.setVisibility(View.VISIBLE);
                binding.tvNoRequest.setVisibility(View.GONE);
                binding.rlMultiImg.setVisibility(View.GONE);

                usetId = dataBean.getRequestedUserData().get(0).getUserId(); //worker user id to give review
                workerName = dataBean.getRequestedUserData().get(0).getName();//worker name
                workerProfilePic = dataBean.getRequestedUserData().get(0).getProfileImage();
                Picasso.with(binding.ivProfileImg.getContext())
                        .load(dataBean.getRequestedUserData()
                                .get(0).getProfileImage()).fit().into(binding.ivProfileImg);

                binding.tvName.setText(dataBean.getRequestedUserData().get(0).getName());
                binding.tvDistance.setText(dataBean.getRequestedUserData().get(0).getDistance_in_km() + " Km");
            } else {// multiple images show

                binding.rlMultiImg.setVisibility(View.VISIBLE);
                binding.rlUserData.setVisibility(View.GONE);
                binding.tvNoRequest.setVisibility(View.GONE);
                binding.llChat.setVisibility(View.GONE);

                String textApplication = dataBean.getTotal_request().trim().equals("1") ? getString(R.string.application) : getString(R.string.applications);
                binding.tvMemberRequested.setText(dataBean.getTotal_request() + " " + textApplication);

                int leftMargin = 0;
                for (int i = 0; i < dataBean.getRequestedUserData().size(); i++) {
                    if (i != 0) {
                        leftMargin = leftMargin + 35;
                    }
                    addhorizontalTimeView(binding.flMultiImg, dataBean.getRequestedUserData().get(i).getProfileImage(), leftMargin);
                }
            }
        }
    }

    //""""""""""  add image at run time """""""""""""//
    void addhorizontalTimeView(FrameLayout linearLayout, String profileImage, int leftMargin) {
        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View v = layoutInflater.inflate(R.layout.multiple_image_cell, linearLayout, false);

        final ImageView showTime = v.findViewById(R.id.iv_profile);

        Picasso.with(showTime.getContext()).load(profileImage).fit().into(showTime);

        // Get the TextView current LayoutParams
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) showTime.getLayoutParams();
        // Set TextView layout margin 25 pixels to all side
        // Left Top Right Bottom Margin
        lp.setMargins(leftMargin, 0, 0, 0);
        showTime.setLayoutParams(lp);
        linearLayout.addView(v);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.fl_multi_img:
                intent = new Intent(this, RequestClientActivity.class);
                //intent.putExtra("UserId", usetId);
                intent.putExtra("JobId", JobId);
                startActivity(intent);
                break;
            case R.id.btn_end_job:
                getCheckOutId();
                break;
            case R.id.ll_chat:
                intent = new Intent(this, ChattingActivity.class);
                intent.putExtra("otherUID", usetId);
                intent.putExtra("titleName", workerName);
                intent.putExtra("profilePic", workerProfilePic);
                startActivity(intent);
                break;
            case R.id.rl_user_data:
                intent = new Intent(this, WorkerProfileDetailClientActivity.class);
                intent.putExtra("UserIdKey", usetId);
                startActivity(intent);
                break;
            case R.id.iv_edit_post:
                intent = new Intent(this, EditShortJobActivity.class);
                intent.putExtra("JobDetail",dataBean);
                startActivity(intent);
                break;
            case R.id.iv_delete_post:
                aleartDeleteDialog();

                break;
            default:
        }
    }

    private void aleartDeleteDialog() {
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setTitle("Alert");
        builder1.setMessage("Are you sure you want to delete this job?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deletePostApi();
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

        android.app.AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    private void deletePostApi() {
        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + DELETE_JOB_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", JobId)
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
                                if (status.equals("success")) {//checkout_id
                                   onBackPressed();
                                } else {
                                    progressDialog.dismiss();
                                    Constant.snackBar(binding.detailMainLayout, message);
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.getMessage());
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, anError.getErrorDetail());
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void getCheckOutId() {
        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + CONFIRM_PAYMENT_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("project_id", JobId)
                    .addBodyParameter("working_days", "")
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
                                if (status.equals("success")) {//checkout_id
                                    String checkoutId = response.getString("checkout_id");
                                    budget = response.getString("amount");
                                    String currency = response.getString("currency");

                                    Intent intent = new Intent(MySingleJobDetailClientActivity.this, MakePaymentActivity.class);
                                    intent.putExtra("SingleJobPayment", "SingleJob");
                                    intent.putExtra("NameKey", workerName);
                                    intent.putExtra("PaymentKey", budget);
                                    intent.putExtra("CurrencyKey", currency);
                                    intent.putExtra("checkoutIdKey", checkoutId);
                                    intent.putExtra("JobIdKey", JobId);
                                    intent.putExtra("UserIdKey", usetId);
                                    startActivity(intent);
                                } else {
                                    progressDialog.dismiss();
                                    Constant.snackBar(binding.detailMainLayout, message);
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.getMessage());
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, anError.getErrorDetail());
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void jobDetailApi() {
        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", JobId)
                    .addBodyParameter("job_type", "1")
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
                                    binding.rlMsg.setVisibility(View.GONE);
                                    binding.subMainLayout.setVisibility(View.VISIBLE);
                                     dataBean = new Gson().fromJson(String.valueOf(response), JobDetailClientResponce.class);
                                    setMyJobDetails(dataBean.getData());
                                    binding.setJobDetail(dataBean.getData());

                                    binding.tvTime.setText(Constant.getDayDifference(dataBean.getData().getCrd(), dataBean.getData().getCurrentDateTime()));
                                } else {
                                    binding.rlMsg.setVisibility(View.VISIBLE);
                                    binding.subMainLayout.setVisibility(View.GONE);
                                    binding.tvMsg.setText(message);
                                    progressDialog.dismiss();
                                   // Constant.snackBar(binding.detailMainLayout, message);
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.getMessage());
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, anError.getErrorDetail());
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}
