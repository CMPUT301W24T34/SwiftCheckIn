package com.example.swiftcheckin.admin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.swiftcheckin.attendee.Profile;
import com.example.swiftcheckin.organizer.Event;
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
import java.util.List;

public class FirestoreAdmin {
    private FirebaseFirestore db;
    private CollectionReference eventsCollectionRef;
    private CollectionReference profilesCollectionRef;
    private CollectionReference geolocationCollectionRef;
    private CollectionReference qrcodesCollectionRef;
    private CollectionReference eventsWithAttendeesCollectionRef;
    private CollectionReference  checkedInCollectionRef;
    private CollectionReference announcementCollectionRef;
    private CollectionReference signedUpEventsCollectionRef;

    public FirestoreAdmin() {
        db = FirebaseFirestore.getInstance();
        eventsCollectionRef = db.collection("events");
        profilesCollectionRef = db.collection("profiles");
        geolocationCollectionRef = db.collection("geolocation");
        qrcodesCollectionRef = db.collection("qrcodes");
        eventsWithAttendeesCollectionRef = db.collection("eventsWithAttendees");
        checkedInCollectionRef = db.collection("checkedIn");
        announcementCollectionRef = db.collection("Announcements");
        signedUpEventsCollectionRef = db.collection("SignedUpEvents");

    }

    public void queryEvents(List<Event> eventList, AdminEventArrayAdapter eventArrayAdapter, ImageArrayAdapter imageArrayAdapter, ProfileArrayAdapter profileArrayAdapter) {
        eventsCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    public void queryProfiles(List<Profile> profileList, ProfileArrayAdapter profileArrayAdapter, AdminEventArrayAdapter eventArrayAdapter, ImageArrayAdapter imageArrayAdapter) {
        profilesCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    public void queryImages(List<Event> imageList, ImageArrayAdapter imageArrayAdapter, AdminEventArrayAdapter eventArrayAdapter, ProfileArrayAdapter profileArrayAdapter) {
        eventsCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    public void deleteFirebaseProfile(List<Event> eventList, String nameToDelete, List<Profile> profileList, Integer selectedPosition, ProfileArrayAdapter profileArrayAdapter, AdminEventArrayAdapter eventArrayAdapter, ImageArrayAdapter imageArrayAdapter) {
        //delete not just profile but all events associated with that profile
        profilesCollectionRef.whereEqualTo("name", nameToDelete)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Citation: For the following code idea, OpenAI, 2024, Licensing: Creative Commons, ChatGPT, Prompt: How to delete all items with a certain device ID
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                profilesCollectionRef.document(documentId)
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
                                deleteFirebaseMultiEvent(eventList, selectedPosition, eventArrayAdapter, documentId, imageArrayAdapter, profileArrayAdapter);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void deleteFirebaseMultiEvent(List<Event> eventList, Integer selectedPosition, AdminEventArrayAdapter eventArrayAdapter, String deviceId, ImageArrayAdapter imageArrayAdapter, ProfileArrayAdapter profileArrayAdapter) {
        String nameToDelete = "filler";
        if (selectedPosition >= 0 && selectedPosition < eventList.size()) {
            nameToDelete = eventList.get(selectedPosition).getDeviceId();
        }
        //Citation: For the following code idea, OpenAI, 2024, ChatGPT, Licensing: Creative Commons, Prompt: How to check if the device Id is not "pass"
        if (!"pass".equals(deviceId)) {
            nameToDelete = deviceId;
        }
        eventsCollectionRef.whereEqualTo("deviceId", nameToDelete)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                deleteGeolocationEvents(documentId);
                                deleteQRCodesByEventID(documentId);
                                deleteEventsWithAttendees(documentId);
                                deleteCheckedIn(documentId);
                                deleteAnnouncement(documentId);
                                deleteSignedUpEvents(documentId);
                                eventsCollectionRef.document(documentId)
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

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    public void deleteFirebaseEvent(List<Event> eventList, Integer selectedPosition, AdminEventArrayAdapter eventArrayAdapter, ImageArrayAdapter imageArrayAdapter, ProfileArrayAdapter profileArrayAdapter) {
        if (selectedPosition >= 0 && selectedPosition < eventList.size()) {
            Event selectedEvent = eventList.get(selectedPosition);
            String title = selectedEvent.getEventTitle();
            String deviceId = selectedEvent.getDeviceId();
            String combinedName = deviceId + title;
            Log.d(TAG, "COMBINED NAME IS " + combinedName);
            deleteGeolocationEvents(combinedName);
            deleteQRCodesByEventID(combinedName);
            deleteEventsWithAttendees(combinedName);
            deleteCheckedIn(combinedName);
            deleteAnnouncement(combinedName);
            deleteSignedUpEvents(combinedName);

            eventsCollectionRef.whereEqualTo("eventTitle", title)
                    .whereEqualTo("deviceId", deviceId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String documentId = document.getId();
                                    eventsCollectionRef.document(documentId)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "Event data has been deleted successfully!");
                                                    // Now you can delete related data or update UI as needed
                                                    eventArrayAdapter.notifyDataSetChanged();
                                                    imageArrayAdapter.notifyDataSetChanged();
                                                    profileArrayAdapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Event data could not be deleted! " + e.toString());
                                                }
                                            });
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            Log.d(TAG, "Selected event position is invalid.");
        }
    }

    public void deleteFirebaseImage(List<Event> eventList, Integer selectedPosition, ImageArrayAdapter imageArrayAdapter,AdminEventArrayAdapter eventArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        String nameToUpdate = "filler";

        if (selectedPosition >= 0 && selectedPosition < eventList.size()) {
            nameToUpdate = eventList.get(selectedPosition).getEventTitle();
        }

        eventsCollectionRef.whereEqualTo("eventTitle", nameToUpdate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                //Citation: For the following code idea, OpenAI, 2024, Licensing: Creative Commons, ChatGPT, Prompt: How to set something to null in firebase
                                eventsCollectionRef.document(documentId)
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
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    //Citation: For the following code idea to use the search bar and filter searches, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
    public void filterFirebaseEvents(List<Event> eventList,String query, AdminEventArrayAdapter eventArrayAdapter,ImageArrayAdapter imageArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
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
            eventsCollectionRef.whereEqualTo("eventTitle", matchingEvent.getEventTitle())
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
    //Citation: For the following code idea to use the search bar and filter searches, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
    public void filterFirebaseImageList(List<Event> imageList, String query, ImageArrayAdapter imageArrayAdapter, AdminEventArrayAdapter eventArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {

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
            eventsCollectionRef.whereEqualTo("eventTitle", matchingEvent.getEventTitle())
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
    //Citation: For the following code idea to use the search bar and filter searches, OpenAI, 2024, Licensing: Creative Commons, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
    public void filterProfileList(List<Profile> profileList, String query, ProfileArrayAdapter profileArrayAdapter, AdminEventArrayAdapter eventArrayAdapter, ImageArrayAdapter imageArrayAdapter) {


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
            profilesCollectionRef.whereEqualTo("name", matchingProfile.getName())
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
    //CITE
    public void deleteGeolocationEvents(String eventId) {
        geolocationCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                // Check if the document ID matches the eventId
                                if (documentId.equals(eventId)) {
                                    geolocationCollectionRef.document(documentId)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "Geolocation data with ID " + eventId + " has been deleted successfully!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Failed to delete geolocation data: " + e.toString());
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    //CITE
    public void deleteQRCodesByEventID(String eventIDToDelete) {
        qrcodesCollectionRef.whereEqualTo("eventID", eventIDToDelete)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                qrcodesCollectionRef.document(documentId)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "QR code data has been deleted successfully!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Failed to delete QR code data: " + e.toString());
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //delete eventswithattendees
    public void deleteEventsWithAttendees(String eventId) {
        eventsWithAttendeesCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                // Check if the document ID matches the eventId
                                if (documentId.equals(eventId)) {
                                    eventsWithAttendeesCollectionRef.document(documentId)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "Event with attendees data has been deleted successfully!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Failed to delete event with attendees data: " + e.toString());
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //deleteCheckedIn

    public void deleteCheckedIn(String eventId) {
        checkedInCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                // Check if the document ID matches the eventId
                                if (documentId.equals(eventId)) {
                                    checkedInCollectionRef.document(documentId)
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
                                                    Log.d(TAG, "Failed to delete data: " + e.toString());
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    //delete announcement

    public void deleteAnnouncement(String eventId) {
        announcementCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                // Check if the document ID matches the eventId
                                if (documentId.equals(eventId)) {
                                    announcementCollectionRef.document(documentId)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "Announcement data with ID " + eventId + " has been deleted successfully!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Failed to delete announcement data: " + e.toString());
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    // delete signedupevents
    //Cite

    public void deleteSignedUpEvents(String eventId) {
        signedUpEventsCollectionRef.whereArrayContains("eventIds", eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> eventsIds = (List<String>) document.get("eventIds");
                                if (eventsIds != null) {
                                    eventsIds.remove(eventId);
                                    document.getReference().update("eventIds", eventsIds)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "Event ID removed from SignedUpEvents successfully!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Failed to remove event ID from SignedUpEvents: " + e.toString());
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }






}
