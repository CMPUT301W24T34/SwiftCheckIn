package com.example.swiftcheckin.attendee;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftcheckin.R;
import com.example.swiftcheckin.organizer.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;



/**
 * The main activity of the application responsible for displaying a list of events.
 */
public class MainActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private ListView listViewMyEvent;

    private EventViewAdapter eventViewAdapter;
    private EventViewAdapter myEventViewAdapter;
    private ArrayList<Event> eventList;
    private ArrayList<Event> myEventList;
    private static final String TAG = "MainActivity";
    private FirebaseAttendee db_attendee;


    TextView myEventButton;
    TextView eventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_otherevent);

        listViewMyEvent = findViewById(R.id.attendee_my_events_list);
        listViewEvents = findViewById(R.id.attendee_other_events_list);

        eventButton = findViewById(R.id.other_events);
         myEventButton= findViewById(R.id.my_events);


        eventList = new ArrayList<>();
        eventViewAdapter = new EventViewAdapter(this, eventList);
        listViewEvents.setAdapter(eventViewAdapter);

        myEventList = new ArrayList<>();
        myEventViewAdapter = new EventViewAdapter(this, myEventList);
        listViewMyEvent.setAdapter(myEventViewAdapter);

        db_attendee = new FirebaseAttendee();

        intializeSignedUpEventListButton(myEventButton);
        intializeEventListButton(eventButton);


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

        db_attendee.getMyEventIds(myEventList, getApplicationContext(),
                new FirebaseAttendee.EventListCallback() {
            @Override
            public void onDataFetched(ArrayList<Event> myEventList) {
                myEventViewAdapter.notifyDataSetChanged();
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
        listViewEvents.setOnItemClickListener((parent, view, position, id) -> {
            Intent annoucementIntent = new Intent(MainActivity.this, AnnoucementActivity.class);
            String eventID = eventList.get(position).getDeviceId() + eventList.get(position).getEventTitle();
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
        listViewMyEvent.setOnItemClickListener((parent, view, position, id) -> {
            Intent annoucementIntent = new Intent(MainActivity.this, AnnoucementActivity.class);
            String eventID = myEventList.get(position).getDeviceId() + eventList.get(position).getEventTitle();

            String eventTitle = myEventList.get(position).getEventTitle();
            String eventLocation = myEventList.get(position).getLocation();
            String eventDescription = myEventList.get(position).getDescription();
            String eventImageUrl = myEventList.get(position).getEventImageUrl();
            String eventStartDate = myEventList.get(position).getStartDate();
            String eventStartTime = myEventList.get(position).getStartTime();
            String eventEndDate = myEventList.get(position).getEndDate();
            String eventEndTime = myEventList.get(position).getEndTime();
            String eventMaxAttendees = Integer.toString(myEventList.get(position).getMaxAttendees());
            String eventCurrentAttendees = Integer.toString(myEventList.get(position).getCurrentAttendees());

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

    protected void intializeEventListButton(TextView view1) {

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventList();

            }
        });
    }

    protected void intializeSignedUpEventListButton(TextView view1) {

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignedUpEventList();

            }
        });
    }


    private void showSignedUpEventList() {
         if (listViewMyEvent.getVisibility() == View.INVISIBLE) {
             listViewMyEvent.setVisibility(View.VISIBLE);
             myEventButton.setBackgroundResource(R.drawable.grey_circle_background);

             listViewEvents.setVisibility(View.INVISIBLE);
             eventButton.setBackground(null);

        }
    }

    private void showEventList() {
        if(listViewEvents.getVisibility() == View.INVISIBLE) {
            listViewEvents.setVisibility(View.VISIBLE);
            eventButton.setBackgroundResource(R.drawable.grey_circle_background);

            listViewMyEvent.setVisibility(View.INVISIBLE);
            myEventButton.setBackground(null);
        }
    }


}