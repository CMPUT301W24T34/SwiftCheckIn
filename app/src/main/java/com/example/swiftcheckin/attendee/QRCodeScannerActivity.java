package com.example.swiftcheckin.attendee;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.List;

/**
 * The QR code scanner activity. This class deals with all the real time check-in functionality for a user where the user can scan the QR code of the event and check-in.
 * The event signed up data is fetched from the Firestore and the user is checked in if the event is found in their signed up events(from their device).
 * Resources used for reference:
 * https://www.geeksforgeeks.org/how-to-read-qr-code-using-zxing-library-in-android/
 * https://reintech.io/blog/implementing-android-app-qr-code-scanner
 */
public class QRCodeScannerActivity extends CaptureActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                initQRCodeScanner();
            }
        } else {
            initQRCodeScanner();
        }
    }


    /**
     * Initializes the QR code scanner.
     */
    private void initQRCodeScanner() {

        new IntentIntegrator(this).initiateScan();
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true);
        integrator.setCaptureActivity(QRCodeScannerActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

    }





   /**
     * Handles the result of the QR code scanning process.
     *
     * @param requestCode The request code.
     * @param permissions The permissions requested.
    *  @param grantResults The results of the permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }



    /**
     * Handles the result of the QR code scanning process.
     *
     * @param requestCode The request code.
     * @param resultCode  The result code.
     * @param data        The data returned from the activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String scannedQRCode = result.getContents();
                Log.d("QRCode", scannedQRCode);
                handleScannedQRCode(scannedQRCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleScannedQRCode(String scannedQRCode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference qrCodeRef = db.collection("qrcodes").document(scannedQRCode);

        qrCodeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot qrDocument = task.getResult();
                if (qrDocument.exists()) {
                    String isPromoString = qrDocument.getString("isPromo");
                    boolean isPromo = "true".equalsIgnoreCase(isPromoString); // Handle 'isPromo' as a string

                    String eventId = qrDocument.getString("eventID");
                    if (eventId != null) {
                        if (isPromo) {
                            fetchEventDetailsAndRedirect(eventId); // Fetch details before redirecting
                        } else {
                            // If 'isPromo' is false, continue with the normal check-in process
                            checkInAttendee(eventId);
                        }
                    } else {
                        // Handle case where 'eventId' is not retrieved properly
                        Log.e("QRCodeScannerActivity", "Event ID is missing in the QR code document.");
                        showDialog("Error", "Event details cannot be found.");
                    }
                } else {
                    showDialog("Error", "QR Code is not valid.");
                }
            } else {
                Log.e("FirestoreError", "Error fetching QR code document: ", task.getException());
                showDialog("Error", "Failed to retrieve QR code information. Please try again.");
            }
        });
    }

    private void fetchEventDetailsAndRedirect(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot eventSnapshot = task.getResult();
                if (eventSnapshot.exists()) {
                    // Create intent and pass all the necessary data to AnnouncementActivity
                    Intent intent = new Intent(QRCodeScannerActivity.this, AnnoucementActivity.class);
                    intent.putExtra("eventID", eventId);
                    intent.putExtra("eventTitle", eventSnapshot.getString("eventTitle"));
                    intent.putExtra("eventDescription", eventSnapshot.getString("eventDescription"));
                    intent.putExtra("eventLocation", eventSnapshot.getString("eventLocation"));
                    intent.putExtra("eventStartDate", eventSnapshot.getString("eventStartDate"));
                    intent.putExtra("eventEndDate", eventSnapshot.getString("eventEndDate"));
                    intent.putExtra("eventStartTime", eventSnapshot.getString("eventStartTime"));
                    intent.putExtra("eventEndTime", eventSnapshot.getString("eventEndTime"));
                    intent.putExtra("eventPosterURL", eventSnapshot.getString("eventPosterURL"));
                    intent.putExtra("eventMaxAttendees", eventSnapshot.getString("eventMaxAttendees"));
                    intent.putExtra("eventCurrentAttendees", eventSnapshot.getString("eventCurrentAttendees"));
                    startActivity(intent);
                } else {
                    Log.e("QRCodeScannerActivity", "Event document does not exist.");
                    showDialog("Error", "Event details cannot be found.");
                }
            } else {
                Log.e("FirestoreError", "Error fetching event details: ", task.getException());
                showDialog("Error", "Failed to retrieve event details. Please try again.");
            }
        });
    }






    /**
     * Retrieves the device ID from the system settings.
     *
     * @return The device ID.
     */

    private String retrieveDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Checks in the attendee for the event with the given ID.
     *
     * @param scannedEventId The ID of the event to check in the attendee for.
     */
    private void checkInAttendee(String scannedEventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceId = retrieveDeviceId(); // Retrieve device ID

        DocumentReference deviceRef = db.collection("SignedUpEvents").document(deviceId);
        deviceRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> eventIds = (List<String>) document.get("eventIds");
                    if (eventIds != null && eventIds.contains(scannedEventId)) {
                        showDialog("Check-in Successful", "You have been checked in successfully!");
                    } else {
                        showDialog("Check-in Failed", "You did not sign up for this event");
                    }
                } else {
                    showDialog("Error", "No events found ");
                }
            } else {
                Log.e("FirestoreError", "Error fetching document: ", task.getException());
                showDialog("Error", "Failed to retrieve event information. Please try again.");
            }
        });
    }

    /**
     * Shows a dialog with the given title and message.
     *
     * @param title   The title of the dialog.
     * @param message The message to be displayed in the dialog.
     */
    private AlertDialog currentDialog = null; // Class level variable to hold the dialog

    private void showDialog(String title, String message) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeScannerActivity.this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", (dialog, id) -> {
                dialog.dismiss();
                finish();
            });
            currentDialog = builder.create();
            currentDialog.show();
        });
    }

    @Override
    protected void onDestroy() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss(); // Dismiss the dialog to prevent window leaks
        }
        super.onDestroy();
    }


}
