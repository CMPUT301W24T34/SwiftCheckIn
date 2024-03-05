package com.example.swiftcheckin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEventActivity extends AppCompatActivity {

    private String deviceId;
    Event CurrentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        deviceId = getIntent().getStringExtra("deviceId");

        // making button onclick listeners
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

        // textWatcher to enforce proper Date format in start and end date
        EditText startDateEditText = findViewById(R.id.eventAddActivity_StartDate_EditText);
        EditText endDateEditText = findViewById(R.id.eventAddActivity_eventEndDate_EditText);

        createTextWatcher(startDateEditText);
        createTextWatcher(endDateEditText);
    }

    private void createTextWatcher(EditText editText)
    {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            // enforcing a few parameters in XML, and adding '/' at proper indexes
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2 || s.length() == 5) {
                    s.append("/");
                }
            }
        });
    }

    protected void editAddEvent()
    {
        EditText eventNameEditText = findViewById(R.id.eventName);
        String eventName = eventNameEditText.getText().toString();

        EditText addressEditText = findViewById(R.id.eventPageAddressEditText);
        String eventAddress = addressEditText.getText().toString();

        EditText eventStartDateEditText = findViewById(R.id.eventAddActivity_StartDate_EditText);
        String eventStartDate = eventStartDateEditText.getText().toString();

        EditText eventEndDateEditText = findViewById(R.id.eventAddActivity_eventEndDate_EditText);
        String eventEndDate = eventEndDateEditText.getText().toString();

        EditText eventStartTimeEditText = findViewById(R.id.eventAddActivity_eventStartTime_EditText);
        String eventStartTime = eventStartTimeEditText.getText().toString();

        EditText eventEndTimeEditText = findViewById(R.id.eventAddActivity_eventEndTime_EditText);
        String eventEndTime = eventEndTimeEditText.getText().toString();

        EditText eventDescriptionEditText = findViewById(R.id.eventPageDescriptionEditText);
        String eventDescription = eventDescriptionEditText.getText().toString();
        Toast.makeText(getApplicationContext(), eventDescription, Toast.LENGTH_LONG).show();

        Intent intent = new Intent("com.example.ADD_EVENT");
        intent.putExtra("eventName", eventName);
        intent.putExtra("eventAddress", eventAddress);
        intent.putExtra("eventStartDate", eventStartDate);
        intent.putExtra("eventEndDate", eventEndDate );
        intent.putExtra("eventStartTime", eventStartTime);
        intent.putExtra("eventEndTime", eventEndTime);
        intent.putExtra("eventDescription", eventDescription);

        sendBroadcast(intent);
        finish();
    }
}
