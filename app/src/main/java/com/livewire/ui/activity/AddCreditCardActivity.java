package com.livewire.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.CreditCardAdapter;
import com.livewire.databinding.ActivityAddCreditCardsBinding;
import com.livewire.pagination.RecyclerItemTouchHelper;
import com.livewire.responce.StripeSaveCardResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccountCollection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AddCreditCardActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = AddCreditCardActivity.class.getName();
    ActivityAddCreditCardsBinding binding;
    private ProgressDialog progressDialog;
    private CreditCardAdapter cardAdapter;
    private StripeSaveCardResponce cardResponce;
    private Handler handler = new Handler(Looper.myLooper());
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_credit_cards);

        progressDialog = new ProgressDialog(this);
        binding.tvAddNewCard.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
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
                    Constant.printLogMethod(Constant.LOG_VALUE,TAG,e.getLocalizedMessage());
                    progressDialog.dismiss();
                    Constant.snackBar(binding.llPaymentLayout,e.getLocalizedMessage());
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

                            if (cardResponce.getData().size() != 0){
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
        if (Constant.isNetworkAvailable(this,binding.llPaymentLayout)){
            progressDialog.show();
            new AsyncTask<Void, Void, Customer>() {
                @Override
                protected Customer doInBackground(Void... voids) {
                    Stripe.apiKey = "sk_test_82UMhsygkviBYxQmikCW9Oa1";

                    Customer customer = null;
                    try {
                         customer = Customer.retrieve(PreferenceConnector.readString(AddCreditCardActivity.this,PreferenceConnector.STRIPE_CUSTOMER_ID,""));
                        customer.getSources().retrieve(id).delete();
                    } catch (StripeException e) {
                        e.printStackTrace();
                        Constant.printLogMethod(Constant.LOG_VALUE,TAG,e.getLocalizedMessage());
                        progressDialog.dismiss();
                        Constant.snackBar(binding.llPaymentLayout,e.getLocalizedMessage());
                    }

                    return customer;
                }

                @Override
                protected void onPostExecute(Customer customer) {
                    super.onPostExecute(customer);
                    progressDialog.dismiss();
                    if (customer != null){
                        showCreditCardInfo();
                    }
                }
            }.execute();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pay:
                Intent intent = new Intent(this, CreditCardActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_back:
                onBackPressed();
                break;
            default:
        }
    }

}
