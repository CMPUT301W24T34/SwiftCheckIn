package com.example.swiftcheckin;
// This is the activity that represents the profile view
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;


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

    private ImageView avatarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        avatarImage = findViewById(R.id.avatarImage);
        Button settingsButton = findViewById(R.id.settings_button);
        Button backButton = findViewById(R.id.back_button);
        db = FirebaseFirestore.getInstance();
        Button editPhotoButton = findViewById(R.id.edit_photo_button);
        editPhotoButton.setOnClickListener(v -> showImagePickerOptions());
        getData();
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    private static final int PERMISSIONS_REQUEST = 100;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int CAPTURE_IMAGE_REQUEST = 102;



    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
    }

    private void getData() {
        // Citation: Getting unique device id, Stack Overflow, License: CC-BY-SA, user name Chintan Rathod, "How to get unique device hardware id in Android?", 2013-06-01, https://stackoverflow.com/questions/16869482/how-to-get-unique-device-hardware-id-in-android
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
                        updateUIWithProfileData(profile);
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
                        if (savedLocation != null && !savedLocation.isEmpty()) {
                            location.setText(savedLocation);
                        }
                    }
                }
            }
        });
    }


    private void showImagePickerOptions() {
        // Check for permissions
        requestPermissions();

        // Options to choose from gallery or take a photo
        CharSequence[] options = {"Choose from Gallery", "Take Photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Profile Photo");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
            } else if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, CAPTURE_IMAGE_REQUEST);
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                Uri selectedImage = data.getData();
                // Handle the selected image from the gallery
                avatarImage.setImageURI(selectedImage);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                // Handle the captured photo
                avatarImage.setImageBitmap(imageBitmap);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerOptions();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void updateUIWithProfileData(Profile profile) {
        String profileName = profile.getName();
        if (profileName != null && !profileName.isEmpty()) {
            AvatarGenerator.generateAvatar(profileName, new AvatarGenerator.AvatarImageCallback() {
                @Override
                public void onAvatarLoaded(Bitmap avatar) {
                    runOnUiThread(() -> avatarImage.setImageBitmap(avatar));
                }
            });
        }
    }
}