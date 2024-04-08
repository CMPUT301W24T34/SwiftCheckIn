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
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * This class deals with everything related to scanning QR codes using the device's camera.
 * OpenAI | April 1 , 2024 | ChatGPT | Assist in the implementation of the QR code scanning feature using CameraX library
 * https://developer.android.com/codelabs/camerax-getting-started#0
 */
public class QRCodeScannerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    public EventSignUp eventSignUp = new EventSignUp();
    public LocationReceiver locationReceiver;
    private FirebaseAttendee firebaseAttendee;

    /**
     * Initializes the activity.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        previewView = findViewById(R.id.previewView);
        cameraExecutor = Executors.newSingleThreadExecutor();
        locationReceiver = new LocationReceiver();
        firebaseAttendee = new FirebaseAttendee();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    /**
     * Starts the camera to scan QR codes.
     */
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

    /**
     * Binds the camera preview to the activity.
     *
     * @param cameraProvider the camera provider
     */
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

    /**
     * Analyzes the image to scan for QR codes.
     *
     * @param imageProxy the image proxy
     */
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


    /**
     * Handles the permissions request result.
     * @param requestCode the request code
     * @param permissions the permissions
     * @param grantResults the grant results
     */
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

    /**
     * Handles the scanned QR code.
     * @param scannedQRCode the scanned QR code
     */
    private void handleScannedQRCode(String scannedQRCode) {
        firebaseAttendee.handleScannedQRCode(scannedQRCode, this);
    }

    /**
     * Retrieves the device ID.
     * @return the device ID
     */
    public String retrieveDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private AlertDialog currentDialog = null; // Class level variable to hold the dialog

    /**
     * Shows a dialog.
     * @param title the title of the dialog
     * @param message the message of the dialog
     */
    public void showDialog(String title, String message) {
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

    /**
     * Handles the activity being destroyed.
     */
    @Override
    protected void onDestroy() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss(); // Dismiss the dialog to prevent window leaks
        }
        cameraExecutor.shutdown();
        super.onDestroy();
    }
}