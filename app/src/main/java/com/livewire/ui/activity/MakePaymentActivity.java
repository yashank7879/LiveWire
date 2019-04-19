package com.livewire.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.livewire.R;
import com.livewire.adapter.CreditCardAdapter;
import com.livewire.databinding.ActivityMakePaymentBinding;
import com.livewire.peach.activity.BasePaymentActivity;
import com.livewire.peach.reciver.CheckoutBroadcastReceiver;
import com.livewire.responce.StripeSaveCardResponce;
import com.livewire.ui.activity.credit_card.AddCreditCardActivity;
import com.livewire.ui.activity.credit_card.CreditCardActivity;
import com.livewire.ui.dialog.ReviewDialog;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import static com.livewire.utils.ApiCollection.ADD_REVIEW_API;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.COMPLETE_PAYMENT_API;

/**
 * Created by mindiii on 4/19/19.
 */

public class MakePaymentActivity extends BasePaymentActivity implements View.OnClickListener{
    ActivityMakePaymentBinding binding;
    private static final String TAG = MakePaymentActivity.class.getName();
    private ProgressDialog progressDialog;
    private CreditCardAdapter cardAdapter;
    private StripeSaveCardResponce cardResponce;
    private Handler handler = new Handler(Looper.myLooper());
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;
    private String cardId = "";
    private String jobId = "";
    private float budget;
    private float stripeFee;
    private String userId = "";
    private String name = "";
    private String workerDays;
    private String offer;
    private String hours;
    private String jobType = "";
    private String checkoutId = "";
    private String CurrencyKey;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = DataBindingUtil.setContentView(this,R.layout.activity_make_payment);

        DecimalFormat df = new DecimalFormat("0.00");
        //""""" user come at credit card activity from JobDetail  """""""""""'//
        if (getIntent().getStringExtra("PaymentKey") != null) {// //user come from setting manage credit card

            binding.btnMakePayment.setOnClickListener(this);

            if (getIntent().getStringExtra("SingleJobPayment") != null) {  //user come from Confirm Single job payment
                binding.rlOfferPrice.setVisibility(View.GONE);
                jobId = getIntent().getStringExtra("JobIdKey");
                userId = getIntent().getStringExtra("UserIdKey");//""""" worker user Id """"//
                name = getIntent().getStringExtra("NameKey");//"""" worker name """"""""//
                budget = Float.parseFloat(getIntent().getStringExtra("PaymentKey"));
                CurrencyKey = getIntent().getStringExtra("CurrencyKey");
                checkoutId = getIntent().getStringExtra("checkoutIdKey");
                binding.projectCost.setText("R "+String.valueOf(budget));
                binding.totalAmount.setText("R "+String.valueOf(budget));

                stripeFee = Float.parseFloat(df.format(((((budget * 2.9) / 100) + 0.30))));
               // binding.tvPayAmount.setText("Pay $" + budget);
                jobType = getIntent().getStringExtra("SingleJobPayment");

            } else if (getIntent().getStringExtra("OngoingJobPayment") != null) {  //user come from Confirm Ongoing job payment
                binding.rlOfferPrice.setVisibility(View.VISIBLE);
                jobId = getIntent().getStringExtra("JobIdKey");
                userId = getIntent().getStringExtra("UserIdKey"); //""""" worker user Id """"//
                name = getIntent().getStringExtra("NameKey"); //"""" worker name """"""""//
                offer = getIntent().getStringExtra("PaymentKey"); //"""" offer Price """"""""//
                workerDays = getIntent().getStringExtra("WorkDays");//"""" working days """"""""//
                hours = getIntent().getStringExtra("Hours"); //"""" Hours """"""""//
                jobType = getIntent().getStringExtra("OngoingJobPayment");
                CurrencyKey = getIntent().getStringExtra("CurrencyKey");
                checkoutId = getIntent().getStringExtra("checkoutIdKey");
                budget = Float.parseFloat(getIntent().getStringExtra("amountKey"));

               binding.tvProjectCost.setText("Toatal working days");
               binding.projectCost.setText(workerDays+" days");
               binding.offerPrice.setText(String.valueOf(offer)+" R/hr");
                binding.totalAmount.setText("R "+String.valueOf(budget));
              /*  budget = Float.parseFloat(offer) * Float.parseFloat(workerDays) * Float.parseFloat(hours);
                Constant.printLogMethod(Constant.LOG_VALUE, TAG, "" + budget);
                stripeFee = Float.parseFloat(df.format(((((budget * 2.9) / 100) + 0.30))));*/
              //  binding.tvPayAmount.setText("Pay $" + budget);

            }
            // Constant.printLogMethod(Constant.LOG_VALUE, "stripe fee:", "" + stripeFee);
        } else {
            binding.tvHeader.setText(R.string.card_list);
        }
        progressDialog = new ProgressDialog(this);
        binding.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.btn_make_payment:
                openCheckoutUI(checkoutId);
                break;
                default:
        }
    }

    private void openCheckoutUI(String checkoutId) {
        CheckoutSettings checkoutSettings = createCheckoutSettings(checkoutId, getString(R.string.checkout_ui_callback_scheme));
        /* Set componentName if you want to receive callbacks from the checkout */
        ComponentName componentName = new ComponentName(
                getPackageName(), CheckoutBroadcastReceiver.class.getName());

        /* Set up the Intent and start the checkout activity. */
        Intent intent = checkoutSettings.createCheckoutActivityIntent(this, componentName);
        // intent.putExtra("CheckOutId",checkoutId);

        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        /* Override onActivityResult to get notified when the checkout process is done. */
        if (requestCode == CheckoutActivity.REQUEST_CODE_CHECKOUT){
            switch (resultCode) {
                case CheckoutActivity.RESULT_OK:
                    /* Transaction completed. */

                    Transaction transaction = data.getParcelableExtra(
                            CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);

                    resourcePath = data.getStringExtra(
                            CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);

                    /*Check the transaction type.*/
                    if (transaction.getTransactionType() == TransactionType.SYNC) {
                        completePaymentApi();

                    } else {
                        /* Asynchronous transaction is processed in the onNewIntent(). */
                        hideProgressDialog();
                    }

                    break;
                case CheckoutActivity.RESULT_CANCELED:
                    hideProgressDialog();

                    break;
                case CheckoutActivity.RESULT_ERROR:
                    hideProgressDialog();

                    PaymentError error = data.getParcelableExtra(
                            CheckoutActivity.CHECKOUT_RESULT_ERROR);

                    showAlertDialog(R.string.error_message);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        /* Check if the intent contains the callback scheme. */
        if (hasCallbackScheme(intent)) {
            // requestPaymentStatus(resourcePath);
            completePaymentApi();
        }
    }

    private void completePaymentApi() {
        if (Constant.isNetworkAvailable(this, binding.llPaymentLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + COMPLETE_PAYMENT_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("project_id", jobId)
                    .addBodyParameter("checkout_id", checkoutId)
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
                                if (status.equals("success")) {//checkout_id
                                    openReviewDialog();
                                    Toast.makeText(MakePaymentActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                                   // Constant.snackBar(binding.llPaymentLayout, message);
                                } else {
                                    progressDialog.dismiss();
                                    Constant.snackBar(binding.llPaymentLayout, message);
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, e.getMessage());
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, anError.getErrorDetail());
                            progressDialog.dismiss();
                        }
                    });
        }
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
                finishAffinity();
                Intent intent = new Intent(MakePaymentActivity.this, ClientMainActivity.class);
                intent.putExtra("NearYouKey", "MyJob");
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
                            finishAffinity();
                            Intent intent = new Intent(MakePaymentActivity.this, ClientMainActivity.class);
                            intent.putExtra("NearYouKey", "MyJob");
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
