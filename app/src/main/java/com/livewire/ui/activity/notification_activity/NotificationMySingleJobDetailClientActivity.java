package com.livewire.ui.activity.notification_activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.livewire.databinding.ActivityNotificationMySingleJobDetailClientBinding;

import com.livewire.responce.JobDetailClientResponce;
import com.livewire.ui.activity.ClientMainActivity;

import com.livewire.ui.activity.RequestClientActivity;
import com.livewire.ui.activity.WorkerMainActivity;
import com.livewire.ui.activity.credit_card.AddCreditCardActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API;

public class NotificationMySingleJobDetailClientActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityNotificationMySingleJobDetailClientBinding binding;
    private static final String TAG = NotificationMySingleJobDetailClientActivity.class.getName();
    private String JobId = "";
    private String usetId = "";
    private String budget = "";
    private String workerName="";
    private ProgressDialog progressDialog;
    private String jobId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_my_single_job_detail_client);
        intializeView();
        jobDetailApi();
    }

    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        binding.ivBack.setOnClickListener(this);
        binding.flMultiImg.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String userType =  extras.getString("for_user_type");
        //"""""""""" check the user type and then go to the activivty """""//
        if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equals(userType)) {
            String type = extras.getString("type");
            if (type.equals("Once_job_request")) {
                jobId = extras.getString("reference_id");
                jobDetailApi();
            }
        }else {// if user type is same
            Intent intent = new Intent(this, WorkerMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("MyProfile", "MyProfile");
            startActivity(intent);
            finish();
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
           // usetId = dataBean.get();
            budget = dataBean.getJob_budget();
            if (dataBean.getTotal_request().equals("0")) {   // no requested yet

                //*//*
                binding.tvNoRequest.setVisibility(View.VISIBLE);
                binding.rlUserData.setVisibility(View.GONE);
                binding.rlMultiImg.setVisibility(View.GONE);
                binding.llChat.setVisibility(View.GONE);
            } else if (/*dataBean.getTotal_request().equals("1") &&*/ dataBean.getJob_confirmed().equals("1")) { // jobconfirmed
                usetId = dataBean.getRequestedUserData().get(0).getUserId(); //worker user id to give review
                workerName = dataBean.getRequestedUserData().get(0).getName();//worker name
                binding.rlUserData.setVisibility(View.VISIBLE);
                binding.llChat.setVisibility(View.VISIBLE);
                binding.tvNoRequest.setVisibility(View.GONE);
                binding.rlMultiImg.setVisibility(View.GONE);

                Picasso.with(binding.ivProfileImg.getContext())
                        .load(dataBean.getRequestedUserData()
                                .get(0).getProfileImage()).fit().into(binding.ivProfileImg);

                binding.tvName.setText(dataBean.getRequestedUserData().get(0).getName());
                binding.tvDistance.setText(dataBean.getRequestedUserData().get(0).getDistance_in_km() + " Km away");

            } else {   // multiple images show

                binding.rlMultiImg.setVisibility(View.VISIBLE);
                binding.rlUserData.setVisibility(View.GONE);
                binding.tvNoRequest.setVisibility(View.GONE);
                binding.llChat.setVisibility(View.GONE);
                binding.tvMemberRequested.setText(dataBean.getTotal_request() + " " + getString(R.string.applications));

                int leftMargin = 0;
                for (int i = 0; i < dataBean.getRequestedUserData().size(); i++) {
                    if (i != 0) {
                        leftMargin = leftMargin + 35;
                    }
                    if (dataBean.getRequestedUserData().size() < 4) {
                        addhorizontalTimeView(binding.flMultiImg, dataBean.getRequestedUserData().get(i).getProfileImage(), leftMargin);
                    }
                }

            }

        }



        /*else if (dataBean.getJob_type().equals("2")){///"""""""""" ONGOING JOB
            if (dataBean.getTotal_request().equals("0")){  // NO OFFER SEND YET

                rlUserData.setVisibility(View.GONE);
                rlMoredetail.setVisibility(View.GONE);
                tvNorequest.setText(R.string.no_offer_request_yet);
                tvNorequest.setVisibility(View.VISIBLE);

            }else if (dataBean.getTotal_request().equals("1") && dataBean.getJob_confirmed().equals("1")) {
                // JOB CONFIRM OR PENDING REQUEST OR IN PROGRESS
                rlUserData.setVisibility(View.VISIBLE);
                rlMoredetail.setVisibility(View.VISIBLE);
                tvNorequest.setVisibility(View.GONE);
            }

            }*/
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
        Intent  intent= null;
        switch (v.getId()) {
            case R.id.iv_back:
               onBackPressed();
                break;
            case R.id.fl_multi_img:
                intent = new Intent(this, RequestClientActivity.class);
                intent.putExtra("JobId", JobId);
                startActivity(intent);

                break;
            case R.id.btn_end_job:
                intent = new Intent(this, AddCreditCardActivity.class);
                intent.putExtra("NameKey",workerName);
                intent.putExtra("PaymentKey", budget);
                intent.putExtra("JobIdKey", JobId);
                intent.putExtra("UserIdKey", usetId);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        Intent intent = new Intent(this, ClientMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void jobDetailApi() {
        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("job_type", "1")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            //progressDialog.dismiss();
                            String status = null;
                            try {
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                    JobDetailClientResponce dataBean = new Gson().fromJson(String.valueOf(response), JobDetailClientResponce.class);
                                    setMyJobDetails(dataBean.getData());
                                    binding.setJobDetail(dataBean.getData());
                                    // binding.setJobDetail(dataBean.getData());

                                    binding.tvTime.setText(Constant.getDayDifference(dataBean.getData().getCrd(), dataBean.getData().getCurrentDateTime()));
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
}
