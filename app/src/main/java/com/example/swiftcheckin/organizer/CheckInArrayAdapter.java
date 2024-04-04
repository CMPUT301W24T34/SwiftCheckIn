package com.example.swiftcheckin.organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.swiftcheckin.R;
import com.example.swiftcheckin.attendee.Profile;

import java.util.ArrayList;

public class CheckInArrayAdapter extends ArrayAdapter<Profile> {

    private ArrayList<Profile> profiles;
    private Context context;

    public CheckInArrayAdapter(Context context, ArrayList<Profile> profiles){
        super(context,0, profiles);
        this.profiles = profiles;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.profile_check_in_content, parent,false);
        }

        Profile profile = profiles.get(position);

        TextView profileName = view.findViewById(R.id.name_text_check_in);
        TextView checkInCount = view.findViewById(R.id.check_in_count);

        profileName.setText(profile.getName());
        String checkInStr = "Number of Check Ins: " + profile.getCheckInCount();
        checkInCount.setText(checkInStr);

        return view;
    }
}
