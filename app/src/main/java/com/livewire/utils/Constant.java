package com.livewire.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.androidnetworking.error.ANError;
import com.livewire.R;
import com.livewire.ui.activity.LoginActivity;
import com.livewire.ui.activity.UserSelectionActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.livewire.utils.ImageRotator.decodeBitmap;

/**
 * Created by mindiii on 9/7/18.
 */

public class Constant {
    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final int DEFAULT_MIN_HEIGHT_QUALITY = 400;
    private static final String TAG = Constant.class.getName();
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
    public static final boolean LOG_VALUE= true;
    public static String MY_UID= "";


    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 12;


    //FireBase....................................................
    public static final String ARG_USERS = "users";
    public static final String ARG_CHAT_ROOMS = "chat_rooms";
    public static final String ARG_GROUP_CHAT_ROOMS_DELETE = "event_chat_deleteMute";
    public static final String ARG_GROUP_CHAT_ROOMS = "group_chat_rooms";
    public static final String ARG_HISTORY = "chat_history";
    public static final String BlockUsers = "block_users";
    public static final String blockedBy = "blockedBy";
    public static String isNotification = "isNotification";

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
        if (activeNetworkInfo != null) {

            // if connected with internet
            return true;

        } else if (activeNetworkInfo == null) {

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

    //*********** day diffrence  *****************//
    public static String getDayDifference(String departDateTime, String curruntTime) {
        String returnDay = "";
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date startDate = simpleDateFormat.parse(departDateTime);
            Date endDate = simpleDateFormat.parse(curruntTime);

            //milliseconds
            long different = endDate.getTime() - startDate.getTime();


            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            if (elapsedDays == 0) {
                if (elapsedHours == 0) {
                    if (elapsedMinutes == 0) {
                        returnDay = /*elapsedSeconds +*/ " Just now";
                    } else {
                        returnDay = elapsedMinutes + " minutes ago";
                    }
                } else if (elapsedHours == 1) {
                    returnDay = elapsedHours + " hour ago";
                } else {
                    returnDay = elapsedHours + " hours ago";
                }
            } else if (elapsedDays == 1) {
                returnDay =  /*elapsedDays + */"Yesterday";
            } else {
                returnDay = elapsedDays + " days ago";
            }
        } catch (ParseException e) {
            Log.d("day diffrence", e.getMessage());
        }
        return returnDay;
    }

    public static String currentUtcTime() {
        String utcTime = null;
        String format = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        utcTime = sdf.format(new Date());

        return utcTime;
    }


    ////""""""""  increase height at run time  """"""//
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (listAdapter.getCount() % 2 == 0) {
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        } else {
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 20;
        }
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public static  String DateFomatChange(String startDate) {


    DateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
    String start = startDate;

            try

    {
        Date newStartDate;
        newStartDate = sd.parse(start);
        sd = new SimpleDateFormat("dd MMM yyyy");
        start = sd.format(newStartDate);

       // holder.tvDate.setText(dateTextColorChange(start));

    } catch(
    ParseException e)

    {
        Log.e(TAG, e.getMessage());
    }
    return start;
}
    public static SpannableStringBuilder dateTextColorChange(Context mContext, String start){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString userName = new SpannableString(start.substring(0,2)+" ");
        userName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 0, 2, 0);
        userName.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        builder.append(userName);
        SpannableString interesString = new SpannableString(start.substring(3));
//                interesString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorDarkBlack)), 3, start.length(), 0);
        builder.append(interesString);
        return  builder;
    }

    public static void errorHandle(ANError error, Activity activity) {
      //  NetworkResponse networkResponse = error.networkResponse;
        String errorMessage = "Unknown error";
            String result = new String(error.getErrorBody());
            try {
                JSONObject response = new JSONObject(result);

                String status = response.getString("responseCode");
                String message = response.getString("message");

                if (status.equals("300")) {
                    if (activity != null) {
                        showAlertDialog(activity,"Please Login Again","Session Expired","LogOut");
                    }
                }

                Log.e("Error Status", "" + status);
                Log.e("Error Message", message);

                if (status.equals("404")) {
                    errorMessage = "Resource not found";
                } else if (status.equals("401")) {
                    errorMessage = message + " Please login again";
                } else if (status.equals("400")) {
                    errorMessage = message + " Check your inputs";
                } else if (status.equals("500")) {
                    errorMessage = message + " Something is getting wrong";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (activity != null) {

                }
            }
        }


    public static void showAlertDialog(final Activity con, String msg, String title, String ok){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
        builder1.setTitle(title);
        builder1.setMessage(msg);
        builder1.setCancelable(false);
        builder1.setPositiveButton(ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        logout(con);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private static void logout(Activity con) {
        PreferenceConnector.writeBoolean(con, PreferenceConnector.IS_LOG_IN, false);
        PreferenceConnector.writeString(con, PreferenceConnector.USER_INFO_JSON, "");
        PreferenceConnector.writeString(con, PreferenceConnector.AUTH_TOKEN, "");
        PreferenceConnector.writeString(con, PreferenceConnector.USER_TYPE, "");
        PreferenceConnector.writeString(con, PreferenceConnector.COMPLETE_PROFILE_STATUS, "");
        PreferenceConnector.writeString(con, PreferenceConnector.PASS_WORD, "");
        PreferenceConnector.writeString(con, PreferenceConnector.SOCIAL_LOGIN, "");
        Intent intent = new Intent(con, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        con.startActivity(intent);
        con.finish();
       con.overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in);
    }

    public static void printLogMethod(boolean value, String tag ,String msg ){
        if (value){
            Log.e(tag,msg);
        }
    }


    public static void openAlertDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("LiveWire");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();

        if(!((Activity) context).isFinishing())
        {
            alert.show();
        }

    }
}
