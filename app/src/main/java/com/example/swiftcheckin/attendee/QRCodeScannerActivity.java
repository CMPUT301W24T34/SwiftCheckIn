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

import com.example.swiftcheckin.organizer.EventSignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private EventSignUp eventSignUp = new EventSignUp();

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
                String scannedEventId = result.getContents();
                // This returns the scanned event ID
                //  Toast.makeText(this, "Scanned: " + scannedEventId, Toast.LENGTH_LONG).show();
                checkInAttendee(scannedEventId);
                // Call checkInAttendee with the scanned ID
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

                    DocumentReference qrDoc = db.collection("qrcodes").document(scannedEventId);
                    qrDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                List<String> eventIds = (List<String>) document.get("eventIds");
                                // Get information from the document.
                                DocumentSnapshot qrcode = task.getResult();
                                String eventID = (String) qrcode.getData().get("eventID");
                                if (eventIds != null && eventIds.contains(eventID)) {
                                    eventSignUp.addCheckedIn(eventID, deviceId);
                                    showDialog("Check-in Successful", "You have been checked in successfully!");
                                } else {
                                    showDialog("Check-in Failed", "You did not sign up for this event");
                                }

                            }
                        }
                    });
                    // On complete
                    // Keep list
                    // Add if statements
//                    List<String> eventIds = (List<String>) document.get("eventIds");
//                    if (eventIds != null && eventIds.contains(scannedEventId)) {
//                        showDialog("Check-in Successful", "You have been checked in successfully!");
//                    } else {
//                        showDialog("Check-in Failed", "You did not sign up for this event");
//                    }
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
    private void showDialog(String title, String message) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeScannerActivity.this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", (dialog, id) -> {
                dialog.dismiss();
                // Close the activity after the dialog is dismissed
                finish();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }



}
