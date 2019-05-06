package com.livewire.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.livewire.R;

public class ProgressDialog extends Dialog {
    Context context;
    public ProgressDialog(Context context) {
        super(context);
        this.context = context;
        // This is the fragment_search_details XML file that describes your Dialog fragment_search_details
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setCancelable(false);
        this.setContentView(R.layout.activity_progress_dialog);
    }
}
