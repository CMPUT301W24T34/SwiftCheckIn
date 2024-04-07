package com.example.swiftcheckin.attendee;
// This is the activity that represents the profile view
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;


import com.bumptech.glide.Glide;
import com.example.swiftcheckin.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;

/**
 * This class deals with the profile view of the user and various functionalities related to it
 */
public class ProfileActivity extends AppCompatActivity {
    private TextView nameText;
    private TextView phoneNumber;
    private TextView email;
    private TextView location;
    private FirebaseFirestore db;

    private ImageView avatarImage;
    private FirebaseAttendee fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        avatarImage = findViewById(R.id.avatarImage);
        Button settingsButton = findViewById(R.id.settings_button);
        Button backButton = findViewById(R.id.back_button);
        fb = new FirebaseAttendee();
        db = fb.getDb();
        Button editPhotoButton = findViewById(R.id.edit_photo_button);
        editPhotoButton.setOnClickListener(v -> showImagePickerOptions());
        nameText = findViewById(R.id.nameText);
        phoneNumber = findViewById(R.id.phoneNumberText);
        email = findViewById(R.id.emailText);
        location = findViewById(R.id.locationText);
        getData();


        Button removeButton = findViewById(R.id.removeButton);
        removeButton.setOnClickListener(v -> removePhoto());
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

    /**
     * This requests permissions for the camera and external storage
     * Citation: Requesting permissions. Android Developers , https://developer.android.com/training/permissions/requesting#java
     */
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
    }

    /**
     * This gets the profile data from firestore
     */
    private void getData() {
        // Citation: Getting unique device id, Stack Overflow, License: CC-BY-SA, user name Chintan Rathod, "How to get unique device hardware id in Android? [duplicate]", 2013-06-01, https://stackoverflow.com/questions/16869482/how-to-get-unique-device-hardware-id-in-android
        String deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        fb.getProfileData(deviceId, new FirebaseAttendee.GetProfileCallback() {
            @Override
            public void onProfileFetched(Profile profile) {
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
                    else{
                        phoneNumber.setText("Phone");
                    }
                    if (savedEmail != null && !savedEmail.isEmpty()) {
                        email.setText(savedEmail);
                    }
                    else{
                        email.setText("Email");
                    }
                    if (savedLocation != null && !savedLocation.isEmpty()) {
                        location.setText(savedLocation);
                    }
                    else{
                        location.setText("Address");
                    }
                }
            }
        });
    }


    /**
     * This shows the options to choose from gallery or take a photo
     */

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
            Uri imageUri = null;
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                imageUri = data.getData();
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUri(getApplicationContext(), imageBitmap);
            }

            if (imageUri != null) {
                uploadImageToFirebaseStorage(imageUri);
            }
        }
    }

    /**
     * This gets the image uri
     * @param inContext
     * @param inImage
     * @return the uri of the image
     */
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    /** Uploads an image to Firebase Storage under the path 'profileImages/{userId}.jpg'.
     * This method first retrieves the user's unique ID to use as part of the storage path.
     * Upon successful upload, it retrieves the download URL of the uploaded image and save this URL in Firestore.
     * If the upload fails, it logs an error message.
     *
     * @param imageUri The URI of the image to be uploaded. This should not be null and must
     *                 point to a valid image file.
     */
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        String userId = getUserId(); // Implement this method to retrieve the user's ID.

       // Create a storage reference from our app
        StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("profileImages/" + userId + ".jpg");

        // Upload file to Firebase Storage
        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // When the image has successfully uploaded, get its download URL
                    profileImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        saveProfileImageUrlToFirestore(downloadUri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Log.e("ProfileActivity", "Image upload failed", e);
                });
    }

    /**
     * This saves the profile image url to firestore
     * @param imageUrl
     */
    private void saveProfileImageUrlToFirestore(String imageUrl) {
        String deviceId = getUserId(); // Retrieve device/user ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles").document(deviceId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Image URL saved to Firestore, now load it into the ImageView
                    // Citation : https://www.youtube.com/watch?v=xrVD7LcQ5nY
                    Glide.with(ProfileActivity.this)
                            .load(imageUrl)
                            .into(avatarImage);
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileActivity", "Error saving image URL to Firestore", e);
                });
    }


    /**
     * This gets the user's id
     * @return the user's id
     */
    private String getUserId() {
        return Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
    }


    /**
     * Handles the request permission result
     * @param requestCode The request code passed in requestPermissions
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerOptions();
            }
            // else {
            // Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        //}
        }
    }




    /**
     * This updates the UI with the profile data
     * @param profile
     */
    private void updateUIWithProfileData(Profile profile) {
        // Load the profile image URL into the ImageView
        String profileImageUrl = profile.getProfileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            // If URL is not empty, load it with Glide or your chosen image loading library.
            Glide.with(this).load(profileImageUrl).into(avatarImage);
        } else {
            // If no URL, then generate the avatar image.
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


    /**
     * This method is used for removing profile image from the storage as well as the app
     */
    private void removePhoto() {
        String userId = getUserId();
        StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("profileImages/" + userId + ".jpg");
        profileImageRef.delete().addOnSuccessListener(aVoid -> {
            // File deleted successfully
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("profiles").document(userId)
                    .update("profileImageUrl", "")
                    .addOnSuccessListener(aVoid1 -> {
                        // Now as no profile image is there, generate the avatar image.
                        String profileName = nameText.getText().toString();
                        if (profileName != null && !profileName.isEmpty()) {
                            AvatarGenerator.generateAvatar(profileName, new AvatarGenerator.AvatarImageCallback() {
                                @Override
                                public void onAvatarLoaded(Bitmap avatar) {
                                    runOnUiThread(() -> avatarImage.setImageBitmap(avatar));
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileActivity", "Error removing image URL from Firestore", e);
                    });
        }).addOnFailureListener(e -> {
            Log.e("ProfileActivity", "Error removing image from Storage", e);
        });
    }




}
