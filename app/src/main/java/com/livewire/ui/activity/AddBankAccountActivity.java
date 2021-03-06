package com.livewire.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
        // binding.llDob.setOnClickListener(this);
        binding.actionBarWorker.ivBack.setOnClickListener(this);
        binding.actionBarWorker.tvLiveWire.setText(R.string.manage_bank_acc);

        accTypeList = new ArrayList<>();
        accTypeList.add("Account Holder Type");
        accTypeList.add("Saving");
        accTypeList.add("Current");
        accTypeList.add("Joint");
        accTypeList.add("Fixed Deposit Account");
        accTypeList.add("NRI Accounts");
        getBankAccountDetails();


        // accTypeAdapter = new ArrayAdapter<>(AddBankAccountActivity.this, R.layout.spinner_item, accTypeList);
        //accTypeAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        //  binding.spinnerBankType.setOnItemSelectedListener(this);
        //  binding.spinnerBankType.setAdapter(accTypeAdapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_now: {
                bankAccountValidations();
                break;
            }
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            default: {
                break;
            }
            /*case R.id.ll_dob: {
                //openDobCalender();
                break;
            }*/
        }
    }

    //"""""""" open date picker """""""""""'//
   /* private void openDobCalender() {
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
                /*/

    /********Date time Format**************//*/
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy ");
                binding.tvDob.setText(sdf1.format(startDateTime.getTime()));

                //  startDateString = sdf1.format(startDateTime.getTime());

            }
        }, mYear, mMonth, mDay);

        //  startDateDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        startDateDialog.show();

    }*/
    private void bankAccountValidations() {
        if (Validation.isEmpty(binding.accHolderFirstName)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_account_holder_first_name));
        }
        else if (binding.accHolderFirstName.getText().toString().trim().contains("  ")) {
            Constant.snackBar(binding.svAddbankLayout, "Single space should allowed here.");
            binding.accHolderFirstName.requestFocus();
            Constant.hideSoftKeyBoard(this,binding.accHolderFirstName);
        }
        else if (Validation.isEmpty(binding.accHolderLastName)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_account_holder_last_name));
        }
        else if (binding.accHolderLastName.getText().toString().trim().contains("  ")) {
            binding.accHolderLastName.requestFocus();
            Constant.hideSoftKeyBoard(this,binding.accHolderLastName);
            Constant.snackBar(binding.svAddbankLayout, "Single space should allowed here.");
        }
        else if (Validation.isEmpty(binding.accNumber)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_account_number));
        }
        else if (Validation.isEmpty(binding.branchCode)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_branch_code));
        }
        else if (binding.branchCode.getText().toString().trim().contains("  ")) {
            binding.branchCode.requestFocus();
            Constant.hideSoftKeyBoard(this,binding.branchCode);
            Constant.snackBar(binding.svAddbankLayout, "Single space should allowed here.");
        }else if (Validation.isEmpty(binding.etBankName)) {
            Constant.snackBar(binding.svAddbankLayout, "Please enter bank name");
        }
        else {
            AddBankAccontModel model = new AddBankAccontModel();
            model.firstName = binding.accHolderFirstName.getText().toString().trim();
            model.lastName = binding.accHolderLastName.getText().toString().trim();
            model.branch_code = binding.branchCode.getText().toString().trim();
            model.accountNumber = binding.accNumber.getText().toString().trim();
            model.bankName = binding.etBankName.getText().toString().trim();
            addBankAccApi(model);
        }

        /*else if (Validation.isEmpty(binding.postalCode)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_Postal_code));
        } else if (Validation.isEmpty(binding.ssnLast)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_SSN_number));
        } else if (accHolderType.isEmpty()) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_select_acc_type));
        } else if (Validation.isEmpty(binding.tvDob)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.pls_select_your_dob));
        }*/
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
                                   // Constant.snackBar(binding.svAddbankLayout, "Your Account Added successfully");

                                    PreferenceConnector.writeString(AddBankAccountActivity.this, PreferenceConnector.IS_BANK_ACC, "1");
                                    binding.payNow.setText(R.string.update_bank_details);
                                    binding.accHolderFirstName.setEnabled(false);
                                    binding.accHolderLastName.setEnabled(false);
                                    binding.accNumber.setEnabled(false);
                                    binding.branchCode.setEnabled(false);

                                    if (binding.payNow.getText().toString().equals("Update Account"))
                                    Toast.makeText(AddBankAccountActivity.this, "Bank account Updated successfully", Toast.LENGTH_SHORT).show();
                                    else Toast.makeText(AddBankAccountActivity.this, "Bank Account Update Successfully", Toast.LENGTH_SHORT).show();

                                    /*binding.postalCode.setEnabled(false);
                                    binding.ssnLast.setEnabled(false);
                                    binding.spinnerBankType.setEnabled(false);
                                    binding.llDob.setEnabled(false);*/
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

     /*   if (!accTypeList.get(binding.spinnerBankType.getSelectedItemPosition()).equals("Account Holder Type")) {
            accHolderType = String.valueOf(binding.spinnerBankType.getSelectedItem());
        } else accHolderType = "";*/
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
                                    binding.actionBarWorker.tvLiveWire.setText(R.string.edit_bank_account);
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
        binding.accNumber.setText(bankAccDetailResonce.getBank_detail().getAccountNumberX());
        binding.accHolderFirstName.setText(bankAccDetailResonce.getBank_detail().getFirstNameX());
        binding.accHolderLastName.setText(bankAccDetailResonce.getBank_detail().getLastNameX());
        binding.branchCode.setText(bankAccDetailResonce.getBank_detail().getBranch_code());
        binding.etBankName.setText(bankAccDetailResonce.getBank_detail().getBankName());

        /*  binding.postalCode.setText(bankAccDetailResonce.getBank_detail().getPostalCode());
        binding.ssnLast.setText(bankAccDetailResonce.getBank_detail().getSsnLast());
        binding.tvDob.setText(dateFomatChange(bankAccDetailResonce.getBank_detail().getDob()));*/

      /*  for (int i = 0; i < accTypeList.size(); i++) {
            if (accTypeList.get(i).equals(bankAccDetailResonce.getBank_detail().getAccountType())) {
                binding.spinnerBankType.setSelection(i);
            }
        }
        binding.accNumber.setText(bankAccDetailResonce.getBank_detail().getAccountNumber());*/

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
