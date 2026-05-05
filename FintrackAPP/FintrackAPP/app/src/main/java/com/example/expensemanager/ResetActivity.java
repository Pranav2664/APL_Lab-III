package com.example.expensemanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResetActivity extends AppCompatActivity {

    private EditText mEmail, mNewPass, mConfirmPass;
    private Button btnUpdate;
    private TextView mBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        dbHelper = new DatabaseHelper(this);

        mEmail = findViewById(R.id.email_reset);
        mNewPass = findViewById(R.id.new_password_reset);
        mConfirmPass = findViewById(R.id.confirm_password_reset);
        btnUpdate = findViewById(R.id.btn_reset_pass);
        mBack = findViewById(R.id.back_to_login);

        btnUpdate.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();
            String pass = mNewPass.getText().toString().trim();
            String confirmPass = mConfirmPass.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email Required");
                return;
            }
            if (TextUtils.isEmpty(pass)) {
                mNewPass.setError("Password Required");
                return;
            }
            if (!pass.equals(confirmPass)) {
                mConfirmPass.setError("Passwords do not match");
                return;
            }

            if (dbHelper.checkEmailExists(email)) {
                boolean isUpdated = dbHelper.updatePassword(email, pass);
                if (isUpdated) {
                    Toast.makeText(ResetActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to Login
                } else {
                    Toast.makeText(ResetActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ResetActivity.this, "Email not registered", Toast.LENGTH_SHORT).show();
            }
        });

        mBack.setOnClickListener(view -> finish());
    }
}
