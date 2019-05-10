package com.example.user.markingactivity.analyser;

import java.util.Arrays;

public class appleIbeaconDevice {
    private int txpower;

    public appleIbeaconDevice(byte[] oriData) {
        txpower = Arrays.copyOfRange(oriData, 29, 30)[0];
    }

    public int getTxpower() {
        return txpower;
    }

    public boolean isAppleIbeacon() {
        return true;
    }
}
