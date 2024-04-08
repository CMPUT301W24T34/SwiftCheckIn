package com.example.swiftcheckin.attendee;
// This fragment is for users to switch between the modes

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.swiftcheckin.organizer.OrganizerActivity;

import com.example.swiftcheckin.R;

/**
 * This deals with the fragment to switch to organizer and admin mode
 */
public class SwitchModeFragment extends DialogFragment {
    @NonNull
    @Override
    /**
     * This deals with the fragment to switch to organizer and admin mode
     * @param savedInstanceState the Bundle
     * @return
     * returns the Dialog
     */
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.switch_fragment, null);
        Button organizer = view.findViewById(R.id.organizer_button);
        Button admin = view.findViewById(R.id.admin_button);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        organizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Citation: Dismissing the popup, Stack Overflow, License: CC-BY-SA, user name Shiva Tiwari, "How to correctly dismiss a DialogFragment?", 03-14-2019, https://stackoverflow.com/questions/11201022/how-to-correctly-dismiss-a-dialogfragment
                dismiss();
                Intent intent = new Intent(getContext(), OrganizerActivity.class);
                startActivity(intent);
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent;
                intent = new Intent(getActivity(), AdminPassword.class);
                startActivity(intent);

            }
        });

        return builder
                .setView(view)
                .setNegativeButton("Cancel", null)
                .create();
    }
}


