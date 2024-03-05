package com.example.swiftcheckin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;

public class OrganizerActivity extends AppCompatActivity implements AddEventFragment.AddEventDialogListener{
    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;

    private FirebaseFirestore db;

    Button addEventButton;

//    FirebaseStorage storage = FirebaseStorage.getInstance();
//
//


    public void addEvent(Event event) {
        eventAdapter.add(event);
        eventAdapter.notifyDataSetChanged();
        saveData(event);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        db = FirebaseFirestore.getInstance();

        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//        DocumentReference deviceRef = db.collection("events").document(deviceId);
//        DocumentReference deviceIdRef = db.collection("deviceIds").document(deviceId);
        getData();


        eventList = findViewById(R.id.event_list);
        dataList = new ArrayList<>();
        eventAdapter = new EventArrayAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);
        FloatingActionButton backButtonOrg = findViewById(R.id.back_button_org);

        addEventButton = findViewById(R.id.add_event_button);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AddEventFragment(deviceId).show(getSupportFragmentManager(), "Add Event");
            }
        });

        backButtonOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData(){
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        CollectionReference eventCol = db.collection("events");
//        DocumentReference deviceRef = eventCol.document(deviceId);
//        CollectionReference deviceRef2 = deviceRef.collection(deviceId);

        eventCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {

                // Clear the old list
                dataList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    String eventTitleFrame = doc.getId();
                    if (eventTitleFrame.contains(deviceId)) {
                        String eventTitle = (String) doc.getData().get("eventTitle");
                        String eventLocation = (String) doc.getData().get("eventLocation");
                        String eventDescription = (String) doc.getData().get("eventDescription");
                        String deviceID = (String) doc.getData().get("deviceId");
                        String eventImageURL = (String) doc.getData().get("eventPosterURL");
                        Event event = new Event(eventTitle, eventDescription, eventLocation, eventImageURL, deviceID);
                        dataList.add(event);
//                        Glide.with(OrganizerActivity.this)
//                                .load(eventImageURL)
//                                .into(eventPoster);
                    }// Adding event details from FireStore

                }
                eventAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud


            }
        });
    }
    private void saveData(Event event){
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        // Ensure we have 3 columns in firestore for simple reference.
        DocumentReference deviceRef = db.collection("events").document(deviceId + event.getEventTitle());
//        CollectionReference deviceRef2 = deviceRef.collection(deviceId);
//        StorageReference eventPosterRef = storage.getReference().child("eventPosterImages");



        HashMap<String, String> data = new HashMap<>();
        data.put("eventTitle", event.getEventTitle());
        data.put("eventLocation", event.getLocation());
        data.put("deviceId", event.getDeviceId());
        data.put("eventDescription", event.getDescription());
        data.put("eventPosterURL", event.getEventImageUrl());

        deviceRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Event Added Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Event could not be added.");
                    }
                });
    }


//    private void uploadPoster(Event event){
//        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//        StorageReference eventPosterRef = storage.getReference().child("eventPosterImages/" + deviceId + event.getEventTitle() + "_poster.jpg");
//        eventPosterRef.putFile(event.getEventPoster())
//                .addOnSuccessListener(taskSnapshot -> {
//                    // Image uploaded successfully
//                    Toast.makeText(OrganizerActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
//
//                    // Get the URL of the uploaded image
//                    eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                        // Handle the image URL, if needed
//                        String imageUrl = uri.toString();
//                        Log.d(TAG, "Image URL: " + imageUrl);
//                    }).addOnFailureListener(exception -> {
//                        // Handle any errors getting the download URL
//                        Log.e(TAG, "Failed to get image URL", exception);
//                    });
//
//                })
//                .addOnFailureListener(exception -> {
//                    // Handle unsuccessful uploads
//                    Toast.makeText(OrganizerActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "Failed to upload image", exception);
//                });
//
//
//
//    }
}