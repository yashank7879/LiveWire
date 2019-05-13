package com.livewire.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.livewire.R;
import com.livewire.databinding.FragmentMapviewBinding;
import com.livewire.responce.NearByResponce;
import com.livewire.ui.activity.EditProfileClientActivity;
import com.livewire.utils.Constant;
import com.livewire.utils.PreferenceConnector;
import com.livewire.utils.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.livewire.utils.ApiCollection.BASE_URL;
import static com.livewire.utils.ApiCollection.NEAR_BY_USER_API;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapviewClientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapviewClientFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, InfoWindowManager.WindowShowListener {
    private static final String TAG = MapviewClientFragment.class.getName();
    // TODO: Rename parameter arguments, choose names that match
    FragmentMapviewBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;
    private GoogleMap mMap;
    private InfoWindow formWindow;
    private static final String FORM_VIEW = "FORM_VIEW_MARKER";
    private InfoWindowManager infoWindowManager;
    private ProgressDialog progressDialog;
    private List<NearByResponce.DataBean> nearByList;
    private List<Marker> markerList;
    private List<InfoWindow> infoWindowList;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 11;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private NearByResponce userData;
    private String message;

    public MapviewClientFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MapviewClientFragment newInstance(String mesage, NearByResponce nearByResponce) {
        Bundle args = new Bundle();
        MapviewClientFragment fragment = new MapviewClientFragment();
        args.putSerializable(ARG_PARAM1, mesage);
        args.putSerializable(ARG_PARAM2, nearByResponce);
        fragment.setArguments(args);
        return fragment;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getSerializable(ARG_PARAM2) != null) {
                userData = (NearByResponce) getArguments().getSerializable(ARG_PARAM2);
                //  binding.rlMap.setVisibility(View.GONE);
            }

            message = (String) getArguments().getSerializable(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mapview, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLocationPermission();
        currentlocation();
        assert message != null;
        if (message.equals("Please update your location to know Live Wires near you.")) {
            binding.rlUpdateProfile.setVisibility(View.VISIBLE);
            binding.tvNoData.setVisibility(View.GONE);
            binding.rlMap.setVisibility(View.GONE);
        } else if (message.equals("No near by user found")) {
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.rlUpdateProfile.setVisibility(View.GONE);
            binding.rlMap.setVisibility(View.GONE);
        } else {
            binding.rlMap.setVisibility(View.VISIBLE);
            binding.rlUpdateProfile.setVisibility(View.GONE);
            binding.tvNoData.setVisibility(View.GONE);

             MapInfoWindowFragment mapFragment;
            mapFragment = (MapInfoWindowFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            infoWindowManager = mapFragment.infoWindowManager();
            mapFragment.getMapAsync(this);
            infoWindowManager.setHideOnFling(true);
            infoWindowManager.setWindowShowListener(MapviewClientFragment.this);

        }
        progressDialog = new ProgressDialog(mContext);
        nearByList = new ArrayList<>();
        markerList = new ArrayList<>();
        infoWindowList = new ArrayList<>();

        binding.btnUpdate.setOnClickListener(this);
        //  getNearByListApi();
    }

    private void getNearByListApi() {
        if (Constant.isNetworkAvailable(mContext, binding.mainLayout)) {
            progressDialog.show();
            AndroidNetworking.get(BASE_URL + NEAR_BY_USER_API)
                    .addHeaders("authToken", PreferenceConnector.readString(mContext, PreferenceConnector.AUTH_TOKEN, ""))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            //    mainLayout.startAnimation(mLoadAnimation);
                            String status = null;
                            try {
                                status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equals("success")) {
                                    if (message.equals("Please update your location to know Live Wires near you.")) {
                                    /*    binding.rlUpdateProfile.setVisibility(View.VISIBLE);
                                        binding.tvNoData.setVisibility(View.GONE);*/
                                    } else if (message.equals("No near by user found")) {
                                       /* binding.tvNoData.setVisibility(View.VISIBLE);
                                        binding.rlUpdateProfile.setVisibility(View.GONE);*/
                                    } else {
                                        //Constant.snackBar(binding.mainLayout, message);
                                        NearByResponce nearByResponce = new Gson().fromJson(String.valueOf(response), NearByResponce.class);
                                        nearByList.addAll(nearByResponce.getData());

                                        for (NearByResponce.DataBean data : nearByList) {
                                            showMarkers(data);
                                        }

                                    }
                                } else {
                                    Constant.snackBar(binding.mainLayout, message);

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

    private void showMarkers(NearByResponce.DataBean data) {
        if (mMap != null) {
            try {
                double lat = Double.parseDouble(data.getLatitude()) + 0.005;
                double lng = Double.parseDouble(data.getLongitude()) + 0.005;
                Log.e(TAG, "change lat"+lat +"------"+"change lng"+lng);
                Log.e(TAG, "actual lat"+data.getLatitude() +"------"+"actual lng"+data.getLongitude());
                final Marker marker2 = mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).snippet(data.getUserId()));
                final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
                final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);
                final InfoWindow.MarkerSpecification markerSpec =
                        new InfoWindow.MarkerSpecification(offsetX, offsetY);
                InfoWindow formWindow = new InfoWindow(marker2, markerSpec, CustomInfoWindowFragment.newInstance(data));
                mMap.setOnMarkerClickListener(MapviewClientFragment.this);
                markerList.add(marker2);
                infoWindowList.add(formWindow);
            }catch (Exception e){
                Log.e(TAG, "showMarkers: "+e.getLocalizedMessage());
            }
        }

    /*    LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker m : markerList) {
            builder.include(m.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
        mMap.animateCamera(cu);*/

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        if (userData != null) {
            for (NearByResponce.DataBean data : userData.getData()) {
                showMarkers(data);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (int i = 0; i < markerList.size(); i++) {
            if (markerList.get(i).getSnippet().equals(marker.getSnippet())) {
                infoWindowManager.toggle(infoWindowList.get(i), true);
            }
        }
        return true;

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            if (mLastKnownLocation!=null){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void currentlocation() {
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(mContext, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(mContext, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onWindowShowStarted(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowShown(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowHideStarted(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onWindowHidden(@NonNull InfoWindow infoWindow) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                startActivity(new Intent(mContext, EditProfileClientActivity.class));
                break;
            default:
        }
    }
}
