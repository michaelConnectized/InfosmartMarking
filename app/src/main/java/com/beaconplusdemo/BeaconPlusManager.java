package com.beaconplusdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.minew.beaconplus.sdk.MTCentralManager;
import com.minew.beaconplus.sdk.MTConnectionHandler;
import com.minew.beaconplus.sdk.MTPeripheral;
import com.minew.beaconplus.sdk.enums.BluetoothState;
import com.minew.beaconplus.sdk.enums.ConnectionStatus;
import com.minew.beaconplus.sdk.enums.FrameType;
import com.minew.beaconplus.sdk.exception.MTException;
import com.minew.beaconplus.sdk.frames.DeviceInfoFrame;
import com.minew.beaconplus.sdk.frames.IBeaconFrame;
import com.minew.beaconplus.sdk.frames.MinewFrame;
import com.minew.beaconplus.sdk.interfaces.ConnectionStatueListener;
import com.minew.beaconplus.sdk.interfaces.GetPasswordListener;
import com.minew.beaconplus.sdk.interfaces.MTCOperationCallback;
import com.minew.beaconplus.sdk.interfaces.MTCentralManagerListener;
import com.minew.beaconplus.sdk.interfaces.OnBluetoothStateChangedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconPlusManager extends MyBluetoothManager{
    private final String tag = "BeaconPlusManager";
    private final int beaconTimeout = 7; //seconds
    public enum STATUS {
        PASSWORD_VALID_TIMEOUT,
        CONNECT_FAIL,
        CHANGE_SUCCESS
    };

    private Landmark landmark;
    private List<MTPeripheral> landmarks;

    private MTCentralManager beaconManager;
    private OnBluetoothStateChangedListener beaconChangedListener;
    private MTCentralManagerListener beaconScannedListener;
    private ConnectionStatueListener connectionStatueListener;
    private CountDownTimer beaconReconnection;
    private MTPeripheral connectingBeacon, lastConnectedBeacon;
    private MTConnectionHandler beaconConnectionHandler;
    private ArrayList<MinewFrame> allFrames;
    private MinewFrame minewFrame;
    private DeviceInfoFrame infoFrame;
    private IBeaconFrame iBeaconFrame;
    private boolean beacon_changed = true;
    private Map<String, String> iBeacon = new HashMap<String, String>();
    private Map<String, String> info = new HashMap<String, String>();

    int poinNum;

    private onConnectionStatusChangedListener ownListener;
    public interface onConnectionStatusChangedListener {
        public void onFinish(STATUS status);
    }

    public static BeaconPlusManager EditTxPwrManager(Activity activity, Landmark landmark) {
        BeaconPlusManager lm = new BeaconPlusManager(activity);
        lm.setLandmark(landmark);
        return lm;
    }

    private BeaconPlusManager(Activity activity) {
        super(activity);
        initialisation();


        applyListener();
        startBeaconService();
        //startBeaconScan();
    }

    private void initialisation() {
        initOwnListener();
        initBeaconManager();
        initBeaconChangedListener();
        initBeaconScannedListener();
        initConnectionStatueListener();
        initBeaconReconnection();
    }

    private void initOwnListener() {
        ownListener = null;
    }

    public void setOnConnectionStatusChangedListener(onConnectionStatusChangedListener ownListener) {
        this.ownListener = ownListener;
    }

    public void startBeaconScan() {
        Log.e(tag, "Start Scan");
        beaconManager.startScan();
    }

    public void stopBeaconScan() {
        Log.e(tag, "Stop Scan");
        beaconManager.stopScan();
    }

    private void initBeaconManager() {
        beaconManager = MTCentralManager.getInstance(activity);
    }


    private void initConnectionStatueListener() {
        connectionStatueListener = new ConnectionStatueListener() {
            @Override
            public void onUpdateConnectionStatus(final ConnectionStatus connectionStatus, final GetPasswordListener getPasswordListener) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (connectionStatus) {
                            case PASSWORDVALIDATING:
                                Log.e(tag, "PASSWORD VALIDATING");
                                getPasswordListener.getPassword(landmark.getPwd());
                                break;
                            case COMPLETED:
                                Log.e(tag, "COMPLETED");
                                beaconConnectionHandler = connectingBeacon.mMTConnectionHandler;
                                allFrames = beaconConnectionHandler.allFrames;
                                minewFrame = allFrames.get(0);//GET Slot 0 DATA, Slot 0 de
                                getFrameiBeacon();
                                setSlotData_0_iBeacon();
                                minewFrame = allFrames.get(4);
                                getFrameinfo();
                                setSlotData_4_info();
                                triggerOwnListener(STATUS.CHANGE_SUCCESS);
                                break;
                            case CONNECTFAILED:
                                triggerOwnListener(STATUS.CONNECT_FAIL);
                                Log.e(tag, "CONNECTFAILED");
                            case DISCONNECTED:
                                Log.e(tag, "DISCONNECTED");
                                break;
                        }

                    }
                });
            }

            @Override
            public void onError(MTException e) {
                if (e.getMessage().equals("Password Validate Timeout")) {
                    triggerOwnListener(STATUS.PASSWORD_VALID_TIMEOUT);
                } else {
                    triggerOwnListener(STATUS.CONNECT_FAIL);
                }
                Log.e(tag, e.getMessage());
            }
        };
    }

    private void triggerOwnListener(STATUS status) {
        if (ownListener != null) {
            ownListener.onFinish(status);
        }
    }

    //-----------------------------------------------------------------
//Change slot data
    private void setSlotData_4_info() {
        // create a uid instance
        infoFrame.setFrameType(FrameType.FrameDeviceInfo);//If you Edit the parameters of the slot, you must enter this code to set the Frame Type.
        // the other parameters of slot
        int curSlot = infoFrame.getCurSlot();
        infoFrame.setAdvtxPower(Integer.valueOf(landmark.getTxPwr())); // RSSI@0m
        infoFrame.setRadiotxPower(Integer.valueOf(landmark.getTxPwr())); // Radio txpower
        // write to device.
        // detail: 1.let No.0 slot advertise iBeacon data.
        //         2.set No.0 slot advertisement interval to 600ms, RSSI@0m to -3dbm, radio txpower to 4dbm
        //The second parameter is the curSlot number
        beaconConnectionHandler.writeSlotFrame(infoFrame, curSlot, new MTCOperationCallback() {
            @Override
            public void onOperation(boolean success, MTException mtException) {
                if (success) {
                    minewFrame = allFrames.get(2);
                    getFrameinfo();
                }
                try {
                    Thread.sleep(1500);
                } catch (Exception e){}

                if (beacon_changed) {
                    Log.e("BeaconPlusManager", "changed");

                } else {
                    Log.e("BeaconPlusManager", "Error");
                    triggerOwnListener(STATUS.CONNECT_FAIL);
                }
                stopBeaconScan();
                beaconManager.disconnect(connectingBeacon);
            }
        });
    }

    //---------------------------------------------------------------
// URL Frame data
    private void getFrameinfo() {
        if (minewFrame.getFrameType() == FrameType.FrameDeviceInfo) {
            infoFrame = (DeviceInfoFrame) minewFrame;
            info.put("advInterval", infoFrame.getAdvInterval() + "");// advertisement interval
            info.put("advtxPower", infoFrame.getAdvtxPower() + "");// RSSI@0m
            info.put("radiotxPower", infoFrame.getRadiotxPower() + "");// Radio txpower.
        }
    }

    //-----------------------------------------------------------------
//Change slot data
    private void setSlotData_0_iBeacon() {
        // create a uid instance
        iBeaconFrame.setFrameType(FrameType.FrameiBeacon);
        iBeaconFrame.setAdvtxPower(Integer.valueOf(landmark.getTxPwr())); // RSSI@0m
        iBeaconFrame.setRadiotxPower(Integer.valueOf(landmark.getTxPwr())); // Radio txpower.

        beaconConnectionHandler.writeSlotFrame(iBeaconFrame, 0, new MTCOperationCallback() {
            @Override
            public void onOperation(boolean success, MTException mtException) {
                if (success) {
                    minewFrame = allFrames.get(0);//GET Slot 0 DATA, Slot 0 de
                    getFrameiBeacon();
                } else {
                    beacon_changed = false;
                }
            }
        });
    }


    //----------------------------------------------------------------------
//iBeacon Frame
    private void getFrameiBeacon() {
        if (minewFrame.getFrameType() == FrameType.FrameiBeacon) {
            iBeaconFrame = (IBeaconFrame) minewFrame;
            // Get Base params data.
            iBeacon.put("advInterval", iBeaconFrame.getAdvInterval() + "");// advertisement interval
            Log.e("advInterval", iBeacon.get("advInterval"));
            iBeacon.put("advtxPower", iBeaconFrame.getAdvtxPower() + "");// RSSI@0m
            Log.e("advtxPower", iBeacon.get("advtxPower"));
            iBeacon.put("radiotxPower", iBeaconFrame.getRadiotxPower() + "");// Radio txpower.
            Log.e("radiotxPower", iBeacon.get("radiotxPower"));
        }
    }

    private void startBeaconService() {
        beaconManager.startService();
    }

    private void applyListener() {
        applyBeaconChangedListener();
        applyBeaconScannedListener();
    }

    private void applyBeaconChangedListener() {
        beaconManager.setBluetoothChangedListener(beaconChangedListener);
    }

    private void applyBeaconScannedListener() {
        beaconManager.setMTCentralManagerListener(beaconScannedListener);
    }

    private void initBeaconChangedListener() {
        beaconChangedListener = new OnBluetoothStateChangedListener() {
            @Override
            public void onStateChanged(BluetoothState state) {
                switch (state) {
                    case BluetoothStateNotSupported:
                        break;
                    case BluetoothStatePowerOff:
                        break;
                    case BluetoothStatePowerOn:
                        break;
                }
            }
        };
    }

    private void initBeaconScannedListener() {
        beaconScannedListener = new MTCentralManagerListener() {
            @Override
            public void onScanedPeripheral(final List<MTPeripheral> beacons) {
                onBeaconScannedEvent(beacons);
            }
        };
    }

    private void onBeaconScannedEvent(List<MTPeripheral> beacons) {
        setLandmarks(beacons);
    }

    private void setLandmarks(List<MTPeripheral> beacons) {
        landmarks = beacons;
    }

    public void tryConnect() {
        beaconManager.connect(connectingBeacon, connectionStatueListener);
    }

    private void initBeaconReconnection() {
        beaconReconnection = new CountDownTimer(beaconTimeout*1000, 1000) {
            @Override
            public void onFinish() {
                try {
                    tryDisconnectBeacon();
                    if (isMatchedBeaconExist()) {
                        connectingBeacon = getMatchedBeacon();
                    }
                    lastConnectedBeacon = connectingBeacon;

                    Log.e(tag,"Device: "+connectingBeacon.mMTFrameHandler.getMac());
                    tryConnect();
                    Log.e(tag,"Start Connect");
                    stopBeaconScan();
                }catch (Exception e){
                    triggerOwnListener(STATUS.CONNECT_FAIL);
                    Log.e(tag,e.toString());
                }
            }

            @Override
            public void onTick(long millilUntilFinished) {

                poinNum = (int)millilUntilFinished % 6;

            }
        };
        beaconReconnection.start();
    }

    private void disconnectBeacon() throws Exception {
        Log.e(tag, "Disconnect");
        beaconManager.disconnect(lastConnectedBeacon);
    }

    private void tryDisconnectBeacon() {
        try {
            disconnectBeacon();
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }
    }

    private MTPeripheral getMatchedBeacon() throws Exception {
        return landmarks.get(getIndexOfBeaconList());
    }

    private boolean isMatchedBeaconExist() {
        return getIndexOfBeaconList()!=-1;
    }


    private int getIndexOfBeaconList() {
        int index = -1;
        for (int i = 0; i < landmarks.size(); i++) {
            if (landmarks.get(i).mMTFrameHandler.getMac().lastIndexOf(landmark.getMacAddr()) != -1) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void setLandmark(Landmark landmark) {
        this.landmark = landmark;
    }

    public Landmark getLandmark() {
        return landmark;
    }
}
