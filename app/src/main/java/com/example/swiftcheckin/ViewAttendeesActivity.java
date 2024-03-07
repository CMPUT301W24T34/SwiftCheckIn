package com.example.swiftcheckin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ViewAttendeesActivity extends AppCompatActivity {

    ArrayList<Profile> profileList;
    ListView dataList;
    private FirebaseFirestore db;

    FloatingActionButton back_button;
    private String eventId;

    TextView eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendees);
        back_button = findViewById(R.id.viewAttendeeBackButton);
        eventTitle = findViewById(R.id.viewAttendeeEventTitle);
        eventId = getIntent().getStringExtra("eventId");

        Event event = createEvent(eventId);



        eventTitle.setText(event.getDescription());


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public Event createEvent(String eventId){
        Event updated_event;
        final String[] eventTitle = new String[1];
        final String[] eventStartTime = new String[1];
        final String[] eventStartDate = new String[1];
        final String[] eventPosterURL = new String[1];
//        final String[] eventMaxAttendees = new String[1];
        final String[] eventLocation = new String[1];
        final String[] eventEndTime = new String[1];
        final String[] eventEndDate = new String[1];
        final String[] eventDescription = new String[1];
        final String[] eventDeviceId = new String[1];

        db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot event = task.getResult();
                            if (event.exists()){
                                eventTitle[0] = (String) event.get("eventTitle");
                                eventStartTime[0] = (String) event.get("eventStartTime");
                                eventStartDate[0] = (String) event.get("eventStartDate");
                                eventPosterURL[0] = (String) event.get("eventPosterURL");
//                                eventMaxAttendees[0] = (String) event.get("eventMaxAttendees");
                                eventLocation[0] = (String) event.get("eventLocation");
                                eventEndTime[0] = (String) event.get("eventEndTime");
                                eventEndDate[0] = (String) event.get("eventEndDate");
                                eventDescription[0] = (String) event.get("eventDescription");
                                eventDeviceId[0] = (String) event.get("deviceId");

                            }
                        }
                    }
                });
        updated_event = new Event(eventTitle[0], eventDescription[0], eventLocation[0], eventDeviceId[0], eventPosterURL[0], eventStartDate[0], eventEndDate[0], eventStartTime[0], eventEndTime[0]);
        return updated_event;

    }


    // How to retrieve data from firebase and put it into a listview?
    // How do I even get the event id??? Done
    // Get the Back button working Done
    // Change the title
            // Need to create and return event
    // How to query through the event to get to the thing?
}