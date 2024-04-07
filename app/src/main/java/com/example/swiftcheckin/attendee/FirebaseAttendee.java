package com.example.swiftcheckin.attendee;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.swiftcheckin.organizer.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class FirebaseAttendee {
    private FirebaseFirestore db;
    private CollectionReference profilesCollectionRef;
    private String deviceId;

    public FirebaseAttendee() {
        db = FirebaseFirestore.getInstance();
        profilesCollectionRef = db.collection("profiles");

    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public CollectionReference getProfilesCollectionRef() {
        return profilesCollectionRef;
    }


    /**
     * update the firebase collection with the location data
     * @param deviceId - deviceid of users phone
     * @param data - the data to be updated to firebase
     */
    public void updateLocationInfo(String deviceId, HashMap<String, Object> data){
        db.collection("profiles").document(deviceId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Location data has been added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Location data could not be added");
                    }
                });
    }

    /**
     * Saves profile data to firebase
     * @param deviceId id of users device
     * @param data data to be updated
     */
    public void saveProfileToFirebase(String deviceId, HashMap<String, String> data) {
        profilesCollectionRef.document(deviceId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "User data has been added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "User data could not be added");
                    }
                });
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
    public void saveSignUpData(String deviceId, String eventId, Context context) {
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
                            addSignUpData(data,ref, context);
                        }
                        // they've already signed up for this event
                        else{
                            Toast.makeText(context, "You are already signed up for this event", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // no list yet
                    else {
                        List<String> eventIds = new ArrayList<>();
                        eventIds.add(eventId);
                        data.put("eventIds", eventIds);
                        addSignUpData(data, ref, context);
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
    private void addSignUpData(Map<String, Object> data, DocumentReference ref, Context context){
        ref.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Signed up!", Toast.LENGTH_SHORT).show();
                        Log.d(ContentValues.TAG, "Event ID added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(ContentValues.TAG, "Error adding event ID", e);
                        Toast.makeText(context, "Could not sign up", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    interface EventListCallback
    {
        public void onDataFetched(ArrayList<Event> eventList);
    }

    public void getEventList(ArrayList<Event> eventList, EventListCallback callback)
    {
        CollectionReference eventCol = db.collection("events");
        eventCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                eventList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    String eventTitle = (String) doc.getData().get("eventTitle");
                    String eventDescription = (String) doc.getData().get("eventDescription");
                    String eventLocation = (String) doc.getData().get("eventLocation");
                    String deviceId = (String) doc.getData().get("deviceId");
                    String eventImageUrl = (String) doc.getData().get("eventPosterURL");
                    String eventStartDate = (String) doc.getData().get("eventStartDate");
                    String eventStartTime = (String) doc.getData().get("eventStartTime");
                    String eventEndDate = (String) doc.getData().get("eventEndDate");
                    String eventEndTime = (String) doc.getData().get("eventEndTime");
                    String eventMaxAttendees = (String) doc.getData().get("eventMaxAttendees");
                    String eventCurrentAttendees = (String) doc.getData().get("eventCurrentAttendees");

                    com.example.swiftcheckin.organizer.Event event;

                    if (eventMaxAttendees.equals("-1")) {
                        event = new com.example.swiftcheckin.organizer.Event(eventTitle, eventDescription, eventLocation, deviceId,
                                eventImageUrl, eventStartDate, eventEndDate, eventStartTime, eventEndTime);

                    } else {
                        event = new com.example.swiftcheckin.organizer.Event(eventTitle, eventDescription, eventLocation, deviceId,
                                eventImageUrl, eventMaxAttendees, eventStartDate, eventEndDate, eventStartTime, eventEndTime);
                    }

                    if (eventCurrentAttendees != null) {
                        event.setCurrentAttendees(Integer.parseInt(eventCurrentAttendees));
                    } else {
                        event.setCurrentAttendees(0);
                    }


                    eventList.add(event);

                }
                callback.onDataFetched(eventList);
            }
        });
    }

    /**
     * Fetches the data of events that the user has signed up for from Firestore.
     */
    public void getMyEventIds(ArrayList<Event> myEventList, Context context,
                              EventListCallback callback) {

        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference deviceRef = db.collection("SignedUpEvents").document(deviceId);
        deviceRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                List<String> eventIds = new ArrayList<>();
                if (document.exists()) {
                    eventIds = (List<String>) document.get("eventIds");
                }
//                ArrayList<Event> myEventList = new ArrayList<>();
                fetchMyEventsData(eventIds, myEventList, callback);

            } else {
                Log.d("getData", "Error getting documents: ", task.getException());
            }
        });
    }


    /**
     * Fetches event data from Firestore based on event IDs.
     *
     * @param eventIds List of event IDs
     */
//    private void fetchMyEventsData(List<String> eventIds, ArrayList<Event> myEventList, EventListCallback callback) {
//        CollectionReference eventCol = db.collection("events");
//        myEventList.clear(); // Clear the old list
//
//        if (eventIds.isEmpty()) {
//            callback.onDataFetched(myEventList); // Handle case with no event IDs immediately
//            return;
//        }
//
//        for (String eventId : eventIds) {
//            eventCol.document(eventId).get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//
//                        String eventTitle = document.getString("eventTitle");
//                        String eventDescription = document.getString("eventDescription");
//                        String eventLocation = document.getString("eventLocation");
//                        String deviceId = document.getString("deviceId");
//                        String eventImageUrl = document.getString("eventPosterURL");
//                        String eventStartDate = document.getString("eventStartDate");
//                        String eventStartTime = document.getString("eventStartTime");
//                        String eventEndDate = document.getString("eventEndDate");
//                        String eventEndTime = document.getString("eventEndTime");
//                        String eventMaxAttendees = document.getString("eventMaxAttendees");
//                        String eventCurrentAttendees = document.getString("eventCurrentAttendees");
//
//                        Event event;
//
//                        if (eventMaxAttendees.equals("-1")) {
//                            event = new Event(eventTitle, eventDescription, eventLocation, deviceId,
//                                    eventImageUrl, eventStartDate, eventEndDate, eventStartTime, eventEndTime);
//
//                        } else {
//                            event = new Event(eventTitle, eventDescription, eventLocation, deviceId,
//                                    eventImageUrl, eventMaxAttendees, eventStartDate, eventEndDate, eventStartTime, eventEndTime);
//                        }
//
//                        if (eventCurrentAttendees != null) {
//                            event.setCurrentAttendees(Integer.parseInt(eventCurrentAttendees));
//                        } else {
//                            event.setCurrentAttendees(0);
//                        }
//
//                        myEventList.add(event);
//                    } else {
//                        Log.d("fetchEventsData", "No such document");
//                    }
//                } else {
//                    Log.d("fetchEventsData", "get failed with ", task.getException());
//                }
//            });
//
//        }
//        callback.onDataFetched(myEventList);
//
//    }

    // Citation: OpenAI, 04-06-2024,  ChatGPT, Had some synchronicity issues with the above
    // commented function

        /*
            Asked Gpt to help fix the issues, cuz I'm getting myEvents list data on the app
         */
    private void fetchMyEventsData(List<String> eventIds, ArrayList<Event> myEventList, EventListCallback callback) {
        CollectionReference eventCol = db.collection("events");
        myEventList.clear(); // Clear the old list

        if (eventIds.isEmpty()) {
            callback.onDataFetched(myEventList); // If no event IDs, callback immediately
            return;
        }

        final int[] remainingFetches = {eventIds.size()}; // Track remaining fetches

        for (String eventId : eventIds) {
            eventCol.document(eventId).get().addOnCompleteListener(task -> {
                synchronized (remainingFetches) {
                    remainingFetches[0]--;
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Extract event data
                            String eventTitle = document.getString("eventTitle");
                            String eventDescription = document.getString("eventDescription");
                            String eventLocation = document.getString("eventLocation");
                            String deviceId = document.getString("deviceId");
                            String eventImageUrl = document.getString("eventPosterURL");
                            String eventStartDate = document.getString("eventStartDate");
                            String eventStartTime = document.getString("eventStartTime");
                            String eventEndDate = document.getString("eventEndDate");
                            String eventEndTime = document.getString("eventEndTime");
                            String eventMaxAttendees = document.getString("eventMaxAttendees");
                            String eventCurrentAttendees = document.getString("eventCurrentAttendees");

                            // Create the event object
                            Event event = new Event(eventTitle, eventDescription, eventLocation, deviceId, eventImageUrl, eventStartDate, eventEndDate, eventStartTime, eventEndTime);
                            if (eventMaxAttendees != null && !eventMaxAttendees.equals("-1")) {
                                event.setMaxAttendees(Integer.parseInt(eventMaxAttendees));
                            }
                            if (eventCurrentAttendees != null) {
                                event.setCurrentAttendees(Integer.parseInt(eventCurrentAttendees));
                            } else {
                                event.setCurrentAttendees(0);
                            }

                            // Add event to list
                            myEventList.add(event);
                        } else {
                            Log.d("fetchEventsData", "No such document");
                        }
                    } else {
                        Log.d("fetchEventsData", "get failed with ", task.getException());
                    }

                    if (remainingFetches[0] == 0) { // All fetches completed
                        callback.onDataFetched(myEventList);
                    }
                }
            });
        }
    }
}