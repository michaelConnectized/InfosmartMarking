package com.example.user.markingactivity.model.Dao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.user.markingactivity.model.Excel_Server;
import com.example.user.markingactivity.model.MarkingRecordDB;
import com.example.user.markingactivity.object.MarkingRecord;
import com.example.user.markingactivity.utils.Network.MyNetwork;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DaoDatabase {
    private final String tag = "DaoDatabase";

    private ProgressDialog dialog;
    private Activity activity;
    private SharedPreferences sp;
    private MyNetwork mNetwork;

    public DaoDatabase(Activity activity) {
        this.activity = activity;
        mNetwork = new MyNetwork(activity);
        dialog = new ProgressDialog(activity);
    }

    public void checkDaoNSendToServer() throws InterruptedException {
        MarkingRecordDB db = Room.databaseBuilder(activity, MarkingRecordDB.class, "markingRecord").allowMainThreadQueries().build();
        if (mNetwork.networkTurnOnAndValid()) {
            int tmp_count = db.markingRecordDao().getCount();
            int progressCount = 0;
            Log.e(tag, "Get Count: "+tmp_count);
            if (tmp_count>0) {
                dialog.setMessage(progressCount+"/"+tmp_count);
                dialog.show();
                try {
                    List<MarkingRecord> tmp_list = toObjectMarkingRecords(db.markingRecordDao().getAll());
                    for (int i=0; i<tmp_list.size(); i++) {
                        String response = new Excel_Server(activity, Excel_Server.ACTION_ASSIGN_ADDRESS, tmp_list.get(i).toString()).execute().get();
                        Log.d(tag, response);
                        JSONObject tmpJSON = new JSONObject(response).getJSONObject("result");
                        if (tmpJSON.getBoolean("success")) {
                            db.markingRecordDao().hardDeleteAll(tmp_list.get(i).getUuid());
                            dialog.setMessage((++progressCount)+"/"+tmp_count);
                        }
                    }
                    if (tmp_count==progressCount) {
                        Toast.makeText(activity, "Upload Successful!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity, (tmp_count-progressCount)+" records are failed to upload!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e(tag, e.toString());
                }
            } else {
                Toast.makeText(activity, "No data are stored!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, "There is no network available!", Toast.LENGTH_LONG).show();
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        db.close();
    }

    public void insertMarkingRecord(MarkingRecord markingRecord) {
        MarkingRecordDB db = Room.databaseBuilder(activity, MarkingRecordDB.class, "markingRecord").allowMainThreadQueries().build();
        Log.e("DaoDatabase", "Insert To Dao: "+markingRecord.toString());
        db.markingRecordDao().insertMarkingRecord(toDbMarkingRecord(markingRecord));
        db.close();
    }

    public int getCount() {
        MarkingRecordDB db = Room.databaseBuilder(activity, MarkingRecordDB.class, "markingRecord").allowMainThreadQueries().build();
        int count = db.markingRecordDao().getCount();
        db.close();
        Log.e(tag, "Get Count: "+count);
        return count;
    }

    public static List<com.example.user.markingactivity.model.Object.MarkingRecord> toDbMarkingRecords(List<MarkingRecord> objs) {
        List<com.example.user.markingactivity.model.Object.MarkingRecord> dbs = new ArrayList<>();
        for (int i=0; i<objs.size(); i++) {
            dbs.add(toDbMarkingRecord(objs.get(i)));
        }
        return dbs;
    }

    public static com.example.user.markingactivity.model.Object.MarkingRecord toDbMarkingRecord(MarkingRecord obj) {
        com.example.user.markingactivity.model.Object.MarkingRecord db = new com.example.user.markingactivity.model.Object.MarkingRecord();
        db.lastUpdateDatetime = obj.getLastUpdateDatetime();
        db.uuid = obj.getUuid();
        db.availableAddressId = obj.getAvailableAddressId();
        db.addressAssignDate = obj.getAddressAssignDate();
        db.batteryLastUpdateTime = obj.getBatteryLastUpdateTime();
        db.batteryStatus = obj.getBatteryStatus();
        db.functional = obj.isFunctional();

        return db;
    }

    public static List<MarkingRecord> toObjectMarkingRecords(List<com.example.user.markingactivity.model.Object.MarkingRecord> dbs) {
        List<MarkingRecord> objs = new ArrayList<>();
        for (int i=0; i<dbs.size(); i++) {
            objs.add(toObjectMarkingRecord(dbs.get(i)));
        }
        return objs;
    }

    public static MarkingRecord toObjectMarkingRecord(com.example.user.markingactivity.model.Object.MarkingRecord db) {
        MarkingRecord obj = new MarkingRecord();
        obj.setLastUpdateDatetime(db.lastUpdateDatetime);
        obj.setUuid(db.uuid);
        obj.setAvailableAddressId(db.availableAddressId);
        obj.setAddressAssignDate(db.addressAssignDate);
        obj.setBatteryLastUpdateTime(db.batteryLastUpdateTime);
        obj.setBatteryStatus(db.batteryStatus);
        obj.setFunctional(db.functional);

        return obj;
    }
}
