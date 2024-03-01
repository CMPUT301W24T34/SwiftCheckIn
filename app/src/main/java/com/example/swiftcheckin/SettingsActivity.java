package com.example.swiftcheckin;
// This activity deals with the settings the user wants
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
/**
 * This deals with the settings activity where users can update their contact information and settings
 */
public class SettingsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText nameEditText;
    private EditText birthdayEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private EditText websiteEditText;
    private EditText addressEditText;
    private CheckBox cameraCheckBox;
    private CheckBox locationCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        nameEditText = findViewById(R.id.name);
        birthdayEditText = findViewById(R.id.birthday);
        phoneNumberEditText = findViewById(R.id.phonenumber);
        emailEditText = findViewById(R.id.email);
        websiteEditText = findViewById(R.id.website);
        addressEditText = findViewById(R.id.address);
        cameraCheckBox = findViewById(R.id.cameraCheckbox);
        locationCheckBox = findViewById(R.id.locationCheckbox);
        Button saveButton = findViewById(R.id.save_button);
        db = FirebaseFirestore.getInstance();
        getData();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user data from EditText fields
                String name = nameEditText.getText().toString();
                String birthday = birthdayEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String website = websiteEditText.getText().toString();
                String address = addressEditText.getText().toString();
                boolean cameraPermission = cameraCheckBox.isChecked();
                boolean locationPermission = locationCheckBox.isChecked();
                saveData(name, birthday, phoneNumber, email, website, address, cameraPermission, locationPermission);
                finish();
            }
        });
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(SettingsActivity.this);
                nameEditText.clearFocus();
                birthdayEditText.clearFocus();
                phoneNumberEditText.clearFocus();
                emailEditText.clearFocus();
                websiteEditText.clearFocus();
                addressEditText.clearFocus();
            }
        });
    }

    private void getData() {
        String deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        DocumentReference profileRef = db.collection("profiles").document(deviceId);

        profileRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (value != null) {
                    Profile profile = value.toObject(Profile.class);

                    if (profile != null) {
                        String savedName = profile.getName();
                        String savedBirthday = profile.getBirthday();
                        String savedPhone = profile.getPhoneNumber();
                        String savedEmail = profile.getEmail();
                        String savedWebsite = profile.getWebsite();
                        String savedAddress = profile.getAddress();
                        boolean savedCameraPermission = profile.isCameraPermission();
                        boolean savedLocationPermission = profile.isLocationPermission();
                        if (savedName != null && !savedName.isEmpty()) {
                            nameEditText.setText(savedName);
                        }
                        if (savedBirthday != null && !savedBirthday.isEmpty()) {
                            birthdayEditText.setText(savedBirthday);
                        }
                        if (savedPhone != null && !savedPhone.isEmpty()) {
                            phoneNumberEditText.setText(savedPhone);
                        }
                        if (savedEmail != null && !savedEmail.isEmpty()) {
                            emailEditText.setText(savedEmail);
                        }
                        if (savedWebsite != null && !savedWebsite.isEmpty()){
                            websiteEditText.setText(savedWebsite);
                        }
                        if (savedAddress != null && !savedAddress.isEmpty()){
                            addressEditText.setText(savedAddress);
                        }
                        cameraCheckBox.setChecked(savedCameraPermission);
                        locationCheckBox.setChecked(savedLocationPermission);
                    }
                }
            }
        });
    }

    private void saveData(String name, String birthday, String phoneNumber, String email, String website, String address, boolean cameraPermission, boolean locationPermission) {
        String deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        HashMap<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("birthday", birthday);
        data.put("phoneNumber", phoneNumber);
        data.put("email", email);
        data.put("website", website);
        data.put("address", address);
        if (cameraPermission){
            data.put("cameraPermission", "True");
        }
        else{
            data.put("cameraPermission", "False");
        }
        if (locationPermission){
            data.put("locationPermission", "True");
        }
        else{
            data.put("locationPermission", "False");
        }

        db.collection("profiles").document(deviceId)
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

    // Citation: How to hide a keyboard, Stack Overflow, License CC-BY-SA, community wiki, "How can I close/hide the Android soft keyboard programmatically?", 2021-03-12, https://stackoverflow.com/questions/1109022/how-can-i-close-hide-the-android-soft-keyboard-programmatically
    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = activity.getCurrentFocus();
        if (v == null) {
            v = new View(activity);
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}