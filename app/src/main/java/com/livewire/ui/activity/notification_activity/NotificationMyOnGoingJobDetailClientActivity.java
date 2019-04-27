package com.livewire.ui.activity.notification_activity;

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
import android.widget.Button;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;

import com.livewire.databinding.ActivityNotificationMyOngoingJobDetailClientBinding;
import com.livewire.responce.JobDetailClientResponce;
import com.livewire.ui.activity.ClientMainActivity;
import com.livewire.ui.activity.NearYouClientActivity;
import com.livewire.ui.activity.WorkerMainActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API;

public class NotificationMyOnGoingJobDetailClientActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityNotificationMyOngoingJobDetailClientBinding binding;
    private static final String TAG = NotificationMyOnGoingJobDetailClientActivity.class.getName();
    private ProgressDialog progressDialog;
    private Button btnSendOffer;
    private String jobId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_my_ongoing_job_detail_client);
        intializeView();
       // jobDetailApi();
    }

    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
       assert extras != null;
        String userType =  extras.getString("for_user_type");
        //"""""""""" check the user type and then go to the activivty """""//
        if (PreferenceConnector.readString(this, PreferenceConnector.USER_MODE, "").equals(userType)) {
            if (extras != null && (extras.getString("type").equals("Ongoing_job_accepted") || extras.getString("type").equals("Ongoing_job_rejected"))) {
                jobId = extras.getString("reference_id");
            /* MyjobResponceClient.DataBean dataBean = (MyjobResponceClient.DataBean) getIntent().getSerializableExtra("MyJobDetail");
                setMyJobDetails(dataBean);
                binding.setDataBean(dataBean);
                binding.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentDateTime()));*/
            } else if (extras != null && extras.getString("type").equals("Ongoing_job_completed")) {
                jobId = extras.getString("reference_id");
            }
        }else {
            Intent intent = new Intent(this, WorkerMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("MyProfile", "MyProfile");
            startActivity(intent);
            finish();
        }
        jobDetailApi();
    }

    private void setMyJobDetails(final JobDetailClientResponce.DataBean dataBean) {
        if (dataBean.getJob_type().equals("2")) {///"""""""""" ONGOING JOB
            if (dataBean.getTotal_request().equals("0")) {  // NO OFFER SEND YET
                binding.btnSendOffer.setVisibility(View.VISIBLE);
                binding.rlMoredetail.setVisibility(View.VISIBLE);
                binding.tvNoRequest.setVisibility(View.VISIBLE);
                binding.tvJobStatus.setVisibility(View.GONE);
                binding.llChat.setVisibility(View.GONE);
                binding.rlUserData.setVisibility(View.GONE);
                binding.rlRange.setVisibility(View.GONE);

                binding.tvNoRequest.setText(R.string.no_offer_application_yet);

                binding.btnSendOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String jobId = dataBean.getJobId();
                        Intent intent = new Intent(NotificationMyOnGoingJobDetailClientActivity.this, NearYouClientActivity.class);
                        intent.putExtra("JobIdKey", jobId);
                        startActivity(intent);
                    }
                });

            } else if (dataBean.getTotal_request().equals("1")) {
                // JOB CONFIRM OR PENDING REQUEST OR IN PROGRESS

                switch (dataBean.getJob_confirmed()) {
                    case "0": // request pending job
                        if (dataBean.getRequestedUserData().get(0).getRequest_status().equals("0")) {
                            binding.tvJobStatus.setText(R.string.Work_offer_pending);
                            binding.tvJobStatus.setBackground(getResources().getDrawable(R.drawable.doteted_orange_shape));
                            binding.tvJobStatus.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));

                        } else if (dataBean.getRequestedUserData().get(0).getRequest_status().equals("2")) {
                            binding.tvJobStatus.setText(R.string.request_cancel);
                            binding.tvJobStatus.setBackground(getResources().getDrawable(R.drawable.doteted_balck_shape));
                            binding.tvJobStatus.setTextColor(ContextCompat.getColor(this, R.color.colorDarkBlack));
                        }
                        break;
                    case "1":  //request_confirmed job
                        if (dataBean.getRequestedUserData().get(0).getRequest_status().equals("1")) {
                            binding.tvJobStatus.setText(R.string.application_confirmed);
                            binding.tvJobStatus.setBackground(getResources().getDrawable(R.drawable.doteted_green_shape));
                            binding.tvJobStatus.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
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


                // binding.tvName.setText(dataBean.getRequestedUserData().get(0).getName());

                binding.tvDistance.setText(dataBean.getRequestedUserData().get(0).getDistance_in_km() + " Km away");

                Picasso.with(binding.ivProfileImg.getContext())
                        .load(dataBean.getRequestedUserData()
                                .get(0).getProfileImage()).fit().into(binding.ivProfileImg);


                SpannableStringBuilder builder = new SpannableStringBuilder();
                SpannableString minprice = new SpannableString("R" + dataBean.getRequestedUserData().get(0).getMin_rate());
                minprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, minprice.length(), 0);
                //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
                builder.append(minprice);
                SpannableString toString = new SpannableString(" to ");
                builder.append(toString);


                SpannableString maxprice = new SpannableString("R" + dataBean.getRequestedUserData().get(0).getMax_rate());
                maxprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, maxprice.length(), 0);
                //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
                builder.append(maxprice);


                binding.tvRangePrice.setText(builder);

            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
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
                                    JobDetailClientResponce JobDetail = new Gson().fromJson(String.valueOf(response), JobDetailClientResponce.class);
                                    setMyJobDetails(JobDetail.getData());
                                    binding.setDataBean(JobDetail.getData());

                                    binding.tvTime.setText(Constant.getDayDifference(JobDetail.getData().getCrd(), JobDetail.getData().getCurrentDateTime()));

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

