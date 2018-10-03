package com.livewire.ui.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.livewire.model.JobCreationModel;
import com.livewire.responce.AddSkillsResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.livewire.ui.activity.CompleteProfileActivity.PLACE_AUTOCOMPLETE_REQUEST_CODE;
import static com.livewire.utils.ApiCollection.BASE_URL;

/**
 * Created by mindiii on 9/28/18.
 */

public class OngoingJobFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = OngoingJobFragment.class.getName();
    private TextView tvSelectedSkill;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private TextView tvWeekDays;
    private TextView tvHourRequierd;
    private TextView tvLocation;
    private EditText etDescription;
    private Context mContext;
    private ScrollView mainLayout;
    private String locationPlace;
    private LatLng locationLatLng;
    private int width;
    private ProgressDialog progressDialog;
    private Spinner subCategorySpinner;
    private AddSkillsResponce skillsResponce;
    private String skillId;
    private int mYear;
    private int mMonth;
    private int mDay;
    private Calendar startDateTime;
    private String startDateString="";
    private String endDateString="";
    private Calendar endDateTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ongoing_job, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);

        RelativeLayout selectSkillsRl = view.findViewById(R.id.select_skills_rl);
        tvSelectedSkill = view.findViewById(R.id.tv_selected_skill);
        RelativeLayout jobStartDateRl =  view.findViewById(R.id.job_start_date_rl);
        tvStartDate =  view.findViewById(R.id.tv_start_date);
        RelativeLayout jobEndDateRl =  view.findViewById(R.id.job_end_date_rl);
        tvEndDate = view.findViewById(R.id.tv_end_date);
        RelativeLayout weekDaysRl = view.findViewById(R.id.week_days_rl);
        tvWeekDays =  view.findViewById(R.id.tv_week_days);
        tvHourRequierd = view.findViewById(R.id.tv_hour_requierd);
        RelativeLayout locationRl = view.findViewById(R.id.location_rl);
        tvLocation = view.findViewById(R.id.tv_location);
        RelativeLayout descriptionRl = view.findViewById(R.id.description_rl);
        etDescription = view.findViewById(R.id.description_tv);
        mainLayout = view.findViewById(R.id.sv_ongoing_job);

        selectSkillsRl.setOnClickListener(this);
        jobStartDateRl.setOnClickListener(this);
        jobEndDateRl.setOnClickListener(this);
        weekDaysRl.setOnClickListener(this);
        locationRl.setOnClickListener(this);
        view.findViewById(R.id.btn_share).setOnClickListener(this);
        loadSkillsData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_skills_rl:
                openSkillDialog();
                break;
            case R.id.job_start_date_rl:
                openStartDateDialog();
                break;
            case R.id.job_end_date_rl:
                openEndDateDialog();
                break;
            case R.id.week_days_rl:

                break;
            case R.id.location_rl:
                autoCompletePlacePicker();
                break;
            case R.id.btn_share:
                jobValidations();
                break;
        }
    }

    private void jobValidations() {
        if (Validation.isEmpty(tvSelectedSkill)) {
            Constant.snackBar(mainLayout, "Please Select Skill");
        } else if (Validation.isEmpty(tvStartDate)) {
            Constant.snackBar(mainLayout, "Please enter Start date");
        } else if (Validation.isEmpty(tvEndDate)) {
            Constant.snackBar(mainLayout, "Please enter End date");
        } else if (Validation.isEmpty(tvWeekDays)) {
            Constant.snackBar(mainLayout, "Please Select week days");
        } else if (Validation.isEmpty(tvHourRequierd)) {
            Constant.snackBar(mainLayout, "Please enter hour required");
        } else if (Validation.isEmpty(tvLocation)) {
            Constant.snackBar(mainLayout, "Please enter your Location");
        } else if (Validation.isEmpty(etDescription)) {
            Constant.snackBar(mainLayout, "Please enter job Description");
        } else {
            JobCreationModel model = new JobCreationModel();
            model.skill = skillId;
            model.job_start_date = tvStartDate.getText().toString();
            model.job_end_date = tvEndDate.getText().toString();
            model.job_location = locationPlace;
            model.job_latitude = String.valueOf(locationLatLng.latitude);
            model.job_longitude = String.valueOf(locationLatLng.longitude);
            model.job_week_days = tvWeekDays.getText().toString();
            model.job_type = "2";
            model.job_title = "test";
            model.job_description = etDescription.getText().toString();


            singleJobCreationApi(model);
        }
    }

    private void singleJobCreationApi(JobCreationModel model) {
        Constant.snackBar(mainLayout,"Success");
    }

    private void openEndDateDialog() {
        if (!startDateString.equals("")) {
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            Constant.hideSoftKeyBoard(mContext, etDescription);
            DatePickerDialog endDateDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    endDateTime = Calendar.getInstance();
                    endDateTime.set(Calendar.YEAR, year);
                    endDateTime.set(Calendar.MONTH, month);
                    endDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    //********Date time Format**************//
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy ");
                    tvEndDate.setText(sdf2.format(endDateTime.getTime()));
                    endDateString = sdf2.format(endDateTime.getTime());
                }

            }, mYear, mMonth, mDay);
            endDateDialog.getDatePicker().setMinDate(startDateTime.getTimeInMillis()); //set min date
            endDateDialog.show();
        } else {
            Constant.snackBar(mainLayout, "Please select start date first");
        }
    }

    private void openStartDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        Constant.hideSoftKeyBoard(mContext, etDescription);
        final DatePickerDialog startDateDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                startDateTime = Calendar.getInstance();
                startDateTime.set(Calendar.YEAR, year);
                startDateTime.set(Calendar.MONTH, month);
                startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //********Date time Format**************//
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy ");
                tvStartDate.setText(sdf1.format(startDateTime.getTime()));
                startDateString = sdf1.format(startDateTime.getTime());

            }
        }, mYear, mMonth, mDay);
        if (endDateString.equals("")) {
            startDateDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        } else {
            startDateDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            tvEndDate.setText("");
            endDateTime.clear();
        }
        startDateDialog.show();

    }


    /*******Auto comple place picker***************/
    private void autoCompletePlacePicker() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(getActivity());
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
                Place place = PlaceAutocomplete.getPlace(mContext, data);

                tvLocation.setText(place.getAddress());
                locationPlace = place.getAddress().toString();
                locationLatLng = place.getLatLng();
                Log.e(TAG, "Place: " + place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status1 = PlaceAutocomplete.getStatus(mContext, data);

                Log.i(TAG, status1.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {

                Constant.hideSoftKeyBoard(mContext, tvLocation);
            }
        }
    }

    private void openSkillDialog() {
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.select_skills_dialog);
            dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            final RelativeLayout addSkillsLayout = dialog.findViewById(R.id.select_skill_rl);

            final Spinner categorySpinner = dialog.findViewById(R.id.category_spinner);
            subCategorySpinner = dialog.findViewById(R.id.sub_category_spinner);
            Button btnAddSkills = dialog.findViewById(R.id.btn_add_skills);

            ArrayAdapter categoryAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, skillsResponce.getData());
            categoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
            categorySpinner.setOnItemSelectedListener(this);
            categorySpinner.setAdapter(categoryAdapter);


            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btnAddSkills.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    skillDialogValidation(categorySpinner, addSkillsLayout,dialog);
                }
            });

            dialog.show();
            dialog.setCancelable(false);
        }
    }

    private void skillDialogValidation(Spinner categorySpinner, RelativeLayout addSkillsLayout, Dialog dialog) {
        if (skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getCategoryName().equals("Select Category")) {
            Constant.snackBar(addSkillsLayout, "Please Select Category");
        } else if (skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName().equals("Select Subcategory")) {
            Constant.snackBar(addSkillsLayout, "Please Select Subcategory");
        } else {
            dialog.dismiss();
            tvSelectedSkill.setText(skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName());
            skillId = skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryId();
            Log.e("openSkillDialog: ", skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName());
        }
    }

    private void loadSkillsData() {
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + "getCategoryList")
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
                            Constant.snackBar(mainLayout, message);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.category_spinner:
                /*if (position >= 1) {*/
                Log.e(TAG, skillsResponce.getData().get(position).getCategoryName());
                ArrayAdapter<AddSkillsResponce.DataBean.SubcatBean> subCateoryAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, skillsResponce.getData().get(position).getSubcat());
                subCateoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
                subCategorySpinner.setOnItemSelectedListener(this);
                subCategorySpinner.setAdapter(subCateoryAdapter);
                Log.d("onItemSelected: ", parent.getSelectedItem().toString());
                // ivCategorySpin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spinner_icon_rotator));
                //   }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

