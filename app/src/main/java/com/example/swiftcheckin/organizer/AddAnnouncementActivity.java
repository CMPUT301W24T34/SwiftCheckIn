package com.example.swiftcheckin.organizer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.swiftcheckin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;


public class AddAnnouncementActivity extends AppCompatActivity {

    private String eventId;

    /**
     * Creates the AddAnnouncementActivity.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);

        eventId = getIntent().getStringExtra("eventId");

        String eventNameString = getIntent().getStringExtra("eventName");
        Log.d("Event Name - Add Announcement", eventNameString);
        TextView eventName = findViewById(R.id.addAnnouncementEventName);
        Button cancelAnnouncement = findViewById(R.id.addAnnouncementCancelButton);
        Button saveAnnouncement = findViewById(R.id.addAnnouncementSaveButton);
        EditText editAnnouncementHeading = findViewById((R.id.announcementEditHeading));
        EditText editAnnouncementDes = findViewById(R.id.announcementEditDes);

        eventName.setText(eventNameString);

        cancelAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnnouncementToFirebase();
                sendAnnouncementToServer(editAnnouncementHeading.getText().toString(), editAnnouncementDes.getText().toString());
                // Toast.makeText(getApplicationContext(), "To be continued", Toast.LENGTH_SHORT).show();
                finish();
            }
        });



    }

    /**
     * Saves an announcement to Firebase Firestore.
     */
    public void saveAnnouncementToFirebase(){
        // 1. Get the edittexts here
        EditText editAnnouncementHeading = findViewById((R.id.announcementEditHeading));
        String announcementHeading = editAnnouncementHeading.getText().toString();

        EditText editAnnouncementDes = findViewById(R.id.announcementEditDes);
        String announcementDes = editAnnouncementDes.getText().toString();
        // 2. Get the instance of Firebase here
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 3. Create a collection and create announcement object.

        DocumentReference eventWithAnnounce = db.collection("Announcements").document(eventId + announcementHeading);
        DocumentReference newAnnouncements = db.collection("Announcements@").document(eventId + "@" + announcementHeading);

        HashMap<String, String> data = new HashMap<>();
        data.put("announcementTitle", announcementHeading);
        data.put("announcementDes", announcementDes);

        eventWithAnnounce
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {   // In the event, the event is added.
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Announcement Added Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener(){ // In the event, the event fails to be added to Firebase.
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Announcement could not be added.");
                    }
                });

        newAnnouncements
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Announcement Added Successfully");
                        Toast.makeText(AddAnnouncementActivity.this, "Announcement Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener(){ // In the event, the event fails to be added to Firebase.
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Announcement could not be added.");
                        Toast.makeText(AddAnnouncementActivity.this, "Announcement could not be added.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Sends an announcement to the server using Firebase Cloud Messaging (FCM).
     * This method constructs a JSON payload and sends it via HTTP POST request to FCM server.
     * @param title       the title of the announcement
     * @param description the description of the announcement
     * Citation: OpenAI | April 3 , 2024 | ChatGPT | Help in sending an announcement to the server
     */
    public void sendAnnouncementToServer(String title, String description) {
        HashMap<String, String> announcementDetails = new HashMap<>();
        announcementDetails.put("title", title);
        announcementDetails.put("description", description);

        // Create a JSON payload for the notification
        JSONObject notification = new JSONObject();
        try {
            notification.put("title", title);
            notification.put("body", description);

            JSONObject data = new JSONObject();
            data.put("eventId", eventId);

            // Remove whitespace from eventId
            String topicName = "event_" + eventId.replaceAll("\\s+", "_");

            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("to", "/topics/" + topicName); // Include the /topics/ prefix
            jsonPayload.put("notification", notification);
            jsonPayload.put("data", data);

            // Send the JSON payload to FCM server using HTTP POST request
            String fcmServerKey = "AAAACN8H9G8:APA91bGnK-j4ge9u-ioFzTNAqjBOHEyIPgiS4Km_hOTaGNRqqfPOw00u5dh_CGW66L1YYcJc9yDL7aXoQprHTVuZjXHUgQhG4EuPuh1HG0zZ6Z0wUMM_DOtgF-8a2zfkWX_eR050x2bt";
            String fcmUrl = "https://fcm.googleapis.com/fcm/send";

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fcmUrl, jsonPayload,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Notification sent successfully");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error sending notification: " + error.getMessage());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "key=" + fcmServerKey);
                    return headers;
                }
            };

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}