package com.example.swiftcheckin.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.swiftcheckin.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * DialogFragment to show generated QR and ask for user choice.
 */
public class FragmentQrcodeMenu1 extends DialogFragment {
    //Citation: The following code for sharing a QR code, 2024, Licensing: CC BY, Youtube, Share an image file from app cache directory, Sanjeev Kumar, https://www.youtube.com/watch?v=QbTCMe9RnJ0

    private Button selectQr;    // to be implemented
    private Button newQr;   // Button to generate new QR.
    private String eventId;     // Stores eventId
    private Button saveButton;  // Button to save the event.


    private ImageView qrImageSuccess;
    private String deviceId;
    private FirebaseOrganizer db_organizer;
    private Qr_Code qrCodeGenerated;     // Qr_Code object
    private Qr_Code promoQrCode;

    private ListView reuseQrList;
    private ArrayList<Qr_Code> qrDataList;
    private ReuseQrAdapter reuseQrAdapter;


    ConstraintLayout layout1;       // The layout with buttons to generate or select a Qr code.
    LinearLayout layout_selection;  // selection yet to be made
    LinearLayout successLayout;     // Layout to show the generated qr, a button to share the qr, and a button to save the event.

    // citation: TechViewHub, Youtube. Adding the sound effect in Android | TechViewHub | Android Studio. March 29, 2024.
    // Link: https://www.youtube.com/watch?v=iMAJRHcC2dQ
    MediaPlayer mediaPlayer;    // used to play a sound effect



    /**
     * Interface to communicate with the activity to set the flag.
     */
    interface AddActivity {
        /**
         * sets the flag to its attribute in the activity.
         *
         * @param flag : flag to indicate if the event has been saved and generated.
         * @param qrCodeID - ID of checkin qr code
         * @param promotionalQrID - ID of promotional qr code
         */
        void setGeneratedFlag(Boolean flag, String qrCodeID, String promotionalQrID);
    }

    // interface instance from labs
    AddActivity listener;   // an instance of the AddActivity interface

    /**
     * Constructor of the FragmentQrcodeMenu1 class. Initialises the dialog fragment.
     */
    public FragmentQrcodeMenu1(String eventId) {
        this.eventId = eventId;
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
        db_organizer = new FirebaseOrganizer(requireContext());    // citation: auto-suggested by android studio
        deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // layout 1
        layout1 = view.findViewById(R.id.qrCodeCreationMenu_Layout1);
        layout_selection = view.findViewById(R.id.existingQrSelectionMenuLayout);
        successLayout = view.findViewById(R.id.qrCodeSelectionSuccessLayout);

        // mediaplayer for saving sound
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.swoosh);


        // reusable qr layout
        layout_selection = view.findViewById(R.id.existingQrSelectionMenuLayout);
        reuseQrList = view.findViewById(R.id.existingQrSelectionMenuListView);
        qrDataList = new ArrayList<>();
        reuseQrAdapter = new ReuseQrAdapter(requireContext(), qrDataList);
        reuseQrList.setAdapter(reuseQrAdapter);
        populateReuseQr();


        // final layout
        qrImageSuccess = view.findViewById(R.id.eventQrCodeCreationSuccessDialog_ImageView);


        // onclick listener initializations
        LinearLayout shareButton = view.findViewById(R.id.qrCodeCreationSuccess_ShareButtonLayout);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) qrImageSuccess.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageAndText(bitmap);

            }
        });


        saveButton = view.findViewById(R.id.qrCodeSelectionSuccessLayout_saveButton);

        newQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    qrCodeGenerated = createQr();
                    promoQrCode = createQr();
                    promoQrCode.setIsPromo(true);       // setting the promotional qr
                    showSuccessScreen(view);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error in QR", "QR not generated-onclick");
                }
            }
        });


        selectQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.INVISIBLE);
                layout_selection.setVisibility(View.VISIBLE);
            }
        });

        reuseQrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try
                {
                    qrCodeGenerated = qrDataList.get(position);
                    qrCodeGenerated.setEventID(eventId);
                    promoQrCode = createQr();
                    promoQrCode.setIsPromo(true);   // setting the promotional qr

                    Log.d("Before success screen", qrCodeGenerated.getQrID());
                    Log.d("Before success screen - promo", promoQrCode.getQrID());
                    showSuccessScreen(view);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e("QR selection error","Error in selecting reusable Qr code");
                }

            }
        });


        // Citation: dismiss() -> https://developer.android.com/guide/fragments/dialogs :  dismiss the fragment and its dialog.(text from the website)
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) { // saving qrcode
                    mediaPlayer.start();
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
     * Populates the list of unused qr in the dialog fragment
     */
    private void populateReuseQr() {
        db_organizer.getReuseQrData(qrDataList, new FirebaseOrganizer.ReuseQrDataCallback() {
            @Override
            public void onDataLoaded(ArrayList<Qr_Code> dataList) {
                if(qrDataList.size() != 0)      // if no qr is available then button is hidden from the user
                    selectQr.setVisibility(View.VISIBLE);
                reuseQrAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Firebase - Error", errorMessage);
            }
        });
    }

    /**
     * Generates a new Qr_Code object with a new Unique ID of length 16, and attaches a bitmap to the object.
     * @return qrcode - Qr_Code object
     */
    private Qr_Code createQr()
    {
        // OpenAI, March 29 2024. ChatGPT. Asked chatgpt on how to create a random alphanumeric string to be used as an id.
        int length_qr_id = 32;
        // random qr id
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        uuid = uuid.substring(0, length_qr_id);

        Bitmap qrcode_bitmap = QrCodeManager.generateQRCode(uuid);
        Qr_Code qrcode = new Qr_Code(uuid, qrcode_bitmap);
        qrcode.setEventID(this.eventId);
        qrcode.setDeviceID(deviceId);

        return qrcode;
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

    /**
     * Sets the eventId for this particular fragment
     * @param data
     */
    public void setData(String data) {
        this.eventId = data;
    }

    /**
     * Sets the flag of the calling activity to true.
     */
    protected void setFlagInContext() {
        db_organizer.addQrCode(qrCodeGenerated);
        db_organizer.addQrCode(promoQrCode);

        if(qrCodeGenerated.getImage() != null && promoQrCode != null)
        {listener.setGeneratedFlag(true, qrCodeGenerated.getQrID(), promoQrCode.getQrID());}
        else
        {
            Toast.makeText(getContext(), "Error occurred in saving the event", Toast.LENGTH_LONG).show();
            Log.e("Event Error", "Error saving current event: "+eventId);
        }
    }

    /**
     * Makes the success layout with qr code, share option and save button visible
     * and makes other views invisible.
     * @param view: Current View of the fragment.
     */
    public void showSuccessScreen(View view) {
        if (layout1.getVisibility() == View.VISIBLE) {
            layout1.setVisibility(View.INVISIBLE);
        } else if (layout_selection.getVisibility() == View.VISIBLE) {
            layout_selection.setVisibility(View.INVISIBLE);
        }

        if (promoQrCode != null) {
            qrImageSuccess.setImageBitmap(promoQrCode.getImage());
        }
        Log.d("Success Screen", "Showing success screen");
        successLayout.setVisibility(View.VISIBLE);
    }
}
