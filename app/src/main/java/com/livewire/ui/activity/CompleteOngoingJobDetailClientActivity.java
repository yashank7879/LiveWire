package com.livewire.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.livewire.R;
import com.livewire.databinding.ActivityCompleteOngoingJobDetailClientBinding;

public class CompleteOngoingJobDetailClientActivity extends AppCompatActivity {
    ActivityCompleteOngoingJobDetailClientBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complete_ongoing_job_detail_client);

    }
}
