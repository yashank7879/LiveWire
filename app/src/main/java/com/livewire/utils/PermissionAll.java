package com.livewire.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by mindiii on 5/11/18.
 */

public class PermissionAll {// camera parmission
    public boolean chackCameraPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]
                            {Manifest.permission.CAMERA},
                    Constant.MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.CAMERA}, Constant.REQUESTPERMISSIONCODE);
            return true;
        }
    }

    public boolean checkAudioPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context , Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    Constant.RECORD_AUDIO);
            return false;
        } else {
            return true;
        }
    }
    public boolean checkWriteStoragePermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }
    public boolean RequestMultiplePermission(Activity context) {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int ForthPermissionResult = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);

        if (FirstPermissionResult != PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult != PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult != PackageManager.PERMISSION_GRANTED&&
                ForthPermissionResult != PackageManager.PERMISSION_GRANTED){
            // No explanation needed, we can request the permission.
            // Creating String Array with Permissions.
            ActivityCompat.requestPermissions((Activity) context, new String[]
                    {
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO
                    }, Constant.REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        } else {
            return true;
        }
    }
}
