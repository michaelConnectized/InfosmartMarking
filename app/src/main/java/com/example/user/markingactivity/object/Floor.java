package com.example.user.markingactivity.object;

import java.util.ArrayList;

public class Floor {
    private ArrayList<Room> rooms;
    private String name;

    public Floor(String floor_name) {
        rooms = new ArrayList<Room>();
        setName(floor_name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addRoom(String room_name) {
        rooms.add(new Room(room_name));
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public Room getRoom(int index) {
        if (index < rooms.size())
            return rooms.get(index);
        else return null;
    }

    public void setRoom(int index, String room_name) {
        Room tmpRoom = rooms.get(index);
        tmpRoom.setName(room_name);
        rooms.set(index, tmpRoom);
    }

    public ArrayList<String> getRoomsString() {
        int tmpNum = 1;

        ArrayList<String> tmp = new ArrayList<String>();
        for (int i=0; i<rooms.size(); i++) {
            tmp.add(String.valueOf(tmpNum++)+". "+rooms.get(i).getName());
        }
        return tmp;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj instanceof Floor){
            Floor floor = (Floor) obj;
            retVal = (floor.name.equals(this.name));
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
        return "Floor Name: "+name+"\n"+rooms.toString();
    }
}