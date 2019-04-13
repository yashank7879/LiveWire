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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.MyJobAdapter;
import com.livewire.adapter.SubCategoryAdapter;
import com.livewire.model.CategoryModel;
import com.livewire.pagination.EndlessRecyclerViewScrollListener;
import com.livewire.responce.MyjobResponceClient;
import com.livewire.responce.SubCategoryResponse;
import com.livewire.ui.activity.MyOnGoingJobDetailClientActivity;
import com.livewire.ui.activity.MyProfileClientActivity;
import com.livewire.ui.activity.MySingleJobDetailClientActivity;
import com.livewire.ui.activity.WorkerProfileDetailClientActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.GET_MY_JOB_LIST_API;
import static com.livewire.utils.ApiCollection.GET_SUBCATEGORY_LIST_API;

public class MyJobClientFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, SubCategoryAdapter.SubCategoryLisner, MyJobAdapter.OnClickMoreInfoListener {
    private Context mContext;
    private ProgressDialog progressDialog;
    private RelativeLayout mainLayout;
    private MyJobAdapter adapter;
    private List<MyjobResponceClient.DataBean> myJobList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int width;
    private String requestStatus = "";
    private String jobType = "all";
    private AppCompatCheckBox cbPending;
    private AppCompatCheckBox cbConfirm;
    private TextView tvNoRecord;
    private boolean state;
    private String tempJobTyp = "";
    private int limit = 5;
    private int start = 0;
    private FloatingActionButton btnFilter;
    private AppCompatCheckBox cbNewApp;
    private AppCompatCheckBox cbCompletedApp;
    private RecyclerView recyclerView;
    private SubCategoryAdapter subCategoryAdapter;
    private ArrayList<CategoryModel> subCategoryList;
    private SubCategoryResponse subCategoryResponse;
    private ArrayList<SubCategoryResponse.DataBean> subCategoryTempList;
    private Spinner subCategorySpinner;
    private String skillsStrin = "";
    private SubCategoryAdapter subCategoryAdapterList;
    private RecyclerView rvFilter;
    private RelativeLayout filterLayout;
    private boolean isOpenFilter= true;
    private TextView tvClearAll;

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

        Constant.printLogMethod(Constant.LOG_VALUE, "token", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""));
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;

        tvNoRecord = view.findViewById(R.id.tv_no_record);
        tvClearAll = view.findViewById(R.id.tv_clear_all);
        mainLayout = view.findViewById(R.id.main_layout);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        btnFilter = view.findViewById(R.id.btn_filter);
        subCategoryList = new ArrayList<>();
        RecyclerView rvMyjob = view.findViewById(R.id.rv_myjob);
        rvFilter = view.findViewById(R.id.rv_filter_list);
        filterLayout = view.findViewById(R.id.filter_layout);

        myJobList = new ArrayList<>();
        subCategoryTempList = new ArrayList<>();
        tvClearAll.setOnClickListener(this);
        btnFilter.setOnClickListener(this);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rvFilter.setLayoutManager(layoutManager1);
        subCategoryAdapterList = new SubCategoryAdapter(mContext, subCategoryList, this, "FilterKey");
        rvFilter.setAdapter(subCategoryAdapterList);


        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvMyjob.setLayoutManager(layoutManager);
        adapter = new MyJobAdapter(mContext, myJobList, this);
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

        //""""""""" floating button hide when scroll down """"""""//
        rvMyjob.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && btnFilter.getVisibility() == View.VISIBLE) {
                    btnFilter.hide();
                } else if (dy < 0 && btnFilter.getVisibility() != View.VISIBLE) {
                    btnFilter.show();
                }
            }
        });
        SubCategoryListApi();
        //actionBarIntialize(view);
        myJobListApi();
    }
    //"""""""""" sub category list api """""""""""""//
    private void SubCategoryListApi() {
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
            AndroidNetworking.get(BASE_URL + GET_SUBCATEGORY_LIST_API)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            //    mainLayout.startAnimation(mLoadAnimation);
                            String status = null;
                            try {
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                    subCategoryResponse = new Gson().fromJson(String.valueOf(response), SubCategoryResponse.class);

                                    addItemsInSubCategoryTempList();

                                } else {
                                    Constant.snackBar(mainLayout, message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sub_category_spinner:
                // if (!subCategoryResponse.getData().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName().equals("Skills")) {
                if (!subCategoryTempList.get(subCategorySpinner.getSelectedItemPosition()).getCategoryName().equals("Skills")) {

                    CategoryModel category = new CategoryModel();
                    category.setCategoryName(subCategoryTempList.get(subCategorySpinner.getSelectedItemPosition()).getCategoryName());
                    category.setCategoryId(subCategoryTempList.get(subCategorySpinner.getSelectedItemPosition()).getCategoryId());
                    category.setPosition(subCategoryTempList.get(subCategorySpinner.getSelectedItemPosition()).getPosition());
                    subCategoryList.add(category);
                    subCategoryTempList.remove(subCategorySpinner.getSelectedItemPosition());
                    subCategoryAdapter.notifyDataSetChanged();
                    subCategorySpinner.setSelection(0);
                    //recyclerView.smoothScrollToPosition(subCategoryList.size()-1);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //"""""" create static responce list """""""""""//
    private void addItemsInSubCategoryTempList() {
        for (int i = 0; i < subCategoryResponse.getData().size(); i++) {
            SubCategoryResponse.DataBean dataBean = new SubCategoryResponse.DataBean();
            dataBean.setCategoryName(subCategoryResponse.getData().get(i).getCategoryName());
            dataBean.setCategoryId(subCategoryResponse.getData().get(i).getCategoryId());
            dataBean.setPosition(i + 1);
            subCategoryTempList.add(dataBean);
        }
        // subCategoryTempList.addAll(subCategoryResponse.getData());

        SubCategoryResponse.DataBean dataBean = new SubCategoryResponse.DataBean();
        dataBean.setCategoryName("Skills");
        subCategoryTempList.add(0, dataBean);
    }
    private void actionBarIntialize(View view) {
        View actionBar = view.findViewById(R.id.action_bar1);
        TextView header = actionBar.findViewById(R.id.tv_live_wire);

        header.setText(R.string.my_livewire_post);
        header.setAllCaps(true);
        header.setTextColor(ContextCompat.getColor(mContext, R.color.colorGreen));
        ImageView ivFilter = actionBar.findViewById(R.id.iv_filter);
        ImageView ivSetting = actionBar.findViewById(R.id.iv_setting);
        ImageView ivProfile = actionBar.findViewById(R.id.iv_profile);
        ivFilter.setVisibility(View.GONE);
        ivSetting.setVisibility(View.GONE);
        ivProfile.setVisibility(View.VISIBLE);

        //ivFilter.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        myJobListApi();

    }

    //""""""""""" My job list api     """"""""""""//
    private void myJobListApi() {// help offer api calling
        if (Constant.isNetworkAvailable(mContext, mainLayout)){
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + GET_MY_JOB_LIST_API)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_type", jobType)
                    .addBodyParameter("request_status", requestStatus)
                    .addBodyParameter("category_id", skillsStrin)
                    .addBodyParameter("limit", "" + limit)
                    .addBodyParameter("start", "" + start)
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
                            subCategoryAdapterList.notifyDataSetChanged();
                        } else {
                            myJobList.clear();
                            adapter.notifyDataSetChanged();
                            if (myJobList.size() == 0) {
                                tvNoRecord.setVisibility(View.VISIBLE);

                            } else Constant.snackBar(mainLayout, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Constant.errorHandle(anError, getActivity());
                    progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_filter:
                // openFilterDialog();
                break;
            case R.id.cb_pending:
                allUnCheckBox();
                cbPending.setChecked(true);
                break;
            case R.id.cb_confirm:
                allUnCheckBox();
                cbConfirm.setChecked(true);
                break;

            case R.id.cb_completed_app:
                allUnCheckBox();
                cbCompletedApp.setChecked(true);
                break;

            case R.id.cb_new_app:
                allUnCheckBox();
                cbNewApp.setChecked(true);
                break;

            case R.id.iv_profile: {
                Intent intent = new Intent(mContext, MyProfileClientActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_filter:
                if (isOpenFilter)
                openFilterDialog();
                break;
            case R.id.tv_clear_all:
                subCategoryList.clear();
                subCategoryTempList.clear();
                addItemsInSubCategoryTempList();
                filterLayout.setVisibility(View.GONE);
                skillsStrin = "";
                myJobListApi();
                break;
            default:
        }
    }

    private void allUnCheckBox() {
        cbPending.setChecked(false);
        cbNewApp.setChecked(false);
        cbCompletedApp.setChecked(false);
        cbConfirm.setChecked(false);
    }

    // open filter
    private void openFilterDialog() {
        isOpenFilter = false;
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.myjob_filter_dialog);
        dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
        LinearLayout llOnce;
        final ImageView ivOnce,ivAllApp;
        LinearLayout llOngoing;
        LinearLayout llAllApp;
        final ImageView ivOngoing;
        final TextView tvOngoing,tvAllApp;
        Button btnSendRequest;

        RelativeLayout cDialogMainLayout = (RelativeLayout) dialog.findViewById(R.id.c_dialog_main_layout);
        llOnce = dialog.findViewById(R.id.ll_once);
        llAllApp = dialog.findViewById(R.id.ll_all_app);
        ivOnce = dialog.findViewById(R.id.iv_once);
        llOngoing = dialog.findViewById(R.id.ll_ongoing);
        ivOngoing = dialog.findViewById(R.id.iv_ongoing);
        ivAllApp = dialog.findViewById(R.id.iv_all);
        tvOngoing = dialog.findViewById(R.id.tv_ongoing);
        tvAllApp = dialog.findViewById(R.id.tv_all);
        recyclerView = dialog.findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        subCategoryAdapter = new SubCategoryAdapter(mContext, subCategoryList, this, "DialogKey");
        recyclerView.setAdapter(subCategoryAdapter);
        final TextView tvOnce = dialog.findViewById(R.id.tv_once);
        cbPending = dialog.findViewById(R.id.cb_pending);
        cbConfirm = dialog.findViewById(R.id.cb_confirm);
        cbNewApp = dialog.findViewById(R.id.cb_new_app);
        cbCompletedApp = dialog.findViewById(R.id.cb_completed_app);
        subCategorySpinner = dialog.findViewById(R.id.sub_category_spinner);
        ArrayAdapter categoryAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, subCategoryTempList);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        subCategorySpinner.setOnItemSelectedListener(this);
        subCategorySpinner.setAdapter(categoryAdapter);

        cbConfirm.setOnClickListener(this);
        cbPending.setOnClickListener(this);
        cbCompletedApp.setOnClickListener(this);
        cbNewApp.setOnClickListener(this);
        if (requestStatus.equals("new") || requestStatus.isEmpty())
        cbNewApp.setChecked(true);
        btnSendRequest = dialog.findViewById(R.id.btn_send_request);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        isDialogStatus(ivOnce, ivOngoing, tvOngoing, tvOnce, ivAllApp, tvAllApp);

        // Log.d("openFilter pending: ", String.valueOf(cbPending.isChecked()));
       // Log.d("openFilter confirm: ", String.valueOf(cbConfirm.isChecked()));
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempJobTyp = "all";
                jobType = tempJobTyp;
                requestStatus = "";
                skillsStrin="";
                filterLayout.setVisibility(View.GONE);
                subCategoryList.clear();
                subCategoryTempList.clear();
                addItemsInSubCategoryTempList();
                myJobListApi();
                dialog.dismiss();
                isOpenFilter = true;
            }
        });

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                skillsString();
                if (cbPending.isChecked()) {
                    requestStatus = "pending";//0
                }
                if (cbConfirm.isChecked()) {
                    requestStatus = "confirm";//1
                }
                if (cbCompletedApp.isChecked()) {
                    requestStatus = "complete";//4
                }
                if (cbNewApp.isChecked()) {
                    requestStatus ="new";
                }

               /* if (!cbConfirm.isChecked() && !cbPending.isChecked() && !cbCompletedApp.isChecked()) {
                    requestStatus = "";
                }*/
                /*if (jobType.equals("")) {
                    jobType = "1";
                }*/
                if (subCategoryList.size() > 0) {
                    filterLayout.setVisibility(View.VISIBLE);
                }
                jobType = tempJobTyp;
                state = true;
                isOpenFilter = true;
                myJobListApi();
            }
        });

        llOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inActiveData(ivOnce, ivOngoing, tvOngoing, tvOnce,tvAllApp,ivAllApp);
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
                inActiveData(ivOnce, ivOngoing, tvOngoing, tvOnce,tvAllApp,ivAllApp);
                tempJobTyp = "2";
                tvOngoing.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkBlack));
                ivOngoing.setBackground(getResources().getDrawable(R.drawable.active_btn_balck_bg));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivOngoing.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorDarkBlack));
                }
            }
        });

        llAllApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inActiveData(ivOnce, ivOngoing, tvOngoing, tvOnce,tvAllApp,ivAllApp);
                tempJobTyp = "all";
                requestStatus="";
                tvAllApp.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkBlack));
                ivAllApp.setBackground(getResources().getDrawable(R.drawable.active_btn_balck_bg));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivAllApp.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorDarkBlack));
                }
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void isDialogStatus(ImageView ivOnce, ImageView ivOngoing, TextView tvOngoing, TextView tvOnce, ImageView ivAllApp, TextView tvAllApp) {
        if (state) {
            if (jobType.equals("1")) {
                tvOnce.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkBlack));
                ivOnce.setBackground(getResources().getDrawable(R.drawable.active_btn_balck_bg));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivOngoing.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
                    ivOnce.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorDarkBlack));
                    ivAllApp.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
                }
                tvAllApp.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
                ivAllApp.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));

                tvOngoing.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
                ivOngoing.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
            } else if (jobType.equals("2")) {
                tvOngoing.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkBlack));
                ivOngoing.setBackground(getResources().getDrawable(R.drawable.active_btn_balck_bg));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivOngoing.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorDarkBlack));
                    ivOnce.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
                    ivAllApp.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
                }
                tvAllApp.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
                ivAllApp.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));

                tvOnce.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
                ivOnce.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
            } else if (jobType.equals("all")) {
                tvAllApp.setTextColor(ContextCompat.getColor(mContext, R.color.colorDarkBlack));
                ivAllApp.setBackground(getResources().getDrawable(R.drawable.active_btn_balck_bg));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivAllApp.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorDarkBlack));
                    ivOnce.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
                    ivOngoing.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
                }
                tvOnce.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
                ivOnce.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
                ivOngoing.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
            }

            switch (requestStatus) {
                case "pending":
                    cbPending.setChecked(true);
                    break;
                case "confirm":
                    cbConfirm.setChecked(true);
                    break;
                case "complete":
                    cbCompletedApp.setChecked(true);
                    break;
                case "new":
                    cbNewApp.setChecked(true);
                    break;
            }
        }
    }

    private void inActiveData(ImageView ivOnce, ImageView ivOngoing, TextView tvOngoing, TextView tvOnce, TextView tvAllApp, ImageView ivAllApp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivOnce.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
            ivOngoing.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
            ivAllApp.setImageTintList(ContextCompat.getColorStateList(mContext, R.color.colorLightGray));
        }
        tvOngoing.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
        tvOnce.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
        tvAllApp.setTextColor(ContextCompat.getColor(mContext, R.color.colorLightGray));
        ivOnce.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
        ivOngoing.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
        ivAllApp.setBackground(getResources().getDrawable(R.drawable.inactive_btn_gray_bg));
    }

    //""""""""" Click on More Info """"""""""""""//
    @Override
    public void moreInfoOnClickClient(MyjobResponceClient.DataBean dataBean) {
        Intent intent = null;
        if (dataBean.getJob_type().equals("1")) { // single job
            intent = new Intent(mContext, MySingleJobDetailClientActivity.class);
            intent.putExtra("JobIdKey", dataBean.getJobId());
            startActivity(intent);
        } else if (dataBean.getJob_type().equals("2")) {// ongoing job
            intent = new Intent(mContext, MyOnGoingJobDetailClientActivity.class);
            intent.putExtra("JobIdKey", dataBean.getJobId());
            intent.putExtra("jobrequestId", dataBean.getJobrequestId());
            startActivity(intent);
        }
    }

    @Override
    public void workerProfileDetail(MyjobResponceClient.DataBean dataBean) {
        Intent intent = new Intent(mContext, WorkerProfileDetailClientActivity.class);
        intent.putExtra("UserIdKey", dataBean.getRequestedUserData().get(0).getUserId());
        mContext.startActivity(intent);
    }

    @Override
    public void subCategoryItemOnClick(int pos, CategoryModel categoryModel, String key) {
        if (key.equals("FilterKey")) {
            addItemsInSubTempList(categoryModel, pos);
            if (subCategoryList.size() != 0) {
                skillsString();
                myJobListApi();
            } else {
                skillsString();
                myJobListApi();
                filterLayout.setVisibility(View.GONE);
            }
        } else {
            addItemsInSubTempList(categoryModel, pos);
         /*   SubCategoryResponse.DataBean dataBean = new SubCategoryResponse.DataBean();
            dataBean.setCategoryName(categoryModel.getCategoryName());
            dataBean.setCategoryId(categoryModel.getCategoryId());
            dataBean.setPosition(categoryModel.getPosition());

            if (subCategoryTempList.size() > categoryModel.getPosition()) {
                subCategoryTempList.add(categoryModel.getPosition(), dataBean);
            } else {
                subCategoryTempList.add(subCategoryTempList.size(), dataBean);
            }
            subCategoryList.remove(pos);
            subCategoryAdapterList.notifyDataSetChanged();
            subCategoryAdapter.notifyDataSetChanged();*/
            //  subCategoryTempList.add()
        }
    }

    // add item in subCategoryTempList when remove from recyclerview
    private void addItemsInSubTempList(CategoryModel categoryModel, int pos) {
        SubCategoryResponse.DataBean dataBean = new SubCategoryResponse.DataBean();
        dataBean.setCategoryName(categoryModel.getCategoryName());
        dataBean.setCategoryId(categoryModel.getCategoryId());
        dataBean.setPosition(categoryModel.getPosition());

        if (subCategoryTempList.size() > categoryModel.getPosition()) {
            subCategoryTempList.add(categoryModel.getPosition(), dataBean);
        } else {
            subCategoryTempList.add(subCategoryTempList.size(), dataBean);
        }
        subCategoryList.remove(pos);
        subCategoryAdapterList.notifyDataSetChanged();
        subCategoryAdapter.notifyDataSetChanged();
    }

    private String skillsString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < subCategoryList.size(); i++) {
            sb.append(subCategoryList.get(i).getCategoryId());
            sb.append(",");
        }
        skillsStrin = String.valueOf(sb);

        return skillsStrin;
    }
}

