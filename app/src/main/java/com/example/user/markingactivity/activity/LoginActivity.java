package com.example.user.markingactivity.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.markingactivity.R;
import com.example.user.markingactivity.shared.SharedPreferencesManager;

public class LoginActivity  extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;
    private EditText et_server;
    private SharedPreferencesManager sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_server = findViewById(R.id.et_server);

        sp = new SharedPreferencesManager(this);

        if (sp.hasLoginUsername())
            et_username.setText(sp.getLoginUsername());
        if (sp.hasLoginPassword())
            et_password.setText(sp.getLoginPassword());

        et_server.setText(sp.getServerUrl());
    }

    public void Save(View view) {
        sp.setLoginUsername(et_username.getText().toString());
        sp.setLoginPassword(et_password.getText().toString());
        sp.setServerUrl(et_server.getText().toString());
        Toast.makeText(this, "Save successful!", Toast.LENGTH_LONG).show();
        finishActivity();
    }

    @Override
    public void onBackPressed(){
        finishActivity();
    }

    public void finishActivity() {
        Intent myIntent = new Intent(LoginActivity.this, ProjectSelectActivity.class);
        LoginActivity.this.startActivity(myIntent);
        this.finish();
    }

    public void onclickTestingServer(View view) {
        et_server.setText("http://42.200.149.215:9860/excel");
    }

    public void onclickProductionServer(View view) {
        et_server.setText("https://excel.infotronic-tech.com/excel");
    }
}
