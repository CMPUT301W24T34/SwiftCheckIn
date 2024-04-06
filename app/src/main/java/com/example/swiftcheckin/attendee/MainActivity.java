package com.example.swiftcheckin.attendee;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
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
    private static final int PERMISSION_REQUEST_CODE = 123;
    String eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_otherevent);

        listViewEvents = findViewById(R.id.attendee_other_events_list);
        eventList = new ArrayList<>();
        eventViewAdapter = new EventViewAdapter(this, eventList);
        listViewEvents.setAdapter(eventViewAdapter);

        db = FirebaseFirestore.getInstance();



        setupUI();
        getData();

        requestNotificationPermission();

    }


    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * Fetches event data from Firestore.
     */

    private void getData(){
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        CollectionReference eventCol = db.collection("events");

        eventCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                eventList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    eventTitle = (String) doc.getData().get("eventTitle");
                    String eventDescription = (String) doc.getData().get("eventDescription");
                    String eventLocation = (String) doc.getData().get("eventLocation");
                    String deviceId = (String) doc.getData().get("deviceId");
                    String eventImageUrl = (String) doc.getData().get("eventPosterURL");
                    String eventStartDate = (String) doc.getData().get("eventStartDate");
                    String eventStartTime = (String) doc.getData().get("eventStartTime");
                    String eventEndDate = (String) doc.getData().get("eventEndDate");
                    String eventEndTime = (String) doc.getData().get("eventEndTime");
                    String eventMaxAttendees = (String) doc.getData().get("eventMaxAttendees");
                    String eventCurrentAttendees = (String) doc.getData().get("eventCurrentAttendees");

                    Event event;

                    if (eventMaxAttendees.equals("-1")) {
                        event = new Event(eventTitle, eventDescription, eventLocation, deviceId,
                                eventImageUrl, eventStartDate, eventEndDate, eventStartTime, eventEndTime);

                    } else {
                        event = new Event(eventTitle, eventDescription, eventLocation, deviceId,
                                eventImageUrl, eventMaxAttendees, eventStartDate, eventEndDate, eventStartTime, eventEndTime);
                    }

                    if (eventCurrentAttendees != null) {
                        event.setCurrentAttendees(Integer.parseInt(eventCurrentAttendees));
                    } else {
                        event.setCurrentAttendees(0);
                    }


                    eventList.add(event);

//                    eventList.add(new Event(eventTitle, eventDescription, eventLocation, deviceId
//                            , eventImageUrl,eventStartDate,eventEndDate, eventStartTime, eventEndTime ));
                }
                eventViewAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with sending notifications
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
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