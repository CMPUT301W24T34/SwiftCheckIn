package com.example.swiftcheckin.attendee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.swiftcheckin.organizer.Event;

import com.example.swiftcheckin.R;

import java.util.List;


/**
 * Adapter for displaying events in a list view.
 */
public class EventViewAdapter extends ArrayAdapter<Event> {

    private Context context;
    private List<Event> events;

    /**
     * Constructs an EventViewAdapter.
     *
     * @param context the context
     * @param events  the list of events to be displayed
     */
    public EventViewAdapter(Context context, List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position    the position of the item within the adapter's data set of the item whose view we want
     * @param convertView the old view to reuse, if possible
     * @param parent      the parent that this view will eventually be attached to
     * @return a View corresponding to the data at the specified position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        } else {
            view = convertView;
        }
        ImageView eventPoster = view.findViewById(R.id.eventPoster2);
        TextView eventName = view.findViewById(R.id.event_Title);
        TextView eventDate = view.findViewById(R.id.edit_event_date);
        TextView eventStartTime = view.findViewById(R.id.edit_event_start_time);
        TextView eventEndTime = view.findViewById(R.id.edit_event_end_Time);

        Event event = events.get(position);

        if (event.getEventImageUrl() != null)
        {
            Glide.with(getContext())
                    .load(event.getEventImageUrl())
                    .into(eventPoster);
        }
        else
        {
            eventPoster.setImageResource(R.drawable.event_poster);
        }

        eventName.setText(event.getEventTitle());
        eventDate.setText(event.getStartDate());
        eventStartTime.setText(event.getStartTime());
        eventEndTime.setText(event.getEndTime());

        return view;
    }
}
