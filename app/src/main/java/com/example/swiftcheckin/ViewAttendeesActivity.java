package com.example.swiftcheckin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ViewAttendeesActivity extends AppCompatActivity {

    ArrayList<Profile> profileList;
    ListView dataList;

    FloatingActionButton back_button;

    TextView bigEventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendees);
        back_button = findViewById(R.id.viewAttendeeBackButton);
        bigEventTitle = findViewById(R.id.viewAttendeeEventTitle);
        String eventId = getIntent().getStringExtra("eventId");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Event finalUpdated_event = new Event();
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot event = task.getResult();
                            String eventTitle  = (String) event.getData().get("eventTitle");
                            Log.e("eventTitle", eventTitle);
                            finalUpdated_event.setEventTitle(eventTitle);
                            Log.e("finaleventTitle", finalUpdated_event.getEventTitle());
                            bigEventTitle.setText(finalUpdated_event.getEventTitle());


                            String eventStartTime = (String) event.get("eventStartTime");
                            finalUpdated_event.setStartTime(eventStartTime);

                            String eventStartDate = (String) event.get("eventStartDate");
                            finalUpdated_event.setStartDate(eventStartDate);

                            String eventPosterURL = (String) event.get("eventPosterURL");
                            finalUpdated_event.setEventImageUrl(eventPosterURL);
//                                eventMaxAttendees[0] = (String) event.get("eventMaxAttendees");

                            String eventLocation = (String) event.get("eventLocation");
                            finalUpdated_event.setLocation(eventLocation);

                            String eventEndTime = (String) event.get("eventEndTime");
                            finalUpdated_event.setEndTime(eventEndTime);

                            String eventEndDate = (String) event.get("eventEndDate");
                            finalUpdated_event.setEndDate(eventEndDate);

                            String eventDescription = (String) event.get("eventDescription");
                            finalUpdated_event.setDescription(eventDescription);

                            String eventDeviceId = (String) event.get("deviceId");
                            finalUpdated_event.setDeviceId(eventDeviceId);

                        }
                    }
                });
        String title = finalUpdated_event.getEventTitle();


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public Event createEvent(String eventId){
        Event updated_event = new Event();
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(eventId);
        Event finalUpdated_event = updated_event;
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot event = task.getResult();
                                String eventTitle  = (String) event.get("eventTitle");
                                finalUpdated_event.setEventTitle(eventTitle);

                                String eventStartTime = (String) event.get("eventStartTime");
                                finalUpdated_event.setStartTime(eventStartTime);

                                String eventStartDate = (String) event.get("eventStartDate");
                                finalUpdated_event.setStartDate(eventStartDate);

                                String eventPosterURL = (String) event.get("eventPosterURL");
                                finalUpdated_event.setEventImageUrl(eventPosterURL);
//                                eventMaxAttendees[0] = (String) event.get("eventMaxAttendees");

                                String eventLocation = (String) event.get("eventLocation");
                                finalUpdated_event.setLocation(eventLocation);

                                String eventEndTime = (String) event.get("eventEndTime");
                                finalUpdated_event.setEndTime(eventEndTime);

                                String eventEndDate = (String) event.get("eventEndDate");
                                finalUpdated_event.setEndDate(eventEndDate);

                                String eventDescription = (String) event.get("eventDescription");
                                finalUpdated_event.setDescription(eventDescription);

                                String eventDeviceId = (String) event.get("deviceId");
                                finalUpdated_event.setDeviceId(eventDeviceId);

                        }
                    }
                });

        return finalUpdated_event;

    }


    // How to retrieve data from firebase and put it into a listview?
    // How do I even get the event id??? Done
    // Get the Back button working Done
    // Change the title
            // Need to create and return event
    // How to query through the event to get to the thing?
}