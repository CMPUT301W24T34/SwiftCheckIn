package com.example.swiftcheckin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This activity is meant to show the organizer's view of the app. They are able to view their own events, their posters,
 * and which attendees have signed up for them.
 */
public class OrganizerActivity extends AppCompatActivity {
    private ArrayList<Event> dataList;   // Represents the list that will store all the events.
    private ListView eventList;  // List view in activity_organizer.
    private EventArrayAdapter eventAdapter; // Adapter meant to keep track of changes in the number of events.

    private String deviceId;  // Represents the current device Id
    private FirebaseFirestore db;  // Represents an instance of the Firestore database

    Button addEventButton; // Button to add events.


    /*
        Citation: OpenAI, Date: 5 March, 2024. ChatGPT. Used to transfer data and call a function from one activity(AddEventActivity) to another(OrganizerActivity), without launching OrganizerActivity again.
        I was struggling with calling a function in OrganizerActivity from the AddEventActivity. The only way I knew was from DialogFragment that was taught in the labs.
        But, that approach wouldn't work here, because getContext() doesn't work in activities, and getParent() I see online(some forum) was not working for me.

        So I asked ChatGPT.

        Prompt:if one activity calls another activity, then how can I get which activity called me?

        Answer:

                To determine which activity has launched the current activity, you can use Intent extras to pass information between activities. When launching the second activity from the first activity, you can include an extra in the Intent to specify the caller activity. Then, in the second activity, you can retrieve this extra to identify the caller.

        Here's how you can do it:

        In the first activity (caller activity), when starting the second activity, include an extra in the Intent:
        Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
        intent.putExtra("caller", "FirstActivity");
        startActivity(intent);

        In the second activity (called activity), retrieve the extra from the Intent to identify the caller:
        String caller = getIntent().getStringExtra("caller");
        if (caller != null) {
            // Perform actions based on the caller activity
            if (caller.equals("FirstActivity")) {
                // The FirstActivity called this activity
            } else if (caller.equals("AnotherActivity")) {
                // Another activity called this activity
            }
        } else {
            // No caller information found
        }
        Replace "FirstActivity" and "AnotherActivity" with the appropriate identifiers for your activities.

        --------
        Followup:

        public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEventDialogListener){
            listener = (AddEventDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddEventDialogListener");
            }
        }

        i want to do something like this, and then call on the listener's method to add a value to a list.
        But this is with fragment, and I want to implement similar behaviour on an activity, so just passing string in Intent wont work

        See,
        organizingActivity launches EditActivity, and EditActivity calls a method of organizingActivity(both extend AppCompatActivity) to a particular function.
        I have to implement this

        Answer:

        If you want to implement communication between EditActivity and OrganizingActivity where EditActivity calls a method of OrganizingActivity to perform a specific action,
        you can an use a broadcast receivers to achieve communication between EditActivity and OrganizingActivity.

        Here's how you can implement it:

        Define a Custom Action String: Define a custom action string to identify the broadcast in both activities. This action string will be used to send and receive broadcasts.

        Send Broadcast from EditActivity: When you need to call a method in OrganizingActivity, send a broadcast with the custom action string.

        Receive Broadcast in OrganizingActivity: Register a BroadcastReceiver in OrganizingActivity to listen for the broadcast with the custom action string. When the broadcast is received, perform the desired action.

        Here's a basic implementation:

        In EditActivity:

        public class EditActivity extends AppCompatActivity {

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_edit);

                // Send broadcast to OrganizingActivity
                Intent intent = new Intent("com.example.ACTION_EDIT_ACTIVITY");
                sendBroadcast(intent);
            }
        }

        In OrganizingActivity:

        private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Perform the desired action when broadcast is received
            if ("com.example.ACTION_EDIT_ACTIVITY".equals(intent.getAction())) {
                // Call the method or perform the action in OrganizingActivity
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_organizing);
    
            // Register BroadcastReceiver to listen for the broadcast
            IntentFilter filter = new IntentFilter("com.example.ACTION_EDIT_ACTIVITY");
            registerReceiver(receiver, filter);
        }};

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // Unregister the BroadcastReceiver
            unregisterReceiver(receiver);
        }


    */
    /**
     * Receives signal(intent) that is broadcast from AddEventActivity class.
     * Used for receiving updates of the new event added.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {   // Receives intents from AddEventActivity to add events.
            String eventName = intent.getStringExtra("eventName");
            String eventAddress = intent.getStringExtra("eventAddress");
            String eventStartDate = intent.getStringExtra("eventStartDate");
            String eventEndDate = intent.getStringExtra("eventEndDate");
            String eventStartTime = intent.getStringExtra("eventStartTime");
            String eventEndTime = intent.getStringExtra("eventEndTime");
            String eventDescription = intent.getStringExtra("eventDescription");
            String eventPosterURL = intent.getStringExtra("eventPosterURL");
            String eventMaxAttendees = intent.getStringExtra("eventMaxAttendees");

            if ("com.example.ADD_EVENT".equals(intent.getAction())) {
                // Meant to add the events.
                if (eventMaxAttendees.isEmpty()) {
                    addEvent(new Event(eventName, eventDescription, eventAddress, deviceId, eventPosterURL, eventStartDate, eventEndDate, eventStartTime, eventEndTime));
                } else {
                    addEvent(new Event(eventName, eventDescription, eventAddress, deviceId, eventPosterURL, eventMaxAttendees, eventStartDate, eventEndDate, eventStartTime, eventEndTime));

                }
            }
        }
    };


    /**
     * Adds an event to the organizer's view
     * @param event - Represents the event that will be added.
     */
    public void addEvent(Event event) {
        eventAdapter.add(event);
        eventAdapter.notifyDataSetChanged();
        saveData(event);   // How we'll save the data to firebase

    }

    /**
     * Method will help start the organizer activity.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        db = FirebaseFirestore.getInstance();  // Instance of the Firestore database.

        // Id of the device that will go to the organizer mode.
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        getData();


        // registering receiver
        IntentFilter filter = new IntentFilter("com.example.ADD_EVENT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        }

        // Initializing the necessary tools
        eventList = findViewById(R.id.event_list);
        dataList = new ArrayList<>();
        eventAdapter = new EventArrayAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);
        FloatingActionButton backButtonOrg = findViewById(R.id.back_button_org);

        addEventButton = findViewById(R.id.add_event_button);

        // In order to view who signed up to the event.
        // Check ins have not been implemented yet.
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedPos = position;
                Intent intent = new Intent(OrganizerActivity.this, ViewAttendeesActivity.class);
                Event event = dataList.get(selectedPos);
                intent.putExtra("eventId", event.getDeviceId() + event.getEventTitle());
                startActivity(intent);
            }
        });

        // Opens the activity to add events to the organizer's event list.
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerActivity.this, AddEventActivity.class);
                intent.putExtra("deviceId", deviceId);
                startActivity(intent);
            }
        });

        // Goes back to the main page.
        backButtonOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * This method is meant to retrieve the data for the events from Firebase and present it onto the organizer's view.
     */
    private void getData(){
        CollectionReference eventCol = db.collection("events");  // Collection where the different events are stored.

        eventCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {   // From lab 5

                // Clear the old list
                dataList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)  // Loops through the different documents and retrieves their info.
                {

                    String eventTitleFrame = doc.getId();  // Document Id, AKA eventId = deviceId + eventTitle
                    if (eventTitleFrame.contains(deviceId)) {   // Ensures that the organizer can only see their events.
                        String eventTitle = (String) doc.getData().get("eventTitle");
                        String eventLocation = (String) doc.getData().get("eventLocation");
                        String eventDescription = (String) doc.getData().get("eventDescription");
                        String deviceID = (String) doc.getData().get("deviceId");

                        String eventImageURL = (String) doc.getData().get("eventPosterURL");
                        String startDate = (String) doc.getData().get("eventStartDate");
                        String endDate = (String) doc.getData().get("eventEndDate");
                        String startTime = (String) doc.getData().get("eventStartTime");
                        String endTime = (String) doc.getData().get("eventEndTime");
                        String maxAttendees = (String) doc.getData().get("eventMaxAttendees");

                        if (maxAttendees == null || maxAttendees.equals("-1")) {   // Was meant to work in case there was no limit for max attendees.
                            dataList.add(new Event(eventTitle, eventDescription, eventLocation, deviceID, eventImageURL, startDate, endDate, startTime, endTime));
                        } else {   // In case max attendees was specified.
                            dataList.add(new Event(eventTitle, eventDescription, eventLocation, deviceID, eventImageURL, maxAttendees, startDate, endDate, startTime, endTime));
                        }
                    }// Adding event details from FireStore

                }
                eventAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud


            }
        });
    }

    /**
     * Saves the data from a new event to the Firestore database.
     * @param event - Represents the event that is to be saved.
     */
    private void saveData(Event event){;
        // Ensure we have 3 columns in firestore for simple reference.
        // This is to ensure admin will be able to delete any events much more conveniently.
        DocumentReference deviceRef = db.collection("events").document(deviceId + event.getEventTitle());
        // Document Id, AKA eventId = deviceId + eventTitle

        // Hashmap method learned in labs.
        HashMap<String, String> data = new HashMap<>();
        data.put("eventTitle", event.getEventTitle());
        data.put("eventLocation", event.getLocation());
        data.put("deviceId", event.getDeviceId());
        data.put("eventDescription", event.getDescription());
        data.put("eventPosterURL", event.getEventImageUrl());
        data.put("eventStartDate", event.getStartDate());
        data.put("eventEndDate", event.getEndDate());
        data.put("eventStartTime", event.getStartTime());
        data.put("eventEndTime", event.getEndTime());
        data.put("eventMaxAttendees", Integer.toString(event.getMaxAttendees()));
        // Sets the data to Firebase.
        deviceRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {   // In the event, the event is added.
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Event Added Successfully");
                        Toast.makeText(OrganizerActivity.this, "Event Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener(){ // In the event, the event fails to be added to Firebase.
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Event could not be added.");
                        Toast.makeText(OrganizerActivity.this, "Event could not be added.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Destroys the Broadcast receiver, when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver
        unregisterReceiver(receiver);
    }
}