package com.example.swiftcheckin.organizer;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is meant to help the attendee sign up to an event and have their data be stored in Firebase
 * NOT a class that covers all forms sign ups and records, but adds attendees to events for now.
 */
public class EventSignUp {

    private FirebaseFirestore db;

    // Store eventIds as documents
    // Store attendee device ids as fields in that document to help with memory.

    /**
     * This methods helps associate an attendee to an event.
     *
     * @param eventId          - The Id of the event the attendee wants to sign up for.
     * @param attendeeDeviceId - The id of the attendee who wants to signs up
     */
    // Instead of the eventId, why don't I pass an event entirely?
    public void addAttendeeToEvent(String eventId, String attendeeDeviceId, String eventMaxAttendees, String eventCurrentAttendees) {
        int maxAttendees = Integer.parseInt(eventMaxAttendees);
        int currentAttendees = Integer.parseInt(eventCurrentAttendees);
        db = FirebaseFirestore.getInstance();
        DocumentReference eventDoc = db.collection("events").document(eventId);

        // Check if the event exists in the "events" collection
        eventDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Event exists in the "events" collection
                    System.out.println("Event with ID " + eventId + " exists in the events collection.");
                    // Perform additional operations here if needed
                } else {
                    // Event does not exist in the "events" collection
                    System.out.println("Event with ID " + eventId + " does not exist in the events collection.");
                    // Stop further processing here if needed
                }
            } else {
                System.out.println("Error querying event document: " + task.getException());
            }

            // Now, check if the event exists in the "eventsWithAttendees" collection
            checkEventWithAttendees(eventId, attendeeDeviceId, eventMaxAttendees, eventCurrentAttendees);
        });
    }
    public void checkEventWithAttendees(String eventId, String attendeeDeviceId, String eventMaxAttendees, String eventCurrentAttendees) {
        int maxAttendees = Integer.parseInt(eventMaxAttendees);
        int currentAttendees = Integer.parseInt(eventCurrentAttendees);
        db = FirebaseFirestore.getInstance();



        DocumentReference eventDoc = db.collection("eventsWithAttendees").document(eventId);


        eventDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // Check if task is successful
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    HashMap<String, String> data = new HashMap<>();

                    if (document.exists()){
                        if (!document.contains(attendeeDeviceId)){
                           updateEvent(eventId, maxAttendees, currentAttendees, attendeeDeviceId, eventDoc, eventCurrentAttendees);
                        }
                    }
                    else {
                        updateEvent(eventId, maxAttendees, currentAttendees, attendeeDeviceId, eventDoc, eventCurrentAttendees);
                    }
                }
                // If currentAttendees is 0, add (Done)
                // Check if deviceId is in the firebase
                // If it is, ignore it. Otherwise, add the deviceId

            }
        });

    }


    public void updateEvent(String eventId, int maxAttendees, int currentAttendees, String attendeeDeviceId, DocumentReference eventDoc, String eventCurrentAttendees){
        if (currentAttendees != maxAttendees) {
            HashMap<String, String> data = new HashMap<>();
            data.put(attendeeDeviceId, attendeeDeviceId);

            // Citation: OpenAI, 03-07-2024,  ChatGPT, updating data in documents without resetting the entire document.
        /*
        I wanted to know how to update fields without overwriting previous data and I tried using .update(), but that did not work
        I asked ChatGPT how to update and it provided this code,
        eventDoc.set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Attendee added to event successfully.");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error adding attendee to event: " + e.getMessage());
                });
         Where SetOptions.merge() helped me avoid overwriting any information.
         */
            eventDoc.set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Attendee added to event successfully.");  // Checks if attendee is added here
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("Error adding attendee to event: " + e.getMessage());  // In case attendee is not added.
                    });
            currentAttendees += 1;

            eventCurrentAttendees = Integer.toString(currentAttendees);
            DocumentReference updateEventDoc = db.collection("events").document(eventId);
            HashMap<String, String> data2 = new HashMap<>();
            data2.put("eventCurrentAttendees", eventCurrentAttendees);
            updateEventDoc.set(data2, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Event updated successfully");  // Checks if attendee is added here
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("Error updating event: " + e.getMessage());  // In case attendee is not added.
                    });
        }

    }



    public void addCheckedIn(String eventId, String attendeeDeviceId){
        db = FirebaseFirestore.getInstance();
        DocumentReference checkedInDoc = db.collection("checkedIn").document(eventId);

        checkedInDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    HashMap<String, String> data = new HashMap<>();

                    if (document.exists()){
                        updateCheckedIn(attendeeDeviceId, checkedInDoc);

                    }
                    else {
                        updateCheckedIn(attendeeDeviceId, checkedInDoc);
                    }
                }

            }
        });


    }


    public void updateCheckedIn(String attendeeDeviceId, DocumentReference checkedInDoc){
        HashMap<String, String> data = new HashMap<>();
        checkedInDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()){
                        if (document.contains(attendeeDeviceId)){
                            String checkInCountStr = (String) document.get(attendeeDeviceId);
                            if (checkInCountStr != null) {
                                int checkInCount = Integer.parseInt(checkInCountStr) + 1;
                                checkInCountStr = Integer.toString(checkInCount);
                                // Try document .set
                                data.put(attendeeDeviceId, checkInCountStr);
                            }
                        } else {
                            data.put(attendeeDeviceId, "1");
                        }
                    } else {
                        data.put(attendeeDeviceId, "1");
                    }
                    checkedInDoc.set(data, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                System.out.println("Attendee added to event successfully.");  // Checks if attendee is added here
                            })
                            .addOnFailureListener(e -> {
                                System.out.println("Error adding attendee to event: " + e.getMessage());  // In case attendee is not added.
                            });

                }

            }
        });
        // Get check in count, convert it to an integer, increment, set the new check in count
        // Update on firebase.

    }
}