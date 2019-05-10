package com.example.user.markingactivity.analyser;

import android.util.Log;

//
public class BeaconAnalyser {
    public final static int MINEW = 0;
    public final static int APPLEIBEACON = 1;

    private String tag = "BeaconAnalyser";
    private minewDevice minew;
    private appleIbeaconDevice aIbeacon;
    private byte[] oriData;
    private int deviceCode;

    public BeaconAnalyser(byte[] oriData) {
        this.oriData = oriData;
        deviceCode = -1;

        if (minewDeviceInfo()) {
            deviceCode = MINEW;
        } else if (iBeaconDeviceInfo()) {
            deviceCode = APPLEIBEACON;
        }
    }

    public boolean minewDeviceInfo() {
        boolean isSuccess;
        try {
            minew = new minewDevice(oriData);
            isSuccess = minew.isMinew();
        } catch (Exception e) {
            Log.d(tag, e.toString());
            isSuccess = false;
        }
        return isSuccess;
    }

    public boolean iBeaconDeviceInfo() {
        boolean isSuccess;
        try {
            aIbeacon = new appleIbeaconDevice(oriData);
            isSuccess = aIbeacon.isAppleIbeacon();
        } catch (Exception e) {
            Log.d(tag, e.toString());
            isSuccess = false;
        }
        return isSuccess;
    }

    public minewDevice getMinew() {
        return minew;
    }

    public appleIbeaconDevice getaIbeacon() {
        return aIbeacon;
    }

    public int getDeviceCode() {
        return deviceCode;
    }
}
