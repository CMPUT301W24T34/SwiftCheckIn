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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class ViewAttendeesActivity extends AppCompatActivity {

    ArrayList<Profile> profileList = new ArrayList<>();
    ListView dataList;

    FloatingActionButton back_button;
    ProfileArrayAdapter profileArrayAdapter;

    TextView bigEventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendees);
        back_button = findViewById(R.id.viewAttendeeBackButton);
        bigEventTitle = findViewById(R.id.viewAttendeeEventTitle);
        profileArrayAdapter = new ProfileArrayAdapter(this, profileList);
        dataList = findViewById(R.id.viewAttendeeEventList);
        dataList.setAdapter(profileArrayAdapter);

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
        queryAttendees(eventId);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void createProfile(String profileId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference profileRef = db.collection("profiles").document(profileId);


        profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Map<String, Object> profileData = document.getData();
                        String profileName = (String) profileData.get("name");

                        String profilePhone = (String) profileData.get("phoneNumber");
                        ;
                        String profileAddress = (String) profileData.get("address");
                        ;
                        String profileLocPermission = (String) profileData.get("locationPermission");
                        Boolean permission = Boolean.parseBoolean(profileLocPermission);
                        ;
                        String profileWebsite = (String) profileData.get("website");
                        ;
                        String profileImageUrl = (String) profileData.get("profileImageUrl");

                        String profileEmail = (String) profileData.get("email");

                        ;
                        String profileBday = (String) profileData.get("birthday");

                        ;
                        profileList.add(new Profile(profileName, profileBday, profilePhone, profileEmail, profileWebsite, profileImageUrl, profileAddress, permission));
                        profileArrayAdapter.notifyDataSetChanged();
                        Log.e("Added Profile?", "True");

                    }
                }
            }
        });
    }



    public void queryAttendees(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("eventsWithAttendees").document(eventId);

        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> eventData = document.getData();
                        if (eventData != null) {
                            for (Map.Entry<String, Object> entry : eventData.entrySet()) {
                                String profileKey = entry.getKey();
                                Object profileValue = entry.getValue();
                                createProfile(profileKey); // Call your method here
                            }
                        }
                    } else {
                        Log.e("Document Doesn't Exist", "No such document");
                    }
                } else {
                    Log.e("Failed Task", "Error getting documents.", task.getException());
                }
            }
        });
    }
}