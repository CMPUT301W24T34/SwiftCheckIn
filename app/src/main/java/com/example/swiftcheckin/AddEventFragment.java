package com.example.swiftcheckin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddEventFragment extends DialogFragment {

    private String deviceId;

    interface AddEventDialogListener{
        void addEvent(Event event);
    }

    public AddEventFragment(String deviceId){
        this.deviceId = deviceId;
    }
    public AddEventFragment(){
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
        EditText editEventDate = view.findViewById(R.id.edit_event_date);
        EditText editEventStartTime = view.findViewById(R.id.edit_event_start_time);
        EditText editEventEndTime = view.findViewById(R.id.edit_event_end_Time);
        EditText editEventAM_PM = view.findViewById(R.id.edit_am_pm);
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
                    String eventDate = editEventDate.getText().toString();
                    String eventStartTime = editEventStartTime.getText().toString();
                    String eventEndTime = editEventEndTime.getText().toString();
                    String eventAM_PM = editEventAM_PM.getText().toString();

//                    String maxAttendees = editMaxAttendees.getText().toString();
                    listener.addEvent(new Event(eventTitle, descriptionText, eventLocation,
                            deviceId,eventDate, eventStartTime, eventEndTime, eventAM_PM));
                })
                .create();
    }

}
