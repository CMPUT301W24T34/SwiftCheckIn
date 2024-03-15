package com.example.swiftcheckin.attendee;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * A utility class to generate avatars for users.

 */

public class AvatarGenerator {

    /**
     * Generates and sets an avatar for a given name.
     * @param name The name to generate the avatar for.
     * @param avatarImageCallback A callback to handle the Bitmap once it's loaded.
     * OpenAI | March , 2024 | ChatGPT | Assist in conversion of names to emails as the gravatar is generated with email reference
     */
    public static void generateAvatar(String name, AvatarImageCallback avatarImageCallback) {
        new Thread(() -> {
            try {
                // Because the Gravatar API requires an email address, we'll generate a pseudo-email address using the name(requirement of the project).
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


    /**
     * Generates an MD5 hash of a given input.
     * @param input The input to hash.
     * @return The MD5 hash of the input.
     * * Citation: Some reference is from the https://docs.gravatar.com/gravatar-images/java/
     */
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

    /**
     * Gets a Bitmap from a URL.
     * @param src The URL to get the Bitmap from.
     * @return The Bitmap from the URL.
     */
    // Citation: This method is referenced from the https://stackoverflow.com/questions/11831188/how-to-get-bitmap-from-a-url-in-android
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
