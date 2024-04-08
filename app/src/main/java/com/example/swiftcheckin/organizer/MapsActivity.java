package com.example.swiftcheckin.organizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.swiftcheckin.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;




/**
 * Displays the map activity
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private String eventId;


    private FirebaseOrganizer firebase_organizer ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Citation: The following code for creating and populating the map, 2024, Youtube, "Step by Step Google Maps Implementation in Android App | Google Maps in Android: Step-by-Step Guide", Codingzest, https://www.youtube.com/watch?v=pOKPQ8rYe6g
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
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
    }


    /**
     * Called when the Google Map is ready to be used.
     *
     * @param googleMap The Google Map object representing the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        firebase_organizer.getCheckedIn(eventId,myMap);

    }


}