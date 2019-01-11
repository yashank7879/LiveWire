package com.livewire.ui.activity;

import android.databinding.DataBindingUtil;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;

import com.livewire.R;
import com.livewire.databinding.ActivityPlayVideoBinding;
import com.livewire.utils.Constant;
import com.livewire.utils.ProgressDialog;

public class PlayVideoActivity extends AppCompatActivity {
    ActivityPlayVideoBinding binding;
    private com.livewire.utils.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_video);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        if (Constant.isNetworkAvailable(this, binding.playvideoLayout)) {
            if (getIntent().getStringExtra("VideoUrlKey") != null) {
                Log.e("Video url98789: ", getIntent().getStringExtra("VideoUrlKey"));
                String url = getIntent().getStringExtra("VideoUrlKey");

                Uri uri = Uri.parse(getIntent().getStringExtra("VideoUrlKey"));

                // Uri uri = Uri.parse("https://livewire.work//uploads//introVideo//phRqVEZGj4gyxOe8.mp4");
                binding.videoView1.setVideoPath(getIntent().getStringExtra("VideoUrlKey"));
                binding.videoView1.requestFocus();
                binding.videoView1.start();
                MediaController ctlr = new MediaController(this);
                ctlr.setMediaPlayer(binding.videoView1);
                binding.videoView1.setMediaController(ctlr);
                binding.videoView1.requestFocus();
            }
        }

        binding.videoView1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressDialog.dismiss();
            }
        });

    }
}
