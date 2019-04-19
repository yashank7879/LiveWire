package com.livewire.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.NearYouAdapter;
import com.livewire.pagination.EndlessRecyclerViewScrollListener;
import com.livewire.responce.AddSkillsResponce;
import com.livewire.responce.NearYouResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.GET_NEAR_BY_WORKER_API;
import static com.livewire.utils.ApiCollection.JOBPOSTSEND_REQUEST_2_API;

public class NearYouClientActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, NearYouAdapter.NearYouRequestListener {
    private ProgressDialog progressDialog;
    private RelativeLayout mainLayout;
    private List<NearYouResponce.DataBean> nearYouList;
    private NearYouAdapter nearYouAdapter;
    private String jobId;
    private int width;
    private TextView tv_no_job_post;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int limit = 5;
    private int start = 0;
    private List<String> currencyList = new ArrayList<>();
    private String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_you_client);
        intailizeView();
    }

    private void intailizeView() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        addVlaueInCurrency();
        if (getIntent().getStringExtra("JobIdKey") != null) {
            jobId = getIntent().getStringExtra("JobIdKey");
        }
        //jobId = "34";
        progressDialog = new ProgressDialog(this);
        mainLayout = findViewById(R.id.nearyou_layout);
        ImageView ivBack = findViewById(R.id.iv_back);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        tv_no_job_post = findViewById(R.id.tv_no_job_post);
        nearYouList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.rv_near_you);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        nearYouAdapter = new NearYouAdapter(this, nearYouList, this);
        recyclerView.setAdapter(nearYouAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { //swipe to refresh rcyclerview data
                swipeRefreshLayout.setRefreshing(false);
                if (Constant.isNetworkAvailable(NearYouClientActivity.this, mainLayout)) {
                    nearYouListApi();
                }
            }
        });

        //******  Pagination """""""""""""""//
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (Constant.isNetworkAvailable(NearYouClientActivity.this, mainLayout)) {
                    limit = limit + 5; //load 5 items in recyclerview

                    // progressDialog.show();
                    nearYouListApi();
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        ivBack.setOnClickListener(this);
        nearYouListApi();
    }

    private void addVlaueInCurrency() {
        currencyList.add("Currency");
        currencyList.add("ZAR (R)");
    }

    //"""""""""' near you api at client side""""""""""""""//
    private void nearYouListApi() {// help offer api calling
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + GET_NEAR_BY_WORKER_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("start", String.valueOf(start))
                    .addBodyParameter("limit", String.valueOf(limit))
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        progressDialog.dismiss();
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            nearYouList.clear();
                            tv_no_job_post.setVisibility(View.GONE);
                            NearYouResponce helpOfferedResponce = new Gson().fromJson(String.valueOf(response), NearYouResponce.class);
                            nearYouList.addAll(helpOfferedResponce.getData());
                            nearYouAdapter.notifyDataSetChanged();
                        } else {
                            if (nearYouList.size() == 0) {
                                tv_no_job_post.setVisibility(View.VISIBLE);
                            } else
                                Constant.snackBar(mainLayout, message);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
        }
    }

    @Override
    public void requestBtnOnclick(NearYouResponce.DataBean response) {
        openRequestDialog(response.getUserId());
    }

    @Override
    public void userInfoDetail(String userId) {
        Intent intent = new Intent(this, WorkerProfileDetailClientActivity.class);
        intent.putExtra("UserIdKey", userId);
        startActivity(intent);
    }

    //""""""""""" open send offer dialog """"""""""""""'//
    private void openRequestDialog(final String userId) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.request_send_client_dialog);
        dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
        final RelativeLayout mainLayout1 = dialog.findViewById(R.id.c_dialog_main_layout);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        Button btnSendRequest = dialog.findViewById(R.id.btn_send_request);
        final EditText etOfferPrice = dialog.findViewById(R.id.et_offer_price);
        Spinner CurrencySpinner = dialog.findViewById(R.id.currency_spinner);

        ArrayAdapter currencyAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, currencyList);
        currencyAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        CurrencySpinner.setOnItemSelectedListener(this);
        CurrencySpinner.setAdapter(currencyAdapter);


        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currency.equals("Currency")) {
                    Constant.snackBar(mainLayout1, "Please select currency");
                } else if (Validation.isEmpty(etOfferPrice)) {
                    Constant.snackBar(mainLayout1, "Please enter offer price");
                } else if (etOfferPrice.getText().toString().trim().equals("0")) {
                    Constant.snackBar(mainLayout1, "Offer price should not be zero");
                } else if (Float.parseFloat(etOfferPrice.getText().toString()) < 3) {
                    Constant.snackBar(mainLayout1, "Offer rate should not be less than 3 dollar");
                } else {
                    dialog.dismiss();
                    sendOfferRequestApi(etOfferPrice.getText().toString().trim(), mainLayout1, userId);
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.hideSoftKeyBoard(NearYouClientActivity.this, etOfferPrice);
                dialog.dismiss();
            }
        });

        //"""""" textWacher for bid price e.g "12345.99"
        etOfferPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*this method is not used*/
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            /*this method is not used*/
            }

            @Override
            public void afterTextChanged(Editable editable) { //""" price format "15455.15"""""""""
                String str = etOfferPrice.getText().toString();
                if (str.isEmpty()) return;
                String str2 = perfectDecimal(str, 6, 2);
                if (!str2.equals(str)) {
                    etOfferPrice.setText(str2);
                    int pos = etOfferPrice.getText().length();
                    etOfferPrice.setSelection(pos);
                }
            }
        });


        dialog.show();
        dialog.setCancelable(false);

    }

    public String perfectDecimal(String str, int maxBeforePoint, int maxDecimal) { //price format "15455.15"
        if (str.charAt(0) == '.') str = "0" + str;
        int max = str.length();

        String rFinal = "";
        boolean after = false;
        int i = 0;
        int up = 0;
        int decimal = 0;
        char t;
        while (i < max) {
            t = str.charAt(i);
            if (t != '.' && !after) {
                up++;
                if (up > maxBeforePoint) return rFinal;
            } else if (t == '.') {
                after = true;
            } else {
                decimal++;
                if (decimal > maxDecimal)
                    return rFinal;
            }
            rFinal = rFinal + t;
            i++;
        }
        return rFinal;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.currency_spinner:
                currency = currencyList.get(position);
                Log.e("fkjgh", "onItemSelected: " + currency);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void sendOfferRequestApi(String offerPrice, RelativeLayout mainLayout1, String userId) {
        if (Constant.isNetworkAvailable(this, mainLayout1)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + JOBPOSTSEND_REQUEST_2_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("job_id", jobId)
                    .addBodyParameter("currency", "ZAR")
                    //.addBodyParameter("request_to", "2")
                    .addBodyParameter("request_to", userId)
                    .addBodyParameter("job_offer", "" + Float.parseFloat(offerPrice))
                    .addBodyParameter("request_status", "0")
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    String status = null;
                    try {
                        status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            Constant.snackBar(mainLayout, message);
                            //"""""' if user successfully created on going post """""""""""//
                            Toast.makeText(NearYouClientActivity.this, R.string.offer_has_been_successfully_sent, Toast.LENGTH_SHORT).show();
                            //  Constant.snackBar(mainLayout,getString(R.string.your_project_has_been_successfully_shared));
                            Intent intent = new Intent(NearYouClientActivity.this, ClientMainActivity.class);
                            intent.putExtra("NearYouKey", "MyJobs");
                            startActivity(intent);
                            finish();
                            // first time replace home fragment
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
                }
            });
        }

    }

}
