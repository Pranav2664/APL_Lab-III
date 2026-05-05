package com.example.databasehandling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;

    Button insertBtn, displayBtn;
    TextView resultText;

    EditText rollInput, nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertBtn = findViewById(R.id.button1);
        displayBtn = findViewById(R.id.button2);

        resultText = findViewById(R.id.textView3);

        rollInput = findViewById(R.id.editText1);
        nameInput = findViewById(R.id.editText2);

        try {

            db = openOrCreateDatabase("StudentDB", MODE_PRIVATE, null);

            db.execSQL("CREATE TABLE IF NOT EXISTS Temp(id INTEGER, name TEXT)");

        } catch (SQLException e) {

            Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show();
        }


        // Insert Record
        insertBtn.setOnClickListener(v -> {

            String roll = rollInput.getText().toString();
            String name = nameInput.getText().toString();

            if (roll.isEmpty() || name.isEmpty()) {

                Toast.makeText(MainActivity.this,
                        "Please enter all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();

            values.put("id", roll);
            values.put("name", name);

            long result = db.insert("Temp", null, values);

            if (result != -1) {

                Toast.makeText(MainActivity.this,
                        "Record Inserted Successfully",
                        Toast.LENGTH_SHORT).show();

                rollInput.setText("");
                nameInput.setText("");

            } else {

                Toast.makeText(MainActivity.this,
                        "Insert Failed",
                        Toast.LENGTH_SHORT).show();
            }

        });


        // Display Data
        displayBtn.setOnClickListener(v -> {

            Cursor c = db.rawQuery("SELECT * FROM Temp", null);

            StringBuilder data = new StringBuilder();

            if (c.moveToFirst()) {

                do {

                    data.append("Roll: ")
                            .append(c.getString(0))
                            .append("  Name: ")
                            .append(c.getString(1))
                            .append("\n\n");

                } while (c.moveToNext());

            } else {

                data.append("No Records Found");

            }

            resultText.setText(data.toString());

            c.close();

        });
    }
}