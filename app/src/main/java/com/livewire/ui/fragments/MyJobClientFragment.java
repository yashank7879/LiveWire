package com.livewire.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.MyJobAdapter;
import com.livewire.pagination.EndlessRecyclerViewScrollListener;
import com.livewire.responce.MyjobResponceClient;
import com.livewire.ui.activity.MyOnGoingJobDetailClientActivity;
import com.livewire.ui.activity.MySingleJobDetailClientActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.GET_MY_JOB_LIST_API;


public class MyJobClientFragment extends Fragment implements View.OnClickListener, MyJobAdapter.OnClickMoreInfoListener {
    private Context mContext;
    private ProgressDialog progressDialog;
    private RelativeLayout mainLayout;
    private MyJobAdapter adapter;
    private List<MyjobResponceClient.DataBean> myJobList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int width;
    private String requestStatus = "";
    private String jobType = "";
    private AppCompatCheckBox cbPending;
    private AppCompatCheckBox cbConfirm;
    private TextView tvNoRecord;
    private boolean state;
    private String tempJobTyp="";
    private int limit = 5;
    private int start = 0;

    public MyJobClientFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_job_client, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(mContext);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;

        tvNoRecord = view.findViewById(R.id.tv_no_record);
        mainLayout = view.findViewById(R.id.main_layout);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        RecyclerView rvMyjob = view.findViewById(R.id.rv_myjob);


        myJobList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvMyjob.setLayoutManager(layoutManager);
        adapter = new MyJobAdapter(mContext, myJobList,this);
        rvMyjob.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { //swipe to refresh rcyclerview data
                swipeRefreshLayout.setRefreshing(false);
                if (Constant.isNetworkAvailable(mContext, mainLayout)) {
                    myJobListApi();
                }
            }
        });

        //******  Pagination """""""""""""""//
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                limit = limit + 5; //load 5 items in recyclerview
                if (Constant.isNetworkAvailable(mContext, mainLayout)) {
                    // progressDialog.show();
                    myJobListApi();
                }
            }
        };

        rvMyjob.addOnScrollListener(scrollListener);
        actionBarIntialize(view);
        myJobListApi();

    }

    private void actionBarIntialize(View view) {
        View actionBar = view.findViewById(R.id.action_bar1);
        TextView header = actionBar.findViewById(R.id.tv_live_wire);

        header.setText(R.string.my_jobs);
        header.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
        ImageView ivFilter = actionBar.findViewById(R.id.iv_filter);
        ImageView ivSetting = actionBar.findViewById(R.id.iv_setting);
        ivFilter.setVisibility(View.VISIBLE);
        ivSetting.setVisibility(View.GONE);
        ivFilter.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        myJobListApi();

    }

    //""""""""""" My job list api     """"""""""""//
    private void myJobListApi() {// help offer api calling
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + GET_MY_JOB_LIST_API)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_type", jobType)
                    .addBodyParameter("request_status", requestStatus)
                    .addBodyParameter("limit", ""+limit)
                    .addBodyParameter("start", ""+start)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            tvNoRecord.setVisibility(View.GONE);
                            myJobList.clear();
                            MyjobResponceClient myjobResponceClient = new Gson().fromJson(String.valueOf(response), MyjobResponceClient.class);
                            myJobList.addAll(myjobResponceClient.getData());
                            adapter.notifyDataSetChanged();
                        } else {
                            myJobList.clear();
                            adapter.notifyDataSetChanged();
                            if (myJobList.size() == 0) {
                                tvNoRecord.setVisibility(View.VISIBLE);

                            }else Constant.snackBar(mainLayout, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Constant.errorHandle(anError,getActivity());
                    progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_filter:
                openFilterDialog();
                break;
            case R.id.cb_pending:
                if (cbPending.isChecked()) {
                    cbConfirm.setChecked(false);
                }
                break;
            case R.id.cb_confirm:
                if (cbConfirm.isChecked()) {
                    cbPending.setChecked(false);
                }
                break;
            default:
        }
    }

    // open filter
    private void openFilterDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.myjob_filter_dialog);
        dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
        LinearLayout llOnce;
        final ImageView ivOnce;
        LinearLayout llOngoing;
        final ImageView ivOngoing;
        final TextView tvOngoing;
        Button btnSendRequest;


        RelativeLayout cDialogMainLayout = (RelativeLayout) dialog.findViewById(R.id.c_dialog_main_layout);
        llOnce = dialog.findViewById(R.id.ll_once);
        ivOnce = dialog.findViewById(R.id.iv_once);
        llOngoing = dialog.findViewById(R.id.ll_ongoing);
        ivOngoing = dialog.findViewById(R.id.iv_ongoing);
        tvOngoing = dialog.findViewById(R.id.tv_ongoing);
        final TextView tvOnce = dialog.findViewById(R.id.tv_once);
        cbPending = dialog.findViewById(R.id.cb_pending);
        cbConfirm = dialog.findViewById(R.id.cb_confirm);
        cbConfirm.setOnClickListener(this);
        cbPending.setOnClickListener(this);
        btnSendRequest = dialog.findViewById(R.id.btn_send_request);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);

        isDialogStatus(ivOnce, ivOngoing, tvOngoing, tvOnce);

        Log.d("openFilter pending: ", String.valueOf(cbPending.isChecked()));
        Log.d("openFilter confirm: ", String.valueOf(cbConfirm.isChecked()));
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (cbPending.isChecked()){
                    requestStatus = "pending";
                }
                if (cbConfirm.isChecked()){
                    requestStatus = "1";
                }
                if (!cbConfirm.isChecked() && !cbPending.isChecked()){
                    requestStatus="";
                }
                /*if (jobType.equals("")) {
                    jobType = "1";
                }*/
                jobType = tempJobTyp;
                state= true;
                myJobListApi();
            }
        });

        llOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inActiveData(ivOnce, ivOngoing, tvOngoing, tvOnce);
                tempJobTyp = "1";
                tvOnce.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkBlack));
                ivOnce.setBackground(getResources().getDrawable(R.drawable.active_btn_balck_bg));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivOnce.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorDarkBlack));
                }
            }
        });

        llOngoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inActiveData(ivOnce, ivOngoing, tvOngoing, tvOnce);
                tempJobTyp = "2";
                tvOngoing.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkBlack));
                ivOngoing.setBackground(getResources().getDrawable(R.drawable.active_btn_balck_bg));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivOngoing.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorDarkBlack));
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void isDialogStatus(ImageView ivOnce, ImageView ivOngoing, TextView tvOngoing, TextView tvOnce) {
       if (state) {
           if (jobType.equals("1")) {
               tvOnce.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkBlack));
               ivOnce.setBackground(getResources().getDrawable(R.drawable.active_btn_balck_bg));
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                   ivOngoing.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
                   ivOnce.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorDarkBlack));
               }
               tvOngoing.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
               ivOngoing.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
           } else if (jobType.equals("2")) {
               tvOngoing.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkBlack));
               ivOngoing.setBackground(getResources().getDrawable(R.drawable.active_btn_balck_bg));
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                   ivOngoing.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorDarkBlack));
                   ivOnce.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
               }
               tvOnce.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
               ivOnce.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
           }

           if (requestStatus.equals("pending")) {
               cbPending.setChecked(true);
           } else if (requestStatus.equals("1")) {
               cbConfirm.setChecked(true);
           }
       }
    }

    private void inActiveData(ImageView ivOnce, ImageView ivOngoing, TextView tvOngoing, TextView tvOnce) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivOnce.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
            ivOngoing.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
        }
        tvOngoing.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
        tvOnce.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
        ivOnce.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
        ivOngoing.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
    }

    //""""""""" Click on More Info """"""""""""""//
    @Override
    public void moreInfoOnClickClient(MyjobResponceClient.DataBean dataBean) {
        Intent intent =null;
        if (dataBean.getJob_type().equals("1")) { // single job
            intent  = new Intent(mContext, MySingleJobDetailClientActivity.class);
            intent.putExtra("MyJobDetail", dataBean);
            startActivity(intent);
        }else if (dataBean.getJob_type().equals("2")){// ongoing job
            intent  = new Intent(mContext, MyOnGoingJobDetailClientActivity.class);
            intent.putExtra("MyJobDetail", dataBean);
            startActivity(intent);
        }
    }
}

