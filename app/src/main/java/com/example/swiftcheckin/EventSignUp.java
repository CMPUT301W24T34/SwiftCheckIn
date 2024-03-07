package com.example.swiftcheckin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EventSignUp {

    private FirebaseFirestore db;
    // Need to create a new collection for attendees attending events
    // Store eventIds as documents
    // Store attendee device ids as fields in that document to help with memory.

    public void addAttendeeToEvent(String eventId, String attendeeDeviceId){
        // Add the attendee to the firestore database.
        // How will they be able to access the things tho?
        db = FirebaseFirestore.getInstance();
        Event updated_event = createEvent(eventId);
        HashMap<String, String> data = new HashMap<>();
        data.put("attendee", attendeeDeviceId);

        CollectionReference eventAttendee = db.collection("eventsWithAttendees-test");
        int currentAttendees = updated_event.getCurrentAttendees();
        DocumentReference eventDoc = eventAttendee.document(eventId);
//        if (updated_event.getMaxAttendees() == -1 || currentAttendees < updated_event.getMaxAttendees()) {

        eventDoc.set(data)
                .addOnSuccessListener(aVoid -> {
                    // On success
                    System.out.println("Attendee added to event successfully.");
//                        updated_event.incrementCurrentAttendees();
                })
                .addOnFailureListener(e -> {
                    // On failure
                    System.out.println("Error adding attendee to event: " + e.getMessage());
                });
        updateEventInFirebase(updated_event);

        // Need to update event
    }

    public Event createEvent(String eventId){
        Event updated_event;
        final String[] eventTitle = new String[1];
        final String[] eventStartTime = new String[1];
        final String[] eventStartDate = new String[1];
        final String[] eventPosterURL = new String[1];
//        final String[] eventMaxAttendees = new String[1];
        final String[] eventLocation = new String[1];
        final String[] eventEndTime = new String[1];
        final String[] eventEndDate = new String[1];
        final String[] eventDescription = new String[1];
        final String[] eventDeviceId = new String[1];

        db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot event = task.getResult();
                            if (event.exists()){
                                eventTitle[0] = (String) event.get("eventTitle");
                                eventStartTime[0] = (String) event.get("eventStartTime");
                                eventStartDate[0] = (String) event.get("eventStartDate");
                                eventPosterURL[0] = (String) event.get("eventPosterURL");
//                                eventMaxAttendees[0] = (String) event.get("eventMaxAttendees");
                                eventLocation[0] = (String) event.get("eventLocation");
                                eventEndTime[0] = (String) event.get("eventEndTime");
                                eventEndDate[0] = (String) event.get("eventEndDate");
                                eventDescription[0] = (String) event.get("eventDescription");
                                eventDeviceId[0] = (String) event.get("deviceId");

                            }
                        }
                    }
                });
        updated_event = new Event(eventTitle[0], eventDescription[0], eventLocation[0], eventDeviceId[0], eventPosterURL[0], eventStartDate[0], eventEndDate[0], eventStartTime[0], eventEndTime[0]);
        return updated_event;

    }

    public void updateEventInFirebase(Event event){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");
        DocumentReference oneEvent = eventsRef.document(event.getDeviceId() + event.getEventTitle());

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

        oneEvent
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

    // Need to handle querying the documents.
}
