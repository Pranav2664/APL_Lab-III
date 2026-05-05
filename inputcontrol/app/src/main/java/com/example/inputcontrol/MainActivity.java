package com.example.inputcontrol; // Updated to match your actual project path!

import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This links your Java file to the XML layout
        setContentView(R.layout.activity_main);

        // Find the views by the IDs we gave them in the XML
        EditText inputControl = findViewById(R.id.myInputControl);
        SeekBar seekBar = findViewById(R.id.mySeekBar);

        // Listen for changes on the Seek Bar (useful for camera zoom logic)
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // This triggers every time the slider moves.
                // 'progress' will be a number between 0 and 100.
            }

            @Override

            public void onStartTrackingTouch(SeekBar seekBar) {
                // User started dragging
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // User stopped dragging. We show a little pop-up message (Toast) to test it.
                Toast.makeText(MainActivity.this, "Zoom level set to " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}