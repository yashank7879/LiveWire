package com.livewire.ui.fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
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
import com.livewire.databinding.FragmentWriteSomthingAboutWorkerBinding;
import com.livewire.multiple_file_upload.MultiPartRequest;
import com.livewire.multiple_file_upload.Template;
import com.livewire.multiple_file_upload.VolleyMySingleton;
import com.livewire.responce.SignUpResponce;
import com.livewire.ui.activity.ClientMainActivity;
import com.livewire.ui.activity.CompleteProfileActivity;
import com.livewire.ui.activity.EditProfileWorkerActivity;
import com.livewire.ui.activity.MyProfileClientActivity;
import com.livewire.ui.activity.UploadIntroVideoActivity;
import com.livewire.ui.activity.WorkerMainActivity;
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
import java.util.Date;
import java.util.HashMap;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.CHANGE_USER_MODE_API;
import static com.livewire.utils.ApiCollection.UPDATE_WORKER_PROFILEAPI;

public class WriteSomthingAboutWorkerFragment extends Fragment implements View.OnClickListener {
    FragmentWriteSomthingAboutWorkerBinding binding;
    private final String TAG = WriteSomthingAboutWorkerFragment.class.getName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mParam1;
    private String mParam2;
    private Calendar startDateTime;
    private String startDateString = "";
    private HashMap<String, String> mPram;
    private ArrayList<File> videoThumbFileList;
    private ArrayList<File> profileImageFileList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ArrayList<File> videoFile;
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");//1985-09-18

    public WriteSomthingAboutWorkerFragment() {
        // Required empty public constructor
    }

    public static WriteSomthingAboutWorkerFragment newInstance(String param1, String param2) {
        WriteSomthingAboutWorkerFragment fragment = new WriteSomthingAboutWorkerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_write_somthing_about_worker, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(mContext);
        binding.ivBack.setOnClickListener(this);
        binding.rlDob.setOnClickListener(this);
        binding.mainLayout.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);

        if (!PreferenceConnector.readString(mContext, PreferenceConnector.USER_DOB, "").isEmpty()){
           binding.tvDob.setText(PreferenceConnector.readString(mContext, PreferenceConnector.USER_DOB,""));
        }
        if (!PreferenceConnector.readString(mContext, PreferenceConnector.ABOUT_ME, "").isEmpty()){
            binding.etAboutMe.setText(PreferenceConnector.readString(mContext, PreferenceConnector.ABOUT_ME, ""));
        }

        binding.etAboutMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = binding.etAboutMe.getText().toString();
                if (str.length() >= 200) {
                    Toast.makeText(mContext, "About yourself should not more 200 characters", Toast.LENGTH_SHORT).show();
                } else {
                    //   Toast.makeText(EditProfileWorkerActivity.this, "max lenght is 200", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                profileValidations();
                break;
            case R.id.iv_back:
                PreferenceConnector.writeString(mContext, PreferenceConnector.ABOUT_ME, binding.etAboutMe.getText().toString().trim());

                // Toast.makeText(mContext, "fragment", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                break;
            case R.id.tv_cancel:
                getActivity().finishAffinity();
                Intent intent = new Intent(mContext, ClientMainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.btn_skip:
                break;
            case R.id.rl_dob:
                openDobDialog();
                //Toast.makeText(mContext, "124421", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_layout:

                break;
        }
    }

    private void profileValidations() {
        if (binding.tvDob.getText().toString().isEmpty()) {
            Constant.snackBar(binding.mainLayout, "Please select DOB");
        } else if (binding.etAboutMe.getText().toString().isEmpty()) {
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
            mPram.put("date_of_birth",""+binding.tvDob.getText().toString());
            mPram.put("intro_discription", binding.etAboutMe.getText().toString().trim());


            if (UploadIntroVideoActivity.uploadIntroVideoActivity.videoThumbFile != null) {
                // video send from the volly multi part
                uploadVideo();
            } else {
                // profile image and skills and location send
                sendOtherData();
            }
        }
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
                    apiCallForUploadVideo(videoFile);

                }

                return null;
            }
        }.execute();
    }


    //""""""" show progress """""""""""""""//
    private void videoDialog() {

        PermissionAll permissionAll = new PermissionAll();
        permissionAll.checkWriteStoragePermission(getActivity());

        progressDialog.show();

    }


    private void compressVideo(Uri uri, final File tmpFile) {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/videos");
        if (f.mkdirs() || f.isDirectory())
            //compress and output new video specs
            new VideoCompressAsyncTask(mContext).execute(UploadIntroVideoActivity.uploadIntroVideoActivity.finalVideoFilePath, f.getPath());

    }

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
                apiCallForUploadVideo(videoFile);

            } else {
                progressDialog.dismiss();
                UploadIntroVideoActivity.uploadIntroVideoActivity.finalVideoFilePath = "";
                UploadIntroVideoActivity.uploadIntroVideoActivity.finalVideoUri = null;
                Constant.snackBar(binding.mainLayout, "Please select another video");
            }
        }
    }


    private void sendOtherData() {
        if (Constant.isNetworkAvailable(mContext, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.upload(BASE_URL + UPDATE_WORKER_PROFILEAPI)
                    //.addMultipartFile("profileImage",imageFile)
                    .addMultipartParameter(mPram)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
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
                                    ChangeModeApi();
                                    SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                                    //Log.e("sign up response", userResponce.getData().toString());
                                    PreferenceConnector.writeString(mContext, PreferenceConnector.COMPLETE_PROFILE_STATUS, "1");
                                    PreferenceConnector.writeString(mContext, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                                    PreferenceConnector.writeString(mContext, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                                    PreferenceConnector.writeString(mContext, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
                                    PreferenceConnector.writeString(mContext, PreferenceConnector.PROFILE_IMG, userResponce.getData().getProfileImage());
                                    PreferenceConnector.writeString(mContext, PreferenceConnector.Name, userResponce.getData().getName());
                                    PreferenceConnector.writeString(mContext, PreferenceConnector.Email, userResponce.getData().getEmail());
                                    //addUserFirebaseDatabase();

                                    getActivity().finishAffinity();
                                    Intent intent = new Intent(mContext, WorkerMainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();


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
                            Constant.errorHandle(anError, getActivity());
                            //  Log.e(TAG, anError.getErrorDetail());
                        }
                    });

        }
    }

    private void ChangeModeApi() {
        if (Constant.isNetworkAvailable(mContext, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + CHANGE_USER_MODE_API)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("user_mode", PreferenceConnector.readString(mContext, PreferenceConnector.USER_MODE, ""))
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
                                    if (PreferenceConnector.readString(mContext, PreferenceConnector.USER_MODE, "").equals("worker")) {
                                        PreferenceConnector.writeString(mContext, PreferenceConnector.USER_MODE, "client");
                                        getActivity().finishAffinity();
                                        Intent intent = new Intent(mContext, ClientMainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    } else {
                                        PreferenceConnector.writeString(mContext, PreferenceConnector.USER_MODE, "worker");
                                        getActivity().finishAffinity();
                                        Intent intent = new Intent(mContext, WorkerMainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }

                                } else {
                                    Constant.snackBar(binding.mainLayout, message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Constant.errorHandle(anError, getActivity());
                            progressDialog.dismiss();
                        }
                    });


        }
    }


    //""""start date picker dialog """""""""""""
    private void openDobDialog() {
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog startDateDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                startDateTime = Calendar.getInstance();
                startDateTime.set(Calendar.YEAR, year);
                startDateTime.set(Calendar.MONTH, month);
                startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //********Date time Format**************//
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                Log.e(TAG, "onDateSet: " + sdf2.format(startDateTime.getTime()));
                PreferenceConnector.writeString(mContext, PreferenceConnector.USER_DOB, sdf1.format(startDateTime.getTime()));

                startDateString = sdf1.format(startDateTime.getTime());
                binding.tvDob.setText(sdf2.format(startDateTime.getTime()));

            }
        }, mYear - 40, mMonth, mDay);

        Calendar c = Calendar.getInstance();
        c.set(mYear - 40, mMonth, mDay);//Year,Mounth -1,Day


        startDateDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        startDateDialog.show();

    }

    private void apiCallForUploadVideo(ArrayList<File> tmpFile) {
        VolleyMySingleton volleySingleton = new VolleyMySingleton(mContext);
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
                    Log.e("ADDVECH", "setResponse: " + response.toString());
                    JSONObject result;

                    result = new JSONObject(response.toString());
                    String status = result.getString("status");
                    String message = result.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        //*************success fully status**************//
                        ChangeModeApi();
                        SignUpResponce userResponce = new Gson().fromJson(String.valueOf(response), SignUpResponce.class);
                        PreferenceConnector.writeString(mContext, PreferenceConnector.COMPLETE_PROFILE_STATUS, "1");
                        PreferenceConnector.writeString(mContext, PreferenceConnector.MY_USER_ID, userResponce.getData().getUserId());
                        PreferenceConnector.writeString(mContext, PreferenceConnector.USER_TYPE, userResponce.getData().getUserType());
                        PreferenceConnector.writeString(mContext, PreferenceConnector.USER_MODE, userResponce.getData().getUser_mode());
                        PreferenceConnector.writeString(mContext, PreferenceConnector.PROFILE_IMG, userResponce.getData().getProfileImage());
                        PreferenceConnector.writeString(mContext, PreferenceConnector.Name, userResponce.getData().getName());
                        PreferenceConnector.writeString(mContext, PreferenceConnector.Email, userResponce.getData().getEmail());

                        // addUserFirebaseDatabase();
                        getActivity().finishAffinity();
                        Intent intent = new Intent(mContext, WorkerMainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        // finish();
                    } else {

                        Constant.snackBar(binding.mainLayout, message);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }, tmpFile, tmpFile.size(), profileImageFileList, profileImageFileList.size(), videoThumbFileList, mPram, mContext);

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


}
