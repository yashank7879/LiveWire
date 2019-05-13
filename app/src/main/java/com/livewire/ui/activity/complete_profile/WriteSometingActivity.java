package com.livewire.ui.activity.complete_profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.iceteck.silicompressorr.SiliCompressor;
import com.livewire.R;
import com.livewire.databinding.ActivityWriteSometingBinding;
import com.livewire.responce.SignUpResponce;
import com.livewire.ui.activity.ClientMainActivity;
import com.livewire.ui.activity.WorkerMainActivity;
import com.livewire.ui.fragments.WriteSomthingAboutWorkerFragment;
import com.livewire.utils.Constant;
import com.livewire.utils.PermissionAll;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.UPDATE_WORKER_PROFILEAPI;

public class WriteSometingActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityWriteSometingBinding binding;
    private final String TAG = WriteSometingActivity.class.getName();
    private HashMap<String, String> mPram;
    private ArrayList<File> videoThumbFileList;
    private ArrayList<File> profileImageFileList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ArrayList<File> videoFile;
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");//1985-09-18

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write_someting);
        intializeView();
    }

    private void intializeView() {
        progressDialog = new ProgressDialog(this);
        binding.ivBack.setOnClickListener(this);
        //  binding.rlDob.setOnClickListener(this);
        binding.mainLayout.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.btnSkip.setOnClickListener(this);

       /* if (!PreferenceConnector.readString(mContext, PreferenceConnector.USER_DOB, "").isEmpty()){
           binding.tvDob.setText(PreferenceConnector.readString(mContext, PreferenceConnector.USER_DOB,""));
        }*/
        if (!PreferenceConnector.readString(this, PreferenceConnector.ABOUT_ME, "").isEmpty()) {
            binding.etAboutMe.setText(PreferenceConnector.readString(this, PreferenceConnector.ABOUT_ME, ""));
        }

        binding.etAboutMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //kjad
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //aksdgh
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = binding.etAboutMe.getText().toString();
                if (str.length() >= 200) {
                    Toast.makeText(WriteSometingActivity.this, "About yourself should not more 200 characters", Toast.LENGTH_SHORT).show();

                } else {
                    //   Toast.makeText(EditProfileWorkerActivity.this, "max lenght is 200", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_next:
                 intent = new Intent(this,BankAccountActivity.class);
                PreferenceConnector.writeString(this, PreferenceConnector.ABOUT_ME, binding.etAboutMe.getText().toString().trim());
                startActivity(intent);
               // profileValidations();
                break;
            case R.id.iv_back:
                PreferenceConnector.writeString(this, PreferenceConnector.ABOUT_ME, binding.etAboutMe.getText().toString().trim());
                // Toast.makeText(mContext, "fragment", Toast.LENGTH_SHORT).show();
               onBackPressed();
                break;
            case R.id.tv_cancel:
                break;
            case R.id.btn_skip:
                intent = new Intent(this,BankAccountActivity.class);
                startActivity(intent);
                //profileValidations();
                break;
            /*case R.id.rl_dob:
                openDobDialog();
                //Toast.makeText(mContext, "124421", Toast.LENGTH_SHORT).show();
                break;*/
            case R.id.main_layout:

                break;
        }
    }

 /*   private void profileValidations() {

        videoThumbFileList = new ArrayList<>();
        if (UploadIntroVideoActivity.uploadIntroVideoActivity.videoThumbFile != null) {
            videoThumbFileList.add(UploadIntroVideoActivity.uploadIntroVideoActivity.videoThumbFile);
        }

        mPram = new HashMap<>();

        mPram.put("workerSkillData", UploadIntroVideoActivity.uploadIntroVideoActivity.keySkills);
        // mPram.put("date_of_birth",""+sdf2.format(startDateTime.getTime()));
        // mPram.put("date_of_birth",""+binding.tvDob.getText().toString());
        mPram.put("intro_discription", binding.etAboutMe.getText().toString().trim());


        if (UploadIntroVideoActivity.uploadIntroVideoActivity.videoThumbFile != null) {
            // video send from the volly multi part
            uploadVideo();
        } else {
            // profile image and skills and location send
            sendOtherData();
        }

        *//*if (binding.tvDob.getText().toString().isEmpty()) {
            Constant.snackBar(binding.mainLayout, "Please select DOB");
        } else *//*
       *//* if (binding.etAboutMe.getText().toString().isEmpty()) {
            Constant.snackBar(binding.mainLayout, "please write something about you.");
        } else {
            // profileImageFileList = new ArrayList<>();

            videoThumbFileList = new ArrayList<>();
            if (UploadIntroVideoActivity.uploadIntroVideoActivity.videoThumbFile != null) {
                videoThumbFileList.add(UploadIntroVideoActivity.uploadIntroVideoActivity.videoThumbFile);
            }

            mPram = new HashMap<>();

            mPram.put("workerSkillData", UploadIntroVideoActivity.uploadIntroVideoActivity.keySkills);
            // mPram.put("date_of_birth",""+sdf2.format(startDateTime.getTime()));
            // mPram.put("date_of_birth",""+binding.tvDob.getText().toString());
            mPram.put("intro_discription", binding.etAboutMe.getText().toString().trim());


            if (UploadIntroVideoActivity.uploadIntroVideoActivity.videoThumbFile != null) {
                // video send from the volly multi part
                uploadVideo();
            } else {
                // profile image and skills and location send
                sendOtherData();
            }
        }*//*
    }

    //"""""""""" upload video  """"""""""""""//
    @SuppressLint("StaticFieldLeak")
    private void uploadVideo() {
        videoDialog();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                File file = new File(UploadIntroVideoActivity.uploadIntroVideoActivity.finalVideoFilePath);

                // Get length of file in bytes
                long fileSizeInBytes = file.length();
                // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                long fileSizeInKB = fileSizeInBytes / 1024;
                // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                long fileSizeInMB = fileSizeInKB / 1024;

                if (fileSizeInMB > 10) {
                    compressVideo(UploadIntroVideoActivity.uploadIntroVideoActivity.finalVideoUri, file);
                } else {
                    videoFile = new ArrayList<>();
                    videoFile.add(file);
                    HashMap<String, File> map = new HashMap<>();
                    map.put("intro_video", videoFile.get(0));
                    map.put("video_thumb", videoThumbFileList.get(0));
                    apiCallForUploadVideo(map);
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
    }


    private void compressVideo(Uri uri, final File tmpFile) {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/videos");
        if (f.mkdirs() || f.isDirectory())
            //compress and output new video specs
            new VideoCompressAsyncTask(this).execute(UploadIntroVideoActivity.uploadIntroVideoActivity.finalVideoFilePath, f.getPath());

    }

    @SuppressLint("StaticFieldLeak")
    private class VideoCompressAsyncTask extends AsyncTask<String, Void, String> {
        private Context mContext;

        public VideoCompressAsyncTask(Context mCon) {
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
                HashMap<String, File> map = new HashMap<>();
                map.put("intro_video", videoFile.get(0));
                map.put("video_thumb", videoThumbFileList.get(0));

                apiCallForUploadVideo(map);
                //apiCallForUploadVideo(videoFile);

            } else {
                progressDialog.dismiss();
                UploadIntroVideoActivity.uploadIntroVideoActivity.finalVideoFilePath = "";
                UploadIntroVideoActivity.uploadIntroVideoActivity.finalVideoUri = null;
                Constant.snackBar(binding.mainLayout, "Please select another video");
            }
        }
    }

    private void apiCallForUploadVideo(HashMap<String, File> map) {
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.upload(BASE_URL + UPDATE_WORKER_PROFILEAPI)
                    .addHeaders("authToken", PreferenceConnector.readString(WriteSometingActivity.this, PreferenceConnector.AUTH_TOKEN, ""))
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
                                    SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, "1");
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getProfileImage());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());

                                    finishAffinity();
                                    Intent intent = new Intent(WriteSometingActivity.this, WorkerMainActivity.class);
                                    startActivity(intent);
                                    finish();
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

    private void sendOtherData() {
        if (Constant.isNetworkAvailable(this, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.upload(BASE_URL + UPDATE_WORKER_PROFILEAPI)
                    .addMultipartParameter(mPram)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            //  progressBar.setVisibility(View.GONE);
                            String status = null;
                            try {
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                    //ChangeModeApi();
                                    SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                                    //Log.e("sign up response", userResponce.getData().toString());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, "1");
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getProfileImage());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                                    PreferenceConnector.writeString(WriteSometingActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
                                    //addUserFirebaseDatabase();

                                    finishAffinity();
                                    Intent intent = new Intent(WriteSometingActivity.this, WorkerMainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Constant.snackBar(binding.mainLayout, message);
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                // Log.e(TAG, e.getMessage());
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            progressDialog.dismiss();
                            Constant.errorHandle(anError, WriteSometingActivity.this);
                            //  Log.e(TAG, anError.getErrorDetail());
                        }
                    });

        }
    }*/

}
