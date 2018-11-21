package com.livewire.ui.activity.notification_activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivityNotificationMySingleJobDetailClientBinding;

import com.livewire.responce.JobDetailClientResponce;
import com.livewire.ui.activity.ClientMainActivity;

import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API;

public class NotificationMySingleJobDetailClientActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityNotificationMySingleJobDetailClientBinding binding;
    private static final String TAG = NotificationMySingleJobDetailClientActivity.class.getName();

    private ProgressDialog progressDialog;
    private String jobId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_my_single_job_detail_client);
        intializeView();
        jobDetailApi();
    }

    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        binding.ivBack.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        String type = extras.getString("type");
        if (type.equals("Once_job_request")) {
            jobId = extras.getString("reference_id");
            jobDetailApi();

          /*  MyjobResponceClient.DataBean dataBean = (MyjobResponceClient.DataBean) getIntent().getSerializableExtra("MyJobDetail");
            setMyJobDetails(dataBean);
            binding.setJobDetail(dataBean);

            binding.tvCategory.setText(dataBean.getParent_category());
            binding.tvSubCategory.setText(dataBean.getSub_category());
            binding.tvBudgetPrice.setText("$ "+dataBean.getJob_budget());
            binding.tvDescription.setText(dataBean.getJob_description());
            binding.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(),dataBean.getCurrentDateTime()));*/
        }
    }

    private void setMyJobDetails(JobDetailClientResponce.DataBean dataBean) {
        if (dataBean.getJob_type().equals("1")) {/// """"""" SINGLE JOB
            if (dataBean.getTotal_request().equals("0")) {   // no requested yet

               /*// binding.tvNoRequest.setVisibility(View.VISIBLE);
                binding.rlUserData.setVisibility(View.GONE);
                binding.rlMultiImg.setVisibility(View.GONE);
             //   binding.llChat.setVisibility(View.GONE);*/

            } else if (dataBean.getTotal_request().equals("1") && dataBean.getJob_confirmed().equals("1")) { // jobconfirmed

                binding.rlUserData.setVisibility(View.VISIBLE);
                binding.llChat.setVisibility(View.VISIBLE);
                binding.tvNoRequest.setVisibility(View.GONE);
                binding.rlMultiImg.setVisibility(View.GONE);

                Picasso.with(binding.ivProfileImg.getContext())
                        .load(dataBean.getRequestedUserData()
                                .get(0).getProfileImage()).fit().into(binding.ivProfileImg);

                binding.tvName.setText(dataBean.getRequestedUserData().get(0).getName());
                binding.tvDistance.setText(dataBean.getRequestedUserData().get(0).getDistance_in_km() + " Km away");


            } else {   // multiple images show

                binding.rlMultiImg.setVisibility(View.VISIBLE);
                binding.rlUserData.setVisibility(View.GONE);
                binding.tvNoRequest.setVisibility(View.GONE);
                binding.llChat.setVisibility(View.GONE);
                binding.tvMemberRequested.setText(dataBean.getTotal_request() + " " + getString(R.string.member_requested));

                int leftMargin = 0;
                for (int i = 0; i < dataBean.getRequestedUserData().size(); i++) {
                    if (i != 0) {
                        leftMargin = leftMargin + 35;
                    }
                    addhorizontalTimeView(binding.flMultiImg, dataBean.getRequestedUserData().get(i).getProfileImage(), leftMargin);
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
                Intent intent = new Intent(this, ClientMainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
        }
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