package com.example.swiftcheckin;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private EventViewAdapter eventViewAdapter;
    private ArrayList<Event> eventList;
    private static final String TAG = "MainActivity";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_myevent);

        listViewEvents = findViewById(R.id.attendee_my_events_list);
        eventList = new ArrayList<>();
        eventViewAdapter = new EventViewAdapter(this, eventList);
        listViewEvents.setAdapter(eventViewAdapter);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        setupUI();         // Setup UI elements
        getData();          // Fetch data from Firestore
    }


    private void getData(){
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        CollectionReference eventCol = db.collection("events");

        eventCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                eventList.clear(); // Clear the old list
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
//                    String eventId = doc.getId();
                    String eventTitle = (String) doc.getData().get("eventTitle");
                    String eventDescription = (String) doc.getData().get("eventDescription");
                    String eventLocation = (String) doc.getData().get("eventLocation");
                    String deviceId = (String) doc.getData().get("deviceId");
                    String eventImageUrl = (String) doc.getData().get("eventImageUrl");

                    String eventStartDate = (String) doc.getData().get("eventStartDate");
                    String eventStartTime = (String) doc.getData().get("eventStartTime");
                    String eventEndDate = (String) doc.getData().get("eventEndDate");
                    String eventEndTime = (String) doc.getData().get("eventEndTime");

                    eventList.add(new Event(eventTitle, eventDescription, eventLocation, deviceId
                            , eventImageUrl,eventStartDate,eventEndDate, eventStartTime, eventEndTime ));
                }
                eventViewAdapter.notifyDataSetChanged(); // Notify the adapter to render new data
            }
        });
    }



    private void setupUI() {
        // Profile Picture Button
        ImageView profileButton = findViewById(R.id.profile_picture);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Switch Mode Button
        FloatingActionButton fab = findViewById(R.id.switch_modes);
        fab.setOnClickListener(v -> new SwitchModeFragment().show(getSupportFragmentManager(), "Switch Modes"));


        // Camera Button
        ImageView cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QRCodeScannerActivity.class);
            startActivity(intent);
        });



        listViewEvents.setOnItemClickListener((parent,view, position, id) -> {
            Intent annoucementIntent = new Intent(MainActivity.this, AnnoucementActivity.class);

            annoucementIntent.putExtra("Message", "hello world");
            startActivity(annoucementIntent);

        });
    }




}