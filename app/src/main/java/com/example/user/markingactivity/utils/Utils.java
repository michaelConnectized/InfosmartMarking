package com.example.user.markingactivity.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private final static String tag = "Utils";

    public static String toJsonArray(String[] arr) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(arr[0]); //block
        jsonArray.put(arr[1]); //floor
        jsonArray.put(arr[2]); //room
        jsonArray.put(arr[3]); //area within
        return jsonArray.toString();
    }

    public static String[] toAddressFromJson(String json) {
        String[] result = new String[4];
        try {
            JSONArray jsonArray = new JSONArray(json);
            result[0] = jsonArray.get(0).toString();
            result[1] = jsonArray.get(1).toString();
            result[2] = jsonArray.get(2).toString();
            result[3] = jsonArray.get(3).toString();
        } catch (Exception e) {
            result[0] = "";
            result[1] = "";
            result[2] = "";
            result[3] = "";
            Log.e(tag, e.toString());
        }
        return result;
    }
}
