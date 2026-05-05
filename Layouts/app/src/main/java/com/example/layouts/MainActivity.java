package com.example.layouts;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnLinear).setOnClickListener(v ->
                startActivity(new Intent(this, LinearActivity.class)));

        findViewById(R.id.btnFrame).setOnClickListener(v ->
                startActivity(new Intent(this, FrameActivity.class)));

        findViewById(R.id.btnTable).setOnClickListener(v ->
                startActivity(new Intent(this, TableActivity.class)));

        findViewById(R.id.btnGrid).setOnClickListener(v ->
                startActivity(new Intent(this, GridActivity.class)));
    }
}
