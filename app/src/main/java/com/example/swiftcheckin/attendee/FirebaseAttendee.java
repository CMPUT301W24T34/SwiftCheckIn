package com.example.swiftcheckin.attendee;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.provider.Settings;
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
import com.google.firebase.messaging.FirebaseMessaging; // Added this import for FirebaseMessaging

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseAttendee {
    private FirebaseFirestore db;
    private CollectionReference profilesCollectionRef;
    private String deviceId;

    public FirebaseAttendee() {
        db = FirebaseFirestore.getInstance();
        profilesCollectionRef = db.collection("profiles");
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public CollectionReference getProfilesCollectionRef() {
        return profilesCollectionRef;
    }


    public String getDeviceToken(Context context) {
        String deviceId = retrieveDeviceId(context);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Failed to get device token", task.getException());
                            return;
                        }

                        // Store the device token in a variable or return it
                        String deviceToken = task.getResult();
                        // ...
                    }
                });

        // Return the device token if you stored it in a variable, or return null if not
        return null;
    }

    private String retrieveDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * update the firebase collection with the location data
     * @param deviceId - deviceid of users phone
     * @param data - the data to be updated to firebase
     */
    public void updateLocationInfo(String deviceId, HashMap<String, Object> data){
        db.collection("profiles").document(deviceId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Location data has been added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Location data could not be added");
                    }
                });
    }

    /**
     * Saves profile data to firebase
     * @param deviceId id of users device
     * @param data data to be updated
     */
    public void saveProfileToFirebase(String deviceId, HashMap<String, String> data) {
        profilesCollectionRef.document(deviceId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "User data has been added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "User data could not be added");
                    }
                });
    }


    // Citation: OpenAI, 03-05-2024, ChatGPT, Saving the data as a list in an attribute called eventIds
    /* Chatgpt suggested to use Map<String, Object> data = new HashMap<>() for the list
    output was also about the oncompletelistener and document snapshots and tasks
    giving this code: ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
     and if (document.exists() && document.contains("eventIds")) to see if there is already a list for this user
     and List<String> eventIds = (List<String>) document.get("eventIds") to get it
    */
    /**
     * Saves the attendance data for a specific user and event.
     *
     * @param deviceId the unique identifier of the device
     * @param eventId  the unique identifier of the event
     */
    public void saveSignUpData(String deviceId, String eventId, Context context) {
        DocumentReference ref = db.collection("SignedUpEvents").document(deviceId);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> data = new HashMap<>();

                    // already has a list
                    if (document.exists() && document.contains("eventIds")) {
                        List<String> eventIds = (List<String>) document.get("eventIds");
                        if (!eventIds.contains(eventId)) {
                            eventIds.add(eventId);
                            data.put("eventIds", eventIds);
                            addSignUpData(data, ref, context);
                            storeDeviceToken(deviceId, eventId, context); // Added this line to store the device token
                        }
                        // they've already signed up for this event
                        else{
                            Toast.makeText(context, "You are already signed up for this event", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // no list yet
                    else {
                        List<String> eventIds = new ArrayList<>();
                        eventIds.add(eventId);
                        data.put("eventIds", eventIds);
                        addSignUpData(data, ref, context);
                        storeDeviceToken(deviceId, eventId, context); // Added this line to store the device token
                    }
                }
            }
        });
    }

    /**
     * Adds data to the Firestore database.
     *
     * @param data the data to be added
     * @param ref  the reference to the Firestore document
     */
    private void addSignUpData(Map<String, Object> data, DocumentReference ref, Context context){
        ref.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Signed up!", Toast.LENGTH_SHORT).show();
                        Log.d(ContentValues.TAG, "Event ID added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(ContentValues.TAG, "Error adding event ID", e);
                        Toast.makeText(context, "Could not sign up", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Added this new method to store the device token
    private void storeDeviceToken(String deviceId, String eventId, Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Failed to get device token", task.getException());
                            return;
                        }

                        String deviceToken = task.getResult();

                        // Store the device token in Firestore
                        Map<String, Object> tokenData = new HashMap<>();
                        tokenData.put("eventId", eventId);
                        tokenData.put("deviceToken", deviceToken);

                        db.collection("AttendeeDeviceTokens")
                                .add(tokenData)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "Device token stored successfully");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error storing device token", e);
                                    }
                                });
                    }
                });
    }
}