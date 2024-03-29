package com.example.swiftcheckin.organizer;

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

import com.example.swiftcheckin.R;

public class SwitchOrgDetailsFragment extends DialogFragment {

    public static SwitchOrgDetailsFragment newInstance(String extraData) {
        SwitchOrgDetailsFragment fragment = new SwitchOrgDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", extraData);
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        String eventId = args.getString("eventId");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.org_switch_details_fragment, null);
        Button viewSignedUp = view.findViewById(R.id.view_sign_up_attendees_button);
        Button sendNotifs = view.findViewById(R.id.send_notifications_button);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

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

}
