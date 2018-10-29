package com.livewire.ui.activity;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.livewire.R;
import com.livewire.databinding.ActivityJobOngoingDetailWorkerBinding;
import com.livewire.responce.OnGoingWorkerResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;

public class JobOnGoingDetailWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityJobOngoingDetailWorkerBinding binding;
    private static final String TAG = JobOnGoingDetailWorkerActivity.class.getName();

    private ProgressDialog progressDialog;
    private ScrollView detailMainLayout;
    private OnGoingWorkerResponce.DataBean workerResponcd;

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

        if (getIntent().getSerializableExtra("JobDetail") != null) {
            workerResponcd = (OnGoingWorkerResponce.DataBean) getIntent().getSerializableExtra("JobDetail");
            setJobDetailData(workerResponcd);
            binding.setWorkerResponcd(workerResponcd);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_ignore:
                acceptRejectrequestApi(workerResponcd.getUserId(), workerResponcd.getJobId(), "2");
                break;
            case R.id.btn_accept:
                acceptRejectrequestApi(workerResponcd.getUserId(), workerResponcd.getJobId(), "1");
                break;
            default:
        }
    }

    private void setJobDetailData(OnGoingWorkerResponce.DataBean workerResponcd) {

        binding.tvTime.setText(Constant.getDayDifference(workerResponcd.getCrd(), workerResponcd.getCurrentDateTime()));

        binding.tvDistance.setText(workerResponcd.getDistance_in_km() + " Km away");

        Picasso.with(binding.ivProfileImg.getContext())
                .load(workerResponcd.getProfileImage()).fit().into(binding.ivProfileImg);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString minprice = new SpannableString("$ " + workerResponcd.getMin_rate());
        minprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, minprice.length(), 0);
        //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(minprice);
        SpannableString toString = new SpannableString(" to ");
        builder.append(toString);


        SpannableString maxprice = new SpannableString("$ " + workerResponcd.getMax_rate());
        maxprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, maxprice.length(), 0);
        //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(maxprice);

        binding.tvRangePrice.setText(builder);
    }

    private void acceptRejectrequestApi(String userId, String jobId, String requestStatus) {
        if (Constant.isNetworkAvailable(this, detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "Jobpost/sendRequest2")
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

