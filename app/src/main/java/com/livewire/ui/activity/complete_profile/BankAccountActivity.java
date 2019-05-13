package com.livewire.ui.activity.complete_profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.iceteck.silicompressorr.SiliCompressor;
import com.livewire.R;
import com.livewire.databinding.ActivityAddBankAccountBinding;
import com.livewire.model.AddBankAccontModel;
import com.livewire.multiple_file_upload.MultiPartRequest;
import com.livewire.multiple_file_upload.Template;
import com.livewire.multiple_file_upload.VolleyMySingleton;
import com.livewire.responce.BankAccDetailResponce;
import com.livewire.responce.SignUpResponce;
import com.livewire.ui.activity.MyProfileClientActivity;
import com.livewire.ui.activity.WorkerMainActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PermissionAll;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.livewire.utils.ApiCollection.ADD_BANK_ACCOUNT_API;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.GET_BANK_DETAILS_API;
import static com.livewire.utils.ApiCollection.UPDATE_WORKER_PROFILEAPI;

public class BankAccountActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ActivityAddBankAccountBinding binding;
    private ArrayAdapter accTypeAdapter;
    private String accHolderType;
    private ArrayList<String> accTypeList;
    private ProgressDialog progressDialog;
    private Calendar startDateTime;
    private final String TAG = WriteSometingActivity.class.getName();
    private HashMap<String, String> mPram;
    private ArrayList<File> videoThumbFileList;
    private ArrayList<File> profileImageFileList = new ArrayList<>();
    private ArrayList<File> videoFile;
    private AddBankAccontModel model = new AddBankAccontModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bank_account);
        progressDialog = new ProgressDialog(this);

        binding.payNow.setOnClickListener(this);
        // binding.llDob.setOnClickListener(this);
        binding.actionBarWorker.ivBack.setOnClickListener(this);
        binding.actionBarWorker.tvLiveWire.setText(R.string.add_bank_account);

        accTypeList = new ArrayList<>();
        accTypeList.add("Account Holder Type");
        accTypeList.add("Saving");
        accTypeList.add("Current");
        accTypeList.add("Joint");
        accTypeList.add("Fixed Deposit Account");
        accTypeList.add("NRI Accounts");

        setBankData();
        // accTypeAdapter = new ArrayAdapter<>(AddBankAccountActivity.this, R.layout.spinner_item, accTypeList);
        //accTypeAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        //  binding.spinnerBankType.setOnItemSelectedListener(this);
        //  binding.spinnerBankType.setAdapter(accTypeAdapter);


    }

    private void setBankData() {
        if (!PreferenceConnector.readString(this, PreferenceConnector.FIRST_NAME, "").isEmpty()) {
            binding.accHolderFirstName.setText(PreferenceConnector.readString(this, PreferenceConnector.FIRST_NAME, ""));
        }
        if (!PreferenceConnector.readString(this, PreferenceConnector.LAST_NAME, "").isEmpty()) {
            binding.accHolderLastName.setText(PreferenceConnector.readString(this, PreferenceConnector.LAST_NAME, ""));
        }
        if (!PreferenceConnector.readString(this, PreferenceConnector.BANK_ACC_NO, "").isEmpty()) {
            binding.accNumber.setText(PreferenceConnector.readString(this, PreferenceConnector.BANK_ACC_NO, ""));
        }
        if (!PreferenceConnector.readString(this, PreferenceConnector.BRANCH_CODE, "").isEmpty()) {
            binding.branchCode.setText(PreferenceConnector.readString(this, PreferenceConnector.BRANCH_CODE, ""));
        }
        if (!PreferenceConnector.readString(this, PreferenceConnector.BANK_NAME, "").isEmpty()) {
            binding.etBankName.setText(PreferenceConnector.readString(this, PreferenceConnector.BANK_NAME, ""));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_now: {
                bankAccountValidations();
                break;
            }
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            default: {
                break;
            }
            /*case R.id.ll_dob: {
                //openDobCalender();
                break;
            }*/
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PreferenceConnector.writeString(this, PreferenceConnector.FIRST_NAME, binding.accHolderFirstName.getText().toString());
        PreferenceConnector.writeString(this, PreferenceConnector.LAST_NAME, binding.accHolderLastName.getText().toString().trim());
        PreferenceConnector.writeString(this, PreferenceConnector.BANK_ACC_NO, binding.accNumber.getText().toString().trim());
        PreferenceConnector.writeString(this, PreferenceConnector.BRANCH_CODE, binding.branchCode.getText().toString().trim());
        PreferenceConnector.writeString(this, PreferenceConnector.BANK_NAME, binding.etBankName.getText().toString().trim());

    }

    private void resetBankData() {
        PreferenceConnector.writeString(this, PreferenceConnector.FIRST_NAME, "");
        PreferenceConnector.writeString(this, PreferenceConnector.LAST_NAME, "");
        PreferenceConnector.writeString(this, PreferenceConnector.BANK_ACC_NO, "");
        PreferenceConnector.writeString(this, PreferenceConnector.BRANCH_CODE, "");
        PreferenceConnector.writeString(this, PreferenceConnector.BANK_NAME, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void bankAccountValidations() {
        if (Validation.isEmpty(binding.accHolderFirstName)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_account_holder_first_name));
        } else if (binding.accHolderFirstName.getText().toString().trim().contains("  ")) {
            Constant.snackBar(binding.svAddbankLayout, "Single space should allowed here.");
            binding.accHolderFirstName.requestFocus();
            Constant.hideSoftKeyBoard(this, binding.accHolderFirstName);
        } else if (Validation.isEmpty(binding.accHolderLastName)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_account_holder_last_name));
        } else if (binding.accHolderLastName.getText().toString().trim().contains("  ")) {
            binding.accHolderLastName.requestFocus();
            Constant.hideSoftKeyBoard(this, binding.accHolderLastName);
            Constant.snackBar(binding.svAddbankLayout, "Single space should allowed here.");
        } else if (Validation.isEmpty(binding.accNumber)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_account_number));
        } else if (Validation.isEmpty(binding.branchCode)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_branch_code));
        } else if (binding.branchCode.getText().toString().trim().contains("  ")) {
            binding.branchCode.requestFocus();
            Constant.hideSoftKeyBoard(this, binding.branchCode);
            Constant.snackBar(binding.svAddbankLayout, "Single space should allowed here.");
        } else if (Validation.isEmpty(binding.etBankName)) {
            Constant.snackBar(binding.svAddbankLayout, "Please enter bank name");
        } else {
            // AddBankAccontModel model = new AddBankAccontModel();
         /*   model.firstName = binding.accHolderFirstName.getText().toString().trim();
            model.lastName = binding.accHolderLastName.getText().toString().trim();
            model.branch_code = binding.branchCode.getText().toString().trim();
            model.accountNumber = binding.accNumber.getText().toString().trim();
            model.bankName = binding.etBankName.getText().toString().trim();*/

            // addBankAccApi(model);
            profileValidations();
        }

        /*else if (Validation.isEmpty(binding.postalCode)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_Postal_code));
        } else if (Validation.isEmpty(binding.ssnLast)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_enter_SSN_number));
        } else if (accHolderType.isEmpty()) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.please_select_acc_type));
        } else if (Validation.isEmpty(binding.tvDob)) {
            Constant.snackBar(binding.svAddbankLayout, getString(R.string.pls_select_your_dob));
        }*/
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

     /*   if (!accTypeList.get(binding.spinnerBankType.getSelectedItemPosition()).equals("Account Holder Type")) {
            accHolderType = String.valueOf(binding.spinnerBankType.getSelectedItem());
        } else accHolderType = "";*/

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void profileValidations() {

        videoThumbFileList = new ArrayList<>();
        if (UploadIntroVideoActivity.uploadIntroVideoActivity.videoThumbFile != null) {
            videoThumbFileList.add(UploadIntroVideoActivity.uploadIntroVideoActivity.videoThumbFile);
        }

        mPram = new HashMap<>();
        String skillData  = UploadIntroVideoActivity.uploadIntroVideoActivity.keySkills != null ? UploadIntroVideoActivity.uploadIntroVideoActivity.keySkills :"";
        mPram.put("workerSkillData", skillData);
        // mPram.put("date_of_birth",""+sdf2.format(startDateTime.getTime()));
        // mPram.put("date_of_birth",""+binding.tvDob.getText().toString());
        mPram.put("intro_discription", PreferenceConnector.readString(this, PreferenceConnector.ABOUT_ME, ""));

        mPram.put("firstName", binding.accHolderFirstName.getText().toString().trim());
        mPram.put("lastName", binding.accHolderLastName.getText().toString().trim());
        mPram.put("branch_code", binding.branchCode.getText().toString().trim());
        mPram.put("accountNumber", binding.accNumber.getText().toString().trim());
        mPram.put("bankName", binding.etBankName.getText().toString().trim());


        if (UploadIntroVideoActivity.uploadIntroVideoActivity.videoThumbFile != null) {
            // video send from the volly multi part
            uploadVideo();
        } else {
            // profile image and skills and location send
            sendOtherData();
        }

        /*if (binding.tvDob.getText().toString().isEmpty()) {
            Constant.snackBar(binding.mainLayout, "Please select DOB");
        } else */
       /* if (binding.etAboutMe.getText().toString().isEmpty()) {
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
        }*/
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
                    //apiCallForUploadVideo(map);

                    apiCallForUploadVideo();
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

                apiCallForUploadVideo();
                //apiCallForUploadVideo(map);
                //apiCallForUploadVideo(videoFile);

            } else {
                progressDialog.dismiss();
                UploadIntroVideoActivity.uploadIntroVideoActivity.finalVideoFilePath = "";
                UploadIntroVideoActivity.uploadIntroVideoActivity.finalVideoUri = null;
                Constant.snackBar(binding.svAddbankLayout, "Please select another video");
            }
        }
    }

    private void apiCallForUploadVideo(HashMap<String, File> map) {
        if (Constant.isNetworkAvailable(this, binding.svAddbankLayout)) {
            progressDialog.show();
            AndroidNetworking.upload(BASE_URL + UPDATE_WORKER_PROFILEAPI)
                    .addHeaders("authToken", PreferenceConnector.readString(BankAccountActivity.this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addMultipartParameter(mPram)
                    .addMultipartFile(map)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String status = null;
                            try {
                                progressDialog.dismiss();
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equalsIgnoreCase("success")) {
                                    SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, "1");
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getProfileImage());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.USER_MODE, Constant.WORKER);
                                    resetBankData();
                                    finishAffinity();
                                    Intent intent = new Intent(BankAccountActivity.this, WorkerMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Constant.snackBar(binding.svAddbankLayout, message);
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
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

     private void apiCallForUploadVideo() {
        VolleyMySingleton volleySingleton = new VolleyMySingleton(this);
        RequestQueue mRequest = volleySingleton.getInstance().getRequestQueue();
        mRequest.start();
        progressDialog.show();
        // progressbar.setVisibility(View.VISIBLE);
        MultiPartRequest mMultiPartRequest = new MultiPartRequest(new Response.ErrorListener() {
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
                    // Log.e("ADDVECH", "setResponse: " + response.toString());
                    JSONObject result;
                    result = new JSONObject(response.toString());
                    String status = result.getString("status");
                    String message = result.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        /// ***********success fully status**************
                        //ChangeModeApi();
                        SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                        PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, "1");
                        PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                        PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                        PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
                        PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getProfileImage());
                        PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                        PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());

                        // addUserFirebaseDatabase();
                        finishAffinity();
                        Intent intent = new Intent(BankAccountActivity.this, WorkerMainActivity.class);
                        startActivity(intent);
                       finish();
                        // finish();
                    } else {

                        Constant.snackBar(binding.svAddbankLayout, message);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }, videoFile, videoFile.size(), profileImageFileList, profileImageFileList.size(), videoThumbFileList, mPram, this);

        mMultiPartRequest.setTag("MultiRequest");
        mMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(Template.VolleyRetryPolicy.SOCKET_TIMEOUT,
                Template.VolleyRetryPolicy.RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequest.add(mMultiPartRequest);
    }

    void setResponse(Object response, VolleyError error) {
        Log.e(TAG, error.getLocalizedMessage());
        /*not used*/
    }


    private void sendOtherData() {
        if (Constant.isNetworkAvailable(this, binding.svAddbankLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + UPDATE_WORKER_PROFILEAPI)
                    .addBodyParameter(mPram)
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
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.COMPLETE_PROFILE_STATUS, "1");
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.PROFILE_IMG, userResponce.getData().getProfileImage());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.Name, userResponce.getData().getName());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.Email, userResponce.getData().getEmail());
                                    PreferenceConnector.writeString(BankAccountActivity.this, PreferenceConnector.USER_MODE, Constant.WORKER);

                                    //addUserFirebaseDatabase();
                                    resetBankData();

                                    finishAffinity();
                                    Intent intent = new Intent(BankAccountActivity.this, WorkerMainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Constant.snackBar(binding.svAddbankLayout, message);
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                // Log.e(TAG, e.getMessage());
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            progressDialog.dismiss();
                            Constant.errorHandle(anError, BankAccountActivity.this);
                            //  Log.e(TAG, anError.getErrorDetail());
                        }
                    });

        }
    }

}
