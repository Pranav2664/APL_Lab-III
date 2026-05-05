package com.example.layout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLinear = findViewById(R.id.btnLinear);
        Button btnTable  = findViewById(R.id.btnTable);
        Button btnGrid   = findViewById(R.id.btnGrid);
        Button btnFrame  = findViewById(R.id.btnFrame);

        btnLinear.setOnClickListener(v ->
                setContentView(R.layout.activity_linear));

        btnTable.setOnClickListener(v ->
                setContentView(R.layout.activity_table));

        btnGrid.setOnClickListener(v ->
                setContentView(R.layout.activity_grid));

        btnFrame.setOnClickListener(v ->
                setContentView(R.layout.activity_frame));
    }
}
