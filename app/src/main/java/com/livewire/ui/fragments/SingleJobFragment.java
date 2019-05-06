
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
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.livewire.model.JobCreationModel;
import com.livewire.responce.AddSkillsResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.livewire.ui.activity.CompleteProfileActivity.PLACE_AUTOCOMPLETE_REQUEST_CODE;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.CLIENT_JOB_POST_API;
import static com.livewire.utils.ApiCollection.GET_CATEGORY_LIST_API;

/**
 * Created by mindiii on 9/28/18.
 */
public class SingleJobFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = SingleJobFragment.class.getName();
    private TextView tvSelectSkill;
    private TextView tvSelectDate;
    private TextView tvLocation;
    private Context mContext;
    private Spinner subCategorySpinner;
    private ScrollView mainLayout;
    private EditText etBudget;
    private EditText etDescription;
    private Button btnCancel;

    private String skillId;
    private String locationPlace;
    private List<String> currencyList = new ArrayList<>();
    private String currency = "";
    private Spinner currencySpinner;
    private AddSkillsResponce skillsResponce;
    private ProgressDialog progressDialog;
    private Calendar startDateTime;
    private int width;
    private LatLng locationLatLng;

    private PopupWindow popupWindow;
    private TextView tv_currency;
    private RelativeLayout rlCurrencySpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_job, container, false);
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
        addVlaueInCurrency();
        progressDialog = new ProgressDialog(mContext);
        mainLayout = view.findViewById(R.id.sv_single_job);
        RelativeLayout selectSkillsRl = view.findViewById(R.id.select_skills_rl);
        tvSelectSkill = view.findViewById(R.id.tv_select_skill);
        RelativeLayout selectDateRl = view.findViewById(R.id.select_date_rl);
        rlCurrencySpinner = view.findViewById(R.id.rl_currency_spinner);
        tvSelectDate = view.findViewById(R.id.tv_select_date);
        RelativeLayout locationRl = view.findViewById(R.id.location_rl);
        tvLocation = view.findViewById(R.id.tv_location);
        etBudget = view.findViewById(R.id.et_budget);
        btnCancel = view.findViewById(R.id.btn_cancel);
        etDescription = view.findViewById(R.id.et_description);
        currencySpinner = view.findViewById(R.id.currency_spinner);
        tv_currency = view.findViewById(R.id.tv_currency);

        tvLocation.setOnClickListener(this);
        rlCurrencySpinner.setOnClickListener(this);
        tvLocation.setSelected(true);
        tvLocation.setHorizontallyScrolling(true);
        tvLocation.setMovementMethod(new ScrollingMovementMethod());
        tvLocation.setHint(R.string.location);

        RelativeLayout.LayoutParams newLayoutParams = new RelativeLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        newLayoutParams.addRule(RelativeLayout.BELOW, tv_currency.getId());
        //newLayoutParams.setMargins(5,5,5,5);
        currencySpinner.setLayoutParams(newLayoutParams);

        ArrayAdapter currencyAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, currencyList);
        currencyAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        currencySpinner.setOnItemSelectedListener(this);
        currencySpinner.setAdapter(currencyAdapter);

        //""""  to hide keyboard """""""//
        etDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);
        locationRl.setOnClickListener(this);
        selectSkillsRl.setOnClickListener(this);
        selectDateRl.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        TextView btnShare = view.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);

        //"""""" textWacher for bid price e.g "12345.99"
        etBudget.addTextChangedListener(new TextWatcher() {
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
                String str = etBudget.getText().toString();
                if (str.isEmpty()) return;
                String str2 = Constant.perfectDecimal(str, 6, 2);
                if (!str2.equals(str)) {
                    etBudget.setText(str2);
                    int pos = etBudget.getText().length();
                    etBudget.setSelection(pos);
                }
            }
        });


        loadSkillsData();
    }

    private void addVlaueInCurrency() {
        currencyList.add("Currency");
        currencyList.add("ZAR (R)");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG, "setUserVisibleHint1: " + isVisibleToUser);
        if (isVisibleToUser) {
            clearData();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.btn_cancel:
                clearData();
                break;
            case R.id.rl_currency_spinner:
                currencySpinner.performClick();
                break;
        }
    }

    private void popupMenuOpen() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View customView = layoutInflater.inflate(R.layout.sub_cat_cell, null);

        //instantiate popup window
        popupWindow = new PopupWindow(customView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        //display the popup window
        //popupWindow.showAtLocation(tv_apply, Gravity.CENTER, 0, 0);


        /*PopupMenu popup = new PopupMenu(mContext, tv_apply, Gravity.START,0,R.style.PopupMenuMoreCentralized);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        popup.getMenu().add("One");
        popup.getMenu().add("Two");
        popup.getMenu().add("Three");
        popup.getMenu().add("Four");
        popup.show();*/
    }

    private void clearData() {
        if (tvSelectSkill != null) {
            tvSelectSkill.setText("");
            tvSelectDate.setText("");
            tvLocation.setText("");
            etBudget.setText("");
            etDescription.setText("");
            tvLocation.setHint(R.string.location);
            tv_currency.setText(null);
            currencySpinner.setSelection(0);
        }
    }

    //"""""""" open date picker """""""""""'//
    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        Constant.hideSoftKeyBoard(mContext, tvLocation);
        final DatePickerDialog startDateDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                startDateTime = Calendar.getInstance();
                startDateTime.set(Calendar.YEAR, year);
                startDateTime.set(Calendar.MONTH, month);
                startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //********Date time Format**************//
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy ");
                if (startDateTime.getTimeInMillis() >= System.currentTimeMillis() - 1000) {
                    tvSelectDate.setText(sdf1.format(startDateTime.getTime()));

                } else {
                    Toast.makeText(mContext, "Can't select past date", Toast.LENGTH_SHORT).show();
                }
                //  startDateString = sdf1.format(startDateTime.getTime());

            }
        }, mYear, mMonth, mDay);
        startDateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        startDateDialog.show();
    }

    //"""""" load skills data """"""""""//
    private void loadSkillsData() {
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
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


    //""""""""""" open skills data """""""""""//
    private void openSkillDialog() {
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.select_skills_dialog);
            dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            final RelativeLayout addSkillsLayout = dialog.findViewById(R.id.select_skill_rl);

            TextView tvHeader = dialog.findViewById(R.id.tv_header);
            tvHeader.setText(R.string.select_skill);
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

                    skillDialogValidation(categorySpinner, addSkillsLayout, dialog);
                }
            });

            dialog.show();
            dialog.setCancelable(false);
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
            tvSelectSkill.setText(skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName());
            skillId = skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryId();
        }
    }


    ///"""""""""" validations """""""""""""""//
    private void jobValidations() {
        if (Validation.isEmpty(tvSelectSkill)) {
            Constant.snackBar(mainLayout, "Please Select Skill");
        } else if (Validation.isEmpty(tvSelectDate)) {
            Constant.snackBar(mainLayout, "Please Select Date");
        } else if (currency.equals("Currency")) {
            Constant.snackBar(mainLayout, "Please select currency");
        } else if (Validation.isEmpty(etBudget)) {
            Constant.snackBar(mainLayout, "Please enter Budget");
        } else if (etBudget.getText().toString().trim().equals("0")) {
            Constant.snackBar(mainLayout, "Please enter correct Budget");
        } else if (Float.parseFloat(etBudget.getText().toString()) < 3) {
            Constant.snackBar(mainLayout, "Budget price should not be less than 3 dollar");
        } else if (Validation.isEmpty(tvLocation)) {
            Constant.snackBar(mainLayout, "Please enter your Location");
        }/* else if (Validation.isEmpty(etDescription)) {
            Constant.snackBar(mainLayout, "Please enter job Description");
        }*/ else {
            JobCreationModel model = new JobCreationModel();
            model.skill = skillId;
            model.job_start_date = tvSelectDate.getText().toString();
            model.job_location = locationPlace;
            model.job_latitude = String.valueOf(locationLatLng.latitude);
            model.job_longitude = String.valueOf(locationLatLng.longitude);
            model.job_budget = Float.parseFloat(etBudget.getText().toString());
            model.job_title = "test";
            model.currency = "ZAR";
            model.job_type = "1";
            model.job_description = etDescription.getText().toString();
            singleJobCreationApi(model);
        }
    }

    //"""""""""""" single job creation """""""""" //
    private void singleJobCreationApi(JobCreationModel model) {
        if (Constant.isNetworkAvailable(mContext, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + CLIENT_JOB_POST_API)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
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
                            Constant.snackBar(mainLayout, "Your offer has been successfully sent.");
                            tvSelectSkill.setText("");
                            tvLocation.setText("");
                            tvSelectDate.setText("");
                            etBudget.setText("");
                            etDescription.setText("");
                            tv_currency.setText(null);
                            currencySpinner.setSelection(0);
                        } else {
                            Constant.snackBar(mainLayout, message);
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Constant.errorHandle(anError, getActivity());
                    Log.d(TAG, anError.getErrorDetail());
                    progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.category_spinner:
                Log.e(TAG, skillsResponce.getData().get(position).getCategoryName());
                ArrayAdapter<AddSkillsResponce.DataBean.SubcatBean> subCateoryAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, skillsResponce.getData().get(position).getSubcat());
                subCateoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
                subCategorySpinner.setOnItemSelectedListener(this);
                subCategorySpinner.setAdapter(subCateoryAdapter);
                break;
            case R.id.currency_spinner:
                currency = currencyList.get(position);
                if (!currency.equals("Currency"))
                    tv_currency.setText(currency);
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
}


