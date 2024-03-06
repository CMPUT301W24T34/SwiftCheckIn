package com.example.swiftcheckin;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;

public class QrCodeManager {

    /**
     * Generates a QR code bitmap for the given data.
     *
     * @param data The data to be encoded into the QR code.
     * @return The generated QR code bitmap.
     */
    public static Bitmap generateQRCode(String data) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 1500, 1500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a bitmap to a Base64-encoded string.
     *
     * @param bitmap The bitmap to be converted.
     * @return The Base64-encoded string representing the bitmap.
     */
    public static String convertToBase64String(Bitmap bitmap) {
        if (bitmap == null) return null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // returning the string version
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Generates a QR code bitmap for the given data and returns it as a Base64-encoded string.
     *
     * @param data The data to be encoded into the QR code.
     * @return The Base64-encoded string representing the generated QR code bitmap.
     */
    public static String generateAndReturnBase64String(String data) {
        Bitmap bitmap = generateQRCode(data);
        return convertToBase64String(bitmap);
    }
}
