package com.example.swiftcheckin.admin;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.swiftcheckin.attendee.Profile;
import com.example.swiftcheckin.R;

import java.util.ArrayList;
/**
 * This is the profile array adapter for admin
 */
public class ProfileArrayAdapter extends ArrayAdapter<Profile>{
    private ArrayList<Profile> profiles;
    private Context context;

    public ProfileArrayAdapter(Context context, ArrayList<Profile> profiles){
        super(context,0, profiles);
        this.profiles = profiles;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.profile_content, parent,false);
        }

        Profile profile = profiles.get(position);

        TextView profileName = view.findViewById(R.id.name_text);

        profileName.setText(profile.getName());

        return view;
    }


}
