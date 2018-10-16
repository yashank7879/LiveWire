package com.livewire.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.NearYouAdapter;
import com.livewire.responce.HelpOfferedResponce;
import com.livewire.responce.NearYouResponce;
import com.livewire.ui.fragments.MyJobClientFragment;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;

public class NearYouClientActivity extends AppCompatActivity implements View.OnClickListener, NearYouAdapter.NearYouRequestListener {
    private ProgressDialog progressDialog;
    private RelativeLayout mainLayout;
    private List<NearYouResponce.DataBean> nearYouList;
    private NearYouAdapter nearYouAdapter;
    private String jobId;
    private int width;
    private TextView tv_no_job_post;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_you_client);
        intailizeView();
    }

    private void intailizeView() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;

        if (getIntent().getStringExtra("JobIdKey") != null) {
            jobId = getIntent().getStringExtra("JobIdKey");
        }
        //jobId = "34";
        progressDialog = new ProgressDialog(this);
        mainLayout = findViewById(R.id.nearyou_layout);
        ImageView ivBack = findViewById(R.id.iv_back);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        tv_no_job_post = findViewById(R.id.tv_no_job_post);
        nearYouList = new ArrayList<>();


        RecyclerView recyclerView = findViewById(R.id.rv_near_you);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        nearYouAdapter = new NearYouAdapter(this, nearYouList, this);
        recyclerView.setAdapter(nearYouAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { //swipe to refresh rcyclerview data
                swipeRefreshLayout.setRefreshing(false);
                if (Constant.isNetworkAvailable(NearYouClientActivity.this, mainLayout)) {
                    nearYouApi();
                }
            }
        });


        ivBack.setOnClickListener(this);
        nearYouApi();
    }


    //"""""""""' near you api at client side""""""""""""""//
    private void nearYouApi() {// help offer api calling
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "Jobpost/getNearByWorker")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", jobId)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            NearYouResponce helpOfferedResponce = new Gson().fromJson(String.valueOf(response), NearYouResponce.class);
                            nearYouList.addAll(helpOfferedResponce.getData());
                            nearYouAdapter.notifyDataSetChanged();
                        } else {
                            if (nearYouList.size() == 0) {
                                tv_no_job_post.setVisibility(View.VISIBLE);
                            }
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
    public void requestBtnOnclick(NearYouResponce.DataBean response) {
        openRequestDialog(response.getUserId());
    }

    //""""""""""" open send offer dialog """"""""""""""'//
    private void openRequestDialog(final String userId) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.request_send_client_dialog);
        dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
        final RelativeLayout mainLayout1 = dialog.findViewById(R.id.c_dialog_main_layout);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        Button btnSendRequest = dialog.findViewById(R.id.btn_send_request);
        final EditText etOfferPrice = dialog.findViewById(R.id.et_offer_price);

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (Validation.isEmpty(etOfferPrice)){
                Constant.snackBar(mainLayout1,"Please enter offer price.");
            }else {
                dialog.dismiss();
                sendOfferRequestApi(etOfferPrice.getText().toString().trim(), mainLayout1,userId);
            }
            }
        });


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.hideSoftKeyBoard(NearYouClientActivity.this,etOfferPrice);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCancelable(false);

    }

    private void sendOfferRequestApi(String offerPrice, RelativeLayout mainLayout1, String userId) {
        if (Constant.isNetworkAvailable(this,mainLayout1)){
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "Jobpost/sendRequest2")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", jobId)
                    //.addBodyParameter("request_to", "2")
                    .addBodyParameter("request_to", userId)
                    .addBodyParameter("job_offer", offerPrice)
                    .addBodyParameter("request_status", "0")
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
                            Constant.snackBar(mainLayout, message);
                            //"""""' if user successfully created on going post """""""""""//
                            Intent intent = new Intent(NearYouClientActivity.this,ClientMainActivity.class);
                           intent.putExtra("NearYouKey","MyJobs");
                            startActivity(intent);
                            finish();
                           // first time replace home fragment

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