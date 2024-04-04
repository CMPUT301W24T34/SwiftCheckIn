package com.example.swiftcheckin.organizer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftcheckin.R;

public class EventInfoPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_information);   // This the xml the activity is connected to.

        TextView checkedInButton = findViewById(R.id.organizerEventInfo_CheckedInTitle);
        TextView signedUpButton = findViewById(R.id.organizerEventInfo_SignedUpTitle);



    }


}
