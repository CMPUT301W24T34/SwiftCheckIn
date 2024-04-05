package com.example.swiftcheckin.organizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftcheckin.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

//Citation: The following code for creating google map and adding markers, 2024, Youtube, "Step by Step Google Maps Implementation in Android App | Google Maps in Android: Step-by-Step Guide", Codingzest, https://www.youtube.com/watch?v=pOKPQ8rYe6g


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private String eventId;


    private FirebaseOrganizer firebase_organizer ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebase_organizer = new FirebaseOrganizer(this);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();

//Citation: For the following code line, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to get the eventID from one class to the other when switching intents
        if (intent != null) {
            eventId = intent.getStringExtra("eventId");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        firebase_organizer.getCheckedIn(eventId,myMap);

    }

}