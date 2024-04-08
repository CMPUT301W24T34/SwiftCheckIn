package com.example.swiftcheckin.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.swiftcheckin.R;

import java.util.ArrayList;

/**
 * A dialog fragment to display milestone achievements based on checked-in and signed-up lists.
 */
public class FragmentMilestone extends DialogFragment {

    ArrayList<Pair<String, String>> checkedInList;
    ArrayList<Pair<String, String>> signedUpList;

    /**
     * Constructs a new FragmentMilestone with the specified checked-in and signed-up lists.
     *
     * @param checkedInList The list of checked-in attendees.
     * @param signedUpList  The list of signed-up attendees.
     */
    FragmentMilestone(ArrayList<Pair<String, String>> checkedInList, ArrayList<Pair<String, String>> signedUpList)
    {
        this.checkedInList = checkedInList;
        this.signedUpList = signedUpList;
    }

    /**
     * Creates and returns a dialog for milestone achievements based on checked-in and signed-up lists.
     *
     * @param savedInstanceState The saved instance state.
     * @return The created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.organizer_event_info_milestone, null);

        ImageView firstSignUp = view.findViewById(R.id.milestone_firstSignUp);
        ImageView firstCheckIn = view.findViewById(R.id.milestone_firstCheckIn);
        ImageView tenSignUp = view.findViewById(R.id.milestone_tenSignUp);
        ImageView tenCheckIn = view.findViewById(R.id.milestone_tenCheckIn);

        if(signedUpList.size() >= 1)
        {
            firstSignUp.setImageResource(R.drawable.milestone);
        }

        if(checkedInList.size() >= 1)
        {
            firstCheckIn.setImageResource(R.drawable.milestone);
        }

        if(signedUpList.size() >= 10)
        {
            tenSignUp.setImageResource(R.drawable.milestone);
        }

        if(checkedInList.size() >= 10)
        {
            tenCheckIn.setImageResource(R.drawable.milestone);
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        return dialogBuilder.setView(view).setCancelable(true).create();
    }
}
