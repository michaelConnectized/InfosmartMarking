package com.example.user.markingactivity.model.Object;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class MarkingRecord {
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "lastUpdateDatetime")
    public String lastUpdateDatetime;

    @ColumnInfo(name = "uuid")
    public String uuid;

    @ColumnInfo(name = "availableAddressId")
    public String availableAddressId;

    @ColumnInfo(name = "addressAssignDate")
    public String addressAssignDate;

    @ColumnInfo(name = "batteryLastUpdateTime")
    public String batteryLastUpdateTime;

    @ColumnInfo(name = "batteryStatus")
    public int batteryStatus;

    @ColumnInfo(name = "functional")
    public boolean functional;

    @ColumnInfo(name = "deleteDatetime")
    public String deleteDatetime;
}
