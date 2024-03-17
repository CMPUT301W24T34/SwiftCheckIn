package com.example.swiftcheckin.organizer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

public class QrCodeManager {


    //Citation: Cambo Tutorial, Youtube. March 7, 2024. Link: https://www.youtube.com/watch?v=n8HdrLYL9DA
    // Shifted from old code, it was not working in some testing process.
    /**
     * Generates a QR code bitmap for the given data.
     *
     * @param data The data to be encoded into the QR code.
     * @return The generated QR code bitmap.
     */
    public static Bitmap generateQRCode(String data) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 1500, 1500);
            BarcodeEncoder encoder =  new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // Citation: OpenAI. Date: March 6, 2024. ChatGPT.
    // Gave GPT the older version of code and asked it how can we store a qr in the database.
    // It produced this function to convert the bitmap to a string, and store in the database when needed. Not tested, as it is not needed in this part.
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
