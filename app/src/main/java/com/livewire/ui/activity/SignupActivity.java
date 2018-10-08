package com.livewire.ui.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.cropper.CropImage;
import com.livewire.cropper.CropImageView;
import com.livewire.model.UserModel;
import com.livewire.responce.SignUpResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.ImageRotator;
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


public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        AndroidNetworking.initialize(getApplicationContext());
        intializeView();
    }


    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

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

        if (getIntent().getStringExtra("UserTypeKey").equals("client")) {
            flUserProfile.setVisibility(View.VISIBLE);
            tvTownResidence.setVisibility(View.VISIBLE);
            horizontalLine.setVisibility(View.VISIBLE);
            btnClientSignup.setVisibility(View.VISIBLE);
            btnWorkerSignup.setVisibility(View.GONE);
            flUserProfile.setOnClickListener(this);
            btnClientSignup.setOnClickListener(this);
            tvTownResidence.setOnClickListener(this);
        } else if (getIntent().getStringExtra("UserTypeKey").equals("worker")) {
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
            default:

        }
    }

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
    }

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
            model.deviceToken = "";
            signUpClientApi(model);
        }
    }

    private void signUpClientApi(UserModel model) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            //  progressBar.setVisibility(View.VISIBLE);
            AndroidNetworking.upload(BASE_URL+"userRegistration")
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

                            PreferenceConnector.writeBoolean(SignupActivity.this, PreferenceConnector.IS_LOG_IN, true);
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_INFO_JSON, response.toString());
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                            /*PreferenceConnector.writeBoolean(SignupActivity.this, PreferenceConnector.IS_LOG_IN, true);
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_INFO_JSON, response.toString());
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
                        */
                            Intent intent = new Intent(SignupActivity.this, ClientMainActivity.class);
                            startActivity(intent);
                            finish();
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
            model.deviceToken = "";
            signUpWorkerApi(model);
        }
    }

    private void signUpWorkerApi(UserModel model) {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            // progressBar.setVisibility(View.VISIBLE);
            AndroidNetworking.post(BASE_URL+"userRegistration")
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
                            Log.e("sign up response", userResponce.getData().toString());
                            PreferenceConnector.writeBoolean(SignupActivity.this, PreferenceConnector.IS_LOG_IN, true);
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_INFO_JSON, response.toString());
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.AUTH_TOKEN, userResponce.getData().getAuthToken());
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, userResponce.getData().getCompleteProfile());
                            PreferenceConnector.writeString(SignupActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                            Intent intent = new Intent(SignupActivity.this, CompleteProfileActivity.class);
                            startActivity(intent);
                            finish();

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
}
