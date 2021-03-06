package com.livewire.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivityMyOngoingJobDetailClientBinding;
import com.livewire.responce.JobDetailClientResponce;
import com.livewire.ui.activity.chat.ChattingActivity;
import com.livewire.ui.activity.credit_card.AddCreditCardActivity;
import com.livewire.ui.dialog.ReviewDialog;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.ADD_REVIEW_API;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.CANCLE_JOB_BY_CLIENT_API;
import static com.livewire.utils.ApiCollection.CONFIRM_PAYMENT_API;
import static com.livewire.utils.ApiCollection.DELETE_JOB_API;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API;

public class MyOnGoingJobDetailClientActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMyOngoingJobDetailClientBinding binding;
    private static final String TAG = MyOnGoingJobDetailClientActivity.class.getName();
    private ProgressDialog progressDialog;
    private int width;
    private String offer = "";
    private String hours = "";
    private String jobId = "";
    private String userId = "";
    private String workerProfilePic = "";
    private String jobRequestId = "";
    private String noOfDays = "";
    private String name="";
    private JobDetailClientResponce JobDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_ongoing_job_detail_client);
        intializeView();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        // jobDetailApi();
    }


    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        binding.btnEndJob.setOnClickListener(this);
        binding.llChat.setOnClickListener(this);
        binding.rlUserData.setOnClickListener(this);
        binding.btnSendOffer.setOnClickListener(this);
        binding.ivEditPost.setOnClickListener(this);
        binding.ivDeletePost.setOnClickListener(this);


        if (getIntent().getStringExtra("JobIdKey") != null) {
            jobId = getIntent().getStringExtra("JobIdKey");
            if (getIntent().getStringExtra("jobrequestId") != null) {
                jobRequestId = getIntent().getStringExtra("jobrequestId");
            }
            jobDetailApi();


            /*setMyJobDetails(dataBean);
            binding.setDataBean(dataBean);
            binding.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentDateTime()));*/

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        jobDetailApi();
    }

    private void setMyJobDetails(final JobDetailClientResponce dataBean) {
        noOfDays = dataBean.getData().getNumber_of_days();
        jobId = dataBean.getData().getJobId();

        if (dataBean.getData().getJob_type().equals("2")) {///"""""""""" ONGOING JOB """'
            if (dataBean.getData().getTotal_request().equals("0")) {  // NO OFFER SEND YET
                binding.btnSendOffer.setVisibility(View.VISIBLE);
                binding.rlMoredetail.setVisibility(View.VISIBLE);
                binding.tvNoRequest.setVisibility(View.VISIBLE);
                binding.tvJobStatus.setVisibility(View.INVISIBLE);
                binding.llChat.setVisibility(View.GONE);
                binding.rlUserData.setVisibility(View.GONE);
                binding.rlRange.setVisibility(View.GONE);

                binding.tvNoRequest.setText(R.string.no_offer_application_yet);
                binding.tvStartDate.setText(Constant.DateFomatChange(dataBean.getData().getJob_start_date()));
                binding.tvEndDate.setText(Constant.DateFomatChange(dataBean.getData().getJob_end_date()));

                binding.ivEditPost.setVisibility(View.VISIBLE);//Visible when no one sent you request and also you not send any request : user can edit posted work
                binding.ivDeletePost.setVisibility(View.VISIBLE);//Visible when no one sent you request and also you not send any request : user can edit posted work
                jobId = dataBean.getData().getJobId();


              /*  binding.btnSendOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       *//* Intent intent = new Intent(MyOnGoingJobDetailClientActivity.this, NearYouClientActivity.class);
                        intent.putExtra("JobIdKey", jobId);
                        startActivity(intent);*//*
                    }
                });*/

            } else if (dataBean.getData().getTotal_request().equals("1")) {
                // JOB CONFIRM OR PENDING REQUEST OR IN PROGRESS
                if (!dataBean.getData().getRequestedUserData().get(0).getRating().isEmpty()) {
                    binding.ratingBar.setRating(Float.parseFloat(dataBean.getData().getRequestedUserData().get(0).getRating()));
                }
                switch (dataBean.getData().getJob_confirmed()) {
                    case "0": // request pending job
                        if (dataBean.getData().getRequestedUserData().get(0).getRequest_status().equals("0")) {
                            binding.tvJobStatus.setText(R.string.Work_offer_pending);
                            binding.tvJobStatus.setBackground(getResources().getDrawable(R.drawable.doteted_orange_shape));
                            binding.tvJobStatus.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
                            binding.btnCancelJob.setVisibility(View.VISIBLE);
                            binding.btnCancelJob.setOnClickListener(this);
                            getWorkerData(dataBean);

                        } else if (dataBean.getData().getRequestedUserData().get(0).getRequest_status().equals("2")) {
                            binding.tvJobStatus.setText(R.string.request_cancel);
                            binding.tvJobStatus.setBackground(getResources().getDrawable(R.drawable.doteted_balck_shape));
                            binding.tvJobStatus.setTextColor(ContextCompat.getColor(this, R.color.colorDarkBlack));
                        }
                        break;
                    case "1":  //request_confirmed job
                        if (dataBean.getData().getRequestedUserData().get(0).getRequest_status().equals("1")) {
                            userId = dataBean.getData().getRequestedUserData().get(0).getUserId();
                            binding.tvJobStatus.setText(R.string.work_offer_accepted);
                            binding.tvJobStatus.setBackground(getResources().getDrawable(R.drawable.doteted_green_shape));
                            binding.tvJobStatus.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
                            binding.btnEndJob.setVisibility(View.VISIBLE);

                            getWorkerData(dataBean);
                            break;
                        }
                    case "4":{// completed
                        binding.rlPaymentInfo.setVisibility(View.VISIBLE);
                       binding.tvJobStatus.setVisibility(View.INVISIBLE);
                        userId = dataBean.getData().getRequestedUserData().get(0).getUserId();
                        float totalPrice = Float.parseFloat(dataBean.getData().getJob_time_duration()) *
                                Float.parseFloat(dataBean.getData().getNumber_of_days())
                                * Float.parseFloat(dataBean.getData().getJob_offer());
                        binding.tvTotalPrice.setText("R"+totalPrice);
                        name =dataBean.getData().getRequestedUserData().get(0).getName();
                        for (JobDetailClientResponce.ReviewBean reviewBean : dataBean.getReview()) {
                            if (reviewBean.getReview_by().equals(PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, ""))) {
                                binding.tvJobReview.setVisibility(View.VISIBLE);
                                binding.btnGiveReview.setVisibility(View.GONE);
                                binding.reviewByRl.setVisibility(View.VISIBLE);
                                binding.tvReviewTime.setText(Constant.getDayDifference(reviewBean.getCrd(), dataBean.getData().getCurrentDateTime()));
                                binding.tvReviewDescription.setText(reviewBean.getReview_description());
                                binding.ratingBarReview.setRating(Float.parseFloat(reviewBean.getRating()));
                            } else {
                                binding.tvJobReview.setVisibility(View.VISIBLE);
                                binding.reviewUserRl.setVisibility(View.VISIBLE);
                                binding.btnGiveReview.setVisibility(View.VISIBLE);
                                binding.tvUserNameReview.setText(dataBean.getData().getRequestedUserData().get(0).getName());
                                binding.tvUserTimeReview.setText(Constant.getDayDifference(reviewBean.getCrd(), dataBean.getData().getCurrentDateTime()));
                                binding.tvReviewUserDescription.setText(reviewBean.getReview_description());
                                binding.ratingBarUserReview.setRating(Float.parseFloat(reviewBean.getRating()));
                            }
                        }
                    }
                        break;
                    default: // in progress job
                        binding.tvJobStatus.setBackground(getResources().getDrawable(R.drawable.doteted_green_shape));
                        binding.tvJobStatus.setTextColor(ContextCompat.getColor(this, R.color.colorDarkBlack));
                        break;
                }

                binding.rlRange.setVisibility(View.VISIBLE);
                binding.llChat.setVisibility(View.VISIBLE);
                binding.rlUserData.setVisibility(View.VISIBLE);
                binding.rlMoredetail.setVisibility(View.VISIBLE);
                binding.tvNoRequest.setVisibility(View.GONE);
                binding.btnSendOffer.setVisibility(View.GONE);


                binding.tvName.setText(dataBean.getData().getRequestedUserData().get(0).getName());

                binding.tvDistance.setText(dataBean.getData().getRequestedUserData().get(0).getDistance_in_km() + " Km");

                userId = dataBean.getData().getRequestedUserData().get(0).getUserId();

                Picasso.with(binding.ivProfileImg.getContext())
                        .load(dataBean.getData().getRequestedUserData()
                                .get(0).getProfileImage()).fit().into(binding.ivProfileImg);

                binding.tvStartDate.setText(Constant.DateFomatChange(dataBean.getData().getJob_start_date()));
                binding.tvEndDate.setText(Constant.DateFomatChange(dataBean.getData().getJob_end_date()));

                SpannableStringBuilder builder = new SpannableStringBuilder();
                SpannableString minprice = new SpannableString("R" + dataBean.getData().getRequestedUserData().get(0).getMin_rate());
               /* minprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, minprice.length(), 0);
                //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
                builder.append(minprice);
                SpannableString toString = new SpannableString(" to ");
                builder.append(toString);


                SpannableString maxprice = new SpannableString("R" + dataBean.getData().getRequestedUserData().get(0).getMax_rate());
                maxprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, maxprice.length(), 0);
                //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
                builder.append(maxprice);*/

                binding.tvRangePrice.setText(minprice);

            }

        }
    }

    private void getWorkerData(JobDetailClientResponce dataBean) {
        offer = dataBean.getData().getJob_offer();
        hours = dataBean.getData().getJob_time_duration();
        workerProfilePic = dataBean.getData().getRequestedUserData().get(0).getProfileImage();
    }

//complete job :-
    //give review
    // doted status
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_end_job:
                openWorkingDaysDialog();
                break;
            case R.id.ll_chat:
                Intent intent = new Intent(this, ChattingActivity.class);
                intent.putExtra("otherUID", userId);
                intent.putExtra("titleName", binding.tvName.getText().toString());
                intent.putExtra("profilePic", workerProfilePic);
                startActivity(intent);
                break;
            case R.id.btn_cancel_job:
                cancelJobApi();
                break;
            case R.id.rl_user_data:
                intent = new Intent(this, WorkerProfileDetailClientActivity.class);
                intent.putExtra("UserIdKey", userId);
                startActivity(intent);
                break;
            case R.id.btn_send_offer:
                intent = new Intent(MyOnGoingJobDetailClientActivity.this, NearYouClientActivity.class);
                intent.putExtra("JobIdKey", jobId);
                startActivity(intent);
                break;
            case R.id.btn_give_review:
                openReviewDialog();
                break;
            case R.id.iv_edit_post:
                intent = new Intent(this,EditLongTermJobActivity.class);
                intent.putExtra("JobDetail",JobDetail);
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
                    .addBodyParameter("job_id", jobId)
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
            }

            @Override
            public void onReviewCancel() {

            }
        });
    }


    //"""""""""" give review list""""""""""""""""""'//
    private void giveReviewApi(final String description, final float rating, final ReviewDialog dialog, final LinearLayout layout) {
        if (Constant.isNetworkAvailable(this, layout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + ADD_REVIEW_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("review_by", PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, ""))
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
                            binding.reviewByRl.setVisibility(View.VISIBLE);
                            binding.ratingBarReview.setRating(rating);
                            binding.tvReviewDescription.setText(description);
                            binding.tvJobReview.setVisibility(View.VISIBLE);
                            binding.btnGiveReview.setVisibility(View.GONE);
                            binding.tvReviewTime.setText("just now");
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



    private void cancelJobApi() {
        //  Jobpost/cancleJobByClient

        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + CANCLE_JOB_BY_CLIENT_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_request_id", jobRequestId)
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
                                    Intent intent = new Intent(MyOnGoingJobDetailClientActivity.this, NearYouClientActivity.class);
                                    intent.putExtra("JobIdKey", jobId);
                                    startActivity(intent);
                                    finish();
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

    private void openWorkingDaysDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.end_job_dialog);
        dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
        final EditText etEmailDialog = dialog.findViewById(R.id.et_days);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        final ProgressBar progressBar1 = dialog.findViewById(R.id.progress_bar);
        final RelativeLayout forgotDialogLayout = dialog.findViewById(R.id.forgot_layout);
        etEmailDialog.setText(noOfDays);
        dialog.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmailDialog.getText().toString().isEmpty()) {
                    Constant.snackBar(forgotDialogLayout, "Please Enter Number Of working Days.");
                    Constant.hideSoftKeyBoard(MyOnGoingJobDetailClientActivity.this, etEmailDialog);
                } else {

                    getCheckOutId(etEmailDialog.getText().toString(), dialog);

                    Constant.hideSoftKeyBoard(MyOnGoingJobDetailClientActivity.this, etEmailDialog);
                }
            }
        });


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.hideSoftKeyBoard(MyOnGoingJobDetailClientActivity.this, etEmailDialog);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();

    }


    private void jobDetailApi() {
        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("job_type", "2")
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
                                    binding.tvMsg.setVisibility(View.GONE);
                                    binding.subMainLayout.setVisibility(View.VISIBLE);
                                     JobDetail = new Gson().fromJson(String.valueOf(response), JobDetailClientResponce.class);
                                    setMyJobDetails(JobDetail);
                                    binding.setDataBean(JobDetail.getData());

                                    binding.tvTime.setText(Constant.getDayDifference(JobDetail.getData().getCrd(), JobDetail.getData().getCurrentDateTime()));

                                } else {
                                    progressDialog.dismiss();
                                    binding.tvMsg.setVisibility(View.VISIBLE);
                                    binding.subMainLayout.setVisibility(View.GONE);
                                    binding.tvMsg.setText(message);
                                    //Constant.snackBar(binding.detailMainLayout, message);
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, anError.getErrorDetail());
                            Constant.errorHandle(anError,MyOnGoingJobDetailClientActivity.this);
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void getCheckOutId(String workingdays, Dialog dialog) {
        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + CONFIRM_PAYMENT_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("project_id", jobId)
                    .addBodyParameter("working_days", workingdays)
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
                                    dialog.dismiss();
                                    String checkoutId = response.getString("checkout_id");
                                    String budget = response.getString("amount");
                                    String currency = response.getString("currency");

                                     Intent intent = new Intent(MyOnGoingJobDetailClientActivity.this, MakePaymentActivity.class);
                                    intent.putExtra("OngoingJobPayment", "OngoingJob");
                                    intent.putExtra("NameKey", binding.tvName.getText().toString());
                                    intent.putExtra("PaymentKey", offer);
                                    intent.putExtra("JobIdKey", jobId);
                                    intent.putExtra("UserIdKey", userId);
                                    intent.putExtra("WorkDays", workingdays);
                                    intent.putExtra("CurrencyKey", currency);
                                    intent.putExtra("checkoutIdKey", checkoutId);
                                    intent.putExtra("amountKey", budget);
                                    intent.putExtra("Hours", hours);
                                    startActivity(intent);

                                    /*intent.putExtra("CurrencyKey", currency);
                                    intent.putExtra("checkoutIdKey", checkoutId);
                                    intent.putExtra("JobIdKey", JobId);*/
                                    //startActivity(intent);
                                } else {
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                    Constant.snackBar(binding.detailMainLayout, message);
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.getMessage());
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            dialog.dismiss();
                            Constant.errorHandle(anError,MyOnGoingJobDetailClientActivity.this);
                            Log.d(TAG, anError.getErrorDetail());
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}

