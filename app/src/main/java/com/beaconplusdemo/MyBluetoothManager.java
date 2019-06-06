package com.beaconplusdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class MyBluetoothManager {
    private static final int REQUEST_ENABLE_BT = 3;

    protected Activity activity;
    protected BluetoothManager btManager;
    protected BluetoothAdapter btAdapter;


    private PermissionManager permissionManager;

    public MyBluetoothManager(Activity activity) {
        this.activity = activity;

        initialisation();

    }

    private void initialisation() {
        initPermissionManager();
        initBtManager();
        initBtAdapter();

        requestServices();
    }

    private void requestServices() {
        requestPermission();
        checkBleNRequestEnable();
    }

    private void initBtManager() {
        btManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    private void initBtAdapter() throws NullPointerException {
        btAdapter = btManager.getAdapter();
    }



    private boolean isBleExist() {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private boolean isBleEnable() {
        return btAdapter != null && btAdapter.isEnabled();
    }

    private void requestEnableBle() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    public void checkBleNRequestEnable() {
        if (isBleExist() && !isBleEnable())
            requestEnableBle();
    }

    private void requestPermission() {
        permissionManager.checkNRequestAccessCoarseLocationPermission();
    }

    private void initPermissionManager() {
        permissionManager = new PermissionManager(activity);
    }
}
