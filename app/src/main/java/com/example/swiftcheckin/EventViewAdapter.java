package com.example.swiftcheckin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class EventViewAdapter extends BaseAdapter {

    private Context context;
    private List<Event> events;

    public EventViewAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        }

        TextView eventName = convertView.findViewById(R.id.event_name);
        TextView eventDate = convertView.findViewById(R.id.edit_event_date);
        TextView eventStartTime = convertView.findViewById(R.id.edit_event_start_time);
        TextView eventEndTime = convertView.findViewById(R.id.edit_event_end_Time);
        TextView eventAmPm = convertView.findViewById(R.id.edit_am_pm);
        // Set more fields as required

        Event event = events.get(position);
        eventName.setText(event.getEventTitle());
        eventDate.setText(event.getDate());
        eventStartTime.setText(event.getStartTime());
        eventEndTime.setText(event.getEndTime());
        eventAmPm.setText(event.getAmPM());
        // Populate more fields as required

        return convertView;
    }
}
