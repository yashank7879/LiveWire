package com.livewire.utils;

import android.text.Editable;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mindiii on 5/10/18.
 */

public class Validation {
    private final static String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
    private final static String FULLNAME_PATTERN = "^[\\p{L} .'-]+$";

    private final static String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

    private static String getString(TextView textView){
        return textView.getText().toString().trim();
    }

    @Deprecated // "use isEmpty instead of the isNullValue"
    public static boolean isNullValue(TextView textView){
        if(getString(textView).isEmpty()){

            return false;
        }
        textView.setError(null);
        return true;
    }

    public static boolean isEmpty(TextView textView){
        if(getString(textView).isEmpty()){

            textView.requestFocus();
            return true;
        }
        return false;
    }

    public static boolean isUserNameValid(EditText textView){
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(getString(textView));
        boolean bool = matcher.matches();
        if(!bool){
            textView.setError("enter valid userName");
            textView.requestFocus();
        }
        return bool;
    }

    public static boolean isFullNameValid(TextView textView){
        Pattern pattern = Pattern.compile(FULLNAME_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(getString(textView));
        return matcher.find();
    }




    /*public static boolean isEmailValid(TextView textView) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(getString(textView)).matches();
    }*/

    public static boolean isEmailValid(TextView textView) {

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(getString(textView));
        /* if (!bool) {
          //  isValid = true;
        }*/
        return matcher.matches();
    }


}
