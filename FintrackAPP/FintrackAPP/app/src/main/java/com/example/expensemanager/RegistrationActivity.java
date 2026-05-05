package com.example.expensemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnReg;
    private TextView mSignin;

    private ProgressDialog mDialog;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new DatabaseHelper(this);
        mDialog = new ProgressDialog(this);

        registration();
    }

    private void registration() {
        mEmail = findViewById(R.id.email_registration);
        mPass = findViewById(R.id.password_registration);
        btnReg = findViewById(R.id.btn_registration);
        mSignin = findViewById(R.id.signin_here);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email Required...");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password Required...");
                    return;
                }

                mDialog.setMessage("Processing...");
                mDialog.show();

                if (dbHelper.registerUser(email, pass)) {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Registration Complete...", Toast.LENGTH_SHORT).show();

                    // Save login state
                    SharedPreferences sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.apply();

                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Registration Failed...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}