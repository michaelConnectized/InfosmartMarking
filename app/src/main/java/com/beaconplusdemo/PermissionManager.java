package com.beaconplusdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionManager {
    private static final int PERMISSION_COARSE_LOCATION = 2;
    private Activity activity;

    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    public boolean isAccessCoarseLocationGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestAccessCoarseLocationPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_COARSE_LOCATION);
    }

    public void checkNRequestAccessCoarseLocationPermission() {
        if (isAccessCoarseLocationGranted())
            requestAccessCoarseLocationPermission();
    }
}
