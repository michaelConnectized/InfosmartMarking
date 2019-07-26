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

public class LoginActivity  extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;
    private EditText et_server;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_server = findViewById(R.id.et_server);

        if (preferences.contains("login_username"))
            et_username.setText(preferences.getString("login_username", ""));
        if (preferences.contains("login_password"))
            et_password.setText(preferences.getString("login_password", ""));

        et_server.setText(preferences.getString("serverUrl", "http://42.200.149.215:9860/excel"));
    }

    public void Save(View view) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("login_username", et_username.getText().toString());
        editor.putString("login_password", et_password.getText().toString());
        editor.putString("serverUrl", et_server.getText().toString());
        editor.apply();
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
