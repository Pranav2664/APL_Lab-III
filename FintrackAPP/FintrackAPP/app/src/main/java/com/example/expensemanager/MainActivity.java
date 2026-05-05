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

public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnLogin;
    private TextView mForgetPassword;
    private TextView mSignupHere;

    private ProgressDialog mDialog;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", null);
        if (userEmail != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

        mDialog = new ProgressDialog(this);
        loginDetails();
    }

    private void loginDetails() {
        mEmail = findViewById(R.id.email_login);
        mPass = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.btn_login);
        mForgetPassword = findViewById(R.id.forgot_password);
        mSignupHere = findViewById(R.id.signup_req);

        btnLogin.setOnClickListener(new View.OnClickListener() {
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

                if (dbHelper.checkUser(email, pass)) {
                    mDialog.dismiss();

                    // Save login state
                    SharedPreferences sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.apply();

                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                    Toast.makeText(getApplicationContext(), "Login Successful...", Toast.LENGTH_SHORT).show();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Failed...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSignupHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });
    }
}