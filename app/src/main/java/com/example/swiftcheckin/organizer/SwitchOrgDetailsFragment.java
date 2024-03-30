package com.example.swiftcheckin.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.swiftcheckin.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SwitchOrgDetailsFragment extends DialogFragment {

    String eventId;
    Bitmap bitmap_qr;

//    public static SwitchOrgDetailsFragment newInstance(String extraData) {
//        SwitchOrgDetailsFragment fragment = new SwitchOrgDetailsFragment();
//        Bundle args = new Bundle();
//        args.putString("eventId", extraData);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public SwitchOrgDetailsFragment(String eventId, Bitmap bitmap)
    {
        this.eventId = eventId;
        this.bitmap_qr = bitmap;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.org_switch_details_fragment, null);
        Button viewSignedUp = view.findViewById(R.id.view_sign_up_attendees_button);
        Button sendNotifs = view.findViewById(R.id.send_notifications_button);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        ImageView qrImageView = view.findViewById(R.id.organizer_switch_details_fragment_qrImage);
        qrImageView.setImageBitmap(bitmap_qr);

        LinearLayout shareLayout = view.findViewById(R.id.switch_details_ShareButtonLayout);

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImageAndText(bitmap_qr);
            }
        });

        viewSignedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ViewAttendeesActivity.class);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });

        sendNotifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddAnnouncementActivity.class);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });
        return builder
                .setView(view)
                .setNegativeButton("Cancel", null)
                .create();
    }

    /**
     * Shares the image and text data between the activities.
     * @param bitmap: Image of the Qr code.
     */
    //Citation: The following code for sharing a QR code, 2024, Licensing: CC BY, Youtube, Share an image file from app cache directory, Sanjeev Kumar, https://www.youtube.com/watch?v=QbTCMe9RnJ0
    private void shareImageAndText(Bitmap bitmap) {
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Image Text");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Image Subject");
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    /**
     * Gets the Uri of the Qr image.
     * @param bitmap: Bitmap Qr code
     * @return uri: returns the uri of the image

     */
    //Citation: The following code for sharing a QR code, 2024, Youtube, "Send Image To Other Apps in Android Studio (Updated) || Android 11 onwards", Android Tutorials, https://www.youtube.com/watch?v=eSi28xqGjbE
    //Citation: The following code for sharing a QR code, 2024, Licensing: CC BY, Youtube, Share an image file from app cache directory, Sanjeev Kumar, https://www.youtube.com/watch?v=QbTCMe9RnJ0
    //Both above citations were used
    private Uri getImageToShare(@NonNull Bitmap bitmap) {
        File folder = new File(requireContext().getCacheDir(), "images");
        Uri uri = null;
        try {
            folder.mkdirs();
            File file = new File(folder, "image.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            uri = FileProvider.getUriForFile(requireContext(), "com.example.swiftcheckin", file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }

}
