package com.livewire.ui.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.HelpOfferedAdapter;
import com.livewire.adapter.SubCategoryAdapter;
import com.livewire.model.CategoryModel;
import com.livewire.responce.HelpOfferedResponce;
import com.livewire.responce.SubCategoryResponse;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.livewire.utils.ApiCollection.BASE_URL;


public class HelpOfferedFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Context mContext;
    private RelativeLayout mainLayout;
    private ArrayList<HelpOfferedResponce.DataBean> offerList;
    private HelpOfferedAdapter offeredAdapter;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView btnFilter;
    private int width;
    private ArrayList<CategoryModel> subCategoryList;
    private SubCategoryResponse subCategoryResponse;
    private Spinner subCategorySpinner;
    private SubCategoryAdapter subCategoryAdapter;
    private RecyclerView recyclerView;
    private String skillsStrin = "";
    private TextView tvNoJobPost;
    private TextView tvClearAll;
    private RelativeLayout filterLayout;
    private RecyclerView rvFilter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_offered, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        subCategoryList = new ArrayList<>();
        offerList = new ArrayList<>();
        progressDialog = new ProgressDialog(mContext);
        mainLayout = view.findViewById(R.id.help_offered_layout);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        btnFilter = view.findViewById(R.id.btn_filter);
        tvNoJobPost = view.findViewById(R.id.tv_no_job_post);
        filterLayout = view.findViewById(R.id.filter_layout);
        tvClearAll = view.findViewById(R.id.tv_clear_all);
        rvFilter = view.findViewById(R.id.rv_filter_list);


        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        offeredAdapter = new HelpOfferedAdapter(mContext, offerList);
        recyclerView.setAdapter(offeredAdapter);
        btnFilter.setOnClickListener(this);
        tvClearAll.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { //swipe to refresh rcyclerview data
                swipeRefreshLayout.setRefreshing(false);
                if (Constant.isNetworkAvailable(mContext, mainLayout)) {
                    helpOfferedApi();
                }
            }
        });
        SubCategoryListApi();
        helpOfferedApi();
    }

    private void SubCategoryListApi() {
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
                                    SubCategoryResponse.DataBean dataBean = new SubCategoryResponse.DataBean();

                                    dataBean.setCategoryName("Skills");
                                    subCategoryResponse.getData().add(0, dataBean);
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

    private void helpOfferedApi() {
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "Jobpost/getJobList")
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_type", "1")
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
                            offerList.clear();
                            HelpOfferedResponce helpOfferedResponce = new Gson().fromJson(String.valueOf(response), HelpOfferedResponce.class);
                            offerList.addAll(helpOfferedResponce.getData());
                            offeredAdapter.notifyDataSetChanged();


                        } else {
                            offerList.clear();
                            offeredAdapter.notifyDataSetChanged();
                            if (offerList.size() == 0) {
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
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_filter:
                openFilterDialog();
                break;
            case  R.id.tv_clear_all:
                subCategoryList.clear();
                filterLayout.setVisibility(View.GONE);
                helpOfferedApi();
                break;
            default:
        }
    }

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
            subCategoryAdapter = new SubCategoryAdapter(mContext, subCategoryList);
            recyclerView.setAdapter(subCategoryAdapter);

            ArrayAdapter categoryAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, subCategoryResponse.getData());
            categoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
            subCategorySpinner.setOnItemSelectedListener(this);
            subCategorySpinner.setAdapter(categoryAdapter);
            final StringBuilder sb = new StringBuilder();

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
                        for (int i = 0; i < subCategoryList.size(); i++) {
                            sb.append(subCategoryList.get(i).getCategoryId());
                            sb.append(",");
                        }
                        filterLayout.setVisibility(View.VISIBLE);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                        rvFilter.setLayoutManager(layoutManager);
                        SubCategoryAdapter subCategoryAdapter = new SubCategoryAdapter(mContext, subCategoryList);
                        rvFilter.setAdapter(subCategoryAdapter);
                        skillsStrin = String.valueOf(sb);
                        helpOfferedApi();
                        dialog.dismiss();
                    }

                    //  skillDialogValidation(categorySpinner, addSkillsLayout);
                }
            });

            dialog.show();
            dialog.setCancelable(false);
        } else {
            SubCategoryListApi();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sub_category_spinner:
                if (!subCategoryResponse.getData().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName().equals("Skills")) {
                    CategoryModel category = new CategoryModel();
                    category.setCategoryName(subCategoryResponse.getData().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName());
                    category.setCategoryId(subCategoryResponse.getData().get(subCategorySpinner.getSelectedItemPosition()).getCategoryId());
                    subCategoryList.add(category);
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
}
