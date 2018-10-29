package com.livewire.ui.activity;

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
import com.livewire.databinding.ActivityMyOngoingJobDetailClientBinding;
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
    ActivityMyOngoingJobDetailClientBinding binding;
    private static final String TAG = MyOnGoingJobDetailClientActivity.class.getName();
    private ProgressDialog progressDialog;
    private Button btnSendOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_ongoing_job_detail_client);
        intializeView();
        jobDetailApi();
    }


    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        findViewById(R.id.iv_back).setOnClickListener(this);

        btnSendOffer = findViewById(R.id.btn_send_offer);

        if (getIntent().getSerializableExtra("MyJobDetail") != null) {
            MyjobResponceClient.DataBean dataBean = (MyjobResponceClient.DataBean) getIntent().getSerializableExtra("MyJobDetail");
            setMyJobDetails(dataBean);
            binding.setDataBean(dataBean);
           // binding.tvCategory.setText(dataBean.getParent_category());
           // binding.tvSubCategory.setText(dataBean.getSub_category());
           // binding.tvOfferPrice.setText("$ " + dataBean.getJob_offer());
          ///  binding.tvStartDate.setText(dataBean.getJob_start_date());
          //  binding.tvEndDate.setText(dataBean.getJob_end_date());
          //  binding.tvWeekDays.setText(dataBean.getJob_week_days());
          //  binding.tvDescription.setText(dataBean.getJob_description());
          //  binding.tvTimeDuration.setText(dataBean.getJob_time_duration() + " hr");
            binding.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentDateTime()));
        }
    }

    private void setMyJobDetails(final MyjobResponceClient.DataBean dataBean) {
        if (dataBean.getJob_type().equals("2")) {///"""""""""" ONGOING JOB
            if (dataBean.getTotal_request().equals("0")) {  // NO OFFER SEND YET
                btnSendOffer.setVisibility(View.VISIBLE);
                binding.rlMoredetail.setVisibility(View.VISIBLE);
                binding.tvNoRequest.setVisibility(View.VISIBLE);
                binding.tvJobStatus.setVisibility(View.GONE);
                binding.llChat.setVisibility(View.GONE);
                binding.rlUserData.setVisibility(View.GONE);
                binding.rlRange.setVisibility(View.GONE);

                binding.tvNoRequest.setText(R.string.no_offer_request_yet);

                btnSendOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String jobId = dataBean.getJobId();
                        Intent intent = new Intent(MyOnGoingJobDetailClientActivity.this, NearYouClientActivity.class);
                        intent.putExtra("JobIdKey", jobId);
                        startActivity(intent);
                    }
                });

            } else if (dataBean.getTotal_request().equals("1")) {
                // JOB CONFIRM OR PENDING REQUEST OR IN PROGRESS

                switch (dataBean.getJob_confirmed()) {
                    case "0": // request pending job
                        if (dataBean.getRequestedUserData().get(0).getRequest_status().equals("0")) {
                            binding.tvJobStatus.setText(R.string.request_pending);
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
                            binding.tvJobStatus.setText(R.string.request_confirmed);
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
                btnSendOffer.setVisibility(View.GONE);


                binding.tvName.setText(dataBean.getName());

                binding.tvDistance.setText(dataBean.getRequestedUserData().get(0).getDistance_in_km() + " Km away");

                Picasso.with(binding.ivProfileImg.getContext())
                        .load(dataBean.getRequestedUserData()
                                .get(0).getProfileImage()).fit().into(binding.ivProfileImg);


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

    private void jobDetailApi() {
        if (Constant.isNetworkAvailable(this, binding.detailMainLayout)) {
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

