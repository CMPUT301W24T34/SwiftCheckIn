package com.example.swiftcheckin;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FragmentQrcodeMenu1 extends DialogFragment {

    private Button selectQr;
    private Button newQr;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode_choice_menu1, null);
        selectQr = view.findViewById(R.id.fragmentQrCodeMenu1ExistingButton);
        newQr = view.findViewById(R.id.fragmentQrCodeMenu1NewButton);

        newQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createQr();
            }
        });
        return null;
    }

    protected void createQr()
    {

    }

}
