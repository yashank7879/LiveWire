package com.livewire.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.adapter.NearYouClientPagerAdpter;
import com.livewire.databinding.FragmentNearByClientBinding;
import com.livewire.responce.NearByResponce;
import com.livewire.ui.activity.EditProfileClientActivity;
import com.livewire.ui.activity.MyProfileClientActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.MyApplication;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.NEAR_BY_USER_API;

public class NearByClientFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private FragmentNearByClientBinding binding;
    private Context mContext;
    private ProgressDialog progressDialog;
    private List<NearByResponce.DataBean> nearByList;
    private int clickId;
    private String message = "";
    private NearByResponce nearByResponce;
    private FusedLocationProviderClient mFusedLocationClient;

    private Location location;
    private GoogleApiClient googleApiClient;
    //private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request //
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    Boolean flag = true;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public final static int REQUEST_CHECK_SETTINGS_GPS = 96;

    @NonNull
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);

                setLatLng(location);
            }
        }
    };

    private void setLatLng(Location location) {
        MyApplication.latitude = location.getLatitude();
        MyApplication.longitude = location.getLongitude();
        Log.e("Current location : ",""+ location.getLatitude()+"  "+location.getLongitude());
    }

    public NearByClientFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_near_by_client, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        nearByList = new ArrayList<>();
        progressDialog = new ProgressDialog(mContext);


        binding.tvNearYou.setOnClickListener(this);
        binding.tvMapview.setOnClickListener(this);


        getCurrentLocation();

        // getCurrentLocation();
        // getNearByListApi();
       /* FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_container1, new NearYouClientFragment());
        transaction.commit();
        clickId = R.id.tv_near_you;*/
    }

    private void getNearByListApi() {
        if (Constant.isNetworkAvailable(mContext, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.post(BASE_URL + NEAR_BY_USER_API)
                    .addBodyParameter("latitude", "" + location.getLatitude())
                    .addBodyParameter("longitude", "" +location.getLongitude())
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            String status = null;
                            try {//22.705033,75.909497
                                if (flag) {
                                    flag = false;
                                    status = response.getString("status");
                                    message = response.getString("message");
                                    if (status.equals("success")) {
                                        nearByResponce = new Gson().fromJson(String.valueOf(response), NearByResponce.class);
                                      //  checkVisibleFragmnet(response);
                                       nearByAdapter();
                                       // Log.e("onViewCreated: ",""+binding.viewpager.getCurrentItem() );
                                    } else {
                                        Constant.snackBar(binding.mainLayout, message);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            progressDialog.dismiss();
                            Constant.errorHandle(anError, getActivity());
                        }
                    });
        }
    }

    private void nearByAdapter() {
        NearYouClientPagerAdpter adapter = new NearYouClientPagerAdpter(getChildFragmentManager(),getActivity(),message,nearByResponce);
        binding.viewpager.setAdapter(adapter);
        binding.slidingTabs.setupWithViewPager(binding.viewpager);
        // decrease width of indicator
        for (int i = 0; i < binding.slidingTabs.getTabCount(); i++) {
            View tab = ((ViewGroup) binding.slidingTabs.getChildAt(0)).getChildAt(i);

            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(15, 0, 15, 0);
            tab.requestLayout();
        }

        final Typeface tf = ResourcesCompat.getFont(mContext,R.font.poppins_medium);
        final Typeface rl = ResourcesCompat.getFont(mContext,R.font.poppins_regular);


        //""""" Zero position selected so change fontFamily """""""""""//
        for (int i=0;i<binding.slidingTabs.getTabCount();i++){
            LinearLayout tabLayout1 = (LinearLayout) ((ViewGroup) binding.slidingTabs.getChildAt(0)).getChildAt(i);
            TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
            tabTextView.setAllCaps(false);
            tabTextView.setTypeface(tf);
        }

    }

    private void checkVisibleFragmnet(JSONObject response) {
        Fragment visible = getCurrentFragment();
        if (visible instanceof MapviewClientFragment) {
            binding.viewpager.setCurrentItem(1);

            /*
            if (message.equals("Please update your location to know Live Wires near you.")) {
                replaceFragment(new MapviewClientFragment().newInstance(message, nearByResponce), false, R.id.fl_container1);
                clickId = R.id.tv_mapview;
            } else if (message.equals("No near by user found")) {
                replaceFragment(new MapviewClientFragment().newInstance(message, nearByResponce), false, R.id.fl_container1);
                clickId = R.id.tv_mapview;
            } else {
                nearByResponce = new Gson().fromJson(String.valueOf(response), NearByResponce.class);
                replaceFragment(new MapviewClientFragment().newInstance(message, nearByResponce), false, R.id.fl_container1);
                clickId = R.id.tv_mapview;
            }*/
        } else {
            binding.viewpager.setCurrentItem(0);
           /* if (message.equals("Please update your location to know Live Wires near you.")) {
                replaceFragment(new NearYouClientFragment().newInstance(message, nearByResponce), false, R.id.fl_container1);
                clickId = R.id.tv_near_you;
            } else if (message.equals("No near by user found")) {
                replaceFragment(new NearYouClientFragment().newInstance(message, nearByResponce), false, R.id.fl_container1);
                clickId = R.id.tv_near_you;
            } else {
                nearByResponce = new Gson().fromJson(String.valueOf(response), NearByResponce.class);
                replaceFragment(new NearYouClientFragment().newInstance(message, nearByResponce), false, R.id.fl_container1);
                clickId = R.id.tv_near_you;
            }*/
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_profile: {
                Intent intent = new Intent(mContext, MyProfileClientActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_update:
                startActivity(new Intent(mContext, EditProfileClientActivity.class));
                break;
            case R.id.tv_near_you:
                if (clickId != R.id.tv_near_you ) {
                    binding.tvNearYou.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    binding.tvNearYou.setBackground(ContextCompat.getDrawable(mContext, R.drawable.map_round_bg));
                    binding.tvMapview.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                    binding.tvMapview.setBackground(null);
                    replaceFragment(new NearYouClientFragment().newInstance(message,nearByResponce), false, R.id.fl_container1);
                    clickId = R.id.tv_near_you;
                }
                break;
            case R.id.tv_mapview:
                if (clickId != R.id.tv_mapview ) {
                    binding.tvMapview.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    binding.tvMapview.setBackground(ContextCompat.getDrawable(mContext, R.drawable.map_round_bg));
                    binding.tvNearYou.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                    binding.tvNearYou.setBackground(null);
                    replaceFragment(new MapviewClientFragment().newInstance(message,nearByResponce), false, R.id.fl_container1);
                    clickId = R.id.tv_mapview;
                }
                break;
            default:
        }
    }


    //*********   replace Fragment  ************//
    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_container1, fragment);
        transaction.commit();

    }

    Fragment getCurrentFragment()
    {
        Fragment currentFragment = getFragmentManager()
                .findFragmentById(R.id.fl_container1);
        return currentFragment;
    }






    private void getCurrentLocation() {

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(mContext).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
    }


    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(mContext,permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (location != null)
            //getNearByListApi();

        if (!checkPlayServices()) {
            Toast.makeText(getActivity(), "You need to install Google Play Services to use the App properly", Toast.LENGTH_SHORT).show();
            //locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                //finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {

            getNearByListApi();
            setLatLng(location);
            // Toast.makeText(mContext, ""+"Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            // locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }else {
            //Location not available
            onGpsAutomatic();
        }

        startLocationUpdates();
    }

    private void onGpsAutomatic() {
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

            locationRequest = new LocationRequest();
            locationRequest.setInterval(3000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            builder.setNeedBle(true);

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());

            Task<LocationSettingsResponse> task =
                    LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());

            task.addOnCompleteListener(task1 -> {
                try {
                    //getting target response use below code
                    LocationSettingsResponse response = task1.getResult(ApiException.class);

                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    int permissionLocation1 = ContextCompat
                            .checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION);
                    if (permissionLocation1 == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(getActivity(), location -> {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        setLatLng(location);
                                    } else {
                                        //Location not available
                                        Log.e("Test", "Location not available");
                                      //  AppLogger.e("Test", "Location not available");
                                    }
                                });
                    }
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        getActivity(),
                                        REQUEST_CHECK_SETTINGS_GPS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //  Toast.makeText(mContext, ""+"Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(), Toast.LENGTH_SHORT).show();

            //locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(mContext).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }


}
