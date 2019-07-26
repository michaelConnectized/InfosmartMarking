package com.example.user.markingactivity.object;

import org.json.JSONException;
import org.json.JSONObject;

public class MarkingRecord {
    private String lastUpdateDatetime;
    private String uuid;
    private String availableAddressId;
    private String addressAssignDate;
    private String batteryLastUpdateTime;
    private int batteryStatus;
    private boolean functional;

    public MarkingRecord() {

    }

    public String getLastUpdateDatetime() {
        return lastUpdateDatetime;
    }

    public void setLastUpdateDatetime(String lastUpdateDatetime) {
        this.lastUpdateDatetime = lastUpdateDatetime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAvailableAddressId() {
        return availableAddressId;
    }

    public void setAvailableAddressId(String availableAddressId) {
        this.availableAddressId = availableAddressId;
    }

    public String getAddressAssignDate() {
        return addressAssignDate;
    }

    public void setAddressAssignDate(String addressAssignDate) {
        this.addressAssignDate = addressAssignDate;
    }

    public String getBatteryLastUpdateTime() {
        return batteryLastUpdateTime;
    }

    public void setBatteryLastUpdateTime(String batteryLastUpdateTime) {
        this.batteryLastUpdateTime = batteryLastUpdateTime;
    }

    public int getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(int batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void setFunctional(boolean functional) {
        this.functional = functional;
    }

    public String toString() {
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lastUpdateDatetime", lastUpdateDatetime);
            jsonObject.put("uuid", uuid);
            jsonObject.put("availableAddressId", availableAddressId);
            jsonObject.put("addressAssignDate", addressAssignDate);
            jsonObject.put("batteryLastUpdateTime", batteryLastUpdateTime);
            jsonObject.put("batteryStatus", batteryStatus);
            jsonObject.put("functional", functional);
            result = jsonObject.toString();
        } catch (JSONException e) {
            result = "";
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj instanceof MarkingRecord){
            MarkingRecord markingRecord = (MarkingRecord) obj;
            retVal = (markingRecord.getUuid().equals(this.uuid));
        }

        return retVal;
    }
}
