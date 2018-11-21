package com.livewire.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.livewire.R;
import com.livewire.databinding.ActivityCompleteSingleJobDetailClientBinding;

public class CompleteSingleJobDetailClientActivity extends AppCompatActivity {
    ActivityCompleteSingleJobDetailClientBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = DataBindingUtil.setContentView(this,R.layout.activity_complete_single_job_detail_client);
    }
}
