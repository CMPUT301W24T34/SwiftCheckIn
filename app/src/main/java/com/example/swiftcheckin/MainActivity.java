package com.example.swiftcheckin;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


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

        setupUI();         // Setup UI elements
        getData();          // Fetch data from Firestore
    }

    private void getData(){
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        CollectionReference eventCol = db.collection("events");

        eventCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                eventList.clear(); // Clear the old list
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
//                    String eventId = doc.getId();
                    String eventTitle = (String) doc.getData().get("eventTitle");
                    String eventDate = (String) doc.getData().get("eventDate");
                    String eventStartTime = (String) doc.getData().get("eventStartTime");
                    String eventEndTime = (String) doc.getData().get("eventEndTime");
                    String eventAmPm = (String) doc.getData().get("eventAmPm");

                    eventList.add(new Event(eventTitle, "frfr", "grgrf", "fre", eventDate,
                            eventStartTime, eventEndTime, eventAmPm));
                }
                eventViewAdapter.notifyDataSetChanged(); // Notify the adapter to render new data
            }
        });
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

        listViewEvents.setOnItemClickListener((parent,view, position, id) -> {
            Intent annoucementIntent = new Intent(MainActivity.this, AnnoucementActivity.class);

            annoucementIntent.putExtra("Message", "hello world");
            startActivity(annoucementIntent);

        });
    }

}