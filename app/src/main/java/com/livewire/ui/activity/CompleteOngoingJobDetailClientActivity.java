package com.livewire.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
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
import com.livewire.databinding.ActivityCompleteOngoingJobDetailClientBinding;
import com.livewire.responce.JobDetailClientResponce;
import com.livewire.responce.JobDetailWorkerResponce;
import com.livewire.ui.dialog.ReviewDialog;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

import static com.livewire.utils.ApiCollection.ADD_REVIEW_API;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API;

public class CompleteOngoingJobDetailClientActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CompleteOngoingJobDetailClientActivity.class.getName();
    ActivityCompleteOngoingJobDetailClientBinding binding;
    private ProgressDialog progressDialog;
    private String jobId="";
    private String name="";
    private String userId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complete_ongoing_job_detail_client);
        progressDialog = new ProgressDialog(this);
        binding.ivBack.setOnClickListener(this);
        binding.btnGiveReview.setOnClickListener(this);
        binding.rlUserData.setOnClickListener(this);
        if (getIntent().getStringExtra("JobIdKey") != null) {
            jobId = getIntent().getStringExtra("JobIdKey");
        }
        jobDetailApi();
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
                                    JobDetailClientResponce dataBean = new Gson().fromJson(String.valueOf(response), JobDetailClientResponce.class);
                                    setMyJobDetails(dataBean);
                                    binding.setJobDetail(dataBean.getData());

                                  userId = dataBean.getData().getRequestedUserData().get(0).getUserId();
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

    private void setMyJobDetails(JobDetailClientResponce data) {
        Picasso.with(binding.ivProfileImg.getContext()).load(data.getData().getRequestedUserData()
                .get(0).getProfileImage())
                .fit().into(binding.ivProfileImg);

        binding.tvStartDate.setText(Constant.DateFomatChange(data.getData().getJob_start_date()));
        binding.tvEndDate.setText(Constant.DateFomatChange(data.getData().getJob_end_date()));
        binding.tvTime.setText(Constant.getDayDifference(data.getData().getCrd(), data.getData().getCurrentDateTime()));


        float totalPrice = Float.parseFloat(data.getData().getJob_time_duration()) *
                Float.parseFloat(data.getData().getNumber_of_days())
                * Float.parseFloat(data.getData().getJob_offer());
        binding.tvTotalPrice.setText("R"+totalPrice);
        name =data.getData().getRequestedUserData().get(0).getName();
        for (JobDetailClientResponce.ReviewBean reviewBean : data.getReview()) {
            if (reviewBean.getReview_by().equals(PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, ""))) {
                binding.tvJobReview.setVisibility(View.VISIBLE);
                binding.btnGiveReview.setVisibility(View.GONE);
                binding.reviewByRl.setVisibility(View.VISIBLE);
                binding.tvReviewTime.setText(Constant.getDayDifference(reviewBean.getCrd(), data.getData().getCurrentDateTime()));
                binding.tvReviewDescription.setText(reviewBean.getReview_description());
                binding.ratingBarReview.setRating(Float.parseFloat(reviewBean.getRating()));
            } else {
                binding.tvJobReview.setVisibility(View.VISIBLE);
                binding.reviewUserRl.setVisibility(View.VISIBLE);
                binding.btnGiveReview.setVisibility(View.VISIBLE);
                binding.tvUserNameReview.setText(data.getData().getRequestedUserData().get(0).getName());
                binding.tvUserTimeReview.setText(Constant.getDayDifference(reviewBean.getCrd(), data.getData().getCurrentDateTime()));
                binding.tvReviewUserDescription.setText(reviewBean.getReview_description());
                binding.ratingBarUserReview.setRating(Float.parseFloat(reviewBean.getRating()));
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_give_review:
                openReviewDialog();
                break;
            case R.id.rl_user_data:
               Intent intent = new Intent(this, WorkerProfileDetailClientActivity.class);
                intent.putExtra("UserIdKey", userId);
                startActivity(intent);
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

}
