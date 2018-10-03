package com.livewire.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.CategaryAdapter;
import com.livewire.cropper.CropImage;
import com.livewire.cropper.CropImageView;
import com.livewire.model.AddedSkillBean;
import com.livewire.model.CategoryModel;
import com.livewire.model.IntroVideoModal;
import com.livewire.model.SubCategoryModel;
import com.livewire.multiple_file_upload.MultiPartRequest;
import com.livewire.multiple_file_upload.Template;
import com.livewire.multiple_file_upload.VolleyMySingleton;
import com.livewire.responce.AddSkillsResponce;
import com.livewire.utils.Constant;
import com.livewire.utils.ImageRotator;
import com.livewire.utils.ImageVideoUtils;
import com.livewire.utils.PermissionAll;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.squareup.picasso.Picasso;

import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.Constant.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.livewire.utils.Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.livewire.utils.Constant.RECORD_AUDIO;

public class CompleteProfileActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = CompleteProfileActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private Uri tmpUri;
    private CircleImageView ivProfileImg;
    private Bitmap profileImageBitmap;
    private ImageView inactiveUserImg;
    private int width;
    private List<CategoryModel> category = new ArrayList<>();
    private AddSkillsResponce skillsResponce;
    private ArrayAdapter categoryAdapter;
    private Spinner subCategorySpinner;
    private ArrayAdapter<AddSkillsResponce.DataBean.SubcatBean> subCateoryAdapter;
    private RecyclerView recyclerView;
    private ImageView videoImg;
    //private Uri VideoUri;
    //private String videoFilePath;
    private ArrayList<AddedSkillBean> addedSkillBeans = new ArrayList<>();
    private ArrayList<SubCategoryModel> subCategoryModelList = new ArrayList<>();
    private CategaryAdapter addSkillsAdapter;
    private TextView tvLocation;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String locationPlace;
    private Notification.Builder mBuilder;
    private Dialog pDialog;
    private ProgressBar progressbar;
    private TextView tv_for_videoP;
    private NotificationManager mNotifyManager;
    private static int ID = 100;
    private LatLng locationLatLng;
    private File profileImagefile;
    private Button btnSave;
    private ScrollView mainLayout;
    private RelativeLayout mainLayout1;
    private ArrayList<File> videoFile;
    private HashMap<String, String> mPram;
    private ProgressDialog progressDialog;
    private ArrayList<File> profileImageFileList = new ArrayList<>();
    private Uri finalVideoUri;
    private String finalVideoFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        Log.e("Auth token", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""));
        intializeView();
        loadSkillsData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadSkillsData() {
        if (Constant.isNetworkAvailable(this, mainLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + "getCategoryList")
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
                            Constant.snackBar(mainLayout, message);
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

    private void intializeView() {
        PermissionAll permissionAll = new PermissionAll();
        permissionAll.RequestMultiplePermission(CompleteProfileActivity.this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;



        progressDialog = new ProgressDialog(this);
        mainLayout = findViewById(R.id.main_layout);
        TextView tvSkip = findViewById(R.id.tv_skip);
        FrameLayout flProfileImg = findViewById(R.id.fl_user_profile);
        ivProfileImg = findViewById(R.id.iv_profile_img);
        inactiveUserImg = findViewById(R.id.inactive_user_img);
        RelativeLayout addSkillsLayout = findViewById(R.id.add_skills_rl);
        recyclerView = findViewById(R.id.recycler_view);
        tvLocation = findViewById(R.id.tv_location);
        btnSave = findViewById(R.id.btn_save);
        mainLayout1 = findViewById(R.id.complete_rl);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        videoImg = findViewById(R.id.video_img);
        addSkillsAdapter = new CategaryAdapter(this, addedSkillBeans);
        recyclerView.setAdapter(addSkillsAdapter);


        tvSkip.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
        videoImg.setOnClickListener(this);
        addSkillsLayout.setOnClickListener(this);
        flProfileImg.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (getIntent().hasExtra("imageKey")) {
            if (getIntent().getStringExtra("imageKey") != null) {
                inactiveUserImg.setVisibility(View.GONE);
                Picasso.with(ivProfileImg.getContext()).load(getIntent().getStringExtra("imageKey")).placeholder(R.drawable.ic_user).fit().into(ivProfileImg);
            }
        }

    }

    @Override
    public void onClick(View v) {
        PermissionAll permissionAll = new PermissionAll();
        switch (v.getId()) {
            case R.id.fl_user_profile:
                if (permissionAll.RequestMultiplePermission(CompleteProfileActivity.this)) //camera permission
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
                } else showSetIntroVideoDialog();
                break;
            case R.id.tv_location:
                autoCompletePlacePicker();
                break;
            case R.id.btn_save:
                Log.e("SubCategory: ", getWorkerSkillData());
                profileValidations();
                break;
            case R.id.tv_skip:
                Intent intent1 = new Intent(CompleteProfileActivity.this, WorkerMainActivity.class);
                startActivity(intent1);
                finish();
            default:
        }
    }

    private void profileValidations() {
        if (addedSkillBeans.size() == 0) {
            Constant.snackBar(mainLayout, "please add your skills");
        } else if (subCategoryModelList.size() == 0) {
            Constant.snackBar(mainLayout, "please add your Sub category ");
        } else if (locationLatLng == null) {
            Constant.snackBar(mainLayout, "please enter loation");
        } else if (finalVideoUri == null) {
            Constant.snackBar(mainLayout, "please add introvideo");
        } else {
            mPram = new HashMap<>();
            mPram.put("workerSkillData", getWorkerSkillData());
            mPram.put("latitude", String.valueOf(locationLatLng.latitude));
            mPram.put("longitude", String.valueOf(locationLatLng.longitude));
            mPram.put("town", locationPlace);
            uploadVideo();
        }

    }

    //"""""""""show dialog for add skills""""""""""""""""//
    private void showAddSkillsDialog() {
        if (Constant.isNetworkAvailable(this, mainLayout) && skillsResponce != null) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.add_skills_dialog);
            dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
            final RelativeLayout addSkillsLayout = dialog.findViewById(R.id.add_skills_layout);
            final TextView etMinPrice = dialog.findViewById(R.id.et_min_price);
            final TextView etMaxPrice = dialog.findViewById(R.id.et_max_price);
            Button btnAddSkills = dialog.findViewById(R.id.btn_add_skills);
            final Spinner categorySpinner = dialog.findViewById(R.id.category_spinner);
            subCategorySpinner = dialog.findViewById(R.id.sub_category_spinner);

            categoryAdapter = new ArrayAdapter<>(CompleteProfileActivity.this, R.layout.spinner_item, skillsResponce.getData());
            categoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
            categorySpinner.setOnItemSelectedListener(this);
            categorySpinner.setAdapter(categoryAdapter);
            Constant.hideSoftKeyBoard(CompleteProfileActivity.this, etMaxPrice);
            btnAddSkills.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogValidations(categorySpinner, etMinPrice, etMaxPrice, addSkillsLayout, dialog);
                    Constant.hideSoftKeyBoard(CompleteProfileActivity.this, etMaxPrice);

                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.hideSoftKeyBoard(CompleteProfileActivity.this, etMaxPrice);
                    dialog.dismiss();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } else loadSkillsData();
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
        } else if (maxPrice.getText().toString().equals("") || maxPrice.getText().toString().length() == 0) {
            Constant.snackBar(addSkillsLayout, "Please enter max price");
            maxPrice.requestFocus();
        }/*else if ((maxPric-minPric) < 0){
            Constant.snackBar(addSkillsLayout, "Min price and Max price can't be same");
            maxPrice.requestFocus();
        }*/ else if (Integer.parseInt(minPrice.getText().toString()) >= Integer.parseInt(maxPrice.getText().toString())) {
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
                            Toast.makeText(CompleteProfileActivity.this, "Subcategory Already added.", Toast.LENGTH_SHORT).show();
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
        videoDialog();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mNotifyManager = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new Notification.Builder(getApplicationContext());
                mBuilder.setSmallIcon(R.drawable.livelogo);

                // String path = ImageVideoUtil.generatePath(videoUri, AddVideosActivity.this);
                File file = new File(finalVideoFilePath);

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
                    apiCallForUploadVideo(videoFile);
                }
                return null;
            }
        }.execute();
    }

    //""""""" show progress """""""""""""""//
    private void videoDialog() {

        PermissionAll permissionAll = new PermissionAll();
        permissionAll.checkWriteStoragePermission(this);

        progressDialog.show();

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

        AlertDialog.Builder builder = new AlertDialog.Builder(CompleteProfileActivity.this);

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
                            tmpUri = FileProvider.getUriForFile(CompleteProfileActivity.this,
                                    getApplicationContext().getPackageName() + ".fileprovider",
                                    getTemporalFile(CompleteProfileActivity.this));
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
                    inactiveUserImg.setVisibility(View.GONE);
                    ivProfileImg.setImageURI(tmpUri);
                    profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tmpUri);
                    File file = savebitmap(this.getExternalCacheDir(), profileImageBitmap, ".jpg");
                    profileImageFileList.add(file);
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

                    if (fileSizeInMB <= 18) {
                        finalVideoUri = videoUri;
                        finalVideoFilePath = videoFilePath;
                        thumbBitmap = ImageVideoUtils.getVidioThumbnail(finalVideoFilePath); //ImageVideoUtil.getCompressBitmap();
                        //thumbBitmap = ImageVideoUtils.getVideoToThumbnil(videoFilePath, this);

                        int rotation = ImageRotator.getRotation(this, finalVideoUri, true);
                        thumbBitmap = ImageRotator.rotate(thumbBitmap, rotation);


                        File thumbFile = savebitmap(this.getExternalCacheDir(), thumbBitmap, UUID.randomUUID() + ".jpg");
// productImages.add(file);
                        IntroVideoModal carsImageBean = new IntroVideoModal();
                        carsImageBean.setmUri(finalVideoUri);
                        carsImageBean.setmFile(videoFile);
                        carsImageBean.setUrl(false);
                        carsImageBean.setFileType("video");
                        carsImageBean.setThumbBitmap(thumbBitmap);
                        File file = new File("");
                        carsImageBean.setThumbFile((thumbFile == null) ? file : thumbFile);

                        videoImg.setImageBitmap(thumbBitmap);

                        // mediaFilesList.add(1, carsImageBean);
/*if (mediaFilesList.size() == 5) {
mediaFilesList.remove(0);
}*/

                   /* if (mediaFilesList.size() == 5) {
                        mediaFilesList.remove(null);
                    } else if (!mediaFilesList.contains(null)) {
                        mediaFilesList.add(0, null);
                    }*/
                    } else {
                        Toast.makeText(this, "Please take less than 20Mb file", Toast.LENGTH_SHORT).show();
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

                tvLocation.setText(place.getAddress());
                locationPlace = place.getAddress().toString();
                locationLatLng = place.getLatLng();
                Log.e(TAG, "Place: " + place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status1 = PlaceAutocomplete.getStatus(this, data);

                Log.i(TAG, status1.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {

                Constant.hideSoftKeyBoard(CompleteProfileActivity.this, tvLocation);
            }
        }
    }

    private File savebitmap(File file, Bitmap thumbBitmap, String s) {
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
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.category_spinner:
                /*if (position >= 1) {*/
                Log.e(TAG, skillsResponce.getData().get(position).getCategoryName());
                subCateoryAdapter = new ArrayAdapter<>(CompleteProfileActivity.this, R.layout.spinner_item, skillsResponce.getData().get(position).getSubcat());
                subCateoryAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
                subCategorySpinner.setOnItemSelectedListener(this);
                subCategorySpinner.setAdapter(subCateoryAdapter);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(CompleteProfileActivity.this);

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
                    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    } else {
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
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
                    Toast.makeText(CompleteProfileActivity.this, "Deny Camera Permission", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(CompleteProfileActivity.this, "Deny Storage Permission", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(CompleteProfileActivity.this, "Deny Audio Permission", Toast.LENGTH_SHORT).show();

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

    //"""""""""Video Compreesor"""""""""""""//
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
                if (fileSizeInMB <= 30) {
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

    private void apiCallForUploadVideo(ArrayList<File> tmpFile) {
        VolleyMySingleton volleySingleton = new VolleyMySingleton(CompleteProfileActivity.this);
        RequestQueue mRequest = volleySingleton.getInstance().getRequestQueue();
        mRequest.start();
        progressDialog.show();
       // progressbar.setVisibility(View.VISIBLE);
        MultiPartRequest mMultiPartRequest = new MultiPartRequest(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
               // pDialog.dismiss();
              //  progressbar.setVisibility(View.GONE);
                setResponse(null, error);
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                progressDialog.dismiss();
              /*  progressbar.setVisibility(View.GONE);
                pDialog.dismiss();*/
                try {
                    Log.e("ADDVECH", "setResponse: " + response.toString());
                    JSONObject result;

                    result = new JSONObject(response.toString());
                    String status = result.getString("status");
                    String message = result.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        //*************sucess fully add car status**************//
                        Constant.snackBar(mainLayout, message);
                        PreferenceConnector.writeString(CompleteProfileActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, "1");
                        Intent intent = new Intent(CompleteProfileActivity.this, WorkerMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        Constant.snackBar(mainLayout, message);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }

            }
        }, tmpFile, tmpFile.size()/*,profileImageFileList,profileImageFileList.size()*/, mPram, CompleteProfileActivity.this);

        mMultiPartRequest.setTag("MultiRequest");
        mMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequest.add(mMultiPartRequest);
    }

    //Respon dari volley, untuk menampilkan keterengan upload, seperti error, message dari server
    void setResponse(Object response, VolleyError error) {
        Log.e(TAG, error.getLocalizedMessage());
          /*not used*/
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

}