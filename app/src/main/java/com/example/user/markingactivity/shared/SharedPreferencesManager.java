package com.example.user.markingactivity.shared;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.user.markingactivity.model.Excel_Server;

import org.json.JSONArray;

import java.nio.channels.AcceptPendingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SharedPreferencesManager {
    private SharedPreferences sp;
    private Activity activity;

    public SharedPreferencesManager(Activity activity) {
        sp = activity.getSharedPreferences("tnc", Context.MODE_PRIVATE);
        this.activity = activity;
    }

    public boolean isServerMode() {
        return sp.getBoolean("server_mode", true);
    }

    public void setServerMode(boolean isServerMode) {
        sp.edit().putBoolean("server_mode", isServerMode).commit();
    }

    public boolean hasScanRange() {
        return sp.contains("scan_range");
    }

    public boolean hasScanTime() {
        return sp.contains("scan_time");
    }

    public boolean hasLoginUsername() {
        return sp.contains("loginUsername");
    }

    public boolean hasLoginPassword() {
        return sp.contains("loginPassword");
    }

    public int getScanRange() {
        return sp.getInt("scan_range", 100);
    }

    public int getScanTime() {
        return sp.getInt("scan_time", 15);
    }

    public String getLoginUsername() {
        return sp.getString("loginUsername", "");
    }

    public String getLoginPassword() {
        return sp.getString("loginPassword", "");
    }

    public String getServerUrl() {
        return sp.getString("serverUrl", "http://42.200.149.215:9860/excel");
    }

    public String getSelectedBlock() {
        return sp.getString("selected_block", "");
    }

    public String getSelectedFloor() {
        return sp.getString("selected_floor", "");
    }

    public String getSelectedRoom() {
        return sp.getString("selected_room", "");
    }

    public String getSelectedAreaWithin() {
        return sp.getString("selected_areawithin", "");
    }

    public void setScanRange(int scanRange) {
        sp.edit().putInt("scan_range", scanRange).commit();
    }

    public void setScanTime(int scanTime) {
        sp.edit().putInt("scan_time", scanTime).commit();
    }

    public void setLoginUsername(String loginUsername) {
        sp.edit().putString("loginUsername", loginUsername).commit();
    }

    public void setLoginPassword(String loginPassword) {
        sp.edit().putString("loginPassword", loginPassword).commit();
    }

    public void setServerUrl(String serverUrl) {
        sp.edit().putString("serverUrl", serverUrl).commit();
    }

    public void setSelectedBlock(String selectedBlock) {
        sp.edit().putString("selected_block", selectedBlock).commit();
    }

    public void setSelectedFloor(String selectedFloor) {
        sp.edit().putString("selected_floor", selectedFloor).commit();
    }

    public void setSelectedRoom(String selectedRoom) {
        sp.edit().putString("selected_room", selectedRoom).commit();
    }

    public void setSelectedAreaWithin(String selectedAreaWithin) {
        sp.edit().putString("selected_areawithin", selectedAreaWithin).commit();
    }




    public void saveDataFromServer(String projectId, String projectServerId) {
        saveAddrFromServerToSharedPreference(projectId, projectServerId);
        saveFilterListFromServerToSharedPreference(projectId, projectServerId);
        saveLastUpdateFromServerToSharedPreference(projectId, projectServerId);
        saveLandmarksFromServerToSharedPreference(projectId, projectServerId);
        saveAddrFromServerToSharedPreference(projectId, projectServerId);
    }

    public void saveAddrFromServerToSharedPreference(String projectId, String projectServerId) {
        try {
            new Excel_Server(activity, Excel_Server.ACTION_INIT).execute().get();
            String addressJson = new JSONArray(new Excel_Server(activity, Excel_Server.ACTION_GET_ADDRESSES, projectServerId).execute().get()).toString();
            if (addressJson.equals("")) {
                return;
            }
            sp.edit().putString("addressesFromServer"+projectId, addressJson).commit();
        } catch (Exception e) {

        }
    }

    public String getAddressesFromServer(String projectId) {
        return sp.getString("addressesFromServer"+projectId, "");
    }

    public void saveFilterListFromServerToSharedPreference(String projectId, String projectServerId) {
        try {
            new Excel_Server(activity, Excel_Server.ACTION_INIT).execute().get();
            String json = new JSONArray(new Excel_Server(activity, Excel_Server.ACTION_GET_LANDMARKS, projectServerId).execute().get()).toString();
            if (json.equals("")) {
                return;
            }
            sp.edit().putString("filterListFromServer"+projectId, json).commit();
        } catch (Exception e) {

        }
    }

    public String getLandmarksFromServer(String projectId) {
        return sp.getString("landmarksFromServer"+projectId, "");
    }

    public void saveLandmarksFromServerToSharedPreference(String projectId, String projectServerId) {
        try {
            new Excel_Server(activity, Excel_Server.ACTION_INIT).execute().get();
            String json = new JSONArray(new Excel_Server(activity, Excel_Server.ACTION_GET_LANDMARKS, projectServerId).execute().get()).toString();
            if (json.equals("")) {
                return;
            }
            sp.edit().putString("landmarksFromServer"+projectId, json).commit();
        } catch (Exception e) {
        }
    }

    public void saveLastUpdateFromServerToSharedPreference(String projectId, String projectServerId) {
        try {
            new Excel_Server(activity, Excel_Server.ACTION_INIT).execute().get();
            String json = new JSONArray(new Excel_Server(activity, Excel_Server.ACTION_GET_LANDMARKS, projectServerId).execute().get()).toString();
            if (json.equals("")) {
                return;
            }
            sp.edit().putString("lastUpdateDatetimeFromServer"+projectId, json).commit();
            saveGetDataFromServerTime(projectId);
        } catch (Exception e) {

        }
    }

    public void saveGetDataFromServerTime(String projectId) {
        sp.edit().putString("getDataFromServerTime"+projectId, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).commit();
    }

    public String getGetDataFromServerTime(String projectId) {
        return sp.getString("getDataFromServerTime"+projectId, "None");
    }
}
