package com.example.swiftcheckin.attendee;
// This activity deals with the settings the user wants,
// still need more user input validation here for example in the phone number field
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;


import android.Manifest;
import android.widget.Toast;

import com.example.swiftcheckin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.regex.Pattern;

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
    private CheckBox locationCheckBox;
    private static final int LOCATION_PERMISSION = 1001;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String deviceId;

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
        locationCheckBox = findViewById(R.id.locationCheckbox);
        Button saveButton = findViewById(R.id.save_button);
        db = FirebaseFirestore.getInstance();
        deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        getData();
        // Citation: OpenAI, 03-29-2024, ChatGPT, How to set a listener for the location checkbox
        // output was below, the onCheckedChangeListener and onCheckedChanged Method
        locationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getLocationPermission();
                }
            }
        });

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
                boolean locationPermission = locationCheckBox.isChecked();
                if (!name.equals("") && isValid(birthday)) {
                    saveData(name, birthday, phoneNumber, email, website, address, locationPermission);
                }
                if (!name.equals("") && !isValid(birthday)) {
                    saveData(name, "", phoneNumber, email, website, address, locationPermission);
                }

                finish();
            }
        });
        // Citation: How to clear focus, Stack Overflow, License: CC-BY-SA, user name xtr, "Android: Force EditText to remove focus? [duplicate]", 2011-05-25, https://stackoverflow.com/questions/5056734/android-force-edittext-to-remove-focus
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
    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = activity.getCurrentFocus();
        if (v == null) {
            v = new View(activity);
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private boolean isValid(String birthday) {
        if (birthday.equals("")) {
            return true;
        } else {
            String pattern = "\\d{2}/\\d{2}/\\d{4}";
            return Pattern.matches(pattern, birthday);
        }
    }


    /**
     * This gets the profile data from firestore
     */
    private void getData() {
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
                        locationCheckBox.setChecked(savedLocationPermission);
                    }
                }
            }
        });
    }

    /**
     * This saves the data to firestore
     * @param name - string of name
     * @param birthday - string of birthday
     * @param phoneNumber - string of phone number
     * @param email - string of email
     * @param website - string of website
     * @param address - string of address
     * @param locationPermission - boolean of whether location permission is on or not
     */
    private void saveData(String name, String birthday, String phoneNumber, String email, String website, String address, boolean locationPermission) {
        HashMap<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("birthday", birthday);
        data.put("phoneNumber", phoneNumber);
        data.put("email", email);
        data.put("website", website);
        data.put("address", address);

        if (locationPermission) {
            data.put("locationPermission", "True");
            saveToFirebase(deviceId, data);
            getLocation(deviceId);

        }
        else {
            data.put("locationPermission", "False");
            data.put("latitude", "Unknown");
            data.put("longitude", "Unknown");
            saveToFirebase(deviceId, data);
        }
    }

    private void saveToFirebase(String deviceId, HashMap<String, String> data) {
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

    // Citation: OpenAI, 03-29-2024, ChatGPT, How to obtain location of user
    // output was to use a location manager and listener, gave me the onLocationChanged, onProviderDisabled, onProviderEnabled, onStatusChanged methods
    // also gave the checking of permissions
    private void getLocation(String deviceId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                Toast.makeText(SettingsActivity.this, "Location data received", Toast.LENGTH_SHORT).show();
                // Citation: OpenAI, 03-29-2024, ChatGPT, How to ensure that the updates don't keep coming
                // output was locationManager.removeUpdates(this);
                // Remove updates after first one
                locationManager.removeUpdates(this);
                // Save location data to Firebase
                HashMap<String, Object> data = new HashMap<>();
                data.put("latitude", String.valueOf(latitude));
                data.put("longitude", String.valueOf(longitude));
                updateLocationInfo(deviceId, data);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.d("Location", "Provider disabled: " + provider);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                Log.d("Location", "Provider enabled: " + provider);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Location", "Provider status changed: " + provider);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, locationListener);
    }

    // Citation: OpenAI, 03-29-2024, ChatGPT, How to ask user for location permission
    // output is getLocationPermission and onRequestPermissionsResult functions below
    private void getLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            }
            else {
                // Permission denied
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                        !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // Tell user to go to settings
                    settingsDialog();
                }

            }
        }
    }
    // Citation: OpenAI, 03-29-2024, ChatGPT, How to transfer user to settings
    // output is this function below, creating the dialog, and setting the buttons
    private void settingsDialog() {
        locationCheckBox.setChecked(false);
        AlertDialog.Builder popup = new AlertDialog.Builder(this);
        popup.setTitle("Permission Required");
        popup.setMessage("Location permission is required for this feature. Please enable it in the app settings.");

        popup.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open app settings
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        popup.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update firebase

                HashMap<String, Object> data = new HashMap<>();
                data.put("locationPermission", "False");
                data.put("latitude", "Unknown");
                data.put("longitude", "Unknown");
                updateLocationInfo(deviceId, data);

                dialog.dismiss();
            }
        });
        AlertDialog dialog = popup.create();
        dialog.show();
    }

    private void updateLocationInfo(String deviceId, HashMap<String, Object> data){
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


}