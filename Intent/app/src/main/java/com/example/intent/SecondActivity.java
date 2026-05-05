package com.example.intent;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    ListView listView;

    String[] layouts = {
            "LinearLayout",
            "RelativeLayout",
            "ConstraintLayout",
            "FrameLayout",
            "TableLayout",
            "GridLayout"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        listView = findViewById(R.id.listView);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        layouts);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) ->
                Toast.makeText(this,
                        layouts[position] + " Clicked",
                        Toast.LENGTH_SHORT).show());
    }
}
