package com.example.swiftcheckin.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.swiftcheckin.R;
import com.google.api.Distribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A DialogFragment to display event QR codes for check-in and promo.
 */
public class EventFragmentQrs extends DialogFragment {

    String eventId;
    Bitmap checkInQr;
    Bitmap promoQr;

    /**
     * Constructor for EventFragmentQrs.
     * @param eventId The ID of the event.
     * @param checkInQr The check-in QR code Bitmap.
     * @param promoQr The promo QR code Bitmap.
     */
    public EventFragmentQrs(String eventId, Bitmap checkInQr, Bitmap promoQr)
    {
        this.eventId = eventId;
        this.checkInQr = checkInQr;
        this.promoQr = promoQr;
    }

    /**
     * Called to create the dialog shown by this fragment. This method creates and configures an AlertDialog
     * containing the layout for displaying QR codes and share options.
     * @param savedInstanceState The previously saved state of the fragment (unused in this implementation).
     * @return The dialog created for this fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.organizer_info_page_qrcodes, null);
        ImageView checkInQrImage = view.findViewById(R.id.organizerEventInfo_CheckInQR);
        ImageView promoQrImage = view.findViewById(R.id.organizerEventInfo_PromoQR);

        LinearLayout checkInQrImageShare = view.findViewById(R.id.organizerEventInfo_CheckInQrShare);
        LinearLayout promoQrShare = view.findViewById(R.id.organizerEventInfo_PromoQrShare);

        checkInQrImage.setImageBitmap(this.checkInQr);
        promoQrImage.setImageBitmap(this.promoQr);

        initializeLayoutClick(checkInQrImageShare, this.checkInQr);
        initializeLayoutClick(promoQrShare, this.promoQr);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view).setCancelable(true).create();
    }

    /**
     * Initialize click listener for share layouts.
     * @param button The layout to initialize the click listener on.
     * @param qrImage The QR code image to share.
     */
    private void initializeLayoutClick(LinearLayout button, Bitmap qrImage)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImageAndText(qrImage);
            }
        });
    }

    /**
     * Share the QR code image along with text.
     * @param bitmap The QR code image to share.
     */
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
     * Get URI of the image to share.
     * @param bitmap The QR code image.
     * @return The URI of the image.
     */
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
