package com.example.swiftcheckin.organizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.swiftcheckin.R;
import com.example.swiftcheckin.admin.ProfileArrayAdapter;
import com.example.swiftcheckin.attendee.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class ViewCheckInActivity extends AppCompatActivity {

    ArrayList<Profile> profileList = new ArrayList<>();  // Attendees are represented by profiles
    ListView dataList;  // For multiple profiles
    FloatingActionButton back_button;
    CheckInArrayAdapter checkInArrayAdapter;   // From Admin

    TextView bigEventTitle;  // Name change due to complications regarding creating the activity.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_check_in);
        back_button = findViewById(R.id.viewCheckInBackButton);
        bigEventTitle = findViewById(R.id.viewCheckInEventTitle);
        //checkInArrayAdapter = new CheckInArrayAdapter(this, profileList);
        dataList = findViewById(R.id.viewCheckInEventList);
        dataList.setAdapter(checkInArrayAdapter);

        String eventId = getIntent().getStringExtra("eventId");  // eventId passed through Intent

        FirebaseFirestore db = FirebaseFirestore.getInstance();  // Instance of firebase
        Event finalUpdated_event = new Event();
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            // Get information from the document.
                            DocumentSnapshot event = task.getResult();
                            String eventTitle  = (String) event.getData().get("eventTitle");

                            finalUpdated_event.setEventTitle(eventTitle);
                            bigEventTitle.setText(finalUpdated_event.getEventTitle());

                            String eventStartTime = (String) event.get("eventStartTime");
                            finalUpdated_event.setStartTime(eventStartTime);

                            String eventStartDate = (String) event.get("eventStartDate");
                            finalUpdated_event.setStartDate(eventStartDate);

                            String eventPosterURL = (String) event.get("eventPosterURL");
                            finalUpdated_event.setEventImageUrl(eventPosterURL);

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

    public void queryAttendees(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("checkedIn").document(eventId);

        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> eventData = document.getData();
                        if (eventData != null) {
                            for (Map.Entry<String, Object> entry : eventData.entrySet()) {   // Both are the same value, helps create profile.
                                String profileKey = entry.getKey();
                                Object profileValue = entry.getValue();
                                createProfile(profileKey, (String) profileValue);  // Create profile
                            }
                        }
                    } else {  // Some checks.
                        Log.e("Document Doesn't Exist", "No such document");
                    }
                } else {
                    Log.e("Failed Task", "Error getting documents.", task.getException());
                }
            }
        });
    }



    public void createProfile(String profileId, String profileCount){   // Add the value here, set the value in profile, and notify the new array adapter.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference profileRef = db.collection("profiles").document(profileId);

        int checkInCount = Integer.parseInt(profileCount);


        profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        // Get the information and create a profile object.
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

                        Profile profile = new Profile(profileName, profileBday, profilePhone, profileEmail, profileWebsite, profileImageUrl, profileAddress, permission);

                        profile.setCheckInCount(checkInCount);



                        ;
                        // Create and add the profile.
                        profileList.add(profile);
                        checkInArrayAdapter.notifyDataSetChanged();

                    }
                }
            }
        });
    }
}