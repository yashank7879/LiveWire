package com.livewire.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.livewire.R;
import com.livewire.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding =  DataBindingUtil.setContentView(this, R.layout.activity_setting);
      binding.llChangePass.setOnClickListener(this);
      binding.llAddBankAcc.setOnClickListener(this);
      binding.llAboutUs.setOnClickListener(this);
      binding.llTermsCondition.setOnClickListener(this);
      binding.llPrivacyPolicy.setOnClickListener(this);
      binding.llLogout.setOnClickListener(this);
      binding.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_switch:
                break;

            case R.id.ll_change_pass:
                break;

            case R.id.ll_add_bank_acc:
                break;

                case R.id.ll_about_us:
                break;

                case R.id.ll_terms_condition:
                break;

                case R.id.ll_privacy_policy:
                break;

                case R.id.ll_logout:

                break;

            case R.id.iv_back:
                onBackPressed();
                break;
                default:

        }
    }
}
