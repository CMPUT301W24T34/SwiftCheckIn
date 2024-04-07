package com.example.swiftcheckin.organizer;

import android.content.Context;
import android.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.swiftcheckin.R;
import com.example.swiftcheckin.attendee.Profile;

import java.util.ArrayList;

public class CheckInArrayAdapter extends ArrayAdapter<Pair<String, String>> {


    public CheckInArrayAdapter(@NonNull Context context, ArrayList<Pair<String, String>> pairList) {
        super(context, 0, pairList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.organizer_info_checkedin_item, parent, false);
        }
        else
        {
            view = convertView;
        }

        Pair<String, String> pair = getItem(position);
        TextView attendeeName = view.findViewById(R.id.organizerEventInfo_item_CheckedInName);
        TextView attendeeCount = view.findViewById(R.id.organizerEventInfo_item_CheckedInCount);
        assert pair != null;
        attendeeName.setText(pair.first);

        if(pair.second.equals("None"))
        {
            attendeeCount.setText("Signed Up");
        }
        else
        {
            attendeeCount.setText(pair.second);
        }


        return view;
    }
}
