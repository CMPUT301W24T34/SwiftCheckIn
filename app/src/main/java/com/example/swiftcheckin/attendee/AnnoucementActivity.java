package com.example.swiftcheckin.attendee;

import static android.content.ContentValues.TAG;

import android.content.Intent;
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
import com.example.swiftcheckin.organizer.Event;
import com.example.swiftcheckin.organizer.EventArrayAdapter;
import com.example.swiftcheckin.organizer.EventSignUp;

import com.example.swiftcheckin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Right now there is no way to unsign up for an event
/**
 * This class deals with the announcement activity to show details and announcements of an event as well as allowing attendees to sign up for an event.
 */
public class AnnoucementActivity extends AppCompatActivity {

    private Button sign_up;
    private FirebaseFirestore db;
    private ArrayList<Announcement> dataList;   // Represents the list that will store all the events.
    private ListView announcementList;  // List view in activity_organizer.
    private AnnouncementArrayAdapter announceAdapter; // Adapter meant to keep track of changes in the number of events.
    EventSignUp eventSignUp = new EventSignUp();



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


        String eventId = getIntent().getStringExtra("eventID");
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String eventMaxAttendees = getIntent().getStringExtra("eventMaxAttendees");
        String eventCurrentAttendees = getIntent().getStringExtra("eventCurrentAttendees");
        getData(eventId);

        Button sign_up = findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveData(deviceId, eventId);
                eventSignUp.addAttendeeToEvent(eventId, deviceId, eventMaxAttendees, eventCurrentAttendees);

            }
        });

        ImageView profileButton = findViewById(R.id.profile_picture);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AnnoucementActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Switch Mode Button
        FloatingActionButton fab = findViewById(R.id.switch_modes);
        fab.setOnClickListener(v -> new SwitchModeFragment().show(getSupportFragmentManager(), "Switch Modes"));


        Intent intent = getIntent();
        String eventID = intent.getStringExtra("eventID");
        String eventTitle = intent.getStringExtra("eventTitle");
        String eventLocation = intent.getStringExtra("eventLocation");
        String eventDescription = intent.getStringExtra("eventDescription");
//        String eventImageUrl = intent.getStringExtra("eventImageUrl");
        String eventStartDate = intent.getStringExtra("eventStartDate");
        String eventStartTime = intent.getStringExtra("eventStartTime");
        String eventEndTime = intent.getStringExtra("eventEndTime");

//        TextView textViewEventID = findViewById(R.id.ann);
        TextView textViewEventTitle = findViewById(R.id.announcement_event_name);
        TextView textViewEvenLocation = findViewById(R.id.announcement_location);
        TextView textViewEventDescription = findViewById(R.id.announcement_description);
//        ImageView textViewEventImageURL = findViewById(R.id.announcement_event_poster1);
        TextView textViewEvenStartDate = findViewById(R.id.annoucement_event_date);
        TextView textViewEventStartTime = findViewById(R.id.annoucement_start_time);
        TextView textViewEventEndTime = findViewById(R.id.announcement_end_Time);



//        textViewEventID.setText(eventID);
        textViewEventTitle.setText(eventTitle);
        textViewEvenLocation.setText(eventLocation);
        textViewEventDescription.setText(eventDescription);
//        textViewEventImageURL.setText(eventImageUrl);
        textViewEvenStartDate.setText(eventStartDate);
        textViewEventStartTime.setText(eventStartTime);
        textViewEventEndTime.setText(eventEndTime);

        // OpenAI: ChatGPT Marc 4, 2024 - Added glide to save & fetch image from firebase

        ImageView imageViewEventPoster = findViewById(R.id.announcement_event_poster1);
        String eventImageUrl = intent.getStringExtra("eventImageUrl");
        Glide.with(this)
                .load(eventImageUrl)
                .into(imageViewEventPoster);
    }


    // Citation: OpenAI, 03-05-2024, ChatGPT, Saving the data as a list in an attribute called eventIds
    /* Chatgpt suggested to use Map<String, Object> data = new HashMap<>() for the list
    output was also about the oncompletelistener and document snapshots and tasks
    giving this code: ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
     and if (document.exists() && document.contains("eventIds")) to see if there is already a list for this user
     and List<String> eventIds = (List<String>) document.get("eventIds") to get it
    */

    /**
     * Saves the attendance data for a specific user and event.
     *
     * @param deviceId the unique identifier of the device
     * @param eventId  the unique identifier of the event
     */
    private void saveData(String deviceId, String eventId) {
        DocumentReference ref = db.collection("SignedUpEvents").document(deviceId);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> data = new HashMap<>();

                    // already has a list
                    if (document.exists() && document.contains("eventIds")) {
                        List<String> eventIds = (List<String>) document.get("eventIds");
                        if (!eventIds.contains(eventId)) {
                            eventIds.add(eventId);
                            data.put("eventIds", eventIds);
                            addData(data,ref);
                        }
                        // they've already signed up for this event
                        else{
                            Toast.makeText(AnnoucementActivity.this, "You are already signed up for this event", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // no list yet
                    else {
                        List<String> eventIds = new ArrayList<>();
                        eventIds.add(eventId);
                        data.put("eventIds", eventIds);
                        addData(data, ref);
                    }
                }
            }
        });
    }

    /**
     * Adds data to the Firestore database.
     *
     * @param data the data to be added
     * @param ref  the reference to the Firestore document
     */
    private void addData(Map<String, Object> data, DocumentReference ref){
        ref.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AnnoucementActivity.this, "Signed up!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Event ID added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error adding event ID", e);
                        Toast.makeText(AnnoucementActivity.this, "Could not sign up", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Need something to get data related to announcements, portraying data will be here.
    private void getData(String eventId){
        CollectionReference announceCol = db.collection("Announcements");

        announceCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                dataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){

                    String announcementTitleFrame = doc.getId();
                    if (announcementTitleFrame.contains(eventId)){
                        String announcementTitle= (String) doc.getData().get("announcementTitle");
                        String announcementDes = (String) doc.getData().get("announcementDes");

                        dataList.add(new Announcement(announcementTitle, announcementDes));
                    }
                }
                announceAdapter.notifyDataSetChanged();
            }
        });
    }


}
