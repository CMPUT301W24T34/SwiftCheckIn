package com.example.swiftcheckin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

public class FragmentQrcodeMenu1 extends DialogFragment {

    private Button selectQr;    // to be implemented
    private Button newQr;
    private String eventId;

    ConstraintLayout layout1;
    LinearLayout layout_selection;  // selection yet to be made
    LinearLayout successLayout;

    interface AddActivity
    {
        void setGeneratedFlag(Boolean flag);
    }

    AddActivity listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof AddActivity){  // checks if the passed context is instance of AddActivity
            listener = (AddActivity) context;
        }else{
            throw new RuntimeException(context + " must implement AddActivity"); // error
        }
    }
    private Bitmap qrCodeGenerated = null;
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


        newQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createQr(eventId);
                    showSuccessScreen(view);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the exception here, e.g., show an error message
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view).setCancelable(true).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Dismiss the dialog
                dialog.dismiss();
            }})
            .create();
    }
    public void setData(String data)
    {
        this.eventId = data;
    }

    protected void createQr(String data)
    {
       qrCodeGenerated = QrCodeManager.generateQRCode(data);
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
            setFlagInContext();
        }
        successLayout.setVisibility(View.VISIBLE);
    }
}
