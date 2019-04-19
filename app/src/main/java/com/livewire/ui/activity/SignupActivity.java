package com.livewire.ui.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
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
import com.livewire.ui.dialog.LocationDialog;
import com.livewire.utils.Constant;
import com.livewire.utils.ImageRotator;
import com.livewire.utils.PermissionAll;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.CHECK_SOCIAL_STATUS_API;
import static com.livewire.utils.ApiCollection.USER_REGISTRATION_API;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener ,GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = SignupActivity.class.getName();
    private EditText etFullName;
    private EditText etEmail;
    private EditText etPass;
    private Animation shake;
    private TextView btnLogin;
    private TextView tvTownResidence;
    private RelativeLayout mainLayout;
    private ProgressBar progressBar;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private LatLng locationLatLng;
    private String locationPlace;
    private Uri tmpUri;
    private Bitmap profileImageBitmap;
    private ImageView ivProfileImg;
    private ImageView inactiveUserImg;
    private File userImageFile;
    private ProgressDialog progressDialog;
    private LoginButton fb_btn;
    private LinearLayout googleLogInBtn;
    private LinearLayout fbLogInBtn;
    private CallbackManager callBackManager;
    private GraphRequest data_request;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 7;
    private String imageUrl;
    private static String key;
    private String email="";
    private String personName="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        intializeView();
    }


    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        //""""""" all permission """"""""""""
        PermissionAll permissionAll = new PermissionAll();
        permissionAll.RequestMultiplePermission(SignupActivity.this);

        TextView tvLiveWire = findViewById(R.id.tv_live_wire);
        mainLayout = findViewById(R.id.main_layout);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email1);
        etPass = findViewById(R.id.et_pass);
        ImageView ivBack = findViewById(R.id.iv_back);
        Button btnWorkerSignup = findViewById(R.id.btn_worker_signup);
        Button btnClientSignup = findViewById(R.id.btn_client_signup);
        btnLogin = findViewById(R.id.btn_login);
        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanim);
        progressBar = findViewById(R.id.progress_bar);

        inactiveUserImg = findViewById(R.id.inactive_user_img);
        ivProfileImg = findViewById(R.id.iv_profile_img);
        FrameLayout flUserProfile = findViewById(R.id.fl_user_profile);
        tvTownResidence = findViewById(R.id.tv_town_resident);
        View horizontalLine = findViewById(R.id.horizontal_line);
        googleLogInBtn = findViewById(R.id.btn_google_sign_in);
        fbLogInBtn = findViewById(R.id.btn_fb_sign_in);
        fb_btn = findViewById(R.id.fb_btn);
        fb_btn.setReadPermissions("email");//fb email permission

        callBackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               // .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


     /*   key = getIntent().getStringExtra("UserTypeKey");
        if (getIntent().getStringExtra("UserTypeKey").equals("client")) {// if client side
            flUserProfile.setVisibility(View.VISIBLE);
            tvTownResidence.setVisibility(View.VISIBLE);
            horizontalLine.setVisibility(View.VISIBLE);
            btnClientSignup.setVisibility(View.VISIBLE);

        } else if (getIntent().getStringExtra("UserTypeKey").equals("worker")) {// if worker side
            flUserProfile.setVisibility(View.GONE);
            tvTownResidence.setVisibility(View.GONE);
            horizontalLine.setVisibility(View.GONE);
            btnClientSignup.setVisibility(View.GONE);
            btnWorkerSignup.setVisibility(View.VISIBLE);
            btnWorkerSignup.setOnClickListener(this);
        }*/

        btnWorkerSignup.setVisibility(View.GONE);
        flUserProfile.setOnClickListener(this);
        btnClientSignup.setOnClickListener(this);
        tvTownResidence.setOnClickListener(this);

        tvLiveWire.setText(Constant.liveWireText(this));
        ivBack.setOnClickListener(this);
        mainLayout.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        googleLogInBtn.setOnClickListener(this);
        fbLogInBtn.setOnClickListener(this);
        fb_btn.setOnClickListener(this);

        tvTownResidence.setSelected(true);
        tvTownResidence.setHorizontallyScrolling(true);
        tvTownResidence.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_worker_signup:
                etWorkerValdidations();
                break;
            case R.id.btn_client_signup:
                etClientValdidations();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.main_layout:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                break;
            case R.id.btn_login:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_town_resident:
                autoCompletePlacePicker();
                break;
            case R.id.fl_user_profile:
                Constant.hideSoftKeyBoard(this, etEmail);
                showSetProfileImageDialog();
                break;
            case R.id.btn_google_sign_in:
                googleSignIn();
                break;
            case R.id.btn_fb_sign_in:
                fb_btn.performClick();
                break;
            case R.id.fb_btn:
                fbResponce();
                break;
            default:

        }
    }



    //""""""google sign in """"""""""//
    private void googleSignIn() {
        progressDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //"""""""" open fb dialog """"""""""//
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

    //"""""""  fb log in responce """"""""""""//
    private void getUserDtails(final LoginResult loginResult) {
        data_request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject graphObject = response.getJSONObject();
                String fbId = null;
                try {
                    fbId = graphObject.getString("id");
                    email = graphObject.getString("email");
                    personName = graphObject.getString("name");
                    if (graphObject.getString("picture") != null) {
                        imageUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    }
                    if (graphObject.has("email")) {
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
                    } else checkSocialfbLogin(fbId,"fb");
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
                model.userType = key;
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
                signUpApiForSocial(model, "First");
            }
        });
    }

    //"""""""""""gmail response"""""""//
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            assert acct != null;
            checkSocialLogin(acct.getId(), "gmail");
            personName = acct.getDisplayName();
            email = acct.getEmail();
            if (acct.getPhotoUrl() != null) {
                imageUrl = String.valueOf(acct.getPhotoUrl());
            }

          /*  // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //checkSocialLogin(acct.getId(), "gmail");
            personName = acct.getDisplayName();
            email = acct.getEmail();

            if (acct.getPhotoUrl() != null) {
                imageUrl = String.valueOf(acct.getPhotoUrl());
            }

            UserModel model = new UserModel();
            model.name = personName;
            model.email = email;
            model.userType = key;
            model.deviceType = "2";
            model.deviceToken = FirebaseInstanceId.getInstance().getToken();
            model.socialId = acct.getId();
            model.socialType = "gmail";

            signUpApiForSocial(model);*/

          /*  if (key.equals("client")) {
                checkSocialLogin(acct.getId(), "gmail");
                personName = acct.getDisplayName();
                email = acct.getEmail();

                if (acct.getPhotoUrl() != null) {
                    imageUrl = String.valueOf(acct.getPhotoUrl());
                }
                Log.e(TAG, "Name: " + personName + ", email: " + email + "social: " + acct.getId());
            }else {
                UserModel model = new UserModel();
                model.name = acct.getDisplayName();
                model.email = acct.getEmail();
                // model.profileImage = acct.getPhotoUrl();
                model.userType = key;
                model.deviceType = "2";
                model.deviceToken = FirebaseInstanceId.getInstance().getToken();
                model.socialId = acct.getId();
                model.socialType = "gmail";
                signUpApiForSocial(model);
            }*/

        }
    }

    //"""""""""signup with google and fb""""""""""""""""""//
    private void signUpApiForSocial(UserModel model, String first) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + USER_REGISTRATION_API)
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
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_INFO_JSON, response.toString());
                            setSocialResponceData(userResponce,first);
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

    ///""""""" set social responce data """"""""""//
    private void setSocialResponceData(SignUpResponce userResponce, String isSignUp) {
        PreferenceConnector.writeBoolean(SignupActivity.this, PreferenceConnector.IS_LOG_IN, true);
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.SOCIAL_LOGIN, userResponce.getData().getSocialType());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, userResponce.getData().getStripe_customer_id());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.IS_BANK_ACC, userResponce.getData().getIs_bank_account());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_DOB, userResponce.getData().getConfirm_dob());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AVAILABILITY_1, userResponce.getData().getAvailability());

        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getThumbImage());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());
       // addUserFirebaseDatabase();

        if (userResponce.getData().getUser_mode().equals("worker")) {// if user is worker
            Intent intent = null;
            addUserFirebaseDatabase();
            finishAffinity();
            intent = new Intent(SignupActivity.this, WorkerMainActivity.class);
            startActivity(intent);
            finish();
            soicialLogOut();
        } else { // if user is Client
            addUserFirebaseDatabase();
            finishAffinity();
            Intent intent = null;
            if (isSignUp.equals("First")) {// first sign up
                intent = new Intent(SignupActivity.this, MyProfileClientActivity.class);
                intent.putExtra("SignUp", "SignUp");
            } else {
                intent = new Intent(SignupActivity.this, ClientMainActivity.class);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            soicialLogOut();
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
    //"""""" open image dialog """"""""""""//
    private void showSetProfileImageDialog() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);

        builder.setTitle("Add Photo");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Take Photo")) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //***** Ensure that there's a camera activity to handle the intent ****//
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        //** Create the File where the photo should go *****//
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            Log.d(TAG, ex.getMessage());
                        }
                        if (photoFile != null) {
                            tmpUri = FileProvider.getUriForFile(SignupActivity.this,
                                    getApplicationContext().getPackageName() + ".fileprovider",
                                    getTemporalFile(SignupActivity.this));
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);
                            startActivityForResult(takePictureIntent, Constant.CAMERA);
                        }
                    }

                } else if (options[which].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.GALLERY);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callBackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.GALLERY && resultCode == RESULT_OK && null != data) {
            //isCamera = false;
            tmpUri = data.getData();
            if (tmpUri != null) { // it will go to the CropImageActivity
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                    userImageFile = savebitmap(this, bitmap, UUID.randomUUID() + ".jpg");
                    // userImageFile = new File(tmpUri.toString());
                    inactiveUserImg.setVisibility(View.GONE);
                    ivProfileImg.setImageURI(tmpUri);
                    profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }


              /*  CropImage.activity(tmpUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(200, 200)
                        .setMaxCropResultSize(4000, 4000)
                        .setAspectRatio(300, 300)
                        .start(this);*/
            }
        } else {
            if (requestCode == Constant.CAMERA && resultCode == RESULT_OK) {
                // isCamera = true;
                Bitmap bm = null;
                File imageFile = getTemporalFile(this);
                Uri photoURI = Uri.fromFile(imageFile);

                bm = Constant.getImageResized(this, photoURI); ///Image resizer
                int rotation = ImageRotator.getRotation(this, photoURI, true);
                bm = ImageRotator.rotate(bm, rotation);

                File profileImagefile = new File(this.getExternalCacheDir(), UUID.randomUUID() + ".jpg");
                tmpUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".fileprovider", profileImagefile);

                try {
                    OutputStream outStream = null;
                    outStream = new FileOutputStream(profileImagefile);
                    bm.compress(Bitmap.CompressFormat.PNG, 80, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
                if (tmpUri != null) { // it will go to the CropImageActivity

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                        userImageFile = savebitmap(this, bitmap, UUID.randomUUID() + ".jpg");
                        // userImageFile = new File(tmpUri.toString());
                        inactiveUserImg.setVisibility(View.GONE);
                        ivProfileImg.setImageURI(tmpUri);
                        profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    /*CropImage.activity(tmpUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300)
                            .start(this);*/
                }
            }
        }

    /*    /************ circle cropping image ********//*/
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {   // Image Cropper
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                tmpUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                    userImageFile = savebitmap(this, bitmap, UUID.randomUUID() + ".jpg");
                    // userImageFile = new File(tmpUri.toString());
                    inactiveUserImg.setVisibility(View.GONE);
                    ivProfileImg.setImageURI(tmpUri);
                    profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }*/

        //*********Autocomplete place p[icker****************//
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Place place = PlaceAutocomplete.getPlace(this, data);
                tvTownResidence.setText(place.getAddress());
                tvTownResidence.setTextColor(ContextCompat.getColor(this,R.color.colorDarkBlack));
                locationPlace = place.getAddress().toString();
                locationLatLng = place.getLatLng();

                Log.e(TAG, "Place: " + place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status1 = PlaceAutocomplete.getStatus(this, data);

                Log.i(TAG, status1.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {

                Constant.hideSoftKeyBoard(SignupActivity.this, etEmail);
            }
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            progressDialog.dismiss();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //"""""""""" create bitmap to file """"""""//
    public File savebitmap(Context mContext, Bitmap bitmap, String name) {
        File filesDir = mContext.getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
            os.flush();
            os.close();
            return imageFile;
        } catch (Exception e) {
            Log.e(mContext.getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return null;
    }

    //"""""' client validations """"""""""""//
    private void etClientValdidations() {
        if (Validation.isEmpty(etFullName)) {
            Constant.snackBar(mainLayout, getString(R.string.please_enter_fullname));
            etFullName.startAnimation(shake);
        } else if (etFullName.getText().toString().length() < 2) {
            Constant.snackBar(mainLayout, getString(R.string.full_name_should_not_less_than_characters));
            etFullName.startAnimation(shake);
        }
        else if (Validation.isEmpty(etEmail)) {
            Constant.snackBar(mainLayout, getString(R.string.please_enter_email_id));
            etEmail.startAnimation(shake);
        } else if (etEmail.getText().toString().trim().contains(" ")) {
            etEmail.startAnimation(shake);
            Constant.snackBar(mainLayout, getString(R.string.email_can_hold_space));
        } else if (!Validation.isEmailValid(etEmail)) {
            etEmail.startAnimation(shake);
            etEmail.requestFocus();
            Constant.snackBar(mainLayout, getString(R.string.please_enter_valid_email_id));
        }
       /* else if (tvTownResidence.getText().toString().equals("")) {
            tvTownResidence.startAnimation(shake);
            tvTownResidence.requestFocus();
            Constant.snackBar(mainLayout, getString(R.string.please_enter_your_location));
        } */
        else if (Validation.isEmpty(etPass)) {
            etPass.startAnimation(shake);
            Constant.snackBar(mainLayout, getString(R.string.pass_can_hold_space));
        } else if (etPass.getText().toString().length() < 6) {
            etPass.startAnimation(shake);
            etPass.requestFocus();
            Constant.snackBar(mainLayout, getString(R.string.pass_should_have_minimum_six_char));
        } else if (etPass.getText().toString().length() > 10) {
            etPass.startAnimation(shake);
            etPass.requestFocus();
            Constant.snackBar(mainLayout, getString(R.string.password_should_not_be_more_than_ten_characters));
        } else {
            UserModel model = new UserModel();
            model.name = etFullName.getText().toString();
            model.email = etEmail.getText().toString();
            model.password = etPass.getText().toString();
            model.town = locationPlace;
            model.userType = "client";
            model.deviceType = "2";
            model.deviceToken = FirebaseInstanceId.getInstance().getToken();
            if (locationLatLng!= null) {
                model.latitude = String.valueOf(locationLatLng.latitude);
                model.longitude = String.valueOf(locationLatLng.longitude);
            }
            signUpClientApi(model);
        }
    }

    //"""""""" signup api client """""""""""//
    private void signUpClientApi(UserModel model) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.upload(BASE_URL+USER_REGISTRATION_API)
                    .addMultipartFile("profileImage", userImageFile)
                    .addMultipartParameter(model)
                    .setPriority(Priority.MEDIUM).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    //  progressBar.setVisibility(View.GONE);
                    String status = null;
                    try {
                        status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                          //  Log.e("sign up response", userResponce.getData().toString());
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_INFO_JSON, response.toString());
                            setSignUpClientData(userResponce);

                        } else {
                            Constant.snackBar(mainLayout, message);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }


                }

                @Override
                public void onError(ANError anError) {
                    progressDialog.dismiss();
                    Log.e(TAG, anError.getErrorDetail());
                }
            });
        }
    }

    //""""""" setSignUpClientData """"""""""""//
    private void setSignUpClientData(SignUpResponce userResponce) {
        PreferenceConnector.writeBoolean(SignupActivity.this, PreferenceConnector.IS_LOG_IN, true);
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.PASS_WORD, etPass.getText().toString());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, userResponce.getData().getStripe_customer_id());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AVAILABILITY_1, userResponce.getData().getAvailability());

        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getThumbImage());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_DOB, userResponce.getData().getConfirm_dob());

        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.STRIPE_FEES, userResponce.getStripe_fees());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.STRIPE_TRANSACTION_FEES, userResponce.getStripe_transaction_fees());
        addUserFirebaseDatabase();

        finishAffinity();
      //  Intent intent = new Intent(SignupActivity.this, ClientMainActivity.class);
        Intent intent = new Intent(SignupActivity.this, MyProfileClientActivity.class);
        intent.putExtra("SignUp","SignUp");
        startActivity(intent);
        finish();
    }

    /*******Auto comple place picker***************/
    private void autoCompletePlacePicker() {
        try {
            // Set the fields to specify which types of place data to
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    //"""""""  worker validations  """""""""//
    private void etWorkerValdidations() {
        if (Validation.isEmpty(etFullName)) {
            Constant.snackBar(mainLayout, getString(R.string.please_enter_fullname));
            etFullName.startAnimation(shake);
        } else if (etFullName.getText().toString().length() < 2) {
            Constant.snackBar(mainLayout, getString(R.string.full_name_should_not_less_than_characters));
            etFullName.startAnimation(shake);
        }  else if (Validation.isEmpty(etEmail)) {
            Constant.snackBar(mainLayout, getString(R.string.please_enter_email_id));
            etEmail.startAnimation(shake);
        } else if (etEmail.getText().toString().trim().contains(" ")) {
            etEmail.startAnimation(shake);
            Constant.snackBar(mainLayout, getString(R.string.email_can_hold_space));
        } else if (!Validation.isEmailValid(etEmail)) {
            etEmail.startAnimation(shake);
            etEmail.requestFocus();
            Constant.snackBar(mainLayout, getString(R.string.please_enter_valid_email_id));
        } else if (Validation.isEmpty(etPass)) {
            etPass.startAnimation(shake);
            Constant.snackBar(mainLayout, getString(R.string.pass_can_hold_space));
        } else if (etPass.getText().toString().length() < 6) {
            etPass.startAnimation(shake);
            etPass.requestFocus();
            Constant.snackBar(mainLayout, getString(R.string.pass_should_have_minimum_six_char));
        } else if (etPass.getText().toString().length() > 10) {
            etPass.startAnimation(shake);
            etPass.requestFocus();
            Constant.snackBar(mainLayout, getString(R.string.password_should_not_be_more_than_ten_characters));
        }else {
            UserModel model = new UserModel();
            model.name = etFullName.getText().toString();
            model.email = etEmail.getText().toString();
            model.password = etPass.getText().toString();
            model.userType = "worker";
            model.deviceType = "2";
            model.deviceToken = FirebaseInstanceId.getInstance().getToken();
            signUpWorkerApi(model);
        }
    }

    private void signUpWorkerApi(UserModel model) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            // progressBar.setVisibility(View.VISIBLE);
            AndroidNetworking.post(BASE_URL+USER_REGISTRATION_API)
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
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_INFO_JSON, response.toString());
                            setSignUpWorkerdata(userResponce);

                        } else {
                            Constant.snackBar(mainLayout, message);
                        }

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {

                }
            });
        }
    }

    //"""""" set sign up responce """""""""""""//
    private void setSignUpWorkerdata(SignUpResponce userResponce) {
        Log.e("sign up response", userResponce.getData().toString());
        PreferenceConnector.writeBoolean(SignupActivity.this, PreferenceConnector.IS_LOG_IN, true);
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.PASS_WORD, etPass.getText().toString());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, userResponce.getData().getStripe_customer_id());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.IS_BANK_ACC, userResponce.getData().getIs_bank_account());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_DOB, userResponce.getData().getConfirm_dob());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AVAILABILITY_1, userResponce.getData().getAvailability());

        finishAffinity();
        Intent intent = new Intent(SignupActivity.this, CompleteProfileActivity.class);
        startActivity(intent);
        finish();
    }


    //***********Create an image file name*******************//
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalCacheDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    //"""""" temp file genrate"""""""//
    private File getTemporalFile(Context context) {
        return new File(context.getExternalCacheDir(), "tmp.jpg");
    }

    //Turn Bitmap into byte array.
    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //""""""""" register user in Firebase data base  """""""""""""""//
    private void addUserFirebaseDatabase(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Log.e(TAG, database.toString());
        UserInfoFcm infoFcm = new UserInfoFcm();
        infoFcm.email = PreferenceConnector.readString(this,PreferenceConnector.Email,"");
        infoFcm.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        infoFcm.name = PreferenceConnector.readString(this,PreferenceConnector.Name,"");
        infoFcm.notificationStatus = "";
        infoFcm.profilePic = PreferenceConnector.readString(this,PreferenceConnector.PROFILE_IMG,"");
        infoFcm.uid = PreferenceConnector.readString(this,PreferenceConnector.MY_USER_ID,"");
        infoFcm.userType = PreferenceConnector.readString(this,PreferenceConnector.USER_TYPE,"");
        infoFcm.userType = PreferenceConnector.readString(this,PreferenceConnector.USER_MODE,"");
        infoFcm.authToken = PreferenceConnector.readString(this,PreferenceConnector.AUTH_TOKEN,"");
        infoFcm.availability = PreferenceConnector.readString(this,PreferenceConnector.AVAILABILITY_1,"");

        database.child(Constant.ARG_USERS)
                .child(PreferenceConnector.readString(this,PreferenceConnector.MY_USER_ID,""))
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
                            Toast.makeText(SignupActivity.this, "Not Store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
