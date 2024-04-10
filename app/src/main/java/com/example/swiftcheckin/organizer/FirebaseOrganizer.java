package com.example.swiftcheckin.organizer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class for managing Firebase interactions related to event organization.
 */
public class FirebaseOrganizer {
    private FirebaseFirestore db;

    private List<String> matchedProfiles = new ArrayList<>();

    String deviceID;


    /**
     * Constructor for FirebaseOrganizer class.
     * Initializes Firestore database instance and retrieves device ID.
     *
     * @param context The context of the application/activity.
     */
    public FirebaseOrganizer(Context context) {
        this.db = FirebaseFirestore.getInstance();
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Empty constructor for FirebaseOrganizer class.
     * Initializes Firestore database instance.
     */
    public FirebaseOrganizer(){
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Adds a QR code entry to the Firestore database.
     *
     * @param qrcode The QR code object containing information about the QR code.
     */
    public void addQrCode(Qr_Code qrcode) {
        DocumentReference deviceRef = db.collection("qrcodes").document(qrcode.getQrID());
        HashMap<String, String> data = new HashMap<>();
        data.put("deviceID", qrcode.getDeviceID());
        data.put("QrID", qrcode.getQrID());
        data.put("eventID", qrcode.getEventID());
        data.put("isPromo", qrcode.getIsPromo().toString());
        deviceRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Qrcode Added Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "QrCode could not be added.");
                    }
                });
    }

    /**
     * Saves an event to the Firestore database.
     *
     * @param event The event object containing information about the event.
     */
    public void saveEvent(Event event) {
        // Ensure we have 3 columns in firestore for simple reference.
        // This is to ensure admin will be able to delete any events much more conveniently.

        DocumentReference deviceRef = db.collection("events").document(deviceID+event.getEventTitle());

        // Document Id, AKA eventId = deviceId + eventTitle

        // Hashmap method learned in labs.
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
        data.put("eventCurrentAttendees", Integer.toString(event.getCurrentAttendees()));
        data.put("qrID", event.getQrID());
        data.put("qrPromoID", event.getQrPromoID());
        // Sets the data to Firebase.
        deviceRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {   // In the event, the event is added.
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Event Added Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() { // In the event, the event fails to be added to Firebase.
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Event could not be added.");
                    }
                });
    }

    /**
     * Interface for handling the retrieval of QR code IDs.
     */
    public interface QrIDCallback {
        /**
         * Callback method invoked when the QR code ID is received.
         *
         * @param qrID The QR code ID received.
         */
        void onQrIDReceived(String qrID);
    }

    /**
     * Retrieves the associated QR code ID for a given event ID from the Firestore database.
     *
     * @param eventId The ID of the event for which to retrieve the QR code ID.
     * @param field The field in the Firestore document containing the QR code ID.
     * @param callback The callback interface to handle the retrieved QR code ID.
     */
    public void getAssociatedCodeID(String eventId, String field, QrIDCallback callback) {
        // Citation: OpenAI, Date: 29 March, 2024. ChatGPT. Used to find a way to wait till db operation is complete.
        // gave the code without this and asked how to make the program not crash due to asynchronous behaviour of db.
        // ChatGPT suggested this

        Log.e("Qr Code Id requested for event", eventId);

        db.collection("events").document(eventId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String qrID = document.getString(field);
                                Log.e("Qr Code Id requested for fragment", qrID);
                                callback.onQrIDReceived(qrID);
                            }
                        } else {
                            Log.e("Error in fetching qr Id for eventID", "Error - event: " + eventId);
                            callback.onQrIDReceived(null); // Signal failure
                        }
                    }
                });
    }
    /**
     * Creates a default geolocation for an event
     * @param  eventId: the id of the event, String
     */

    public void addGeolocation(String eventId) {
        DocumentReference geolocationRef = db.collection("geolocation").document(eventId);
        geolocationRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Citation: For the following code query, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to set a default geolocation setting to false
                if (!documentSnapshot.exists()) {
                    geolocationRef.set(new HashMap<String, Object>() {{
                                put("geolocation", false);
                            }})
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(ContentValues.TAG, "Geolocation document created successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(ContentValues.TAG, "Error creating geolocation document", e);
                                }
                            });
                }
            }
        });

    }
    /**
     * Checks if geolocation is enabled
     * @param eventId: the id of the event, String
     * @param callback: the callback to asynchronously set the switch, GeolocationCallback
     */

    public void geolocationEnabled(String eventId, GeolocationCallback callback) {
        db.collection("geolocation").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        //Citation: For the following code to check geolocation enabled, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to return a boolean with a firebase query asynchronously
                        Boolean enabled = documentSnapshot.getBoolean("geolocation");
                        callback.onGeolocationStatus(enabled);
                    } else {
                        callback.onGeolocationStatus(false); // No document found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching geolocation", e);
                    callback.onGeolocationStatus(false); // Handle failure
                });
    }
    /**
     * The callback for the geolocation boolean
     */
    public interface GeolocationCallback {
        //Citation: For the following code to use a callback to return the boolean, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to return a boolean with a firebase query asynchronously

        void onGeolocationStatus(boolean enabled);
    }
    /**
     * Updates the geolocation when the switch is clicked
     * @param eventId: the id of the event, String
     * @param isChecked: sees if the switch is enabled, Boolean
     */
    public void updateGeolocation(String eventId, Boolean isChecked) {
        DocumentReference geolocationRef = db.collection("geolocation").document(eventId);
        //Citation: For the following code query to update the field, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to update firebase geolocation with the switch state
        geolocationRef.update("geolocation", isChecked);
    }

    /**
     * Gets the checked in location of the checked in profiles and displays the locations on a map
     * @param eventId: the id of the event, String
     * @param myMap: The map with the profiles, GoogleMap
     */
    public void getCheckedIn(String eventId, GoogleMap myMap) {
        matchedProfiles.clear();
        db.collection("checkedIn")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> checkedInData = documentSnapshot.getData();
                        if (checkedInData != null) {
                            //Citation: For the following code to get the field names and add them to a list, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to get the field names and add them to a list
                            matchedProfiles.addAll(checkedInData.keySet());
                            //Citation: For the following code idea to query each profile in a list, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to make a query for each element of a list
                            for (String id : matchedProfiles) {

                                db.collection("profiles").document(id)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    Map<String, Object> data = documentSnapshot.getData();
                                                    String locationPermission = (String) data.get("locationPermission");
                                                    if (!locationPermission.equals("False")) {

                                                        String latitudeStr = (String) data.get("latitude");
                                                        String longitudeStr = (String) data.get("longitude");
                                                        if (latitudeStr != null && longitudeStr != null && !latitudeStr.equals("Unknown") && !longitudeStr.equals("Unknown")){
                                                            //Citation: For the following code to convert string to double, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to turn a list into a double

                                                            Double latitude = Double.parseDouble(latitudeStr);
                                                        Double longitude = Double.parseDouble(longitudeStr);
                                                        if (latitude != null && longitude != null) {
                                                            //Citation: The following code for creating google map and adding markers, 2024, Youtube, "Step by Step Google Maps Implementation in Android App | Google Maps in Android: Step-by-Step Guide", Codingzest, https://www.youtube.com/watch?v=pOKPQ8rYe6g

                                                            LatLng location = new LatLng(latitude, longitude);
                                                            myMap.addMarker(new MarkerOptions().position(location).title((String) data.get("name")));
                                                            float zoomLevel = 16.0f;
                                                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, zoomLevel);
                                                            myMap.moveCamera(cameraUpdate);
                                                        }
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }

                })


                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(ContentValues.TAG, "Error querying documents: ", e);
                    }
                });
    }


    /**
     * Interface to implement a callback to wait for data to be loaded from the firebase.
     */
    public interface ReuseQrDataCallback {
        void onDataLoaded(ArrayList<Qr_Code> dataList);
        void onError(String errorMessage);
    }

    /**
     * Fetches latest data of unused qr codes generated by the user in the past.
     * @param callback Callback to wait for firebase operation to end
     */
    public void getReuseQrData(ArrayList<Qr_Code> dataList, ReuseQrDataCallback callback)
    {
        // callbacks learned from previous implementations
        CollectionReference qrcodeCol = db.collection("qrcodes");

        qrcodeCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    dataList.clear();
                    QuerySnapshot queryQrSnapshots = task.getResult();
                    for(QueryDocumentSnapshot qrcode: queryQrSnapshots)
                    {
                        String deviceId = (String) qrcode.get("deviceID");
                        String qrId = (String) qrcode.get("QrID");
                        String eventID = (String) qrcode.get("eventID");

                        if(deviceId != null && deviceId.equals(deviceID) && eventID.equals("null"))
                        {
                            Qr_Code qrNewCode = new Qr_Code(qrId, QrCodeManager.generateQRCode(qrId));
                            qrNewCode.setEventID(eventID);
                            qrNewCode.setDeviceID(deviceID);
                            dataList.add(qrNewCode);
                        }

                    }
                    callback.onDataLoaded(dataList);
                }
                else
                {
                    callback.onError("Error in fetching reusable QRs");
                }
            }
        });
    }


    // make callback interface
    /**
     * Interface for handling the completion or error of fetching event data.
     */
    public interface EventCallback
    {
        /**
         * Callback method invoked when event data is successfully fetched.
         *
         * @param event The event object containing the fetched data.
         */
        void onCompleteFetch(Event event);

        /**
         * Callback method invoked when an error occurs during event data fetching.
         *
         * @param errorMessage The error message describing the encountered error.
         */
        void onError(String errorMessage);
    }

    /**
     * Retrieves event data from Firestore based on the provided event ID and invokes appropriate callbacks.
     *
     * @param eventId         The ID of the event to retrieve.
     * @param eventCallback   The callback interface for handling the completion or error of fetching event data.
     */
    public void getEvent(String eventId, EventCallback eventCallback)
    {
        DocumentReference eventDoc = db.collection("events").document(eventId);

        eventDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete())
                {
                    Event event;
                    DocumentSnapshot result = task.getResult();
                    if(result.exists())
                    {
                        String eventTitle = (String) result.getData().get("eventTitle");
                        String eventLocation = (String) result.getData().get("eventLocation");
                        String eventDescription = (String) result.getData().get("eventDescription");
                        String deviceId = (String) result.getData().get("deviceId");

                        String eventImageURL = (String) result.getData().get("eventPosterURL");
                        String startDate = (String) result.getData().get("eventStartDate");
                        String endDate = (String) result.getData().get("eventEndDate");
                        String startTime = (String) result.getData().get("eventStartTime");
                        String endTime = (String) result.getData().get("eventEndTime");
                        String maxAttendees = (String) result.getData().get("eventMaxAttendees");

                        String checkInQrId = (String) result.getData().get("qrID");
                        String promoQrId = (String) result.getData().get("qrPromoID");

                        if (maxAttendees == null || maxAttendees.equals("-1")) {   // Was meant to work in case there was no limit for max attendees.
                            event = new Event(eventTitle, eventDescription, eventLocation, deviceId, eventImageURL, startDate, endDate, startTime, endTime);
                        } else {   // In case max attendees was specified.
                            event = new Event(eventTitle, eventDescription, eventLocation, deviceId, eventImageURL, maxAttendees, startDate, endDate, startTime, endTime);
                        }
                        event.setQrID(checkInQrId);
                        event.setQrPromoID(promoQrId);
                        eventCallback.onCompleteFetch(event);
                    }
                }
                else
                {
                    eventCallback.onError("Error fetching event information");
                }
            }
        });

    }
    public void addAttendeeToEvent(String eventId, String attendeeDeviceId, String eventMaxAttendees, String eventCurrentAttendees) {
        int maxAttendees = Integer.parseInt(eventMaxAttendees);
        int currentAttendees = Integer.parseInt(eventCurrentAttendees);
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


    /**
     * Callback interface for handling the completion or error of fetching check-in data.
     */
    public interface getCheckInCallback
    {
        /**
         * Invoked when check-in data is successfully fetched.
         *
         * @param dataList The list of pairs containing check-in data (e.g., attendee ID and check-in count).
         */
        void onDataFetched(ArrayList<Pair<String, String>> dataList);

        /**
         * Invoked when an error occurs while fetching check-in data.
         *
         * @param errorMessage The error message describing the encountered error.
         */
        void onError(String errorMessage);
    }

    /**
     * Fetches the checked-in details for a specific event.
     *
     * @param eventId  The ID of the event for which checked-in details are to be fetched.
     * @param collection  The collection from which to fetch the checked-in details.
     * @param dataList  The list to populate with checked-in data pairs (e.g., attendee ID and check-in count).
     * @param callback  The callback to handle the completion or error of fetching checked-in details.
     */
    public void getCheckedInDetails(String eventId, String collection, ArrayList<Pair<String, String>> dataList, getCheckInCallback callback)
    {
        DocumentReference checkInDoc = db.collection(collection).document(eventId);

        checkInDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    callback.onError("Error in fetching check-in details");
                    return;
                }
                dataList.clear();
                if(value!= null && value.exists())
                {
                    Map<String, Object> data = value.getData();
                    if(data != null)
                    {
                        for(Map.Entry<String, Object> entry: data.entrySet())
                        {
                            String device = entry.getKey();
                            String count = entry.getValue().toString();
                            if(collection.equals("eventsWithAttendees"))
                            {
                                dataList.add(new Pair<>(device, "None"));
                            }
                            else
                            {
                                dataList.add(new Pair<>(device, count));
                            }

                        }
                    }
                }
                callback.onDataFetched(dataList);
            }
        });
    }

    /**
     * Callback interface used to retrieve a user's name asynchronously.
     */
    interface getNameCallBack
    {
        /**
         * Called when the user's name is successfully fetched.
         *
         * @param name The name of the user.
         */
        public void onNameFetched(String name);
    }

    /**
     * Retrieves the user's name associated with the given device ID asynchronously.
     *
     * @param device   The device ID of the user.
     * @param callBack The callback to be invoked when the user's name is fetched.
     */
    public void getUserName(String device, getNameCallBack callBack)
    {
        DocumentReference documentReference = db.collection("profiles").document(device);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete())
                {
                    DocumentSnapshot result = task.getResult();
                    if(result.exists())
                    {
                        String name = (String) result.get("name");
                        if(name.equals(""))
                        {
                            callBack.onNameFetched("No Name");
                        }
                        callBack.onNameFetched(name);
                    }
                }
            }
        });
    }
}
