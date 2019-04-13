package com.livewire.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.CategaryAdapter;
import com.livewire.databinding.ActivityAddYourSkillsBinding;
import com.livewire.model.AddedSkillBean;
import com.livewire.model.CategoryModel;
import com.livewire.model.SubCategoryModel;
import com.livewire.responce.AddSkillsResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PermissionAll;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.GET_CATEGORY_LIST_API;

public class AddYourSkillsActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {
    ActivityAddYourSkillsBinding binding;
    private int width;
    private Spinner subCategorySpinner;
    private ArrayAdapter categoryAdapter;
    private AddSkillsResponce skillsResponce;
    private ProgressDialog progressDialog;
    private List<CategoryModel> category = new ArrayList<>();
    private ArrayList<AddedSkillBean> addedSkillBeans = new ArrayList<>();
    private CategaryAdapter addSkillsAdapter;
    private ArrayAdapter<AddSkillsResponce.DataBean.SubcatBean> subCateoryAdapter;
    private ArrayList<SubCategoryModel> subCategoryModelList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_your_skills);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        //""""""" all permission """"""""""""
        PermissionAll permissionAll = new PermissionAll();
        permissionAll.RequestMultiplePermission(AddYourSkillsActivity.this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvSkills.setLayoutManager(layoutManager);
        addSkillsAdapter = new CategaryAdapter(this, addedSkillBeans);
        binding.rvSkills.setAdapter(addSkillsAdapter);
        binding.rvSkills.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);

        /* categoryAdapter = new ArrayAdapter<>(AddYourSkillsActivity.this, R.layout.spinner_item, skillsResponce.getData());
        categoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);*/
        binding.btnNext.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.addSkillsRl.setOnClickListener(this);
        loadSkillsData();
    }

    //"""""""""""" skills data """""""""""""""""""""//
    private void loadSkillsData() {
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + GET_CATEGORY_LIST_API)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            skillsResponce = new Gson().fromJson(String.valueOf(response), AddSkillsResponce.class);

                            for (AddSkillsResponce.DataBean dataBean : skillsResponce.getData()) {
                                CategoryModel categoryModel = new CategoryModel();
                                categoryModel.setCategoryId(dataBean.getCategoryId());
                                categoryModel.setCategoryName(dataBean.getCategoryName());
                                category.add(categoryModel);
                            }

                            for (AddSkillsResponce.DataBean dataBean : skillsResponce.getData()) {
                                AddSkillsResponce.DataBean.SubcatBean subcatBean = new AddSkillsResponce.DataBean.SubcatBean();
                                subcatBean.setCategoryName("Select Subcategory");
                                dataBean.getSubcat().add(0, subcatBean);
                            }
                            AddSkillsResponce.DataBean dataBean = new AddSkillsResponce.DataBean();
                            AddSkillsResponce.DataBean.SubcatBean subcatBean = new AddSkillsResponce.DataBean.SubcatBean();
                            subcatBean.setCategoryName("Select Subcategory");
                            dataBean.setCategoryName("Select Category");
                            dataBean.setSubcat(new ArrayList<AddSkillsResponce.DataBean.SubcatBean>());
                            dataBean.getSubcat().add(subcatBean);
                            skillsResponce.getData().add(0, dataBean);
                        } else {
                            Constant.snackBar(binding.mainLayout, message);
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

    private String getWorkerSkillData() {//it gives array format subcategory data  [{categor:10,Min_rate:2,Max_rate:10}]
        subCategoryModelList.clear();
        String json = null;
        for (int i = 0; i < addedSkillBeans.size(); i++) {
            for (int j = 0; j < addedSkillBeans.get(i).getSubCatagories().size(); j++) {
                SubCategoryModel model = new SubCategoryModel();
                model.setCategory_id(addedSkillBeans.get(i).getSubCatagories().get(j).getSubCatId());
                model.setMax_rate(addedSkillBeans.get(i).getSubCatagories().get(j).getMax_rate());
                model.setMin_rate(addedSkillBeans.get(i).getSubCatagories().get(j).getMin_rate());
                subCategoryModelList.add(model);
            }
        }
        json = new Gson().toJson(subCategoryModelList);

        return json;
    }

    private void nextBtnValidations() {
        if (addedSkillBeans.size() == 0) {
            Constant.snackBar(binding.mainLayout, "please add your skills");
        } else if (subCategoryModelList.size() == 0) {
            Constant.snackBar(binding.mainLayout, "please add your Sub category");
        } else {
            Intent intent = new Intent(this, UploadIntroVideoActivity.class);
            intent.putExtra("Skills", getWorkerSkillData());
            startActivity(intent);
            // startActivity(new Intent(this, UploadIntroVideoActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                getWorkerSkillData();
                nextBtnValidations();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_cancel:
                finishAffinity();
                Intent intent = new Intent(this, ClientMainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.add_skills_rl:
                showAddSkillsDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {// it will move to the Worker main activity
        super.onBackPressed();
        finishAffinity();
        Intent intent = new Intent(this,WorkerMainActivity.class);
        intent.putExtra("workerMyProfile","workerMyProfile");
        startActivity(intent);
        finish();
    }

    //"""""""""show dialog for add skills""""""""""""""""//
    private void showAddSkillsDialog() {
        if (Constant.isNetworkAvailable(this, binding.mainLayout) && skillsResponce != null) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.add_skills_dialog);
            dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            final RelativeLayout addSkillsLayout = dialog.findViewById(R.id.add_skills_layout);
            final EditText etMinPrice = dialog.findViewById(R.id.et_min_price);
            final EditText etMaxPrice = dialog.findViewById(R.id.et_max_price);
            Button btnAddSkills = dialog.findViewById(R.id.btn_add_skills);
            final Spinner categorySpinner = dialog.findViewById(R.id.category_spinner);
            subCategorySpinner = dialog.findViewById(R.id.sub_category_spinner);

            categoryAdapter = new ArrayAdapter<>(AddYourSkillsActivity.this, R.layout.spinner_item, skillsResponce.getData());
            categoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
            categorySpinner.setOnItemSelectedListener(this);
            categorySpinner.setAdapter(categoryAdapter);
            Constant.hideSoftKeyBoard(AddYourSkillsActivity.this, etMaxPrice);
            btnAddSkills.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogValidations(categorySpinner, etMinPrice, etMaxPrice, addSkillsLayout, dialog);
                    Constant.hideSoftKeyBoard(AddYourSkillsActivity.this, etMaxPrice);

                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.hideSoftKeyBoard(AddYourSkillsActivity.this, etMaxPrice);
                    dialog.dismiss();
                }
            });

            etMaxPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String str = etMaxPrice.getText().toString();
                    if (str.isEmpty()) return;
                    String str2 = perfectDecimal(str, 6, 2);
                    if (!str2.equals(str)) {
                        etMaxPrice.setText(str2);
                        int pos = etMaxPrice.getText().length();
                        etMaxPrice.setSelection(pos);
                    }
                }
            });
            etMinPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String str = etMinPrice.getText().toString();
                    if (str.isEmpty()) return;
                    String str2 = perfectDecimal(str, 6, 2);
                    if (!str2.equals(str)) {
                        etMinPrice.setText(str2);
                        int pos = etMinPrice.getText().length();
                        etMinPrice.setSelection(pos);
                    }
                }
            });


            dialog.setCancelable(false);
            dialog.show();
        } else loadSkillsData();
    }


    ///"""""""""""" dialog validationd""""""""""""
    private void dialogValidations(Spinner categorySpinner, TextView minPrice, TextView maxPrice, RelativeLayout addSkillsLayout, Dialog dialog) {
/*        int minPric = Integer.parseInt(minPrice.getText().toString());
        int maxPric = Integer.parseInt(maxPrice.getText().toString());*/
        if (skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getCategoryName().equals("Select Category")) {
            Constant.snackBar(addSkillsLayout, "Please Select Category");
        } else if (skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName().equals("Select Subcategory")) {
            Constant.snackBar(addSkillsLayout, "Please Select Subcategory");
        } else if (minPrice.getText().toString().equals("") || minPrice.getText().toString().length() == 0) {
            Constant.snackBar(addSkillsLayout, "Please enter min price");
            minPrice.requestFocus();
        } else if (maxPrice.getText().toString().equals("") || maxPrice.getText().toString().length() == 0) {
            Constant.snackBar(addSkillsLayout, "Please enter max price");
            maxPrice.requestFocus();
        }/*else if ((maxPric-minPric) < 0){
            Constant.snackBar(addSkillsLayout, "Min price and Max price can't be same");
            maxPrice.requestFocus();
        }*/ else if (Float.parseFloat(minPrice.getText().toString()) >= Float.parseFloat(maxPrice.getText().toString())) {
            Constant.snackBar(addSkillsLayout, "Min price always less than Max price");
            minPrice.requestFocus();
        } else {
            Constant.hideSoftKeyBoard(this, minPrice);
            AddSkillsResponce.DataBean dataBean = skillsResponce.getData().get(categorySpinner.getSelectedItemPosition());
            AddedSkillBean addedSkillBean = new AddedSkillBean();
            addedSkillBean.setCatageryId(dataBean.getCategoryId());
            addedSkillBean.setName(dataBean.getCategoryName());

            AddSkillsResponce.DataBean.SubcatBean subcatBean = skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition());
            AddedSkillBean.SubCatagory subCatagory = new AddedSkillBean.SubCatagory();
            subCatagory.setSubCatId(subcatBean.getCategoryId());
            subCatagory.setSubName(subcatBean.getCategoryName());
            subCatagory.setMax_rate(Float.parseFloat(maxPrice.getText().toString()));
            subCatagory.setMin_rate(Float.parseFloat(minPrice.getText().toString()));

            boolean isExsist = false;
            for (AddedSkillBean bean : addedSkillBeans) {
                if (bean.getCatageryId().equals(addedSkillBean.getCatageryId())) {
                    for (AddedSkillBean.SubCatagory subCatagory1 : bean.getSubCatagories()) {
                        if (subCatagory1.getSubCatId().equals(subCatagory.getSubCatId())) {
                            Toast.makeText(AddYourSkillsActivity.this, "This subcategory is already added.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    bean.getSubCatagories().add(subCatagory);
                    isExsist = true;
                    break;
                }
            }
            if (!isExsist) {
                addedSkillBean.setSubCatagories(new ArrayList<AddedSkillBean.SubCatagory>());
                addedSkillBean.getSubCatagories().add(subCatagory);
                addedSkillBeans.add(addedSkillBean);
            }

            String json = new Gson().toJson(addedSkillBeans);
            addSkillsAdapter.notifyDataSetChanged();
            Log.e("json: ", json);

            if (addedSkillBeans.size() > 0) {
                binding.rvSkills.setVisibility(View.VISIBLE);

            } else {
                binding.rvSkills.setVisibility(View.GONE);
            }
            dialog.dismiss();
            // uploadVideo();
        }

    }


    public String perfectDecimal(String str, int maxBeforePoint, int maxDecimal) { //price format "15455.15"
        if (str.charAt(0) == '.') str = "0" + str;
        int max = str.length();

        String rFinal = "";
        boolean after = false;
        int i = 0;
        int up = 0;
        int decimal = 0;
        char t;
        while (i < max) {
            t = str.charAt(i);
            if (t != '.' && !after) {
                up++;
                if (up > maxBeforePoint) return rFinal;
            } else if (t == '.') {
                after = true;
            } else {
                decimal++;
                if (decimal > maxDecimal)
                    return rFinal;
            }
            rFinal = rFinal + t;
            i++;
        }
        return rFinal;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        switch (parent.getId()) {
            case R.id.category_spinner:
                /*if (position >= 1) {*/
                // Log.e(TAG, skillsResponce.getData().get(position).getCategoryName());
                subCateoryAdapter = new ArrayAdapter<>(AddYourSkillsActivity.this, R.layout.spinner_item, skillsResponce.getData().get(position).getSubcat());
                subCateoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
                subCategorySpinner.setOnItemSelectedListener(this);
                subCategorySpinner.setAdapter(subCateoryAdapter);

                // ivCategorySpin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spinner_icon_rotator));
                //   }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceConnector.writeString(this, PreferenceConnector.USER_DOB, "");
        PreferenceConnector.writeString(this, PreferenceConnector.SELECTED_VIDEO, "");
        PreferenceConnector.writeString(this, PreferenceConnector.ABOUT_ME, "");
    }
}
