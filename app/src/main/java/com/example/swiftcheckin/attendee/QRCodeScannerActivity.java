package com.example.swiftcheckin.attendee;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.swiftcheckin.R;
import com.example.swiftcheckin.organizer.EventSignUp;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;


public class QRCodeScannerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private EventSignUp eventSignUp = new EventSignUp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        previewView = findViewById(R.id.previewView);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("QRCodeScannerActivity", "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(previewView.getWidth(), previewView.getHeight()))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        cameraProvider.unbindAll();
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private boolean isScanning = true;

    private void analyzeImage(ImageProxy imageProxy) {
        if (!isScanning) {
            imageProxy.close();
            return;
        }

        @SuppressLint("UnsafeOptInUsageError")
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build();

            BarcodeScanner scanner = BarcodeScanning.getClient(options);

            Task<List<Barcode>> result = scanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        if (!isScanning) {
                            imageProxy.close();
                            return;
                        }

                        for (Barcode barcode : barcodes) {
                            if (barcode.getValueType() == Barcode.TYPE_TEXT) {
                                String scannedQRCode = barcode.getDisplayValue();
                                Log.d("QRCode", scannedQRCode);
                                isScanning = false;
                                handleScannedQRCode(scannedQRCode);
                                break;
                            }
                        }
                        imageProxy.close();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("QRCodeScannerActivity", "Error scanning QR code: " + e.getMessage());
                        imageProxy.close();
                    })
                    .addOnCompleteListener(task -> imageProxy.close());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
                finish();
            }
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

    private String retrieveDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

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
                        eventSignUp.addCheckedIn(scannedEventId, deviceId);
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
        cameraExecutor.shutdown();
        super.onDestroy();
    }
}