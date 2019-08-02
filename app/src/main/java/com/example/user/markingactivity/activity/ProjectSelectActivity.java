package com.example.user.markingactivity.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.example.user.markingactivity.model.ExcelPath;
import com.example.user.markingactivity.object.Locations;
import com.example.user.markingactivity.R;
import com.example.user.markingactivity.shared.SharedPreferencesManager;

import java.util.ArrayList;

public class ProjectSelectActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_CONSTANT = 2;

    private Switch switch_server_xml;
    private SharedPreferencesManager sp;
    private boolean server_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_select);

        sp = new SharedPreferencesManager(this);

        server_mode = sp.isServerMode();
		ListView project_list = findViewById(R.id.project_list);

        switch_server_xml = findViewById(R.id.switch_server_xml);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_CONSTANT);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_CONSTANT);
		Locations locs = new Locations(this);
		if (server_mode) {
            switch_server_xml.setText("SERVER");
            switch_server_xml.setChecked(true);
            locs.setProjectsFromServer();
        } else {
            switch_server_xml.setText("EXCEL");
            switch_server_xml.setChecked(false);
            locs.setProjectsFromFiles(ExcelPath.homepage, "addr");
        }

        ArrayList<String> values = locs.getProjectsString();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        project_list.setAdapter(adapter);


        switch_server_xml.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp.setServerMode(true);
                } else {
                    sp.setServerMode(false);
                }
                refreshPage();
            }
        });

		project_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Intent myIntent = new Intent(ProjectSelectActivity.this, AddressSelectActivity.class);
				myIntent.putExtra("project_id", position); //Optional parameters
                ProjectSelectActivity.this.startActivity(myIntent);
                finish();
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project_select_main, menu);
        return true;
    }

    //Setting page
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refreshPage();
        }
        if (id == R.id.action_login) {
            login();
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshPage() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
    }

    public void login() {
        Intent myIntent = new Intent(ProjectSelectActivity.this, LoginActivity.class);
        ProjectSelectActivity.this.startActivity(myIntent);
        this.finish();
    }

}
