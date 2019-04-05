package com.livewire.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.ReviewAdapter;
import com.livewire.databinding.ActivityReviewListBinding;
import com.livewire.responce.GetReviewResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.GET_OTHER_REVIEW_LIST_API;

/**
 * Created by mindiii on 4/5/19.
 */

public class OtherReviewListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = OtherReviewListActivity.class.getName();
    ActivityReviewListBinding binding;
    private ProgressDialog progressDialog;
    private GetReviewResponce reviewResponce;
    private List<GetReviewResponce.DataBean> reviewList = new ArrayList<>();
    private ReviewAdapter adapter;
    private String userId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review_list);

        if (getIntent().getStringExtra("userId") != null) {
            userId = getIntent().getStringExtra("userId");

        }
        binding.tvNoReview.setText("This user don't have any review yet");

        progressDialog = new ProgressDialog(this);

        binding.actionBarWorker.tvLiveWire.setText(R.string.reviews);
        binding.actionBarWorker.tvLiveWire.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
        binding.actionBarWorker.ivBack.setOnClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvReview.setLayoutManager(layoutManager);

        adapter = new ReviewAdapter(this, reviewList);
        binding.rvReview.setAdapter(adapter);

        binding.rvReview.addItemDecoration(new
                DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        reviewListApi();

    }

    private void reviewListApi() {
        if (Constant.isNetworkAvailable(this, binding.reviewLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + GET_OTHER_REVIEW_LIST_API)
                    .addBodyParameter("user_id", userId)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {

                            reviewResponce = new Gson().fromJson(String.valueOf(response), GetReviewResponce.class);
                            reviewList.addAll(reviewResponce.getData());
                            if (reviewResponce.getData().size() != 0) {
                                binding.tvNoReview.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            binding.tvNoReview.setVisibility(View.VISIBLE);
                            if (!message.equals("Records not found")) {
                                Constant.snackBar(binding.reviewLayout, message);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    progressDialog.dismiss();
                    Log.e(TAG, anError.getErrorDetail());
                }
            });
        }
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
}
