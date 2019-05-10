package com.example.user.markingactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity  extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        if (preferences.contains("login_username"))
            et_username.setText(preferences.getString("login_username", ""));
        if (preferences.contains("login_password"))
            et_password.setText(preferences.getString("login_password", ""));
    }

    public void Save(View view) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("login_username", et_username.getText().toString());
        editor.putString("login_password", et_password.getText().toString());
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
}
