package com.example.swiftcheckin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * DialogFragment to show generated QR and ask for user choice.
 */
public class FragmentQrcodeMenu1 extends DialogFragment {
    //Citation: The following code for sharing a QR code, 2024, Licensing: CC BY, Youtube, Share an image file from app cache directory, Sanjeev Kumar, https://www.youtube.com/watch?v=QbTCMe9RnJ0

    private Button selectQr;    // to be implemented
    private Button newQr;   // Button to generate new QR.
    private String eventId;     // Stores eventId
    private Button saveButton;  // Button to save the event.

    private Bitmap qrCodeGenerated;     // Bitmap images of the generated/selected qr code.
    ConstraintLayout layout1;       // The layout with buttons to generate or select a Qr code.
    LinearLayout layout_selection;  // selection yet to be made
    LinearLayout successLayout;     // Layout to show the generated qr, a button to share the qr, and a button to save the event.

    /**
     * Interface to communicate with the activity to set the flag.
     */
    interface AddActivity {
        /**
         * sets the flag to its attribute in the activity.
         * @param flag: flag to indicate if the event has been saved and generated.
         */
        void setGeneratedFlag(Boolean flag);
    }

    AddActivity listener;

    /**
     * Constructor of the FragmentQrcodeMenu1 class. Initialises the dialog fragment.
     * @param eventId: String to get the eventId of the event generated.
     * @param bitmap: Bitmap image of the unique Qr code generated for the event.
     */
    public FragmentQrcodeMenu1(String eventId, Bitmap bitmap) {
        this.eventId = eventId;
        this.qrCodeGenerated = bitmap;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddActivity) {  // checks if the passed context is instance of AddActivity
            listener = (AddActivity) context;
        } else {
            throw new RuntimeException(context + " must implement AddActivity"); // error
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode_choice_menu1, null);
        selectQr = view.findViewById(R.id.fragmentQrCodeMenu1ExistingButton);
        newQr = view.findViewById(R.id.fragmentQrCodeMenu1NewButton);
        ImageView imageView = view.findViewById(R.id.eventQrCodeCreationSuccessDialog_ImageView);


        // layout 1
        layout1 = view.findViewById(R.id.qrCodeCreationMenu_Layout1);
        layout_selection = view.findViewById(R.id.existingQrSelectionMenuLayout);
        successLayout = view.findViewById(R.id.qrCodeSelectionSuccessLayout);
        LinearLayout shareButton = view.findViewById(R.id.qrCodeCreationSuccess_ShareButtonLayout);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageAndText(bitmap);

            }
        });


        saveButton = view.findViewById(R.id.qrCodeSelectionSuccessLayout_saveButton);

        newQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showSuccessScreen(view);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error in QR", "QR not generated-onclick");
                }
            }
        });


        // Citation: dismiss() -> https://developer.android.com/guide/fragments/dialogs :  dismiss the fragment and its dialog.(text from the website)
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    setFlagInContext();
                }
            }
        });

        // Citation: AlertDialog taken from the lab works.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setCancelable(true)
                .create();
    }

    /**
     * Shares the image data between the activities.
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
     * @return
     */
    //Citation: The following code for sharing a QR code, 2024, Licensing: CC BY, Youtube, Share an image file from app cache directory, Sanjeev Kumar, https://www.youtube.com/watch?v=QbTCMe9RnJ0
    private Uri getImageToShare(Bitmap bitmap) {
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

    public void setData(String data) {
        Log.e("EventID", "The id is " + data);
        this.eventId = data;
    }

    protected void setFlagInContext() {
        listener.setGeneratedFlag(true);
    }

    public void showSuccessScreen(View view) {
        if (layout1.getVisibility() == View.VISIBLE) {
            layout1.setVisibility(View.INVISIBLE);
        } else if (layout_selection.getVisibility() == View.VISIBLE) {
            layout_selection.setVisibility(View.INVISIBLE);
        }
        ImageView qrImage = view.findViewById(R.id.eventQrCodeCreationSuccessDialog_ImageView);
        if (qrCodeGenerated != null) {
            qrImage.setImageBitmap(qrCodeGenerated);
        }
        successLayout.setVisibility(View.VISIBLE);
    }
}
