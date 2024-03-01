package com.example.swiftcheckin;
// This is the activity that represents the profile view
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;

/**
 * This deals with the user's profile information
 */
public class ProfileActivity extends AppCompatActivity {
    private TextView nameText;
    private TextView phoneNumber;
    private TextView email;
    private TextView location;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Button settingsButton = findViewById(R.id.settings_button);
        db = FirebaseFirestore.getInstance();
        getData();
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);

            }
        });
    }


    private void getData() {
        // Citation: Getting unique device id, Stack Overflow, License CC-BY-SA, user name Chintan Rathod, "How to get unique device hardware id in Android?", 2013-06-01, https://stackoverflow.com/questions/16869482/how-to-get-unique-device-hardware-id-in-android
        String deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        nameText = findViewById(R.id.nameText);
        phoneNumber = findViewById(R.id.phoneNumberText);
        email = findViewById(R.id.emailText);
        location = findViewById(R.id.locationText);
        DocumentReference userRef = db.collection("profiles").document(deviceId);
        // Citation: Collecting a document from firebase, Stack Overflow, License: CC-BY-SA, user name Frank van Puffelen, "How to fix a null object reference on document snapshot, 2022-04-28, https://stackoverflow.com/questions/72042682/how-to-fix-a-null-object-reference-on-document-snapshot
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (value != null) {
                    // Citation: Converting document to class object, Stack Overflow, License: CC-BY-SA, user name Matheus Padovani (edited by Doug Stevenson), "How to directly convert Data Snapshot to object?", 2020-06-17 (edited 2020-06-17), https://stackoverflow.com/questions/62436421/how-to-directly-convert-data-snapshot-to-object
                    Profile profile = value.toObject(Profile.class);

                    if (profile != null) {
                        String savedName = profile.getName();
                        String savedPhone = profile.getPhoneNumber();
                        String savedEmail = profile.getEmail();
                        String savedLocation = profile.getAddress();
                        if (savedName != null && !savedName.isEmpty()) {
                            nameText.setText(savedName);
                        }
                        if (savedPhone != null && !savedPhone.isEmpty()) {
                            phoneNumber.setText(savedPhone);
                        }
                        if (savedEmail != null && !savedEmail.isEmpty()) {
                            email.setText(savedEmail);
                        }
                        if (savedLocation != null && !savedLocation.isEmpty()){
                            location.setText(savedLocation);
                        }
                    }
                }
            }
        });
    }

}