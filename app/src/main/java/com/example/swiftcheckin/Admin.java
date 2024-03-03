package com.example.swiftcheckin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.ArchTaskExecutor;

import android.os.Bundle;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

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
        dataList.setAdapter(eventArrayAdapter);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = "Event";
                collectionReference = db.collection("Events");
                dataList.setAdapter(eventArrayAdapter);
                refreshListView( profileArrayAdapter, eventArrayAdapter,tab);

            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab = "Profile";
                collectionReference = db.collection("profiles");
                dataList.setAdapter(profileArrayAdapter);
                refreshListView(profileArrayAdapter, eventArrayAdapter,tab);
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
                    if  (tab == "Event") {

                        }

                    else if (tab == "Profile") {

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
                                                selectedPosition = -1;
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }

                }
            }
        });

    }
    private void refreshListView( ProfileArrayAdapter profileArrayAdapter, EventArrayAdapter eventArrayAdapter, String tab) {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (tab == "Event") {
                    eventList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Event event = doc.toObject(Event.class);
                        eventList.add(event);
                    }
                    eventArrayAdapter.notifyDataSetChanged();
                } else if (tab == "Profile") {
                    profileList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Profile profile = doc.toObject(Profile.class);
                        profileList.add(profile);
                    }
                    profileArrayAdapter.notifyDataSetChanged();
                }
            }
        });

    }


}
