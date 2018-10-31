package com.livewire.ui.activity;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import com.livewire.R;
import com.livewire.databinding.ActivityPlayVideoBinding;
import com.livewire.utils.Constant;
import com.squareup.picasso.Picasso;

public class PlayVideoActivity extends AppCompatActivity {
    ActivityPlayVideoBinding binding;
    private MediaController ctlr;
    private com.livewire.utils.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_video);
        progressDialog = new com.livewire.utils.ProgressDialog(this);
        progressDialog.show();
        progressDialog.setCancelable(true);

        if (Constant.isNetworkAvailable(this, binding.playvideoLayout)) {
            if (getIntent().getStringExtra("VideoUrlKey") != null) {
                Uri uri = Uri.parse(getIntent().getStringExtra("VideoUrlKey"));
                binding.videoView1.setVideoURI(uri);
                binding.videoView1.start();
                ctlr = new MediaController(this);
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
