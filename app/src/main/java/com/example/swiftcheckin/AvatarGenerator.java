package com.example.swiftcheckin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AvatarGenerator {

    /**
     * Generates and sets an avatar for a given name.
     * @param name The name to generate the avatar for.
     * @param avatarImageCallback A callback to handle the Bitmap once it's loaded.
     */
    public static void generateAvatar(String name, AvatarImageCallback avatarImageCallback) {
        new Thread(() -> {
            try {
                // Create a pseudo-email from the name to use with Gravatar
                String pseudoEmail = name.replaceAll("\\s+", "").toLowerCase() + "@example.com";
                String hash = md5Hex(pseudoEmail);
                String gravatarUrl = "https://www.gravatar.com/avatar/" + hash + "?d=identicon";
                Bitmap bitmap = getBitmapFromURL(gravatarUrl);
                if (bitmap != null) {
                    avatarImageCallback.onAvatarLoaded(bitmap);
                }
            } catch (Exception e) {
                Log.e("AvatarGenerator", "Error generating avatar", e);
            }
        }).start();
    }


    private static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface AvatarImageCallback {
        void onAvatarLoaded(Bitmap avatar);
    }
}
