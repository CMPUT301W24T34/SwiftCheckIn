package com.example.swiftcheckin;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
<<<<<<< HEAD
import android.view.View;
=======
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
=======
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c

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



/**
 * The main activity of the application responsible for displaying a list of events.
 */
public class MainActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private EventViewAdapter eventViewAdapter;
    private ArrayList<Event> eventList;
    private static final String TAG = "MainActivity";
    private FirebaseFirestore db;
<<<<<<< HEAD
=======

    String eventTitle;
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        setContentView(R.layout.attendee_myevent);

        listViewEvents = findViewById(R.id.attendee_my_events_list);
=======
        setContentView(R.layout.attendee_otherevent);

        listViewEvents = findViewById(R.id.attendee_other_events_list);
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
        eventList = new ArrayList<>();
        eventViewAdapter = new EventViewAdapter(this, eventList);
        listViewEvents.setAdapter(eventViewAdapter);

<<<<<<< HEAD
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        setupUI();         // Setup UI elements
        getData();          // Fetch data from Firestore
    }

=======
        db = FirebaseFirestore.getInstance();

        setupUI();
        getData();
    }


    /**
     * Fetches event data from Firestore.
     */

>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
    private void getData(){
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        CollectionReference eventCol = db.collection("events");

        eventCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

<<<<<<< HEAD
                eventList.clear(); // Clear the old list
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
//                    String eventId = doc.getId();
                    String eventTitle = (String) doc.getData().get("eventTitle");
=======
                eventList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    eventTitle = (String) doc.getData().get("eventTitle");
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
                    String eventDescription = (String) doc.getData().get("eventDescription");
                    String eventLocation = (String) doc.getData().get("eventLocation");
                    String deviceId = (String) doc.getData().get("deviceId");
                    String eventImageUrl = (String) doc.getData().get("eventImageUrl");
<<<<<<< HEAD

=======
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
                    String eventStartDate = (String) doc.getData().get("eventStartDate");
                    String eventStartTime = (String) doc.getData().get("eventStartTime");
                    String eventEndDate = (String) doc.getData().get("eventEndDate");
                    String eventEndTime = (String) doc.getData().get("eventEndTime");

<<<<<<< HEAD

//    public Event(String eventTitle, String description, String location, String deviceId, String eventImageUrl, String startDate,
//                            String endDate, String startTime, String endTime){

                    eventList.add(new Event(eventTitle, eventDescription, eventLocation, deviceId
                            , eventImageUrl,eventStartDate,eventEndDate, eventStartTime, eventEndTime ));
                }
                eventViewAdapter.notifyDataSetChanged(); // Notify the adapter to render new data
=======
                    eventList.add(new Event(eventTitle, eventDescription, eventLocation, deviceId
                            , eventImageUrl,eventStartDate,eventEndDate, eventStartTime, eventEndTime ));
                }
                eventViewAdapter.notifyDataSetChanged();
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
            }
        });
    }

<<<<<<< HEAD
=======

    /**
     * Sets up UI elements and listeners.
     */
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
    private void setupUI() {
        // Profile Picture Button
        ImageView profileButton = findViewById(R.id.profile_picture);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

<<<<<<< HEAD
        // Switch Mode Button
=======
        // Other Page
        TextView myEventPage = findViewById(R.id.my_events);
        myEventPage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyEventActivity.class);
            startActivity(intent);
        });

//         Switch Mode Button
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
        FloatingActionButton fab = findViewById(R.id.switch_modes);
        fab.setOnClickListener(v -> new SwitchModeFragment().show(getSupportFragmentManager(), "Switch Modes"));


        // Camera Button
        ImageView cameraButton = findViewById(R.id.camera_button);
<<<<<<< HEAD
        cameraButton.setOnClickListener(v -> Toast.makeText(MainActivity.this, "This does nothing yet", Toast.LENGTH_SHORT).show());

        listViewEvents.setOnItemClickListener((parent,view, position, id) -> {
            Intent annoucementIntent = new Intent(MainActivity.this, AnnoucementActivity.class);

            annoucementIntent.putExtra("Message", "hello world");
=======
        cameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QRCodeScannerActivity.class);
            startActivity(intent);
        });


        // Event Details
        listViewEvents.setOnItemClickListener((parent,view, position, id) -> {
            Intent annoucementIntent = new Intent(MainActivity.this, AnnoucementActivity.class);
            String eventID = eventList.get(position).getDeviceId() +  eventList.get(position).getEventTitle();

            String eventTitle = eventList.get(position).getEventTitle();
            String eventLocation = eventList.get(position).getLocation();
            String eventDescription = eventList.get(position).getDescription();
            String eventImageUrl = eventList.get(position).getEventImageUrl();
            String eventStartDate = eventList.get(position).getStartDate();
            String eventStartTime = eventList.get(position).getStartTime();
            String eventEndDate = eventList.get(position).getEndDate();
            String eventEndTime = eventList.get(position).getEndTime();


            annoucementIntent.putExtra("eventID", eventID);
            annoucementIntent.putExtra("eventTitle", eventTitle);
            annoucementIntent.putExtra("eventLocation", eventLocation);
            annoucementIntent.putExtra("eventDescription", eventDescription);
            annoucementIntent.putExtra("eventImageUrl", eventImageUrl);
            annoucementIntent.putExtra("eventStartDate", eventStartDate);
            annoucementIntent.putExtra("eventStartTime", eventStartTime);
            annoucementIntent.putExtra("eventEndDate", eventEndDate);
            annoucementIntent.putExtra("eventEndTime", eventEndTime);


>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
            startActivity(annoucementIntent);

        });
    }

<<<<<<< HEAD
=======



>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
}