package com.livewire.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.livewire.R;
import com.livewire.databinding.ActivityJobHelpOfferedDetailWorkerBinding;
import com.livewire.responce.HelpOfferedResponce;
import com.livewire.ui.dialog.ReviewDialog;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_REQUEST_2_API;

public class JobHelpOfferedDetailWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityJobHelpOfferedDetailWorkerBinding binding;
    private ProgressDialog progressDialog;
    private String jobId = "";
    private String userId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_help_offered_detail_worker);
        intializeViews();

        if (getIntent().getSerializableExtra("JobIdKey") != null) {
            HelpOfferedResponce.DataBean jobDetail = (HelpOfferedResponce.DataBean) getIntent().getSerializableExtra("JobIdKey");
            setWorkerDataResponce(jobDetail);
            binding.setJobDetail(jobDetail);
        }
    }

    private void intializeViews() {
        progressDialog = new ProgressDialog(this);
    /*    binding.ivProfileImg = findViewById(R.id.iv_profile_img);
        tvName = findViewById(R.id.tv_name);
        ratingBar = findViewById(R.id.rating_bar);
        tvDistance = findViewById(R.id.tv_distance);
        tvTime = findViewById(R.id.tv_time);
        tvMoreInfo = findViewById(R.id.tv_chat);
        tvCategory = findViewById(R.id.tv_category);
        tvDate = findViewById(R.id.tv_date);
        tvDateMonth = findViewById(R.id.tv_date_month);
        tvSubCategory = findViewById(R.id.tv_sub_category);
        tvBudgetPrice = findViewById(R.id.tv_budget_price);
        tvDescription = findViewById(R.id.tv_description);
        mainLayout = findViewById(R.id.detail_main_layout);
        btnSendRequest = findViewById(R.id.btn_send_request);*/
        binding.btnSendRequest.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        binding.btnDilog.setOnClickListener(this);

    }

  /*  private void getJobDetailApi(String reference_id) {
        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "Jobpost/getJobDetail")
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("job_type", "1")
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
                                    JobDetailWorkerResponce workerResponce = new Gson().fromJson(String.valueOf(response), JobDetailWorkerResponce.class);
                                  //  binding.setJobDetailFromApi(workerResponce.getData());

                                } else {
                                    Constant.snackBar(binding.detailMainLayout, message);
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
    }*/

    //"""""""""set job deatil worker response data """""""""""
    private void setWorkerDataResponce(HelpOfferedResponce.DataBean jobDetail) {
        jobId = jobDetail.getJobId();
        userId = jobDetail.getUserId();
        binding.tvDistance.setText(jobDetail.getDistance_in_km() + " Km away");

        binding.tvTime.setText(Constant.getDayDifference(jobDetail.getCrd(), jobDetail.getCurrentDateTime()));

        if (jobDetail.getJob_confirmed().equals("0")) { // pending request
            binding.btnSendRequest.setBackground(null);
            binding.btnSendRequest.setText(R.string.pending_request);
            binding.btnSendRequest.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
            binding.btnSendRequest.setClickable(false);
   /*        }else if (dataBean.getJob_confirmed().equals("1")){// accepted
               holder.btnSendRequest.setClickable(false);
           }else if (dataBean.getJob_confirmed().equals("2")){// job not accepted
            holder.btnSendRequest.setClickable(false);*/
        } else if (jobDetail.getJob_confirmed().equals("3")) {// job not send
            binding.btnSendRequest.setBackground(this.getResources().getDrawable(R.drawable.button_green_bg));
            binding.btnSendRequest.setText(R.string.send_request);
            binding.btnSendRequest.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        Picasso.with(binding.ivProfileImg.getContext())
                .load(jobDetail.getProfileImage())
                .fit().into(binding.ivProfileImg);

        //********"2018-07-04" date format converted into "04 jul 2018"***********//
        DateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String start = jobDetail.getJob_start_date();

        try {
            Date newStartDate;
            newStartDate = sd.parse(start);
            sd = new SimpleDateFormat("dd MMM yyyy");
            start = sd.format(newStartDate);

            binding.tvDate.setText(start.substring(0, 2) + " ");
            binding.tvDateMonth.setText(start.substring(3));
            // holder.tvDate.setText(start);
        } catch (ParseException e) {
            Log.e("Tag", e.getMessage());
        }

        if (jobDetail.getJob_confirmed().equals("0")) { // pending request
            binding.btnSendRequest.setBackground(null);
            binding.btnSendRequest.setText(R.string.pending_request);
            binding.btnSendRequest.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
            binding.btnSendRequest.setClickable(false);
     /*   } else if (jobDetail.getJob_confirmed().equals("1")) {// accepted
            btnSendRequest.setClickable(false);
        } else if (jobDetail.getJob_confirmed().equals("2")) {// job not accepted
            btnSendRequest.setClickable(false);*/
        } else if (jobDetail.getJob_confirmed().equals("3")) {// job not send

        }
        // tvDate.setText(Constant.getDayDifference(workerResponce.getData().get(0).getCrd(),workerResponce.getData().get(0).getCurrentDateTime()) );
        // tvDateMonth.setText(Constant.getDayDifference(workerResponce.getData().get(0).getCrd(),workerResponce.getData().get(0).getCurrentDateTime()) );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_request:
                sendRequestApi();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_dilog:
                openReviewDialog();
                break;
            default:
        }
    }

    private void openReviewDialog() {
        final ReviewDialog dialog = new ReviewDialog();
        dialog.show(getSupportFragmentManager(), "");
        dialog.setCancelable(true);
        dialog.getReviewInfo(new ReviewDialog.ReviewDialogListner() {
            @Override
            public void onReviewOnClick(String description, float rating, LinearLayout layout) {

                giveReviewApi(description,rating,dialog,layout);
               // dialog.dismiss();

                Toast.makeText(JobHelpOfferedDetailWorkerActivity.this, description +" rating: "+rating, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void giveReviewApi(String description, float rating, final ReviewDialog dialog, final LinearLayout layout) {
        if (Constant.isNetworkAvailable(this,layout)){
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "user/addReview")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("review_to", userId)
                    .addBodyParameter("job_id", jobId )
                    .addBodyParameter("rating", ""+rating)
                    .addBodyParameter("description", description)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            dialog.dismiss();
                          //  Constant.snackBar(binding.detailMainLayout, message);
                        } else {
                            Constant.snackBar(layout, message);
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

    //"""""""""""  send request to
    private void sendRequestApi() {
        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + JOBPOSTSEND_REQUEST_2_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("request_to", userId)
                    .addBodyParameter("request_status", "0")
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            binding.btnSendRequest.setBackground(null);
                            binding.btnSendRequest.setText(R.string.pending_request);
                            binding.btnSendRequest.setTextColor(ContextCompat.getColor(JobHelpOfferedDetailWorkerActivity.this, R.color.colorOrange));
                            binding.btnSendRequest.setClickable(false);
                            Constant.snackBar(binding.detailMainLayout, message);
                        } else {
                            Constant.snackBar(binding.detailMainLayout, message);
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

