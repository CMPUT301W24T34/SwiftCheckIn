package com.example.swiftcheckin.organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.swiftcheckin.R;
import com.example.swiftcheckin.organizer.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;


/**
 * This is an adapter to bind ListView to ArrayList containing Event class objects.
 * Extends ArrayAdapter<Event>
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {

    /**
     * Constructor of the EventArrayAdapter
     * @param context Context(environment/activity) in which the adapter is being created.
     * @param events  ArrayList containing event objects.
     */
    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return            A View corresponding to the data at the specified position.
     */
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
        TextView eventTitle = view.findViewById(R.id.organizerPageItem_eventName);
        ImageView eventPoster = view.findViewById(R.id.organizerPageItem_image);
        TextView eventDateView = view.findViewById(R.id.organizerPageItem_date);
        TextView eventTime = view.findViewById(R.id.organizerPageItem_time);


        eventTitle.setText(event.getEventTitle());
        eventDateView.setText(event.getStartDate());
        if (event.getEventImageUrl() != null)
        {
            Glide.with(getContext())
                    .load(event.getEventImageUrl())
                    .into(eventPoster);
        }
        else
        {
            eventPoster.setImageResource(R.drawable.test_rect);
        }

        String timeString;
        if (event.getStartDate().equals(event.getEndDate()))
        {
            timeString = event.getStartTime() + " - " + event.getEndTime();
        }
        else
        {
            
            timeString = event.getStartTime() + "  + 1 Day";
        }
        eventTime.setText(timeString);
        return view;
    }
}
