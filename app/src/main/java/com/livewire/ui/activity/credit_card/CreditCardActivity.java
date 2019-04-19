package com.livewire.ui.activity.credit_card;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.livewire.R;
import com.livewire.databinding.ActivityCreditCardsBinding;
import com.livewire.ui.activity.ClientMainActivity;
import com.livewire.ui.dialog.ReviewDialog;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static com.livewire.utils.ApiCollection.ADD_REVIEW_API;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.END_JOB_API;
import static com.livewire.utils.ApiCollection.ONGOING_END_JOB_API;

public class CreditCardActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityCreditCardsBinding binding;
    private static final String TAG = CreditCardActivity.class.getName();
    private int width;
    private int year1;
    private int month1;
    private ProgressDialog progressDialog;
    private String jobId;
    private float budget;
    private float stripeFee;
    private String userId="";
    private String name="";
    private String cardNumber;
    private String workingDays="";
    private String jobType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credit_cards);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;

        if (getIntent().getStringExtra("JobType").equals("SingleJob")){ //user come from Single job payment
            binding.tvPayAmount.setText("Pay $"+getIntent().getFloatExtra("PayKey",0));
            budget = getIntent().getFloatExtra("PayKey",0);
            stripeFee = getIntent().getFloatExtra("stripeFeeKey",0);
            jobId = getIntent().getStringExtra("JobIdKey");
            userId = getIntent().getStringExtra("UserIdKey");
            name = getIntent().getStringExtra("NameKey");
            jobType = getIntent().getStringExtra("JobType");


            binding.tvPayAmount.setVisibility(View.VISIBLE);
            binding.btnPay.setVisibility(View.VISIBLE);
            binding.cbSaveCard.setVisibility(View.VISIBLE);
            binding.btnSaveCard.setVisibility(View.GONE);
            binding.tvHeader.setText(R.string.payment);
            binding.tvHeader.setAllCaps(true);

        } else if (getIntent().getStringExtra("JobType").equals("OngoingJob")) { //user come from Ongoing job payment
            binding.tvPayAmount.setText("Pay $"+getIntent().getFloatExtra("PayKey",0));
            budget = getIntent().getFloatExtra("PayKey",0);
            stripeFee = getIntent().getFloatExtra("stripeFeeKey",0);
            jobId = getIntent().getStringExtra("JobIdKey");
            userId = getIntent().getStringExtra("UserIdKey");
            name = getIntent().getStringExtra("NameKey");
            workingDays = getIntent().getStringExtra("WorkingDays");
            jobType = getIntent().getStringExtra("JobType");

            binding.tvPayAmount.setVisibility(View.VISIBLE);
            binding.btnPay.setVisibility(View.VISIBLE);
            binding.cbSaveCard.setVisibility(View.VISIBLE);
            binding.btnSaveCard.setVisibility(View.GONE);
            binding.tvHeader.setText(R.string.payment);
            binding.tvHeader.setAllCaps(true);
        }else if (getIntent().getStringExtra("JobType").equals("SaveCreditCard")){ //user come from setting :- manage Credit card :- Add Credit card
            binding.tvPayAmount.setVisibility(View.GONE);
            binding.btnPay.setVisibility(View.GONE);
            binding.cbSaveCard.setVisibility(View.GONE);
            binding.btnSaveCard.setVisibility(View.VISIBLE);

        }

        progressDialog = new ProgressDialog(this);
        binding.cardNum1.addTextChangedListener(watcherClass);
        binding.cardNum2.addTextChangedListener(watcherClass);
        binding.cardNum3.addTextChangedListener(watcherClass);
        binding.cardNum4.addTextChangedListener(watcherClass);
        binding.tvDate.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.btnSaveCard.setOnClickListener(this);
        binding.cbSaveCard.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
    }

    private TextWatcher watcherClass = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            /*this method is not used*/
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /*this method is not used*/
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s == binding.cardNum1.getEditableText() && s.length() == 4) {
                binding.cardNum2.requestFocus();

            } else if (s == binding.cardNum2.getEditableText() && s.length() == 4) {
                binding.cardNum3.requestFocus();

            } else if (s == binding.cardNum3.getEditableText() && s.length() == 4) {
                binding.cardNum4.requestFocus();

            } else if (s == binding.cardNum4.getEditableText() && s.length() == 4) {
                binding.tvDate.requestFocus();
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_save_card:
                Constant.hideSoftKeyBoard(this,binding.edCvv);
                validationd();
                break;
                case R.id.btn_pay:
                Constant.hideSoftKeyBoard(this,binding.edCvv);
                payValidations();
                break;
            case R.id.tv_date:
                showMonthYearDialog();
                break;
            case R.id.cb_save_card:
                if (binding.cbSaveCard.isChecked()) {
                    binding.cbSaveCard.setChecked(true);
                    binding.cbSaveCard.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
                } else {
                    binding.cbSaveCard.setChecked(false);
                    binding.cbSaveCard.setTextColor(ContextCompat.getColor(this, R.color.colorDarkGray));
                }
                break;
            default:
        }
    }

    //""""""""""' user come from JobDetailActivity :- save and pay from credit card """"""""'//
    private void payValidations() {
        if (Validation.isEmpty(binding.cardHolderName)) {
            Constant.snackBar(binding.svCreditcardLayout, getString(R.string.please_enter_cardholder_name));
        } else if (Validation.isEmpty(binding.cardNum1) || Validation.isEmpty(binding.cardNum2) || Validation.isEmpty(binding.cardNum3) || Validation.isEmpty(binding.cardNum4)) {
            Constant.snackBar(binding.svCreditcardLayout, getString(R.string.please_enter_card_number));
        } else if (Validation.isEmpty(binding.tvDate)) {
            Constant.snackBar(binding.svCreditcardLayout, getString(R.string.please_select_mm_yy));
        } else if (Validation.isEmpty(binding.edCvv) || binding.edCvv.getText().length() != 3) {
            Constant.snackBar(binding.svCreditcardLayout, getString(R.string.please_enter_cvv));
        } else {
            String cardNumber = binding.cardNum1.getText().toString().trim()
                    + binding.cardNum2.getText().toString().trim()
                    + binding.cardNum3.getText().toString().trim()
                    + binding.cardNum4.getText().toString().trim();

           // saveCardAndPay(cardNumber, month1, year1, binding.edCvv.getText().toString().trim());

        }
    }

    /*//""""""""""' user come from JobDetailActivity :- save and pay from credit card """"""""'//
    @SuppressLint("StaticFieldLeak")
    private void saveCardAndPay(final String cardNumber, final int month1, final int year1, final String cvv) {
        if (Constant.isNetworkAvailable(this, binding.svCreditcardLayout)) {
            progressDialog.show();

            new AsyncTask<Void, Void, com.stripe.model.Token>() {
                @Override
                protected com.stripe.model.Token doInBackground(Void... voids) {
                   // com.stripe.Stripe.apiKey = "sk_test_82UMhsygkviBYxQmikCW9Oa1";
                    Stripe.apiKey = "sk_test_wPa0eyao4nuDkLZG0QvaPzec";// project client account key

                    com.stripe.model.Token token = null;
                    Map<String, Object> tokenParams = new HashMap<String, Object>();
                    Map<String, Object> cardParams = new HashMap<String, Object>();
                    cardParams.put("number", cardNumber);
                    cardParams.put("exp_month", month1);
                    cardParams.put("exp_year", year1);
                    cardParams.put("cvc", cvv);
                    tokenParams.put("card", cardParams);

                    try {
                        token = com.stripe.model.Token.create(tokenParams);

                    } catch (StripeException e) {
                        Constant.printLogMethod(Constant.LOG_VALUE, TAG, e.getLocalizedMessage());
                        progressDialog.dismiss();
                        Constant.snackBar(binding.svCreditcardLayout, e.getLocalizedMessage());

                    }
                    return token;
                }

                @Override
                protected void onPostExecute(final com.stripe.model.Token token) {
                    super.onPostExecute(token);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                             if (token != null *//*&& binding.cbSaveCard.isChecked()*//*) {
                                if (Constant.isNetworkAvailable(CreditCardActivity.this, binding.svCreditcardLayout)) {
                                    if (binding.cbSaveCard.isChecked()) {
                                        createStripeToken(cardNumber, month1, year1, binding.edCvv.getText().toString().trim());
                                        //saveCreditCard(token.getId());
                                    }
                                    if (getIntent().getStringExtra("JobType").equals("SingleJob")) {
                                        singleJobPaymentApi(token.getId());
                                    }else if (getIntent().getStringExtra("JobType").equals("OngoingJob")){
                                        paymentForOngoingJob(token.getId());
                                    }
                                }
                            }
                        }
                    });
                }
            }.execute();
        }
    }
*/
    private void paymentForOngoingJob(String id) {
        if (Constant.isNetworkAvailable(this, binding.svCreditcardLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + ONGOING_END_JOB_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("jobId", jobId)
                    .addBodyParameter("amount", "" + budget)
                    .addBodyParameter("source",id )
                    .addBodyParameter("source_type", "token")
                    .addBodyParameter("STRIPE_FEES", "" + stripeFee)
                    .addBodyParameter("currency", "")
                    .addBodyParameter("working_days", workingDays)
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
                                    openReviewDialog();
                                    Constant.snackBar(binding.svCreditcardLayout, message);
                                } else {
                                    Constant.snackBar(binding.svCreditcardLayout, message);
                                    progressDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });

        }
    }

    ///"""""""""" server api to make payment """""""//
    private void singleJobPaymentApi(final String tokenId) {
        if (Constant.isNetworkAvailable(CreditCardActivity.this, binding.svCreditcardLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + END_JOB_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("jobId", jobId)
                    .addBodyParameter("amount", String.valueOf(budget))
                    .addBodyParameter("source",tokenId)
                    .addBodyParameter("source_type","token")
                    .addBodyParameter("STRIPE_FEES",""+stripeFee)
                    .addBodyParameter("currency", "")
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
                                    openReviewDialog();
                                   // Constant.snackBar(binding.svCreditcardLayout,message);
                                } else {
                                    Constant.snackBar(binding.svCreditcardLayout,message);
                                    progressDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.getMessage());
                            }
                        }
                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }
    }

    private void validationd() {
        if (Validation.isEmpty(binding.cardHolderName)) {
            Constant.snackBar(binding.svCreditcardLayout, getString(R.string.please_enter_cardholder_name));
        } else if (Validation.isEmpty(binding.cardNum1) || Validation.isEmpty(binding.cardNum2) || Validation.isEmpty(binding.cardNum3) || Validation.isEmpty(binding.cardNum4)) {
            Constant.snackBar(binding.svCreditcardLayout, getString(R.string.please_enter_card_number));
        } else if (Validation.isEmpty(binding.tvDate)) {
            Constant.snackBar(binding.svCreditcardLayout, getString(R.string.please_select_mm_yy));
        } else if (Validation.isEmpty(binding.edCvv) || binding.edCvv.getText().length() != 3) {
            Constant.snackBar(binding.svCreditcardLayout, getString(R.string.please_enter_cvv));
        } else {
             cardNumber = binding.cardNum1.getText().toString().trim()
                    + binding.cardNum2.getText().toString().trim()
                    + binding.cardNum3.getText().toString().trim()
                    + binding.cardNum4.getText().toString().trim();

           // createStripeToken(cardNumber, month1, year1, binding.edCvv.getText().toString().trim());
        }
    }

   /* //"""""""""" Save your credit card :- user come from manage credit card """"""""""""'///
    @SuppressLint("StaticFieldLeak")
    private void createStripeToken(final String cardNumber, final int month1, final int year1, final String cvv) {
        if (Constant.isNetworkAvailable(this, binding.svCreditcardLayout)) {
            progressDialog.show();

            new AsyncTask<Void, Void, com.stripe.model.Token>() {
                @Override
                protected com.stripe.model.Token doInBackground(Void... voids) {
                 //   com.stripe.Stripe.apiKey = "sk_test_82UMhsygkviBYxQmikCW9Oa1";
                    Stripe.apiKey = "sk_test_wPa0eyao4nuDkLZG0QvaPzec";// project client account key

                    com.stripe.model.Token token = null;
                    Map<String, Object> tokenParams = new HashMap<String, Object>();
                    Map<String, Object> cardParams = new HashMap<String, Object>();
                    cardParams.put("number", cardNumber);
                    cardParams.put("exp_month", month1);
                    cardParams.put("exp_year", year1);
                    cardParams.put("cvc", cvv);
                    tokenParams.put("card", cardParams);

                    try {
                        token = com.stripe.model.Token.create(tokenParams);

                    } catch (StripeException e) {
                        Constant.printLogMethod(Constant.LOG_VALUE, TAG, e.getLocalizedMessage());
                        progressDialog.dismiss();
                        Constant.snackBar(binding.svCreditcardLayout, e.getLocalizedMessage());

                    }
                    return token;
                }

                @Override
                protected void onPostExecute(final com.stripe.model.Token token) {
                    super.onPostExecute(token);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (token != null) {
                                saveCreditCard(token.getId());
                            }
                        }
                    });
                }
            }.execute();
        }
    }
*/


    @SuppressLint("StaticFieldLeak")
    private void createStripeToken() {
        if (Constant.isNetworkAvailable(this, binding.svCreditcardLayout)) {
            progressDialog.show();
      /*      Stripe stripe = new Stripe(this, "pk_test_g1InT1K0MoCW5lhkc6uf3UW3");
            stripe.createToken(card, new TokenCallback() {
                @Override
                public void onError(Exception error) {
                    progressDialog.dismiss();
                    Toast.makeText(CreditCardActivity.this,
                            error.getLocalizedMessage(),
                            Toast.LENGTH_LONG
                    ).show();


                  *//*  Map<String, Object> cardParams = new HashMap<String, Object>();
                    cardParams.put("limit", 3);
                    cardParams.put("object", "card");
                    Customer.retrieve("cus_E2l1Iea3Z9gZ6R").getSources().all(cardParams);*//*
                }

                @Override
                public void onSuccess(Token token) {
                    progressDialog.dismiss();
                    Log.e("token type: ", "" + token.getType());
                    Log.e("Token Id : ", token.getId());
                    //  Log.e( "bank acc: ",""+ token.getBankAccount().getAccountNumber());
                    Toast.makeText(CreditCardActivity.this, "token:" + token.getId(), Toast.LENGTH_LONG).show();

                    if (binding.cbSaveCard.isChecked()) {
                        if (Constant.isNetworkAvailable(CreditCardActivity.this, binding.svCreditcardLayout)) {
                            saveCreditCard(token.getId());
                        }
                    }

                }
            });*/
        }
    }

  /*  @SuppressLint("StaticFieldLeak")
    private void saveCreditCard(final String id) {
        progressDialog.show();
        new AsyncTask<Void, Void, Customer>() {
            @Override
            protected Customer doInBackground(Void... voids) {
              //  Stripe.apiKey = "sk_test_82UMhsygkviBYxQmikCW9Oa1";
                Stripe.apiKey = "sk_test_wPa0eyao4nuDkLZG0QvaPzec";// project client account key

                Customer customer = null;
                try {
                    customer = Customer.retrieve(PreferenceConnector.readString(CreditCardActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, ""));
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("source", id);
                    customer.getSources().create(params);

                } catch (StripeException e) {
                    e.printStackTrace();
                }
                return customer;
            }

            @Override
            protected void onPostExecute(Customer customer) {
                super.onPostExecute(customer);
                progressDialog.dismiss();
                if (customer != null) {
                    if (name.isEmpty()){
                        onBackPressed();
                    }
                   // Toast.makeText(CreditCardActivity.this, "Your card is saved successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Constant.snackBar(binding.svCreditcardLayout, "Stripe Error");
                }
            }
        }.execute();

    }*/

    //*************show  MonthYear  Dialog *******************//
    private void showMonthYearDialog() {
        final int year = Calendar.getInstance().get(Calendar.YEAR);
        final int month = Calendar.getInstance().get(Calendar.MONTH);
        final Dialog yearDialog = new Dialog(this);
        yearDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        yearDialog.setContentView(R.layout.dialog_month_year);
        yearDialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
        yearDialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialog;
        Button set = yearDialog.findViewById(R.id.button1);
        Button cancel = yearDialog.findViewById(R.id.button2);
        final NumberPicker yearPicker = yearDialog.findViewById(R.id.numberPicker1);
        final NumberPicker monthPicker = yearDialog.findViewById(R.id.numberPicker2);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setWrapSelectorWheel(false);
        monthPicker.setValue(month);
        monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


        yearPicker.setMaxValue(2050);
        yearPicker.setMinValue(year);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setValue(year);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year = String.valueOf(yearPicker.getValue());
                year1 = yearPicker.getValue();
                month1 = monthPicker.getValue();
                //  year = year.substring(2);
                binding.tvDate.setText(monthPicker.getValue() + "/" + year);

                yearDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearDialog.dismiss();
            }
        });
        yearDialog.show();
    }

    //""""""" give Review Dialog """""""""""//
    private void openReviewDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("NameKey", name);
        final ReviewDialog dialog = new ReviewDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "");
        dialog.setCancelable(false);
        dialog.getReviewInfo(new ReviewDialog.ReviewDialogListner() {
            @Override
            public void onReviewOnClick(String description, float rating, LinearLayout layout) {

                giveReviewApi(description, rating, dialog, layout);
                // dialog.dismiss();

                // Toast.makeText(JobHelpOfferedDetailWorkerActivity.this, description +" rating: "+rating, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReviewCancel() {
                Intent intent = new Intent(CreditCardActivity.this, ClientMainActivity.class);
                intent.putExtra("NearYouKey","MyJob");
                startActivity(intent);
                finish();
            }
        });
    }

    //"""""""""" give review list""""""""""""""""""'//
    private void giveReviewApi(String description, float rating, final ReviewDialog dialog, final LinearLayout layout) {
        if (Constant.isNetworkAvailable(this, layout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + ADD_REVIEW_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("review_to", userId)
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("rating", "" + rating)
                    .addBodyParameter("description", description)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            dialog.dismiss();
                            Intent intent = new Intent(CreditCardActivity.this, ClientMainActivity.class);
                            intent.putExtra("NearYouKey","MyJob");
                            startActivity(intent);
                            finish();
                            //  Constant.snackBar(binding.detailMainLayout, message);
                        } else {
                            Constant.snackBar(layout, message);
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
