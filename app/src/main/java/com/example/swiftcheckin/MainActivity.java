package com.example.swiftcheckin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button qrButton = findViewById(R.id.btn_qr);
        Button adminButton = findViewById(R.id.btn_admin);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //switch view to admin
                Intent intent = new Intent(MainActivity.this, PromotionQR.class);
                startActivity(intent);

            }
        });

        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //switch view to admin
                Intent intent = new Intent(MainActivity.this, Admin.class);
                startActivity(intent);

            }
        });
    }

}