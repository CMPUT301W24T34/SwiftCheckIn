package com.example.swiftcheckin.attendee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.swiftcheckin.R;

import java.util.ArrayList;

public class AnnouncementArrayAdapter extends ArrayAdapter<Announcement> {

    public AnnouncementArrayAdapter(Context context, ArrayList<Announcement> announcements){
        super(context, 0, announcements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.announcement_content, parent, false);
        } else {
            view = convertView;
        }

        Announcement announcement = getItem(position);
        TextView announceTitle = view.findViewById(R.id.announcement_heading_con);
        TextView announceDes = view.findViewById(R.id.announcement_description_con);

        announceTitle.setText(announcement.getTitle());
        announceDes.setText(announcement.getDetails());

        return view;
    }

}
