package com.example.user.markingactivity.model;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.user.markingactivity.model.Interface.MarkingRecordInterface;
import com.example.user.markingactivity.model.Object.MarkingRecord;

@Database(entities = {MarkingRecord.class}, version = 1)
public abstract class MarkingRecordDB extends RoomDatabase {
    public abstract MarkingRecordInterface markingRecordDao();
}






