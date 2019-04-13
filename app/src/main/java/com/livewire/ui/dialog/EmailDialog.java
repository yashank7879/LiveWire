package com.livewire.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.livewire.R;
import com.livewire.databinding.DialogEmailBinding;
import com.livewire.databinding.DialogLocationBinding;
import com.livewire.utils.Constant;
import com.livewire.utils.Validation;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by mindiii on 11/15/18.
 */

public class EmailDialog extends DialogFragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = EmailDialog.class.getName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 55;
    DialogEmailBinding binding;
    LocationDialogListner listner;
    private Context mContext;
    private LatLng locationLatLng;
    private String locationPlace = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_email, container, false);
        //  View view = inflater.inflate(R.layout.dialog_review, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSubmit.setOnClickListener(this);
        binding.etEmail.setOnClickListener(this);

        // Toast.makeText(mContext, ""+binding.ratingBar.getRating(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                Constant.hideSoftKeyBoard(mContext, binding.etEmail);
                reviewValidations();
                break;

            default:
        }
    }



    public void getEmailInfo(LocationDialogListner listner) {
        this.listner = listner;
    }


    private void reviewValidations() {
        if (Validation.isEmpty(binding.etEmail)) {
            Constant.snackBar(binding.reviewLayout, "Please Enter Your Email id");
        } else {
            listner.openEmailIdDialog(binding.etEmail.getText().toString().trim());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public interface LocationDialogListner {
        void openEmailIdDialog(String email);
    }
}
