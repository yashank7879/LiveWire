package com.livewire.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivitySingleJobCompleteJobDetailBinding;
import com.livewire.responce.JobDetailClientResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API;

public class SingleJobCompleteJobDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SingleJobCompleteJobDetailActivity.class.getName();
    ActivitySingleJobCompleteJobDetailBinding binding;
    ProgressDialog progressDialog;
    private String jobId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_job_complete_job_detail);
        progressDialog = new ProgressDialog(this);
        binding.ivBack.setOnClickListener(this);

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
                                    binding.tvDate.setText(Constant.DateFomatChange(dataBean.getData().getJob_start_date()).substring(0, 2) + " ");
                                    binding.tvDateMonth.setText(Constant.DateFomatChange(dataBean.getData().getJob_start_date()).substring(3));
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

    private void setMyJobDetails(JobDetailClientResponce.DataBean data) {
        Picasso.with(binding.ivProfileImg.getContext())
                .load(data.getRequestedUserData()
                        .get(0).getProfileImage()).fit().into(binding.ivProfileImg);

        binding.tvName.setText(data.getRequestedUserData().get(0).getName());
        binding.tvDistance.setText(data.getRequestedUserData().get(0).getDistance_in_km() + " Km away");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
        }
    }
}
