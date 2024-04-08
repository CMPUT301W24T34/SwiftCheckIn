package com.example.swiftcheckin.organizer;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.swiftcheckin.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Locale;

/**
 * Activity meant to help create events for attendees to join. Starts from the organizer activity and
 * has the QR Code generation.
 */
public class AddEventActivity extends AppCompatActivity implements FragmentQrcodeMenu1.AddActivity {
    // Implements QR code generation.
    // Requests for device permissions regarding images and cameras.
    private static final int PERMISSIONS_REQUEST = 100;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int CAPTURE_IMAGE_REQUEST = 102;

    Boolean qrGenerated = false;
    ImageView editEventPoster;   // Meant to help set the event poster.
    Uri imageUri;   // Uri of the event poster.

    Event event = new Event();  // Made specifically to store any details related to events.
    private String deviceId;  // Represents the device Id of the device that will create the event.
    Intent broadcastIntent;

    /**
     * Sets up how this activity will start.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);   // This the xml the activity is connected to.

        deviceId = getIntent().getStringExtra("deviceId");   // Retrieves the device Id from the Organizer Activity.

        // making button onclick listeners
        Button cancelButton = findViewById(R.id.eventPageCancelButton);
        Button saveButton = findViewById(R.id.eventPageSaveButton);
        editEventPoster = findViewById(R.id.eventImage);

        // In order to set the event poster.
        editEventPoster.setOnClickListener(v -> showImagePickerOptions());

        // Cancels the creation of the event.
        initCancelButton(cancelButton);

        // Saves the event.
        initSaveButton(saveButton);

        // DatePicker dialog
        TextView startDateEditText = findViewById(R.id.eventAddActivity_StartDate_EditText);
        TextView endDateEditText = findViewById(R.id.eventAddActivity_eventEndDate_EditText);

        initDatePickerClick(startDateEditText);
        initDatePickerClick(endDateEditText);

        // TimePicker
        TextView startTime = findViewById(R.id.eventAddActivity_eventStartTime_EditText);
        TextView endTime = findViewById(R.id.eventAddActivity_eventEndTime_EditText);

        initTimePickerClick(startTime);
        initTimePickerClick(endTime);

    }

    /**
     * Broadcasts the intent
     * @param flag - Boolean parameter which dictates whether a broadcast should be sent or not.
     */
    public void setGeneratedFlag(Boolean flag, String qrcodeID, String promotionalQrID)
    {
        this.qrGenerated = flag;
        if(this.qrGenerated) {
            // Full citation provided in OrganizerActivity
            broadcastIntent.putExtra("qrCodeID", qrcodeID);
            broadcastIntent.putExtra("promoQrID", promotionalQrID);
            sendBroadcast(broadcastIntent);
            finish();
        }
    }

    /**
     * Initializes trigger for launching TimePicker Dialog
     * @param timeView TextView which acts as a trigger on click
     */
    private void initTimePickerClick(TextView timeView)
    {
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker(timeView);
            }
        });
    }


    /**
     * Initializes trigger for launching DatePicker Dialog
     * @param textView TextView which acts as a trigger on click
     */
    private void initDatePickerClick(TextView textView)
    {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDatePicker(textView);
            }
        });

    }

    /**
     * Initializes save button
     * @param button triggers event save procedure on click
     */
    private void initSaveButton(Button button)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAddEvent();
            }
        });
    }

    /**
     * Initializes Cancel Button
     * @param button Cancels the event on click
     */
    private void initCancelButton(Button button)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Citation: Code with Cal, Youtube. Pop Up Date Picker Android Studio Tutorial. Accessed March 30, 2024.
    //Link: https://www.youtube.com/watch?v=qCoidM98zNk

    /**
     * Creates a DatePicker Dialog
     * @param editText Trigger for the creation of DatePicker Dialog
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
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePicker.show();
    }

    /**
     * Creates a TimePicker Dialog
     * @param timeView Trigger for the creation of TimePicker Dialog
     */
    private void initTimePicker(TextView timeView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                timeView.setText(selectedTime);

            }
        }, hour, currentMinute, true);
        timePicker.show();
    }

    /**
     * Retrieves the info the user inputted to send to the organizer activity by calling FragmentQrcodeMenu1
     * Else it will record an error.
     */
    protected void editAddEvent()
    {

        // Information entered by the user.
        EditText eventNameEditText = findViewById(R.id.eventName);
        String eventName = eventNameEditText.getText().toString().trim();

        EditText addressEditText = findViewById(R.id.eventPageAddressEditText);
        String eventAddress = addressEditText.getText().toString().trim();

        EditText descriptionEditText = findViewById(R.id.eventPageDescriptionEditText);
        String eventDescription = descriptionEditText.getText().toString().trim();

        TextView eventStartDateEditText = findViewById(R.id.eventAddActivity_StartDate_EditText);
        String eventStartDate = eventStartDateEditText.getText().toString();

        TextView eventEndDateEditText = findViewById(R.id.eventAddActivity_eventEndDate_EditText);
        String eventEndDate = eventEndDateEditText.getText().toString();

        TextView eventStartTimeEditText = findViewById(R.id.eventAddActivity_eventStartTime_EditText);
        String eventStartTime = eventStartTimeEditText.getText().toString();

        TextView eventEndTimeEditText = findViewById(R.id.eventAddActivity_eventEndTime_EditText);
        String eventEndTime = eventEndTimeEditText.getText().toString();

        EditText eventMaxAttendeesEditText = findViewById(R.id.editMaxAttendeeText);
        String eventMaxAttendee = eventMaxAttendeesEditText.getText().toString().trim();

        broadcastIntent = new Intent("com.example.ADD_EVENT");
        // Puts all the information into broadcastIntent.
        broadcastIntent.putExtra("eventName", eventName);
        broadcastIntent.putExtra("eventAddress", eventAddress);
        broadcastIntent.putExtra("eventPosterURL", event.getEventImageUrl());
        broadcastIntent.putExtra("eventStartDate", eventStartDate);
        broadcastIntent.putExtra("eventEndDate", eventEndDate );
        broadcastIntent.putExtra("eventStartTime", eventStartTime);
        broadcastIntent.putExtra("eventEndTime", eventEndTime);
        broadcastIntent.putExtra("eventDescription", eventDescription);
        broadcastIntent.putExtra("eventMaxAttendees", eventMaxAttendee);

        // Bitmap qr = QrCodeManager.generateQRCode(deviceId + eventName);  // generates the QR code.

        if (!eventName.equals("") && !eventAddress.equals("") && !eventStartDate.equals("") && !eventEndDate.equals("") && !eventStartTime.equals("") && !eventEndTime.equals("") && !eventDescription.equals("")) {
            // starts the fragment for the QR code.
            new FragmentQrcodeMenu1(deviceId+eventName).show(getSupportFragmentManager(), "menu");
        } else {
            // an error message if QR code generation failed
            Toast.makeText(AddEventActivity.this, "Not all required fields are filled", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets permissions for the camera and gallery of the Android device.
     * Based on ProfileActivity Code
     */
    private void requestPermissions() {
        // Checks if device does not have permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If not, permissions are granted.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
    }

    /**
     * Displays the options to either take a photo using the camera or pick one from the gallery.
     * Based on ProfileActivity code.
     */
    private void showImagePickerOptions() {
        // Check for permissions
        requestPermissions();

        // Options to choose from gallery or take a photo
        CharSequence[] options = {"Choose from Gallery", "Take Photo"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Change Profile Photo");
        builder.setItems(options, (dialog, item) -> {
            // Displayed the options
            if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);   // Create an intent and start the Activity for the picture.
            } else if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   // Intent for the camera
                startActivityForResult(takePicture, CAPTURE_IMAGE_REQUEST);  // Start activity for camera.
            }
        });
        builder.show();
    }

    /**
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
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

    /**
     * Gets the Uri for future purposes. Based on the code in ProfileActivity.
     * @param inContext - Context of the Application
     * @param inImage - The bitmap of the image that will be converted into a Uri
     * @return - A Uri version of the string path
     */
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Uploads the information of the image to firebase. Based on the code in ProfileActivity.
     * @param imageUri - Uri of the image that will be uploaded.
     * @param event - event that the image will be associated with
     */
    private void uploadImageToFirebaseStorage(Uri imageUri, Event event) {

        String userId = getUserId(); // Retrieves device Id

        // Create a reference to 'eventImages/userId.jpg'
        // Need a way to uniquely determine the event image.
        StorageReference eventImageRef = FirebaseStorage.getInstance()
                .getReference("eventImages/" + userId + ".jpg");   // Need to add event identifier or some placeholder.

        // Upload file to Firebase Storage
        eventImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // When the image has successfully uploaded, get its download URL
                    eventImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Save the download URL to the Firestore document for the user
                        event.setEventImageUrl(downloadUri.toString());  // URL is saved in event
                        Glide.with(AddEventActivity.this)  // Loads the image as a poster in the activity.
                                .load(downloadUri.toString())
                                .into(editEventPoster);
                    });
                })
                .addOnFailureListener(e -> {   // In case the upload fails
                    Log.e("OrganizerActivity", "Image upload failed", e);
                });
    }

    /**
     * Based on the code in ProfileActivity, retrieves the deviceId for future purposes.
     * @return - String that is representative of the deviceId.
     */
    private String getUserId() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
