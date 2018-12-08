package com.livewire.ui.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.cropper.CropImage;
import com.livewire.cropper.CropImageView;
import com.livewire.model.UserModel;
import com.livewire.responce.SignUpResponce;
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
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        key = getIntent().getStringExtra("UserTypeKey");
        if (getIntent().getStringExtra("UserTypeKey").equals("client")) {// if client side
            flUserProfile.setVisibility(View.VISIBLE);
            tvTownResidence.setVisibility(View.VISIBLE);
            horizontalLine.setVisibility(View.VISIBLE);
            btnClientSignup.setVisibility(View.VISIBLE);
            btnWorkerSignup.setVisibility(View.GONE);
            flUserProfile.setOnClickListener(this);
            btnClientSignup.setOnClickListener(this);
            tvTownResidence.setOnClickListener(this);
        } else if (getIntent().getStringExtra("UserTypeKey").equals("worker")) {// if worker side
            flUserProfile.setVisibility(View.GONE);
            tvTownResidence.setVisibility(View.GONE);
            horizontalLine.setVisibility(View.GONE);
            btnClientSignup.setVisibility(View.GONE);
            btnWorkerSignup.setVisibility(View.VISIBLE);
            btnWorkerSignup.setOnClickListener(this);
        }

        tvLiveWire.setText(Constant.liveWireText(this));
        ivBack.setOnClickListener(this);
        mainLayout.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        googleLogInBtn.setOnClickListener(this);
        fbLogInBtn.setOnClickListener(this);
        fb_btn.setOnClickListener(this);

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
                if (graphObject.has("email")) {
                    String fb_mail = null;
                    try {
                        if (key.equals("client")) {
                            email = graphObject.getString("email");
                            personName = graphObject.getString("name");
                            String fbId = graphObject.getString("id");
                            if (graphObject.getString("picture") != null) {
                                imageUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            }
                            checkSocialLogin(fbId, "fb");
                        } else {
                            fb_mail = graphObject.getString("email");
                            String fbId = graphObject.getString("id");
                            if (graphObject.getString("picture") != null) {
                                imageUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                ;
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
                        }
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
                            //progressBar1.setVisibility(View.GONE);
                            String status = null;
                            try {
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {

                                    String isSocialLogin = response.getString("socialId");
                                    if (isSocialLogin.equals("0")) {
                                        openLocationDialog(id, socialType);
                                    } else {

                                        UserModel model = new UserModel();
                                        model.name = personName;
                                        model.email = email;
                                        model.userType = key;
                                        model.deviceType = "2";
                                        model.deviceToken = FirebaseInstanceId.getInstance().getToken();
                                        model.socialId = id;
                                        model.socialType = socialType;

                                        signUpApiForSocial(model);
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
                signUpApiForSocial(model);
            }
        });
    }

    //"""""""""""gmail response"""""""//
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            if (key.equals("client")) {
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
            }

        }
    }

    //"""""""""signup with google and fb""""""""""""""""""//
    private void signUpApiForSocial(UserModel model) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            // progressBar.setVisibility(View.VISIBLE);
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
                            setSocialResponceData(userResponce);
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
    private void setSocialResponceData(SignUpResponce userResponce) {
        Log.d("Responce", userResponce.getData().getUserType());
        PreferenceConnector.writeBoolean(SignupActivity.this, PreferenceConnector.IS_LOG_IN, true);
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.SOCIAL_LOGIN, userResponce.getData().getSocialType());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, userResponce.getData().getStripe_customer_id());

        Log.d("Responce", userResponce.toString());
        if (userResponce.getData().getUserType().equals("worker")) {// if user is worker
            Intent intent = null;
            if (userResponce.getData().getCompleteProfile().equals("0")) { // if worker not complete own profile
                finishAffinity();
                intent = new Intent(SignupActivity.this, CompleteProfileActivity.class);
                intent.putExtra("imageKey", imageUrl);
                startActivity(intent);
                finish();
            } else if (userResponce.getData().getCompleteProfile().equals("1")) {// if worker complete own profile
                finishAffinity();
                intent = new Intent(SignupActivity.this, WorkerMainActivity.class);
                startActivity(intent);
                finish();
            }
        } else { // if user is Client
            finishAffinity();
            Intent intent = new Intent(SignupActivity.this, ClientMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
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
                CropImage.activity(tmpUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(200, 200)
                        .setMaxCropResultSize(4000, 4000)
                        .setAspectRatio(300, 300)
                        .start(this);
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
                    CropImage.activity(tmpUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300)
                            .start(this);
                }
            }
        }

        //*********** circle cropping image ********//
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
        }

        //*********Autocomplete place p[icker****************//
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Place place = PlaceAutocomplete.getPlace(this, data);
                tvTownResidence.setText(place.getAddress());
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
            Constant.snackBar(mainLayout, "Please enter FullName");
            etFullName.startAnimation(shake);
        } else if (Validation.isEmpty(etEmail)) {
            Constant.snackBar(mainLayout, "Please enter Email");
            etEmail.startAnimation(shake);
        } else if (etEmail.getText().toString().trim().contains(" ")) {
            etEmail.startAnimation(shake);
            Constant.snackBar(mainLayout, "Email can't hold space");
        } else if (!Validation.isEmailValid(etEmail)) {
            etEmail.startAnimation(shake);
            etEmail.requestFocus();
            Constant.snackBar(mainLayout, "Please enter valid email");
        } else if (tvTownResidence.getText().toString().equals("")) {
            tvTownResidence.startAnimation(shake);
            tvTownResidence.requestFocus();
            Constant.snackBar(mainLayout, "Please enter your location");
        } else if (Validation.isEmpty(etPass)) {
            etPass.startAnimation(shake);
            Constant.snackBar(mainLayout, "Please enter password");
        } else if (etPass.getText().toString().length() < 6) {
            etPass.startAnimation(shake);
            etPass.requestFocus();
            Constant.snackBar(mainLayout, "Password should be 6 character");
        } else {
            UserModel model = new UserModel();
            model.name = etFullName.getText().toString();
            model.email = etEmail.getText().toString();
            model.password = etPass.getText().toString();
            model.latitude = String.valueOf(locationLatLng.latitude);
            model.longitude = String.valueOf(locationLatLng.longitude);
            model.town = locationPlace;
            model.userType = "client";
            model.deviceType = "2";
            model.deviceToken = FirebaseInstanceId.getInstance().getToken();
            signUpClientApi(model);
        }
    }

    //"""""""" signup api client """""""""""//
    private void signUpClientApi(UserModel model) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            //  progressBar.setVisibility(View.VISIBLE);
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
                            Log.e("sign up response", userResponce.getData().toString());
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
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.PASS_WORD, etPass.getText().toString());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, userResponce.getData().getStripe_customer_id());

                            /*PreferenceConnector.writeBoolean(SignupActivity.this, PreferenceConnector.IS_LOG_IN, true);
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_INFO_JSON, response.toString());
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
                        */
        Intent intent = new Intent(SignupActivity.this, ClientMainActivity.class);
        startActivity(intent);
        finish();
    }

    /*******Auto comple place picker***************/
    private void autoCompletePlacePicker() {
        try {
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
            Constant.snackBar(mainLayout, "Please enter FullName");
            etFullName.startAnimation(shake);
        } else if (Validation.isEmpty(etEmail)) {
            Constant.snackBar(mainLayout, "Please enter Email");
            etEmail.startAnimation(shake);
        } else if (etEmail.getText().toString().trim().contains(" ")) {
            etEmail.startAnimation(shake);
            Constant.snackBar(mainLayout, "Email can't hold space");
        } else if (!Validation.isEmailValid(etEmail)) {
            etEmail.startAnimation(shake);
            etEmail.requestFocus();
            Constant.snackBar(mainLayout, "Please enter valid email");
        } else if (Validation.isEmpty(etPass)) {
            etPass.startAnimation(shake);
            Constant.snackBar(mainLayout, "Please enter password");
        } else if (etPass.getText().toString().length() < 6) {
            etPass.startAnimation(shake);
            etPass.requestFocus();
            Constant.snackBar(mainLayout, "Password should be 6 character");
        } else {
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
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.PASS_WORD, etPass.getText().toString());
        PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.STRIPE_CUSTOMER_ID, userResponce.getData().getStripe_customer_id());
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
}
