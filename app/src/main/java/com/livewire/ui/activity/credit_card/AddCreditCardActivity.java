package com.livewire.ui.activity.credit_card;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.CreditCardAdapter;
import com.livewire.databinding.ActivityAddCreditCardsBinding;
import com.livewire.responce.StripeSaveCardResponce;
import com.livewire.ui.activity.ClientMainActivity;
import com.livewire.ui.dialog.ReviewDialog;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.PreferenceConnectorRem;
import com.livewire.utils.ProgressDialog;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccountCollection;
import com.stripe.model.issuing.Card;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.livewire.utils.ApiCollection.ADD_REVIEW_API;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.END_JOB_API;


public class AddCreditCardActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = AddCreditCardActivity.class.getName();
    ActivityAddCreditCardsBinding binding;
    private ProgressDialog progressDialog;
    private CreditCardAdapter cardAdapter;
    private StripeSaveCardResponce cardResponce;
    private Handler handler = new Handler(Looper.myLooper());
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;
    private String cardId = "";
    private String jobId = "";
    private float budget;
    private float stripeFee;
    private String userId="";
    private String name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_credit_cards);

        //""""" user come at credit card activity from JobDetail  """""""""""'//
        if (getIntent().getStringExtra("PaymentKey") != null) {
            binding.tvPayAmount.setVisibility(View.VISIBLE);
            binding.tvSelectCard.setVisibility(View.VISIBLE);
            binding.tvAddNewCard.setVisibility(View.VISIBLE);
            binding.btnPay.setVisibility(View.VISIBLE);
            binding.btnAddCard.setVisibility(View.GONE);

            jobId = getIntent().getStringExtra("JobIdKey");
            userId = getIntent().getStringExtra("UserIdKey");//""""" worker user Id """"//
            name = getIntent().getStringExtra("NameKey");//"""" worker name """"""""//

            budget = Float.parseFloat(getIntent().getStringExtra("PaymentKey"));
            //stripeFee = (float) ((((budget * 2.9) / 100) + 0.30));

            DecimalFormat df = new DecimalFormat("0.00");
            stripeFee = Float.parseFloat(df.format(((((budget * 2.9) / 100) + 0.30))));

            binding.tvPayAmount.setText("Pay $" + budget);
            Constant.printLogMethod(Constant.LOG_VALUE, "stripe fee:", "" + stripeFee);
        }
        progressDialog = new ProgressDialog(this);
        binding.tvAddNewCard.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.btnAddCard.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);

        showCreditCardInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCreditCardInfo();
    }

    ///""""""""" Saved credit card api """"""""//
    @SuppressLint("StaticFieldLeak")
    private void showCreditCardInfo() {
        progressDialog.show();

        new AsyncTask<Void, Void, ExternalAccountCollection>() {
            @Override
            protected ExternalAccountCollection doInBackground(Void... voids) {

                Stripe.apiKey = "sk_test_82UMhsygkviBYxQmikCW9Oa1";

                ExternalAccountCollection customer = null;
                try {

                    Map<String, Object> cardParams = new HashMap<String, Object>();
                    cardParams.put("limit", 5);
                    cardParams.put("object", "card");
                    customer = Customer.retrieve(PreferenceConnector.readString(AddCreditCardActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, "")).getSources().all(cardParams);

                } catch (StripeException e) {
                    Constant.printLogMethod(Constant.LOG_VALUE, TAG, e.getLocalizedMessage());
                    progressDialog.dismiss();
                    Constant.snackBar(binding.llPaymentLayout, e.getLocalizedMessage());
                }
                return customer;
            }

            @Override
            protected void onPostExecute(final ExternalAccountCollection externalAccountCollection) {
                super.onPostExecute(externalAccountCollection);
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (externalAccountCollection != null) {

                            cardResponce = new Gson().fromJson(externalAccountCollection.toJson(), StripeSaveCardResponce.class);
                            //  Log.e("Size: ", "" + cardResponce.getData().size());

                            if (cardResponce.getData().size() != 0) {
                               // binding.tvSelectCard.setVisibility(View.VISIBLE);
                                binding.tvNoCardAdd.setVisibility(View.GONE);
                            }
                            for (int i = 0; i < cardResponce.getData().size(); i++) {
                                cardResponce.getData().get(i).setMoreDetail(true);
                            }


                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddCreditCardActivity.this);
                            cardAdapter = new CreditCardAdapter(AddCreditCardActivity.this, cardResponce.getData(), new CreditCardAdapter.CardDetailInterface() {
                                //"""""""""" click on holo circle img and then show card details """"""""""//
                                @Override
                                public void moreDetailOnClick(int pos, boolean value) {
                                    for (int j = 0; j < cardResponce.getData().size(); j++) {
                                        if (j == pos) {
                                            cardResponce.getData().get(pos).setMoreDetail(value);
                                            cardId = cardResponce.getData().get(pos).getId();
                                        } else {
                                            cardResponce.getData().get(j).setMoreDetail(true);
                                        }
                                    }
                                    cardAdapter.notifyDataSetChanged();
                                }


                                //"""""""""" user want to delete saved card """"""""""""//
                                @Override
                                public void deleteSaveCard(int pos, final String customerId) {

                                    removedSaveCardApi(customerId);

                                 /* // Use the Builder class for convenient dialog construction
                                  final AlertDialog.Builder builder = new AlertDialog.Builder(AddCreditCardActivity.this);

                                  builder.setMessage(R.string.do_you_want_to_delete_this_card)
                                          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                              public void onClick(DialogInterface dialog, int id) {
                                                  // FIRE ZE MISSILES!
                                                  //Toast.makeText(AddCreditCardActivity.this, "yes", Toast.LENGTH_SHORT).show();
                                                  removedSaveCardApi(customerId);
                                              }
                                          })
                                          .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                              public void onClick(DialogInterface dialog, int id) {
                                                  // User cancelled the dialog
                                                 // Toast.makeText(AddCreditCardActivity.this, "no", Toast.LENGTH_SHORT).show();
                                                  builder.create().dismiss();
                                              }
                                          });
                                  AlertDialog dialog = builder.create();
                                  dialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialog;
                                  dialog.show();*/

                                }
                            });
                            binding.rvPaymentCard.setLayoutManager(layoutManager);
                            binding.rvPaymentCard.setAdapter(cardAdapter);

                        }

                    }
                });

            }

        }.execute();
    }

    //""""""""""  Remove Saved credit card """"""""""""//
    @SuppressLint("StaticFieldLeak")
    private void removedSaveCardApi(final String id) {
        if (Constant.isNetworkAvailable(this, binding.llPaymentLayout)) {
            progressDialog.show();
            new AsyncTask<Void, Void, Customer>() {
                @Override
                protected Customer doInBackground(Void... voids) {
                    Stripe.apiKey = "sk_test_82UMhsygkviBYxQmikCW9Oa1";

                    Customer customer = null;
                    try {
                        customer = Customer.retrieve(PreferenceConnector.readString(AddCreditCardActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, ""));
                        customer.getSources().retrieve(id).delete();
                    } catch (StripeException e) {
                        e.printStackTrace();
                        Constant.printLogMethod(Constant.LOG_VALUE, TAG, e.getLocalizedMessage());
                        progressDialog.dismiss();
                        Constant.snackBar(binding.llPaymentLayout, e.getLocalizedMessage());
                    }

                    return customer;
                }

                @Override
                protected void onPostExecute(Customer customer) {
                    super.onPostExecute(customer);
                    progressDialog.dismiss();
                    if (customer != null) {
                        showCreditCardInfo();

                    }
                }
            }.execute();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_add_card:
                intent = new Intent(this, CreditCardActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_add_new_card:
                intent = new Intent(this, CreditCardActivity.class);
                intent.putExtra("PayKey", budget);
                intent.putExtra("JobIdKey", jobId);
                intent.putExtra("stripeFeeKey", stripeFee);
                intent.putExtra("NameKey",name);
                intent.putExtra("UserIdKey", userId);
                startActivity(intent);
                break;

            case R.id.btn_pay:
                if (cardId.isEmpty()) {
                    Constant.snackBar(binding.llPaymentLayout, "Please select Card");
                } else {
                    paymentApi();
                }
                break;
            default:
        }
    }

    private void paymentApi() {
        if (Constant.isNetworkAvailable(this, binding.llPaymentLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + END_JOB_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("jobId", jobId)
                    .addBodyParameter("amount", "" + budget)
                    .addBodyParameter("source", cardId)
                    .addBodyParameter("source_type", "card")
                    .addBodyParameter("stripe_fees", "" + stripeFee)
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
                                    Constant.snackBar(binding.llPaymentLayout, message);
                                } else {
                                    Constant.snackBar(binding.llPaymentLayout, message);
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
                Intent intent = new Intent(AddCreditCardActivity.this, ClientMainActivity.class);
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
                            Intent intent = new Intent(AddCreditCardActivity.this, ClientMainActivity.class);
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


     /* new Thread(new Runnable() {
            @Override
            public void run() {
                Stripe.apiKey = "sk_test_82UMhsygkviBYxQmikCW9Oa1";

                try {

                    *//*customer = com.stripe.model.Customer.retrieve(PreferenceConnector.readString(CreditCardActivity.this,PreferenceConnector.STRIPE_CUSTOMER_ID,""));
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("source", id);
                    customer.getSources().create(params);*//*
                    Map<String, Object> cardParams = new HashMap<String, Object>();
                    cardParams.put("limit", 3);
                    cardParams.put("object", "card");
                    externalAccountCollection = Customer.retrieve(PreferenceConnector.readString(AddCreditCardActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, "")).getSources().all(cardParams);

                } catch (StripeException e) {
                    e.printStackTrace();
                }


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        cardResponce = new Gson().fromJson(externalAccountCollection.toJson().toString(), StripeSaveCardResponce.class);
                        Log.e("Size: ", "" + cardResponce.getData().size());
                        // saveCardList = cardResponce.getData();
                        for (int i = 0; i < cardResponce.getData().size(); i++) {
                            cardResponce.getData().get(i).setMoreDetail(true);
                        }

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddCreditCardActivity.this);
                        cardAdapter = new CreditCardAdapter(AddCreditCardActivity.this, cardResponce.getData(), new CreditCardAdapter.CardDetailInterface() {
                            @Override
                            public void moreDetailOnClick(int pos, boolean value) {

                            }
                        });
                        binding.rvPaymentCard.setLayoutManager(layoutManager);
                        binding.rvPaymentCard.setAdapter(cardAdapter);

                    }
                });





                 *//*handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardResponce = new Gson().fromJson(externalAccountCollection.toJson().toString(), StripeSaveCardResponce.class);
                        Log.e( "Size: ",""+cardResponce.getData().size());
                        saveCardList = cardResponce.getData();
                        cardAdapter.notifyDataSetChanged();

                    }
                });*//*
            }
        }).start();*/

}
