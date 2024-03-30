package com.example.swiftcheckin.organizer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.swiftcheckin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.Reference;
import java.util.HashMap;

public class AddAnnouncementActivity extends AppCompatActivity {

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);

        eventId = getIntent().getStringExtra("eventId");
        Button cancelAnnouncement = findViewById(R.id.addAnnouncementCancelButton);
        Button saveAnnouncement = findViewById(R.id.addAnnouncementSaveButton);
        EditText editAnnouncementHeading = findViewById((R.id.announcementEditHeading));
        EditText editAnnouncementDes = findViewById(R.id.announcementEditDes);

        cancelAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnnouncementToFirebase();
                Toast.makeText(getApplicationContext(), "To be continued", Toast.LENGTH_SHORT).show();
                finish();
            }
        });



    }

    public void saveAnnouncementToFirebase(){
        // 1. Get the edittexts here
        EditText editAnnouncementHeading = findViewById((R.id.announcementEditHeading));
        String announcementHeading = editAnnouncementHeading.getText().toString();

        EditText editAnnouncementDes = findViewById(R.id.announcementEditDes);
        String announcementDes = editAnnouncementDes.getText().toString();
        // 2. Get the instance of Firebase here
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 3. Create a collection and create announcement object.

        DocumentReference eventWithAnnounce = db.collection("Announcements").document(eventId + announcementHeading);

        HashMap<String, String> data = new HashMap<>();
        data.put("announcementTitle", announcementHeading);
        data.put("announcementDes", announcementDes);

        eventWithAnnounce
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {   // In the event, the event is added.
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Announcement Added Successfully");
                        Toast.makeText(AddAnnouncementActivity.this, "Announcement Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener(){ // In the event, the event fails to be added to Firebase.
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Announcement could not be added.");
                        Toast.makeText(AddAnnouncementActivity.this, "Announcement could not be added.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}