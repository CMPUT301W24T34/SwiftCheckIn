package com.example.swiftcheckin.admin;

import static java.security.AccessController.getContext;

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
import com.example.swiftcheckin.Event;
import com.example.swiftcheckin.R;

import java.util.ArrayList;

public class ImageArrayAdapter extends ArrayAdapter<Event> {
    public ImageArrayAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_image_content, parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);
        //cite the below code for chat gpt
        ImageView eventImage = view.findViewById(R.id.imageView);

        if (event != null && event.getEventImageUrl() != null && !event.getEventImageUrl().isEmpty()) {
            Glide.with(getContext())
                    .load(event.getEventImageUrl())
                    .into(eventImage);
        } else {
            // Set default image resource if image URL is empty
            eventImage.setImageResource(R.drawable.event_poster);
        }
        return view;
    }
}
