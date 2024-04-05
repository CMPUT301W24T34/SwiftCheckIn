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
        return null;
    }
}
