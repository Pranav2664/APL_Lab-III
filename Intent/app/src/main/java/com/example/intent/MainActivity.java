package com.example.intent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    Button btnCall, btnSMS, btnWeb, btnList;
    private static final int CALL_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCall = findViewById(R.id.btnCall);
        btnSMS = findViewById(R.id.btnSMS);
        btnWeb = findViewById(R.id.btnWeb);
        btnList = findViewById(R.id.btnList);

        btnCall.setOnClickListener(v -> makeCall());
        btnSMS.setOnClickListener(v -> sendSMS());
        btnWeb.setOnClickListener(v -> openWebsite());
        btnList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        });
    }

    private void makeCall() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    CALL_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:9876543210"));
            startActivity(intent);
        }
    }

    private void sendSMS() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:9876543210"));
        intent.putExtra("sms_body", "Hello from Enhanced App!");
        startActivity(intent);
        Toast.makeText(this, "Opening SMS...", Toast.LENGTH_SHORT).show();
    }

    private void openWebsite() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://developer.android.com"));
        startActivity(intent);
        Toast.makeText(this, "Opening Website...", Toast.LENGTH_SHORT).show();
    }
}
