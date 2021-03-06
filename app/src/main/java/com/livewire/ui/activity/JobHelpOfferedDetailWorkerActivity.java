package com.livewire.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivityJobHelpOfferedDetailWorkerBinding;
import com.livewire.responce.JobDetailWorkerResponce;
import com.livewire.ui.activity.chat.ChattingActivity;
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


import static com.livewire.utils.ApiCollection.ADD_REVIEW_API;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_WORKER_JOB_DETAIL_API;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_REQUEST_2_API;

public class JobHelpOfferedDetailWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityJobHelpOfferedDetailWorkerBinding binding;
    private ProgressDialog progressDialog;
    private String jobId = "";
    private String userId = "";
    private String name = "";
    private String clientProfileImg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_help_offered_detail_worker);
        intializeViews();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("type").equals("Once_job_created")) {
                jobId = extras.getString("reference_id");
                getJobDetailApi();
            } else if (extras.getString("type").equals("Once_job_rejected")) {
                jobId = extras.getString("reference_id");
                getJobDetailApi();
            } else if (extras.getString("type").equals("Once_job_accepted")) {
                jobId = extras.getString("reference_id");
                binding.btnSendRequest.setVisibility(View.GONE);
                /*binding.tvStatus.setText(R.string.job_confirmed);
                binding.tvStatus.setVisibility(View.VISIBLE);*/
                getJobDetailApi();
            } else if (extras.getString("type") != null) {
                jobId = extras.getString("type");
                getJobDetailApi();
            }

        }
        /*if (getIntent().getSerializableExtra("JobIdKey") != null) {
            HelpOfferedResponce.DataBean jobDetail = (HelpOfferedResponce.DataBean) getIntent().getSerializableExtra("JobIdKey");
            setWorkerDataResponce(jobDetail);
            binding.setJobDetail(jobDetail);
        }*/
    }

    private void intializeViews() {
        progressDialog = new ProgressDialog(this);
        binding.btnSendRequest.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        binding.btnDilog.setOnClickListener(this);
        binding.llChat.setOnClickListener(this);
        binding.rlUserData.setOnClickListener(this);

    }

    private void getJobDetailApi() {
        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + JOBPOSTSEND_GET_WORKER_JOB_DETAIL_API)
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
                                    binding.subMainLayout.setVisibility(View.VISIBLE);
                                    binding.tvMsg.setVisibility(View.GONE);
                                    JobDetailWorkerResponce workerResponce = new Gson().fromJson(String.valueOf(response), JobDetailWorkerResponce.class);
                                    binding.setJobDetail(workerResponce.getData());
                                    setWorkerDataResponce(workerResponce.getData());
                                    userId = workerResponce.getData().getUserId();
                                } else {
                                    binding.subMainLayout.setVisibility(View.GONE);
                                    binding.tvMsg.setVisibility(View.VISIBLE);
                                    binding.tvMsg.setText(""+message);
                                    ///Constant.snackBar(binding.detailMainLayout, message);
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

    //"""""""""set job deatil worker response data """""""""""
    private void setWorkerDataResponce(JobDetailWorkerResponce.DataBean jobDetail) {
        jobId = jobDetail.getJobId();
        userId = jobDetail.getUserId();
        binding.tvDistance.setText(jobDetail.getDistance_in_km() + " Km");
        name = jobDetail.getName();
        binding.tvTime.setText(Constant.getDayDifference(jobDetail.getCrd(), jobDetail.getCurrentDateTime()));

        if (jobDetail.getJob_confirmed().equals("0") && jobDetail.getJob_status().equals("0")) { // pending request
            binding.btnSendRequest.setBackground(null);
            binding.btnSendRequest.setText(R.string.Work_offer_pending);
            binding.btnSendRequest.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
            binding.btnSendRequest.setClickable(false);
        }else if (jobDetail.getJob_confirmed().equals("2")) {// job not decline
            binding.btnSendRequest.setClickable(false);
            binding.btnSendRequest.setText(R.string.application_decline);
            binding.btnSendRequest.setBackground(null);
            binding.btnSendRequest.setTextColor(ContextCompat.getColor(this,R.color.colorRed));
        } else if (jobDetail.getJob_confirmed().equals("3") && jobDetail.getJob_status().equals("0")) {// job not send
            binding.btnSendRequest.setBackground(this.getResources().getDrawable(R.drawable.button_black_bg));
            binding.btnSendRequest.setText(R.string.apply);
            binding.btnSendRequest.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        }else if (jobDetail.getJob_confirmed().equals("4") || jobDetail.getJob_confirmed().equals("1")){// JOB IS COMPLITED || job is confirmed
            binding.btnSendRequest.setVisibility(View.GONE);
        }else if (jobDetail.getJob_status().equals("1")){  // work is accepted by other user
            binding.btnSendRequest.setEnabled(false);
            binding.btnSendRequest.setBackground(null);
            binding.btnSendRequest.setText(R.string.work_accepted);
            binding.btnSendRequest.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        }else if (jobDetail.getJob_status().equals("4")){ // work completed by other user
            binding.btnSendRequest.setEnabled(false);
            binding.btnSendRequest.setBackground(null);
            binding.btnSendRequest.setText(R.string.work_completed);
            binding.btnSendRequest.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        }

        Picasso.with(binding.ivProfileImg.getContext())
                .load(jobDetail.getProfileImage())
                .fit().into(binding.ivProfileImg);
        clientProfileImg = jobDetail.getProfileImage();

        if (!jobDetail.getRating().isEmpty()) {
            binding.ratingBar.setRating(Float.parseFloat(jobDetail.getRating()));
        }

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

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_send_request:
              /*  if (PreferenceConnector.readString(this, PreferenceConnector.IS_BANK_ACC, "").equals("1")) {
                    sendRequestApi();
                } else {
                    showAddBankAccountDialog();
                }*/

                if (PreferenceConnector.readString(this,PreferenceConnector.AVAILABILITY_1,"").equals("1")) {// check avaialability
                    if (PreferenceConnector.readString(this, PreferenceConnector.IS_BANK_ACC, "0").equals("1")) {// check bank account detail
                        sendRequestApi();
                    } else {
                        showAddBankAccountDialog();
                    }
                }else{
                    Constant.showAvaialabilityAlert(this);
                }

                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_dilog:
                openReviewDialog();
                break;
            case R.id.ll_chat:
                intent = new Intent(this, ChattingActivity.class);
                intent.putExtra("otherUID", userId);
                intent.putExtra("titleName", binding.tvName.getText().toString().trim());
                intent.putExtra("profilePic", clientProfileImg);
                startActivity(intent);
                break;
            case R.id.rl_user_data:
                if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
                    intent = new Intent(this, ClientProfileDetailWorkerActivity.class);
                    intent.putExtra("UserIdKey", userId);
                    startActivity(intent);
                }
                break;
            default:
        }
    }

    private void showAddBankAccountDialog() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Add Bank Account");
        builder.setMessage("To apply, first please add your bank account details.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(JobHelpOfferedDetailWorkerActivity.this, AddBankAccountActivity.class);
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

    //""""""" give Review Dialog """""""""""//
    private void openReviewDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("NameKey", name);
        final ReviewDialog dialog = new ReviewDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "");
        dialog.setCancelable(false);
        dialog.getReviewInfo(new ReviewDialog.ReviewDialogListner() {
            @Override
            public void onReviewOnClick(String description, float rating, LinearLayout layout) {

                giveReviewApi(description, rating, dialog, layout);
                // dialog.dismiss();

                // Toast.makeText(JobHelpOfferedDetailWorkerActivity.this, description +" rating: "+rating, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReviewCancel() {

            }
        });
    }

    //"""""""""" give review list""""""""""""""""""'//
    private void giveReviewApi(String description, float rating, final ReviewDialog dialog, final LinearLayout layout) {
        if (Constant.isNetworkAvailable(this, layout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + ADD_REVIEW_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("review_to", userId)
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("rating", "" + rating)
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

    //"""""""""""""""""  send request to """"""""""""""""""""///
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
                            binding.btnSendRequest.setText(R.string.Work_offer_pending);
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

