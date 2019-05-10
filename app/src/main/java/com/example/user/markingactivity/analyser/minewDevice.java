package com.example.user.markingactivity.analyser;

import java.util.Arrays;

public class minewDevice {
    private int data_length;
    private int flag_data_type;
    private int flag_data;
    private int data_length2;
    private int complete_list_of_uuids;
    private String uuid_data;
    private int data_length3;
    private int service_data;
    private String uuid_data2;
    private int frame_type;
    private int version_number;
    private int battery_level;
    private String mac_address;
    private byte[] name;

    public minewDevice(byte[] oriData) {
        data_length = Arrays.copyOfRange(oriData, 0, 1)[0];
        flag_data_type = Arrays.copyOfRange(oriData, 1, 2)[0];
        flag_data = Arrays.copyOfRange(oriData, 2, 3)[0];
        data_length2 = Arrays.copyOfRange(oriData, 3, 4)[0];
        complete_list_of_uuids = Arrays.copyOfRange(oriData, 4, 5)[0];

        byte[] uuid_data_byte = Arrays.copyOfRange(oriData, 5, 7);
        uuid_data = Integer.toHexString(uuid_data_byte[0] & 0xFF)+ Integer.toHexString(uuid_data_byte[1] & 0xFF);

        data_length3 = Arrays.copyOfRange(oriData, 7, 8)[0];
        service_data = Arrays.copyOfRange(oriData, 8, 9)[0];

        byte[] uuid_data2_byte = Arrays.copyOfRange(oriData, 9, 11);
        uuid_data2 = Integer.toHexString(uuid_data2_byte[0] & 0xFF)+ Integer.toHexString(uuid_data2_byte[1] & 0xFF);

        frame_type = Arrays.copyOfRange(oriData, 11, 12)[0];
        version_number = Arrays.copyOfRange(oriData, 12, 13)[0];
        battery_level = Arrays.copyOfRange(oriData, 13, 14)[0];

        //Mac address
        byte[] mac_address_byte = Arrays.copyOfRange(oriData, 14, 20);
        mac_address = Integer.toHexString(mac_address_byte[mac_address_byte.length-1] & 0xFF).toUpperCase();
        for (int i=mac_address_byte.length-2; i>=0; i--) {
            mac_address += ":"+Integer.toHexString(mac_address_byte[i] & 0xFF).toUpperCase();
        }

        name = Arrays.copyOfRange(oriData, 20, oriData.length);
    }

    public boolean isMinew() {
        return uuid_data.equals("e1ff");
    }

    public int getData_length() {
        return data_length;
    }

    public int getFlag_data_type() {
        return flag_data_type;
    }

    public int getFlag_data() {
        return flag_data;
    }

    public int getData_length2() {
        return data_length2;
    }

    public int getComplete_list_of_uuids() {
        return complete_list_of_uuids;
    }

    public String getUuid_data() {
        return uuid_data;
    }

    public int getData_length3() {
        return data_length3;
    }

    public int getService_data() {
        return service_data;
    }

    public String getUuid_data2() {
        return uuid_data2;
    }

    public int getFrame_type() {
        return frame_type;
    }

    public int getVersion_number() {
        return version_number;
    }

    public int getBattery_level() {
        return battery_level;
    }

    public String getMac_address() {
        return mac_address;
    }

    public byte[] getName() {
        return name;
    }
}
