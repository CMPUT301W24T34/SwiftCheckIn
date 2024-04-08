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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
/**
 * The admin firebase activity
 */

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

    /**
     * The firestore admin constructor
     */

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
    /**
     * Queries the events
     * @param eventList, List<Event>: the list of events
     * @param eventArrayAdapter: AdminEventArrayAdapter the event array adapter
     * @param imageArrayAdapter: ImageArrayAdapter the image array adapter
     * @param profileArrayAdapter: ProfileArrayAdapter the profile array adapter
     */

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

    /**
     * Queries the events
     * @param profileList, List<Profile>: the list of profiles
     * @param eventArrayAdapter: AdminEventArrayAdapter the event array adapter
     * @param imageArrayAdapter: ImageArrayAdapter the image array adapter
     * @param profileArrayAdapter: ProfileArrayAdapter the profile array adapter
     */


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

    /**
     * Queries the images
     * @param imageList, List<Event>: the list of images
     * @param eventArrayAdapter: AdminEventArrayAdapter the event array adapter
     * @param imageArrayAdapter: ImageArrayAdapter the image array adapter
     * @param profileArrayAdapter: ProfileArrayAdapter the profile array adapter
     */

    public void queryImages(List<Event> imageList, ImageArrayAdapter imageArrayAdapter, AdminEventArrayAdapter eventArrayAdapter, ProfileArrayAdapter profileArrayAdapter) {
        eventsCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                imageList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Event event = doc.toObject(Event.class);
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
     * Deletes a profile
     * @param eventList, List<Event>: the list of events
     * @param eventArrayAdapter: AdminEventArrayAdapter the event array adapter
     * @param imageArrayAdapter: ImageArrayAdapter the image array adapter
     * @param profileArrayAdapter: ProfileArrayAdapter the profile array adapter
     * @param profileList, List<Profile>: the list of profiles
     * @param nameToDelete, String: the name of the element to delete
     * @param selectedPosition, Integer: the position of the element to delete
     */

    public void deleteFirebaseProfile(List<Event> eventList, String nameToDelete, List<Profile> profileList, Integer selectedPosition, ProfileArrayAdapter profileArrayAdapter, AdminEventArrayAdapter eventArrayAdapter, ImageArrayAdapter imageArrayAdapter) {
        //delete not just profile but all events associated with that profile
        profilesCollectionRef.whereEqualTo("name", nameToDelete)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                deleteEventsWithAttendeesProfile(documentId);
                                deleteCheckedInProfile(documentId);
                                deleteSignedUpProfile(documentId);
//Citation: For the following code for firebase document deletion, OpenAI, 2024, Licensing: Creative Commons, ChatGPT, Prompt: How to delete all items with a certain device ID
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
    /**
     * Deletes multiple events
     * @param eventList, List<Event>: the list of events
     * @param eventArrayAdapter: AdminEventArrayAdapter the event array adapter
     * @param imageArrayAdapter: ImageArrayAdapter the image array adapter
     * @param profileArrayAdapter: ProfileArrayAdapter the profile array adapter
     * @param selectedPosition, Integer: the position of the element to delete
     */

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
    /**
     * Deletes a single event
     * @param eventList, List<Event>: the list of events
     * @param eventArrayAdapter: AdminEventArrayAdapter the event array adapter
     * @param imageArrayAdapter: ImageArrayAdapter the image array adapter
     * @param profileArrayAdapter: ProfileArrayAdapter the profile array adapter
     * @param selectedPosition, Integer: the position of the element to delete
     */

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
    /**
     * Deletes an image
     * @param eventList, List<Event>: the list of events
     * @param eventArrayAdapter: AdminEventArrayAdapter the event array adapter
     * @param imageArrayAdapter: ImageArrayAdapter the image array adapter
     * @param profileArrayAdapter: ProfileArrayAdapter the profile array adapter
     * @param selectedPosition, Integer: the position of the element to delete
     */

    public void deleteFirebaseImage(List<Event> eventList, Integer selectedPosition, ImageArrayAdapter imageArrayAdapter,AdminEventArrayAdapter eventArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        String nameToUpdate = "filler";
        String deviceIdImage = "filler";
        if (selectedPosition >= 0 && selectedPosition < eventList.size()) {
            nameToUpdate = eventList.get(selectedPosition).getEventTitle();
            Event selectedEvent = eventList.get(selectedPosition);
            deviceIdImage = selectedEvent.getDeviceId();
        }
        eventsCollectionRef.whereEqualTo("eventTitle", nameToUpdate)
                .whereEqualTo("deviceId", deviceIdImage)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                //Citation: For the following code to set a firebase field to null, OpenAI, 2024, Licensing: Creative Commons, ChatGPT, Prompt: How to set something to null in firebase
                                eventsCollectionRef.document(documentId)
                                        .update("eventPosterURL", null)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "Event poster URL has been set to null successfully!");

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
    /**
     * Filters firebase events
     * @param eventList, List<Event>: the list of events
     * @param eventArrayAdapter: AdminEventArrayAdapter the event array adapter
     * @param imageArrayAdapter: ImageArrayAdapter the image array adapter
     * @param profileArrayAdapter: ProfileArrayAdapter the profile array adapter
     * @param query, String: the name of the string being searched
     */
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
    /**
     * Filters firebase images
     * @param imageList, List<Event>: the list of images
     * @param eventArrayAdapter: AdminEventArrayAdapter the event array adapter
     * @param imageArrayAdapter: ImageArrayAdapter the image array adapter
     * @param profileArrayAdapter: ProfileArrayAdapter the profile array adapter
     * @param query, String: the name of the string being searched
     */
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
     * Filters firebase profiles
     * @param profileList, List<Profile>: the list of profiles
     * @param eventArrayAdapter: AdminEventArrayAdapter the event array adapter
     * @param imageArrayAdapter: ImageArrayAdapter the image array adapter
     * @param profileArrayAdapter: ProfileArrayAdapter the profile array adapter
     * @param query, String: the name of the string being searched
     */

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
    /**
     * Deletes geolocation event
     * @param eventId, String: the id of the event
     */
    //Citation: For the following code idea, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to query for documents that match a string and remove the document if so
    public void deleteGeolocationEvents(String eventId) {
        geolocationCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
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

    /**
     * Deletes qr code for event
     * @param eventIDToDelete, String: the id of the event
     */

    //Citation: For the following code idea, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to query for all documents with an attribute in a field that matches a string and remove the document if so
    public void deleteQRCodesByEventID(String eventIDToDelete) {
        qrcodesCollectionRef.whereEqualTo("eventID", eventIDToDelete)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                String isPromo = document.getString("isPromo");
                                if ("true".equals(isPromo)) {
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
                                } else {
                                    qrcodesCollectionRef.document(documentId)
                                            .update("eventID", "null")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "eventID has been set to null successfully!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Failed to update eventID: " + e.toString());
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
    /**
     * Deletes events with attendees
     * @param eventId, String: the id of the event
     */
    //Citation: For the following code idea, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to query for documents that match a string and remove the document if so
    public void deleteEventsWithAttendees(String eventId) {
        eventsWithAttendeesCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
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
    /**
     * Deletes events with checked in status
     * @param eventId, String: the id of the event
     */

    //Citation: For the following code idea, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to query for documents that match a string and remove the document if so
    public void deleteCheckedIn(String eventId) {
        checkedInCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
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


    /**
     * Deletes announcements for event
     * @param eventId, String: the id of the event
     */
    //Citation: For the following code idea, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to query for documents that match a string and remove the document if so
    public void deleteAnnouncement(String eventId) {
        announcementCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
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
    /**
     * Deletes events with people signed up
     * @param eventId, String: the id of the event
     */

    //Citation: For the following code idea, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to query for if an attribute in a firebase array matches a string and remove the attribute if so

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

    /**
     * Deletes profile for events with attendees
     * @param deviceIDToDelete, String: the id of the device
     */
    //Citation: For the following code idea, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to query and delete a field that matches a string
    public void deleteEventsWithAttendeesProfile(String deviceIDToDelete) {
        eventsWithAttendeesCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.contains(deviceIDToDelete)) {
                                    document.getReference()
                                            .update(deviceIDToDelete, FieldValue.delete())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "Field with device ID deleted successfully!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Failed to delete field with device ID: " + e.toString());
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


    /**
     * Deletes profile for checked in
     * @param deviceIDToDelete, String: the id of the device
     */
    //Citation: For the following code idea, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to query and delete a field that matches a string

    public void deleteCheckedInProfile(String deviceIDToDelete) {
        checkedInCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.contains(deviceIDToDelete)) {
                                    document.getReference()
                                            .update(deviceIDToDelete, FieldValue.delete())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "Field with device ID deleted successfully from checkedInCollectionRef!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Failed to delete field with device ID from checkedInCollectionRef: " + e.toString());
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents from checkedInCollectionRef: ", task.getException());
                        }
                    }
                });
    }


    /**
     * Deletes profile for signed up
     * @param deviceIDToDelete, String: the id of the device
     */
    //Citation: For the following code idea, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to query and delete a document that matches a string
    public void deleteSignedUpProfile(String deviceIDToDelete) {
        signedUpEventsCollectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(deviceIDToDelete)) {
                                    document.getReference()
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "Document with matching ID deleted successfully from signedUpEventsCollectionRef!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "Failed to delete document with matching ID from signedUpEventsCollectionRef: " + e.toString());
                                                }
                                            });
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents from signedUpEventsCollectionRef: ", task.getException());
                        }
                    }
                });
    }







}
