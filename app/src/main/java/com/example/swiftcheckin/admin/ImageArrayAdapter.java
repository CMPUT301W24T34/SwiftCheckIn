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
import com.example.swiftcheckin.R;
import com.example.swiftcheckin.organizer.Event;

import java.util.ArrayList;
/**
 * This is the image array adapter for admin
 */
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
        //Citation: For the following code to upload an image into an array adapter, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to add an image into the array adapter
        ImageView eventImage = view.findViewById(R.id.imageView);

        if (event != null && event.getEventImageUrl() != null && !event.getEventImageUrl().isEmpty()) {
            Glide.with(getContext())
                    .load(event.getEventImageUrl())
                    .into(eventImage);
        } else {
            //Citation: For the following code line for a default image, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to make a default image in case no image is provided
            eventImage.setImageResource(R.drawable.event_poster);
        }
        return view;
    }
}
