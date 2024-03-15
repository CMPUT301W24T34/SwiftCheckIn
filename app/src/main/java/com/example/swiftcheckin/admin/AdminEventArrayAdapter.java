package com.example.swiftcheckin.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.swiftcheckin.organizer.Event;
import com.bumptech.glide.Glide;
import com.example.swiftcheckin.R;

import java.util.ArrayList;
/**
 * This is the Admin array adapter for events
 */

public class AdminEventArrayAdapter extends ArrayAdapter<Event> {
    public AdminEventArrayAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_event_content, parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);
        TextView eventTitle = view.findViewById(R.id.event_name);

        eventTitle.setText(event.getEventTitle());
        return view;
    }

}
