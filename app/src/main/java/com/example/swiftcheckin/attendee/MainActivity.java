package com.example.swiftcheckin.attendee;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.swiftcheckin.organizer.Event;

import com.example.swiftcheckin.R;
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
    private FirebaseAttendee db_attendee;
    String eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_otherevent);

        listViewEvents = findViewById(R.id.attendee_other_events_list);
        eventList = new ArrayList<>();
        eventViewAdapter = new EventViewAdapter(this, eventList);
        listViewEvents.setAdapter(eventViewAdapter);
        db_attendee = new FirebaseAttendee();
        db = FirebaseFirestore.getInstance();

        setupUI();
        getData();
    }


    /**
     * Fetches event data from Firestore.
     */

    private void getData(){
        db_attendee.getEventList(eventList, new FirebaseAttendee.EventListCallback() {
            @Override
            public void onDataFetched(ArrayList<Event> eventList) {
                eventViewAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * Sets up UI elements and listeners.
     */
    private void setupUI() {
        // Profile Picture Button
        ImageView profileButton = findViewById(R.id.profile_picture);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Other Page
        TextView myEventPage = findViewById(R.id.my_events);
        myEventPage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyEventActivity.class);
            startActivity(intent);
        });

//         Switch Mode Button
        FloatingActionButton fab = findViewById(R.id.switch_modes);
        fab.setOnClickListener(v -> new SwitchModeFragment().show(getSupportFragmentManager(), "Switch Modes"));


        // Camera Button
        ImageView cameraButton = findViewById(R.id.camera_button);
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
            String eventMaxAttendees = Integer.toString(eventList.get(position).getMaxAttendees());
            String eventCurrentAttendees = Integer.toString(eventList.get(position).getCurrentAttendees());


            annoucementIntent.putExtra("eventID", eventID);
            annoucementIntent.putExtra("eventTitle", eventTitle);
            annoucementIntent.putExtra("eventLocation", eventLocation);
            annoucementIntent.putExtra("eventDescription", eventDescription);
            annoucementIntent.putExtra("eventImageUrl", eventImageUrl);
            annoucementIntent.putExtra("eventStartDate", eventStartDate);
            annoucementIntent.putExtra("eventStartTime", eventStartTime);
            annoucementIntent.putExtra("eventEndDate", eventEndDate);
            annoucementIntent.putExtra("eventEndTime", eventEndTime);
            annoucementIntent.putExtra("eventMaxAttendees", eventMaxAttendees);
            annoucementIntent.putExtra("eventCurrentAttendees", eventCurrentAttendees);


            startActivity(annoucementIntent);

        });
    }




}