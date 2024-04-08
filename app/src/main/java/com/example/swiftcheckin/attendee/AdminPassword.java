package com.example.swiftcheckin.attendee;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftcheckin.R;
import com.example.swiftcheckin.admin.AdminActivity;
/**
 * This allows only those with a password to view admin
 */
public class AdminPassword extends AppCompatActivity {

    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        TextView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        passwordEditText = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Citation: For the following code for edit text, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to format password and ignore casing
                String password = passwordEditText.getText().toString().trim();

                if (password.equalsIgnoreCase("SwiftCheckIn")) {
                    Toast.makeText(AdminPassword.this, "Switched to admin", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminPassword.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AdminPassword.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}