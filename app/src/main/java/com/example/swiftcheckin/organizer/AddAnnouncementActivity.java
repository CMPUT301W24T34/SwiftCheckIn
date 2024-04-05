package com.example.swiftcheckin.organizer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import com.example.swiftcheckin.R;
import com.example.swiftcheckin.attendee.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class AddAnnouncementActivity extends AppCompatActivity {

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);

        eventId = getIntent().getStringExtra("eventId");
        Button cancelAnnouncement = findViewById(R.id.addAnnouncementCancelButton);
        Button saveAnnouncement = findViewById(R.id.addAnnouncementSaveButton);
        EditText editAnnouncementHeading = findViewById((R.id.announcementEditHeading));
        EditText editAnnouncementDes = findViewById(R.id.announcementEditDes);

        cancelAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnnouncementToFirebase();
                Toast.makeText(getApplicationContext(), "To be continued", Toast.LENGTH_SHORT).show();
                finish();
            }
        });



    }

    public void saveAnnouncementToFirebase(){
        // 1. Get the edittexts here
        EditText editAnnouncementHeading = findViewById((R.id.announcementEditHeading));
        String announcementHeading = editAnnouncementHeading.getText().toString();

        EditText editAnnouncementDes = findViewById(R.id.announcementEditDes);
        String announcementDes = editAnnouncementDes.getText().toString();
        // 2. Get the instance of Firebase here
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 3. Create a collection and create announcement object.

        DocumentReference eventWithAnnounce = db.collection("Announcements").document(eventId + announcementHeading);

        HashMap<String, String> data = new HashMap<>();
        data.put("announcementTitle", announcementHeading);
        data.put("announcementDes", announcementDes);

        eventWithAnnounce
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {   // In the event, the event is added.
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Announcement Added Successfully");
                        Toast.makeText(AddAnnouncementActivity.this, "Announcement Added Successfully", Toast.LENGTH_SHORT).show();
                        sendPushNotificationToAttendees(announcementHeading, announcementDes);
                    }
                })
                .addOnFailureListener(new OnFailureListener(){ // In the event, the event fails to be added to Firebase.
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Announcement could not be added.");
                        Toast.makeText(AddAnnouncementActivity.this, "Announcement could not be added.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void sendPushNotificationToAttendees(String announcementHeading, String announcementDes) {
        List<String> attendeesDeviceTokens = getAttendeesDeviceTokensFromFirestore();

        if (!attendeesDeviceTokens.isEmpty()) {
            Map<String, String> notificationData = new HashMap<>();
            notificationData.put("title", announcementHeading);
            notificationData.put("body", announcementDes);

            for (String deviceToken : attendeesDeviceTokens) {
                FirebaseMessaging.getInstance().sendToDevice(deviceToken)
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Push notification sent successfully to " + deviceToken);
                                } else {
                                    Log.e(TAG, "Error sending push notification to " + deviceToken, task.getException());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error sending push notification to " + deviceToken, e);
                            }
                        })
                        .addData(notificationData);
            }
        } else {
            Log.d(TAG, "No attendees' device tokens found.");
        }
    }

        // Implement this method to retrieve the attendees' device tokens from Firestore
        private List<String> getAttendeesDeviceTokensFromFirestore() {
            List<String> deviceTokens = new ArrayList<>();

            // Get the profileList from the ViewCheckInActivity
            ViewCheckInActivity viewCheckInActivity = new ViewCheckInActivity();
            viewCheckInActivity.queryAttendees(eventId); // Call the queryAttendees method to populate the profileList

            // Retrieve the device tokens from the profileList
            for (Profile profile : viewCheckInActivity.profileList) {
                String deviceToken = profile.getDeviceToken();
                if (deviceToken != null) {
                    deviceTokens.add(deviceToken);
                }
            }

            return deviceTokens;
        }
    }
