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
        TextView eventTitle = view.findViewById(R.id.organizerPageItem_eventName);
        ImageView eventPoster = view.findViewById(R.id.organizerPageItem_image);
        TextView eventDateView = view.findViewById(R.id.organizerPageItem_date);
        TextView eventTime = view.findViewById(R.id.organizerPageItem_time);

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        String dateStr = "";

        try
        {
            assert event != null;
            dateStr = outputFormat.format(Objects.requireNonNull(inputFormat.parse(event.getStartDate())));
        } catch(ParseException e)
        {
            e.printStackTrace();
        }

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
