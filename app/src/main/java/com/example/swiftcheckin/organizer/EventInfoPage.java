package com.example.swiftcheckin.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftcheckin.R;

public class EventInfoPage extends AppCompatActivity {


    ListView checkedInList;
    ListView signedUpList;

    TextView checkedInButton;

    TextView signedUpButton;

    String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_information);   // This the xml the activity is connected to.
        View view = getWindow().getDecorView();     // OpenAI, April 4, 2024. ChatGPT. Prompt: how to get the current acitivity view in the activity

        eventId = getIntent().getStringExtra("eventId");

        checkedInButton= findViewById(R.id.organizerEventInfo_CheckedInTitle);
        signedUpButton = findViewById(R.id.organizerEventInfo_SignedUpTitle);

        checkedInList = findViewById(R.id.organizerEventInfo_CheckedInList);
        signedUpList = findViewById(R.id.organizerEventInfo_SignedUpList);


        initializeListButton(checkedInButton);
        initializeListButton(signedUpButton);


    }

    protected void initializeListButton(TextView view1)
    {
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList();
            }
        });

    }

    private void showList()
    {
        if(checkedInList.getVisibility() == View.INVISIBLE)
        {
            signedUpList.setVisibility(View.INVISIBLE);
            checkedInList.setVisibility(View.VISIBLE);
            signedUpButton.setBackground(null);
            checkedInButton.setBackgroundResource(R.drawable.grey_circle_background);
        }
        else if(signedUpList.getVisibility() == View.INVISIBLE)
        {
            checkedInList.setVisibility(View.INVISIBLE);
            signedUpList.setVisibility(View.VISIBLE);
            checkedInButton.setBackground(null);
            signedUpButton.setBackgroundResource(R.drawable.grey_circle_background);
        }
    }

    private void getEventInformation()
    {

    }
}

