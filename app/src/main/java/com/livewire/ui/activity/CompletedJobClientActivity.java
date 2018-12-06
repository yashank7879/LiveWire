package com.livewire.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.CompletedJobAdapter;
import com.livewire.adapter.ConfirmJobWorkerAdapter;
import com.livewire.databinding.ActivityCompletedJobClientBinding;
import com.livewire.pagination.EndlessRecyclerViewScrollListener;
import com.livewire.responce.CompleteJobResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.COMPLETE_JOB_LIST_API;
import static com.livewire.utils.ApiCollection.GET_JOB_LIST_API;

public class CompletedJobClientActivity extends AppCompatActivity implements View.OnClickListener, CompletedJobAdapter.CompleteJobClientListener {
    ActivityCompletedJobClientBinding binding;
    private int limit=5;
    private CompletedJobAdapter jobAdapter;
    private ProgressDialog progressDialog ;
    private int start=0;
    private List<CompleteJobResponce.DataBean> completeJobList = new ArrayList<>();
    private String currentTime="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_completed_job_client);

        progressDialog = new ProgressDialog(this);
        binding.actionBarClient.ivBack.setVisibility(View.VISIBLE);
        binding.actionBarClient.tvLiveWire.setText(R.string.completed_jobs);
        binding.actionBarClient.tvLiveWire.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));

        binding.actionBarClient.ivBack.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        jobAdapter = new CompletedJobAdapter(this, this,completeJobList,currentTime);
        binding.recyclerView.setAdapter(jobAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { //swipe to refresh rcyclerview data
                binding.swipeRefresh.setRefreshing(false);
                if (Constant.isNetworkAvailable(CompletedJobClientActivity.this, binding.compltejobLayout)) {
                    completeJobApi();
                }
            }
        });

        //*************  Pagination """""""""""""""//
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                limit = limit + 5; //load 5 items in recyclerview
                if (Constant.isNetworkAvailable(CompletedJobClientActivity.this, binding.compltejobLayout)) {
                    if (page != 1) {
                        // progressDialog.show();
                        completeJobApi();
                    }
                }
            }
        };
        completeJobApi();
        binding.recyclerView.addOnScrollListener(scrollListener);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
        }
    }

    //"""""" help offer list api calling"""""""""""//
    private void completeJobApi() {// help offer api calling
        if (Constant.isNetworkAvailable(this, binding.compltejobLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + COMPLETE_JOB_LIST_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            currentTime = response.getString("currentDateTime");
                            jobAdapter.currentTime1(currentTime);
                            binding.tvNoJobPost.setVisibility(View.GONE);
                            completeJobList.clear();
                            CompleteJobResponce helpOfferedResponce = new Gson().fromJson(String.valueOf(response), CompleteJobResponce.class);
                            completeJobList.addAll(helpOfferedResponce.getData());
                            jobAdapter.notifyDataSetChanged();
                        } else {
                            completeJobList.clear();
                            jobAdapter.notifyDataSetChanged();
                            if (completeJobList.size() == 0) {
                                binding.tvNoJobPost.setVisibility(View.VISIBLE);
                            } else Constant.snackBar(binding.compltejobLayout, message);
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Constant.errorHandle(anError, CompletedJobClientActivity.this);
                    progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void singlejobcompleteMoreInfoListener(String jobType , String jobId) {

        if (jobType.equals("1")) {// MysingleJob More Info
            Intent intent = new Intent(this, SingleJobCompleteJobDetailActivity.class);
            intent.putExtra("JobIdKey", jobId);
            startActivity(intent);
        }else {// MyOngoingJob More Info
            Intent intent = new Intent(this, CompleteOngoingJobDetailClientActivity.class);
            intent.putExtra("JobIdKey", jobId);
            startActivity(intent);
        }
    }
}
