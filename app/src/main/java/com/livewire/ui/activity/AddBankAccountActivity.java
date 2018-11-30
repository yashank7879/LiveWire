package com.livewire.ui.activity;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.ActivityAddBankAccountBinding;
import com.livewire.model.AddBankAccontModel;
import com.livewire.responce.BankAccDetailResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.livewire.utils.ApiCollection.ADD_BANK_ACCOUNT_API;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.GET_BANK_DETAILS_API;

public class AddBankAccountActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ActivityAddBankAccountBinding binding;
    private ArrayAdapter accTypeAdapter;
    private String accHolderType;
    private ArrayList<String> accTypeList;
    private ProgressDialog progressDialog;
    private Calendar startDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bank_account);
        progressDialog = new ProgressDialog(this);

        binding.payNow.setOnClickListener(this);
        binding.llDob.setOnClickListener(this);
        binding.actionBarWorker.ivBack.setOnClickListener(this);
        binding.actionBarWorker.tvLiveWire.setText(R.string.add_bank_acc);

        accTypeList = new ArrayList<>();
        accTypeList.add("Account Holder Type");
        accTypeList.add("Saving Account");
        accTypeList.add("Current Account");
        accTypeList.add("Joint Account");
        getBankAccountDetails();

        accTypeAdapter = new ArrayAdapter<>(AddBankAccountActivity.this, R.layout.spinner_item, accTypeList);
        accTypeAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        binding.spinnerBankType.setOnItemSelectedListener(this);
        binding.spinnerBankType.setAdapter(accTypeAdapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_now: {
                bankAccountValidations();
                break;
            }
            case R.id.ll_dob: {
                openDobCalender();
                break;
            }
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            default: {
                break;
            }
        }
    }

    //"""""""" open date picker """""""""""'//
    private void openDobCalender() {
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        Constant.hideSoftKeyBoard(this, binding.accHolderLastName);
        final DatePickerDialog startDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                startDateTime = Calendar.getInstance();
                startDateTime.set(Calendar.YEAR, year);
                startDateTime.set(Calendar.MONTH, month);
                startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //********Date time Format**************//
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy ");
                binding.tvDob.setText(sdf1.format(startDateTime.getTime()));

                //  startDateString = sdf1.format(startDateTime.getTime());

            }
        }, mYear, mMonth, mDay);

        //  startDateDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        startDateDialog.show();

    }

    private void bankAccountValidations() {
        if (Validation.isEmpty(binding.accHolderFirstName)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_account_holder_first_name));
        } else if (Validation.isEmpty(binding.accHolderLastName)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_account_holder_last_name));
        } else if (Validation.isEmpty(binding.accNumber)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_account_number));
        } else if (Validation.isEmpty(binding.routingNo)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_routing_number));
        } else if (Validation.isEmpty(binding.postalCode)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_Postal_code));
        } else if (Validation.isEmpty(binding.ssnLast)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_SSN_number));
        } else if (accHolderType.isEmpty()) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_select_acc_type));
        } else if (Validation.isEmpty(binding.tvDob)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.pls_select_your_dob));
        } else {
            AddBankAccontModel model = new AddBankAccontModel();
            model.firstName = binding.accHolderFirstName.getText().toString().trim();
            model.lastName = binding.accHolderLastName.getText().toString().trim();
            model.dob = binding.tvDob.getText().toString().trim();
            model.routingNumber = binding.routingNo.getText().toString().trim();
            model.accountNumber = binding.accNumber.getText().toString().trim();
            model.postalCode = binding.postalCode.getText().toString().trim();
            model.ssnLast = binding.ssnLast.getText().toString().trim();
            model.accountType = accHolderType;
            addBankAccApi(model);
        }
    }

    private void addBankAccApi(AddBankAccontModel model) {
        if (Constant.isNetworkAvailable(this, binding.svAddbankLayout)) {
            progressDialog.show();
            AndroidNetworking
                    .post(BASE_URL + ADD_BANK_ACCOUNT_API)
                    .addBodyParameter(model)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            try {
                                String status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                    onBackPressed();
                                    Constant.snackBar(binding.svAddbankLayout, "Your Account Added successfully");

                                    binding.payNow.setText(R.string.update_bank_details);
                                    binding.accHolderFirstName.setEnabled(false);
                                    binding.accHolderLastName.setEnabled(false);
                                    binding.accNumber.setEnabled(false);
                                    binding.routingNo.setEnabled(false);
                                    binding.postalCode.setEnabled(false);
                                    binding.ssnLast.setEnabled(false);
                                    binding.spinnerBankType.setEnabled(false);
                                    binding.llDob.setEnabled(false);
                                } else {

                                    Constant.snackBar(binding.svAddbankLayout, message);
                                }
                            } catch (JSONException e) {
                                Log.d("Exception", e.getMessage());
                            }
                            //  binding.accHolderFirstName.setEnabled(false);

                        }

                        @Override
                        public void onError(ANError anError) {
                            progressDialog.dismiss();
                            Constant.errorHandle(anError, AddBankAccountActivity.this);
                        }
                    });
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (!accTypeList.get(binding.spinnerBankType.getSelectedItemPosition()).equals("Account Holder Type")) {
            accHolderType = String.valueOf(binding.spinnerBankType.getSelectedItem());
        } else accHolderType = "";
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void getBankAccountDetails() {
        if (Constant.isNetworkAvailable(this, binding.svAddbankLayout)) {
            progressDialog.show();
            AndroidNetworking
                    .get(BASE_URL + GET_BANK_DETAILS_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            try {
                                String status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                    BankAccDetailResponce bankAccDetailResonce = new Gson().fromJson(String.valueOf(response), BankAccDetailResponce.class);
                                    setBankDetail(bankAccDetailResonce);
                                    PreferenceConnector.writeString(AddBankAccountActivity.this, PreferenceConnector.IS_BANK_ACC, "1");
                                    binding.tvContent.setVisibility(View.GONE);
                                    binding.payNow.setText(R.string.update_bank_details);
                                } else {

                                    if (message.equals("Your bank details is not registered")) {

                                    } else {
                                        Constant.snackBar(binding.svAddbankLayout, message);
                                    }

                                }
                            } catch (JSONException e) {
                                Log.d("Exception", e.getMessage());
                            }
                            //  binding.accHolderFirstName.setEnabled(false);

                        }

                        @Override
                        public void onError(ANError anError) {
                            progressDialog.dismiss();
                            Constant.errorHandle(anError, AddBankAccountActivity.this);
                        }
                    });
        }
    }

    //{"status":"fail","message":"Your bank details is not registered"}

    private void setBankDetail(BankAccDetailResponce bankAccDetailResonce) {
        binding.accNumber.setText(bankAccDetailResonce.getBank_detail().getAccountNumber());
        binding.accHolderFirstName.setText(bankAccDetailResonce.getBank_detail().getFirstName());
        binding.accHolderLastName.setText(bankAccDetailResonce.getBank_detail().getLastName());
        binding.routingNo.setText(bankAccDetailResonce.getBank_detail().getRoutingNumber());
        binding.postalCode.setText(bankAccDetailResonce.getBank_detail().getPostalCode());
        binding.ssnLast.setText(bankAccDetailResonce.getBank_detail().getSsnLast());
        binding.tvDob.setText(dateFomatChange(bankAccDetailResonce.getBank_detail().getDob()));

        for (int i = 0; i < accTypeList.size(); i++) {
            if (accTypeList.get(i).equals(bankAccDetailResonce.getBank_detail().getAccountType())) {
                binding.spinnerBankType.setSelection(i);
            }
        }


        binding.accNumber.setText(bankAccDetailResonce.getBank_detail().getAccountNumber());

    }

    public static String dateFomatChange(String startDate) {

        DateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String start = startDate;
        try {
            Date newStartDate;
            newStartDate = sd.parse(start);
            sd = new SimpleDateFormat("dd-MM-yyyy");
            start = sd.format(newStartDate);

            // holder.tvDate.setText(dateTextColorChange(start));

        } catch (
                ParseException e)

        {
            Log.e("Date", e.getMessage());
        }
        return start;
    }
}
