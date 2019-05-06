package com.livewire.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
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
import com.livewire.adapter.SelectWeekAdapter;
import com.livewire.databinding.ActivityEditLongTermJobBinding;
import com.livewire.model.JobCreationModel;
import com.livewire.model.WeekListModel;
import com.livewire.responce.AddSkillsResponce;
import com.livewire.responce.JobDetailClientResponce;
import com.livewire.ui.fragments.OngoingJobFragment;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.livewire.ui.activity.CompleteProfileActivity.PLACE_AUTOCOMPLETE_REQUEST_CODE;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.CLIENT_JOB_POST_API;
import static com.livewire.utils.ApiCollection.GET_CATEGORY_LIST_API;
import static com.livewire.utils.ApiCollection.UPDATE_JOB_API;

public class EditLongTermJobActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ActivityEditLongTermJobBinding binding;
    private static final String TAG = EditLongTermJobActivity.class.getName();
    private String locationPlace;
    private LatLng locationLatLng;
    private int width;
    private ProgressDialog progressDialog;
    private Spinner subCategorySpinner;
    private AddSkillsResponce skillsResponce;
    private String skillId;
    private String jobId;
    private int mYear;
    private int mMonth;
    private int mDay;
    private Calendar startDateTime =Calendar.getInstance();
    private String startDateString = "";
    private String endDateString = "";
    private Calendar endDateTime =Calendar.getInstance();
    private ArrayList<WeekListModel> weekList;
    List<String> hourList;
    private ArrayAdapter<String> hourRequireAdapter;
    private int eYear,eMonth,eDay;
    private boolean isClickStart=false;
    private String hourRequired="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_long_term_job);
        intializeView();
        if (getIntent().getSerializableExtra("JobDetail") != null){
            JobDetailClientResponce jobDetail = (JobDetailClientResponce) getIntent().getSerializableExtra("JobDetail");
            setData(jobDetail);
        }
    }

    private void setData(JobDetailClientResponce jobDetail) {
        skillId = jobDetail.getData().getCategory_id();
        jobId = jobDetail.getData().getJobId();
        binding.tvSelectedSkill.setText(jobDetail.getData().getSub_category());
        binding.tvStartDate.setText(jobDetail.getData().getJob_start_date());
        binding.tvEndDate.setText(jobDetail.getData().getJob_end_date());
        binding.tvWeekDays.setText(jobDetail.getData().getJob_week_days());
        locationLatLng = new LatLng(Double.parseDouble(jobDetail.getData().getJob_latitude()), Double.parseDouble(jobDetail.getData().getJob_longitude()));
        binding.etDescription.setText(jobDetail.getData().getJob_description());
        binding.tvLocation.setText(jobDetail.getData().getJob_location());
        locationPlace = jobDetail.getData().getJob_location();

        String[] sdate = jobDetail.getData().getJob_start_date().split("-");
        String day = sdate[2];
        String month = sdate[1];
        String year = sdate[0];
        binding.tvStartDate.setText(day+"-"+month+"-"+year);
        startDateString = binding.tvStartDate.getText().toString();
        mYear = Integer.parseInt(year);
        mMonth = Integer.parseInt(month)-1;
        mDay = Integer.parseInt(day);
        startDateTime.set(Integer.parseInt(year),Integer.parseInt(month)-1,Integer.parseInt(day));

        String[] edate = jobDetail.getData().getJob_end_date().split("-");
        String day1 = edate[2];
        String month1 = edate[1];
        String year1 = edate[0];
        binding.tvEndDate.setText(day1+"-"+month1+"-"+year1);
        endDateString = binding.tvEndDate.getText().toString();
        eDay = Integer.parseInt(day1);
        eMonth = Integer.parseInt(month1)-1;
        eYear = Integer.parseInt(year1);

        List<String> list = new ArrayList<String>(Arrays.asList(jobDetail.getData().getJob_week_days().split(",")));
        for (int i = 0; i < weekList.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (weekList.get(i).getWeekDays().equalsIgnoreCase(list.get(j))){
                    weekList.get(i).setisWeekDay(true);
                }
            }
        }
        for (int i = 0; i < hourList.size(); i++) {
            if (hourList.get(i).equals(jobDetail.getData().getJob_time_duration())) {
                binding.hourRequireSpinner.setSelection(i);
                hourRequireAdapter.notifyDataSetChanged();
            }
        }
    }

    private void intializeView() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        progressDialog = new ProgressDialog(this);

        weekList = new ArrayList<>();
        weekList.add(new WeekListModel("Monday", false));
        weekList.add(new WeekListModel("Tuesday", false));
        weekList.add(new WeekListModel("Wednesday", false));
        weekList.add(new WeekListModel("Thursday", false));
        weekList.add(new WeekListModel("Friday", false));
        weekList.add(new WeekListModel("Saturday", false));
        weekList.add(new WeekListModel("Sunday", false));

        //etHourRequierd.addTextChangedListener(watcherClass);
        binding.selectSkillsRl.setOnClickListener(this);
        binding.jobStartDateRl.setOnClickListener(this);
        binding.jobEndDateRl.setOnClickListener(this);
        binding.weekDaysRl.setOnClickListener(this);
        binding.locationRl.setOnClickListener(this);
        binding.btnShare.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.rlHourRequierd.setOnClickListener(this);

        //""""  to hide keyboard """""""//
        binding.etDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.etDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);

        openHourAdapter();
        //"""""""""" don't select first spinner value """""""""""""//
        RelativeLayout.LayoutParams newLayoutParams = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        newLayoutParams.addRule(RelativeLayout.BELOW, binding.tvHourRequierd.getId());
        //newLayoutParams.setMargins(5,5,5,5);
        binding.hourRequireSpinner.setLayoutParams(newLayoutParams);

        hourList = new ArrayList<>();
        hourRequireAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, hourList);
        hourRequireAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        binding.hourRequireSpinner.setOnItemSelectedListener(this);
        binding.hourRequireSpinner.setAdapter(hourRequireAdapter);

        binding.tvLocation.setOnClickListener(this);
        binding.tvLocation.setSelected(true);
        binding.tvLocation.setHorizontallyScrolling(true);
        binding.tvLocation.setMovementMethod(new ScrollingMovementMethod());

        binding.tvWeekDays.setOnClickListener(this);
        binding.tvWeekDays.setSelected(true);
        binding.tvWeekDays.setHorizontallyScrolling(true);
        binding.tvWeekDays.setMovementMethod(new ScrollingMovementMethod());

        loadSkillsData();
    }

    @SuppressLint("StaticFieldLeak")
    private void openHourAdapter() {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                try {
                    hourList.add(0, "Hour Required Per Day");
                    double hour = 0.5;
                    for (int i = 0; i <= 48; i++) {
                        if (hour < 24) {
                            hour += 0.5;
                            hourList.add(String.valueOf(hour));
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                return hourList;
            }

            @Override
            protected void onPostExecute(List<String> hour) {
                super.onPostExecute(hour);
                hourRequireAdapter.notifyDataSetChanged();
            }
        }.execute();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
                openWeekDialog();
                break;
            case R.id.tv_week_days:
                openWeekDialog();
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
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.rl_hour_requierd:
                binding.hourRequireSpinner.performClick();
                break;
            default:

                break;
        }
    }


    //""""start date picker dialog """""""""""""//

    private void openStartDateDialog() {
        final Calendar calendar = Calendar.getInstance();
       /* mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);*/
        Constant.hideSoftKeyBoard(this, binding.etDescription);
         DatePickerDialog startDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               // startDateTime = Calendar.getInstance();
                startDateTime.set(Calendar.YEAR, year);
                startDateTime.set(Calendar.MONTH, month);
                startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //******** Date time Format **************//
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                startDateString = sdf1.format(startDateTime.getTime());
                if (startDateTime.getTimeInMillis() >= (System.currentTimeMillis() - 6000)) {
                    binding.tvStartDate.setText(sdf1.format(startDateTime.getTime()));
                } else {
                    Toast.makeText(EditLongTermJobActivity.this, "Can't select past date", Toast.LENGTH_SHORT).show();
                }
                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
            }
        }, mYear, mMonth, mDay);

        if (endDateString.equals("")) {
            startDateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else {
            binding.tvEndDate.setText("");
            endDateTime.clear();
            startDateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }
        isClickStart= true;
        startDateDialog.show();
     }

    //""""End date picker dialog """""""""""""//
    private void openEndDateDialog() {
        if (!startDateString.equals("")) {
             Calendar calendar = Calendar.getInstance();
            if (isClickStart) {
                eYear = calendar.get(Calendar.YEAR);
                eMonth = calendar.get(Calendar.MONTH);
                eDay = calendar.get(Calendar.DAY_OF_MONTH);
            }
            Constant.hideSoftKeyBoard(this, binding.etDescription);
            DatePickerDialog endDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                  //  endDateTime = Calendar.getInstance();
                    endDateTime.set(Calendar.YEAR, year);
                    endDateTime.set(Calendar.MONTH, month);
                    endDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    //********Date time Format**************//
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy ");
                    endDateString = sdf2.format(endDateTime.getTime());
                    if (startDateTime.getTimeInMillis() < endDateTime.getTimeInMillis()) {
                        binding.tvEndDate.setText(sdf2.format(endDateTime.getTime()));
                    } else {
                        Toast.makeText(EditLongTermJobActivity.this, "Can't select past date", Toast.LENGTH_SHORT).show();
                    }
                    eYear =year;
                    eMonth = month;
                    eDay = dayOfMonth;
                }
            }, eYear, eMonth, eDay);
            endDateDialog.getDatePicker().setMinDate(startDateTime.getTimeInMillis() + (24 * 60 * 60 * 1000)); //set min date
            endDateDialog.show();
        } else {
            Constant.snackBar(binding.svOngoingJob, "Please select start date first");
        }
    }

    //""""""""""" open week dialog """""""""""""""//
    private void openWeekDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.select_week_dialog);
        dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
        Button btnDone = dialog.findViewById(R.id.btn_done);
        final RelativeLayout addSkillsLayout = dialog.findViewById(R.id.select_skill_rl);
        RecyclerView recyclerView = dialog.findViewById(R.id.rv_week);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        SelectWeekAdapter weekAdapter = new SelectWeekAdapter(this, weekList);
        recyclerView.setAdapter(weekAdapter);

        final StringBuffer sb = new StringBuffer();
        btnDone.setOnClickListener(new View.OnClickListener() {
            String ss;

            @Override
            public void onClick(View v) {
                for (int i = 0; i < weekList.size(); i++) {
                    if (weekList.get(i).isWeekDay()) {

                        ss = TextUtils.join(",", weekList);
                        sb.append(weekList.get(i).getWeekDays());
                        sb.append(",");
                    }
                }
                if (!sb.toString().isEmpty()) {
                    sb.deleteCharAt(sb.length() - 1);
                    // Log.e("favroit team", stringBuffer.toString());
                }
                //   Log.e("week list ss: ", ss);
                binding.tvWeekDays.setText(sb);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void jobValidations() {
       // Log.e(TAG, "jobValidations: "+binding.hourRequireSpinner.getSelectedItem().toString() );
        if (Validation.isEmpty(binding.tvSelectedSkill)) {
            Constant.snackBar(binding.svOngoingJob, "Please Select Skill");
        } else if (Validation.isEmpty(binding.tvStartDate)) {
            Constant.snackBar(binding.svOngoingJob, "Please enter Start date");
        } else if (Validation.isEmpty(binding.tvEndDate)) {
            Constant.snackBar(binding.svOngoingJob, "Please enter End date");
        } else if (Validation.isEmpty(binding.tvWeekDays)) {
            Constant.snackBar(binding.svOngoingJob, "Please Select week days");
        } else if (hourRequired.equals("Hour Required Per Day") || hourRequired.isEmpty()) {
            Constant.snackBar(binding.svOngoingJob, "Please select hour Required per day");
        } else if (Validation.isEmpty(binding.tvLocation)) {
            Constant.snackBar(binding.svOngoingJob, "Please enter your Location");
        } /*else if (Validation.isEmpty(binding.etDescription)) {
            Constant.snackBar(binding.svOngoingJob, "Please enter job Description");
        }*/ else {
            JobCreationModel model = new JobCreationModel();
            model.skill = skillId;
            model.job_id = jobId;
            model.job_start_date = binding.tvStartDate.getText().toString().trim();
            model.job_end_date = binding.tvEndDate.getText().toString().trim();
            model.job_location = locationPlace;
            model.job_latitude = String.valueOf(locationLatLng.latitude);
            model.job_longitude = String.valueOf(locationLatLng.longitude);
            model.job_time_duration = binding.tvHourRequierd.getText().toString().trim();
            model.job_week_days = binding.tvWeekDays.getText().toString().trim();
            model.job_type = "2";
            model.job_title = "test";
            model.job_description = binding.etDescription.getText().toString().trim();

            onGoingJobUpdateApi(model);
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
    //"""""""""  On going job creation api calling """""""""""//
    private void onGoingJobUpdateApi(JobCreationModel model) {
        if (Constant.isNetworkAvailable(this, binding.svOngoingJob)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + UPDATE_JOB_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter(model)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            binding.tvSelectedSkill.setText("");
                            binding.tvStartDate.setText("");
                            binding.tvEndDate.setText("");
                            binding.tvLocation.setText("");
                            binding.tvWeekDays.setText("");
                            //binding.etHourRequierd.setText("");
                            binding.hourRequireSpinner.setSelection(0);
                            binding.etDescription.setText("");
                            onBackPressed();
                            // Toast.makeText(EditLongTermJobActivity.this, "Your offer has been successfully created.", Toast.LENGTH_SHORT).show();

                            String jobId = response.getString("clientJobId");
                            Intent intent = new Intent(EditLongTermJobActivity.this, NearYouClientActivity.class);
                            intent.putExtra("JobIdKey", jobId);
                            startActivity(intent);
                            // clientJobId
                            // Constant.snackBar(mainLayout,"Your job successfully post");
                        } else {

                            Constant.snackBar(binding.svOngoingJob, message);
                        }
                    } catch (JSONException e) {
                        //  Log.d(TAG, e.getMessage());
                    }
                }

                @Override
                public void onError(ANError anError) {
                    //   Log.d(TAG, anError.getErrorDetail());
                    Constant.errorHandle(anError, EditLongTermJobActivity.this);
                    progressDialog.dismiss();
                }
            });
        }
    }

    //"""""""""" skills dialog category and sub category """""""""""//
    private void openSkillDialog() {
        if (Constant.isNetworkAvailable(this, binding.svOngoingJob)) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.select_skills_dialog);
            dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            final RelativeLayout addSkillsLayout = dialog.findViewById(R.id.select_skill_rl);

            final Spinner categorySpinner = dialog.findViewById(R.id.category_spinner);
            subCategorySpinner = dialog.findViewById(R.id.sub_category_spinner);
            Button btnAddSkills = dialog.findViewById(R.id.btn_add_skills);
            TextView tvHeader = dialog.findViewById(R.id.tv_header);
            tvHeader.setText(R.string.select_skill);

            ArrayAdapter categoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, skillsResponce.getData());
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

                    skillDialogValidation(categorySpinner, addSkillsLayout, dialog);
                }
            });

            dialog.show();
            dialog.setCancelable(false);
        }
    }

    /*******Auto comple place picker***************/
    private void autoCompletePlacePicker() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            //   Log.d(TAG, e.getMessage());
        }
    }


    private void skillDialogValidation(Spinner categorySpinner, RelativeLayout addSkillsLayout, Dialog dialog) {
        if (skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getCategoryName().equals("Select Category")) {
            Constant.snackBar(addSkillsLayout, "Please Select Category");
        } else if (skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName().equals("Select Subcategory")) {
            Constant.snackBar(addSkillsLayout, "Please Select Subcategory");
        } else {
            dialog.dismiss();
            binding.tvSelectedSkill.setText(skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName());
            skillId = skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryId();
            //   Log.e("openSkillDialog: ", skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName());
        }
    }


    //""""""""""""  Load category data """"""""""""""//
    private void loadSkillsData() {
        if (Constant.isNetworkAvailable(this, binding.svOngoingJob)) {
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
                            Constant.snackBar(binding.svOngoingJob, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    progressDialog.dismiss();
                    // Log.e(TAG, anError.getErrorDetail());
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.category_spinner:
                /*if (position >= 1) {*/
                //Log.e(TAG, skillsResponce.getData().get(position).getCategoryName());
                ArrayAdapter<AddSkillsResponce.DataBean.SubcatBean> subCateoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, skillsResponce.getData().get(position).getSubcat());
                subCateoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
                subCategorySpinner.setOnItemSelectedListener(this);
                subCategorySpinner.setAdapter(subCateoryAdapter);
                ///  Log.d("onItemSelected: ", parent.getSelectedItem().toString());
                // ivCategorySpin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spinner_icon_rotator));
                //   }
                break;

            case R.id.hour_require_spinner:
                if (!parent.getSelectedItem().toString().equals("Hour Required Per Day")){
                    binding.tvHourRequierd.setText(parent.getSelectedItem().toString());
                    hourRequired = parent.getSelectedItem().toString();
                }
                break;
            default:
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    //not used this method//
    }
}
