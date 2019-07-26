package com.example.user.markingactivity.utils.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;


import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyNetwork {
    private final String tag = "MyNetwork";
    private Context ctx;

    private WifiManager wifiManager;

    public MyNetwork(Context ctx) {
        this.ctx = ctx;
    }


    //open wifi
    public void wifi(){
        //enable wifi if wifi is disabled
        try {
            wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }
    }


    //check internet
    public boolean networkTurnOnAndValid(){
        boolean internet = false;

        try {
            if (networkTurnOn()) {
                //return networkValid();
                return true;
            }
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }

        return internet;
    }

    public boolean networkTurnOn() {
        boolean internet = false;

        try {
            wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            ConnectivityManager connMgr =
                    (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean isWifiConn = false;
            boolean isMobileConn = false;
            for (android.net.Network network : connMgr.getAllNetworks()) {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn |= networkInfo.isConnected();
                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn |= networkInfo.isConnected();
                }
            }
            internet = false;
            if (isWifiConn || isMobileConn){
                //return new CheckInternetStable().execute().get();
                return true;
            }
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }

        return internet;
    }

    public boolean networkValid() {
        try {
            if (networkTurnOn())
                return new CheckInternetStable().execute().get();
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public class CheckInternetStable extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return pingMethod() || accessGoogleMethod();
        }
        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }

        private boolean pingMethod() {
            try {
                Runtime runTime = Runtime.getRuntime();
                Process mIpAddrProcess = runTime.exec("/system/bin/ping -c 1 8.8.8.8");
                int mExitValue = mIpAddrProcess.waitFor();
                if(mExitValue==0){
                    return true;
                }
            } catch (Exception e) {
                Log.e(tag, e.toString());
            }
            return false;
        }

        private boolean accessGoogleMethod() {
            try {
                Log.d("accessgoogle","success");
                URL url = new URL("https://www.google.com/favicon.ico");
                HttpsURLConnection urlc = (HttpsURLConnection) url.openConnection();
                urlc.setConnectTimeout(1000);
                urlc.setReadTimeout(1000);
                urlc.connect();
                Log.d("accessgoogle","success after timeout");
                if ((urlc.getResponseCode() == 200) || (urlc.getResponseCode() == 304)) {
                    return true;
                }
            } catch (Exception e) {
                Log.e(tag, e.toString());
            }
            return false;
        }
    }
}
