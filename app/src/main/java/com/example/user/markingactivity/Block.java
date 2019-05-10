package com.example.user.markingactivity;

import java.util.ArrayList;
import java.util.Collections;

public class Block {
    private ArrayList<Floor> floors;
    private String name;

    public Block(String block_name) {
        floors = new ArrayList<Floor>();
        setName(block_name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addFloor(String floor_name) {
        floors.add(new Floor(floor_name));
    }

    public ArrayList<Floor> getFloors() {
        return floors;
    }

    public Floor getFloor(int index) {
        if (index < floors.size())
            return floors.get(index);
        else return null;
    }

    public void setFloor(int index, String floor_name) {
        Floor tmpFloor = floors.get(index);
        tmpFloor.setName(floor_name);
        floors.set(index, tmpFloor);
    }

    public ArrayList<String> getFloorsString() {
        ArrayList<String> tmp = new ArrayList<String>();
        for (int i=0; i<floors.size(); i++) {
            tmp.add(floors.get(i).getName());
        }
        return tmp;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj instanceof Block){
            Block block = (Block) obj;
            retVal = (block.name.equals(this.name));
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
        return "\nBlock Name: "+name+"\n"+floors.toString();
    }
}