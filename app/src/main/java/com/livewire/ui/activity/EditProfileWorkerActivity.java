package com.livewire.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.iceteck.silicompressorr.SiliCompressor;
import com.livewire.R;
import com.livewire.adapter.EditCategaryAdapter;
import com.livewire.cropper.CropImage;
import com.livewire.databinding.ActivityEditProfileWorkerBinding;
import com.livewire.model.AddedSkillBean;
import com.livewire.model.CategoryModel;
import com.livewire.model.IntroVideoModal;
import com.livewire.model.SubCategoryModel;
import com.livewire.model.UserInfoFcm;
import com.livewire.multiple_file_upload.MultiPartRequestForUpdateProfile;
import com.livewire.multiple_file_upload.Template;
import com.livewire.multiple_file_upload.VolleyMySingleton;
import com.livewire.responce.AddSkillsResponce;
import com.livewire.responce.MyProfileResponce;
import com.livewire.responce.SignUpResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.ImageRotator;
import com.livewire.utils.ImageVideoUtils;
import com.livewire.utils.PermissionAll;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.DELETE_VIDEO_API;
import static com.livewire.utils.ApiCollection.GET_CATEGORY_LIST_API;
import static com.livewire.utils.ApiCollection.GET_MY_PROFILE_API;
import static com.livewire.utils.Constant.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.livewire.utils.Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.livewire.utils.Constant.RECORD_AUDIO;

public class EditProfileWorkerActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener {
    ActivityEditProfileWorkerBinding binding;
    private static final String TAG = EditProfileWorkerActivity.class.getName();
    private Uri tmpUri;
    private int width;
    private List<CategoryModel> category = new ArrayList<>();
    private AddSkillsResponce skillsResponce;
    private ArrayAdapter categoryAdapter;
    private Spinner subCategorySpinner;
    private ArrayAdapter<AddSkillsResponce.DataBean.SubcatBean> subCateoryAdapter;

    private ArrayList<AddedSkillBean> addedSkillBeans = new ArrayList<>();
    private ArrayList<SubCategoryModel> subCategoryModelList = new ArrayList<>();
    private EditCategaryAdapter addSkillsAdapter;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String locationPlace;
    private Notification.Builder mBuilder;
    private ProgressBar progressbar;
    private TextView tv_for_videoP;
    private NotificationManager mNotifyManager;
    private static int ID = 100;
    private LatLng locationLatLng;
    private String placeLatitude;
    private String placeLongitude;
    private File profileImagefile;

    private ArrayList<File> videoFile;
    private HashMap<String, String> mPram;
    private ProgressDialog progressDialog;
    private ArrayList<File> profileImageFileList;
    private File videoThumbFile;
    private List<File> videoThumbFileList;
    private Uri finalVideoUri;
    private String videoUrl;
    private String finalVideoFilePath;
    private File imageFile;
    private ImageView removeVideoImg;
    private boolean isvideoUrl = false;
    private MyProfileResponce userResponce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile_worker);

        // progressDialog = new ProgressDialog(this);
        //  removeVideoImg = findViewById(R.id.iv_remove_video);
        // Log.e("Auth token", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""));

        PermissionAll permissionAll = new PermissionAll();
        permissionAll.checkWriteStoragePermission(this);

        intializeView();
        loadSkillsData();
        if (getIntent().getStringExtra("EditProfileKey") != null) { // if user come from profile setting
            MyProfileResponce userData = (MyProfileResponce) getIntent().getSerializableExtra("CategoryListKey");
            // ArrayList<CategoryBean> categoryList = (ArrayList<CategoryBean>) getIntent().getSerializableExtra("CategoryListKey");

            // MyProfileResponce.DataBean userData = (MyProfileResponce.DataBean) getIntent().getSerializableExtra("MyProfileKey");

            /*for (MyProfileResponce.DataBean.CategoryBean categoryBean : userData.getData().getCategory()) {
                AddedSkillBean addedCatagory = new AddedSkillBean();
                addedCatagory.setName(categoryBean.getCategoryName());
                addedCatagory.setCatageryId(categoryBean.getParentCategoryId());
                addedCatagory.setVisible(true);
                if (!categoryBean.getSubcat().isEmpty()) {
                    addedCatagory.setSubCatagories(new ArrayList<AddedSkillBean.SubCatagory>());
                }
                for (int i = 0; i < categoryBean.getSubcat().size(); i++) {
                    AddedSkillBean.SubCatagory subcatBean = new AddedSkillBean.SubCatagory();
                    subcatBean.setSubName(categoryBean.getSubcat().get(i).getCategoryName());
                    //subcatBean.(categoryBean.getSubcat().get(i).getParent_id());
                    subcatBean.setSubCatId(categoryBean.getSubcat().get(i).getCategoryId());
                    subcatBean.setMin_rate(Float.parseFloat(categoryBean.getSubcat().get(i).getMin_rate()));
                    subcatBean.setMax_rate(Float.parseFloat(categoryBean.getSubcat().get(i).getMax_rate()));


                    addedCatagory.getSubCatagories().add(subcatBean);

                }
                addedSkillBeans.add(addedCatagory);
            }

         *//*   binding.etFullName.setText(userData.getData().getName());
            binding.etEmail1.setText(userData.getData().getEmail());
            binding.tvLocation.setText(userData.getData().getTown());*//*
            binding.setUserData(userData.getData());

            locationPlace = userData.getData().getTown();

            placeLatitude = userData.getData().getLatitude();
            placeLongitude = userData.getData().getLongitude();

           *//* if (!userData.getData().getIntro_video().isEmpty() || userData.getData().getIntro_video() != null ){
                finalVideoUri = Uri.parse(userData.getData().getIntro_video());
                isvideoUrl = true;
            }*//*

            if (!userData.getData().getIntro_video().isEmpty()) {
                binding.ivRemoveVideo.setVisibility(View.VISIBLE);
                videoUrl = userData.getData().getIntro_video();


                Picasso.with(binding.videoImg.getContext()).load(userData.getData().getVideo_thumb())
                        .fit().into(binding.videoImg);
                isvideoUrl = true;
            } else {
                binding.videoImg.setClickable(true);
                isvideoUrl = false;
                binding.ivRemoveVideo.setVisibility(View.GONE);
                //  binding.videoImg.setImageDrawable(ContextCompat.getDrawable(this, R.color.colorDarkBlack));
            }
            if (!userData.getData().getProfileImage().isEmpty()) {
                Picasso.with(binding.ivProfileImg.getContext())
                        .load(userData.getData().getProfileImage())
                        .fit().into(binding.ivProfileImg);
            }

            binding.etDescription.setText(userData.getData().getIntro_discription());
            binding.inactiveUserImg.setVisibility(View.GONE);


            binding.llNameEmail.setVisibility(View.VISIBLE);
            binding.btnSaveAndUpdate.setVisibility(View.VISIBLE);
            binding.ivRemoveVideo.setOnClickListener(this);
            binding.btnSaveAndUpdate.setOnClickListener(this);
            binding.videoImg.setClickable(false);
*/

            if (userData != null)
            setUserData(userData);


        } else {
            myProfileApi();
        }


        binding.etDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});

        binding.etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = binding.etDescription.getText().toString();
                if (str.length() >= 200) {
                    Toast.makeText(EditProfileWorkerActivity.this, "intro description should not more then 200 characters", Toast.LENGTH_SHORT).show();
                } else {
                    //   Toast.makeText(EditProfileWorkerActivity.this, "max lenght is 200", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    //"""""""""' my profile worker side""""""""""""""//
    private void myProfileApi() {// help offer api calling
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + GET_MY_PROFILE_API)
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
                                    userResponce = new Gson().fromJson(String.valueOf(response), MyProfileResponce.class);
                                    videoUrl = userResponce.getData().getIntro_video();

                                  /*  if (!userResponce.getData().getVideo_thumb().isEmpty()) {
                                        Picasso.with(binding.videoImg.getContext()).load(userResponce.getData().getVideo_thumb()).fit()
                                                .error(R.color.colorWhite)
                                                .into(binding.videoImg);
                                    }


                                   *//* showSkillBeans.clear();
                                    showSkillBeans.addAll(userResponce.getData().getCategory());
                                    showSkillsAdapter.notifyDataSetChanged();*//*

                                    if (!userResponce.getData().getProfileImage().isEmpty()) {
                                        Picasso.with(binding.ivProfileImg.getContext())
                                                .load(userResponce.getData().getProfileImage())
                                                .fit().into(binding.ivProfileImg);
                                    }
                                    binding.inactiveUserImg.setVisibility(View.GONE);*/
                                    binding.setUserData(userResponce.getData());
                                    setUserData(userResponce);
                                } else {
                                    Constant.snackBar(binding.mainLayout, message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("getErrorBody: ", anError.getErrorBody());
                            Constant.errorHandle(anError, EditProfileWorkerActivity.this);
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void setUserData(MyProfileResponce userData) {
        // MyProfileResponce.DataBean userData = (MyProfileResponce.DataBean) getIntent().getSerializableExtra("MyProfileKey");
        for (MyProfileResponce.DataBean.CategoryBean categoryBean : userData.getData().getCategory()) {
            AddedSkillBean addedCatagory = new AddedSkillBean();
            addedCatagory.setName(categoryBean.getCategoryName());
            addedCatagory.setCatageryId(categoryBean.getParentCategoryId());
            addedCatagory.setVisible(true);
            if (!categoryBean.getSubcat().isEmpty()) {
                addedCatagory.setSubCatagories(new ArrayList<AddedSkillBean.SubCatagory>());
            }
            for (int i = 0; i < categoryBean.getSubcat().size(); i++) {
                AddedSkillBean.SubCatagory subcatBean = new AddedSkillBean.SubCatagory();
                subcatBean.setSubName(categoryBean.getSubcat().get(i).getCategoryName());
                //subcatBean.(categoryBean.getSubcat().get(i).getParent_id());
                subcatBean.setSubCatId(categoryBean.getSubcat().get(i).getCategoryId());
                subcatBean.setMin_rate(Float.parseFloat(categoryBean.getSubcat().get(i).getMin_rate()));
                subcatBean.setMax_rate(Float.parseFloat(categoryBean.getSubcat().get(i).getMax_rate()));
                addedCatagory.getSubCatagories().add(subcatBean);
            }
            addedSkillBeans.add(addedCatagory);
        }
         /*   binding.etFullName.setText(userData.getData().getName());
            binding.etEmail1.setText(userData.getData().getEmail());
            binding.tvLocation.setText(userData.getData().getTown());*/
        binding.setUserData(userData.getData());

        locationPlace = userData.getData().getProfile_address();

        placeLatitude = userData.getData().getLatitude();
        placeLongitude = userData.getData().getLongitude();

        //"""""""" editable edit text """""""""""//
        /*if (!PreferenceConnector.readString(this,PreferenceConnector.SOCIAL_LOGIN,"").isEmpty()){
            binding.etEmail1.setText(userData.getData().getEmail());
            binding.etEmail1.setEnabled(false);
        }else binding.etEmail1.setText(userData.getData().getEmail());*/


           /* if (!userData.getData().getIntro_video().isEmpty() || userData.getData().getIntro_video() != null ){
                finalVideoUri = Uri.parse(userData.getData().getIntro_video());
                isvideoUrl = true;
            }*/

        if (!userData.getData().getIntro_video().isEmpty()) {
            binding.ivRemoveVideo.setVisibility(View.VISIBLE);
            videoUrl = userData.getData().getIntro_video();

            Picasso.with(binding.videoImg.getContext()).load(userData.getData().getVideo_thumb())
                    .fit().into(binding.videoImg);
            isvideoUrl = true;
        } else {
            binding.videoImg.setClickable(true);
            isvideoUrl = false;
            binding.ivRemoveVideo.setVisibility(View.GONE);
            //  binding.videoImg.setImageDrawable(ContextCompat.getDrawable(this, R.color.colorDarkBlack));
        }
        if (!userData.getData().getProfileImage().isEmpty()) {
            Picasso.with(binding.ivProfileImg.getContext())
                    .load(userData.getData().getProfileImage())
                    .fit().into(binding.ivProfileImg);
        }

        binding.etDescription.setText(userData.getData().getIntro_discription());
        binding.inactiveUserImg.setVisibility(View.GONE);

        if (!PreferenceConnector.readString(this, PreferenceConnector.SOCIAL_LOGIN, "").isEmpty()) {
            binding.etEmail1.setText(userData.getData().getEmail());
            binding.etEmail1.setEnabled(false);
        } else binding.etEmail1.setText(userData.getData().getEmail());

        binding.llNameEmail.setVisibility(View.VISIBLE);
        binding.btnSaveAndUpdate.setVisibility(View.VISIBLE);
        binding.ivRemoveVideo.setOnClickListener(this);
        binding.btnSaveAndUpdate.setOnClickListener(this);
        //binding.videoImg.setClickable(false);
        addSkillsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //"""""""""""" skills data """""""""""""""""""""//
    private void loadSkillsData() {
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + GET_CATEGORY_LIST_API)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            skillsResponce = new Gson().fromJson(String.valueOf(response), AddSkillsResponce.class);

                            for (AddSkillsResponce.DataBean dataBean : skillsResponce.getData()) {
                                CategoryModel categoryModel = new CategoryModel();
                                categoryModel.setCategoryId(dataBean.getCategoryId());
                                categoryModel.setCategoryName(dataBean.getCategoryName());
                                category.add(categoryModel);
                            }

                            for (AddSkillsResponce.DataBean dataBean : skillsResponce.getData()) {
                                AddSkillsResponce.DataBean.SubcatBean subcatBean = new AddSkillsResponce.DataBean.SubcatBean();
                                subcatBean.setCategoryName("Select Subcategory");
                                dataBean.getSubcat().add(0, subcatBean);
                            }
                            AddSkillsResponce.DataBean dataBean = new AddSkillsResponce.DataBean();
                            AddSkillsResponce.DataBean.SubcatBean subcatBean = new AddSkillsResponce.DataBean.SubcatBean();
                            subcatBean.setCategoryName("Select Subcategory");
                            dataBean.setCategoryName("Select Category");
                            dataBean.setSubcat(new ArrayList<AddSkillsResponce.DataBean.SubcatBean>());
                            dataBean.getSubcat().add(subcatBean);
                            skillsResponce.getData().add(0, dataBean);
                        } else {
                            Constant.snackBar(binding.mainLayout, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    //""""""""""""""  intialize view """""""""""
    private void intializeView() {
        //""""""" all permission """"""""""""
        PermissionAll permissionAll = new PermissionAll();
        permissionAll.RequestMultiplePermission(EditProfileWorkerActivity.this);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;


        progressDialog = new ProgressDialog(this);


        binding.actionBar2.ivBack.setOnClickListener(this);
        binding.actionBar2.tvLiveWire.setText(R.string.edit_profile);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        addSkillsAdapter = new EditCategaryAdapter(this, addedSkillBeans);
        binding.recyclerView.setAdapter(addSkillsAdapter);


        binding.tvSkip.setOnClickListener(this);
        binding.tvLocation.setOnClickListener(this);

        binding.addSkillsRl.setOnClickListener(this);
        binding.flUserProfile.setOnClickListener(this);
        binding.videoImg.setOnClickListener(this);


        if (getIntent().hasExtra("imageKey")) {
            if (getIntent().getStringExtra("imageKey") != null) {
                binding.inactiveUserImg.setVisibility(View.GONE);
                Picasso.with(binding.ivProfileImg.getContext()).load(getIntent().getStringExtra("imageKey")).placeholder(R.drawable.ic_user).fit().into(binding.ivProfileImg);
            }
        }

    }

    @Override
    public void onClick(View v) {
        PermissionAll permissionAll = new PermissionAll();
        switch (v.getId()) {
            case R.id.fl_user_profile:
                if (permissionAll.RequestMultiplePermission(EditProfileWorkerActivity.this)) //camera permission
                    showSetProfileImageDialog();
                break;
            case R.id.add_skills_rl:
                showAddSkillsDialog();
                break;
            case R.id.video_img:
                if (finalVideoUri != null) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse(finalVideoFilePath);
                    i.setDataAndType(data, "video/*");
                    i.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
                    startActivity(i);
                } else if (videoUrl != null) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.parse(videoUrl), "video/mp4");
                    startActivity(i);
                } else showSetIntroVideoDialog();
                break;
            case R.id.tv_location:
                autoCompletePlacePicker();
                break;
            case R.id.tv_skip:
                Intent intent1 = new Intent(EditProfileWorkerActivity.this, WorkerMainActivity.class);
                startActivity(intent1);
                finish();
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_save_and_update:
                Log.e("SubCategory: ", getWorkerSkillData());
                updateProfileValidations();
                break;
            case R.id.iv_remove_video:
                if (isvideoUrl) {
                    deleteDialog();

                } else {

                    binding.ivRemoveVideo.setVisibility(View.GONE);
                    binding.videoImg.setClickable(true);
                    finalVideoUri = null;
                    videoUrl = null;
                    isvideoUrl = false;
                    binding.videoImg.setImageDrawable(ContextCompat.getDrawable(this, R.color.colorWhite));
                    binding.videoImg.setOnClickListener(this);
                }
                break;

            default:
        }
    }

    private void deleteDialog() {
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);
        builder1.setTitle("Alert");
        builder1.setMessage("Are you sure you want to delete intro video?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        removeVideo();
                        dialog.cancel();

                    }
                });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        android.app.AlertDialog alert11 = builder1.create();
        alert11.show();


    }


    //"""""""""""Complete profile validations """"""""""//
    private void updateProfileValidations() {
        if (Validation.isEmpty(binding.etFullName)) {
            Constant.snackBar(binding.mainLayout, "please enter your Name");
        } else if (Validation.isEmpty(binding.etEmail1)) {
            Constant.snackBar(binding.mainLayout, "please enter Email Id");
        } else if (!Validation.isEmailValid(binding.etEmail1)) {
            Constant.snackBar(binding.mainLayout, "please enter valid Email Id");
        } else if (subCategoryModelList.size() == 0) {
            Constant.snackBar(binding.mainLayout, "please add your skills");
        } else if (placeLongitude == null) {
            Constant.snackBar(binding.mainLayout, "please enter loation");
        } /*else if (finalVideoUri == null) {
            Constant.snackBar(binding.mainLayout, "please add introvideo");
        }*/ else {
            profileImageFileList = new ArrayList<>();
            videoThumbFileList = new ArrayList<>();

            if (videoThumbFile != null) {
                videoThumbFileList.add(videoThumbFile);
            }

            if (imageFile != null) {
                profileImageFileList.add(imageFile);
            }
            mPram = new HashMap<>();
            mPram.put("workerSkillData", getWorkerSkillData());
            mPram.put("latitude", placeLatitude);
            mPram.put("longitude", placeLongitude);
            mPram.put("town", locationPlace);
            mPram.put("name", binding.etFullName.getText().toString().trim());
            mPram.put("email", binding.etEmail1.getText().toString().trim());
            mPram.put("intro_discription", binding.etDescription.getText().toString().trim());

            if (isvideoUrl || finalVideoUri == null) {
                mPram.put(Template.Query.INTRO_VIDEO_KEY, "");
                HashMap<String ,File> map = new HashMap<>();
                map.put("profileImage",imageFile);
                apiCallForUpdateUserData(map);

            } else {
                uploadVideo();
            }

        }

    }



    //"""""""""show dialog for add skills""""""""""""""""//
    private void showAddSkillsDialog() {
        if (Constant.isNetworkAvailable(this, binding.mainLayout) && skillsResponce != null) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.add_skills_dialog);
            dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            final RelativeLayout addSkillsLayout = dialog.findViewById(R.id.add_skills_layout);
            final EditText etMinPrice = dialog.findViewById(R.id.et_min_price);
            final EditText etMaxPrice = dialog.findViewById(R.id.et_max_price);
            Button btnAddSkills = dialog.findViewById(R.id.btn_add_skills);
            final Spinner categorySpinner = dialog.findViewById(R.id.category_spinner);
            subCategorySpinner = dialog.findViewById(R.id.sub_category_spinner);

            categoryAdapter = new ArrayAdapter<>(EditProfileWorkerActivity.this, R.layout.spinner_item, skillsResponce.getData());
            categoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
            categorySpinner.setOnItemSelectedListener(this);
            categorySpinner.setAdapter(categoryAdapter);
            Constant.hideSoftKeyBoard(EditProfileWorkerActivity.this, etMaxPrice);
            btnAddSkills.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogValidations(categorySpinner, etMinPrice, etMaxPrice, addSkillsLayout, dialog);
                    Constant.hideSoftKeyBoard(EditProfileWorkerActivity.this, etMaxPrice);

                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.hideSoftKeyBoard(EditProfileWorkerActivity.this, etMaxPrice);
                    dialog.dismiss();
                }
            });

            etMaxPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String str = etMaxPrice.getText().toString();
                    if (str.isEmpty()) return;
                    String str2 = perfectDecimal(str, 6, 2);
                    if (!str2.equals(str)) {
                        etMaxPrice.setText(str2);
                        int pos = etMaxPrice.getText().length();
                        etMaxPrice.setSelection(pos);
                    }
                }
            });
            etMinPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String str = etMinPrice.getText().toString();
                    if (str.isEmpty()) return;
                    String str2 = perfectDecimal(str, 6, 2);
                    if (!str2.equals(str)) {
                        etMinPrice.setText(str2);
                        int pos = etMinPrice.getText().length();
                        etMinPrice.setSelection(pos);
                    }
                }
            });

            dialog.setCancelable(false);
            dialog.show();
        } else loadSkillsData();
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


    ///"""""""""""" dialog validationd""""""""""""
    private void dialogValidations(Spinner categorySpinner, TextView minPrice, TextView maxPrice, RelativeLayout addSkillsLayout, Dialog dialog) {
/*        int minPric = Integer.parseInt(minPrice.getText().toString());
        int maxPric = Integer.parseInt(maxPrice.getText().toString());*/
        if (skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getCategoryName().equals("Select Category")) {
            Constant.snackBar(addSkillsLayout, "Please Select Category");
        } else if (skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition()).getCategoryName().equals("Select Subcategory")) {
            Constant.snackBar(addSkillsLayout, "Please Select Subcategory");
        } else if (minPrice.getText().toString().equals("") || minPrice.getText().toString().length() == 0) {
            Constant.snackBar(addSkillsLayout, "Please enter min price");
            minPrice.requestFocus();
        } else if (Float.parseFloat(minPrice.getText().toString()) < 3) {
            Constant.snackBar(addSkillsLayout, "Min price should not be less than 3 dollar");
            minPrice.requestFocus();
        } else if (maxPrice.getText().toString().equals("") || maxPrice.getText().toString().length() == 0) {
            Constant.snackBar(addSkillsLayout, "Please enter max price");
            maxPrice.requestFocus();
        }/*else if ((maxPric-minPric) < 0){
            Constant.snackBar(addSkillsLayout, "Min price and Max price can't be same");
            maxPrice.requestFocus();
        }*/ else if (Float.parseFloat(minPrice.getText().toString()) >= Float.parseFloat(maxPrice.getText().toString())) {
            Constant.snackBar(addSkillsLayout, "Min price always less than Max price");
            minPrice.requestFocus();
        } else {
            Constant.hideSoftKeyBoard(this, minPrice);
            AddSkillsResponce.DataBean dataBean = skillsResponce.getData().get(categorySpinner.getSelectedItemPosition());
            AddedSkillBean addedSkillBean = new AddedSkillBean();
            addedSkillBean.setCatageryId(dataBean.getCategoryId());
            addedSkillBean.setName(dataBean.getCategoryName());

            AddSkillsResponce.DataBean.SubcatBean subcatBean = skillsResponce.getData().get(categorySpinner.getSelectedItemPosition()).getSubcat().get(subCategorySpinner.getSelectedItemPosition());
            AddedSkillBean.SubCatagory subCatagory = new AddedSkillBean.SubCatagory();
            subCatagory.setSubCatId(subcatBean.getCategoryId());
            subCatagory.setSubName(subcatBean.getCategoryName());
            subCatagory.setMax_rate(Float.parseFloat(maxPrice.getText().toString()));
            subCatagory.setMin_rate(Float.parseFloat(minPrice.getText().toString()));

            boolean isExsist = false;
            for (AddedSkillBean bean : addedSkillBeans) {
                if (bean.getCatageryId().equals(addedSkillBean.getCatageryId())) {
                    for (AddedSkillBean.SubCatagory subCatagory1 : bean.getSubCatagories()) {
                        if (subCatagory1.getSubCatId().equals(subCatagory.getSubCatId())) {
                            Toast.makeText(EditProfileWorkerActivity.this, "This subcategory is already added", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    bean.getSubCatagories().add(subCatagory);
                    isExsist = true;
                    break;
                }
            }
            if (!isExsist) {
                addedSkillBean.setSubCatagories(new ArrayList<AddedSkillBean.SubCatagory>());
                addedSkillBean.getSubCatagories().add(subCatagory);
                addedSkillBeans.add(addedSkillBean);
            }

            String json = new Gson().toJson(addedSkillBeans);
            addSkillsAdapter.notifyDataSetChanged();
            Log.e("json: ", json);
            dialog.dismiss();
            // uploadVideo();
        }

    }

    //"""""""""" upload video  """"""""""""""//
    @SuppressLint("StaticFieldLeak")
    private void uploadVideo() {

        //videoDialog();
        progressDialog.show();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                File file = null;

                // if(isvideoUrl){
                file = new File(finalVideoFilePath);
                // }
                // Get length of file in bytes
                long fileSizeInBytes = file.length();
                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                long fileSizeInKB = fileSizeInBytes / 1024;
                // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                long fileSizeInMB = fileSizeInKB / 1024;

                if (fileSizeInMB > 10) {
                    compressVideo(finalVideoUri, file);
                } else {
                    videoFile = new ArrayList<>();
                    videoFile.add(file);
                    // if user come from edit profile
                    HashMap<String ,File> map = new HashMap<>();
                    map.put("intro_video", videoFile.get(0));
                    map.put("video_thumb", videoThumbFileList.get(0));
                    map.put("profileImage",imageFile);
                    apiCallForUpdateProfile(map);
                }
                return null;
            }
        }.execute();

    }

    //""""""" show progress """""""""""""""//
    private void videoDialog() {






       /* pDialog = new Dialog(CompleteProfileActivity.this);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.setCancelable(false);
        pDialog.setContentView(R.layout.progress_bar_video);
        progressbar = pDialog.findViewById(R.id.progressbar);
        tv_for_videoP = pDialog.findViewById(R.id.tv_for_videoP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            progressbar.setMin(1);
        }
        progressbar.setMax(100);

        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.show();*/
    }

    //"""""""""""""" Profile Image Dialog """"""""""""""//
    private void showSetProfileImageDialog() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileWorkerActivity.this);

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
                            tmpUri = FileProvider.getUriForFile(EditProfileWorkerActivity.this,
                                    getApplicationContext().getPackageName() + ".fileprovider",
                                    getTemporalFile(EditProfileWorkerActivity.this));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    private File getTemporalFile(Context context) {
        return new File(context.getExternalCacheDir(), "tmp.jpg");
    }

    //"""""   On ActivityResult   """""""""
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.GALLERY && resultCode == RESULT_OK && null != data) {
            //isCamera = false;
            tmpUri = data.getData();
            // Log.e(TAG, tmpUri.toString());
            if (tmpUri != null) { // it will go to the CropImageActivity
                try {
                    // Bitmap profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                    //File file = savebitmap(this.getExternalCacheDir(), profileImageBitmap, ".jpg");

                    binding.inactiveUserImg.setVisibility(View.GONE);
                    binding.ivProfileImg.setImageURI(tmpUri);
                    Bitmap profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                    imageFile = savebitmap(this, profileImageBitmap, UUID.randomUUID() + ".jpg");

                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
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
                File imageFile1 = getTemporalFile(this);
                Uri photoURI = Uri.fromFile(imageFile1);

                bm = Constant.getImageResized(this, photoURI); ///Image resizer
                int rotation = ImageRotator.getRotation(this, photoURI, true);
                bm = ImageRotator.rotate(bm, rotation);

                profileImagefile = new File(this.getExternalCacheDir(), UUID.randomUUID() + ".jpg");
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
                    try {
                        // Bitmap profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                        //File file = savebitmap(this.getExternalCacheDir(), profileImageBitmap, ".jpg");

                        binding.inactiveUserImg.setVisibility(View.GONE);
                        binding.ivProfileImg.setImageURI(tmpUri);
                        Bitmap profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                        imageFile = savebitmap(this, profileImageBitmap, UUID.randomUUID() + ".jpg");

                    } catch (IOException e) {
                        Log.d(TAG, e.getMessage());
                    }

                   /* CropImage.activity(tmpUri).setCropShape(CropImageView.CropShape.OVAL).setMinCropResultSize(200, 200)
                            .setMaxCropResultSize(4000, 4000)
                            .setAspectRatio(300, 300)
                            .start(this);*/
                }
            }
        }
        //*********** circle cropping image ********//
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {   // Image Cropper
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                tmpUri = result.getUri();
                try {
                    // Bitmap profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                    //File file = savebitmap(this.getExternalCacheDir(), profileImageBitmap, ".jpg");

                    binding.inactiveUserImg.setVisibility(View.GONE);
                    binding.ivProfileImg.setImageURI(tmpUri);
                    Bitmap profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                    imageFile = savebitmap(this, profileImageBitmap, UUID.randomUUID() + ".jpg");

                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        if (requestCode == Constant.REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK || requestCode == Constant.SELECT_VIDEO_REQUEST && resultCode == RESULT_OK) {
            Bitmap thumbBitmap;

            try {
                Uri videoUri = data.getData();

                String videoFilePath = ImageVideoUtils.generatePath(videoUri, this);

                if (videoFilePath.endsWith(".mp4") | videoFilePath.endsWith(".3gp")) {
                    File videoFile = new File(videoFilePath);
                    // Get length of file in bytes
                    long fileSizeInBytes = videoFile.length();
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    long fileSizeInMB = fileSizeInKB / 1024;

                    if (fileSizeInMB < 30) {
                        finalVideoUri = videoUri;
                        finalVideoFilePath = videoFilePath;
                        thumbBitmap = ImageVideoUtils.getVidioThumbnail(finalVideoFilePath); //ImageVideoUtil.getCompressBitmap();
                        thumbBitmap = ImageVideoUtils.getVidioThumbnail(finalVideoFilePath);
                        //thumbBitmap = ImageVideoUtils.getVideoToThumbnil(videoFilePath, this);

                        int rotation = ImageRotator.getRotation(this, finalVideoUri, true);
                        thumbBitmap = ImageRotator.rotate(thumbBitmap, rotation);

                        videoThumbFile = savebitmap(this, thumbBitmap, UUID.randomUUID() + ".jpg");
                        // productImages.add(file);
                        IntroVideoModal carsImageBean = new IntroVideoModal();
                        carsImageBean.setmUri(finalVideoUri);
                        carsImageBean.setmFile(videoFile);
                        carsImageBean.setUrl(false);
                        carsImageBean.setFileType("video");
                        carsImageBean.setThumbBitmap(thumbBitmap);
                        File file = new File("");
                        carsImageBean.setThumbFile((videoThumbFile == null) ? file : videoThumbFile);

                        binding.videoImg.setImageBitmap(thumbBitmap);
                        binding.ivRemoveVideo.setVisibility(View.VISIBLE);

                        // profileImageFileList.add(bitmapToFile(thumbBitmap));

                        // mediaFilesList.add(1, carsImageBean);
                        /*if (mediaFilesList.size() == 5) {
                        mediaFilesList.remove(0);
                            }*/
                    } else {
                        Toast.makeText(this, "Please take less than 30 Mb file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Video format not supported", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                //  Toast.makeText(this, getString(R.string.alertImageException), Toast.LENGTH_SHORT).show();
            } catch (OutOfMemoryError e) {
                Toast.makeText(this, "Out of memory", Toast.LENGTH_SHORT).show();
            }
        }

        //*********Autocomplete place p[icker****************//
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Place place = PlaceAutocomplete.getPlace(this, data);

                binding.tvLocation.setText(place.getAddress());
                locationPlace = place.getAddress().toString();
                locationLatLng = place.getLatLng();
                placeLatitude = String.valueOf(locationLatLng.latitude);
                placeLongitude = String.valueOf(locationLatLng.longitude);
                Log.e(TAG, "Place: " + place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status1 = PlaceAutocomplete.getStatus(this, data);

                Log.i(TAG, status1.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {

                Constant.hideSoftKeyBoard(EditProfileWorkerActivity.this, binding.tvLocation);
            }
        }
    }

/*    private File savebitmap(File file, Bitmap thumbBitmap, String s) {
        tmpUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".fileprovider", file);

        try {
            OutputStream outStream = null;

            outStream = new FileOutputStream(file);
            thumbBitmap.compress(Bitmap.CompressFormat.PNG, 80, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return file;
    }*/


    // from bitmap to file creater"""""""""""
    public File savebitmap(Context mContext, Bitmap bitmap, String name) {
        File filesDir = mContext.getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, name);

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private File bitmapToFile(Bitmap bitmap) {
        try {
            String name = System.currentTimeMillis() + ".png";
            File file = new File(getCacheDir(), name);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bArr);
            fos.flush();
            fos.close();

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.category_spinner:
                /*if (position >= 1) {*/
                Log.e(TAG, skillsResponce.getData().get(position).getCategoryName());
                subCateoryAdapter = new ArrayAdapter<>(EditProfileWorkerActivity.this, R.layout.spinner_item, skillsResponce.getData().get(position).getSubcat());
                subCateoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
                subCategorySpinner.setOnItemSelectedListener(this);
                subCategorySpinner.setAdapter(subCateoryAdapter);
                addSkillsAdapter.notifyDataSetChanged();
                // ivCategorySpin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spinner_icon_rotator));
                //   }
                break;
            case R.id.sub_category_spinner:
                //ivSubCategorySpin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spinner_icon_rotator));
                // Log.e(TAG, "sub category: " + skillsResponce.getData().get(position).getSubcat().get(position).getCategoryName());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showSetIntroVideoDialog() {

        final CharSequence[] options = {"Take Video", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileWorkerActivity.this);

        builder.setTitle("Add Video");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Take Video")) {

                    Intent intentCaptureVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (intentCaptureVideo.resolveActivity(getPackageManager()) != null) {
                        long maxVideoSize = 18 * 1024 * 1024; // 18 MB
                        intentCaptureVideo.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);//120 sec = 2min  //10000sec //10 min
                        intentCaptureVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        intentCaptureVideo.putExtra(MediaStore.EXTRA_SIZE_LIMIT, maxVideoSize);
                        startActivityForResult(intentCaptureVideo, Constant.REQUEST_VIDEO_CAPTURE);
                    }

                } else if (options[which].equals("Choose from Gallery")) {
                    Intent intent;
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    } else {
                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                    }
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.SELECT_VIDEO_REQUEST);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        //success permission granted & call Location method
                    }
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(EditProfileWorkerActivity.this, "Deny Camera Permission", Toast.LENGTH_SHORT).show();

                }
            }
            break;
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        //success permission granted & call Location method
                    }
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(EditProfileWorkerActivity.this, "Deny Storage Permission", Toast.LENGTH_SHORT).show();

                }
            }
            break;

            case RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED) {
                        //success permission granted & call Location method
                        showSetIntroVideoDialog();
                    }
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(EditProfileWorkerActivity.this, "Deny Audio Permission", Toast.LENGTH_SHORT).show();

                }
            }
            break;
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

 /*   //"""""""""Video Compreesor"""""""""""""//
    private void compressVideo(Uri uri, final File tmpFile) {

        final File file;
        try {
            File outputDir = new File(getExternalFilesDir(null), "outputs");
            //noinspection ResultOfMethodCallIgnored
            outputDir.mkdir();
            file = File.createTempFile("transcode_test", ".mp4", outputDir);
        } catch (IOException e) {
            Log.e("TAG", "Failed to create temporary file.", e);
            Toast.makeText(this, "Failed to create temporary file.", Toast.LENGTH_LONG).show();
            return;
        }
        ContentResolver resolver = getContentResolver();
        final ParcelFileDescriptor parcelFileDescriptor;
        try {
            parcelFileDescriptor = resolver.openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            Log.d("Could not open '" + uri.toString() + "'", e.getMessage());
            // Toast.makeText(getApplicationContext(), "File not found.", Toast.LENGTH_LONG).show();
            return;
        }
        final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        final long startTime = SystemClock.uptimeMillis();
        MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
            @Override
            public void onTranscodeProgress(double progress) {
                mBuilder.setProgress(Constant.PROGRESS_BAR_MAX, (int) Math.round(progress * Constant.PROGRESS_BAR_MAX), false);
                mBuilder.setContentTitle("Uploading Video...");
                mNotifyManager.notify(ID, mBuilder.build());

              //  progressbar.setProgress((int) (progress * 100));
            }

            @Override
            public void onTranscodeCompleted() {
                Log.d("TAG", "transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                onTranscodeFinished(true, "transcoded file placed on " + file, parcelFileDescriptor);
                Log.e(TAG, String.valueOf((file.length() / 1024) / 1024));
                videoFile = new ArrayList<>();
                videoFile.add(file);
                apiCallForUploadVideo(videoFile);
            }

            @Override
            public void onTranscodeCanceled() {
                onTranscodeFinished(false, "Compress canceled.", parcelFileDescriptor);
            }

            @Override
            public void onTranscodeFailed(Exception exception) {
                // Get length of file in bytes
                long fileSizeInBytes = tmpFile.length();
                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                long fileSizeInKB = fileSizeInBytes / 1024;
                // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                long fileSizeInMB = fileSizeInKB / 1024;
                if (fileSizeInMB <= 10) {
                    Log.e(TAG, String.valueOf(fileSizeInMB));
                    videoFile = new ArrayList<>();
                    videoFile.add(file);
                    apiCallForUploadVideo(videoFile);
                }
            }
        };
        Log.d("TAG", "transcoding into " + file);
        Future<Void> mFuture = MediaTranscoder.getInstance().transcodeVideo(fileDescriptor, file.getAbsolutePath(),
                // MediaFormatStrategyPresets.createAndroid720pStrategy(8000 * 1000, 128 * 1000, 1), listener);
                MediaFormatStrategyPresets.createExportPreset960x540Strategy(), listener);
        // switchButtonEnabled(true);
    }

    private void onTranscodeFinished(boolean isSuccess, String toastMessage, ParcelFileDescriptor parcelFileDescriptor) {
        try {
            parcelFileDescriptor.close();
        } catch (IOException e) {
            Log.w("Error while closing", e);
        }
    }
*/

    private void compressVideo(Uri uri, final File tmpFile) {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/videos");
        if (f.mkdirs() || f.isDirectory())
            //compress and output new video specs
            new VideoCompressAsyncTask(this).execute(finalVideoFilePath, f.getPath());

    }






 /*   //"""""" upload video with user info """"""""""'//
    private void apiCallForUploadVideo(File tmpFile) {
        if (Constant.isNetworkAvailable(this,mainLayout)) {
            WorkerProfileModel workerProfileModel = new WorkerProfileModel();
            workerProfileModel.town = locationPlace;
            workerProfileModel.latitude = String.valueOf(locationLatLng.latitude);
            workerProfileModel.longitude = String.valueOf(locationLatLng.longitude);

            workerProfileModel.workerSkillData = getWorkerSkillData();
            Log.e("subCategory : ", getWorkerSkillData());

            progressbar.setVisibility(View.VISIBLE);
            Log.e("FIle size", String.valueOf((tmpFile.length() / 1024) / 1024));

            AndroidNetworking.upload("http://dev.livewire.work/service/user/updateWorkerProfile")
                    .addHeaders("authToken",PreferenceConnector.readString(this,PreferenceConnector.AUTH_TOKEN,""))
                    .addMultipartFile("intro_video",tmpFile)
                    .addMultipartFile("profileImage",profileImagefile)
                    .addMultipartParameter(workerProfileModel).setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener(){
                @Override
                public void onProgress(long bytesUploaded, long totalBytes) {
                    Log.e(TAG, "onProgress: "+bytesUploaded );
                }
            }).getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        String status = response.getString("status");
                        Constant.snackBar(mainLayout,message);
                        progressbar.setVisibility(View.GONE);
                        pDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("Complete p response:", response.toString());
                }

                @Override
                public void onError(ANError anError) {
                    Log.e("Error",anError.getErrorBody());
                }
            });

        }
    }*/

    private String getWorkerSkillData() {//it gives array format subcategory data  [{categor:10,Min_rate:2,Max_rate:10}]
        subCategoryModelList.clear();
        String json = null;
        for (int i = 0; i < addedSkillBeans.size(); i++) {
            for (int j = 0; j < addedSkillBeans.get(i).getSubCatagories().size(); j++) {
                SubCategoryModel model = new SubCategoryModel();
                model.setCategory_id(addedSkillBeans.get(i).getSubCatagories().get(j).getSubCatId());
                model.setMax_rate(addedSkillBeans.get(i).getSubCatagories().get(j).getMax_rate());
                model.setMin_rate(addedSkillBeans.get(i).getSubCatagories().get(j).getMin_rate());
                subCategoryModelList.add(model);
            }
        }
        json = new Gson().toJson(subCategoryModelList);

        return json;
    }

    @SuppressLint("StaticFieldLeak")
    private class VideoCompressAsyncTask extends AsyncTask<String, Void, String> {
        private Context mContext;

        VideoCompressAsyncTask(Context mCon) {
            this.mContext = mCon;
        }

        @Override
        protected String doInBackground(String... strings) {

            String filePath = null;
            try {
                //"""""""  silicon compressor video""""""""""""//
                filePath = SiliCompressor.with(mContext).compressVideo(strings[0], strings[1]);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return filePath;
        }

        @Override
        protected void onPostExecute(String s) {

            File videoFil = new File(s);
            long fileSizeInBytes = videoFil.length();
            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = fileSizeInBytes / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            long fileSizeInMB = fileSizeInKB / 1024;
            Log.e("Compresss Video size", String.valueOf(((videoFil.length() / 1024) / 1024)));

            if (fileSizeInMB < 10) {
                Uri compressUri = Uri.fromFile(videoFil);
                //FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName()+ FILE_PROVIDER_EXTENTION, imageFile);
                videoFile = new ArrayList<>();
                videoFile.add(videoFil);
                HashMap<String ,File> map = new HashMap<>();
                map.put("intro_video", videoFile.get(0));
                map.put("video_thumb", videoThumbFileList.get(0));
                map.put("profileImage",imageFile);
                apiCallForUpdateProfile(map);

            } else {
                progressDialog.dismiss();
                finalVideoFilePath = "";
                finalVideoUri = null;
                Constant.snackBar(binding.mainLayout, "Please select another video");
            }
        }
    }

    private void apiCallForUpdateProfile(HashMap<String, File> map) {
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.upload(BASE_URL + "user/updateUserProfile")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addMultipartParameter(mPram)
                    .addMultipartFile(map)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String status = null;
                            try {
                                progressDialog.hide();
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equalsIgnoreCase("success")) {
                                    //**********sucess fully add car status**************////
                                    SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                                    PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                                    PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                                    PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getThumbImage());
                                    PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                                    PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
                                    addUserFirebaseDatabase();
                                } else {
                                    progressDialog.hide();
                                    Constant.snackBar(binding.mainLayout, message);
                                }
                            } catch (JSONException e) {
                                progressDialog.hide();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                            progressDialog.hide();
                        }
                    });
        }
    }

   /* private void apiCallForUpdateProfile() {
        VolleyMySingleton volleySingleton = new VolleyMySingleton(EditProfileWorkerActivity.this);
        RequestQueue mRequest = volleySingleton.getInstance().getRequestQueue();
        mRequest.start();
        progressDialog.show();
        // progressbar.setVisibility(View.VISIBLE);
        MultiPartRequestForUpdateProfile mMultiPartRequest = new MultiPartRequestForUpdateProfile(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                setResponse(null, error);
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                progressDialog.dismiss();

                try {
                    Log.e("ADDVECH", "setResponse: " + response.toString());
                    JSONObject result;

                    result = new JSONObject(response.toString());
                    String status = result.getString("status");
                    String message = result.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        *//*//**/

    /*************sucess fully add car status
     * @param map**************//**//*//*
                        SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);

                        PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                        PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                        PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getThumbImage());
                        PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                        PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
                        addUserFirebaseDatabase();


                    } else {

                        Constant.snackBar(binding.mainLayout, message);
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Log.d(TAG, e.getMessage());
                }

            }
        }, videoFile, videoFile.size(), profileImageFileList, profileImageFileList.size(), videoThumbFileList, mPram, EditProfileWorkerActivity.this);

        mMultiPartRequest.setTag("MultiRequest");
        mMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequest.add(mMultiPartRequest);
    }*/

    private void apiCallForUpdateUserData(HashMap<String, File> map) {
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.upload(BASE_URL + "user/updateUserProfile")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addMultipartParameter(mPram)
                    .addMultipartFile(map)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String status = null;
                            try {
                                progressDialog.hide();
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equalsIgnoreCase("success")) {
                                    //**********sucess fully add car status**************////

                                    SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);

                                    PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                                    PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                                    PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getThumbImage());
                                    PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                                    PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
                                    addUserFirebaseDatabase();
                                } else {
                                    progressDialog.hide();
                                    Constant.snackBar(binding.mainLayout, message);
                                }
                            } catch (JSONException e) {
                                progressDialog.hide();
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {

                            progressDialog.hide();
                        }
                    });
        }
    }


    /*private void apiCallForUpdateUserData() {
        VolleyMySingleton volleySingleton = new VolleyMySingleton(EditProfileWorkerActivity.this);
        RequestQueue mRequest = volleySingleton.getInstance().getRequestQueue();
        mRequest.start();
        progressDialog.show();
        // progressbar.setVisibility(View.VISIBLE);
        MultiPartRequestForUpdateProfile mMultiPartRequest = new MultiPartRequestForUpdateProfile(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                setResponse(null, error);
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                progressDialog.dismiss();

                try {
                    Log.e("ADDVECH", "setResponse: " + response.toString());
                    JSONObject result;

                    result = new JSONObject(response.toString());
                    String status = result.getString("status");
                    String message = result.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        /*//*************sucess fully add car status**************//*/

                        SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);

                        PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                        PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                        PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getThumbImage());
                        PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                        PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
                        addUserFirebaseDatabase();

                    } else {

                        Constant.snackBar(binding.mainLayout, message);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }

            }
        }, profileImageFileList, profileImageFileList.size(), mPram, EditProfileWorkerActivity.this);

        mMultiPartRequest.setTag("MultiRequest");
        mMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequest.add(mMultiPartRequest);
    }

    //Respon dari volley, untuk menampilkan keterengan upload, seperti error, message dari server
    void setResponse(Object response, VolleyError error) {
        Log.e(TAG, error.getLocalizedMessage());
          *//*not used*//*
    }*/


    //""""""""""" remove added video """""""""""""//
    private void removeVideo() {
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + DELETE_VIDEO_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            binding.ivRemoveVideo.setVisibility(View.GONE);
                            binding.videoImg.setClickable(true);
                            finalVideoUri = null;
                            videoUrl = null;
                            isvideoUrl = false;
                            binding.videoImg.setImageDrawable(ContextCompat.getDrawable(EditProfileWorkerActivity.this, R.color.colorWhite));
                            binding.videoImg.setOnClickListener(EditProfileWorkerActivity.this);
                        } else {
                            Constant.snackBar(binding.mainLayout, message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
        infoFcm.availability = PreferenceConnector.readString(this, PreferenceConnector.AVAILABILITY_1, "");

        database.child(Constant.ARG_USERS)
                .child(PreferenceConnector.readString(this, PreferenceConnector.MY_USER_ID, ""))
                .setValue(infoFcm)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (subCategoryModelList.size() > 0) {
                                PreferenceConnector.writeString(EditProfileWorkerActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, "1");
                            }
                            finish();
                          /*  //Utils.goToOnlineStatus(SignInActivity.this, Constant.online);*/

                        } else {
                            Toast.makeText(EditProfileWorkerActivity.this, "Not Store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
