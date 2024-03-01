package com.example.swiftcheckin;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Admin extends AppCompatActivity {
    ListView eventsList;
    ListView profilesList;
    ListView imagesList;
    //ArrayList<Event> dataList;
    //ArrayList<Profile> dataList;
    ArrayList<Image> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        // if event is clicked
        // if profiles is clicked
        // if images is clicked
        //make an image class

    }


}
