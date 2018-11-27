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
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.livewire.R;
import com.livewire.databinding.DialogLocationBinding;
import com.livewire.databinding.DialogReviewBinding;
import com.livewire.ui.activity.SignupActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.Validation;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by mindiii on 11/15/18.
 */

public class LocationDialog extends DialogFragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LocationDialog.class.getName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 55;
    DialogLocationBinding binding;
    LocationDialogListner listner;
    private Context mContext;
    private LatLng locationLatLng;
    private String locationPlace = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_location, container, false);
        //  View view = inflater.inflate(R.layout.dialog_review, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnSubmit.setOnClickListener(this);
        binding.tvLocation.setOnClickListener(this);

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
                Constant.hideSoftKeyBoard(mContext, binding.tvLocation);
                reviewValidations();

                break;
            case R.id.tv_location:
                autoCompletePlacePicker();
                break;

            default:
        }
    }

    /*******Auto comple place picker***************/
    private void autoCompletePlacePicker() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void getLocationInfo(LocationDialogListner listner) {
        this.listner = listner;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //*********Autocomplete place p[icker****************//
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                assert data != null;
                Place place = PlaceAutocomplete.getPlace(mContext, data);
                binding.tvLocation.setText(place.getAddress());
                locationPlace = place.getAddress().toString();
                locationLatLng = place.getLatLng();
                Log.e(TAG, "Place: " + place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status1 = PlaceAutocomplete.getStatus(mContext, data);

                Log.i(TAG, status1.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {

                Constant.hideSoftKeyBoard(mContext, binding.tvLocation);
            }
        }
    }

    private void reviewValidations() {
        if (Validation.isEmpty(binding.tvLocation)) {
            Constant.snackBar(binding.reviewLayout, "Please Enter Your Location");
        } else {
            listner.onLocationOnClick(binding.tvLocation.getText().toString().trim(), locationLatLng, binding.reviewLayout);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public interface LocationDialogListner {
        void onLocationOnClick(String town, LatLng latLng, LinearLayout layout);
    }
}
