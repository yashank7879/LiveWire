package com.livewire.ui.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.livewire.R;
import com.livewire.responce.HelpOfferedResponce;
import com.livewire.responce.MyjobResponceClient;
import com.livewire.responce.OnGoingWorkerResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.loopeer.shadow.ShadowView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.livewire.utils.ApiCollection.BASE_URL;

public class JobOnGoingDetailWorkerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = JobOnGoingDetailWorkerActivity.class.getName();
    private ShadowView actionBar;
    private ImageView ivBack;
    private ShadowView detailsId;
    private TextView tvRequest;
    private RelativeLayout rlUserData;
    private CircleImageView ivProfileImg;
    private TextView tvName;
    private RatingBar ratingBar;
    private LinearLayout llKmAway;
    private TextView tvDistance;
    private TextView tvNoRequest;
    private TextView tvTime;
    private LinearLayout llChat;
    private TextView tvChat;
    private TextView tvDetailsOfHelp;
    private TextView tvJobStatus;
    private TextView tvCategory;
    private TextView tvSubCategory;
    private RelativeLayout rlRange;
    private TextView tvOfferRate;
    private TextView tvOfferPrice;
    private TextView rateRange;
    private TextView tvRangePrice;
    private TextView hr1;
    private RelativeLayout rlMoredetail;
    private TextView tvMoreDeatail;
    private TextView startDate;
    private TextView tvStartDate;
    private TextView endDate;
    private TextView tvEndDate;
    private TextView weekDays;
    private TextView tvWeekDays;
    private TextView timeDuration;
    private TextView tvTimeDuration;
    private TextView description;
    private TextView tvDescription;
    private ProgressDialog progressDialog;
    private ScrollView detailMainLayout;
    private OnGoingWorkerResponce.DataBean workerResponcd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_ongoing_detail_worker);
        intializeView();

    }


    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        detailMainLayout = findViewById(R.id.detail_main_layout);
        actionBar = findViewById(R.id.action_bar);
        ivBack = findViewById(R.id.iv_back);
        detailsId = findViewById(R.id.details_id);
        tvRequest = findViewById(R.id.tv_request);
        rlUserData = findViewById(R.id.rl_user_data);
        ivProfileImg = findViewById(R.id.iv_profile_img);
        tvName = findViewById(R.id.tv_name);
        ratingBar = findViewById(R.id.rating_bar);
        llKmAway = findViewById(R.id.ll_km_away);
        tvDistance = findViewById(R.id.tv_distance);
        tvNoRequest = findViewById(R.id.tv_no_request);
        tvTime = findViewById(R.id.tv_time);
        llChat = findViewById(R.id.ll_chat);
        tvChat = findViewById(R.id.tv_chat);
        tvDetailsOfHelp = findViewById(R.id.tv_details_of_help);
        tvCategory = findViewById(R.id.tv_category);
        tvSubCategory = findViewById(R.id.tv_sub_category);
        rlRange = findViewById(R.id.rl_range);
        tvOfferRate = findViewById(R.id.tv_offer_rate);
        tvOfferPrice = findViewById(R.id.tv_offer_price);
        rateRange = findViewById(R.id.rate_range);
        tvRangePrice = findViewById(R.id.tv_range_price);
        hr1 = findViewById(R.id.hr1);
        rlMoredetail = findViewById(R.id.rl_moredetail);
        tvMoreDeatail = findViewById(R.id.tv_more_deatail);
        startDate = findViewById(R.id.start_date);
        tvStartDate = findViewById(R.id.tv_start_date);
        endDate = findViewById(R.id.end_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        weekDays = findViewById(R.id.week_days);
        tvWeekDays = findViewById(R.id.tv_week_days);
        timeDuration = findViewById(R.id.time_duration);
        tvTimeDuration = findViewById(R.id.tv_time_duration);
        description = findViewById(R.id.description);
        tvDescription = findViewById(R.id.tv_description);
        findViewById(R.id.btn_ignore).setOnClickListener(this);
        findViewById(R.id.btn_accept).setOnClickListener(this);

        if (getIntent().getSerializableExtra("JobDetail") != null) {
            workerResponcd = (OnGoingWorkerResponce.DataBean) getIntent().getSerializableExtra("JobDetail");
            setJobDetailData(workerResponcd);

        }
    }

    private void setMyJobDetails() {
        ///"""""""""" ONGOING JOB deatail """"""""""""""//

/*
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString minprice = new SpannableString("$ " + dataBean.getRequestedUserData().get(0).getMin_rate());
        minprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, minprice.length(), 0);
        //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(minprice);
        SpannableString toString = new SpannableString(" to ");
        builder.append(toString);

        SpannableString maxprice = new SpannableString("$ " + dataBean.getRequestedUserData().get(0).getMax_rate());
        maxprice.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkBlack)), 0, maxprice.length(), 0);
        //  userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(maxprice);

        tvRangePrice.setText(builder);*/

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
        tvCategory.setText(workerResponcd.getParentCategoryName());
        tvSubCategory.setText(workerResponcd.getSubCategoryName());
        tvOfferPrice.setText("$ " + workerResponcd.getJob_offer_rate());
        tvStartDate.setText(workerResponcd.getJob_start_date());
        tvEndDate.setText(workerResponcd.getJob_end_date());
        tvWeekDays.setText(workerResponcd.getJob_week_days());
        tvDescription.setText(workerResponcd.getJob_description());
        tvTimeDuration.setText(workerResponcd.getJob_time_duration() + " hr");
        tvTime.setText(Constant.getDayDifference(workerResponcd.getCrd(), workerResponcd.getCurrentDateTime()));

        tvName.setText(workerResponcd.getName());

        tvDistance.setText(workerResponcd.getDistance_in_km() + " Km away");

        Picasso.with(ivProfileImg.getContext())
                .load(workerResponcd.getProfileImage()).fit().into(ivProfileImg);



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


        tvRangePrice.setText(builder);
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

