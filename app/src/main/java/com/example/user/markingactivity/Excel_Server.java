package com.example.user.markingactivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

public class Excel_Server extends AsyncTask<URL, Integer, String> {
    public static final int ACTION_INIT = 0;
    public static final int ACTION_GET_PROJECTS = 1;
    public static final int ACTION_GET_ADDRESSES = 2;
    public static final int ACTION_GET_LANDMARKS = 3;
    public static final int ACTION_ASSIGN_ADDRESS = 5;

    private String tag = "Excel_Server_Error";
    private String access_token = "";
    private Context ctx;
    private int action;
    private String env;

    public Excel_Server(Context ctx, int action) {
        super();
        this.ctx = ctx;
        this.action = action;

        SharedPreferences editor = ctx.getSharedPreferences("Excel_Server", MODE_PRIVATE);
        access_token = editor.getString("access_token", "");
    }

    public Excel_Server(Context ctx, int action, String env) {
        this(ctx, action);
        this.env = env;
    }

    protected String doInBackground(URL... urls) {
        switch (action) {
            case ACTION_INIT: return getToken();
            case ACTION_GET_PROJECTS: return getProjects();
            case ACTION_GET_ADDRESSES: return getAddresses();
            case ACTION_GET_LANDMARKS: return getLandmarks();
            case ACTION_ASSIGN_ADDRESS: return assignAddress();
            default: throw new InputMismatchException();
        }
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(Long result) {
    }

    private String getProjects() {
        if (access_token.equals("")) {
            return "Fail";
        }
        String response = "";
        try {
            URL url = new URL("http://42.200.149.215:9860/excel/api/project");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestProperty("Authorization", "Bearer "+access_token);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);

                int responseCode=urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getJSONObject("result").getBoolean("success")) {
                        return jsonObject.getJSONArray("projects").toString();
                    } else {
                        return null;
                    }
                } else {
                    Log.d(tag, "Fail: "+String.valueOf(responseCode));
                    return "Fail";
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.d(tag, e.toString());
        }
        return "";
    }

    private String getAddresses() {
        if (access_token.equals("")) {
            return "Fail";
        }
        if (env == null) {
            return "Fail";
        }
        String response = "";
        try {
            URL url = new URL("http://42.200.149.215:9860/excel/api/availableAddress/project/"+env);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestProperty("Authorization", "Bearer "+access_token);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);

                int responseCode=urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getJSONObject("result").getBoolean("success")) {
                        return jsonObject.getJSONArray("availableAddress").toString();
                    } else {
                        return null;
                    }
                } else {
                    Log.d(tag, "Fail: "+String.valueOf(responseCode));
                    return "Fail";
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.d(tag, e.toString());
        }
        return "";
    }

    private String getLandmarks() {
        if (access_token.equals("")) {
            return "Fail";
        }
        if (env == null) {
            return "Fail";
        }
        String response = "";
        try {
            URL url = new URL("http://42.200.149.215:9860/excel/api/landmark/"+env);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestProperty("Authorization", "Bearer "+access_token);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);

                int responseCode=urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getJSONObject("result").getBoolean("success")) {
                        return jsonObject.getJSONArray("landmarks").toString();
                    } else {
                        return null;
                    }
                } else {
                    Log.d(tag, "Fail: "+String.valueOf(responseCode));
                    return "Fail";
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.d(tag, e.toString());
        }
        return "";
    }

    private String assignAddress() {
        if (access_token.equals("")) {
            return "Fail";
        }
        if (env == null) {
            return "Fail";
        }
        String response = "";
        try {
            URL url = new URL("http://42.200.149.215:9860/excel/api/landmark/assign");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestProperty("Authorization", "Bearer "+access_token);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("PUT");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(env);
                writer.flush();
                writer.close();
                os.close();
                int responseCode=urlConnection.getResponseCode();

                //if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                    return response;
                //} else {
                 //   return String.valueOf(responseCode)+" : "+urlConnection.getResponseMessage();
                //}
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.d(tag, e.toString());
        }
        return "";
    }

    private String getToken() {
        String response = "";
        try {
            URL url = new URL("http://42.200.149.215:9860/excel/api/oauth/token");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Authorization", "Basic ZXhjZWwtYXBpOmV4Y2VsLXNlY3JldA==");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("scope", "api");
                hm.put("grant_type", "password");
                hm.put("username", preferences.getString("login_username", "")); //"peterson@connectized.com"
                hm.put("password", preferences.getString("login_password", "")); //"ipwc3298HA"

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(hm));
                writer.flush();
                writer.close();
                os.close();
                int responseCode=urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    access_token = jsonObject.getString("access_token");

                    SharedPreferences.Editor editor = ctx.getSharedPreferences("Excel_Server", MODE_PRIVATE).edit();
                    editor.putString("access_token", access_token);
                    editor.apply();
                } else {
                    Log.d(tag, "Fail: "+String.valueOf(responseCode));
                    response="";
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.d(tag, e.toString());
        }
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    @TargetApi(19)
    private String shaHash(String pw) {
        String hex = pw;
        try {
            MessageDigest md = MessageDigest.getInstance( "SHA-256" );
            // Change this to UTF-16 if needed
            md.update( pw.getBytes( StandardCharsets.UTF_8 ) );
            byte[] digest = md.digest();
            hex = String.format( "%064x", new BigInteger( 1, digest ) );
            System.out.println( hex );
        } catch (Exception e) {
            Log.d(tag, e.toString());
        }
        return hex;
    }
}
