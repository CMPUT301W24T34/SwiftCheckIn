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
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;


import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swiftcheckin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * This deals with the settings activity where users can update their contact information and settings
 */
public class SettingsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText nameEditText;
    private TextView birthdayEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private EditText websiteEditText;
    private EditText addressEditText;
    private CheckBox locationCheckBox;
    // Citation: OpenAI, 03-29-2024, ChatGPT, How to get location of user
    // said to use the following 3 lines
    private static final int LOCATION_PERMISSION = 1001;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String deviceId;
    private LocationReceiver locationReceiver;
    private FirebaseAttendee fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        locationReceiver = new LocationReceiver();
        nameEditText = findViewById(R.id.name);
        birthdayEditText = findViewById(R.id.birthday);
        phoneNumberEditText = findViewById(R.id.phonenumber);
        emailEditText = findViewById(R.id.email);
        websiteEditText = findViewById(R.id.website);
        addressEditText = findViewById(R.id.address);
        locationCheckBox = findViewById(R.id.locationCheckbox);
        Button saveButton = findViewById(R.id.save_button);
        deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        fb = new FirebaseAttendee();
        db = fb.getDb();
        locationReceiver = new LocationReceiver();
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


        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDatePicker(birthdayEditText);
            }
        });

        // Citation: OpenAI, 04-07-2024, ChatGPT, How to change view to front of text when focus is gone
        // output is functions below, the setOnFocusChangeListener and onFocusChange method
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    emailEditText.setSelection(0);
                }
            }
        });
        addressEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    addressEditText.setSelection(0);
                }
            }
        });
        websiteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    websiteEditText.setSelection(0);
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
                if (!name.equals("") && isValid(phoneNumber)) {
                    saveData(name, birthday, phoneNumber, email, website, address, locationPermission);
                    finish();
                }
                else if (!name.equals("") && !isValid(phoneNumber)){
                    invalidPhoneDialog();
                }
                else if (name.equals("")){
                    invalidNameDialog();
                }
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
    // Citation: How to hide a keyboard, Stack Overflow, License: CC-BY-SA, community wiki, "How can I close/hide the Android soft keyboard programmatically?", 2021-03-12, https://stackoverflow.com/questions/1109022/how-can-i-close-hide-the-android-soft-keyboard-programmatically

    /**
     * This hides the built in keyboard
     * @param activity - the activity
     */
    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = activity.getCurrentFocus();
        if (v == null) {
            v = new View(activity);
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    // Citation: OpenAI, 03-05-2024, ChatGPT, Checking if string matches pattern
    // gave me output of this "\\d{2}/\\d{2}/\\d{4}" and told me to use Pattern.matches(pattern, birthday);
    // (birthday has been changed to phone for this method)
    /**
     * This checks if the inputted phone number is of valid format
     * @param phone - string to check
     * @return
     * returns boolean of whether its valid
     */
    private boolean isValid(String phone) {
        if (phone.equals("")) {
            return true;
        } else {
            String pattern = "\\d{3}-\\d{3}-\\d{4}";
            return Pattern.matches(pattern, phone);
        }
    }


    /**
     * This gets the profile data from firestore
     */
    private void getData() {
        fb.getProfileData(deviceId, new FirebaseAttendee.GetProfileCallback() {
            @Override
            public void onProfileFetched(Profile profile) {
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
                    if (savedWebsite != null && !savedWebsite.isEmpty()) {
                        websiteEditText.setText(savedWebsite);
                    }
                    if (savedAddress != null && !savedAddress.isEmpty()) {
                        addressEditText.setText(savedAddress);
                    }
                    locationCheckBox.setChecked(savedLocationPermission);
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
            fb.saveProfileToFirebase(deviceId, data);
            // Citation: OpenAI, 04-06-2024, ChatGPT, How to get context to use in this call
            // gave me getApplicationContext()
            locationReceiver.getLocation(deviceId, getApplicationContext());

        }
        else {
            data.put("locationPermission", "False");
            data.put("latitude", "Unknown");
            data.put("longitude", "Unknown");
            fb.saveProfileToFirebase(deviceId, data);
        }
    }

    // Citation: OpenAI, 03-29-2024, ChatGPT, How to ask user for location permission
    // output is getLocationPermission and onRequestPermissionsResult functions below

    /**
     * gets location permission
     */
    private void getLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION);
    }

    /**
     * calls on the settings dialog if permission is denied
     * @param requestCode The request code passed in {@link # requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            }
            else {
                // Permission denied
                settingsDialog();

            }
        }
    }
    // Citation: OpenAI, 03-29-2024, ChatGPT, How to transfer user to settings
    // output is this function below, also creating the dialog, and setting the buttons

    /**
     * If a user denies location permission, they are prompted to go to settings to enable it
     */
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
                fb.updateLocationInfo(deviceId, data);

                dialog.dismiss();
            }
        });
        AlertDialog dialog = popup.create();
        dialog.show();
    }

    /**
     * dialog for when phone number doesn't follow format
     */
    private void invalidPhoneDialog() {
        AlertDialog.Builder popup = new AlertDialog.Builder(this);
        popup.setTitle("Invalid Phone Number");
        popup.setMessage("Phone Number must be in the format XXX-XXX-XXXX");

        popup.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = popup.create();
        dialog.show();
    }

    /**
     * dialog for when name is not entered
     */
    private void invalidNameDialog() {
        AlertDialog.Builder popup = new AlertDialog.Builder(this);
        popup.setTitle("Invalid Name");
        popup.setMessage("Please enter your name to proceed.");

        popup.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = popup.create();
        dialog.show();
    }

    /**
     * Gets a calendar for birthday and sets user choice to the field
     * @param editText - the birthday textview that gets updated
     */
    private void initDatePicker(TextView editText)
    {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthWord = monthNames[month];
                String selectedDate = monthWord +" " + dayOfMonth + " " + year;
                editText.setText(selectedDate);
            }
        }, 2000, 0,
                1);
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        datePicker.show();
    }



}