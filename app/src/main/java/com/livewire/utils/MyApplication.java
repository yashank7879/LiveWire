package com.livewire.utils;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by mindiii on 9/17/18.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        AndroidNetworking.initialize(getApplicationContext());
    }
}
