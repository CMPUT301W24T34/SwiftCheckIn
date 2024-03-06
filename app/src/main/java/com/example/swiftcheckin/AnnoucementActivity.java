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

        Toast.makeText(this, "fdsfdsfds", Toast.LENGTH_SHORT).show();
        String eventId = getIntent().getStringExtra("eventID");
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Button sign_up = findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveData(deviceId, eventId);
            }
        });

    }

    public void saveData(String deviceId, String eventId) {
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
                        }
                        else{ // they've already signed up for this event
                            data.put("eventIds", eventIds);
                            Toast.makeText(AnnoucementActivity.this, "You are already signed up for this event", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // no list yet
                    else {
                        List<String> eventIds = new ArrayList<>();
                        eventIds.add(eventId);
                        data.put("eventIds", eventIds); // Initialize the data map
                    }

                    // Set or update the document with the new data
                    ref.set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AnnoucementActivity.this, "Signed up!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Event ID added successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding event ID", e);
                                    Toast.makeText(AnnoucementActivity.this, "Could not sign up", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {
                    Exception exception = task.getException();
                    if(exception != null) {
                        Log.e(TAG, "Error retrieving document", exception);
                        Toast.makeText(AnnoucementActivity.this, "Error retrieving document: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

}
