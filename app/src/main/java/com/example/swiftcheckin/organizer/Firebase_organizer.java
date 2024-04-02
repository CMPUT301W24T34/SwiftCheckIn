package com.example.swiftcheckin.organizer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class Firebase_organizer {
    private FirebaseFirestore db;

    private List<String> matchedProfiles = new ArrayList<>();

    String deviceID;


    public Firebase_organizer(Context context) {
        this.db = FirebaseFirestore.getInstance();
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

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

    public interface QrIDCallback {
        void onQrIDReceived(String qrID);
    }

    public void getAssociatedCodeID(String eventId, QrIDCallback callback) {
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
                                String qrID = document.getString("qrID");
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


    public void addGeolocation(String eventId) {
        DocumentReference geolocationRef = db.collection("geolocation").document(eventId);
        geolocationRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Citation: For the following code query ideas, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to set a default geolocation setting to false
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

    //Citation: For the following code query ideas, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to return a boolean with a firebase query asynchronously
    public void geolocationEnabled(String eventId, GeolocationCallback callback) {
        db.collection("geolocation").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
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

    public interface GeolocationCallback {
        void onGeolocationStatus(boolean enabled);
    }

    //Citation: For the following code query ideas, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to update firebase geolocation with the switch state
    public void updateGeolocation(String eventId, Boolean isChecked) {
        DocumentReference geolocationRef = db.collection("geolocation").document(eventId);
        geolocationRef.update("geolocation", isChecked);
    }

    public void getCheckedIn(String eventId, GoogleMap myMap) {
        //Citation: For the following code query ideas, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to get the field names and add them to a list
        matchedProfiles.clear();
        db.collection("checkedIn")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> checkedInData = documentSnapshot.getData();
                        if (checkedInData != null) {
                            matchedProfiles.addAll(checkedInData.keySet());
                            //Citation: For the following code query ideas, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to make a nested query to query twice based on the results of the first query
                            for (String id : matchedProfiles) {

                                db.collection("profiles").document(id)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    Map<String, Object> data = documentSnapshot.getData();
                                                    String locationPermission = (String) data.get("locationPermission");
                                                    if (locationPermission != "False") {
                                                        String latitudeStr = (String) data.get("latitude");
                                                        String longitudeStr = (String) data.get("longitude");

                                                        Double latitude = Double.parseDouble(latitudeStr);
                                                        Double longitude = Double.parseDouble(longitudeStr);
                                                        if (latitude != null && longitude != null) {
                                                            LatLng location = new LatLng(latitude, longitude);
                                                            myMap.addMarker(new MarkerOptions().position(location).title((String) data.get("name")));
                                                            float zoomLevel = 16.0f;
                                                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, zoomLevel);
                                                            myMap.moveCamera(cameraUpdate);
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


}
