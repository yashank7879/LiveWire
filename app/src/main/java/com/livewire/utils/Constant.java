package com.livewire.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.livewire.R;

import static com.livewire.utils.ImageRotator.decodeBitmap;

/**
 * Created by mindiii on 9/7/18.
 */

public class Constant {
    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final int DEFAULT_MIN_HEIGHT_QUALITY = 400;
    private static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;
    private static int minHeightQuality = DEFAULT_MIN_HEIGHT_QUALITY;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    public static final int REQUESTPERMISSIONCODE = 2;
    public static final int REQUEST_VIDEO_CAPTURE = 300;
    public static final int CAMERA = 5;
    public static final int GALLERY = 6;
    public static final int PROGRESS_BAR_MAX = 1000;
    public static final int SELECT_VIDEO_REQUEST = 0;
    public static final int RECORD_AUDIO = 15;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 13;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 12;

    //"""""""its used for live wire text""""""""//
   public static SpannableStringBuilder liveWireText(Context mContext) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString Name1 = new SpannableString("Live");
        Name1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorGreen)), 0, Name1.length(), 0);
        builder.append(Name1);
        SpannableString interesString = new SpannableString("\nWire");
        builder.append(interesString);
        return builder;
    }

    //************snack bar**************//
    public static void snackBar(View loginLayout, String message) {
        Snackbar snackbar = Snackbar.make(loginLayout, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(loginLayout.getResources().getColor(R.color.colorBlack));
        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(0xFF049a43);
        snackbar.show();
    }

    //*****************check for network connection******************//
    public static boolean isNetworkAvailable(Context context, View coordinatorLayout) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // Check for network connections
        if (activeNetworkInfo!=null) {

            // if connected with internet
            return true;

        } else if (activeNetworkInfo==null) {

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_SHORT)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*not used*/
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
            return false;
        }
        return false;
    }

    //""""""""image Resize"""""""""/
    public static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            i++;
        } while (bm != null
                && (bm.getWidth() < minWidthQuality || bm.getHeight() < minHeightQuality)
                && i < sampleSizes.length);
        Log.i("FragmentFeed", "Final bitmap width = " + (bm != null ? bm.getWidth() : "No final bitmap"));
        return bm;
    }


    //**************hide keyboard********************//
    public static void hideSoftKeyBoard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}