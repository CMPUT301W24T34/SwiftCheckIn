package com.example.swiftcheckin.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.swiftcheckin.Event;
import com.example.swiftcheckin.attendee.Profile;
import com.example.swiftcheckin.R;
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

/**
 * This is the administration activity
 */
public class AdminActivity extends AppCompatActivity {
    ListView dataList;
    Button deleteButton;
    Button eventButton;
    Button profileButton;
    Button imageButton;
    ArrayList<Profile> profileList;
    ArrayList<Event> eventList;
    ArrayList<Event> imageList;
    private int selectedPosition = -1;

    final String TAG = "Sample";
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    String tab;
    SearchView searchView;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        deleteButton = findViewById(R.id.remove_tab_button);
        eventButton = findViewById(R.id.event_button);
        profileButton = findViewById(R.id.profile_button);
        imageButton = findViewById(R.id.images_button);
        dataList = findViewById(R.id.listView);
        db = FirebaseFirestore.getInstance();
        profileList = new ArrayList<>();
        eventList = new ArrayList<>();
        imageList = new ArrayList<>();
        tab = "Event";
        searchView = findViewById(R.id.searchView);
        ProfileArrayAdapter profileArrayAdapter = new ProfileArrayAdapter(this, profileList);
        AdminEventArrayAdapter eventArrayAdapter = new AdminEventArrayAdapter(this, eventList);
        ImageArrayAdapter imageArrayAdapter = new ImageArrayAdapter(this, imageList);
        //Make the default view the events tab
        displayEventsTab(eventArrayAdapter);

        //Citation: For the following code idea to use the search bar and filter searches, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
         public boolean onQueryTextSubmit(String query) {
             if (tab.equals("Event")) {

                   filterEventList(query, eventArrayAdapter);
               } else if (tab.equals("Profile")) {

                   filterProfileList(query, profileArrayAdapter);
               }
               return true;
           }

           @Override
           public boolean onQueryTextChange(String newText) {
               if (tab.equals("Event")) {

                   filterEventList(newText, eventArrayAdapter);
               } else if (tab.equals("Profile")) {

                   filterProfileList(newText, profileArrayAdapter);
                }
               return true;
            }
        });


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

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {displayImagesTab(imageArrayAdapter);
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
                        deleteEvent(eventArrayAdapter);

                    } if (tab == "Profile") {
                        deleteProfile(profileArrayAdapter,eventArrayAdapter);
                    }
                    if (tab == "Image") {
                        deleteImage(imageArrayAdapter);
                    }

                }
            }
        });
    }

    /**
     * This displays the events tab
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     */

    private void displayEventsTab(AdminEventArrayAdapter eventArrayAdapter) {
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
    /**
     * This displays the profiles tab
     *  @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
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

    private void displayImagesTab(ImageArrayAdapter imageArrayAdapter) {
        tab = "Image";
        collectionReference = db.collection("events");
        dataList.setAdapter(imageArrayAdapter);
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                imageList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Event image = doc.toObject(Event.class);
                    imageList.add(image);
                }
                imageArrayAdapter.notifyDataSetChanged();
            }
        });

    }


    /**
     * This deletes the profile and all associated events
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     */
    private void deleteProfile(ProfileArrayAdapter profileArrayAdapter,AdminEventArrayAdapter eventArrayAdapter){
        //delete not just profile but all events associated with that profile
        String nameToDelete = profileList.get(selectedPosition).getName();
        collectionReference.whereEqualTo("name", nameToDelete)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Citation: For the following code idea, OpenAI, 2024, Licensing: Creative Commons, ChatGPT, Prompt: How to delete all items with a certain device ID
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
                                deleteMultiEvent( eventArrayAdapter, documentId);
                                selectedPosition = -1;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    /**
     * This deletes multiple events`
     * @param deviceId the id of the device - String
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     */
    private void deleteMultiEvent(AdminEventArrayAdapter eventArrayAdapter, String deviceId) {
        tab = "Event";
        collectionReference = db.collection("events");
        String nameToDelete = "filler";

        if (selectedPosition >= 0 && selectedPosition < eventList.size()) {
            nameToDelete = eventList.get(selectedPosition).getDeviceId();
        }
        //Citation: For the following code idea, OpenAI, 2024, ChatGPT, Licensing: Creative Commons, Prompt: How to check if the device Id is not "pass"
            if (!"pass".equals(deviceId)){
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

                                    //eventList.remove(selectedPosition);
                                    eventArrayAdapter.notifyDataSetChanged();
                                    selectedPosition = -1;

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });


    }
    /**
     * This deletes the events`
      * @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     */
    private void deleteEvent(AdminEventArrayAdapter eventArrayAdapter) {
        tab = "Event";
        collectionReference = db.collection("events");
        String nameToDelete = "filler";

        if (selectedPosition >= 0 && selectedPosition < eventList.size()) {
            nameToDelete = eventList.get(selectedPosition).getEventTitle();
        }
        //Citation: For the following code idea, OpenAI, 2024, ChatGPT, Licensing: Creative Commons, Prompt: How to check if the device Id is not "pass"
        collectionReference.whereEqualTo("eventTitle", nameToDelete)
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

                                //eventList.remove(selectedPosition);
                                eventArrayAdapter.notifyDataSetChanged();
                                selectedPosition = -1;

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    /**
     * This deletes the events`
     * @param imageArrayAdapter the image array adapter - AdminEventArrayAdapter
     */
    private void deleteImage(ImageArrayAdapter imageArrayAdapter) {
        tab = "Image";
        collectionReference = db.collection("events");
        String nameToUpdate = "filler";
        String defaultPosterUrl = "drawable/event_poster"; // Replace with your default poster image URL

        if (selectedPosition >= 0 && selectedPosition < eventList.size()) {
            nameToUpdate = eventList.get(selectedPosition).getEventTitle();
        }

        collectionReference.whereEqualTo("eventTitle", nameToUpdate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                collectionReference.document(documentId)
                                        .update("eventPosterURL", defaultPosterUrl)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "Event poster URL has been updated successfully!");
                                                // Notify adapter of the change
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Failed to update event poster URL: " + e.toString());
                                            }
                                        });
                                imageArrayAdapter.notifyDataSetChanged();
                                selectedPosition = -1;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * This filters through the events using search
     *@param query the searched word - String
      *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     */
    //Citation: For the following code idea to use the search bar and filter searches, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
    private void filterEventList(String query, AdminEventArrayAdapter eventArrayAdapter) {
        CollectionReference eventCollectionRef = db.collection("events");

        eventCollectionRef.whereEqualTo("eventTitle", query)  // Adjust "eventName" to the actual field you want to search
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            eventList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Event event = document.toObject(Event.class);
                                eventList.add(event);
                            }
                            eventArrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    /**
     * This filters through the profiles using search
     * @param query the searched word - String
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    //Citation: For the following code idea to use the search bar and filter searches, OpenAI, 2024, Licensing: Creative Commons, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
    private void filterProfileList(String query, ProfileArrayAdapter profileArrayAdapter) {
        CollectionReference profileCollectionRef = db.collection("profiles");

        profileCollectionRef.whereEqualTo("name", query)  // Adjust "profileName" to the actual field you want to search
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            profileList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Profile profile = document.toObject(Profile.class);
                                profileList.add(profile);
                            }
                            profileArrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }





}




