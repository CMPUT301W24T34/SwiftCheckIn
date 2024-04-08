package com.example.swiftcheckin.attendee;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.swiftcheckin.R;
import com.example.swiftcheckin.organizer.EventSignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
// Right now there is no way to unsign up for an event

/**
 * This class deals with the announcement activity to show details and announcements of an event as well as allowing attendees to sign up for an event.
 */
public class AnnoucementActivity extends AppCompatActivity {

    private Button sign_up;
    private FirebaseFirestore db;
    private ArrayList<Announcement> dataList;
    private ListView announcementList;
    private AnnouncementArrayAdapter announceAdapter;
    EventSignUp eventSignUp = new EventSignUp();
    private FirebaseAttendee fb;


    /**
     * Initializes the activity and sets up necessary components.
     *
     * @param savedInstanceState a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_announcement);
        db = FirebaseFirestore.getInstance();
        announcementList = findViewById(R.id.announcementList);
        dataList = new ArrayList<>();
        announceAdapter = new AnnouncementArrayAdapter(this, dataList);
        announcementList.setAdapter(announceAdapter);
        fb = new FirebaseAttendee();

        String eventId = getIntent().getStringExtra("eventID");
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        getData(eventId);
        String eventMaxAttendees = getIntent().getStringExtra("eventMaxAttendees");
        String eventCurrentAttendees = getIntent().getStringExtra("eventCurrentAttendees");

        Button sign_up = findViewById(R.id.sign_up);
        // Add a condition here, if current attendees != maxAttendees
        // If it is, grey out the button and make it unclickable
        if (eventMaxAttendees.equals(eventCurrentAttendees)) {
            sign_up.setBackgroundColor(Color.LTGRAY);
        }

        DocumentReference eventCheckRef = fb.getDb().collection("events").document(eventId);
        eventCheckRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.exists()){
                    Toast.makeText(getApplicationContext(), "Event has been deleted by admin.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DocumentReference profileDoc = db.collection("profiles").document(deviceId);
                profileDoc.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (!eventMaxAttendees.equals(eventCurrentAttendees)) {
                                Toast.makeText(AnnoucementActivity.this, "Please wait a moment to get signed up", Toast.LENGTH_LONG).show();
                                fb.saveSignUpData(deviceId, eventId, AnnoucementActivity.this);
                                eventSignUp.addAttendeeToEvent(eventId, deviceId, eventMaxAttendees, eventCurrentAttendees);

                                // Remove whitespace from eventId
                                String topicName = "event_" + eventId.replaceAll("\\s+", "_");

                                FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + topicName)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Subscribed to topic: /topics/" + topicName);
                                                } else {
                                                    Log.e(TAG, "Failed to subscribe to topic: /topics/" + topicName);
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "Event is full", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "You must create a profile before you can sign up for events", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        ImageView profileButton = findViewById(R.id.profile_picture);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AnnoucementActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        FloatingActionButton fab = findViewById(R.id.switch_modes);
        fab.setOnClickListener(v -> new SwitchModeFragment().show(getSupportFragmentManager(), "Switch Modes"));

        Intent intent = getIntent();
        String eventTitle = intent.getStringExtra("eventTitle");
        String eventLocation = intent.getStringExtra("eventLocation");
        String eventDescription = intent.getStringExtra("eventDescription");
        String eventStartDate = intent.getStringExtra("eventStartDate");
        String eventStartTime = intent.getStringExtra("eventStartTime");
        String eventEndTime = intent.getStringExtra("eventEndTime");

        TextView textViewEventTitle = findViewById(R.id.announcement_event_name);
        TextView textViewEvenLocation = findViewById(R.id.announcement_location);
        TextView textViewEventDescription = findViewById(R.id.announcement_description);
        TextView textViewEvenStartDate = findViewById(R.id.annoucement_event_date);
        TextView textViewEventStartTime = findViewById(R.id.annoucement_start_time);
        TextView textViewEventEndTime = findViewById(R.id.announcement_end_Time);

        textViewEventTitle.setText(eventTitle);
        textViewEvenLocation.setText(eventLocation);
        textViewEventDescription.setText(eventDescription);
        textViewEvenStartDate.setText(eventStartDate);
        textViewEventStartTime.setText(eventStartTime);
        textViewEventEndTime.setText(eventEndTime);


        // OpenAI: ChatGPT Marc 4, 2024 - Added glide to save & fetch image from firebase
        ImageView imageViewEventPoster = findViewById(R.id.announcement_event_poster1);
        String eventImageUrl = intent.getStringExtra("eventImageUrl");
        if (eventImageUrl != null && !eventImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(eventImageUrl)
                    .into(imageViewEventPoster);
        }
    }

    // Need something to get data related to announcements, portraying data will be here.
    private void getData(String eventId) {
        CollectionReference announceCol = db.collection("Announcements@");

        announceCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                dataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String announcementTitleFrame = doc.getId();
                    String eventName = EventUtils.convertEventIdToEventName(eventId);
                    if (announcementTitleFrame.contains(eventName + "@")) {
                        String announcementTitle = (String) doc.getData().get("announcementTitle");
                        String announcementDes = (String) doc.getData().get("announcementDes");
                        dataList.add(new Announcement(announcementTitle, announcementDes));
                    }
                }
                announceAdapter.notifyDataSetChanged();
            }
        });
    }

}