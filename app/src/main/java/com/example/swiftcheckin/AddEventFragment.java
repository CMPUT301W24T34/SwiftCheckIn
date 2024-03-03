package com.example.swiftcheckin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddEventFragment extends DialogFragment {

    interface AddEventDialogListener{
        void addEvent(Event event);
    }

    private AddEventDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEventDialogListener){
            listener = (AddEventDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddEventDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_event, null);
        EditText editEventTitle = view.findViewById(R.id.edit_event_title_text);
        EditText editDescriptionText = view.findViewById(R.id.edit_event_description_text);
        EditText editEventLocation = view.findViewById(R.id.edit_event_location_text);
//        EditText editMaxAttendees = view.findViewById(R.id.edit_max_attendees_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add an Event")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Next", (dialog, which) ->{
                    String eventTitle = editEventTitle.getText().toString();
                    String descriptionText = editDescriptionText.getText().toString();
                    String eventLocation = editEventLocation.getText().toString();
//                    String maxAttendees = editMaxAttendees.getText().toString();
                    listener.addEvent(new Event(eventTitle, descriptionText, eventLocation));
                })
                .create();
    }

}
