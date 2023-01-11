package com.dywa.e_doc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "SDKSDK";

    Button get_started;
    public static Activity mf;

    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mf = this;
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token_key", null);
        Log.d(TAG, "Stored Token : " + token);

        get_started = findViewById(R.id.get_started);

        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(loginActivity);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(token != null) {
            Intent i = new Intent(MainActivity.this, PatientListActivity.class);
            startActivity(i);
            finish();
        }
    }
}