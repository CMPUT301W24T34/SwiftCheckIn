package com.example.swiftcheckin.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftcheckin.organizer.Event;

import com.example.swiftcheckin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity displays events that the user has signed up for.
 */
public class MyEventActivity extends AppCompatActivity {

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

        db = FirebaseFirestore.getInstance();

        setupUI();
        getData();
    }


    private void setupUI() {
        // Profile Picture Button
        ImageView profileButton = findViewById(R.id.profile_picture);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventActivity.this, ProfileActivity.class);
            startActivity(intent);
        });


        // Switch Mode Button
        FloatingActionButton fab = findViewById(R.id.switch_modes);
        fab.setOnClickListener(v -> new SwitchModeFragment().show(getSupportFragmentManager(), "Switch Modes"));

        // Main Event Page , for all events
        TextView myEventPage = findViewById(R.id.other_events);
        myEventPage.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventActivity.this, MainActivity.class);
            startActivity(intent);
        });


        // Camera Button
        ImageView cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventActivity.this, QRCodeScannerActivity.class);
            startActivity(intent);
        });


        // Event Details
        listViewEvents.setOnItemClickListener((parent, view, position, id) -> {
            Intent annoucementIntent = new Intent(MyEventActivity.this, AnnoucementActivity.class);
            String eventID = eventList.get(position).getDeviceId() + eventList.get(position).getEventTitle();

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


            startActivity(annoucementIntent);

        });
    }


// OpenAI, March 2, 2024, ChatGPT, Took help from ChatGPT on how to fetch list of events the user
// has signed up for, and using those event ids to fetch their respective event data

    /**
     * Fetches the data of events that the user has signed up for from Firestore.
     */
    private void getData() {
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        DocumentReference deviceRef = db.collection("SignedUpEvents").document(deviceId);
        deviceRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                List<String> eventIds = new ArrayList<>();
                if (document.exists()) {
                    eventIds = (List<String>) document.get("eventIds");
                }
                fetchEventsData(eventIds);
            } else {
                Log.d("getData", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Fetches event data from Firestore based on event IDs.
     *
     * @param eventIds List of event IDs
     */
    private void fetchEventsData(List<String> eventIds) {
        CollectionReference eventCol = db.collection("events");
        eventList.clear(); // Clear the old list

        for (String eventId : eventIds) {
            eventCol.document(eventId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {


                        String eventTitle = document.getString("eventTitle");
                        String eventDescription = document.getString("eventDescription");
                        String eventLocation = document.getString("eventLocation");
                        String deviceId = document.getString("deviceId");
                        String eventImageUrl = document.getString("eventImageUrl");
                        String eventStartDate = document.getString("eventStartDate");
                        String eventStartTime = document.getString("eventStartTime");
                        String eventEndDate = document.getString("eventEndDate");
                        String eventEndTime = document.getString("eventEndTime");


                        eventList.add(new Event(eventTitle, eventDescription, eventLocation, deviceId,
                                eventImageUrl, eventStartDate, eventEndDate, eventStartTime, eventEndTime));

                        eventViewAdapter.notifyDataSetChanged(); // Notify the adapter to render new data
                    } else {
                        Log.d("fetchEventsData", "No such document");
                    }
                } else {
                    Log.d("fetchEventsData", "get failed with ", task.getException());
                }
            });
        }
    }

}