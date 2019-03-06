package com.livewire.ui.activity.complete_confirm_job_worker;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivityNotificationJobHelpOfferedDetailWorkerBinding;
import com.livewire.responce.JobDetailWorkerResponce;
import com.livewire.ui.activity.ClientProfileDetailWorkerActivity;
import com.livewire.ui.activity.chat.ChattingActivity;
import com.livewire.ui.dialog.ReviewDialog;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.livewire.utils.ApiCollection.ADD_REVIEW_API;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_WORKER_JOB_DETAIL_API;

public class CompleteJobHelpOfferedDetailWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityNotificationJobHelpOfferedDetailWorkerBinding binding;
    private ProgressDialog progressDialog;
    private String userId = "";
    private String jobId = "";
    private String name = "";
    private String clientProfileImg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_job_help_offered_detail_worker);
        intializeViews();
        if (getIntent().getStringExtra("JobIdKey") != null) {
            jobId = getIntent().getStringExtra("JobIdKey");
            getJobDetailApi();
            binding.btnSendRequest.setVisibility(View.GONE);
            binding.tvJobReview.setVisibility(View.VISIBLE);
            binding.tvJobStatus.setVisibility(View.VISIBLE);
            binding.rlUserData.setVisibility(View.VISIBLE);


            if (getIntent().getStringExtra("CompleteJobKey") != null) {
                binding.tvOfferRate.setText(R.string.paid_amount);
                binding.tvOfferRate.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
                binding.paymentInfoLayout.setVisibility(View.VISIBLE);
                binding.btnGiveReview.setVisibility(View.VISIBLE);

            }
        }

    }

    private void intializeViews() {
        progressDialog = new ProgressDialog(this);
        binding.btnSendRequest.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        binding.btnGiveReview.setOnClickListener(this);
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
                                    JobDetailWorkerResponce workerResponce = new Gson().fromJson(String.valueOf(response), JobDetailWorkerResponce.class);
                                    binding.setJobDetail(workerResponce.getData());

                                    setWorkerDataResponce(workerResponce);

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

    //"""""""""set job deatil worker response data """""""""""
    private void setWorkerDataResponce(JobDetailWorkerResponce jobDetail) {
        userId = jobDetail.getData().getUserId();
        clientProfileImg = jobDetail.getData().getProfileImage();
        binding.tvTime.setText(Constant.getDayDifference(jobDetail.getData().getCrd(), jobDetail.getData().getCurrentDateTime()));

        Picasso.with(binding.ivProfileImg.getContext())
                .load(jobDetail.getData().getProfileImage())
                .fit().into(binding.ivProfileImg);

        if (!jobDetail.getData().getRating().isEmpty()) {
            binding.ratingBar.setRating(Float.parseFloat(jobDetail.getData().getRating()));
        }

        name = jobDetail.getData().getName();
        for (JobDetailWorkerResponce.ReviewBean reviewBean : jobDetail.getReview()) {
            if (reviewBean.getReview_by().equals(PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, ""))) {
                binding.tvJobReview.setVisibility(View.VISIBLE);
                binding.reviewByRl.setVisibility(View.VISIBLE);
                binding.tvReviewTime.setText(Constant.getDayDifference(reviewBean.getCrd(), jobDetail.getData().getCurrentDateTime()));
                binding.tvReviewDescription.setText(reviewBean.getReview_description());
                binding.ratingBarReview.setRating(Float.parseFloat(reviewBean.getRating()));
            } else {
                binding.tvJobReview.setVisibility(View.VISIBLE);
                binding.reviewUserRl.setVisibility(View.VISIBLE);
                binding.tvUserNameReview.setText(jobDetail.getData().getName());
                binding.tvUserTimeReview.setText(Constant.getDayDifference(reviewBean.getCrd(), jobDetail.getData().getCurrentDateTime()));
                binding.tvReviewUserDescription.setText(reviewBean.getReview_description());
                binding.ratingBarUserReview.setRating(Float.parseFloat(reviewBean.getRating()));
            }
        }

        if (jobDetail.getData().getReview_status().equals("1")) {
            binding.btnGiveReview.setVisibility(View.GONE);
        }

        float adminCommision = (Float.parseFloat(jobDetail.getData().getJob_budget()) * 3) / 100;
        float paidToYou = Float.parseFloat(jobDetail.getData().getJob_budget()) - adminCommision;
        binding.tvAdminCommission.setText(MessageFormat.format("${0}", adminCommision));
        binding.tvPaidToYou.setText(MessageFormat.format("${0}", paidToYou));
        binding.tvTotalPrice.setText("$" + jobDetail.getData().getJob_budget() + ".0");

        //********"2018-07-04" date format converted into "04 jul 2018"***********//
        DateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String start = jobDetail.getData().getJob_start_date();

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
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_give_review:
                openReviewDialog();
                break;
            case R.id.ll_chat: {
                intent = new Intent(this, ChattingActivity.class);
                intent.putExtra("otherUID", userId);
                intent.putExtra("titleName", binding.tvName.getText().toString().trim());
                intent.putExtra("profilePic", clientProfileImg);
                startActivity(intent);
            }
            break;
            case R.id.rl_user_data: {
                if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
                    intent = new Intent(this, ClientProfileDetailWorkerActivity.class);
                    intent.putExtra("UserIdKey", userId);
                    startActivity(intent);
                }

            }
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
                            binding.tvReviewTime.setText("just now");
                            binding.btnGiveReview.setVisibility(View.GONE);
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


}

