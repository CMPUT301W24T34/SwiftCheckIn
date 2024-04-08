package com.example.swiftcheckin.organizer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.swiftcheckin.R;

import java.util.ArrayList;

/**
 * Adapter class for displaying reusable QR codes in a ListView or GridView.
 */
public class ReuseQrAdapter extends ArrayAdapter<Qr_Code> {

    /**
     * Constructs a new ReuseQrAdapter.
     *
     * @param context         The context.
     * @param reusableQrList  The list of reusable QR codes.
     */
    public ReuseQrAdapter(@NonNull Context context, ArrayList<Qr_Code> reusableQrList) {
        super(context, 0, reusableQrList);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.organizer_menu1_item, parent, false);
        }
        else
        {
            view = convertView;
        }

        Qr_Code code = getItem(position);
        ImageView imgQr = view.findViewById(R.id.fragment_qrcode_reuse_choice_img);
        imgQr.setImageBitmap(code.getImage());

        return view;
    }
}
