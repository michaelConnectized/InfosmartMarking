package com.example.user.markingactivity.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/*
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;*/
//import org.apache.poi.hssf.usermodel.*;
import com.example.user.markingactivity.baseAdapter.confirmAdapter;
import com.example.user.markingactivity.model.Dao.DaoDatabase;
import com.example.user.markingactivity.model.ExcelPath;
import com.example.user.markingactivity.model.Excel_Server;
import com.example.user.markingactivity.object.Locations;
import com.example.user.markingactivity.R;
import com.example.user.markingactivity.object.MarkingRecord;
import com.example.user.markingactivity.object.mDevice;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ConfirmActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_CONSTANT = 2;
    private String tag = "ConfirmActivity";

    File file;
    Workbook wb;
    Sheet sheet;
    TextView tv_at1;
    TextView tv_at2;
    TextView tv_at3;
    TextView tv_at4;
    TextView tv_project;
    ListView lv;

    String project_name;
    int project_id;
    int address_row_num;
    int mDevCount;
    ArrayList<mDevice> mDevs;
    String address_id;

    Locations locs;
    String[] addresses;
    String fileExtension;

    private SharedPreferences preferences;
    private boolean server_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        tryOnCreateEvent();
    }

    private void tryOnCreateEvent() {
        try {
            initVar();
            initVarFromIntentExtra();
            initProjectInfo();
            initUI();
            Log.e(tag, "inited");
        } catch (Exception e) {
            Log.e(tag, e.toString());
            finishActivity();
        }
    }

    private void initVar() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        locs = new Locations(this);
        mDevs = new ArrayList<mDevice>();

        tv_at1 = findViewById(R.id.tv_at1);
        tv_at2 = findViewById(R.id.tv_at2);
        tv_at3 = findViewById(R.id.tv_at3);
        tv_at4 = findViewById(R.id.tv_at4);
        tv_project = findViewById(R.id.tv_project);
        lv = findViewById(R.id.lv);

        server_mode = preferences.getBoolean("server_mode", true);
    }

    private void initVarFromIntentExtra() {
        Intent intent = getIntent();
        project_id = intent.getIntExtra("project_id", 0);
        addresses = intent.getStringArrayExtra("addresses");
        mDevCount = intent.getIntExtra("mDevCount", 0);
        address_id = intent.getStringExtra("address_id");
        address_row_num = intent.getIntExtra("row_num", 0);
        for (int i=0; i<mDevCount; i++) {
            if ( intent.getSerializableExtra("mDevs"+i)!=null)
                mDevs.add((mDevice) intent.getSerializableExtra("mDevs"+i));
        }
    }

    private void initProjectInfo() {
        if (server_mode) {
            locs.setProjectsFromServer();
        } else {
            locs.setProjectsFromFiles(ExcelPath.homepage, "addr");
        }

        project_name = locs.getProject(project_id).getName();

        fileExtension = "";
        if (!project_name.equals("Unnamed")) {
            fileExtension = "-"+project_name;
        }
    }

    private void initUI() {
        tv_project.setText(project_name);
        tv_at1.setText(addresses[0]);
        tv_at2.setText(addresses[1]);
        tv_at3.setText(addresses[2]);
        tv_at4.setText(addresses[3]);

        lv.setAdapter(new confirmAdapter(this, mDevs));
    }

    public void onClickBtnConfirm(View view) {
        Log.d(tag, "Click Confirm");
        try {
            if (server_mode) {
                Log.d(tag, "server mode");
                sendDataToServer();
            } else {
                Log.d(tag, "excel mode");
                sendDataToExcel();
            }
        } catch (Exception e) {
            Log.d(tag, e.toString());
        }
    }

    private void sendDataToServer() throws InterruptedException, ExecutionException, JSONException {
        Log.d(tag, "Start Sending: "+mDevs.size());
        new Excel_Server(this, Excel_Server.ACTION_INIT).execute().get();
        boolean success = true;

        List<MarkingRecord> markingRecordList = new ArrayList<>();
        List<MarkingRecord> uploadedMarkingRecordList = new ArrayList<>();
        for (int i=0; i<mDevs.size(); i++) {
            String time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(Calendar.getInstance().getTime());

            MarkingRecord markingRecord = new MarkingRecord();
            markingRecord.setLastUpdateDatetime(mDevs.get(i).getLastUpdateDatetime());
            markingRecord.setUuid(mDevs.get(i).getmUUID());
            markingRecord.setAvailableAddressId(address_id);
            markingRecord.setAddressAssignDate(time);
            markingRecord.setBatteryLastUpdateTime(time);
            markingRecord.setBatteryStatus(mDevs.get(i).getBatteryLevel());
            markingRecord.setFunctional(true);

            markingRecordList.add(markingRecord);

        }

        for (int i=0; i<markingRecordList.size(); i++) {
            Log.d(tag, markingRecordList.get(i).toString());
            String response = new Excel_Server(this, Excel_Server.ACTION_ASSIGN_ADDRESS, markingRecordList.get(i).toString()).execute().get();
            Log.d(tag, response);

            boolean isThisSuccess = false;
            String errMsg = "";
            if (isJsonObject(response)) {
                JSONObject tmpJSON = new JSONObject(response);
                if (tmpJSON.has("result")) {
                    tmpJSON = tmpJSON.getJSONObject("result");
                    if (tmpJSON.getBoolean("success")) {
                        isThisSuccess = true;
                    } else {
                        errMsg = tmpJSON.get("message").toString();
                    }
                }
            }
            if (isThisSuccess) {
                uploadedMarkingRecordList.add(markingRecordList.get(i));
            } else {
                success = false;
                addToDao(markingRecordList.get(i));
                Toast.makeText(this, "Fail: "+errMsg, Toast.LENGTH_LONG).show();
            }
        }

        for (int i=0; i<uploadedMarkingRecordList.size(); i++) {
            markingRecordList.remove(uploadedMarkingRecordList.get(i));
        }

        if (success) {
            Toast.makeText(this, "Update Successful!", Toast.LENGTH_LONG).show();
            finishUploadActivity();
        } else {
            Toast.makeText(this, "Update Fail, Data are stored!", Toast.LENGTH_LONG).show();
            finishUploadActivity();
        }
    }

    private boolean isJsonObject(String ori) {
        try {
            new JSONObject(ori);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    private void addToDao(MarkingRecord markingRecord) {
        DaoDatabase daoDatabase = new DaoDatabase(this);
        daoDatabase.insertMarkingRecord(markingRecord);
    }

    private void sendDataToExcel() throws IOException {
        createXLSXFile();
        for (mDevice mDev: mDevs) {
            appendXLSXFile(mDev);
        }
        saveXLSXFile();
    }

    public void createXLSXFile() throws IOException {
        File path = new File(Environment.getExternalStorageDirectory()+"/infosmart");
        if (!path.exists()) {
            path.mkdirs();
        }

        try {
            file = new File(ExcelPath.xlsx(ExcelPath.Marking+fileExtension));
            if (!file.exists() || file.length()==0) {
                wb = new XSSFWorkbook();
                sheet = wb.createSheet("Sheet1");
                Row row = sheet.createRow(0);

                Cell cell = row.createCell(0);
                cell.setCellValue("uuid");
                cell = row.createCell(1);
                cell.setCellValue("rfPower");
                cell = row.createCell(2);
                cell.setCellValue("addressAssignDate");
                cell = row.createCell(3);
                cell.setCellValue("block");
                cell = row.createCell(4);
                cell.setCellValue("floor");
                cell = row.createCell(5);
                cell.setCellValue("room");
                cell = row.createCell(6);
                cell.setCellValue("areaWithin");
                cell = row.createCell(7);
                cell.setCellValue("batteryStatus");
                cell = row.createCell(8);
                cell.setCellValue("batteryLastUpdateTime");
                cell = row.createCell(9);
                cell.setCellValue("validationCode");
                cell = row.createCell(10);
                cell.setCellValue("functional");
                sheet.setPrintGridlines(true);
            } else {
                FileInputStream inputStream = new FileInputStream(new File(ExcelPath.xlsx(ExcelPath.Marking+fileExtension)));
                wb =  WorkbookFactory.create(inputStream);
                sheet = wb.getSheetAt(0);
            }
        } catch (Exception e) {
            Log.d("Files", e.toString());
        }
    }

    public void appendXLSXFile(mDevice mDev) throws IOException {
        try {
            int lastRow = sheet.getLastRowNum();
            Row row = sheet.createRow(++lastRow);

            Cell cell;
            cell = row.createCell(0);
            cell.setCellValue(mDev.getmUUID());
            cell = row.createCell(1);
            cell.setCellValue(mDev.getPower());
            cell = row.createCell(2);
            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            cell = row.createCell(3);
            cell.setCellValue(addresses[0]);
            cell = row.createCell(4);
            cell.setCellValue(addresses[1]);
            cell = row.createCell(5);
            cell.setCellValue(addresses[2]);
            cell = row.createCell(6);
            cell.setCellValue(addresses[3]);
            cell = row.createCell(7);
            cell.setCellValue(String.valueOf(mDev.getBatteryLevel()));
            cell = row.createCell(8);
            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            cell = row.createCell(9);
            cell.setCellValue("0000");
            cell = row.createCell(10);
            cell.setCellValue("");
        } catch (Exception e) {
            Log.d("Files", e.toString());
        }
    }

    public void saveXLSXFile() throws IOException{
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_CONSTANT);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_CONSTANT);
            FileOutputStream fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            fileOut.close();

            file = new File(ExcelPath.xlsx(ExcelPath.Marking+fileExtension));
            if (file.exists() && file.canRead()) {
                sendBroadcast( new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                Toast.makeText(this, "Update MarkingData"+fileExtension+".xlsx Successful", Toast.LENGTH_LONG).show();
                markAddressXLSXFile();
                markLandmarkXLSXFile();
                Intent myIntent = new Intent(ConfirmActivity.this, AddressSelectActivity.class);
                myIntent.putExtra("project_id", project_id);

                ConfirmActivity.this.startActivity(myIntent);
                this.finish();
            }
        } catch (Exception e) {
            Log.d("Files", e.toString());
            Toast.makeText(this,  "Error: "+e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void markAddressXLSXFile() throws IOException {
        try {
            File file = new File(ExcelPath.xlsx(ExcelPath.Address+fileExtension));
            if (file.exists() && file.length()>0) {
                FileInputStream excelFile = new FileInputStream(file);
                Workbook wb = WorkbookFactory.create(excelFile);
                Sheet sheet = wb.getSheetAt(0);

                Row row = sheet.getRow(address_row_num);
                if (row == null) {
                    row = sheet.createRow(address_row_num);
                }
                row.getCell(9, Row.CREATE_NULL_AS_BLANK).setCellValue("marked");

                FileOutputStream fileOut = new FileOutputStream(file);
                wb.write(fileOut);
                fileOut.close();
                Toast.makeText(this, "Update addr" + fileExtension + ".xlsx Successful", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("Files", e.toString());
        }
    }

    public void markLandmarkXLSXFile() throws IOException {
        try {
            File file = new File(ExcelPath.xlsx(ExcelPath.FilterLandmark+fileExtension));
            if (file.exists() && file.length()>0) {
                FileInputStream excelFile = new FileInputStream(file);
                Workbook wb = WorkbookFactory.create(excelFile);
                Sheet sheet = wb.getSheetAt(0);
                for (mDevice mDev: mDevs) {
                    Row row = sheet.getRow(mDev.getExcelIndex());
                    if(row == null){
                        row = sheet.createRow(mDev.getExcelIndex());
                    }
                    row.getCell(6, Row.CREATE_NULL_AS_BLANK).setCellValue("marked");
                }
                FileOutputStream fileOut = new FileOutputStream(file);
                wb.write(fileOut);
                fileOut.close();
                Toast.makeText(this, "Update lm"+fileExtension+".xlsx Successful", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("Files", e.toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finishActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        finishActivity();
    }

    public void finishActivity() {
        Intent myIntent = new Intent(ConfirmActivity.this, ScanActivity.class);
        myIntent.putExtra("project_id", project_id);
        myIntent.putExtra("addresses", addresses);
        ConfirmActivity.this.startActivity(myIntent);
        this.finish();
    }
    public void finishUploadActivity() {
        Intent myIntent = new Intent(ConfirmActivity.this, AddressSelectActivity.class);
        myIntent.putExtra("project_id", project_id);
        ConfirmActivity.this.startActivity(myIntent);
        this.finish();
    }
}
