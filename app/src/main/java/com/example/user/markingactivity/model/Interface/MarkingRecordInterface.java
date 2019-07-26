package com.example.user.markingactivity.model.Interface;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.user.markingactivity.model.Object.MarkingRecord;

import java.util.List;

@Dao
public interface MarkingRecordInterface {

    @Query("SELECT count(*) FROM MarkingRecord WHERE deleteDatetime<0 OR deleteDatetime IS NULL")
    int getCount();

    @Query("SELECT * FROM MarkingRecord WHERE deleteDatetime<0 OR deleteDatetime IS NULL")
    List<MarkingRecord> getAll();

    @Query("SELECT * FROM MarkingRecord WHERE Uuid IN (:uuid)")
    List<MarkingRecord> loadAllByIds(String[] uuid);

    @Query("UPDATE MarkingRecord SET deleteDatetime=:deleteDatetime WHERE Uuid=:uuid")
    void deleteAll(String uuid, long deleteDatetime);

    @Query("DELETE FROM MarkingRecord WHERE Uuid=:uuid")
    void hardDeleteAll(String uuid);

    @Insert
    void insertMarkingRecord(MarkingRecord markingRecord);

    @Insert
    void insertAll(MarkingRecord... markingRecords);

    @Delete
    void delete(MarkingRecord markingRecord);
}
