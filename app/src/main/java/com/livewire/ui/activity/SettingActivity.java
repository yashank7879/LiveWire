package com.livewire.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.livewire.R;
import com.livewire.databinding.ActivitySettingBinding;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;
import com.livewire.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.USER_LOGOUT_API;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySettingBinding binding;
    private int width;
    private ProgressDialog progressDialog;
    private Animation shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        progressDialog = new ProgressDialog(this);
        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanim);
        binding.llChangePass.setOnClickListener(this);
        binding.llAddBankAcc.setOnClickListener(this);
        binding.llAboutUs.setOnClickListener(this);
        binding.llTermsCondition.setOnClickListener(this);
        binding.llPrivacyPolicy.setOnClickListener(this);
        binding.llLogout.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        TextView tvBankAcc = findViewById(R.id.tv_bank_account);

        if (PreferenceConnector.readString(this, PreferenceConnector.USER_TYPE, "").equals("worker")) {// show Bank acc
            if (PreferenceConnector.readString(this, PreferenceConnector.IS_BANK_ACC, "").equals("1")) {
                tvBankAcc.setText("Edit BAnk Account");
            }
                binding.llAddBankAcc.setVisibility(View.VISIBLE);
            binding.rlAvailable.setVisibility(View.VISIBLE);
        } else if (PreferenceConnector.readString(this, PreferenceConnector.USER_TYPE, "").equals("client")) {// show credit card
            binding.llAddCreditCard.setVisibility(View.VISIBLE);
        }

        if (!PreferenceConnector.readString(this, PreferenceConnector.SOCIAL_LOGIN, "").isEmpty()) {
            binding.llChangePass.setVisibility(View.GONE);
        } else {
            binding.llChangePass.setVisibility(View.VISIBLE);
        }


        binding.btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {// user available
                    availablityUser("1");
                } else {// user unavailable
                    availablityUser("0");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_change_pass:
                openChnagePassDialog();
                break;

            case R.id.ll_add_bank_acc:
                Intent intent = new Intent(this, AddBankAccountActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_about_us:
                break;

            case R.id.ll_terms_condition:
                break;

            case R.id.ll_privacy_policy:
                break;

            case R.id.ll_logout:
                showDialogforLogout();

                break;

            case R.id.iv_back:
                onBackPressed();
                break;
            default:

        }
    }

    //""""""""""""""  Log out Dialog """"""""""""//
    private void showDialogforLogout() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutApiCalling();

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void logoutApiCalling() {

        if (Constant.isNetworkAvailable(this, binding.settingMainLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + USER_LOGOUT_API)
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {

                            PreferenceConnector.clear(SettingActivity.this);
                            finishAffinity();
                            Intent intent = new Intent(SettingActivity.this, UserSelectionActivity.class);
                            startActivity(intent);
                            finish();
                            SettingActivity.this.overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in);

                        } else {

                            Constant.snackBar(binding.settingMainLayout, message);
                        }
                    } catch (JSONException e) {
                        Log.d("Exception", e.getMessage());
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Log.d("EXception", anError.getErrorDetail());
                    progressDialog.dismiss();
                }
            });
        }
    }

    //"""""""""' open dialog for change password """""""""""//
    private void openChnagePassDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.change_pass_dialog);
        dialog.getWindow().setLayout((width * 10) / 11, WindowManager.LayoutParams.WRAP_CONTENT);
        final EditText etCurrentPass = dialog.findViewById(R.id.et_current_pass);
        final EditText etNewPass = dialog.findViewById(R.id.et_new_pass);
        final EditText etConfirmPass = dialog.findViewById(R.id.et_confirm_pass);
        final RelativeLayout dilogParentLayout = dialog.findViewById(R.id.change_pass_layout);
        Button btnDone = dialog.findViewById(R.id.btn_done);
        TextView cancel = dialog.findViewById(R.id.tv_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.hideSoftKeyBoard(SettingActivity.this, etNewPass);
                dialogValidation(etCurrentPass, etNewPass, etConfirmPass, dilogParentLayout, dialog);
            }
        });

        dialog.show();
        dialog.setCancelable(false);

    }

    private void dialogValidation(EditText etCurrentPass, EditText etNewPass, EditText etConfirmPass, RelativeLayout dilogParentLayout, Dialog dialog) {
        if (Validation.isEmpty(etCurrentPass)) {
            Constant.snackBar(dilogParentLayout, "Please Enter Current Password");
            etCurrentPass.startAnimation(shake);
        } else if (!etCurrentPass.getText().toString().trim().equals(PreferenceConnector.readString(this, PreferenceConnector.PASS_WORD, ""))) {
            Constant.snackBar(dilogParentLayout, "Please Enter Current Password");
            etCurrentPass.requestFocus();
            etCurrentPass.startAnimation(shake);
        } else if (Validation.isEmpty(etNewPass)) {
            Constant.snackBar(dilogParentLayout, "Please Enter New Password");
            etNewPass.requestFocus();
            etNewPass.startAnimation(shake);
        } else if (etNewPass.getText().toString().trim().length() < 6) {
            Constant.snackBar(dilogParentLayout, "New Password should be 6 character");
            etNewPass.requestFocus();
            etNewPass.startAnimation(shake);
        } else if (Validation.isEmpty(etConfirmPass)) {
            Constant.snackBar(dilogParentLayout, "Please Again Enter New Password");
            etConfirmPass.requestFocus();
            etConfirmPass.startAnimation(shake);
        } else if (!etNewPass.getText().toString().equals(etConfirmPass.getText().toString())) {
            Constant.snackBar(dilogParentLayout, "Please Enter New Password");
            etConfirmPass.requestFocus();
            etConfirmPass.startAnimation(shake);
        } else if (etCurrentPass.getText().toString().trim().equals(etConfirmPass.getText().toString().trim())) {
            Constant.snackBar(dilogParentLayout, "Current Password and new Password can't be same");
            etNewPass.requestFocus();
            etNewPass.startAnimation(shake);
        } else {
            newPassApiCalling(dialog, dilogParentLayout, etCurrentPass, etNewPass, etConfirmPass);
        }
    }

    private void newPassApiCalling(final Dialog dialog, final RelativeLayout dilogParentLayout, EditText etCurrentPass, final EditText etNewPass, EditText etConfirmPass) {
        if (Constant.isNetworkAvailable(this, dilogParentLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "user/changePassword")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("oldPassword", etCurrentPass.getText().toString().trim())
                    .addBodyParameter("newPassword", etNewPass.getText().toString().trim())
                    //.addBodyParameter("confirmPassword", etConfirmPass.getText().toString().trim())
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            Toast.makeText(SettingActivity.this, "Your Password Successfully Changed", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            PreferenceConnector.writeString(SettingActivity.this, PreferenceConnector.PASS_WORD, etNewPass.getText().toString());
                            // clientJobId
                            // Constant.snackBar(mainLayout,"Your job successfully post");
                        } else {

                            Constant.snackBar(dilogParentLayout, message);
                        }
                    } catch (JSONException e) {
                        Log.d("Exception", e.getMessage());
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Log.d("EXception", anError.getErrorDetail());
                    progressDialog.dismiss();
                }
            });
        }


    }


    private void availablityUser(String s) {
        if (Constant.isNetworkAvailable(this, binding.settingMainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + "user/availability")
                    .addHeaders("authToken", PreferenceConnector.readString(this, PreferenceConnector.AUTH_TOKEN, ""))
                    .addBodyParameter("availability", s)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {

                            Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {

                            Constant.snackBar(binding.settingMainLayout, message);
                        }
                    } catch (JSONException e) {
                        Log.d("Exception", e.getMessage());
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Log.d("EXception", anError.getErrorDetail());
                    progressDialog.dismiss();
                }
            });
        }
    }

}