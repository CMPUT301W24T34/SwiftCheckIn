package com.example.swiftcheckin.organizer;

import androidx.annotation.NonNull;

import com.example.swiftcheckin.organizer.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

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
     * @param eventId - The Id of the event the attendee wants to sign up for.
     * @param attendeeDeviceId - The id of the attendee who wants to signs up
     */
    // Instead of the eventId, why don't I pass an event entirely?
    public void addAttendeeToEvent(String eventId, String attendeeDeviceId, String eventMaxAttendees, String eventCurrentAttendees){
        int maxAttendees = Integer.parseInt(eventMaxAttendees);
        int currentAttendees = Integer.parseInt(eventCurrentAttendees);
        // Add the attendee to the firestore database.
        db = FirebaseFirestore.getInstance();
//        // Hashmap methods from labs

        if (currentAttendees < maxAttendees) {
            HashMap<String, String> data = new HashMap<>();
            data.put(attendeeDeviceId, attendeeDeviceId);

//        int currentAttendees = updated_event.getCurrentAttendees();  // Current Attendees, in the future to help limit the number of attendees that can join.
            DocumentReference eventDoc = db.collection("eventsWithAttendees").document(eventId);
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
//        updateEventInFirebase(updated_event);  // Updates this event in Firebase, meant to help with limiting attendees.
            // Has not been achieved yet.

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
     * Creates an event that will be used to updated.
     * @param eventId - Represents the event the attendee will attend.
     * @return - A substitute event that will be created for updation in the future.
     */
    public Event createEvent(String eventId){
        Event updated_event;

        // String arrays to store the information of the event from Firebase.
        final String[] eventTitle = new String[1];
        final String[] eventStartTime = new String[1];
        final String[] eventStartDate = new String[1];
        final String[] eventPosterURL = new String[1];
        final String[] eventLocation = new String[1];
        final String[] eventEndTime = new String[1];
        final String[] eventEndDate = new String[1];
        final String[] eventDescription = new String[1];
        final String[] eventDeviceId = new String[1];

        db = FirebaseFirestore.getInstance();
        db.collection("events")   // For the DocumentReference
                .document(eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    // Citation: OpenAI, 03-05-2024, ChatGPT, How to get specific information from a document?
                    /*
                    Although I knew how to query through the documents and get the information from the labs, I did not know how to get
                    specific information from one document only.
                                        db.collection("events")
                        .document(eventId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot event = task.getResult();
                                    if (event.exists()){
                                        String eventTitle = (String) event.get("eventTitle");
                                        String eventStartTime = (String) event.get("eventStartTime");
                                        String eventStartDate = (String) event.get("eventStartDate");
                                        String eventPosterURL = (String) event.get("eventPosterURL");
                                        String eventLocation = (String) event.get("eventLocation");
                                        String eventEndTime = (String) event.get("eventEndTime");
                                        String eventEndDate = (String) event.get("eventEndDate");
                                        String eventDescription = (String) event.get("eventDescription");
                                        String eventDeviceId = (String) event.get("deviceId");

                                        Event updated_event = new Event(eventTitle, eventDescription, eventLocation, eventDeviceId, eventPosterURL, eventStartDate, eventEndDate, eventStartTime, eventEndTime);


                                        }
                                    }
                                }
                            });

                            The above code was presented to me by ChatGPT and the Task<DocumentSnapshot> is what helped me
                            to get the specific information of an event in FireBase.
                     */
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot event = task.getResult();
                            if (event.exists()){
                                eventTitle[0] = (String) event.get("eventTitle");
                                eventStartTime[0] = (String) event.get("eventStartTime");
                                eventStartDate[0] = (String) event.get("eventStartDate");
                                eventPosterURL[0] = (String) event.get("eventPosterURL");
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
        return updated_event;   // Event is returned.

    }

    /**
     * Allows for updation of event information in Firebase. Meant to help with the updation of currentAttendees, which has not been
     * implemented yet.
     * @param event - Represents the event that will be updated.
     */
//    public void updateEventInFirebase(Event event){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference eventsRef = db.collection("events");
//        DocumentReference oneEvent = eventsRef.document(event.getDeviceId() + event.getEventTitle());  // Event Id
//
//        // Hashmap method we learned in labs.
//        HashMap<String, String> data = new HashMap<>();
//        data.put("eventTitle", event.getEventTitle());
//        data.put("eventLocation", event.getLocation());
//        data.put("deviceId", event.getDeviceId());
//        data.put("eventDescription", event.getDescription());
//        data.put("eventPosterURL", event.getEventImageUrl());
//        data.put("eventStartDate", event.getStartDate());
//        data.put("eventEndDate", event.getEndDate());
//        data.put("eventStartTime", event.getStartTime());
//        data.put("eventEndTime", event.getEndTime());
//        data.put("eventMaxAttendees", Integer.toString(event.getMaxAttendees()));  // Converts the integer value into a string.
//        // How to convert integer to string in Java:
//        // JavaTpoint. "Java int to String." Accessed 03-06-2024.
//        // URL: https://www.javatpoint.com/java-int-to-string#:~:text=We%20can%20convert%20int%20to,method%2C%20string%20concatenation%20operator%20etc.
//        // Sets the data in the event.
//        oneEvent
//                .set(data)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {  // If updating event is successful
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d(TAG, "Event updated Successfully");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener(){  // If updating the events is not successful.
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "Event could not be updated.");
//                    }
//                });
//    }
}
