package com.livewire.ui.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
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

public class MySingleJobDetailClientActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MySingleJobDetailClientActivity.class.getName();
    private ScrollView detailMainLayout;
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
    private TextView tvTime;
    private LinearLayout llChat;
    private TextView tvChat;
    private TextView tvDetailsOfHelp;
    private TextView tvJobStatus;
    private TextView tvCategory;
    private TextView tvSubCategory;
    private TextView tvBudget;
    private TextView tvBudgetPrice;
    private TextView description;
    private TextView tvMemberRequested;
    private TextView tvDescription;
    private TextView tvNorequest;
    private FrameLayout flMultiImg;
    private RelativeLayout rlMultiImg;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_single_job_detail_client);
        intializeView();
        jobDetailApi();
    }

    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        detailMainLayout = findViewById(R.id.detail_main_layout);
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
        tvTime = (TextView) findViewById(R.id.tv_time);
        llChat = (LinearLayout) findViewById(R.id.ll_chat);
        tvChat = (TextView) findViewById(R.id.tv_chat);
        tvDetailsOfHelp = (TextView) findViewById(R.id.tv_details_of_help);
        tvCategory = (TextView) findViewById(R.id.tv_category);
        tvSubCategory = (TextView) findViewById(R.id.tv_sub_category);
        tvBudget = (TextView) findViewById(R.id.tv_offer_rate);
        tvBudgetPrice = (TextView) findViewById(R.id.tv_budget_price);
        description = (TextView) findViewById(R.id.description);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        tvNorequest = (TextView) findViewById(R.id.tv_no_request);
        tvMemberRequested = (TextView) findViewById(R.id.tv_member_requested);
        flMultiImg = findViewById(R.id.fl_multi_img);
        rlMultiImg = findViewById(R.id.rl_multi_img);


        if (getIntent().getSerializableExtra("MyJobDetail") != null) {
            MyjobResponceClient.DataBean dataBean = (MyjobResponceClient.DataBean) getIntent().getSerializableExtra("MyJobDetail");
            setMyJobDetails(dataBean);
            tvCategory.setText(dataBean.getParent_category());
            tvSubCategory.setText(dataBean.getSub_category());
            tvBudgetPrice.setText("$ "+dataBean.getJob_budget());
            tvDescription.setText(dataBean.getJob_description());
            tvTime.setText(Constant.getDayDifference(dataBean.getCrd(),dataBean.getCurrentTime()));
        }
    }

    private void setMyJobDetails(MyjobResponceClient.DataBean dataBean) {
        if (dataBean.getJob_type().equals("1")) {/// """"""" SINGLE JOB
            if (dataBean.getTotal_request().equals("0")) {   // no requested yet

                tvNorequest.setVisibility(View.VISIBLE);
                rlUserData.setVisibility(View.GONE);
                rlMultiImg.setVisibility(View.GONE);
                llChat.setVisibility(View.GONE);

            } else if (dataBean.getTotal_request().equals("1") && dataBean.getJob_confirmed().equals("1")) { // jobconfirmed
                rlUserData.setVisibility(View.VISIBLE);
                llChat.setVisibility(View.VISIBLE);
                tvNorequest.setVisibility(View.GONE);
                rlMultiImg.setVisibility(View.GONE);

                Picasso.with(ivProfileImg.getContext())
                        .load(dataBean.getRequestedUserData()
                        .get(0).getProfileImage()).fit().into(ivProfileImg);

                tvName.setText(dataBean.getName());
                tvDistance.setText(dataBean.getRequestedUserData().get(0).getDistance_in_km()+" Km away");


            } else {   // multiple images show

                rlMultiImg.setVisibility(View.VISIBLE);
                rlUserData.setVisibility(View.GONE);
                tvNorequest.setVisibility(View.GONE);
                llChat.setVisibility(View.GONE);
                tvMemberRequested.setText(dataBean.getTotal_request()+" "+getString(R.string.member_requested));

                int leftMargin = 0;
                for (int i = 0; i < dataBean.getRequestedUserData().size(); i++) {
                    if (i!=0) {
                        leftMargin = leftMargin + 35;
                    }
                    addhorizontalTimeView(flMultiImg, dataBean.getRequestedUserData().get(i).getProfileImage(), leftMargin);
                }

            }

        }



        /*else if (dataBean.getJob_type().equals("2")){///"""""""""" ONGOING JOB
            if (dataBean.getTotal_request().equals("0")){  // NO OFFER SEND YET

                rlUserData.setVisibility(View.GONE);
                rlMoredetail.setVisibility(View.GONE);
                tvNorequest.setText(R.string.no_offer_request_yet);
                tvNorequest.setVisibility(View.VISIBLE);

            }else if (dataBean.getTotal_request().equals("1") && dataBean.getJob_confirmed().equals("1")) {
                // JOB CONFIRM OR PENDING REQUEST OR IN PROGRESS
                rlUserData.setVisibility(View.VISIBLE);
                rlMoredetail.setVisibility(View.VISIBLE);
                tvNorequest.setVisibility(View.GONE);
            }

            }*/
    }


    //""""""""""  add image at run time """""""""""""//
    void addhorizontalTimeView(FrameLayout linearLayout, String profileImage, int leftMargin) {
        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View v = layoutInflater.inflate(R.layout.multiple_image_cell, linearLayout, false);

        final ImageView showTime = v.findViewById(R.id.iv_profile);

        Picasso.with(showTime.getContext()).load(profileImage).fit().into(showTime);

        // Get the TextView current LayoutParams
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) showTime.getLayoutParams();

        // Set TextView layout margin 25 pixels to all side
        // Left Top Right Bottom Margin
        lp.setMargins(leftMargin, 0, 0, 0);
        showTime.setLayoutParams(lp);

        linearLayout.addView(v);
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

    private void jobDetailApi() {
        if (Constant.isNetworkAvailable(this, detailMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "Jobpost/getJobDetail")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
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

                                } else {
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
