package com.example.swiftcheckin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.ArchTaskExecutor;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Admin extends AppCompatActivity {
    ListView dataList;
    Button deleteButton;
    Button eventButton;
    Button profileButton;

    ArrayList<Profile> profileList;
    ArrayList<Event> eventList;
    private int selectedPosition = -1;

    final String TAG = "Sample";
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    String tab;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);
        deleteButton = findViewById(R.id.remove_tab_button);
        eventButton = findViewById(R.id.event_button);
        profileButton = findViewById(R.id.profile_button);
        dataList = findViewById(R.id.listView);
        db = FirebaseFirestore.getInstance();
        profileList = new ArrayList<>();
        eventList = new ArrayList<>();
        tab = "Event";
        ProfileArrayAdapter profileArrayAdapter = new ProfileArrayAdapter(this, profileList);
        EventArrayAdapter eventArrayAdapter = new EventArrayAdapter(this, eventList);
        displayEventsTab(eventArrayAdapter);

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayEventsTab(eventArrayAdapter);
            }
        });


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProfilesTab(profileArrayAdapter);
            }
        });


        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    if (tab == "Event") {
                        deleteEvent(eventArrayAdapter,"pass");

                    } else if (tab == "Profile") {
                        deleteProfile(profileArrayAdapter,eventArrayAdapter);
                    }

                }
            }
        });
    }

    private void displayEventsTab(EventArrayAdapter eventArrayAdapter) {

        tab = "Event";
        collectionReference = db.collection("events");
        dataList.setAdapter(eventArrayAdapter);
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                eventList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Event event = doc.toObject(Event.class);
                    eventList.add(event);
                }
                eventArrayAdapter.notifyDataSetChanged();
            }
        });

    }
    private void displayProfilesTab(ProfileArrayAdapter profileArrayAdapter) {
        tab = "Profile";
        collectionReference = db.collection("profiles");
        dataList.setAdapter(profileArrayAdapter);
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                profileList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Profile profile = doc.toObject(Profile.class);
                    profileList.add(profile);
                }
                profileArrayAdapter.notifyDataSetChanged();
            }
        });

    }
    private void deleteProfile(ProfileArrayAdapter profileArrayAdapter,EventArrayAdapter eventArrayAdapter){
        //delete not just profile but all events associated with that profile
        String nameToDelete = profileList.get(selectedPosition).getName(); // Assuming 'getName()' returns the name field in your Profile class
        collectionReference.whereEqualTo("name", nameToDelete)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                collectionReference.document(documentId)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "Data has been deleted successfully!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Data could not be deleted!" + e.toString());
                                            }
                                        });

                                profileList.remove(selectedPosition);
                                profileArrayAdapter.notifyDataSetChanged();
                                deleteEvent( eventArrayAdapter, documentId);
                                selectedPosition = -1;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private void deleteEvent(EventArrayAdapter eventArrayAdapter, String deviceId) {
        int iterations;
        Log.d(TAG, "In delete event ");
        tab = "Event";
        collectionReference = db.collection("events");
        // if device Id is "pass" do normal deletion, else for every event with that device ID delete it
            String nameToDelete = eventList.get(selectedPosition).getDeviceId();
            if (!"pass".equals(deviceId)){
                Log.d(TAG, "In this if statement!");
                nameToDelete = deviceId;
            }
            collectionReference.whereEqualTo("deviceId", nameToDelete)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String documentId = document.getId();
                                    collectionReference.document(documentId)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "Event data has been deleted successfully!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Event data could not be deleted! " + e.toString());
                                                }
                                            });

                                    eventList.remove(selectedPosition);
                                    eventArrayAdapter.notifyDataSetChanged();
                                    selectedPosition = -1;

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });


    }


}
