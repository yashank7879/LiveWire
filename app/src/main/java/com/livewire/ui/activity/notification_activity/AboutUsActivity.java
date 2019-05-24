package com.livewire.ui.activity.notification_activity;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.livewire.R;
import com.livewire.databinding.ActivityAboutUsBinding;
import com.livewire.utils.Constant;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class AboutUsActivity extends AppCompatActivity {
    ActivityAboutUsBinding binding;
    private String type = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);
        progressDialog = new ProgressDialog(this);

        type = getIntent().getStringExtra("Type");
        switch (type) {
            case "AboutUs":
                binding.editCarTitle.setText(R.string.about_us);
                break;
            case "TermsCondition":
                binding.editCarTitle.setText(R.string.terms_and_conditions);

                break;
            case "PrivacyPolicy":
                binding.editCarTitle.setText(R.string.privacy_policy);
                break;
        }
        binding.backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getContentApi();
    }

    private void getContentApi() {
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.get( "https://livewire.work/service/getContent")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("status");
                                String message = response.getString("status");
                                if (status.equals("success")) {
                                    JSONObject jsonObject = response.getJSONObject("Content");
                                    String privacy_policy = jsonObject.getString("privacy_policy");
                                    String terms = jsonObject.getString("terms");
                                    String aboutus = jsonObject.getString("aboutus");

                                    switch (type) {
                                        case "AboutUs":
                                            openWebView(aboutus);
                                            break;
                                        case "TermsCondition":
                                            openWebView(terms);
                                            break;
                                        case "PrivacyPolicy":
                                            openWebView(privacy_policy);
                                            break;
                                    }

                                } else {
                                    Constant.snackBar(binding.mainLayout, message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }
    }

    private void openWebView(String aboutus) {
        binding.webView.loadUrl(aboutus);
        WebSettings settings =  binding.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        binding.webView.setVisibility(View.VISIBLE);
        binding.webView.setWebViewClient(new MyWebViewClient());
    }

    private class MyWebViewClient extends WebViewClient {

        private MyWebViewClient() {
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressDialog.dismiss();
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressDialog.show();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }
    }
}
