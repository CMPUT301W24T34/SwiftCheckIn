package com.example.swiftcheckin.organizer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.swiftcheckin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

    private FirebaseOrganizer dbOrganizer;

    LinearLayout addEventButton; // Button to add events.

    interface QrImageCallback {
        void onQrImageReceived(Bitmap bitmap);
    }


    /*
        Citation: OpenAI, Date: 5 March, 2024. ChatGPT. Used to transfer data and call a function from one activity(AddEventActivity) to another(OrganizerActivity), without launching OrganizerActivity again.
        I was struggling with calling a function in OrganizerActivity from the AddEventActivity. The only way I knew was from DialogFragment that was taught in the labs.
        But, that approach wouldn't work here, because getContext() doesn't work in activities, and getParent() I saw online(some forum) was not working for me.

        So I asked ChatGPT. I gave some background information to it like See,
        organizingActivity launches EditActivity, and EditActivity calls a method of organizingActivity(both extend AppCompatActivity) to a particular function.

        Chatgpt presented me with the following code:

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
            String qrCodeID = intent.getStringExtra("qrCodeID");
            String qrPromoCodeID = intent.getStringExtra("promoQrID");

            if ("com.example.ADD_EVENT".equals(intent.getAction())) {
                // Meant to add the events.
                assert eventMaxAttendees != null;
                Event event;
                if (eventMaxAttendees.isEmpty()) {
                    event = new Event(eventName, eventDescription, eventAddress, deviceId, eventPosterURL, eventStartDate, eventEndDate, eventStartTime, eventEndTime);
                } else {
                    event = new Event(eventName, eventDescription, eventAddress, deviceId, eventPosterURL, eventMaxAttendees, eventStartDate, eventEndDate, eventStartTime, eventEndTime);
                }
                event.setQrPromoID(qrPromoCodeID);
                event.setQrID(qrCodeID);
                addEvent(event);
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
        dbOrganizer = new FirebaseOrganizer(getApplicationContext());

        // Id of the device that will go to the organizer mode.
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


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

        getData();

        addEventButton = findViewById(R.id.add_event_button);

        // In order to view who signed up to the event.
        // Check ins have not been implemented yet.
        eventList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedPos = position;
                // Start the Dialog fragment with all the options. Add the buttons to send notifications and view who signed up.

//                Intent intent = new Intent(OrganizerActivity.this, ViewAttendeesActivity.class);
                Event event = dataList.get(selectedPos);

                String eventId = event.getDeviceId()+event.getEventTitle();

                getAssociatedQrImage(eventId, new QrImageCallback() {
                    @Override
                    public void onQrImageReceived(Bitmap bitmap) {
                        if (bitmap != null) {
                            SwitchOrgDetailsFragment dialogFragment = new SwitchOrgDetailsFragment(eventId, bitmap, event.getEventTitle());
                            dialogFragment.show(getSupportFragmentManager(), "eventId");
                        } else {
                            Log.e("Error dialog qr", "Image not generated and failed to open dialogbox");
                        }
                    }
                });

//                intent.putExtra("eventId", event.getDeviceId() + event.getEventTitle());
//                startActivity(intent);

                return true;
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
        dbOrganizer.saveEvent(event);
    }

    private Bitmap getAssociatedQrImage(String eventId, QrImageCallback callback)
    {
        dbOrganizer.getAssociatedCodeID(eventId, "qrID", new FirebaseOrganizer.QrIDCallback() {
            @Override
            public void onQrIDReceived(String qrID) {
                if (qrID != null) {
                   Bitmap bitmap = QrCodeManager.generateQRCode(qrID);
                    callback.onQrImageReceived(bitmap);
                } else {
                    callback.onQrImageReceived(null);
                }
            }
        });
        return null;
    }

    /**
     * Overriding onDestroy method of the activity.
     * Modified to destroy broadcast receiver when activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver
        unregisterReceiver(receiver);
    }
}