package com.livewire.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.model.UserInfoFcm;
import com.livewire.model.UserModel;
import com.livewire.responce.SignUpResponce;
import com.livewire.ui.dialog.EmailDialog;
import com.livewire.utils.Constant;
import com.livewire.utils.PermissionAll;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.PreferenceConnectorRem;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.CHECK_SOCIAL_STATUS_API;
import static com.livewire.utils.ApiCollection.FORGOT_PASSWORD_API;
import static com.livewire.utils.ApiCollection.USER_LOGIN_API;
import static com.livewire.utils.ApiCollection.USER_REGISTRATION_API;

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
    private String personName = "";
    private String email = "";
    private SpannableString please;
    private TextView tvSignup;
    private Button btnWatchVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeView();
        // printHashKey();
    }


/*    public void printHashKey() {
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

        callBackManager = CallbackManager.Factory.create();

        //""""""" all permission """"""""""""
        PermissionAll permissionAll = new PermissionAll();
        permissionAll.RequestMultiplePermission(LoginActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //"""""""" shake fields """"""""//
        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanim);
        TextView tvLiveWire = findViewById(R.id.tv_live_wire);
        TextView btnSignup = findViewById(R.id.btn_signup);
        Button btnLogin = findViewById(R.id.btn_login);
        etMail = findViewById(R.id.et_email);
        tvSignup = findViewById(R.id.btn_signup1);
        etPass = findViewById(R.id.et_pass);
        ivCheckBox = findViewById(R.id.iv_cBox);
        mainLayout = findViewById(R.id.loginMainlayout);
        progressBar = findViewById(R.id.progress_bar);
        googleLogInBtn = findViewById(R.id.btn_google_sign_in);
        fbLogInBtn = findViewById(R.id.btn_fb_sign_in);
        forgotpassId = findViewById(R.id.forgotpass_id);
        fb_btn = findViewById(R.id.fb_btn);
        btnWatchVideo = findViewById(R.id.btn_watch_video);
        findViewById(R.id.fl_play_video).setOnClickListener(this);

        fb_btn.setReadPermissions("email");

        if (getIntent().getStringExtra("UserKey") != null) {
            key = getIntent().getStringExtra("UserKey");
        }

        forgotpassId.setOnClickListener(this);
        btnWatchVideo.setOnClickListener(this);
        googleLogInBtn.setOnClickListener(this);
        tvSignup.setOnClickListener(this);
        fbLogInBtn.setOnClickListener(this);
        fb_btn.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        ivCheckBox.setOnClickListener(this);
        tvLiveWire.setText(Constant.liveWireText(this));


        please = new SpannableString(" Please ");
        please.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorDarkGray)), 0, please.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignup.append(please);
        SpannableString Signup = new SpannableString(getResources().getString(R.string.sign_up));
        Signup.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGreen)), 0, Signup.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignup.append(Signup);

        //""""""it check user type remember login credential""""""""""
        if (PreferenceConnectorRem.readBoolean(LoginActivity.this, PreferenceConnectorRem.IS_CHECKED, false)) {
            // if (PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_USER_TYPE, "").equals(key) && key.equals("worker")) { //""""""if worker
            etMail.setText(PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_EMAIL, ""));
            etPass.setText(PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_PASS, ""));
            ivCheckBox.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check));
           /* } else if (PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_USER_TYPE, "").equals(key) && key.equals("client")) {//""if Client
                etMail.setText(PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_EMAIL, ""));
                etPass.setText(PreferenceConnectorRem.readString(LoginActivity.this, PreferenceConnectorRem.REM_PASS, ""));
                ivCheckBox.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check));
            }*/
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
        Intent intent;
        int id = v.getId();
        switch (id) {
            case R.id.iv_cBox:
                checkBoxSelector();
                break;
            case R.id.btn_signup:
                intent = new Intent(this, SignupActivity.class);
                // intent.putExtra("UserTypeKey", key);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_signup1:
                intent = new Intent(this, SignupActivity.class);
                // intent.putExtra("UserTypeKey", key);
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
            case R.id.btn_watch_video:
            case R.id.fl_play_video:
                if (Constant.isNetworkAvailable(this, mainLayout)) {
                 /*   Intent intent = new Intent(this, PlayVideoActivity.class);
                    intent.putExtra("VideoUrlKey", videoUrl);
                    startActivity(intent);*/
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    i.setDataAndType(Uri.parse("https://livewire.work/uploads/LiveWireHomeVideol.mp4"), "video/mp4");
                    startActivity(i);
                }
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
                //nothing
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
                String fbId = null;
                try {
                    fbId = graphObject.getString("id");
                    personName = graphObject.getString("name");
                    if (graphObject.getString("picture") != null) {
                        imageUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    }
                    if (graphObject.has("email")) {
                        email = graphObject.getString("email");
                        checkSocialLogin(fbId, "fb");
                           /* UserModel model = new UserModel();
                            model.name = graphObject.getString("name");
                            model.email = graphObject.getString("email");
                            model.profileImage = imageUrl;
                            // model.userType = key;
                            model.deviceType = "2";
                            model.deviceToken = FirebaseInstanceId.getInstance().getToken();
                            model.socialId = graphObject.getString("id");
                            model.socialType = "fb";
                            signUpApiForSocial(model);*/
                        //Toast.makeText(LoginActivity.this, "Name" + fb_mail, Toast.LENGTH_SHORT).show();
                    } else checkSocialfbLogin(fbId, "fb");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "id, name, email, picture.width(120).height(120)");
        data_request.setParameters(bundle);
        data_request.executeAsync();
    }

    private void checkSocialfbLogin(String fbId, String fb) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + CHECK_SOCIAL_STATUS_API)
                    .addBodyParameter("socialId", fbId)
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

                                    String isSocialLogin = response.getString("socialId");
                                    if (isSocialLogin.equals("0")) {// sign up
                                        openEmailIdDialog(fbId, "fb");
                                    } else {// log in
                                        UserModel model = new UserModel();
                                        model.name = personName;
                                        model.email = email;
                                        //model.profileImage = imageUrl;
                                        model.deviceType = "2";
                                        model.deviceToken = FirebaseInstanceId.getInstance().getToken();
                                        model.socialId = fbId;
                                        model.socialType = fb;
                                        signUpApiForSocial(model, "Second");
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
                            Log.e(TAG, anError.getErrorDetail());
                        }
                    });
        }
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
            AndroidNetworking.post(BASE_URL + FORGOT_PASSWORD_API)
                    .addBodyParameter("email", email)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
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
            assert acct != null;
            checkSocialLogin(acct.getId(), "gmail");
            personName = acct.getDisplayName();
            email = acct.getEmail();
            if (acct.getPhotoUrl() != null) {
                imageUrl = String.valueOf(acct.getPhotoUrl());
            }
           /* UserModel model = new UserModel();
            model.name = acct.getDisplayName();
            model.email = acct.getEmail();
            model.profileImage = String.valueOf(acct.getPhotoUrl());
            // model.userType = key;
            model.deviceType = "2";
            model.deviceToken = FirebaseInstanceId.getInstance().getToken();
            model.socialId = acct.getId();
            model.socialType = "gmail";
            signUpApiForSocial(model, "");*/

     /*       if (key.equals("client")) {
                checkSocialLogin(acct.getId(), "gmail");
                personName = acct.getDisplayName();
                email = acct.getEmail();
                if (acct.getPhotoUrl() != null) {
                    imageUrl = String.valueOf(acct.getPhotoUrl());
                }
            } else {
                UserModel model = new UserModel();
                model.name = acct.getDisplayName();
                model.email = acct.getEmail();
                model.profileImage = String.valueOf(acct.getPhotoUrl());
                model.userType = key;
                model.deviceType = "2";
                model.deviceToken = FirebaseInstanceId.getInstance().getToken();
                model.socialId = acct.getId();
                model.socialType = "gmail";
                signUpApiForSocial(model);
                Log.e(TAG, "Name: " + personName + ", email: " + email + "social: " + acct.getId());
            }*/
        }
    }

    //"""""" If you are sign up from social to check already registred or not === if not registred so open location dialog
    private void checkSocialLogin(final String id, final String socialType) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + CHECK_SOCIAL_STATUS_API)
                    .addBodyParameter("socialId", id)
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

                                    String isSocialLogin = response.getString("socialId");
                                    if (isSocialLogin.equals("0")) {// sign up
                                        UserModel model = new UserModel();
                                        model.name = personName;
                                        model.email = email;
                                        model.profileImage = imageUrl;
                                        model.deviceType = "2";
                                        model.deviceToken = FirebaseInstanceId.getInstance().getToken();
                                        model.socialId = id;
                                        model.socialType = socialType;
                                        signUpApiForSocial(model, "First");
                                    } else {// log in
                                        UserModel model = new UserModel();
                                        model.name = personName;
                                        model.email = email;
                                        //model.profileImage = imageUrl;
                                        model.deviceType = "2";
                                        model.deviceToken = FirebaseInstanceId.getInstance().getToken();
                                        model.socialId = id;
                                        model.socialType = socialType;
                                        signUpApiForSocial(model, "Second");
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
                            Log.e(TAG, anError.getErrorDetail());
                        }
                    });
        }


    }

    /*   ///""""""" open location dialog if user want to sign up at client side """"""""//
       private void openLocationDialog(final String id, final String socialType) {
           final LocationDialog dialog = new LocationDialog();
           dialog.show(getSupportFragmentManager(), "");
           dialog.setCancelable(false);
           dialog.getLocationInfo(new LocationDialog.LocationDialogListner() {
               @Override
               public void onLocationOnClick(String town, LatLng latLng, LinearLayout layout) {
                   dialog.dismiss();
                   UserModel model = new UserModel();
                   model.name = personName;
                   model.email = email;
                   //  model.userType = key;
                   model.deviceType = "2";
                   model.town = town;
                   model.latitude = String.valueOf(latLng.latitude);
                   model.longitude = String.valueOf(latLng.longitude);
                   if (imageUrl != null) {
                       // imageUrl = String.valueOf(acct.getPhotoUrl());
                       model.profileImage = imageUrl;
                   }
                   model.deviceToken = FirebaseInstanceId.getInstance().getToken();
                   model.socialId = id;
                   model.socialType = socialType;
                   signUpApiForSocial(model, "");
               }
           });
       }
   */
    private void openEmailIdDialog(String fbId, String fb) {
        final EmailDialog dialog = new EmailDialog();
        dialog.show(getSupportFragmentManager(), "");
        dialog.setCancelable(false);
        dialog.getEmailInfo(new EmailDialog.LocationDialogListner() {
            @Override
            public void openEmailIdDialog(String email1) {
                dialog.dismiss();
                email = email1;
                UserModel model = new UserModel();
                model.name = personName;
                model.email = email;
                model.profileImage = imageUrl;
                model.deviceType = "2";
                model.deviceToken = FirebaseInstanceId.getInstance().getToken();
                model.socialId = fbId;
                model.socialType = fb;
                signUpApiForSocial(model, "First");

                /*UserModel model = new UserModel();
                model.name = personName;
                model.email = email;
                //  model.userType = key;
                model.deviceType = "2";
                if (imageUrl != null) {
                    // imageUrl = String.valueOf(acct.getPhotoUrl());
                    model.profileImage = imageUrl;
                }
                model.deviceToken = FirebaseInstanceId.getInstance().getToken();
                // model.socialId = id;
                // model.socialType = socialType;
                signUpApiForSocial(model);*/
            }
        });
    }

    //"""""""""signup with google and fb""""""""""""""""""//
    private void signUpApiForSocial(UserModel model, String isSignUp) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            // progressBar.setVisibility(View.VISIBLE);
            AndroidNetworking.post(BASE_URL + USER_REGISTRATION_API)
                    .addBodyParameter(model)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
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
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, userResponce.getData().getStripe_customer_id());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.IS_BANK_ACC, userResponce.getData().getIs_bank_account());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.AVAILABILITY_1, userResponce.getData().getAvailability());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.PASS_WORD, userResponce.getData().getPassword());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.SOCIAL_LOGIN, userResponce.getData().getSocialType());

                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getThumbImage());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_DOB, userResponce.getData().getConfirm_dob());

                                    if (userResponce.getData().getUser_mode().equals("worker")) {// if user is worker
                                        Intent intent = null;
                                        addUserFirebaseDatabase();
                                        finishAffinity();
                                        intent = new Intent(LoginActivity.this, WorkerMainActivity.class);
                                        intent.putExtra("workerMyProfile","workerMyProfile");
                                        startActivity(intent);
                                        finish();
                                        soicialLogOut();
                                    } else { // if user is Client
                                        addUserFirebaseDatabase();
                                        finishAffinity();
                                        Intent intent = null;

                                       /*
                                       //"""""""" first sign up case is removed """"22/05/19"""""//
                                       if (isSignUp.equals("First")){// first sign up
                                            intent = new Intent(LoginActivity.this, MyProfileClientActivity.class);
                                            intent.putExtra("SignUp", "SignUp");
                                        } else {
                                            intent = new Intent(LoginActivity.this, ClientMainActivity.class);
                                        }*/

                                        intent = new Intent(LoginActivity.this, ClientMainActivity.class);
                                        intent.putExtra("ClientMyProfile", "ClientMyProfile");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                        soicialLogOut();
                                    }

                                } else {
                                    soicialLogOut();
                                    Constant.snackBar(mainLayout, message);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            soicialLogOut();
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void soicialLogOut() {
        LoginManager.getInstance().logOut();// fb logout
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback( // google logout
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    //""""""""""registration validations""""""//
    private void fieldValidation() {
        if (Validation.isEmpty(etMail)) {
            Constant.snackBar(mainLayout, getString(R.string.please_enter_email_id));
            etMail.startAnimation(shake);
        } else if (etMail.getText().toString().trim().contains(" ")) {
            Constant.snackBar(mainLayout, getString(R.string.email_can_hold_space));
            etMail.startAnimation(shake);
        } else if (!Validation.isEmailValid(etMail)) {
            Constant.snackBar(mainLayout, getString(R.string.please_enter_valid_email_id));
            etMail.startAnimation(shake);
        } else if (Validation.isEmpty(etPass)) {
            Constant.snackBar(mainLayout, getString(R.string.please_enter_password));
            etPass.startAnimation(shake);
        } else if (etPass.getText().toString().contains(" ")) {
            Constant.snackBar(mainLayout, getString(R.string.pass_can_hold_space));
            etPass.startAnimation(shake);
        } else if (etPass.getText().toString().length() < 6) {
            Constant.snackBar(mainLayout, getString(R.string.pass_should_have_minimum_six_char));
            etPass.startAnimation(shake);
        } else if (etPass.getText().toString().length() > 10) {
            etPass.startAnimation(shake);
            etPass.requestFocus();
            Constant.snackBar(mainLayout, getString(R.string.password_should_not_be_more_than_ten_characters));
        } else {
            Constant.hideSoftKeyBoard(this, etMail);
            UserModel model = new UserModel();
            model.email = etMail.getText().toString().trim();
            model.password = etPass.getText().toString().trim();
            //   model.userType = key;
            model.deviceType = "2";
            model.deviceToken = FirebaseInstanceId.getInstance().getToken();
            loginApi(model);
        }

    }

    //"""""""""" login api """"""""""""""""""//
    private void loginApi(UserModel model) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + USER_LOGIN_API)
                    .addBodyParameter(model)
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
                                    SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                                    PreferenceConnector.writeBoolean(LoginActivity.this, PreferenceConnector.IS_LOG_IN, true);
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_INFO_JSON, response.toString());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.PASS_WORD, userResponce.getData().getPassword());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.IS_BANK_ACC, userResponce.getData().getIs_bank_account());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, userResponce.getData().getStripe_customer_id());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.AVAILABILITY_1, userResponce.getData().getAvailability());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.SOCIAL_LOGIN, userResponce.getData().getSocialType());

                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getThumbImage());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.USER_DOB, userResponce.getData().getConfirm_dob());

                                    addUserFirebaseDatabase();

                                    if (isremember) {// if remember email and password

                                        PreferenceConnectorRem.writeBoolean(LoginActivity.this, PreferenceConnectorRem.IS_CHECKED, true);
                                        PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_EMAIL, userResponce.getData().getEmail());
                                        PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_PASS, etPass.getText().toString().trim());
                                        PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_USER_TYPE, userResponce.getData().getUser_mode());

                                      /*  if (userResponce.getData().getUser_mode().equals("worker")) {
                                            PreferenceConnectorRem.writeBoolean(LoginActivity.this, PreferenceConnectorRem.IS_CHECKED, true);
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_EMAIL, userResponce.getData().getEmail());
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_PASS, etPass.getText().toString().trim());
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_USER_TYPE, userResponce.getData().getUserType());
                                            PreferenceConnectorRem.writeBoolean(LoginActivity.this, PreferenceConnectorRem.IS_Worker, true);

                                        } else if (userResponce.getData().getUser_mode().equals("client")) {// if remember email and password
                                            PreferenceConnectorRem.writeBoolean(LoginActivity.this, PreferenceConnectorRem.IS_CHECKED, true);
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_EMAIL, userResponce.getData().getEmail());
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_PASS, etPass.getText().toString().trim());
                                            PreferenceConnectorRem.writeString(LoginActivity.this, PreferenceConnectorRem.REM_USER_TYPE, userResponce.getData().getUserType());
                                            PreferenceConnectorRem.writeBoolean(LoginActivity.this, PreferenceConnectorRem.IS_CLIENT, true);
                                        }*/

                                    } else {
                                        PreferenceConnector.writeBoolean(LoginActivity.this, PreferenceConnector.REM_LOGIN_INFO, false);
                                    }

                                    if (userResponce.getData().getUser_mode().equals("worker")) {// if user is worker
                                        Intent intent = null;
                                        finishAffinity();
                                      //  intent = new Intent(LoginActivity.this, MyProfileWorkerActivity.class);
                                        intent = new Intent(LoginActivity.this, WorkerMainActivity.class);
                                        intent.putExtra("workerMyProfile","workerMyProfile");

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                       /* if (userResponce.getData().getCompleteProfile().equals("0")) { // if worker not complete own profile
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
                                        }*/
                                    } else { // if user is Client
                                        finishAffinity();
                                       // Intent intent = new Intent(LoginActivity.this, MyProfileClientActivity.class);
                                      Intent intent = new Intent(LoginActivity.this, ClientMainActivity.class);
                                        intent.putExtra("ClientMyProfile", "ClientMyProfile");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                       /* Intent intent = new Intent(LoginActivity.this, MyProfileClientActivity.class);
                                        intent.putExtra("SignUp","SignUp");
                                        startActivity(intent);
                                        finish();*/
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

    //""""""""" register user in Firebase data base  """""""""""""""//
    private void addUserFirebaseDatabase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        UserInfoFcm infoFcm = new UserInfoFcm();
        infoFcm.email = PreferenceConnector.readString(this, PreferenceConnector.Email, "");
        infoFcm.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        infoFcm.name = PreferenceConnector.readString(this, PreferenceConnector.Name, "");
        infoFcm.notificationStatus = "";
        infoFcm.profilePic = PreferenceConnector.readString(this, PreferenceConnector.PROFILE_IMG, "");
        infoFcm.uid = PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, "");
        infoFcm.userType = PreferenceConnector.readString(this, PreferenceConnector.USER_TYPE, "");
        infoFcm.authToken = PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, "");
        infoFcm.availability = PreferenceConnector.readString(this, PreferenceConnector.AVAILABILITY_1, "");

        database.child(Constant.ARG_USERS)
                .child(PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, ""))
                .setValue(infoFcm)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Utils.goToOnlineStatus(SignInActivity.this, Constant.online);

                          /* if (isProfileUpdate.equals("1")) {
                               MainActivity.start(SignInActivity.this, false);
                               finish();
                           } else {
                               Intent intent = new Intent(SignInActivity.this, EditProfileActivity.class);
                               startActivity(intent);
                               finish();

                              *//*  MainActivity.start(SignInActivity.this, false);
                                finish();*//*
                           }*/
                        } else {
                            Toast.makeText(LoginActivity.this, "Not Store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
