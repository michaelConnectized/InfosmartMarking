package com.example.user.markingactivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class AddressSelectActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_CONSTANT = 2;
    private String tag = "address_select_log";
    Button ddn_block;
    Button ddn_floor;
    Button ddn_room;
    Button ddn_area;
    TextView tv_project;
    Button btn_rmlm;
    Button btn_markedlm;
    Button btn_unmarked;
    Button btn_marked;

    CheckBox chk_include;
    LinearLayout ll_addresses_table;
    LinearLayout ll_landmarks_table;

    String project_name;
    int project_id;
    int block_id = -1;
    int floor_id = -1;
    int room_id = -1;
    int area_id = -1;
    ArrayList<String> blockStrings;
    ArrayList<String> floorStrings;
    ArrayList<String> roomStrings;
    ArrayList<String> areaStrings;

    String fileExtension;

    int unmarked_address = 0;
    int marked_address = 0;
    int remained_landmark = 0;
    int marked_landmark = 0;
    ArrayList<String> unmarked_addresses;
    ArrayList<String> marked_addresses;
    ArrayList<String> marked_address_ids;
    ArrayList<String> remained_landmarks;
    ArrayList<String> marked_landmarks;

    Locations locs;
    Locations filteredLocs;
    int showing_status = mStatusList.ADDRESS_STATUS_INCLUDE_UNMARKED;

    private SharedPreferences preferences;
    private boolean server_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_select);
        tryOnCreateEvent();
    }

    private void tryOnCreateEvent() {
        try {
            init();
            server_mode = preferences.getBoolean("server_mode", true);

            Intent intent = getIntent();
            project_id = intent.getIntExtra("project_id", 0);

            if (server_mode) {
                locs.setProjectsFromServer();
                filteredLocs.setProjectsFromServer();

                project_name = locs.getProject(project_id).getName();
                getLandmarksFromServer();
                getAddressFromServer();
            } else {
                locs.setProjectsFromFiles(ExcelPath.homepage, "addr");
                filteredLocs.setProjectsFromFiles(ExcelPath.homepage, "addr");

                project_name = locs.getProject(project_id).getName();

                if (!project_name.equals("Unnamed")) {
                    fileExtension = "-"+project_name;
                }
                getAddressFromXLSX(locs, mStatusList.ADDRESS_STATUS_INCLUDE_ALL);
                getAddressFromXLSX(filteredLocs, mStatusList.ADDRESS_STATUS_INCLUDE_UNMARKED);
                getLandmarksFromXLSX();
            }

            if (locs.getBlocks().size()>0) {
                block_id = 0;
            } else {
                block_id = -1;
            }



            btn_unmarked.setText(String.valueOf(unmarked_address));
            btn_marked.setText(String.valueOf(marked_address));
            btn_rmlm.setText(String.valueOf(remained_landmark));
            btn_markedlm.setText(String.valueOf(marked_landmark));

            tv_project.setText(project_name);

            AddAddressDropDown();
            editAddressTable(unmarked_addresses);
            editLandmarkTable(remained_landmarks);
        } catch (Exception e) {
            Log.e(tag, e.toString());
            finishActivity();
        }
    }

    private void init() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ddn_block = findViewById(R.id.ddn_block);
        ddn_floor = findViewById(R.id.ddn_floor);
        ddn_room = findViewById(R.id.ddn_room);
        ddn_area = findViewById(R.id.ddn_area);
        tv_project = findViewById(R.id.tv_project);
        btn_rmlm = findViewById(R.id.btn_rmlm);
        btn_markedlm = findViewById(R.id.btn_markedlm);
        btn_unmarked = findViewById(R.id.btn_unmarked);
        btn_marked = findViewById(R.id.btn_marked);
        chk_include = findViewById(R.id.chk_include);
        ll_addresses_table = findViewById(R.id.ll_addresses_table);
        ll_landmarks_table = findViewById(R.id.ll_landmarks_table);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        unmarked_addresses = new ArrayList<String>();
        marked_addresses = new ArrayList<String>();
        remained_landmarks = new ArrayList<String>();
        marked_landmarks = new ArrayList<String>();

        locs = new Locations(this);
        filteredLocs = new Locations(this);

        fileExtension = "";
    }

    private void AddAddressDropDown() {
        Locations tmpLocs = getCurrentLocs();

        blockStrings = tmpLocs.getBlocksString();
        floorStrings = new ArrayList<String>();
        roomStrings = new ArrayList<String>();
        areaStrings = new ArrayList<String>();
        updateBlock();
    }

    public void onClickBtnGo(View view) {
        String[] addresses = new String[]{"", "", "", ""};
        Locations tmpLoc = getCurrentLocs();

        if (tmpLoc.getBlocks().size()==0) {
            return;
        }

        if (ddn_block.getText()!=null) { addresses[0] = tmpLoc.getBlock(block_id).getName(); }
        if (ddn_floor.getText()!=null) { addresses[1] = tmpLoc.getBlock(block_id).getFloor(floor_id).getName(); }
        if (ddn_room.getText()!=null) { addresses[2] = tmpLoc.getBlock(block_id).getFloor(floor_id).getRoom(room_id).getName(); }
        if (ddn_area.getText()!=null) { addresses[3] = tmpLoc.getBlock(block_id).getFloor(floor_id).getRoom(room_id).getArea(area_id).getName(); }


        Intent myIntent = new Intent(AddressSelectActivity.this, ScanActivity.class);

        myIntent.putExtra("project_id", project_id);
        myIntent.putExtra("addresses", addresses);
        myIntent.putExtra("row_num", tmpLoc.getBlock(block_id).getFloor(floor_id).getRoom(room_id).getArea(area_id).getRow_num());
        if (server_mode) {
            myIntent.putExtra("address_id", getAddressesIDFromServer(addresses));
        }
        AddressSelectActivity.this.startActivity(myIntent);
        this.finish();
    }

    public void onClickBtnUnmarked(View view) {
        editAddressTable(unmarked_addresses);
    }

    public void onClickBtnMarked(View view) {
        editAddressTable(marked_addresses);
    }

    public void onClickBtnRemainedLm(View view) {
        editLandmarkTable(remained_landmarks);
    }

    public void onClickBtnMarkedLm(View view) {
        editLandmarkTable(marked_landmarks);
    }

    public void onClickChkInclude(View view) {
        if (chk_include.isChecked()) {
            showing_status = mStatusList.ADDRESS_STATUS_INCLUDE_ALL;
        } else {
            showing_status = mStatusList.ADDRESS_STATUS_INCLUDE_UNMARKED;
        }
        updateBlock();
    }

    public void onClickSelectBlock(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Block");
        builder.setItems(blockStrings.toArray(new String[blockStrings.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ddn_block.setText(blockStrings.get(which));
                block_id = which;
                updateFloor();
            }
        });
        builder.show();
    }

    public void onClickSelectFloor(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Floor");
        builder.setItems(floorStrings.toArray(new String[floorStrings.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ddn_floor.setText(floorStrings.get(which));
                floor_id = which;
                updateRoom();
            }
        });
        builder.show();
    }

    public void onClickSelectRoom(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Room");
        builder.setItems(roomStrings.toArray(new String[roomStrings.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ddn_room.setText(roomStrings.get(which));
                room_id = which;
                updateArea();
            }
        });
        builder.show();
    }

    public void onClickSelectArea(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Area within");
        builder.setItems(areaStrings.toArray(new String[areaStrings.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ddn_area.setText(areaStrings.get(which));
                area_id = which;
            }
        });
        builder.show();
    }

    public void updateBlock() {
        Locations tmpLocs = getCurrentLocs();
        if (tmpLocs.getBlocks().size()>0) {
            block_id = 0;
            ddn_block.setText(tmpLocs.getBlocksString().get(0));
        } else {
            block_id = -1;
        }

        blockStrings = tmpLocs.getBlocksString();
        updateFloor();
    }

    public void updateFloor() {
        Locations tmpLocs = getCurrentLocs();

        if (block_id >= 0) {
            ddn_floor.setClickable(true);
            floorStrings = tmpLocs.getBlock(block_id).getFloorsString();
            if (floorStrings.size()>0) {
                floor_id = 0;
            }
            ddn_floor.setText(floorStrings.get(0));
        } else {
            floor_id = -1;
            floorStrings = new ArrayList<String>();
            ddn_floor.setClickable(false);
            ddn_floor.setText("");
        }
        updateRoom();
    }

    public void updateRoom() {
        Locations tmpLocs = getCurrentLocs();

        if (floor_id >= 0 && block_id >= 0) {
            ddn_room.setClickable(true);
            roomStrings = tmpLocs.getBlock(block_id).getFloor(floor_id).getRoomsString();
            if (roomStrings.size()>0) {
                room_id = 0;
            }
            ddn_room.setText(roomStrings.get(0));
        } else {
            room_id = -1;
            roomStrings = new ArrayList<String>();
            ddn_room.setClickable(false);
            ddn_room.setText("");
        }
        updateArea();
    }

    public void updateArea() {
        Locations tmpLocs = getCurrentLocs();

        if (room_id >= 0 && floor_id >= 0 && block_id >= 0) {
            ddn_area.setClickable(true);
            areaStrings = tmpLocs.getBlock(block_id).getFloor(floor_id).getRoom(room_id).getAreasString();
            if (areaStrings.size()>0) {
                area_id = 0;
            }
            ddn_area.setText(areaStrings.get(0));
        } else {
            areaStrings = new ArrayList<String>();
            ddn_area.setClickable(false);
            ddn_area.setText("");
        }
    }

    //getAddressFromXLSX(Para1: storage, Para2: store the data without this status)
    public void getAddressFromXLSX(Locations tmpLocs, int tmpStatus) {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_CONSTANT);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_CONSTANT);
            FileInputStream excelFile = new FileInputStream(new File(ExcelPath.xlsx(ExcelPath.Address+fileExtension)));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            if (iterator.hasNext()) {
                iterator.next();
            } else {
                return;
            }
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                int[] indexes = new int[4];
                String tmpResult = "";
                Block currentBlock = new Block("");
                Floor currentFloor = new Floor("");
                Room currentRoom = new Room("");
                Boolean existed = true;

                String status = "";
                currentRow.getCell(9, Row.CREATE_NULL_AS_BLANK);
                if (currentRow.getCell(9).getCellType() == XSSFCell.CELL_TYPE_STRING) {
                    status = currentRow.getCell(9).getStringCellValue();
                } else if (currentRow.getCell(9).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                    status = (new DataFormatter()).formatCellValue(currentRow.getCell(9));
                } else if (currentRow.getCell(9).getCellType() == XSSFCell.CELL_TYPE_BLANK) {
                    status = "unmarked";
                }

                if (tmpStatus!=0) {
                    if (tmpStatus==mStatusList.ADDRESS_STATUS_INCLUDE_UNMARKED && status.equals("marked")) {
                        continue;
                    }
                }

                int level = Math.round(Float.parseFloat(getStringFromXLSXCell(currentRow, 0)));

                //handle address
                for (int i=1; i<=4; i++) {

                    tmpResult = getStringFromXLSXCell(currentRow, i);
                    //assign the data to block, floor, room's object
                    if (level-i<0){
                        tmpResult = "";
                    }
                    switch (i) {
                        case 1:
                            if (tmpLocs.getBlocks().contains(new Block(tmpResult))) {
                                indexes[0] = tmpLocs.getBlocks().indexOf(new Block(tmpResult));
                            } else {
                                tmpLocs.addBlock(tmpResult);
                                indexes[0] = tmpLocs.getBlocks().size()-1;
                                existed = false;
                            }
                            break;
                        case 2:
                            currentBlock = tmpLocs.getBlock(indexes[0]);
                            if (currentBlock.getFloors().contains(new Floor(tmpResult))) {
                                indexes[1] = currentBlock.getFloors().indexOf(new Floor(tmpResult));
                            } else {
                                tmpLocs.getBlock(indexes[0]).addFloor(tmpResult);
                                indexes[1] = currentBlock.getFloors().size()-1;
                                existed = false;
                            }
                            break;
                        case 3:
                            currentFloor = tmpLocs.getBlock(indexes[0]).getFloor(indexes[1]);
                            if (currentFloor.getRooms().contains(new Room(tmpResult))) {
                                indexes[2] = currentFloor.getRooms().indexOf(new Room(tmpResult));
                            } else {
                                currentFloor.addRoom(tmpResult);
                                indexes[2] = currentFloor.getRooms().size()-1;
                                existed = false;
                            }
                            break;
                        case 4:
                            currentRoom = tmpLocs.getBlock(indexes[0]).getFloor(indexes[1]).getRoom(indexes[2]);
                            if (currentRoom.getAreas().contains(new Area(tmpResult))) {
                                indexes[3] = currentRoom.getAreas().indexOf(new Area(tmpResult));
                            } else {
                                currentRoom.addArea(tmpResult);
                                indexes[3] = currentRoom.getAreas().size()-1;
                                existed = false;
                            }
                            break;
                        default:
                            Log.d(tag, String.valueOf(i));
                    }
                }

                Area currentArea = tmpLocs.getBlock(indexes[0]).getFloor(indexes[1]).getRoom(indexes[2]).getArea(indexes[3]);

                if (existed) {
                    continue;
                }
                currentArea.setRow_num(currentRow.getRowNum());

                if (tmpStatus!=0 && tmpStatus==mStatusList.ADDRESS_STATUS_INCLUDE_UNMARKED) {
                    continue;
                }

                String tmpAddress = currentBlock.getName()+", "+currentFloor.getName()+", "+currentRoom.getName()+", "+currentArea.getName();
                if (status.equals("unmarked")) {
                    currentArea.setStatus(Area.status_unmarked);
                    unmarked_addresses.add(String.valueOf(currentRow.getRowNum())+". "+tmpAddress);
                    unmarked_address++;
                }
                if (status.equals("marked")) {
                    currentArea.setStatus(Area.status_marked);
                    marked_addresses.add(String.valueOf(currentRow.getRowNum())+". "+tmpAddress);
                    marked_address++;
                }
            }
        } catch (FileNotFoundException e) {
            Log.d(tag,e.toString());
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(tag,e.toString());
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void getAddressFromServer() {
        try {
            new Excel_Server(this, Excel_Server.ACTION_INIT).execute().get();
            JSONArray data = new JSONArray(new Excel_Server(this, Excel_Server.ACTION_GET_ADDRESSES, locs.getProject(project_id).getId()).execute().get());
            for (int k=0; k<data.length(); k++) {
                JSONObject eaRecord = data.getJSONObject(k);
                int[] indexes = new int[4];
                String tmpResult = "";
                Block currentBlock = new Block("");
                Floor currentFloor = new Floor("");
                Room currentRoom = new Room("");
                Locations tmpLocs;
                boolean existed;
                boolean is_filtered;


                if (!marked_address_ids.contains(eaRecord.getString("_id"))) {
                    is_filtered = true;
                } else {
                    is_filtered = false;
                }
                tmpLocs = locs;
                while (true) {
                    int level = eaRecord.getInt("referenceLevel");
                    existed = true;
                    //handle address
                    for (int i=1; i<=4; i++) {

                        switch (i) {
                            case 1:tmpResult = eaRecord.getJSONArray("address").getJSONObject(1).getString("block"); break;
                            case 2:tmpResult = eaRecord.getJSONArray("address").getJSONObject(1).getString("floor"); break;
                            case 3:tmpResult = eaRecord.getJSONArray("address").getJSONObject(1).getString("room"); break;
                            case 4:tmpResult = eaRecord.getJSONArray("address").getJSONObject(1).getString("areaWithin"); break;
                            default: tmpResult = "";
                        }
                        //assign the data to block, floor, room's object
                        if (level - i < 0) {
                            tmpResult = "";
                        }

                        switch (i) {
                            case 1:
                                if (tmpLocs.getBlocks().contains(new Block(tmpResult))) {
                                    indexes[0] = tmpLocs.getBlocks().indexOf(new Block(tmpResult));
                                } else {
                                    tmpLocs.addBlock(tmpResult);
                                    indexes[0] = tmpLocs.getBlocks().size() - 1;
                                    existed = false;
                                }
                                break;
                            case 2:
                                currentBlock = tmpLocs.getBlock(indexes[0]);
                                if (currentBlock.getFloors().contains(new Floor(tmpResult))) {
                                    indexes[1] = currentBlock.getFloors().indexOf(new Floor(tmpResult));
                                } else {
                                    tmpLocs.getBlock(indexes[0]).addFloor(tmpResult);
                                    indexes[1] = currentBlock.getFloors().size() - 1;
                                    existed = false;
                                }
                                break;
                            case 3:
                                currentFloor = tmpLocs.getBlock(indexes[0]).getFloor(indexes[1]);
                                if (currentFloor.getRooms().contains(new Room(tmpResult))) {
                                    indexes[2] = currentFloor.getRooms().indexOf(new Room(tmpResult));
                                } else {
                                    currentFloor.addRoom(tmpResult);
                                    indexes[2] = currentFloor.getRooms().size() - 1;
                                    existed = false;
                                }
                                break;
                            case 4:
                                currentRoom = tmpLocs.getBlock(indexes[0]).getFloor(indexes[1]).getRoom(indexes[2]);
                                if (currentRoom.getAreas().contains(new Area(tmpResult))) {
                                    indexes[3] = currentRoom.getAreas().indexOf(new Area(tmpResult));
                                } else {
                                    currentRoom.addArea(tmpResult);
                                    indexes[3] = currentRoom.getAreas().size() - 1;
                                    existed = false;
                                }
                                break;
                            default:
                                Log.d(tag, String.valueOf(i));
                        }
                    }

                    if (tmpLocs == filteredLocs) {
                        break;
                    }

                    if (existed) {
                        if (is_filtered) {
                            tmpLocs = filteredLocs;
                            is_filtered = false;
                            continue;
                        } else {
                            break;
                        }
                    }

                    Area currentArea = tmpLocs.getBlock(indexes[0]).getFloor(indexes[1]).getRoom(indexes[2]).getArea(indexes[3]);
                    String tmpAddress = currentBlock.getName()+", "+currentFloor.getName()+", "+currentRoom.getName()+", "+currentArea.getName();

                    if (marked_address_ids.contains(eaRecord.getString("_id"))) {
                        currentArea.setStatus(Area.status_marked);
                        marked_addresses.add(String.valueOf((k+1)+". "+tmpAddress));
                        marked_address++;
                    } else {
                        currentArea.setStatus(Area.status_unmarked);
                        unmarked_addresses.add(String.valueOf((k+1)+". "+tmpAddress));
                        unmarked_address++;
                    }
                    if (is_filtered) {
                        tmpLocs = filteredLocs;
                        is_filtered = false;
                        Log.d(tag, "is_filtered");
                    } else {
                        Log.d(tag, "is_not_filtered");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    //getAddressFromXLSX(Para1: storage, Para2: store the data without this status)
    public void getLandmarksFromXLSX() {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_CONSTANT);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_CONSTANT);
            FileInputStream excelFile = new FileInputStream(new File(ExcelPath.xlsx(ExcelPath.FilterLandmark+fileExtension)));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            if (iterator.hasNext()) {
                iterator.next();
            } else {
                return;
            }
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                String tmpResult = "";

                if (getStringFromXLSXCell(currentRow, 6).equals("marked")) {
                    marked_landmarks.add(getStringFromXLSXCell(currentRow, 0));
                    marked_landmark++;
                    continue;
                }

                remained_landmarks.add(getStringFromXLSXCell(currentRow, 0));
                remained_landmark++;
            }
        } catch (FileNotFoundException e) {
            Log.d(tag,e.toString());
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(tag,e.toString());
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    //getAddressFromXLSX(Para1: storage, Para2: store the data without this status)
    public void getLandmarksFromServer() {
        try {
            String login_name = preferences.getString("login_username", "");
            marked_address_ids = new ArrayList<String>();

            new Excel_Server(this, Excel_Server.ACTION_INIT).execute().get();
            JSONArray data = new JSONArray(new Excel_Server(this, Excel_Server.ACTION_GET_LANDMARKS, locs.getProject(project_id).getId()).execute().get());
            for (int k=0; k<data.length(); k++) {
                JSONObject eaRecord = data.getJSONObject(k);
                //if (eaRecord.getString("lastUpdateBy").equals(login_name)) {
                if ((eaRecord.getString("status")!=null)&&(eaRecord.getString("status").equals("Assigned"))) {
                    marked_landmarks.add(eaRecord.getString("uuid"));
                    marked_landmark++;
                    marked_address_ids.add(eaRecord.getString("availableAddressId"));
                } else {
                    remained_landmarks.add(eaRecord.getString("uuid"));
                    remained_landmark++;
                }

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void editAddressTable(ArrayList<String> addresses) {
        ll_addresses_table.removeAllViews();
        Boolean isEven = true;

        for (String addr: addresses) {
            TextView tmpTxtView = new TextView(getApplicationContext());
            LinearLayout.LayoutParams tmpParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tmpTxtView.setLayoutParams(tmpParams);
            tmpTxtView.setTextColor(Color.parseColor("#000000"));
            tmpTxtView.setPadding(10, 10, 10, 10);
            if (isEven) {
                tmpTxtView.setBackgroundColor( Color.parseColor("#e6e6e6"));
                isEven = false;
            } else {
                isEven = true;
            }

            tmpTxtView.setText(addr);
            ll_addresses_table.addView(tmpTxtView);
        }
    }

    public void editLandmarkTable(ArrayList<String> lms) {
        ll_landmarks_table.removeAllViews();
        Boolean isEven = true;

        for (String lm: lms) {
            TextView tmpTxtView = new TextView(getApplicationContext());
            LinearLayout.LayoutParams tmpParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tmpTxtView.setLayoutParams(tmpParams);
            tmpTxtView.setTextColor(Color.parseColor("#000000"));
            tmpTxtView.setPadding(10, 10, 10, 10);
            if (isEven) {
                tmpTxtView.setBackgroundColor( Color.parseColor("#e6e6e6"));
                isEven = false;
            } else {
                isEven = true;
            }

            tmpTxtView.setText(lm);
            ll_landmarks_table.addView(tmpTxtView);
        }
    }

    public Locations getCurrentLocs() {
        Locations tmpLocs;
        if (showing_status == mStatusList.ADDRESS_STATUS_INCLUDE_ALL) {
            tmpLocs = locs;
        } else {
            tmpLocs = filteredLocs;
        }
        return tmpLocs;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.address_select_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finishActivity();
        }
        if (id == R.id.action_addr_xlsx) {
            Intent myIntent = new Intent(AddressSelectActivity.this, AddressXLSXActivity.class);
            myIntent.putExtra("fileExtension", fileExtension);
            AddressSelectActivity.this.startActivity(myIntent);
        }
        if (id == R.id.action_lm_xlsx) {
            Intent myIntent = new Intent(AddressSelectActivity.this, LandmarkXLSXActivity.class);
            myIntent.putExtra("fileExtension", fileExtension);
            AddressSelectActivity.this.startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        finishActivity();
    }

    public void finishActivity() {
        Intent myIntent = new Intent(AddressSelectActivity.this, ProjectSelectActivity.class);
        AddressSelectActivity.this.startActivity(myIntent);
        this.finish();
    }

    public String getStringFromXLSXCell(Row row, int pos) {
        String tmpResult = "";
        if (row.getCell(pos, Row.CREATE_NULL_AS_BLANK).getCellType() != Cell.CELL_TYPE_BLANK) {
            //get the data from cell
            if (row.getCell(pos).getCellType() == XSSFCell.CELL_TYPE_STRING) {
                tmpResult = row.getCell(pos).getStringCellValue();
            } else if (row.getCell(pos).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                tmpResult = String.valueOf(row.getCell(pos).getNumericCellValue());
            }
        }
        return tmpResult;
    }

    public String getAddressesIDFromServer(String[] addresses) {
        String condition = "";
        String target = "";

        try {
            new Excel_Server(this, Excel_Server.ACTION_INIT).execute().get();
            JSONArray data = new JSONArray(new Excel_Server(this, Excel_Server.ACTION_GET_ADDRESSES, locs.getProject(project_id).getId()).execute().get());

            outerLoop:
            for (int k = 0; k < data.length(); k++) {
                JSONObject eaRecord = data.getJSONObject(k);

                int level = eaRecord.getInt("referenceLevel");

                //handle address
                for (int i = 1; i <= 4; i++) {
                    switch (i) {
                        case 1:
                            condition = eaRecord.getJSONArray("address").getJSONObject(1).getString("block");
                            if (i>level) {
                                condition = "";
                            }
                            target = addresses[0];
                            break;
                        case 2:
                            condition = eaRecord.getJSONArray("address").getJSONObject(1).getString("floor");
                            if (i>level) {
                                condition = "";
                            }
                            target = addresses[1];
                            break;
                        case 3:
                            condition = eaRecord.getJSONArray("address").getJSONObject(1).getString("room");
                            if (i>level) {
                                condition = "";
                            }
                            target = addresses[2];
                            break;
                        case 4:
                            condition = eaRecord.getJSONArray("address").getJSONObject(1).getString("areaWithin");
                            if (i>level) {
                                condition = "";
                            }
                            target = addresses[3];
                            break;
                    }
                    if (!condition.equals(target)) {
                        continue outerLoop;
                    }
                }
                return eaRecord.getString("_id");
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

}
