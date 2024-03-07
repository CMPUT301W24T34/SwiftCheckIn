package com.example.swiftcheckin;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnoucementActivity extends AppCompatActivity {

    //    private TextView eventName;
    private Button sign_up;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_announcement);
        db = FirebaseFirestore.getInstance();

        String eventId = getIntent().getStringExtra("eventID");
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Button sign_up = findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(deviceId, eventId);
            }
        });

    }

    private void saveData(String device, String event){
        DocumentReference eventRef = db.collection("SignedUpEvents").document(device);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    if (document.contains(event)) {
                        Toast.makeText(this, "You already signed up for this event", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        addEvent(eventRef, event);
                    }
                }
                else {
                    addEvent(eventRef, event);
                }
            }
            else {
                Log.d(TAG, "Failed", task.getException());
            }
        });
    }

    private void addEvent(DocumentReference eventRef, String event) {
        HashMap<String, Object> data = new HashMap<>();
        data.put(event, event);
        eventRef.set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Sign up successful");
                    Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Sign up failed", e);
                });
    }


}