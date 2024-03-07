package com.example.swiftcheckin;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventSignUp {

    private FirebaseFirestore db;
    // Need to create a new collection for attendees attending events
    // Store eventIds as documents
    // Store attendee device ids as fields in that document to help with memory.

    public void addAttendeeToEvent(Event event, String deviceId, Context context){
        // Add the attendee to the firestore database.
        // How will they be able to access the things tho?
        db = FirebaseFirestore.getInstance();
        CollectionReference eventAttendee = db.collection("eventsWithAttendees");
        int currentAttendees = event.getCurrentAttendees();
        DocumentReference eventDoc = eventAttendee.document(event.getDeviceId()+event.getEventTitle());
        if (event.getMaxAttendees() == -1 || currentAttendees < event.getMaxAttendees()) {

            eventDoc.set(event)
                    .addOnSuccessListener(aVoid -> {
                        // On success
                        System.out.println("Attendee added to event successfully.");
                        event.incrementCurrentAttendees();
                    })
                    .addOnFailureListener(e -> {
                        // On failure
                        System.out.println("Error adding attendee to event: " + e.getMessage());
                    });
        } else {
            Toast.makeText(context, "Event is full.", Toast.LENGTH_SHORT).show();
        }
    }

    // Need to handle querying the documents.
}
