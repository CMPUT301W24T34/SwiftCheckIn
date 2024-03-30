package com.example.swiftcheckin.organizer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class Firebase_organizer {
    private FirebaseFirestore db;
    String deviceId;

    public Firebase_organizer(Context context)
    {
        this.db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    public void addQrCode(Qr_Code qrcode)
    {
        DocumentReference deviceRef = db.collection("qrcodes").document(qrcode.getQrID());
        HashMap<String, String> data = new HashMap<>();
        data.put("QrID", qrcode.getQrID());
        data.put("eventID", qrcode.getEventID());
        data.put("ImageBase64String", QrCodeManager.convertToBase64String(qrcode.getImage()));
        data.put("isPromo", qrcode.getIsPromo().toString());
        deviceRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Qrcode Added Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "QrCode could not be added.");
                    }
                });
    }

    public void saveEvent(Event event)
    {
        // Ensure we have 3 columns in firestore for simple reference.
        // This is to ensure admin will be able to delete any events much more conveniently.
        DocumentReference deviceRef = db.collection("events").document(deviceId+event.getEventTitle());
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
        data.put("qrID", event.getQrID());
        // Sets the data to Firebase.
        deviceRef
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {   // In the event, the event is added.
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Event Added Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener(){ // In the event, the event fails to be added to Firebase.
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
                        if(task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                String qrID = document.getString("qrID");
                                Log.e("Qr Code Id requested for fragment", qrID);
                                callback.onQrIDReceived(qrID);
                            }
                        } else {
                            Log.e("Error in fetching qr Id for eventID", "Error - event: "+eventId);
                            callback.onQrIDReceived(null); // Signal failure
                        }
                    }
                });
    }

}
