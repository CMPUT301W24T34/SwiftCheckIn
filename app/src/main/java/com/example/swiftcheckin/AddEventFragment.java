package com.example.swiftcheckin;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class AddEventFragment extends DialogFragment {

    // Amanvir's code usage
    private static final int PERMISSIONS_REQUEST = 100;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int CAPTURE_IMAGE_REQUEST = 102;

    Event event = new Event();

    private String deviceId;
    ActivityResultLauncher<Intent> eventPosterLauncher;
    Uri imageUri;

    ImageView editEventPoster;

    interface AddEventDialogListener{
        void addEvent(Event event);
    }

    public AddEventFragment(String deviceId){
        this.deviceId = deviceId;
    }
    public AddEventFragment(){
    }

    private AddEventDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEventDialogListener){
            listener = (AddEventDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddEventDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_event, null);
//        registerResult();

        editEventPoster = view.findViewById(R.id.edit_event_poster_image);
        editEventPoster.setOnClickListener(v -> showImagePickerOptions());   // Amanvir's code


        EditText editEventTitle = view.findViewById(R.id.edit_event_title_text);
        EditText editDescriptionText = view.findViewById(R.id.edit_event_description_text);
        EditText editEventLocation = view.findViewById(R.id.edit_event_location_text);
//        EditText editMaxAttendees = view.findViewById(R.id.edit_max_attendees_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add an Event")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Next", (dialog, which) ->{
                    String eventTitle = editEventTitle.getText().toString();
                    String descriptionText = editDescriptionText.getText().toString();
                    String eventLocation = editEventLocation.getText().toString();
                    event.setEventTitle(eventTitle);
                    event.setDescription(descriptionText);
                    event.setLocation(eventLocation);
                    event.setEventPoster(imageUri);
                    event.setDeviceId(deviceId);
//                    String maxAttendees = editMaxAttendees.getText().toString();
//                    listener.addEvent(new Event(eventTitle, descriptionText, eventLocation, imageUri, deviceId));
                    listener.addEvent(event);

                })
                .create();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
    }


    private void showImagePickerOptions() {
        // Check for permissions
        requestPermissions();

        // Options to choose from gallery or take a photo
        CharSequence[] options = {"Choose from Gallery", "Take Photo"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
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
                imageUri = getImageUri(getContext(), imageBitmap);
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
        String userId = deviceId; // Implement this method to retrieve the user's ID.

        // Create a reference to 'eventImages/userId.jpg'
        // Need a way to uniquely determine the event image.
        StorageReference eventImageRef = FirebaseStorage.getInstance()
                .getReference("eventImages/" + deviceId + ".jpg");   // Need to add event identifier

        // Upload file to Firebase Storage
        eventImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // When the image has successfully uploaded, get its download URL
                    eventImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Save the download URL to the Firestore document for the user
//                        Toast.makeText(getContext(), "Save imageURL", Toast.LENGTH_SHORT).show();
                        event.setEventImageUrl(downloadUri.toString());
                        Glide.with(getActivity())
                                .load(downloadUri.toString())
                                .into(editEventPoster);
                        Toast.makeText(getContext(), "Saved imageURL into Event", Toast.LENGTH_SHORT).show();
//                        saveEventImageUrlToFirestore(downloadUri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Log.e("OrganizerActivity", "Image upload failed", e);
                });
    }

    // I can upload the event image url to firestore
























    //  https://www.youtube.com/watch?v=nOtlFl1aUCw

//    private void registerResult(){
//        eventPosterLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult o) {
//                        if (o.getResultCode() == RESULT_OK && o.getData() != null) {
//                            try {
//                                imageUri = o.getData().getData();
//                                editEventPoster.setImageURI(imageUri);
//                            } catch (Exception e) {
//                                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );
//    }
//    private void pickImage(){
//        // Enables us to launch activity with an intent to select images.
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        eventPosterLauncher.launch(intent);
//
//    }



}
