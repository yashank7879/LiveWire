package com.livewire.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class PreferenceConnector {
    public static final String PREF_NAME = "LiveWire";
    public static final int MODE = Context.MODE_PRIVATE;
    //----------session-----//

    public static final String REM_LOGIN_INFO = "LoginInfo";

    //----UserInfo

    public static final String USER_INFO_JSON = "userInfoJson";
    public static final String STRIPE_CUSTOMER_ID = "StripeCustomer";
    public static final String IS_LOG_IN = "IsLogIN";
    public static final String COMPLETE_PROFILE_STATUS = "CompleteProfile";
    public static final String PASS_WORD = "Password";
    public static final String IS_BANK_ACC = "BnakAcc";
    public static final String SOCIAL_LOGIN = "Social";
    public static final String STRIPE_FEES = "StripeFees";
    public static final String STRIPE_TRANSACTION_FEES = "StripeTransactionFees";


    public static final String Email = "email";
    public static final String AUTH_TOKEN = "AuthToken1";
    public static final String USER_TYPE = "userType";
    public static final String USER_MODE = "userMode";
    public static final String MY_USER_ID = "MyUserId";
    public static final String PROFILE_IMG = "ProfileImg";
    public static final String Name = "name";



    public static final String SELECTED_VIDEO = "SELECTED_VIDEO";
    public static final String USER_DOB = "USER_DOB";
    public static final String ABOUT_ME = "ABOUT_ME";





    public static void writeInteger(Context context, String key, int value) {
        getEditor(context).putInt(key, value).commit();

    }

    public static int readInteger(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();

    }

    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static void writeBoolean(Context context, String key, Boolean value) {
        getEditor(context).putBoolean(key, value).commit();

    }

    public static Boolean readBoolean(Context context, String key,
                                      Boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void clear(Context context) {

        getEditor(context).clear().commit();
    }


}
