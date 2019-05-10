package com.example.user.markingactivity;

import java.util.ArrayList;
import java.util.Collections;

public class Room {
    private ArrayList<Area> areas;
    private String name;

    public Room(String room_name) {
        areas = new ArrayList<Area>();
        setName(room_name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addArea(String area_name) {
        areas.add(new Area(area_name));
    }

    public ArrayList<Area> getAreas() {
        return areas;
    }

    public Area getArea(int index) {
        if (index < areas.size())
            return areas.get(index);
        else return null;
    }

    public void setArea(int index, String area_name) {
        Area tmpArea = areas.get(index);
        tmpArea.setName(area_name);
        areas.set(index, tmpArea);
    }

    public ArrayList<String> getAreasString() {
        int tmpNum = 1;

        ArrayList<String> tmp = new ArrayList<String>();
        for (int i=0; i<areas.size(); i++) {
            tmp.add(String.valueOf(tmpNum++)+". "+areas.get(i).getName());
        }
        return tmp;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj instanceof Room){
            Room room = (Room) obj;
            retVal = (room.name.equals(this.name));
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
        return "Room Name: "+name+"\n"+areas.toString();
    }
}
