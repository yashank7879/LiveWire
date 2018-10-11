package com.livewire.ui.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.responce.HelpOfferedResponce;
import com.livewire.responce.JobDetailWorkerResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.loopeer.shadow.ShadowView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.livewire.utils.ApiCollection.BASE_URL;

public class JobDetailWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivBack;
    private CircleImageView ivProfileImg;
    private TextView tvName;
    private RatingBar ratingBar;
    private TextView tvDistance;
    private TextView tvTime;
    private TextView tvMoreInfo;
    private TextView tvCategory;
    private TextView tvDate;
    private TextView tvDateMonth;
    private TextView tvSubCategory;
    private TextView tvBudgetPrice;
    private TextView tvDescription;
    private RelativeLayout mainLayout;
    private ProgressDialog progressDialog;
    private Button btnSendRequest;
    private String jobId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail_worker);
        intializeViews();

        if (getIntent().getSerializableExtra("JobIdKey") != null) {
            HelpOfferedResponce.DataBean jobDetail = (HelpOfferedResponce.DataBean) getIntent().getSerializableExtra("JobIdKey");
            setWorkerDataResponce(jobDetail);
        }
    }

    private void intializeViews() {
        progressDialog = new ProgressDialog(this);
        ivProfileImg = findViewById(R.id.iv_profile_img);
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
        btnSendRequest = findViewById(R.id.btn_send_request);
        btnSendRequest.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);

    }

  /*  private void jobDetailApi(HelpOfferedResponce.DataBean jobDetail) {
        if (Constant.isNetworkAvailable(this,mainLayout)){
            progressDialog.show();
            AndroidNetworking.post(BASE_URL+"Jobpost/getJobDetail")
                    .addBodyParameter("job_id",jobId)
                    .addBodyParameter("job_type","1")
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
                                    JobDetailWorkerResponce workerResponce = new Gson().fromJson(String.valueOf(response),JobDetailWorkerResponce.class);
                                    setWorkerDataResponce(workerResponce);
                                }else {
                                    Constant.snackBar(mainLayout,message);
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

    private void setWorkerDataResponce(HelpOfferedResponce.DataBean jobDetail) {
        jobId = jobDetail.getJobId();
        tvName.setText(jobDetail.getName());
        tvDistance.setText(jobDetail.getDistance_in_km() + " Km away");
        tvCategory.setText(jobDetail.getParentCategoryName());
        tvSubCategory.setText(jobDetail.getSubCategoryName());
        tvBudgetPrice.setText("$ " + jobDetail.getJob_budget());
        tvDescription.setText(jobDetail.getJob_description());
        tvTime.setText(Constant.getDayDifference(jobDetail.getCrd(), jobDetail.getCurrentTime()));
        Picasso.with(ivProfileImg.getContext()).load(jobDetail.getProfileImage()).fit().into(ivProfileImg);

        //********"2018-07-04" date format converted into "04 jul 2018"***********//
        DateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String start = jobDetail.getJob_start_date();

        try {
            Date newStartDate;
            newStartDate = sd.parse(start);
            sd = new SimpleDateFormat("dd MMM yyyy");
            start = sd.format(newStartDate);

            tvDate.setText(start.substring(0, 2) + " ");
            tvDateMonth.setText(start.substring(3));
            // holder.tvDate.setText(start);
        } catch (ParseException e) {
            Log.e("Tag", e.getMessage());
        }

        if (jobDetail.getJob_confirmed().equals("0")) { // pending request
            btnSendRequest.setBackground(null);
            btnSendRequest.setText(R.string.pending_request);
            btnSendRequest.setTextColor(ContextCompat.getColor(this, R.color.colorOrange));
            btnSendRequest.setClickable(false);
     /*   } else if (jobDetail.getJob_confirmed().equals("1")) {// accepted
            btnSendRequest.setClickable(false);
        } else if (jobDetail.getJob_confirmed().equals("2")) {// job not accepted
            btnSendRequest.setClickable(false);*/
        } else if (jobDetail.getJob_confirmed().equals("3")) {// job not send

        }
        // tvDate.setText(Constant.getDayDifference(workerResponce.getData().get(0).getCrd(),workerResponce.getData().get(0).getCurrentTime()) );
        // tvDateMonth.setText(Constant.getDayDifference(workerResponce.getData().get(0).getCrd(),workerResponce.getData().get(0).getCurrentTime()) );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_request:
                sendRequestApi(jobId);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
        }
    }

    private void sendRequestApi(String jobId) {
        if (Constant.isNetworkAvailable(this , mainLayout)){
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "Jobpost/sendRequest")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("request_to", "2")
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
                            btnSendRequest.setBackground(null);
                            btnSendRequest.setText(R.string.pending_request);
                            btnSendRequest.setTextColor(ContextCompat.getColor(JobDetailWorkerActivity.this, R.color.colorOrange));
                            btnSendRequest.setClickable(false);
                            Constant.snackBar(mainLayout, message);
                        } else {
                            Constant.snackBar(mainLayout, message);
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

