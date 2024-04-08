package com.example.swiftcheckin.organizer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
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
import com.example.swiftcheckin.attendee.QRCodeScannerActivity;
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

public class EventQrReuseScanActivity extends AppCompatActivity {


    // code take from QrCodeScannerActivity

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_qr_camera_reuse);

        previewView = findViewById(R.id.organizerPreviewView);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    private void startCamera()
    {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try
            {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            }
            catch(ExecutionException | InterruptedException e)
            {
                Log.e("QR Code Reuse Camera", "Error starting camera: "+ e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider)
    {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
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

    private void analyzeImage(ImageProxy imageProxy)
    {
        if(!isScanning)
        {
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
                                // handle the qr value retrieved
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
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



    private AlertDialog currentDialog = null; // Class level variable to hold the dialog

    public void showDialog(String title, String message) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EventQrReuseScanActivity.this);
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
