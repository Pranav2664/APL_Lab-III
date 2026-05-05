package com.example.filehandling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    Button saveBtn, loadBtn;
    EditText inputText;
    TextView outputText;

    String fileName = "mydata.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveBtn = findViewById(R.id.button);
        loadBtn = findViewById(R.id.button2);
        inputText = findViewById(R.id.editText);
        outputText = findViewById(R.id.textview2);

        // SAVE DATA
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data = inputText.getText().toString();

                if(data.isEmpty()){
                    Toast.makeText(MainActivity.this,
                            "Please enter some text",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                try {

                    FileOutputStream fos = openFileOutput(fileName, MODE_APPEND);
                    fos.write((data + "\n").getBytes());
                    fos.close();

                    Toast.makeText(MainActivity.this,
                            "Data Saved Successfully",
                            Toast.LENGTH_SHORT).show();

                    inputText.setText("");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        // LOAD DATA
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    FileInputStream fis = openFileInput(fileName);

                    int c;
                    String temp = "";

                    while ((c = fis.read()) != -1) {
                        temp += Character.toString((char) c);
                    }

                    fis.close();

                    outputText.setText(temp);

                    Toast.makeText(MainActivity.this,
                            "Data Loaded",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                    outputText.setText("No Data Found");
                }

            }
        });

    }
}