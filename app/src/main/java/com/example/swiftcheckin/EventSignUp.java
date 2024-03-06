package com.example.swiftcheckin;

import com.google.firebase.firestore.FirebaseFirestore;

public class EventSignUp {

    private FirebaseFirestore db;
    // Need to create a new collection for attendees attending events
    // Store eventIds as documents
    // Store attendee device ids as fields in that document to help with memory.

    public void addAttendeeToEvent(Event event, String deviceId){
        // Add the attendee to the firestore database.
        // How will they be able to access the things tho?
    }


}
