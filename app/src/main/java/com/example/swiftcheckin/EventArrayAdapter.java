package com.example.swiftcheckin;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

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
<<<<<<< HEAD
        TextView eventTitle = view.findViewById(R.id.event_title_text);
        TextView eventLocation = view.findViewById(R.id.event_location_text);
        // Fetching event ids for attendee_event section
        TextView eventName = view.findViewById(R.id.event_Title);
        TextView eventDate = view.findViewById(R.id.edit_event_date);
        TextView eventStartTime = view.findViewById(R.id.edit_event_start_time);
        TextView eventEndTime = view.findViewById(R.id.edit_event_end_Time);
        TextView eventAM_PM = view.findViewById(R.id.edit_am_pm);
//        TextView maxAttendees = view.findViewById(R.id.event_max_attend_text);
        TextView description = view.findViewById(R.id.event_description_text);
        ImageView eventPoster = view.findViewById(R.id.event_poster_image);

//        eventTitle.setText(event.getEventTitle());
        eventDate.setText(event.getStartDate());
        eventStartTime.setText(event.getStartTime());
//        eventStartTime.setText(event.getAmPM());
        eventEndTime.setText(event.getEndTime());
        eventAM_PM.setText(event.getEventTitle());

=======
        TextView eventTitle = view.findViewById(R.id.organizerPageItem_eventName);
        ImageView eventPoster = view.findViewById(R.id.organizerPageItem_image);
        TextView eventDateView = view.findViewById(R.id.organizerPageItem_date);
        TextView eventTime = view.findViewById(R.id.organizerPageItem_time);

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        String dateStr = "";

        event.getStartDate();
>>>>>>> 2dd604ac3be75a4a8f201e70a32d6ea743fe267c
        eventTitle.setText(event.getEventTitle());

        if (event.getEventImageUrl() != null)
        {
            Glide.with(getContext())
                    .load(event.getEventImageUrl())
                    .into(eventPoster);
            eventDateView.setText(dateStr);
        }
        else
        {
            eventPoster.setImageResource(R.drawable.test_rect);
        }

        if (event.getStartDate().equals(event.getEndDate()))
        {
            String timeString = event.getStartTime()+" - "+event.getEndTime();
            eventTime.setText(timeString);
        }
        else
        {
            String timeString = event.getStartTime() + "  + 1 Day";
            eventTime.setText(timeString);
        }
        return view;
    }
}
