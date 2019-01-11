package com.livewire.ui.activity;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.RequestAdapter;
import com.livewire.responce.RequestResponceClient;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.GET_MY_JOB_REQUEST_LIST_API;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_REQUEST_2_API;

public class RequestClientActivity extends AppCompatActivity implements View.OnClickListener, RequestAdapter.RequestAcceptIgnorListner {

    private ProgressDialog progressDialog;
    private RelativeLayout mainLayout;
    private List<RequestResponceClient.DataBean> requestList;
    private RequestAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private String jobId;
    private TextView tvNoJobPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_client);
        intializeViews();
        actionBarIntialize();

        if (getIntent().getStringExtra("JobId") != null) {
            jobId = getIntent().getStringExtra("JobId");

            loadRequestListData();
        }

    }

    private void intializeViews() {
        requestList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        mainLayout = findViewById(R.id.request_main_layout);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        tvNoJobPost = findViewById(R.id.tv_no_job_post);
        RecyclerView recyclerView = findViewById(R.id.rv_request);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RequestAdapter(this, requestList, this);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { //swipe to refresh rcyclerview data
                swipeRefresh.setRefreshing(false);
                if (Constant.isNetworkAvailable(RequestClientActivity.this, mainLayout)) {
                    loadRequestListData();
                }
            }
        });

    }

    private void actionBarIntialize() {
        View actionBar = findViewById(R.id.action_bar1);
        TextView header = actionBar.findViewById(R.id.tv_live_wire);
        header.setText(R.string.request);
        header.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        ImageView ivFilter = actionBar.findViewById(R.id.iv_filter);
        ImageView ivBack = actionBar.findViewById(R.id.iv_back);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(this);
        ivFilter.setVisibility(View.GONE);
    }

    private void loadRequestListData() {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + GET_MY_JOB_REQUEST_LIST_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", jobId)
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
                                    tvNoJobPost.setVisibility(View.GONE);
                                    requestList.clear();
                                    RequestResponceClient responceClient = new Gson().fromJson(String.valueOf(response), RequestResponceClient.class);
                                    requestList.addAll(responceClient.getData());
                                    adapter.notifyDataSetChanged();

                                } else {
                                    requestList.clear();
                                    adapter.notifyDataSetChanged();
                                    if (requestList.size() == 0) {
                                           tvNoJobPost.setVisibility(View.VISIBLE);
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
                            Constant.errorHandle(anError, RequestClientActivity.this);

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
        }
    }

    //""""""" accept ignore request """"""""""//
    private void acceptRejectrequestApi(final String requestStatus, String userId, final int pos) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + JOBPOSTSEND_REQUEST_2_API)
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
                            Constant.snackBar(mainLayout, message);

                            if (requestStatus.equals("1")) { // if
                                onBackPressed();
                            } else {
                                requestList.remove(pos);
                                adapter.notifyDataSetChanged();

                                if (requestList.size() == 0) {
                                    onBackPressed();
                                }

                            }
                        } else {
                            Constant.snackBar(mainLayout, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Constant.errorHandle(anError, RequestClientActivity.this);
                    progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void OnClickRequestAccept(String status, String userId, int pos) {
        acceptRejectrequestApi(status, userId, pos);
    }
}
