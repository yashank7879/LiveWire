package com.livewire.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.OngoingAdapter;
import com.livewire.adapter.SubCategoryAdapter;
import com.livewire.model.CategoryModel;
import com.livewire.pagination.EndlessRecyclerViewScrollListener;
import com.livewire.responce.OnGoingWorkerResponce;
import com.livewire.responce.SubCategoryResponse;
import com.livewire.ui.activity.JobHelpOfferedDetailWorkerActivity;
import com.livewire.ui.activity.JobOnGoingDetailWorkerActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;


public class OnGoingWorkerFragment extends Fragment implements SubCategoryAdapter.SubCategoryLisner, AdapterView.OnItemSelectedListener,View.OnClickListener,OngoingAdapter.OnGoingItemOnClick {
    private Context mContext;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout mainLayout;
    private ProgressDialog progressDialog;
    private TextView tvNoJobPost;
    private TextView tvClearAll;
    private OngoingAdapter adapter;
    private RecyclerView rvFilter;
    private List<OnGoingWorkerResponce.DataBean> ongoingList;
    private int limit = 5;
    private int start = 0;
    private FloatingActionButton btnFilter;
    private SubCategoryResponse subCategoryResponse;
    private ArrayList<SubCategoryResponse.DataBean> subCategoryTempList;
    private SubCategoryAdapter subCategoryAdapterList;
    private ArrayList<CategoryModel> subCategoryList;
    private int width;
    private Spinner subCategorySpinner;
    private RecyclerView recyclerView;
    private SubCategoryAdapter subCategoryAdapter;
    private RelativeLayout filterLayout;
    private String skillsStrin="";

    public OnGoingWorkerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_going_worker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(mContext);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;

        subCategoryTempList = new ArrayList<>();
        ongoingList = new ArrayList<>();
        subCategoryList = new ArrayList<>();

        filterLayout = view.findViewById(R.id.filter_layout);
        btnFilter = view.findViewById(R.id.btn_filter);
        rvFilter = view.findViewById(R.id.rv_filter_list);
        tvClearAll= view.findViewById(R.id.tv_clear_all);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        RecyclerView rvOngoing = (RecyclerView) view.findViewById(R.id.rv_ongoing);
        mainLayout = view.findViewById(R.id.main_layout);
        tvNoJobPost = view.findViewById(R.id.tv_no_job_post);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvOngoing.setLayoutManager(layoutManager);
        adapter = new OngoingAdapter(mContext,ongoingList,this);
        rvOngoing.setAdapter(adapter);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rvFilter.setLayoutManager(layoutManager1);
        subCategoryAdapterList = new SubCategoryAdapter(mContext, subCategoryList, this, "FilterKey");
        rvFilter.setAdapter(subCategoryAdapterList);


        btnFilter.setOnClickListener(this);
        tvClearAll.setOnClickListener(this);
        //""""""""""  swpi to refresh """""""""""""//
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { //swipe to refresh rcyclerview data
                swipeRefresh.setRefreshing(false);
                if (Constant.isNetworkAvailable(mContext, mainLayout)) {
                    ongoingListApi();
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
                    ongoingListApi();
                }
            }
        };

        rvOngoing.addOnScrollListener(scrollListener);

        //""""""""" floating button hide when scroll down """"""""//
        rvOngoing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && btnFilter.getVisibility() == View.VISIBLE) {
                    btnFilter.hide();

                } else if (dy < 0 && btnFilter.getVisibility() != View.VISIBLE) {
                    btnFilter.show();
                }
            }
        });

        ongoingListApi();
        subCategoryListApi();
    }

    private void ongoingListApi() {
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "Jobpost/getJobList")
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_type", "2")
                    .addBodyParameter("limit", ""+limit)
                    .addBodyParameter("start", ""+start)
                     .addBodyParameter("skill", skillsStrin)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            tvNoJobPost.setVisibility(View.GONE);
                            ongoingList.clear();
                            OnGoingWorkerResponce workerResponce = new Gson().fromJson(String.valueOf(response), OnGoingWorkerResponce.class);
                            ongoingList.addAll(workerResponce.getData());
                            adapter.notifyDataSetChanged();
                            subCategoryAdapterList.notifyDataSetChanged();
                        } else {
                            ongoingList.clear();
                            adapter.notifyDataSetChanged();
                            if (ongoingList.size() == 0) {
                                tvNoJobPost.setVisibility(View.VISIBLE);
                            } else Constant.snackBar(mainLayout, message);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    //"""""""""" sub category list api """""""""""""//
    private void subCategoryListApi() {
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
            AndroidNetworking.get(BASE_URL + "getSubcategoryList")
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
                                    subCategoryResponse = new Gson().fromJson(String.valueOf(response), SubCategoryResponse.class);

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


    //"""""""""open filter dialog"""""""""""""""""//
    private void openFilterDialog() {
        if (Constant.isNetworkAvailable(mContext, mainLayout) || subCategoryResponse != null) {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.filter_dialog);
            dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tvAllJobs = dialog.findViewById(R.id.tv_all_jobs);
            TextView tvNewJobs = dialog.findViewById(R.id.tv_new_jobs);
            TextView tvPendingRequest = dialog.findViewById(R.id.tv_pending_request);
            subCategorySpinner = dialog.findViewById(R.id.sub_category_spinner);
            recyclerView = dialog.findViewById(R.id.recycler_view);
            TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            final RelativeLayout addSkillsLayout = dialog.findViewById(R.id.filter_layout);
            Button btnApplySkills = dialog.findViewById(R.id.btn_apply_skills);


            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            subCategoryAdapter = new SubCategoryAdapter(mContext, subCategoryList, this, "DialogKey");
            recyclerView.setAdapter(subCategoryAdapter);

            ArrayAdapter categoryAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, subCategoryTempList);
            categoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
            subCategorySpinner.setOnItemSelectedListener(this);
            subCategorySpinner.setAdapter(categoryAdapter);


            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btnApplySkills.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (subCategoryList.size() == 0) {
                        Constant.snackBar(addSkillsLayout, "Please Select at least one Skill");
                    } else {
                        skillsString();
                        subCategoryAdapter.notifyDataSetChanged();
                        filterLayout.setVisibility(View.VISIBLE);
                        ongoingListApi();
                        dialog.dismiss();
                    }

                    //  skillDialogValidation(categorySpinner, addSkillsLayout);
                }
            });

            dialog.show();
            dialog.setCancelable(false);
        } else {
            subCategoryListApi();
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_filter:
                openFilterDialog();
                break;
            case R.id.tv_clear_all:
                subCategoryList.clear();
                filterLayout.setVisibility(View.GONE);
                skillsStrin = "";
                ongoingListApi();
                break;
            default:
        }
    }

    @Override
    public void subCategoryItemOnClick(int pos, CategoryModel categoryModel, String key) {
        if (key.equals("FilterKey")) {
            addItemsInSubTempList(categoryModel,pos);
            subCategoryAdapterList.notifyDataSetChanged();
            if (subCategoryList.size() != 0) {
                skillsString();
                ongoingListApi();
            } else {
                skillsString();
                ongoingListApi();
                filterLayout.setVisibility(View.GONE);
            }
        } else {
            addItemsInSubTempList(categoryModel,pos);
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

    private void addItemsInSubTempList(CategoryModel categoryModel, int pos) {
        subCategoryList.remove(pos);
        subCategoryAdapterList.notifyDataSetChanged();
        subCategoryAdapter.notifyDataSetChanged();
        SubCategoryResponse.DataBean dataBean = new SubCategoryResponse.DataBean();
        dataBean.setCategoryName(categoryModel.getCategoryName());
        dataBean.setCategoryId(categoryModel.getCategoryId());
        dataBean.setPosition(categoryModel.getPosition());

        if (subCategoryTempList.size() > categoryModel.getPosition()) {
            subCategoryTempList.add(categoryModel.getPosition(), dataBean);
        } else {
            subCategoryTempList.add(subCategoryTempList.size(), dataBean);
        }

    }

    @Override
    public void onItemClickListner(OnGoingWorkerResponce.DataBean dataBean, String key) {
        switch (key) {
            case "MoreInfo":
                Intent intent = new Intent(mContext, JobOnGoingDetailWorkerActivity.class);
                intent.putExtra("JobDetail",dataBean);
                startActivity(intent);
                break;
            case "Accept":
                acceptRejectrequestApi(dataBean.getUserId(),dataBean.getJobId(),"1");
                break;
            case "Reject":
                acceptRejectrequestApi(dataBean.getUserId(),dataBean.getJobId(),"2");
                break;
                default:
        }
    }

    //""""""" accept ignore request """"""""""//
    private void acceptRejectrequestApi(String userId, String jobId, String requestStatus) {
    if (Constant.isNetworkAvailable(mContext,mainLayout)){
        progressDialog.show();
        AndroidNetworking.post(BASE_URL + "Jobpost/sendRequest2")
                .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                .addBodyParameter("job_id",jobId)
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
                        //"""""' if user successfully created on going post """""""""""//

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

