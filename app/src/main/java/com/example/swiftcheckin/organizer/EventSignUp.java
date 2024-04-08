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
     * This methods checks events to check an event exists before sign up
     *
     * @param eventId           The ID of the event.
     * @param eventMaxAttendees      The maximum number of attendees allowed for the event.
     * @param eventCurrentAttendees  The current number of attendees registered for the event.
     * @param attendeeDeviceId  The ID of the device of the attendee.
     * @param eventCurrentAttendees  The current number of attendees as a string.
     */

    // Instead of the eventId, why don't I pass an event entirely?
    public void addAttendeeToEvent(String eventId, String attendeeDeviceId, String eventMaxAttendees, String eventCurrentAttendees) {
//Citation: For the following code line to query the events collection, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to ensure an event exists by querying for events
        db = FirebaseFirestore.getInstance();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DocumentReference eventDoc = db.collection("events").document(eventId);

        eventDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    checkEventWithAttendees(eventId, attendeeDeviceId, eventMaxAttendees, eventCurrentAttendees);
                    System.out.println("Event with ID " + eventId + " exists in the events collection.");
                } else {
                    System.out.println("Event with ID " + eventId + " does not exist in the events collection.");
                }
            } else {
                System.out.println("Error querying event document: " + task.getException());



            }
        });
    }
    /**
     * This methods helps associate an attendee to an event.
     *
     * @param eventId           The ID of the event.
     * @param eventMaxAttendees      The maximum number of attendees allowed for the event.
     * @param eventCurrentAttendees  The current number of attendees registered for the event.
     * @param attendeeDeviceId  The ID of the device of the attendee.
     * @param eventCurrentAttendees  The current number of attendees as a string.
     */
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

    /**
     * Updates the event information including the number of attendees.
     *
     * @param eventId           The ID of the event.
     * @param maxAttendees      The maximum number of attendees allowed for the event.
     * @param currentAttendees  The current number of attendees registered for the event.
     * @param attendeeDeviceId  The ID of the device of the attendee.
     * @param eventDoc          The reference to the Firestore document for the event.
     * @param eventCurrentAttendees  The current number of attendees as a string.
     */
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


    /**
     * Adds a new checked-in attendee to the Firestore database.
     *
     * @param eventId          The ID of the event.
     * @param attendeeDeviceId The ID of the device of the attendee.
     */
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


    /**
     * Updates the checked-in attendee information in the Firestore database.
     *
     * @param attendeeDeviceId The ID of the device of the attendee.
     * @param checkedInDoc      The reference to the Firestore document for checked-in attendees.
     */
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