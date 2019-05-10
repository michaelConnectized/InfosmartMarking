package com.example.user.markingactivity;

import java.util.ArrayList;

public class Area {
    public static final int status_unmarked = 0;
    public static final int status_unaccept = 1;
    public static final int status_accepted = 2;
    public static final int status_marked = 3;

    private ArrayList<String> uuids;
    private String name;
    private int status;
    private int row_num;

    public Area(String area_name) {
        uuids = new ArrayList<String>();
        setName(area_name);
        status = status_unmarked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addUuid(String uuid) {
        uuids.add(uuid);
    }

    public ArrayList<String> getUuids() {
        return uuids;
    }

    public String getUuid(int index) {
        return uuids.get(index);
    }

    public void setUuid(int index, String uuid) {
        uuids.set(index, uuid);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRow_num() {
        return row_num;
    }

    public void setRow_num(int row_num) {
        this.row_num = row_num;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj instanceof Area){
            Area area = (Area) obj;
            retVal = (area.name.equals(this.name));
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    public String toString() {
        return "Area Name: "+name+", Row num: "+String.valueOf(row_num);
    }
}
