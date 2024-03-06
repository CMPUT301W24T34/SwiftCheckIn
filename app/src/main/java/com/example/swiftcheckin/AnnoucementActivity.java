package com.example.swiftcheckin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AnnoucementActivity extends AppCompatActivity {

//    private TextView eventName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_announcement);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra("Message");
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();






        // Use the string data received in your UI or logic
        TextView textView = findViewById(R.id.announcement_event_name);
        textView.setText(message);


        }
    }
