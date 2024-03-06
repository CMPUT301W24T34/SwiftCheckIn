package com.example.swiftcheckin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import androidx.fragment.app.DialogFragment;

public class FragmentQrcodeMenu1 extends DialogFragment {

    private Button selectQr;    // to be implemented
    private Button newQr;
    private String eventId;
    private Button saveButton;

    private Bitmap qrCodeGenerated;
    ConstraintLayout layout1;
    LinearLayout layout_selection;  // selection yet to be made
    LinearLayout successLayout;

    interface AddActivity
    {
        void setGeneratedFlag(Boolean flag);
    }
    AddActivity listener;

    public FragmentQrcodeMenu1(String eventId, Bitmap bitmap)
    {
        this.eventId = eventId;
        this.qrCodeGenerated = bitmap;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof AddActivity){  // checks if the passed context is instance of AddActivity
            listener = (AddActivity) context;
        }else{
            throw new RuntimeException(context + " must implement AddActivity"); // error
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode_choice_menu1, null);
        selectQr = view.findViewById(R.id.fragmentQrCodeMenu1ExistingButton);
        newQr = view.findViewById(R.id.fragmentQrCodeMenu1NewButton);


        // layout 1
        layout1 = view.findViewById(R.id.qrCodeCreationMenu_Layout1);
        layout_selection = view.findViewById(R.id.existingQrSelectionMenuLayout);
        successLayout = view.findViewById(R.id.qrCodeSelectionSuccessLayout);
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        return builder.setView(view)
                .setCancelable(true)
                .create();
    }

    public void setData(String data)
    {
        Log.e("EventID", "The id is "+ data);
        this.eventId = data;
    }

    public void setQrBitmap(Bitmap bitmap)
    {
        this.qrCodeGenerated = bitmap;
    }
    protected void createQr(String data)
    {
        qrCodeGenerated = QrCodeManager.generateQRCode(data);
        Log.e("Bitmap", QrCodeManager.convertToBase64String(qrCodeGenerated));
    }

    protected void setFlagInContext()
    {
        listener.setGeneratedFlag(true);
    }

    public void showSuccessScreen(View view)
    {
        if(layout1.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
        } else if(layout_selection.getVisibility()==View.VISIBLE) {
            layout_selection.setVisibility(View.INVISIBLE);
        }
        ImageView qrImage = view.findViewById(R.id.eventQrCodeCreationSuccessDialog_ImageView);
        if(qrCodeGenerated != null){
            qrImage.setImageBitmap(qrCodeGenerated);
        }
        successLayout.setVisibility(View.VISIBLE);
    }

    private void saveEvent()
    {
        AlertDialog dialog = (AlertDialog) getDialog();
        assert dialog != null;
        dialog.dismiss();
        if (getActivity() instanceof AddActivity) {
            setFlagInContext();
        }
    }
}
