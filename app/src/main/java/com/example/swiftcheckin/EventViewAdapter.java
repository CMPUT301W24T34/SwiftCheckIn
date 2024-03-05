package com.example.swiftcheckin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class EventViewAdapter extends ArrayAdapter<Event> {

    private Context context;
    private List<Event> events;

    public EventViewAdapter(Context context, List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        } else {
            view = convertView;
        }
//        ImageView eventPoster = view.findViewById(R.id.event_poster1);
        TextView eventName = view.findViewById(R.id.event_Title);
        TextView eventDate = view.findViewById(R.id.edit_event_date);
        TextView eventStartTime = view.findViewById(R.id.edit_event_start_time);
        TextView eventEndTime = view.findViewById(R.id.edit_event_end_Time);
        TextView eventAmPm = view.findViewById(R.id.edit_am_pm);
        // Set more fields as required

        Event event = events.get(position);
//        eventPoster.setImageURI(event.);
        eventName.setText(event.getEventTitle());
        eventDate.setText(event.getDate());
        eventStartTime.setText(event.getStartTime());
        eventEndTime.setText(event.getEndTime());
        eventAmPm.setText(event.getAmPM());
        // Populate more fields as required

        return view;
    }
}
