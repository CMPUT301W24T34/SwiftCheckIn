package com.example.swiftcheckin.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;


import com.example.swiftcheckin.organizer.Event;

import com.example.swiftcheckin.attendee.Profile;
import com.example.swiftcheckin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
        displayEventsTab(eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);

        //Citation: For the following code idea to use the search bar and filter searches, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
         public boolean onQueryTextSubmit(String query) {
               return false;
           }

           @Override
           public boolean onQueryTextChange(String newText) {
               if (tab.equals("Event")) {

                   filterEventList(newText, eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);
               } if (tab.equals("Profile")) {

                   filterProfileList(newText, profileArrayAdapter,eventArrayAdapter,imageArrayAdapter);
                }
               if (tab.equals("Image")) {

                   filterImageList(newText, imageArrayAdapter,eventArrayAdapter,profileArrayAdapter);
               }
               return true;
            }
        });


        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayEventsTab(eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);
            }
        });


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProfilesTab(profileArrayAdapter,eventArrayAdapter,imageArrayAdapter);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {displayImagesTab(imageArrayAdapter,eventArrayAdapter,profileArrayAdapter);
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
                        deleteEvent(eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);

                    } if (tab == "Profile") {
                        deleteProfile(profileArrayAdapter,eventArrayAdapter,imageArrayAdapter);
                    }
                    if (tab == "Image") {
                        deleteImage(imageArrayAdapter,eventArrayAdapter,profileArrayAdapter);
                    }

                }
            }
        });
    }

    /**
     * This displays the events tab
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */

    private void displayEventsTab(AdminEventArrayAdapter eventArrayAdapter,ImageArrayAdapter imageArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
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
                profileArrayAdapter.notifyDataSetChanged();
                imageArrayAdapter.notifyDataSetChanged();
            }
        });

    }
    /**
     * This displays the profiles tab
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */

    private void displayProfilesTab(ProfileArrayAdapter profileArrayAdapter,AdminEventArrayAdapter eventArrayAdapter, ImageArrayAdapter imageArrayAdapter) {
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
                eventArrayAdapter.notifyDataSetChanged();
                imageArrayAdapter.notifyDataSetChanged();
            }
        });

    }
    /**
     * This displays the images tab
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */

    private void displayImagesTab(ImageArrayAdapter imageArrayAdapter,AdminEventArrayAdapter eventArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        tab = "Image";
        collectionReference = db.collection("events");
        dataList.setAdapter(imageArrayAdapter);

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                imageList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Event event = doc.toObject(Event.class);
                    // Set the event's image URL to be the eventPosterURL from Firebase
                    event.setEventImageUrl(doc.getString("eventPosterURL"));
                    imageList.add(event);
                }
                imageArrayAdapter.notifyDataSetChanged();
                eventArrayAdapter.notifyDataSetChanged();
                profileArrayAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * This deletes the profile and all associated events
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    private void deleteProfile(ProfileArrayAdapter profileArrayAdapter,AdminEventArrayAdapter eventArrayAdapter,ImageArrayAdapter imageArrayAdapter){
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
                                eventArrayAdapter.notifyDataSetChanged();
                                imageArrayAdapter.notifyDataSetChanged();
                                deleteMultiEvent( eventArrayAdapter, documentId,imageArrayAdapter,profileArrayAdapter);
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
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    private void deleteMultiEvent(AdminEventArrayAdapter eventArrayAdapter, String deviceId,ImageArrayAdapter imageArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
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
                                    imageArrayAdapter.notifyDataSetChanged();
                                    profileArrayAdapter.notifyDataSetChanged();
                                    selectedPosition = -1;

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });


    }
    /**
     * This deletes the events
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    private void deleteEvent(AdminEventArrayAdapter eventArrayAdapter,ImageArrayAdapter imageArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
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
                                imageArrayAdapter.notifyDataSetChanged();
                                profileArrayAdapter.notifyDataSetChanged();
                                selectedPosition = -1;

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    /**
     * This deletes the images
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    private void deleteImage(ImageArrayAdapter imageArrayAdapter,AdminEventArrayAdapter eventArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        tab = "Image";
        collectionReference = db.collection("events");
        String nameToUpdate = "filler";

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
                                //Citation: For the following code idea, OpenAI, 2024, Licensing: Creative Commons, ChatGPT, Prompt: How to set something to null in firebase
                                collectionReference.document(documentId)
                                        .update("eventPosterURL", null)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "Event poster URL has been set to null successfully!");
                                                // Notify adapter of the change
                                                imageArrayAdapter.notifyDataSetChanged();
                                                profileArrayAdapter.notifyDataSetChanged();
                                                eventArrayAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Failed to set event poster URL to null: " + e.toString());
                                            }
                                        });
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
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    //Citation: For the following code idea to use the search bar and filter searches, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
    private void filterEventList(String query, AdminEventArrayAdapter eventArrayAdapter,ImageArrayAdapter imageArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        if (query == null || query.isEmpty()) {
            displayEventsTab(eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);
            return; // Exit the method early
        }

        CollectionReference profileCollectionRef = db.collection("events");

        //Citation: For the following code line for lowercase, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to convert to lowercase
        String queryLowerCase = query.toLowerCase();
        List<Event> matchingEvents = new ArrayList<>();

        for (Event event : eventList) {
            if (event != null && event.getEventTitle() != null) {
                if (event.getEventTitle().toLowerCase().contains(queryLowerCase)) {
                    matchingEvents.add(event);
                }
            }
        }

        eventList.clear();
        eventArrayAdapter.notifyDataSetChanged();

        // Query the profiles from Firestore based on the matching profiles
        for (Event matchingEvent : matchingEvents) {
            profileCollectionRef.whereEqualTo("eventTitle", matchingEvent.getEventTitle())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Event event = document.toObject(Event.class);
                                    if (!eventList.contains(event)) {
                                        eventList.add(event);
                                    }
                                }
                                eventArrayAdapter.notifyDataSetChanged();
                                imageArrayAdapter.notifyDataSetChanged();
                                profileArrayAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
    /**
     * This filters through the events using search
     *@param query the searched word - String
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    //Citation: For the following code idea to use the search bar and filter searches, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
    private void filterImageList(String query, ImageArrayAdapter imageArrayAdapter, AdminEventArrayAdapter eventArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        if (query == null || query.isEmpty()) {
            displayImagesTab(imageArrayAdapter,eventArrayAdapter,profileArrayAdapter);
            return; // Exit the method early
        }

        CollectionReference profileCollectionRef = db.collection("events");

        ///Citation: For the following code line for lowercase, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to convert to lowercase
        String queryLowerCase = query.toLowerCase();

        List<Event> matchingImages = new ArrayList<>();

        for (Event event : imageList) {
            if (event != null && event.getEventTitle() != null) {
                if (event.getEventTitle().toLowerCase().contains(queryLowerCase)) {
                    matchingImages.add(event);
                }
            }
        }

        imageList.clear();
        imageArrayAdapter.notifyDataSetChanged();

        for (Event matchingEvent : matchingImages) {
            profileCollectionRef.whereEqualTo("eventTitle", matchingEvent.getEventTitle())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Event event = document.toObject(Event.class);
                                    // Add the profile if not already in the list
                                    if (!imageList.contains(event)) {
                                        imageList.add(event);
                                    }
                                }
                                imageArrayAdapter.notifyDataSetChanged();
                                eventArrayAdapter.notifyDataSetChanged();
                                profileArrayAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }





    /**
     * This filters through the profiles using search
     * @param query the searched word - String
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    //Citation: For the following code idea to use the search bar and filter searches, OpenAI, 2024, Licensing: Creative Commons, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
    private void filterProfileList(String query, ProfileArrayAdapter profileArrayAdapter, AdminEventArrayAdapter eventArrayAdapter, ImageArrayAdapter imageArrayAdapter) {
        if (query == null || query.isEmpty()) {
            displayProfilesTab(profileArrayAdapter, eventArrayAdapter, imageArrayAdapter);
            return; // Exit the method early
        }

        CollectionReference profileCollectionRef = db.collection("profiles");

        //Citation: For the following code line for lowercase, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to convert to lowercase
        String queryLowerCase = query.toLowerCase();

        List<Profile> matchingProfiles = new ArrayList<>();

        for (Profile profile : profileList) {
            if (profile != null && profile.getName() != null) {
                if (profile.getName().toLowerCase().contains(queryLowerCase)) {
                    matchingProfiles.add(profile);
                }
            }
        }

        profileList.clear();
        profileArrayAdapter.notifyDataSetChanged();

        for (Profile matchingProfile : matchingProfiles) {
            profileCollectionRef.whereEqualTo("name", matchingProfile.getName())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Profile profile = document.toObject(Profile.class);
                                    if (!profileList.contains(profile)) {
                                        profileList.add(profile);
                                    }
                                }
                                // Notify adapter after querying all matching profiles
                                profileArrayAdapter.notifyDataSetChanged();
                                imageArrayAdapter.notifyDataSetChanged();
                                eventArrayAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }




}




