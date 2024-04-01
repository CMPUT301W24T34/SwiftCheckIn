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


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private String eventId;


    private FirebaseFirestore db;
    private List<String> matchedProfiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();

//Citation: For the following code line, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to get the eventID from one class to the other when switching intents
        if (intent != null) {
            eventId = intent.getStringExtra("eventId");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        eventId = "615ec84d6781c109Yeet";
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        db = FirebaseFirestore.getInstance();
        matchedProfiles.clear();

        //Citation: For the following code query ideas, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to get the field names and add them to a list
        db.collection("checkedIn")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> checkedInData = documentSnapshot.getData();
                        if (checkedInData != null) {
                            matchedProfiles.addAll(checkedInData.keySet());
                        //Citation: For the following code query ideas, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to make a nested query to query twice based on the results of the first query
                        for (String id : matchedProfiles) {

                            db.collection("profiles").document(id)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Map<String, Object> data = documentSnapshot.getData();
                                                String locationPermission = (String) data.get("locationPermission");
                                                if (locationPermission != "False") {
                                                    String latitudeStr = (String) data.get("latitude");
                                                    String longitudeStr = (String) data.get("longitude");

                                                    Double latitude = Double.parseDouble(latitudeStr);
                                                    Double longitude = Double.parseDouble(longitudeStr);
                                                    if (latitude != null && longitude != null) {
                                                        LatLng location = new LatLng(latitude, longitude);
                                                        myMap.addMarker(new MarkerOptions().position(location).title((String) data.get("name")));
                                                        float zoomLevel = 16.0f;
                                                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( location, zoomLevel);
                                                        myMap.moveCamera(cameraUpdate);
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                    }

    })


                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error querying documents: ", e);
                    }
                });
    }

}