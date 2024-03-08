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

<<<<<<< HEAD
=======

/**
 * Adapter for displaying events in a list view.
 */
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
public class EventViewAdapter extends ArrayAdapter<Event> {

    private Context context;
    private List<Event> events;

<<<<<<< HEAD
=======
    /**
     * Constructs an EventViewAdapter.
     *
     * @param context the context
     * @param events  the list of events to be displayed
     */
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
    public EventViewAdapter(Context context, List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

<<<<<<< HEAD
=======
    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position    the position of the item within the adapter's data set of the item whose view we want
     * @param convertView the old view to reuse, if possible
     * @param parent      the parent that this view will eventually be attached to
     * @return a View corresponding to the data at the specified position
     */
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
<<<<<<< HEAD
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
=======
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        } else {
            view = convertView;
        }
<<<<<<< HEAD
//        ImageView eventPoster = view.findViewById(R.id.event_poster1);
=======
        ImageView eventPoster = view.findViewById(R.id.organizerPageItem_image);
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
        TextView eventName = view.findViewById(R.id.event_Title);
        TextView eventDate = view.findViewById(R.id.edit_event_date);
        TextView eventStartTime = view.findViewById(R.id.edit_event_start_time);
        TextView eventEndTime = view.findViewById(R.id.edit_event_end_Time);
<<<<<<< HEAD
        TextView eventAmPm = view.findViewById(R.id.edit_am_pm);
        // Set more fields as required

        Event event = events.get(position);
//        eventPoster.setImageURI(event.);
=======

        Event event = events.get(position);
//        eventPoster.setImageURI(event.getEventImageUrl());
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
        eventName.setText(event.getEventTitle());
        eventDate.setText(event.getStartDate());
        eventStartTime.setText(event.getStartTime());
        eventEndTime.setText(event.getEndTime());
<<<<<<< HEAD
//        eventAmPm.setText(event.getAmPM());
        // Populate more fields as required
=======
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c

        return view;
    }
}
