package com.example.swiftcheckin.attendee;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

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
}
