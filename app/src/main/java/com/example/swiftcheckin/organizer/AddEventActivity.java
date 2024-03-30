package com.example.swiftcheckin.organizer;

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
import com.example.swiftcheckin.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

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
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Saves the event.
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


    // Citation: OpenAI, Date: March 4, 2024. ChatGPT. Used to enforce the date format in the EditText
    /*
     I wanted to make the input more user friendly, so I searched a bit on google. After not finding good ways to enforce this format,
     I asked ChatGPT to show me a way to enforce the format. It asked me to use TextChangedListener on the EditTexts.
     Prompt: How to enforce a particular format for edittext

     Answer: To enforce a specific format for an EditText in Android, you can use a combination of input type and input filters. Here's how you can enforce a format for an EditText:

            Input Type: Use the android:inputType attribute to specify the type of input allowed. For example, if you want to enforce a numeric format, you can use number or numberDecimal. If you want to enforce an email format, you can use textEmailAddress.

            Input Filters: Use input filters to restrict the characters that can be entered into the EditText. You can create custom input filters to enforce specific formats.

            Here's an example of enforcing a phone number format for an EditText:
                        <EditText
                android:id="@+id/editText_phone"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:hint="Enter phone number"
                android:inputType="phone"
                android:digits="0123456789-() " />

            In this example:

            android:inputType="phone" enforces the EditText to accept phone numbers.
            android:digits="0123456789-() " restricts input to only digits, hyphens, parentheses, and spaces.
            You can also use a TextWatcher to validate the input format programmatically. Here's an example of how you can use a TextWatcher to enforce a specific format (e.g., date format "dd/MM/yyyy"):

            editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2 || s.length() == 5) {
                    s.append("/");
                    }
                }
             });

             This TextWatcher appends a "/" character after the second and fifth characters, effectively enforcing a "dd/MM/yyyy" format. You can adjust this logic according to your specific format requirements.

            Follow-up prompt: What should I write in the XML to enforce input of date

            Answer:
            Here's an example of how you can enforce a "dd/MM/yyyy" date format for an EditText:
            <EditText
            android:id="@+id/editText_date"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:hint="Enter date (dd/MM/yyyy)"
            android:inputType="date"
            android:digits="0123456789/" />


    */

    /**
     *  Enforces the correct format in the EditText.
     *  Specifically for enforcing the date format: DD/MM/YYYY
     * @param editText
     */
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

    /**
     * Retrieves the info the user inputted to send to the organizer activity by calling FragmentQrcodeMenu1
     * Else it will record an error.
     */
    protected void editAddEvent()
    {

        // Information entered by the user.
        EditText eventNameEditText = findViewById(R.id.eventName);
        String eventName = eventNameEditText.getText().toString();

        EditText addressEditText = findViewById(R.id.eventPageAddressEditText);
        String eventAddress = addressEditText.getText().toString();

        EditText descriptionEditText = findViewById(R.id.eventPageDescriptionEditText);
        String eventDescription = descriptionEditText.getText().toString();

        EditText eventStartDateEditText = findViewById(R.id.eventAddActivity_StartDate_EditText);
        String eventStartDate = eventStartDateEditText.getText().toString();

        EditText eventEndDateEditText = findViewById(R.id.eventAddActivity_eventEndDate_EditText);
        String eventEndDate = eventEndDateEditText.getText().toString();

        EditText eventStartTimeEditText = findViewById(R.id.eventAddActivity_eventStartTime_EditText);
        String eventStartTime = eventStartTimeEditText.getText().toString();

        EditText eventEndTimeEditText = findViewById(R.id.eventAddActivity_eventEndTime_EditText);
        String eventEndTime = eventEndTimeEditText.getText().toString();

        EditText eventMaxAttendeesEditText = findViewById(R.id.editMaxAttendeeText);
        String eventMaxAttendee = eventMaxAttendeesEditText.getText().toString();

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

        if (!eventName.equals("") && !eventStartDate.equals("") && !eventAddress.equals("")) {
            // starts the fragment for the QR code.
            new FragmentQrcodeMenu1(deviceId+eventName).show(getSupportFragmentManager(), "menu");
        } else {
            // an error message if QR code generation failed
            Toast.makeText(AddEventActivity.this, "Not all required fields are filled", Toast.LENGTH_SHORT).show();
            finish();
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
