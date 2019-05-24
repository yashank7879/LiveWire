package com.livewire.ui.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivityEditShortJobBinding;
import com.livewire.model.JobCreationModel;
import com.livewire.responce.AddSkillsResponce;
import com.livewire.responce.JobDetailClientResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.livewire.ui.activity.CompleteProfileActivity.PLACE_AUTOCOMPLETE_REQUEST_CODE;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.CLIENT_JOB_POST_API;
import static com.livewire.utils.ApiCollection.GET_CATEGORY_LIST_API;
import static com.livewire.utils.ApiCollection.UPDATE_JOB_API;

public class EditShortJobActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = EditShortJobActivity.class.getName();
    ActivityEditShortJobBinding binding;
    private String skillId;
    private String locationPlace;
    private List<String> currencyList = new ArrayList<>();
    private String currency = "";
    private AddSkillsResponce skillsResponce;
    private List<AddSkillsResponce.DataBean.SubcatBean> subcatBeanList;
    private ArrayAdapter<AddSkillsResponce.DataBean.SubcatBean> subCateoryAdapter;

    private ProgressDialog progressDialog;
    private Calendar startDateTime = Calendar.getInstance();
    private int width;
    private LatLng locationLatLng;
    private Spinner subCategorySpinner;
    private String jobId = "";
    private int mYear, mMonth, mDay;
    private String parentSkillId = "";
    private Spinner categorySpinner;
    private boolean isFirst;
    private int subcatPosition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_short_job);
        intializeView();
        if (getIntent().getSerializableExtra("JobDetail") != null) {
            JobDetailClientResponce jobDetail = (JobDetailClientResponce) getIntent().getSerializableExtra("JobDetail");
            setData(jobDetail);
        }
    }

    private void setData(JobDetailClientResponce jobDetail) {
        binding.tvSelectSkill.setText(jobDetail.getData().getSub_category());

        binding.etBudget.setText(jobDetail.getData().getJob_budget());
        binding.etDescription.setText(jobDetail.getData().getJob_description());
        locationLatLng = new LatLng(Double.parseDouble(jobDetail.getData().getJob_latitude()), Double.parseDouble(jobDetail.getData().getJob_longitude()));
        binding.tvLocation.setText(jobDetail.getData().getJob_location());
        binding.currencySpinner.setSelection(1);
        //binding.tvCurrency.setText(binding.currencySpinner.getSelectedItem().toString());
        locationPlace = jobDetail.getData().getJob_location();
        skillId = "" + jobDetail.getData().getCategory_id();
        parentSkillId = "" + jobDetail.getData().getParent_category_id();
        jobId = "" + jobDetail.getData().getJobId();

        String[] dateParts = jobDetail.getData().getJob_start_date().split("-");
        String day = dateParts[2];
        String month = dateParts[1];
        String year = dateParts[0];
        binding.tvSelectDate.setText(day + "-" + month + "-" + year);

        mYear = Integer.parseInt(year);
        mMonth = Integer.parseInt(month) - 1;
        mDay = Integer.parseInt(day);


    }

    private void intializeView() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        addVlaueInCurrency();
        progressDialog = new ProgressDialog(this);
        subcatBeanList = new ArrayList<>();
        //  subcatBeanList = new ArrayList<>();

        //"""""" textview location horizontal move """""""//
        binding.tvLocation.setOnClickListener(this);
        binding.tvLocation.setSelected(true);
        binding.tvLocation.setHorizontallyScrolling(true);
        binding.tvLocation.setMovementMethod(new ScrollingMovementMethod());
        binding.tvLocation.setHint(R.string.location);

        RelativeLayout.LayoutParams newLayoutParams = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        newLayoutParams.addRule(RelativeLayout.BELOW, binding.tvCurrency.getId());
        //newLayoutParams.setMargins(5,5,5,5);
        binding.currencySpinner.setLayoutParams(newLayoutParams);

        ArrayAdapter currencyAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, currencyList);
        currencyAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        binding.currencySpinner.setOnItemSelectedListener(this);
        binding.currencySpinner.setAdapter(currencyAdapter);


        //""""  to hide keyboard """""""//
        binding.etDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.etDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);
        binding.locationRl.setOnClickListener(this);
        binding.selectSkillsRl.setOnClickListener(this);
        binding.selectDateRl.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
        binding.rlCurrencySpinner.setOnClickListener(this);


        //"""""" textWacher for bid price e.g "12345.99"
        binding.etBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*this method is not used*/
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*this method is not used*/
            }

            @Override
            public void afterTextChanged(Editable editable) { //""""""""" price format "15455.15"
                String str = binding.etBudget.getText().toString();
                if (str.isEmpty()) return;
                String str2 = Constant.perfectDecimal(str, 6, 2);
                if (!str2.equals(str)) {
                    binding.etBudget.setText(str2);
                    int pos = binding.etBudget.getText().length();
                    binding.etBudget.setSelection(pos);
                }
            }
        });

        loadSkillsData();
        binding.ivBack.setOnClickListener(this);
    }

    private void addVlaueInCurrency() {
        currencyList.add("Currency");
        currencyList.add("ZAR (R)");
    }

    //"""""" load skills data """"""""""//
    private void loadSkillsData() {
        if (Constant.isNetworkAvailable(this, binding.svSingleJob)) {
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
                            Constant.snackBar(binding.svSingleJob, message);
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
            case R.id.select_skills_rl:
                openSkillDialog();
                break;
            case R.id.select_date_rl:
                openDatePicker();
                break;
            case R.id.location_rl:
                autoCompletePlacePicker();
                break;
            case R.id.tv_location:
                autoCompletePlacePicker();
                break;
            case R.id.btn_share:
                jobValidations();
                break;

            case R.id.rl_currency_spinner:
                binding.currencySpinner.performClick();
                break;
        }
    }

    ///"""""""""" validations """""""""""""""//
    private void jobValidations() {
        if (Validation.isEmpty(binding.tvSelectSkill)) {
            Constant.snackBar(binding.svSingleJob, "Please Select Skill");
        } else if (Validation.isEmpty(binding.tvSelectDate)) {
            Constant.snackBar(binding.svSingleJob, "Please Select Date");
        } else if (Validation.isEmpty(binding.etBudget)) {
            Constant.snackBar(binding.svSingleJob, "Please enter Budget");
        } else if (currency.equals("Currency")) {
            Constant.snackBar(binding.svSingleJob, "Please select currency");
        } else if (binding.etBudget.getText().toString().trim().equals("0")) {
            Constant.snackBar(binding.svSingleJob, "Please enter correct Budget");
        } else if (Float.parseFloat(binding.etBudget.getText().toString()) < 1) {
            Constant.snackBar(binding.svSingleJob, "Budget price should not be less than 1 Rand");
        } else if (Validation.isEmpty(binding.tvLocation)) {
            Constant.snackBar(binding.svSingleJob, "Please enter your Location");
        }/* else if (Validation.isEmpty(etDescription)) {
            Constant.snackBar(mainLayout, "Please enter job Description");
        }*/ else {
            JobCreationModel model = new JobCreationModel();
            model.skill = skillId;
            model.job_id = jobId;
            model.job_start_date = binding.tvSelectDate.getText().toString();
            model.job_location = locationPlace;
            model.job_latitude = String.valueOf(locationLatLng.latitude);
            model.job_longitude = String.valueOf(locationLatLng.longitude);
            model.job_budget = Float.parseFloat(binding.etBudget.getText().toString());
            model.job_title = "test";
            model.currency = "ZAR";
            model.job_type = "1";
            model.job_description = binding.etDescription.getText().toString();
            singleJobUpdateApi(model);
        }
    }

    //"""""""" open date picker """""""""""'//
    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
      /*  mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);*/
        Constant.hideSoftKeyBoard(this, binding.tvLocation);
        DatePickerDialog startDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDateTime = Calendar.getInstance();
                startDateTime.set(Calendar.YEAR, year);
                startDateTime.set(Calendar.MONTH, month);
                startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //********Date time Format**************//
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy ");
                if (startDateTime.getTimeInMillis() >= (System.currentTimeMillis() - 1000)) {
                    binding.tvSelectDate.setText(sdf1.format(startDateTime.getTime()));
                } else {
                    Toast.makeText(EditShortJobActivity.this, "Can't select past date", Toast.LENGTH_SHORT).show();
                }
                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
                //  startDateString = sdf1.format(startDateTime.getTime());
            }
        }, mYear, mMonth, mDay);


        startDateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        startDateDialog.show();
    }

    //"""""""""""" single job creation """""""""" //
    private void singleJobUpdateApi(JobCreationModel model) {
        if (Constant.isNetworkAvailable(this, binding.svSingleJob)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + UPDATE_JOB_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter(model)
                    .setPriority(Priority.MEDIUM)
                    .setContentType("text/plain; charset=utf-8")
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            // Constant.snackBar(binding.svSingleJob, "Your offer has been successfully sent.");
                            binding.tvSelectSkill.setText("");
                            binding.tvLocation.setText("");
                            binding.tvSelectDate.setText("");
                            binding.etBudget.setText("");
                            binding.etDescription.setText("");
                            binding.currencySpinner.setSelection(0);
                            onBackPressed();
                        } else {
                            Constant.snackBar(binding.svSingleJob, message);
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Constant.errorHandle(anError, EditShortJobActivity.this);
                    Log.d(TAG, anError.getErrorDetail());
                    progressDialog.dismiss();
                }
            });
        }
    }


    //""""""""""" open skills data """""""""""//
    private void openSkillDialog() {
        if (Constant.isNetworkAvailable(this, binding.svSingleJob)) {
            final Dialog dialog = new Dialog(this);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.select_skills_dialog);
            dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            final RelativeLayout addSkillsLayout = dialog.findViewById(R.id.select_skill_rl);

            TextView tvHeader = dialog.findViewById(R.id.tv_header);
            tvHeader.setText(R.string.select_skill);
            categorySpinner = dialog.findViewById(R.id.category_spinner);
            subCategorySpinner = dialog.findViewById(R.id.sub_category_spinner);
            Button btnAddSkills = dialog.findViewById(R.id.btn_add_skills);

            ArrayAdapter categoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, skillsResponce.getData());
            categoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
            categorySpinner.setOnItemSelectedListener(this);
            categorySpinner.setAdapter(categoryAdapter);

            subCateoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, subcatBeanList);
            subCateoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
            subCategorySpinner.setOnItemSelectedListener(this);
            subCategorySpinner.setAdapter(subCateoryAdapter);

            setDataInSpinner();

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btnAddSkills.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    skillDialogValidation(categorySpinner, addSkillsLayout, dialog);
                }
            });

            dialog.show();
            dialog.setCancelable(false);
        }
    }

    private void setDataInSpinner() {
        for (int i = 0; i < skillsResponce.getData().size(); i++) {
            AddSkillsResponce.DataBean dataBean = skillsResponce.getData().get(i);
            if (dataBean.getCategoryId().equals(parentSkillId)) {//parent
                categorySpinner.setSelection(i);
                for (int i1 = 0; i1 < dataBean.getSubcat().size(); i1++) { //child
                    AddSkillsResponce.DataBean.SubcatBean subcatBean = dataBean.getSubcat().get(i1);
                    if (subcatBean.getCategoryId().equals(skillId)) {
                        subcatPosition = i1;
                        Log.e(TAG, "test " + subcatBean.getCategoryName());

                    }
                    subCategorySpinner.setSelection(subcatPosition);
                    subCateoryAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    //"""""""" dialog validations """"""""""""""//
    private void skillDialogValidation(Spinner categorySpinner, RelativeLayout addSkillsLayout, Dialog dialog) {
        if (skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getCategoryName().equals("Select Category")) {
            Constant.snackBar(addSkillsLayout, "Please Select Category");
        } else if (skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName().equals("Select Subcategory")) {
            Constant.snackBar(addSkillsLayout, "Please Select Subcategory");
        } else {
            dialog.dismiss();
            binding.tvSelectSkill.setText(skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName());
            skillId = skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryId();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.category_spinner:
                Log.e(TAG, skillsResponce.getData().get(position).getCategoryName());
                subcatBeanList.clear();
                subcatBeanList.addAll(skillsResponce.getData().get(position).getSubcat());
                subCategorySpinner.setSelection(subcatPosition);
                subCateoryAdapter.notifyDataSetChanged();
                break;
            case R.id.currency_spinner:
                currency = currencyList.get(position);
                if (!currency.equals("Currency"))
                    binding.tvCurrency.setText(currency);
                break;

            case R.id.sub_category_spinner:
                if (position != 0) {
                   // Log.e(TAG, subcatBeanList.get(position).getCategoryName());
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*******Auto comple place picker***************/
    private void autoCompletePlacePicker() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Place place = PlaceAutocomplete.getPlace(this, data);
                binding.tvLocation.setText(place.getAddress());
                locationPlace = place.getAddress().toString();
                locationLatLng = place.getLatLng();
                Log.e(TAG, "Place: " + place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status1 = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status1.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                Constant.hideSoftKeyBoard(this, binding.tvLocation);
            }
        }
    }
}
