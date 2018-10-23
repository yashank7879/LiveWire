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
import com.livewire.responce.MyjobResponceClient;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.loopeer.shadow.ShadowView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.livewire.utils.ApiCollection.BASE_URL;

public class MyOnGoingJobDetailClientActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MyOnGoingJobDetailClientActivity.class.getName() ;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ongoing_job_detail_client);
        intializeView();
        jobDetailApi();
    }



    private void intializeView() {
        progressDialog = new ProgressDialog(this);
       findViewById(R.id.iv_back).setOnClickListener(this);
        detailMainLayout = (ScrollView) findViewById(R.id.detail_main_layout);
        actionBar = (ShadowView) findViewById(R.id.action_bar);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        detailsId = (ShadowView) findViewById(R.id.details_id);
        tvRequest = (TextView) findViewById(R.id.tv_request);
        rlUserData = (RelativeLayout) findViewById(R.id.rl_user_data);
        ivProfileImg = (CircleImageView) findViewById(R.id.iv_profile_img);
        tvName = (TextView) findViewById(R.id.tv_name);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        llKmAway = (LinearLayout) findViewById(R.id.ll_km_away);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
        tvNoRequest = (TextView) findViewById(R.id.tv_no_request);
        tvTime = (TextView) findViewById(R.id.tv_time);
        llChat = (LinearLayout) findViewById(R.id.ll_chat);
        tvChat = (TextView) findViewById(R.id.tv_chat);
        tvDetailsOfHelp = (TextView) findViewById(R.id.tv_details_of_help);
        tvJobStatus = (TextView) findViewById(R.id.tv_job_status);
        tvCategory = (TextView) findViewById(R.id.tv_category);
        tvSubCategory = (TextView) findViewById(R.id.tv_sub_category);
        rlRange = (RelativeLayout) findViewById(R.id.rl_range);
        tvOfferRate = (TextView) findViewById(R.id.tv_offer_rate);
        tvOfferPrice = (TextView) findViewById(R.id.tv_offer_price);
        rateRange = (TextView) findViewById(R.id.rate_range);
        tvRangePrice = (TextView) findViewById(R.id.tv_range_price);
        hr1 = (TextView) findViewById(R.id.hr1);
        rlMoredetail = (RelativeLayout) findViewById(R.id.rl_moredetail);
        tvMoreDeatail = (TextView) findViewById(R.id.tv_more_deatail);
        startDate = (TextView) findViewById(R.id.start_date);
        tvStartDate = (TextView) findViewById(R.id.tv_start_date);
        endDate = (TextView) findViewById(R.id.end_date);
        tvEndDate = (TextView) findViewById(R.id.tv_end_date);
        weekDays = (TextView) findViewById(R.id.week_days);
        tvWeekDays = (TextView) findViewById(R.id.tv_week_days);
        timeDuration = (TextView) findViewById(R.id.time_duration);
        tvTimeDuration = (TextView) findViewById(R.id.tv_time_duration);
        description = (TextView) findViewById(R.id.description);
        tvDescription = (TextView) findViewById(R.id.tv_description);

        if (getIntent().getSerializableExtra("MyJobDetail") != null){
            MyjobResponceClient.DataBean dataBean = (MyjobResponceClient.DataBean) getIntent().getSerializableExtra("MyJobDetail");
            setMyJobDetails(dataBean);
            tvCategory.setText(dataBean.getParent_category());
            tvSubCategory.setText(dataBean.getSub_category());
            tvOfferPrice.setText("$ "+dataBean.getJob_offer());
            tvStartDate.setText(dataBean.getJob_start_date());
            tvEndDate.setText(dataBean.getJob_start_date());
            tvDescription.setText(dataBean.getJob_description());
            tvTime.setText(Constant.getDayDifference(dataBean.getCrd(),dataBean.getCurrentTime()));
        }
    }

    private void setMyJobDetails(MyjobResponceClient.DataBean dataBean) {
      if (dataBean.getJob_type().equals("2")){///"""""""""" ONGOING JOB
            if (dataBean.getTotal_request().equals("0")){  // NO OFFER SEND YET
                rlUserData.setVisibility(View.GONE);
                rlMoredetail.setVisibility(View.VISIBLE);
                tvNoRequest.setText(R.string.no_offer_request_yet);
                tvNoRequest.setVisibility(View.VISIBLE);

            }else if (dataBean.getTotal_request().equals("1")) {
                // JOB CONFIRM OR PENDING REQUEST OR IN PROGRESS
                rlUserData.setVisibility(View.VISIBLE);
                rlMoredetail.setVisibility(View.VISIBLE);
                tvNoRequest.setVisibility(View.GONE);
                tvName.setText(dataBean.getName());
                tvDistance.setText(dataBean.getRequestedUserData().get(0).getDistance_in_km()+" Km away");

                Picasso.with(ivProfileImg.getContext())
                        .load(dataBean.getRequestedUserData()
                                .get(0).getProfileImage()).fit().into(ivProfileImg);


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


              tvRangePrice.setText(builder);

            }

            }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
                default:
        }
    }

    private void jobDetailApi() {
        if (Constant.isNetworkAvailable(this,detailMainLayout)){
            progressDialog.show();
            AndroidNetworking.post(BASE_URL+"Jobpost/getJobDetail")
                    .addHeaders("authToken", PreferenceConnector.readString(this,PreferenceConnector.AUTH_TOKEN,""))
                    .addBodyParameter("job_id")
                    .addBodyParameter("job_type")
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

                                }else {
                                    progressDialog.dismiss();

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

