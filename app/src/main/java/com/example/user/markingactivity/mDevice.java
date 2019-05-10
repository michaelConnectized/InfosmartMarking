package com.example.user.markingactivity;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;
import java.util.UUID;

public class mDevice implements Serializable {
    public static final int MYUUID_LEVEL_MAJOR = 0;
    public static final int MYUUID_LEVEL_MINOR = 1;
    public static final int MYUUID_LEVEL_MAC_ADDRESS = 2;
    public static final int MYUUID_LEVEL_UUID = 3;

    private UUID UUID;
    private String mUUID;
    private String name;
    private String address;
    private int RSSI;
    private int power;
    private transient BluetoothDevice btDevice;
    private int major;
    private int minor;
    private int batteryLevel;
    private int excelIndex;
    private String lastUpdateDatetime;

    public mDevice(String name, String address, int RSSI) {
        this.name = name;
        this.address = address;
        this.RSSI = RSSI;

        if (name == null || name == "") {
            this.name = "N/A";
        }
        if (address == null || address == "") {
            this.address = "N/A";
        }

        //default
        batteryLevel = 999;
        power = 999;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj instanceof mDevice){
            mDevice mDev = (mDevice) obj;
            retVal = (mDev.address.equals(this.address));
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.address != null ? this.address.hashCode() : 0);
        return hash;
    }

    public UUID getUUID() { return UUID; }

    public void setUUID(UUID UUID) { this.UUID = UUID; }

    public String getmUUID() {
        return mUUID;
    }

    public void setmUUID(int level) {
        switch (level) {
            case MYUUID_LEVEL_MAJOR:
                mUUID = String.valueOf(major);
                break;
            case MYUUID_LEVEL_MINOR:
                mUUID = String.valueOf(major)+"."+String.valueOf(minor);
                break;
            case MYUUID_LEVEL_MAC_ADDRESS:
                mUUID = String.valueOf(major)+"."+String.valueOf(minor)+"."+address;
                break;
            case MYUUID_LEVEL_UUID:
                mUUID = String.valueOf(major)+"."+String.valueOf(minor)+"."+address+"."+getUUID().toString();
                break;
            default:
                mUUID = "";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getExcelIndex() {
        return excelIndex;
    }

    public void setExcelIndex(int excelIndex) {
        this.excelIndex = excelIndex;
    }

    public String getLastUpdateDatetime() {
        return lastUpdateDatetime;
    }

    public void setLastUpdateDatetime(String lastUpdateDatetime) {
        this.lastUpdateDatetime = lastUpdateDatetime;
    }

    public String toString() {
        return name+": "+address+", RSSI: "+RSSI;
    }
}
