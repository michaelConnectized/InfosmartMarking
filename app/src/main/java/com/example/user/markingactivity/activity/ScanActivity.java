package com.example.user.markingactivity.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.markingactivity.model.ExcelPath;
import com.example.user.markingactivity.model.Excel_Server;
import com.example.user.markingactivity.object.Locations;
import com.example.user.markingactivity.R;
import com.example.user.markingactivity.analyser.BeaconAnalyser;
import com.example.user.markingactivity.baseAdapter.bleAdapter;
import com.example.user.markingactivity.object.mDevice;
import com.neovisionaries.bluetooth.ble.advertising.ADPayloadParser;
import com.neovisionaries.bluetooth.ble.advertising.ADStructure;
import com.neovisionaries.bluetooth.ble.advertising.IBeacon;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScanActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int MY_PERMISSION_REQUEST_CONSTANT = 2;
    private String tag = "scan_log";
    private final BroadcastReceiver incomingPairRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("testingBLE", action);
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //pair from device: dev.getName()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent intents = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivityForResult(intents, 1);
                }
            }
        }
    };

    //Bluetooth related
    private ArrayList<mDevice> mDev;
    private BluetoothAdapter mBluetoothAdapter;
    private bleAdapter bleA;

    //settings
    private SharedPreferences preferences;
    private int scan_range;
    private boolean scanning;
    private boolean server_mode;
    private String address_id;

    String fileExtension;

    //View init
    ListView lv;
    TextView tv_at1;
    TextView tv_at2;
    TextView tv_at3;
    TextView tv_at4;
    TextView tv_project;
    TextView tv_filter_value;
    TextView tv_scan_time;
    Button btn_scan;

    //project and address def
    String project_name;
    int project_id;
    String[] addresses;
    int address_row_num;
    Locations locs;

    //event handle
    Handler handler;
    Runnable runnable;
    int scan_time;
    int current_time;

    //excel related
    private String[] filterUuidList;
    private boolean[] filterUuidCheckedList;

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        tryOnCreateEvent();
    }

    private void tryOnCreateEvent() {
        try {
            if (getSupportActionBar()!=null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            initAddressInfo();
            initBasicInfo();
            initBlueTooth();
            initBeaconFilter();
        } catch (Exception e) {
            Log.e(tag, e.toString());
            finishActivity();
        }
    }

    public void onClickBtnScan(View view) {
        startScanEvent();
    }

    private void startScanEvent() {
        if (!scanning) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            mDev.clear();
            bleA.getmDevCheckedIndex().clear();
            bleA = new bleAdapter(this, mDev);
            bleA.refleshLayout(ctx, findViewById(R.id.subScroll),false);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }
            });
            handler.postDelayed(runnable, 0);

            Log.d("testingBLE", "Discovery Starting...");

            for (mDevice mSingDev : mDev) {
                Log.d("testingBLE", "Current devices: " + mSingDev.toString());
            }
            scanning = true;
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            bleA.refleshLayout(ctx, findViewById(R.id.subScroll),true);
            handler.removeCallbacks(runnable);
            scanning = false;
            current_time = 0;
            btn_scan.setText("SCAN");
        }
    }

    public void onClickBtnFilterLMList(View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtering Landmark list")

                .setMultiChoiceItems(filterUuidList, filterUuidCheckedList, new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean yes)
                    {
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public void onClickBtnAssign(View view) {
        Intent myIntent = new Intent(ScanActivity.this, ConfirmActivity.class);
        myIntent.putExtra("project_id", project_id);
        myIntent.putExtra("addresses", addresses);
        myIntent.putExtra("row_num", address_row_num);
        myIntent.putExtra("address_id", address_id);
        int mDevCount = 0;
        for (int i = 0; i < bleA.getmDevCheckedIndex().size(); i++) {
            if (bleA.getmDevCheckedIndex().get(i)) {
                Log.d("testingBLE", mDev.get(i).toString());
                myIntent.putExtra("mDevs" + mDevCount, (Serializable) mDev.get(i));
                mDevCount += 1;
            }
        }
        if (mDevCount==0) {
            return;
        }
        myIntent.putExtra("mDevCount", mDevCount);
        ScanActivity.this.startActivity(myIntent);
        this.finish();
    }

    public void onClickBtnConnect(View view) {
        //
        for (int i = 0; i < bleA.getmDevCheckedIndex().size(); i++) {
            if (bleA.getmDevCheckedIndex().get(i)) {
                Log.d("testingBLEc", mDev.get(i).getName());
                ConnectThread ct = new ConnectThread(mDev.get(i));
                ct.run();
            }
        }
    }

    private void initAddressInfo() {
        locs = new Locations(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        scan_range = preferences.getInt("scan_range", -100);
        scan_time = preferences.getInt("scan_time", 5);
        Intent intent = getIntent();
        address_id = intent.getStringExtra("address_id");

        project_id = intent.getIntExtra("project_id", 0);
        address_row_num = intent.getIntExtra("row_num", 0);
        addresses = intent.getStringArrayExtra("addresses");
        server_mode = preferences.getBoolean("server_mode", true);

        if (server_mode) {
            locs.setProjectsFromServer();
        } else {
            locs.setProjectsFromFiles(ExcelPath.homepage, "addr");
        }
    }

    private void initBasicInfo() {
        project_name = locs.getProject(project_id).getName();
        fileExtension = "";
        if (!project_name.equals("Unnamed")) {
            fileExtension = "-"+project_name;
        }

        tv_at1 = findViewById(R.id.tv_at1);
        tv_at2 = findViewById(R.id.tv_at2);
        tv_at3 = findViewById(R.id.tv_at3);
        tv_at4 = findViewById(R.id.tv_at4);
        tv_project = findViewById(R.id.tv_project);
        tv_filter_value = findViewById(R.id.tv_filter_value);
        tv_scan_time = findViewById(R.id.tv_scan_time);
        btn_scan = findViewById(R.id.btn_scan);

        tv_project.setText(project_name);
        tv_at1.setText(addresses[0]);
        tv_at2.setText(addresses[1]);
        tv_at3.setText(addresses[2]);
        tv_at4.setText(addresses[3]);

        current_time = 0;
        tv_filter_value.setText("From "+String.valueOf(scan_range)+" To 0");
        tv_scan_time.setText(String.valueOf(scan_time));

        ctx = this;

    }

    private void initBlueTooth() {
        mDev = new ArrayList<mDevice>();
        scanning = false;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("testingBLE", "Getting the Default Adapter...");
        if (mBluetoothAdapter == null) {
            Log.d("testingBLE", "This device doesn't support BlueTooth.");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        bleA = new bleAdapter(this, mDev);
        bleA.setmDevCheckedIndex(new ArrayList<Boolean>());

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_CONSTANT);

        handler = new Handler();
        runnable = new Runnable(){
            public void run(){
                if (scan_time-current_time<=0) {
                    bleA.refleshLayout(ctx, findViewById(R.id.subScroll),true);
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    scanning = false;
                    handler.removeCallbacks(runnable);
                    current_time = 0;
                    btn_scan.setText("SCAN");
                    return;
                }
                btn_scan.setText(String.valueOf(scan_time-current_time));
                current_time++;
                bleA.refleshLayout(ctx, findViewById(R.id.subScroll),false);
                handler.postDelayed(this, 1000);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(Settings.ACTION_BLUETOOTH_SETTINGS);

        registerReceiver(incomingPairRequestReceiver, filter);
    }

    private void initBeaconFilter() {
        if (server_mode) {
            initFilterFromServer();
        } else {
            initFilterFromFiles();
        }

        Arrays.sort(this.filterUuidList);
        filterUuidCheckedList = new boolean[ this.filterUuidList.length ];
        for (int i = 0; i<filterUuidCheckedList.length; i++) {
            if (this.filterUuidList[i].charAt(this.filterUuidList[i].length()-1)=='0') {
                this.filterUuidCheckedList[i] = true;
            } else {
                this.filterUuidCheckedList[i] = false;
            }
            this.filterUuidList[i] = this.filterUuidList[i].substring(0,this.filterUuidList[i].length()-1);
        }
    }

    private void initFilterFromFiles() {
        ArrayList<String> filterUuidList = new ArrayList<String>();
        String status = "";
        try {
            FileInputStream excelFile = new FileInputStream(new File(ExcelPath.xlsx(ExcelPath.FilterLandmark+fileExtension)));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            if (iterator.hasNext()) {
                iterator.next();
            }

            while (iterator.hasNext()) {
                status = "0";

                Row currentRow = iterator.next();
                //Cell 0: uuids
                //Cell 6: status
                if (currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).getCellType() == XSSFCell.CELL_TYPE_STRING) {
                    status = currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                } else if (currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                    status = String.valueOf(currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).getNumericCellValue());
                }
                if (status.equals("marked")) {
                    status = "1";
                } else {
                    status = "0";
                }
                if (currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).getCellType() == XSSFCell.CELL_TYPE_STRING) {
                    filterUuidList.add(currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue()+status);
                } else if (currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                    filterUuidList.add(String.valueOf(currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).getNumericCellValue())+status);
                }
            }
        } catch (Exception e) {
            Log.d("error", e.toString());
        }

        this.filterUuidList = filterUuidList.toArray(new String[filterUuidList.size()]);
    }

    private void initFilterFromServer() {
        ArrayList<String> filterUuidList = new ArrayList<String>();
        String status = "";
        try {
            saveFilterListFromServerToSharedPreference();
            JSONArray data = new JSONArray(getFilterListFromSharedPreference());
            for (int k=0; k<data.length(); k++) {
                JSONObject eaRecord = data.getJSONObject(k);

                status = "0";
                if ((eaRecord.getString("status")!=null)&&(eaRecord.getString("status").equals("Assigned")))
                    status = "1";
                filterUuidList.add(eaRecord.getString("uuid")+status);
            }

        } catch (Exception e) {
            Log.d("error", e.toString());
        }

        this.filterUuidList = filterUuidList.toArray(new String[filterUuidList.size()]);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (rssi < scan_range) {
                        return;
                    }
                    BeaconAnalyser ba = new BeaconAnalyser(scanRecord);
                    if (ba.getDeviceCode()==BeaconAnalyser.MINEW) {
                        if (mDev.contains(new mDevice(device.getName(), ba.getMinew().getMac_address(), rssi))) {
                            int tmpIndex = mDev.indexOf(new mDevice(device.getName(), ba.getMinew().getMac_address(), rssi));
                            mDev.get(tmpIndex).setBatteryLevel(ba.getMinew().getBattery_level());
                        }
                        return;
                    }
                    if (mDev.contains(new mDevice(device.getName(), device.getAddress(), rssi))) {
                        return;
                    }

                    List<ADStructure> structures =
                            ADPayloadParser.getInstance().parse(scanRecord);
                    Log.d("testingBLE", device.getAddress());
                    // For each AD structure contained in the advertising packet.
                    for (com.neovisionaries.bluetooth.ble.advertising.ADStructure structure : structures) {
                        if (structure instanceof com.neovisionaries.bluetooth.ble.advertising.IBeacon) {
                            com.neovisionaries.bluetooth.ble.advertising.IBeacon iBeacon = (IBeacon) structure;

                            //Filter the required list
                            String[] tmpUuid = new String[3];
                            tmpUuid[0] = iBeacon.getMajor()+"."+iBeacon.getMinor();
                            tmpUuid[1] = tmpUuid[0]+"."+device.getAddress();
                            tmpUuid[2] = tmpUuid[1]+"."+iBeacon.getUUID();
                            int tmpLevel = 0;
                            int ExcelIndex = -1;

                            if (Arrays.asList(filterUuidList).contains(tmpUuid[0])) {
                                tmpLevel = mDevice.MYUUID_LEVEL_MINOR;
                                ExcelIndex = Arrays.asList(filterUuidList).indexOf(tmpUuid[0]);
                            } else if (Arrays.asList(filterUuidList).contains(tmpUuid[1])) {
                                tmpLevel = mDevice.MYUUID_LEVEL_MAC_ADDRESS;
                                ExcelIndex = Arrays.asList(filterUuidList).indexOf(tmpUuid[1]);
                            } else if (Arrays.asList(filterUuidList).contains(tmpUuid[2])) {
                                tmpLevel = mDevice.MYUUID_LEVEL_UUID;
                                ExcelIndex = Arrays.asList(filterUuidList).indexOf(tmpUuid[2]);
                            } else {
                                return;
                            }
                            if (!filterUuidCheckedList[ExcelIndex]) {
                                return;
                            }
                            mDevice tmpDevice = new mDevice(device.getName(), device.getAddress(), rssi);
                            tmpDevice.setExcelIndex(ExcelIndex);
                            tmpDevice.setBtDevice(device);
                            tmpDevice.setUUID(iBeacon.getUUID());
                            tmpDevice.setMajor(iBeacon.getMajor());
                            tmpDevice.setMinor(iBeacon.getMinor());

                            int tmpTxpwr = new BeaconAnalyser(scanRecord).getaIbeacon().getTxpower();
                            if (tmpTxpwr<=-40) {
                                tmpDevice.setPower(4);
                            } else {
                                tmpDevice.setPower(tmpTxpwr);
                            }
                            tmpDevice.setmUUID(tmpLevel);
                            if (server_mode) {
                                tmpDevice.setLastUpdateDatetime(getLastUpdateDatetimeFromServer(filterUuidList[ExcelIndex]));
                        }
                            mDev.add(tmpDevice);

                            Collections.sort(mDev, (dev1, dev2) -> dev2.getRSSI() - dev1.getRSSI());
                            bleA.getmDevCheckedIndex().add(false);
                            Log.d("testingBLE", tmpDevice.toString());

                        }
                    }
                }

                ;
            };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("testingBLE", "granted");
                    //permission granted!
                }
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        try {
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            handler.removeCallbacks(runnable);
            unregisterReceiver(incomingPairRequestReceiver);
        } catch (NullPointerException e) {}
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        finishActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Setting page
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(ScanActivity.this, OptionActivity.class);
            myIntent.putExtra("project_id", project_id);
            myIntent.putExtra("addresses", addresses);
            myIntent.putExtra("address_id", address_id);
            ScanActivity.this.startActivity(myIntent);
            this.finish();
        }
        if (id == android.R.id.home) {
            finishActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    public String getLastUpdateDatetimeFromServer(String uuid) {
        try {
            saveLastUpdateFromServerToSharedPreference();
            JSONArray data = new JSONArray(getLastUpdateDatetimeFromSharedPreference());
            for (int i=0; i<data.length(); i++) {
                if (data.getJSONObject(i).getString("uuid").equals(uuid)) {
                    return data.getJSONObject(i).getString("lastUpdateDatetime");
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(mDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device.getBtDevice();

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                //Log.d("testingBLECon", device.getUuids()[0].getUuid().toString());
                mmDevice.setPin(convertPinToBytes("ktcos23g"));
                Method method = mmDevice.getClass().getMethod("createBond", (Class[]) null);
                method.invoke(mmDevice, (Object[]) null);
                Log.d("testingBLEConcg`cdcx z", String.valueOf(mmDevice.getBondState()));
                tmp = mmDevice.createRfcommSocketToServiceRecord(device.getUUID());
                tmp = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
            } catch (IOException e) {
                Log.e("testingBLECon", "Socket's create() method failed", e);
            } catch (Exception ex) {
                Log.e("testingBLECon", "Socket's create() method failed", ex);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmDevice.connectGatt(getApplicationContext(), true, null);
                mmSocket.connect();
                if (mmSocket.isConnected()) {
                    Log.d("testingBLE", "connected");
                } else {
                    Log.d("testingBLE", "connecting");
                }

            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    Log.d("testingBLEClosing", connectException.toString());
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("testingBLECon", "Could not close the client socket", closeException);
                }
                return;
            }
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("testingBLECon", "Could not close the client socket", e);
            }
        }


        public byte[] convertPinToBytes(String pin) {
            if (pin == null) {
                return null;
            }
            byte[] pinBytes;
            try {
                pinBytes = pin.getBytes("UTF-8");
            } catch (UnsupportedEncodingException uee) {
                return null;
            }
            if (pinBytes.length <= 0 || pinBytes.length > 16) {
                return null;
            }
            return pinBytes;
        }
    }

    public void finishActivity() {
        Intent myIntent = new Intent(ScanActivity.this, AddressSelectActivity.class);
        myIntent.putExtra("project_id", project_id);
        startActivity(myIntent);
        this.finish();
    }

    public void saveFilterListFromServerToSharedPreference() {
        try {
            new Excel_Server(this, Excel_Server.ACTION_INIT).execute().get();
            String json = new JSONArray(new Excel_Server(this, Excel_Server.ACTION_GET_LANDMARKS, locs.getProject(project_id).getId()).execute().get()).toString();
            if (json.equals("")) {
                return;
            }
            preferences.edit().putString("filterListFromServer", json).commit();
        } catch (Exception e) {

        }
    }

    public String getFilterListFromSharedPreference() {
        return preferences.getString("filterListFromServer", "");
    }

    public void saveLastUpdateFromServerToSharedPreference() {
        try {
            new Excel_Server(this, Excel_Server.ACTION_INIT).execute().get();
            String json = new JSONArray(new Excel_Server(this, Excel_Server.ACTION_GET_LANDMARKS, locs.getProject(project_id).getId()).execute().get()).toString();
            if (json.equals("")) {
                return;
            }
            preferences.edit().putString("lastUpdateDatetimeFromServer", json).commit();
        } catch (Exception e) {

        }
    }

    public String getLastUpdateDatetimeFromSharedPreference() {
        return preferences.getString("lastUpdateDatetimeFromServer", "");
    }
}
