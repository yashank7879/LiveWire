package com.livewire.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivityMyOngoingJobDetailClientBinding;
import com.livewire.responce.JobDetailClientResponce;
import com.livewire.responce.MyjobResponceClient;
import com.livewire.ui.activity.credit_card.AddCreditCardActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.loopeer.shadow.ShadowView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_CLIENT_JOB_DETAIL_API;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_GET_JOB_DETAIL_API;
import static com.livewire.utils.Validation.isEmailValid;

public class MyOnGoingJobDetailClientActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMyOngoingJobDetailClientBinding binding;
    private static final String TAG = MyOnGoingJobDetailClientActivity.class.getName();
    private ProgressDialog progressDialog;
    private Button btnSendOffer;
    private int width;
    private String offer = "";
    private String hours = "";
    private String jobId = "";
    private String userId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_ongoing_job_detail_client);
        intializeView();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        // jobDetailApi();
    }


    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        binding.btnEndJob.setOnClickListener(this);

        btnSendOffer = findViewById(R.id.btn_send_offer);

        if (getIntent().getStringExtra("JobIdKey") != null) {
            //MyjobResponceClient.DataBean dataBean = (MyjobResponceClient.DataBean) getIntent().getSerializableExtra("MyJobDetail");
            jobId = getIntent().getStringExtra("JobIdKey");
            jobDetailApi();
            /*setMyJobDetails(dataBean);
            binding.setDataBean(dataBean);

            binding.tvTime.setText(Constant.getDayDifference(dataBean.getCrd(), dataBean.getCurrentDateTime()));*/
        }
    }

    private void setMyJobDetails(final JobDetailClientResponce.DataBean dataBean) {
        if (dataBean.getJob_type().equals("2")) {///"""""""""" ONGOING JOB """'
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
                            binding.btnEndJob.setVisibility(View.VISIBLE);
                            jobId = dataBean.getJobId();
                            userId = dataBean.getRequestedUserData().get(0).getUserId();
                            offer = dataBean.getJob_offer();
                            hours = dataBean.getJob_time_duration();
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


                binding.tvName.setText(dataBean.getRequestedUserData().get(0).getName());

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
            case R.id.btn_end_job:
                openWorkingDaysDialog();
                break;

            default:
        }
    }

    private void openWorkingDaysDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.end_job_dialog);
        dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
        final EditText etEmailDialog = dialog.findViewById(R.id.et_days);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        final ProgressBar progressBar1 = dialog.findViewById(R.id.progress_bar);
        final RelativeLayout forgotDialogLayout = dialog.findViewById(R.id.forgot_layout);
        dialog.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmailDialog.getText().toString().isEmpty()) {
                    Constant.snackBar(forgotDialogLayout, "Please Enter Number Of working Days.");
                    Constant.hideSoftKeyBoard(MyOnGoingJobDetailClientActivity.this, etEmailDialog);
                } else {
                    Intent intent = new Intent(MyOnGoingJobDetailClientActivity.this, AddCreditCardActivity.class);
                    intent.putExtra("OngoingJobPayment", "OngoingJob");
                    intent.putExtra("NameKey", binding.tvName.getText().toString());
                    intent.putExtra("PaymentKey", offer);
                    intent.putExtra("JobIdKey", jobId);
                    intent.putExtra("UserIdKey", userId);
                    intent.putExtra("WorkDays", etEmailDialog.getText().toString().trim());
                    intent.putExtra("Hours", hours);
                    startActivity(intent);
                    Constant.hideSoftKeyBoard(MyOnGoingJobDetailClientActivity.this, etEmailDialog);
                }
            }
        });


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.hideSoftKeyBoard(MyOnGoingJobDetailClientActivity.this, etEmailDialog);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();

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

