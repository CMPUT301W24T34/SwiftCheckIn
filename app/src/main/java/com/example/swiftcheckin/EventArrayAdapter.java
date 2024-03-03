package com.example.swiftcheckin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {
    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_content, parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);
        TextView eventTitle = view.findViewById(R.id.event_title_text);
        TextView eventLocation = view.findViewById(R.id.event_location_text);
//        TextView maxAttendees = view.findViewById(R.id.event_max_attend_text);
        TextView description = view.findViewById(R.id.event_description_text);

        eventTitle.setText(event.getEventTitle());
        eventLocation.setText(event.getLocation());
//        maxAttendees.setText(event.getMaxAttendees());
        description.setText(event.getDescription());

        return view;
    }

}
