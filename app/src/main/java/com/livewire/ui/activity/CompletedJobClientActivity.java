package com.livewire.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.livewire.R;
import com.livewire.adapter.CompletedJobAdapter;
import com.livewire.adapter.ConfirmJobWorkerAdapter;
import com.livewire.databinding.ActivityCompletedJobClientBinding;
import com.livewire.pagination.EndlessRecyclerViewScrollListener;
import com.livewire.utils.Constant;

public class CompletedJobClientActivity extends AppCompatActivity implements View.OnClickListener,CompletedJobAdapter.CompleteJobClientListener{
    ActivityCompletedJobClientBinding binding;
    private int limit;
    private CompletedJobAdapter jobAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = DataBindingUtil.setContentView(this,R.layout.activity_completed_job_client);

      binding.actionBarClient.ivBack.setVisibility(View.VISIBLE);
      binding.actionBarClient.tvLiveWire.setText(R.string.completed_jobs);
      binding.actionBarClient.tvLiveWire.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
       binding.recyclerView.setLayoutManager(layoutManager);
        jobAdapter = new CompletedJobAdapter(this,this);
        binding.recyclerView.setAdapter(jobAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { //swipe to refresh rcyclerview data
                binding.swipeRefresh.setRefreshing(false);
                if (Constant.isNetworkAvailable(CompletedJobClientActivity.this, binding.compltejobLayout)) {
                    // helpOfferedApi();
                }
            }
        });

        //******  Pagination """""""""""""""//
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                limit = limit + 5; //load 5 items in recyclerview
                if (Constant.isNetworkAvailable(CompletedJobClientActivity.this, binding.compltejobLayout)) {
                    if (page != 1) {
                        // progressDialog.show();
                        // helpOfferedApi();
                    }
                }
            }
        };

        binding.recyclerView.addOnScrollListener(scrollListener);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
                default:
        }
    }

    @Override
    public void completeJobMoreInfoListener() {

    }
}
