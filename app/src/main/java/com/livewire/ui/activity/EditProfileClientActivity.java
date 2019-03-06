package com.livewire.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import com.livewire.cropper.CropImage;
import com.livewire.cropper.CropImageView;

import com.livewire.databinding.ActivityEditProfileClientBinding;
import com.livewire.model.UserInfoFcm;
import com.livewire.model.UserModel;
import com.livewire.responce.SignUpResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.ImageRotator;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.GET_CLIENT_PROFILE_API;
import static com.livewire.utils.ApiCollection.UPDATE_CLIENT_PROFILE_API;

public class EditProfileClientActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityEditProfileClientBinding binding;
    private static final String TAG = EditProfileClientActivity.class.getName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String locationPlace = "";
    private Uri tmpUri;
    private LatLng locationLatLng;
    private Bitmap profileImageBitmap;
    private ProgressDialog progressDialog;
    private double locationLat;
    private double locationLng;
    private File userImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile_client);
        intializeView();

        if (getIntent().getSerializableExtra("ClientProfileInfo") != null) {
            SignUpResponce userResponce = (SignUpResponce) getIntent().getSerializableExtra("ClientProfileInfo");
            binding.setUserInfo(userResponce.getData());

            if (!userResponce.getData().getLatitude().isEmpty()) {
                locationLat = Double.parseDouble(userResponce.getData().getLatitude());
                locationLng = Double.parseDouble(userResponce.getData().getLongitude());
            }

            locationPlace = userResponce.getData().getTown();
            Picasso.with(binding.ivProfileImg.getContext())
                    .load(userResponce.getData().getProfileImage())
                    .into(binding.ivProfileImg);
            binding.inactiveUserImg.setVisibility(View.GONE);
        }else {
            myProfileApi();
        }


    }

        //"""""""""' my profile worker side""""""""""""""//
        private void myProfileApi() {// help offer api calling
            if (Constant.isNetworkAvailable(this, binding.editProfileLayout)) {
                progressDialog.show();
                AndroidNetworking.get(BASE_URL + GET_CLIENT_PROFILE_API)
                        .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    progressDialog.dismiss();
                                    String status = response.getString("status");
                                    String message = response.getString("message");
                                    if (status.equals("success")) {
                                        SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);

                                        Picasso.with(binding.ivProfileImg.getContext()).load(userResponce.getData().getProfileImage())
                                                .fit().into(binding.ivProfileImg);
                                       binding.inactiveUserImg.setVisibility(View.GONE);
                                        binding.setUserInfo(userResponce.getData());

                                    } else {
                                        Constant.snackBar(binding.editProfileLayout, message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Constant.errorHandle(anError, EditProfileClientActivity.this);
                                progressDialog.dismiss();
                            }
                        });

        }
    }

    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        binding.actionBarWorker.ivBack.setOnClickListener(this);
        binding.actionBarWorker.tvLiveWire.setText(R.string.edit_profile);
        binding.btnSaveAndUpdate.setOnClickListener(this);
        binding.flUserProfile.setOnClickListener(this);
        binding.tvTownResident.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_save_and_update:
                etClientValdidations();
                break;
            case R.id.fl_user_profile:
                showSetProfileImageDialog();
                break;
            case R.id.tv_town_resident:
                autoCompletePlacePicker();
            default:
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

    //"""""" open image dialog """"""""""""//
    private void showSetProfileImageDialog() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileClientActivity.this);

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
                            tmpUri = FileProvider.getUriForFile(EditProfileClientActivity.this,
                                    getApplicationContext().getPackageName() + ".fileprovider",
                                    getTemporalFile(EditProfileClientActivity.this));
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
                    binding.inactiveUserImg.setVisibility(View.GONE);
                    binding.ivProfileImg.setImageURI(tmpUri);
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
                binding.tvTownResident.setText(place.getAddress());
                locationPlace = place.getAddress().toString();
                locationLatLng = place.getLatLng();
                locationLat = place.getLatLng().latitude;
                locationLng = place.getLatLng().longitude;
                Log.e(TAG, "Place: " + place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status1 = PlaceAutocomplete.getStatus(this, data);

                Log.i(TAG, status1.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {

                Constant.hideSoftKeyBoard(EditProfileClientActivity.this, binding.etEmail1);
            }
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

    private void etClientValdidations() {
        if (Validation.isEmpty(binding.etFullName)) {
            Constant.snackBar(binding.editProfileLayout, "Please enter FullName");
            // binding.etFullName.startAnimation(shake);
        } else if (Validation.isEmpty(binding.etEmail1)) {
            Constant.snackBar(binding.editProfileLayout, "Please enter Email");
            // binding.etEmail1.startAnimation(shake);
        } else if (binding.etEmail1.getText().toString().trim().contains(" ")) {
            // binding.etEmail1.startAnimation(shake);
            Constant.snackBar(binding.editProfileLayout, "Email can't hold space");
        } else if (!Validation.isEmailValid(binding.etEmail1)) {
            //  binding.etEmail1.startAnimation(shake);
            binding.etEmail1.requestFocus();
            Constant.snackBar(binding.editProfileLayout, "Please enter valid email");
        } else if (binding.tvTownResident.getText().toString().equals("")) {
            //  binding.tvTownResident.startAnimation(shake);
            binding.tvTownResident.requestFocus();
            Constant.snackBar(binding.editProfileLayout, "Please enter your location");
        } else {
            UserModel model = new UserModel();
            model.name = binding.etFullName.getText().toString();
            model.email = binding.etEmail1.getText().toString();
            model.latitude = String.valueOf(locationLat);
            model.longitude = String.valueOf(locationLng);
            model.town = locationPlace;
            updateProfileInfoApi(model);
        }
    }

    private void updateProfileInfoApi(UserModel model) {
        if (Constant.isNetworkAvailable(this, binding.editProfileLayout)) {
            progressDialog.show();
            // progressBar.setVisibility(View.VISIBLE);
            AndroidNetworking.upload(BASE_URL + UPDATE_CLIENT_PROFILE_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addMultipartFile("profileImage", userImageFile)
                    .addMultipartParameter(model)
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

                            PreferenceConnector.writeString(EditProfileClientActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                            PreferenceConnector.writeString(EditProfileClientActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                            PreferenceConnector.writeString(EditProfileClientActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getThumbImage());
                            PreferenceConnector.writeString(EditProfileClientActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                            PreferenceConnector.writeString(EditProfileClientActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
                            addUserFirebaseDatabase();
                            finish();
                            // setSignUpWorkerdata(userResponce);

                        } else {
                            Constant.snackBar(binding.editProfileLayout, message);
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

    private void addUserFirebaseDatabase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Log.e(TAG, database.toString());
        UserInfoFcm infoFcm = new UserInfoFcm();
        infoFcm.email = PreferenceConnector.readString(this, PreferenceConnector.Email, "");
        infoFcm.firebaseToken = FirebaseInstanceId.getInstance().getToken();
        infoFcm.name = PreferenceConnector.readString(this, PreferenceConnector.Name, "");
        infoFcm.notificationStatus = "";
        infoFcm.profilePic = PreferenceConnector.readString(this, PreferenceConnector.PROFILE_IMG, "");
        infoFcm.uid = PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, "");
        infoFcm.userType = PreferenceConnector.readString(this, PreferenceConnector.USER_TYPE, "");
        infoFcm.authToken = PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, "");

        database.child(Constant.ARG_USERS)
                .child(PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, ""))
                .setValue(infoFcm)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                          /*  //Utils.goToOnlineStatus(SignInActivity.this, Constant.online);*/

                        } else {
                            Toast.makeText(EditProfileClientActivity.this, "Not Store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
