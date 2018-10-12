package com.livewire.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.model.UserModel;
import com.livewire.responce.SignUpResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.PreferenceConnectorRem;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LoginActivity.class.getName();
    private ImageView ivCheckBox;
    boolean isremember = false;
    private EditText etMail;
    private EditText etPass;
    private ScrollView mainLayout;
    private ProgressBar progressBar;
    private static final int RC_SIGN_IN = 7;
    private GoogleApiClient mGoogleApiClient;
    private LinearLayout googleLogInBtn;
    private LinearLayout fbLogInBtn;
    private TextView forgotpassId;
    private int width;
    private Animation shake;
    private static String key;
    private ProgressDialog progressDialog;
    private String imageUrl;
    private LoginButton fb_btn;
    private CallbackManager callBackManager;
    private GraphRequest data_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeView();
        // printHashKey();
    }


 /*   public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }*/

    private void initializeView() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        callBackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanim);
        TextView tvLiveWire = findViewById(R.id.tv_live_wire);
        TextView btnSignup = findViewById(R.id.btn_signup);
        Button btnLogin = findViewById(R.id.btn_login);
        etMail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_pass);
        ivCheckBox = findViewById(R.id.iv_cBox);
        mainLayout = findViewById(R.id.loginMainlayout);
        progressBar = findViewById(R.id.progress_bar);
        googleLogInBtn = findViewById(R.id.btn_google_sign_in);
        fbLogInBtn = findViewById(R.id.btn_fb_sign_in);
        forgotpassId = findViewById(R.id.forgotpass_id);
        fb_btn = findViewById(R.id.fb_btn);


        fb_btn.setReadPermissions("email");

        if (getIntent().getStringExtra("UserKey") != null) {
            key = getIntent().getStringExtra("UserKey");
        }

        forgotpassId.setOnClickListener(this);
        googleLogInBtn.setOnClickListener(this);
        fbLogInBtn.setOnClickListener(this);
        fb_btn.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        ivCheckBox.setOnClickListener(this);
        tvLiveWire.setText(Constant.liveWireText(this));

        //""""""it check user type remember login credential""""""""""
        if (PreferenceConnectorRem.readBoolean(LoginActivity.this, PreferenceConnectorRem.IS_CHECKED, false)) {
            if (PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_USER_TYPE, "").equals(key) && key.equals("worker")) { //""""""if worker
                etMail.setText(PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_EMAIL, ""));
                etPass.setText(PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_PASS, ""));
                ivCheckBox.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check));
            } else if (PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_USER_TYPE, "").equals(key) && key.equals("client")) {//""if Client
                etMail.setText(PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_EMAIL, ""));
                etPass.setText(PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_PASS, ""));
                ivCheckBox.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check));
            }
        }
    }

    //***************checkBox for remeber password***********//
    private void checkBoxSelector() {
        if (!isremember) {
            ivCheckBox.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check));
            isremember = true;
        } else {
            ivCheckBox.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_uncheck));
            isremember = false;
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_cBox:
                checkBoxSelector();
                break;
            case R.id.btn_signup:
                Intent intent = new Intent(this, SignupActivity.class);
                intent.putExtra("UserTypeKey", key);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_login:
                fieldValidation();
                break;
            case R.id.btn_google_sign_in:
                googleSignIn();
                break;
            case R.id.forgotpass_id:
                showForgotDialog();
                break;
            case R.id.btn_fb_sign_in:
                fb_btn.performClick();
                break;
            case R.id.fb_btn:
                fbResponce();
                break;
        }
    }

    private void fbResponce() {
        fb_btn.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginResult.getAccessToken();
                getUserDtails(loginResult);
            }

            @Override
            public void onCancel() {
            //noting
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Facebook error", error.getMessage());
            }
        });
    }

    private void getUserDtails(final LoginResult loginResult) {
        data_request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {


                JSONObject graphObject = response.getJSONObject();
                if (graphObject.has("email")) {
                    String fb_mail = null;
                    try {
                        fb_mail = graphObject.getString("email");
                        String fbId = graphObject.getString("id");
                        if (graphObject.getString("picture") != null) {
                            imageUrl =object.getJSONObject("picture").getJSONObject("data").getString("url");;
                        }
                        UserModel model = new UserModel();
                        model.name = graphObject.getString("name");
                        model.email = graphObject.getString("email");
                        // model.profileImage = acct.getPhotoUrl();
                        model.userType = key;
                        model.deviceType = "2";
                        model.deviceToken = "";
                        model.socialId = graphObject.getString("id");
                        model.socialType = "fb";
                        signUpApiForSocial(model);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(LoginActivity.this, "Name" + fb_mail, Toast.LENGTH_SHORT).show();
                }
            }

        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "id, name, email, picture.width(120).height(120)");
        data_request.setParameters(bundle);
        data_request.executeAsync();
    }

    private void showForgotDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.forgot_dialog);
        dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
        final EditText etEmailDialog = dialog.findViewById(R.id.et_email_dialog);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        final ProgressBar progressBar1 = dialog.findViewById(R.id.progress_bar);
        final RelativeLayout forgotDialogLayout = dialog.findViewById(R.id.forgot_layout);
        dialog.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmailValid(etEmailDialog) || etEmailDialog.getText().toString().isEmpty()) {
                    Constant.snackBar(forgotDialogLayout, getString(R.string.valide_email));
                    etEmailDialog.startAnimation(shake);
                    Constant.hideSoftKeyBoard(LoginActivity.this, etEmailDialog);
                } else {
                    dilogResetPass(etEmailDialog.getText().toString().trim(), forgotDialogLayout, dialog, progressBar1);
                    Constant.hideSoftKeyBoard(LoginActivity.this, etEmailDialog);
                }
            }
        });


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.hideSoftKeyBoard(LoginActivity.this, etEmailDialog);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private boolean isEmailValid(EditText forgotEmailText) {
        String getValue = forgotEmailText.getText().toString().trim();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(getValue).matches();
    }

    //"""""""""""Reset password api calling""""""""""//
    private void dilogResetPass(String email, final RelativeLayout forgot_layout, final Dialog dialog, final ProgressBar progressBar1) {
        if (Constant.isNetworkAvailable(this, forgot_layout)) {
            progressDialog.show();
            // progressBar1.setVisibility(View.VISIBLE);
            AndroidNetworking.post(BASE_URL + "forgotPassword").addBodyParameter("email", email).setPriority(Priority.MEDIUM).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    //progressBar1.setVisibility(View.GONE);
                    String status = null;
                    try {
                        status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            Toast.makeText(LoginActivity.this, "Password sent to your email successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Constant.snackBar(forgot_layout, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    dialog.dismiss();
                    Log.e(TAG, anError.getErrorDetail());
                }
            });
        }
    }

    //""""""google sign in """"""""""//
    private void googleSignIn() {
        progressDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callBackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            progressDialog.dismiss();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    //"""""""""""gmail response"""""""//
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String personName = acct.getDisplayName();
            String email = acct.getEmail();
            if (acct.getPhotoUrl() != null) {
                imageUrl = String.valueOf(acct.getPhotoUrl());
            }
            UserModel model = new UserModel();
            model.name = acct.getDisplayName();
            model.email = acct.getEmail();
            // model.profileImage = acct.getPhotoUrl();
            model.userType = key;
            model.deviceType = "2";
            model.deviceToken = "";
            model.socialId = acct.getId();
            model.socialType = "gmail";
            signUpApiForSocial(model);
            Log.e(TAG, "Name: " + personName + ", email: " + email + "social: " + acct.getId());
        }
    }

    //"""""""""signup with google and fb""""""""""""""""""//
    private void signUpApiForSocial(UserModel model) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            // progressBar.setVisibility(View.VISIBLE);
            AndroidNetworking.post(BASE_URL + "userRegistration")
                    .addBodyParameter(model)
                    .setPriority(Priority.MEDIUM).
                    build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        progressDialog.dismiss();
                        //progressBar.setVisibility(View.GONE);
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                            Log.d("Responce", userResponce.getData().getUserType());
                            PreferenceConnector.writeBoolean(LoginActivity.this, PreferenceConnector.IS_LOG_IN, true);
                            PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_INFO_JSON, response.toString());
                            PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                            PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());
                            PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
                            Log.d("Responce", userResponce.toString());
                            if (userResponce.getData().getUserType().equals("worker")) {// if user is worker
                                Intent intent = null;
                                if (userResponce.getData().getCompleteProfile().equals("0")) { // if worker not complete own profile
                                    finishAffinity();
                                    intent = new Intent(LoginActivity.this, CompleteProfileActivity.class);
                                    intent.putExtra("imageKey", imageUrl);
                                    startActivity(intent);
                                    finish();
                                } else if (userResponce.getData().getCompleteProfile().equals("1")) {// if worker complete own profile
                                    finishAffinity();
                                    intent = new Intent(LoginActivity.this, CompleteProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else { // if user is Client
                                finishAffinity();
                                Intent intent = new Intent(LoginActivity.this, ClientMainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }

                        /*    Intent intent = new Intent(LoginActivity.this, CompleteProfileActivity.class);
                            startActivity(intent);
                            finish();*/

                        } else {
                            if (message.equals("Invalid user type")){
                                LoginManager.getInstance().logOut();
                            }
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

    //""""""""""registration validations""""""//
    private void fieldValidation() {
        if (Validation.isEmpty(etMail)) {
            Constant.snackBar(mainLayout, "Please enter Email Id");
            etMail.startAnimation(shake);
        } else if (etMail.getText().toString().trim().contains(" ")) {
            Constant.snackBar(mainLayout, "Email can't hold space");
            etMail.startAnimation(shake);
        } else if (!Validation.isEmailValid(etMail)) {
            Constant.snackBar(mainLayout, "Please enter valid Email Id");
            etMail.startAnimation(shake);
        } else if (Validation.isEmpty(etPass)) {
            Constant.snackBar(mainLayout, "Please enter password");
            etPass.startAnimation(shake);
        } else if (etPass.getText().toString().contains(" ")) {
            Constant.snackBar(mainLayout, "Password can't hold space");
            etPass.startAnimation(shake);
        } else if (etPass.getText().toString().length() < 6) {
            Constant.snackBar(mainLayout, "Password should be 6 character");
            etPass.startAnimation(shake);
        } else {
            Constant.hideSoftKeyBoard(this, etMail);
            UserModel model = new UserModel();
            model.email = etMail.getText().toString();
            model.password = etPass.getText().toString();
            model.userType = key;
            model.deviceType = "2";
            model.deviceToken = "";
            loginApi(model);
        }

    }

    //"""""""""" login api """"""""""""""""""//
    private void loginApi(UserModel model) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            // progressBar.setVisibility(View.VISIBLE);
            //  AndroidNetworking.post("http://dev.mindiii.com/livewire/service/userLogin")
            AndroidNetworking.post(BASE_URL + "userLogin")
                    .addBodyParameter(model)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            //progressBar.setVisibility(View.GONE);
                            try {
                                String status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                    SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                                    PreferenceConnector.writeBoolean(LoginActivity.this, PreferenceConnector.IS_LOG_IN, true);
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_INFO_JSON, response.toString());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());


                                    if (isremember) {// if remember email and password
                                        if (userResponce.getData().getUserType().equals("worker")) {
                                            PreferenceConnectorRem.writeBoolean(LoginActivity.this, PreferenceConnectorRem.IS_CHECKED, true);
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_EMAIL, userResponce.getData().getEmail());
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_PASS, etPass.getText().toString().trim());
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_USER_TYPE, userResponce.getData().getUserType());
                                            PreferenceConnectorRem.writeBoolean(LoginActivity.this, PreferenceConnectorRem.IS_Worker, true);

                                        } else if (userResponce.getData().getUserType().equals("client")) {// if remember email and password
                                            PreferenceConnectorRem.writeBoolean(LoginActivity.this, PreferenceConnectorRem.IS_CHECKED, true);
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_EMAIL, userResponce.getData().getEmail());
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_PASS, etPass.getText().toString().trim());
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_USER_TYPE, userResponce.getData().getUserType());
                                            PreferenceConnectorRem.writeBoolean(LoginActivity.this, PreferenceConnectorRem.IS_CLIENT, true);
                                        }

                                    } else {
                                        PreferenceConnector.writeBoolean(LoginActivity.this, PreferenceConnector.REM_LOGIN_INFO, false);
                                    }

                                    if (userResponce.getData().getUserType().equals("worker")) {// if user is worker
                                        Intent intent = null;
                                        if (userResponce.getData().getCompleteProfile().equals("0")) { // if worker not complete own profile
                                            finishAffinity();
                                            intent = new Intent(LoginActivity.this, CompleteProfileActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        } else if (userResponce.getData().getCompleteProfile().equals("1")) {// if worker complete own profile
                                            finishAffinity();
                                            intent = new Intent(LoginActivity.this, WorkerMainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else { // if user is Client
                                        finishAffinity();
                                        Intent intent = new Intent(LoginActivity.this, ClientMainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }

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
                            Log.v("Error", anError.toString());

                        }
                    });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
