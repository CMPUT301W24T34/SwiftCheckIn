package com.example.swiftcheckin.organizer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Firebase_organizer {
    private FirebaseFirestore db;

    public Firebase_organizer()
    {
        this.db = FirebaseFirestore.getInstance();
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
}
