package com.example.swiftcheckin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private EventViewAdapter eventViewAdapter;
    private ArrayList<Event> eventList;
    private static final String TAG = "MainActivity";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_myevent);

        listViewEvents = findViewById(R.id.attendee_my_events_list);
        eventList = new ArrayList<>();
        eventViewAdapter = new EventViewAdapter(this, eventList);
        listViewEvents.setAdapter(eventViewAdapter);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Setup UI elements
        setupUI();

        // Fetch data from Firestore
        getData();
    }

    private void setupUI() {
        // Profile Picture Button
        ImageView profileButton = findViewById(R.id.profile_picture);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Switch Mode Button
        FloatingActionButton fab = findViewById(R.id.switch_modes);
        fab.setOnClickListener(v -> new SwitchModeFragment().show(getSupportFragmentManager(), "Switch Modes"));

        // Camera Button
        ImageView cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> Toast.makeText(MainActivity.this, "This does nothing yet", Toast.LENGTH_SHORT).show());
    }

    private void getData(){
        Log.d("D", "Opening get DATA FUNC");
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        CollectionReference eventCol = db.collection("attendeeEventList");

        eventCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    Log.e(TAG, "Error fetching events", error);
                    return;
                }
                Log.d("TAG","Opening firebaes");

                eventList.clear(); // Clear the old list
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    String eventTitleFrame = doc.getId();
                    String eventTitle = (String) doc.getData().get("eventName");
                    String eventDate = (String) doc.getData().get("eventDate");
                    String eventStartTime = (String) doc.getData().get("eventStartTime");
                    String eventEndTime = (String) doc.getData().get("eventEndTime");
                    String eventAmPm = (String) doc.getData().get("eventAmPm");

                    Log.d(TAG, "Event added successfully");

//                    eventList.add(new Event(eventTitle, null, null, null, eventDate, eventStartTime, eventEndTime, eventAmPm));
                }
                eventViewAdapter.notifyDataSetChanged(); // Notify the adapter to render new data
            }
        });
    }
}
