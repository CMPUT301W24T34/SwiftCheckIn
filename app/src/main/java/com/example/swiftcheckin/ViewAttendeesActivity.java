package com.example.swiftcheckin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.swiftcheckin.admin.ProfileArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class helps an organizer see who is signed up for their event.
 */
public class ViewAttendeesActivity extends AppCompatActivity {

    ArrayList<Profile> profileList = new ArrayList<>();  // Attendees are represented by profiles
    ListView dataList;  // For multiple profiles
    FloatingActionButton back_button;
    ProfileArrayAdapter profileArrayAdapter;   // From Admin

    TextView bigEventTitle;  // Name change due to complications regarding creating the activity.

    /**
     * Methods helps to start the activity.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendees);
        back_button = findViewById(R.id.viewAttendeeBackButton);
        bigEventTitle = findViewById(R.id.viewAttendeeEventTitle);
        profileArrayAdapter = new ProfileArrayAdapter(this, profileList);
        dataList = findViewById(R.id.viewAttendeeEventList);
        dataList.setAdapter(profileArrayAdapter);

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
        queryAttendees(eventId);  // Query through the attendees who have signed up.

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Meant to create a profile based on the information and retrieving it from firebase.
     * @param profileId - A representation of a profile that will allow us to access their data.
     */
    public void createProfile(String profileId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference profileRef = db.collection("profiles").document(profileId);


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

                        ;
                        // Create and add the profile.
                        profileList.add(new Profile(profileName, profileBday, profilePhone, profileEmail, profileWebsite, profileImageUrl, profileAddress, permission));
                        profileArrayAdapter.notifyDataSetChanged();

                    }
                }
            }
        });
    }

    /**
     * This method is meant for us to query to get the Attendee information of those who will sign up.
     * @param eventId - Representative of the event the attendees have signed up for.
     */
    public void queryAttendees(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("eventsWithAttendees").document(eventId);

        // Citation: OpenAI, 03-05-2024, ChatGPT, How to query through fields?
        /*
        Output:
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document exists, retrieve the data
                    Map<String, Object> eventData = document.getData();
                    if (eventData != null) {
                        // Iterate through the fields to find attendees
                        for (Map.Entry<String, Object> entry : eventData.entrySet()) {
                            String field = entry.getKey();
                            Object value = entry.getValue();
                            // Check if the field represents an attendee
                            if (field.startsWith("attendee_")) {
                                String attendeeId = field.substring("attendee_".length());
                                // Now you can use the attendee ID or its associated value
                                System.out.println("Attendee ID: " + attendeeId + ", Data: " + value);
                            }
                        }
                    }
                } else {
                    System.out.println("No such document!");
                }
            } else {
                System.out.println("Failed with: " + task.getException());
            }
        }
    });
         */

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
                                createProfile(profileKey);  // Create profile
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
}