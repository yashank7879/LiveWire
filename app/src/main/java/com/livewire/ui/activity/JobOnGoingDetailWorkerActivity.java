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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.livewire.ui.dialog.ReviewDialog;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.ADD_REVIEW_API;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_WORKER_JOB_DETAIL_API;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_REQUEST_2_API;

public class JobOnGoingDetailWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityJobOngoingDetailWorkerBinding binding;
    private static final String TAG = JobOnGoingDetailWorkerActivity.class.getName();

    private ProgressDialog progressDialog;
    private ScrollView detailMainLayout;
    private JobDetailWorkerResponce workerResponcd;
    private String jobId = "";
    private String userId = "";
    private String clientProfileImg = "";
    private String name = "";

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
        binding.btnGiveReview.setOnClickListener(this);

        if (getIntent().getSerializableExtra("JobIdKey") != null) {
            jobId = getIntent().getStringExtra("JobIdKey");
            getJobDetailApi();
        }
    }


    private void getJobDetailApi() {

        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + JOBPOSTSEND_GET_WORKER_JOB_DETAIL_API)
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("job_type", "2")
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
                                Log.e(TAG, "onResponse: " + message);

                                if (status.equals("success")) {
                                    binding.tvMsg.setVisibility(View.GONE);
                                    binding.subMainLayout.setVisibility(View.VISIBLE);
                                    workerResponcd = new Gson().fromJson(String.valueOf(response), JobDetailWorkerResponce.class);
                                    binding.setWorkerResponcd(workerResponcd.getData());
                                    setJobDetailData(workerResponcd);
                                } else {
                                    binding.tvMsg.setVisibility(View.VISIBLE);
                                    binding.subMainLayout.setVisibility(View.GONE);
                                    binding.tvMsg.setText(message);
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
                if (PreferenceConnector.readString(this,PreferenceConnector.AVAILABILITY_1,"").equals("1")) {// check avaialability
                    if (PreferenceConnector.readString(this,PreferenceConnector.IS_BANK_ACC,"").equals("1")) {
                        acceptRejectrequestApi(workerResponcd.getData().getUserId(), workerResponcd.getData().getJobId(), "2");
                    }else {
                        showAddBankAccountDialog();
                    }
                }else{
                    Constant.showAvaialabilityAlert(this);
                }
                /*if (PreferenceConnector.readString(this,PreferenceConnector.IS_BANK_ACC,"").equals("1")) {
                    acceptRejectrequestApi(workerResponcd.getUserId(), workerResponcd.getJobId(), "2");
                }else {
                    showAddBankAccountDialog();
                }*/
                break;
            case R.id.btn_accept:
                if (PreferenceConnector.readString(this,PreferenceConnector.AVAILABILITY_1,"").equals("1")) {// check avaialability
                    if (PreferenceConnector.readString(this,PreferenceConnector.IS_BANK_ACC,"").equals("1")) {
                        acceptRejectrequestApi(workerResponcd.getData().getUserId(), workerResponcd.getData().getJobId(), "1");
                    }else {
                        showAddBankAccountDialog();
                    }
                }else{
                    Constant.showAvaialabilityAlert(this);
                }

                /* if (PreferenceConnector.readString(this,PreferenceConnector.IS_BANK_ACC,"").equals("1")) {
                    acceptRejectrequestApi(workerResponcd.getUserId(), workerResponcd.getJobId(), "1");
                }else {
                    showAddBankAccountDialog();
                }*/
                break;
            case R.id.ll_chat: {
                Intent intent = new Intent(this, ChattingActivity.class);
                intent.putExtra("otherUID", userId);
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
            case R.id.btn_give_review:
                openReviewDialog();
                break;
            default:
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
                            binding.btnGiveReview.setVisibility(View.GONE);
                            binding.tvReviewTime.setText("just now");
                            binding.tvJobReview.setVisibility(View.VISIBLE);

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



    private void showAddBankAccountDialog() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Add Bank Account");
        builder.setMessage("Please add your bank details first.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(JobOnGoingDetailWorkerActivity.this, AddBankAccountActivity.class);
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

    private void setJobDetailData(JobDetailWorkerResponce workerResponcd) {

        userId = workerResponcd.getData().getUserId();
        clientProfileImg = workerResponcd.getData().getProfileImage();

        binding.tvTime.setText(Constant.getDayDifference(workerResponcd.getData().getCrd(), workerResponcd.getData().getCurrentDateTime()));

        binding.tvDistance.setText(workerResponcd.getData().getDistance_in_km() + " Km");

        Picasso.with(binding.ivProfileImg.getContext())
                .load(workerResponcd.getData().getProfileImage()).fit().into(binding.ivProfileImg);

        if (!workerResponcd.getData().getRating().isEmpty()) {
            binding.ratingBar.setRating(Float.parseFloat(workerResponcd.getData().getRating()));
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString minprice = new SpannableString("R" + workerResponcd.getData().getMin_rate());
     /*   minprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, minprice.length(), 0);
        //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(minprice);
        SpannableString toString = new SpannableString(" to ");
        builder.append(toString);


        SpannableString maxprice = new SpannableString("R " + workerResponcd.getData().getMax_rate());
        maxprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, maxprice.length(), 0);
        //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(maxprice);*/

        binding.tvRangePrice.setText(minprice);

        switch (workerResponcd.getData().getJob_confirmed()) {
            case "0":
                binding.acceptRejectLayout.setVisibility(View.VISIBLE);
                break;
            case "1":
                binding.acceptRejectLayout.setVisibility(View.GONE);
                break;
            case "4":
                binding.acceptRejectLayout.setVisibility(View.GONE);
                binding.paymentInfoLayout.setVisibility(View.VISIBLE);
                binding.rlPaidAmount.setVisibility(View.VISIBLE);
                name = workerResponcd.getData().getName();
                for (JobDetailWorkerResponce.ReviewBean reviewBean : workerResponcd.getReview()) {
                    if (reviewBean.getReview_by().equals(PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, ""))) {
                        binding.tvJobReview.setVisibility(View.VISIBLE);
                        binding.reviewByRl.setVisibility(View.VISIBLE);
                        binding.tvReviewTime.setText(Constant.getDayDifference(reviewBean.getCrd(), workerResponcd.getData().getCurrentDateTime()));
                        binding.tvReviewDescription.setText(reviewBean.getReview_description());
                        binding.ratingBarReview.setRating(Float.parseFloat(reviewBean.getRating()));
                    } else {
                        binding.tvJobReview.setVisibility(View.VISIBLE);
                        binding.reviewUserRl.setVisibility(View.VISIBLE);
                        binding.tvUserNameReview.setText(workerResponcd.getData().getName());
                        binding.tvUserTimeReview.setText(Constant.getDayDifference(reviewBean.getCrd(), workerResponcd.getData().getCurrentDateTime()));
                        binding.tvReviewUserDescription.setText(reviewBean.getReview_description());
                        binding.ratingBarUserReview.setRating(Float.parseFloat(reviewBean.getRating()));
                    }
                }
                if (workerResponcd.getData().getReview_status().equals("0")) {
                    binding.btnGiveReview.setVisibility(View.VISIBLE);
                }

                if (workerResponcd.getData().getJob_confirmed().equals("4")) {
                    binding.tvTotalDays.setText(workerResponcd.getData().getNumber_of_days());
                    binding.tvOfferPriceValue.setText("R" + workerResponcd.getData().getJob_offer());
                    float totalPaid = (Float.parseFloat(workerResponcd.getData().getJob_offer()) * Float.parseFloat(workerResponcd.getData().getNumber_of_days()) * Float.parseFloat(workerResponcd.getData().getJob_time_duration()));
                    float adminCommision = (totalPaid * 3) / 100;
                    //binding.tvCommisionPrice.setText("$" + adminCommision);
                    binding.tvTotalPrice.setText("R" + totalPaid);
                    binding.tvAmount.setText("R" + totalPaid);
                }
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

