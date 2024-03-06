package com.example.swiftcheckin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class AddEventActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 100;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int CAPTURE_IMAGE_REQUEST = 102;
    ImageView editEventPoster;
    Uri imageUri;

    private String eventTitle;
    private String eventDate;
    private String eventEndTime;
    private String eventAmPm;
    private String eventStartTime;
    Event event = new Event(eventTitle, "frfr", "grgrf", "fre", null, null, eventDate, null, eventStartTime, eventEndTime, eventAmPm);
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        deviceId = getIntent().getStringExtra("deviceId");

        // making button onclick listeners
        Button cancelButton = findViewById(R.id.eventPageCancelButton);
        Button saveButton = findViewById(R.id.eventPageSaveButton);
        editEventPoster = findViewById(R.id.eventImage);


        editEventPoster.setOnClickListener(v -> showImagePickerOptions());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAddEvent();
            }
        });

        // textWatcher to enforce proper Date format in start and end date
        EditText startDateEditText = findViewById(R.id.eventAddActivity_StartDate_EditText);
        EditText endDateEditText = findViewById(R.id.eventAddActivity_eventEndDate_EditText);

        createTextWatcher(startDateEditText);
        createTextWatcher(endDateEditText);
    }

    private void createTextWatcher(EditText editText)
    {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            // enforcing a few parameters in XML, and adding '/' at proper indexes
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2 || s.length() == 5) {
                    s.append("/");
                }
            }
        });
    }

    protected void editAddEvent()
    {
        EditText eventNameEditText = findViewById(R.id.eventName);
        String eventName = eventNameEditText.getText().toString();

        EditText addressEditText = findViewById(R.id.eventPageAddressEditText);
        String eventAddress = addressEditText.getText().toString();

        EditText descriptionEditText = findViewById(R.id.eventPageDescriptionEditText);
        String eventDescription = descriptionEditText.getText().toString();

        Toast.makeText(AddEventActivity.this, "Going to add event", Toast.LENGTH_SHORT).show();

        EditText eventStartDateEditText = findViewById(R.id.eventAddActivity_StartDate_EditText);
        String eventStartDate = eventStartDateEditText.getText().toString();

        EditText eventEndDateEditText = findViewById(R.id.eventAddActivity_eventEndDate_EditText);
        String eventEndDate = eventEndDateEditText.getText().toString();

        EditText eventStartTimeEditText = findViewById(R.id.eventAddActivity_eventStartTime_EditText);
        String eventStartTime = eventStartTimeEditText.getText().toString();

        EditText eventEndTimeEditText = findViewById(R.id.eventAddActivity_eventEndTime_EditText);
        String eventEndTime = eventEndTimeEditText.getText().toString();

        Intent intent = new Intent("com.example.ADD_EVENT");
        intent.putExtra("eventName", eventName);
        intent.putExtra("eventAddress", eventAddress);
        intent.putExtra("eventPosterURL", event.getEventImageUrl());
        intent.putExtra("eventStartDate", eventStartDate);
        intent.putExtra("eventEndDate", eventEndDate );
        intent.putExtra("eventStartTime", eventStartTime);
        intent.putExtra("eventEndTime", eventEndTime);
        intent.putExtra("eventDescription", eventDescription);

        sendBroadcast(intent);
        finish();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
    }

    private void showImagePickerOptions() {
        // Check for permissions
        requestPermissions();

        // Options to choose from gallery or take a photo
        CharSequence[] options = {"Choose from Gallery", "Take Photo"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageUri = null;
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                imageUri = data.getData();
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUri(getApplicationContext(), imageBitmap);
            }

            if (imageUri != null) {
                uploadImageToFirebaseStorage(imageUri, event);
            }
        }
    }


    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void uploadImageToFirebaseStorage(Uri imageUri, Event event) {
        // Ensure you have a valid userId before using it in the file path
        String userId = getUserId(); // Implement this method to retrieve the user's ID.

        // Create a reference to 'eventImages/userId.jpg'
        // Need a way to uniquely determine the event image.
        StorageReference eventImageRef = FirebaseStorage.getInstance()
                .getReference("eventImages/" + userId + ".jpg");   // Need to add event identifier

        // Upload file to Firebase Storage
        eventImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // When the image has successfully uploaded, get its download URL
                    eventImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Save the download URL to the Firestore document for the user
                        event.setEventImageUrl(downloadUri.toString());
                        Glide.with(AddEventActivity.this)
                                .load(downloadUri.toString())
                                .into(editEventPoster);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Log.e("OrganizerActivity", "Image upload failed", e);
                });
    }

    private String getUserId() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
