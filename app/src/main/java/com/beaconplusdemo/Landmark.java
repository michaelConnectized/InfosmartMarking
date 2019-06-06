package com.beaconplusdemo;

public class Landmark {
    private String macAddr;
    private String txPwr;
    private String pwd;

    public Landmark(String macAddr, String txPwr, String pwd) {
        this.macAddr = macAddr;
        this.txPwr = txPwr;
        this.pwd = pwd;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getTxPwr() {
        return txPwr;
    }

    public void setTxPwr(String txPwr) {
        this.txPwr = txPwr;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
