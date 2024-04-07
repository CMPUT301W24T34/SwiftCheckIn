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

public class EventFragmentQrs extends DialogFragment {

    String eventId;
    Bitmap checkInQr;
    Bitmap promoQr;
    public EventFragmentQrs(String eventId, Bitmap checkInQr, Bitmap promoQr)
    {
        this.eventId = eventId;
        this.checkInQr = checkInQr;
        this.promoQr = promoQr;
    }

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

    private void initializeLayoutClick(LinearLayout button, Bitmap qrImage)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImageAndText(qrImage);
            }
        });
    }

    private void shareImageAndText(Bitmap bitmap) {
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Image Text");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Image Subject");
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share via"));
    }

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
