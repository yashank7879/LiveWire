package com.livewire.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.livewire.R;
import com.livewire.cropper.CropImage;
import com.livewire.cropper.CropImageView;
import com.livewire.databinding.ActivityUploadIntroVideoBinding;
import com.livewire.model.IntroVideoModal;
import com.livewire.ui.fragments.WriteSomthingAboutWorkerFragment;
import com.livewire.utils.Constant;
import com.livewire.utils.ImageRotator;
import com.livewire.utils.ImageVideoUtils;
import com.livewire.utils.PreferenceConnector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class UploadIntroVideoActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityUploadIntroVideoBinding binding;
    public Uri finalVideoUri;
    public String finalVideoFilePath;
    public File videoThumbFile;
    public ArrayList<File> videoFile;
    public  String keySkills;
   public static UploadIntroVideoActivity uploadIntroVideoActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_intro_video);
         keySkills = getIntent().getStringExtra("Skills");

        binding.ivBack.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        binding.rlVideo.setOnClickListener(this);
        binding.btnSkip.setOnClickListener(this);
        binding.ivRemoveVideo.setOnClickListener(this);

        if (!PreferenceConnector.readString(this,PreferenceConnector.SELECTED_VIDEO,"").isEmpty()){
            String videoFilePath = ImageVideoUtils.generatePath(Uri.parse(PreferenceConnector.readString(this,PreferenceConnector.SELECTED_VIDEO,"")), this);
            finalVideoUri = Uri.parse(PreferenceConnector.readString(this,PreferenceConnector.SELECTED_VIDEO,""));
            finalVideoFilePath = videoFilePath;
            Bitmap thumbBitmap = ImageVideoUtils.getVidioThumbnail(finalVideoFilePath); //ImageVideoUtil.getCompressBitmap();
            //thumbBitmap = ImageVideoUtils.getVideoToThumbnil(videoFilePath, this);

            int rotation = ImageRotator.getRotation(this, finalVideoUri, true);
            thumbBitmap = ImageRotator.rotate(thumbBitmap, rotation);
            binding.ivRound.setImageBitmap(thumbBitmap);
            binding.ivRemoveVideo.setVisibility(View.VISIBLE);
        }
        uploadIntroVideoActivity = this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:

                if (finalVideoUri == null ) {
                    Constant.snackBar(binding.mainLayout,getString(R.string.please_add_intro_video));
                }else {
                    replaceFragment(new WriteSomthingAboutWorkerFragment(), true, R.id.fl_container);
                }
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_cancel:
                finishAffinity();
                Intent intent = new Intent(this, ClientMainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_skip:
                replaceFragment(new WriteSomthingAboutWorkerFragment(), true, R.id.fl_container);
                break;
            case R.id.rl_video: {
                if (finalVideoUri != null) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse(finalVideoFilePath);
                    i.setDataAndType(data, "video/*");
                    i.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
                    startActivity(i);
                } else showSetIntroVideoDialog();
            }
            break;
            case R.id.iv_remove_video: {
                binding.ivRemoveVideo.setVisibility(View.GONE);
                binding.ivRound.setImageDrawable(ContextCompat.getDrawable(this, R.color.colorWhite));
                binding.tvAddVideo.setVisibility(View.VISIBLE);
                finalVideoFilePath ="";
                finalVideoUri= null;
                PreferenceConnector.writeString(this,PreferenceConnector.SELECTED_VIDEO, "");

            }break;

        }
    }

    private void showSetIntroVideoDialog() {

        final CharSequence[] options = {"Take Video", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadIntroVideoActivity.this);

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


    //"""""   On ActivityResult   """""""""
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

                    if (fileSizeInMB < 30){
                        finalVideoUri = videoUri;
                        finalVideoFilePath = videoFilePath;
                        thumbBitmap = ImageVideoUtils.getVidioThumbnail(finalVideoFilePath); //ImageVideoUtil.getCompressBitmap();
                        //thumbBitmap = ImageVideoUtils.getVideoToThumbnil(videoFilePath, this);

                        int rotation = ImageRotator.getRotation(this, finalVideoUri, true);
                        thumbBitmap = ImageRotator.rotate(thumbBitmap, rotation);


                        videoThumbFile = savebitmap(this, thumbBitmap, UUID.randomUUID() + ".jpg");
                        IntroVideoModal carsImageBean = new IntroVideoModal();
                        carsImageBean.setmUri(finalVideoUri);
                        carsImageBean.setmFile(videoFile);
                        carsImageBean.setUrl(false);
                        carsImageBean.setFileType("video");
                        carsImageBean.setThumbBitmap(thumbBitmap);
                        File file = new File("");
                        carsImageBean.setThumbFile((videoThumbFile == null) ? file : videoThumbFile);

                        //"""""" Save video path in session """""""///
                        PreferenceConnector.writeString(this,PreferenceConnector.SELECTED_VIDEO, String.valueOf(videoUri));
                        binding.ivRound.setImageBitmap(thumbBitmap);
                        binding.tvAddVideo.setVisibility(View.GONE);
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

    }

    // from bitmap to file creater"""""""""""
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

    //*********   replace Fragment  ************//
    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.add(containerId, fragment, backStackName);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }

    }

}
