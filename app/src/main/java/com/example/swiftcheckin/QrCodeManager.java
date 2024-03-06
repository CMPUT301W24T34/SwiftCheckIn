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
     * Returns Bitmap generated for the event ID
     * @param data
     * @return
     */
    public static Bitmap generateQRCode(String data) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 300, 300);
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
     * Returns
     * @param code
     * @return
     */
    public static String convertToBase64String(Bitmap code)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        code.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // returning the string version
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Returns Base64 String for Bitmap generated for String input
     * Combines generateQRCode and convertToString
     * @param data
     * @return
     */
    public static String generateAndReturnString(String data)
    {
        Bitmap code = generateQRCode(data);
        assert code != null;
        return convertToBase64String(code);
    }
}
