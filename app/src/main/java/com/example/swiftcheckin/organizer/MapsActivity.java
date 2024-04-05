package com.example.swiftcheckin.organizer;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.swiftcheckin.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Citation: The following code for creating google map and adding markers, 2024, Youtube, "Step by Step Google Maps Implementation in Android App | Google Maps in Android: Step-by-Step Guide", Codingzest, https://www.youtube.com/watch?v=pOKPQ8rYe6g

/**
 * Displays the map activity
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private String eventId;


    private Firebase_organizer firebase_organizer ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebase_organizer = new Firebase_organizer(this);


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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        firebase_organizer.getCheckedIn(eventId,myMap);

    }


}