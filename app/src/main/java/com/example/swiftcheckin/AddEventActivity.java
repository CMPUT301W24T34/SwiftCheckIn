package com.example.swiftcheckin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddEventActivity extends AppCompatActivity {

    Event CurrentEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Button cancelButton = findViewById(R.id.eventPageCancelButton);
        Button saveButton = findViewById(R.id.eventPageSaveButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAddEvent();
            }
        });
    }

    protected void editAddEvent()
    {
        EditText eventNameEditText = findViewById(R.id.eventName);
        String eventName = eventNameEditText.getText().toString();

        EditText addressEditText = findViewById(R.id.address);
    }
}
