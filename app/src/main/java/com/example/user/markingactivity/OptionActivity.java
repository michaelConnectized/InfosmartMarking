package com.example.user.markingactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;

public class OptionActivity extends AppCompatActivity {

    private EditText et_scan_range_start;
    private EditText et_scan_time;
    private SharedPreferences preferences;

    private int project_id;
    private String[] addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        Intent intent = getIntent();
        project_id = intent.getIntExtra("project_id", 0);
        addresses = intent.getStringArrayExtra("addresses");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        et_scan_range_start = findViewById(R.id.et_scan_range_start);
        et_scan_time = findViewById(R.id.et_scan_time);

        if (preferences.contains("scan_range"))
            et_scan_range_start.setText(String.valueOf(preferences.getInt("scan_range", 100)*-1));
        if (preferences.contains("scan_time"))
            et_scan_time.setText(String.valueOf(preferences.getInt("scan_time", 15)));
    }

    public void Save(View view) {
        int scan_range = Integer.parseInt(et_scan_range_start.getText().toString());
        int scan_time = Integer.parseInt(et_scan_time.getText().toString());

        if (scan_range > 100) {
            scan_range = 100;
        }
        if (scan_time <= 0) {
            scan_time = 1;
        }


        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("scan_range", scan_range*-1);
        editor.putInt("scan_time", scan_time);
        editor.apply();
        Toast.makeText(this, "Save successful!", Toast.LENGTH_LONG).show();
        finishActivity();
    }

    @Override
    public void onBackPressed(){
        finishActivity();
    }

    public void onClickBtnScanFilterPlus1(View view) {
        et_scan_range_start.setText(String.valueOf(Integer.parseInt(et_scan_range_start.getText().toString())+1));
    }

    public void onClickBtnScanFilterMinor1(View view) {
        if (!et_scan_range_start.getText().toString().equals("0")) {
            et_scan_range_start.setText(String.valueOf(Integer.parseInt(et_scan_range_start.getText().toString())-1));
        }
    }

    public void onClickBtnScanTimePlus1(View view) {
        et_scan_time.setText(String.valueOf(Integer.parseInt(et_scan_time.getText().toString())+1));
    }

    public void onClickBtnScanTimeMinor1(View view) {
        if (!et_scan_time.getText().toString().equals("0")) {
            et_scan_time.setText(String.valueOf(Integer.parseInt(et_scan_time.getText().toString())-1));
        }
    }

    public void finishActivity() {
        Intent myIntent = new Intent(OptionActivity.this, ScanActivity.class);
        myIntent.putExtra("project_id", project_id);
        myIntent.putExtra("addresses", addresses);
        OptionActivity.this.startActivity(myIntent);
        this.finish();
    }
}
