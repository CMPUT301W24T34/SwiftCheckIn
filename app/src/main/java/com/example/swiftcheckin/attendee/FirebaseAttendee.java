package com.example.swiftcheckin.attendee;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.swiftcheckin.R;
import com.example.swiftcheckin.organizer.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// This class has the firebase related methods on the attendee side

public class FirebaseAttendee {
    private FirebaseFirestore db;
    private CollectionReference profilesCollectionRef;
    private String deviceId;

    /**
     * Constructs the FirebaseAttendee object
     */
    public FirebaseAttendee() {
        db = FirebaseFirestore.getInstance();
        profilesCollectionRef = db.collection("profiles");

    }

    /**
     * Returns the database
     * @return db
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * Returns the profile collection reference
     * @return - profile collection reference
     */
    public CollectionReference getProfilesCollectionRef() {
        return profilesCollectionRef;
    }

    // Citation: OpenAI, 03-29-2024, ChatGPT, How to update without overwriting a document
    // told me to use HashMap with a string and object and then use .update()
    /**
     * update the firebase collection with the location data
     * @param deviceId - deviceid of users phone
     * @param data - the data to be updated to firebase
     */
    public void updateLocationInfo(String deviceId, HashMap<String, Object> data){
        db.collection("profiles").document(deviceId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Location data has been added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Location data could not be added");
                    }
                });
    }

    /**
     * Saves profile data to firebase
     * @param deviceId id of users device
     * @param data data to be updated
     */
    public void saveProfileToFirebase(String deviceId, HashMap<String, String> data) {
        profilesCollectionRef.document(deviceId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "User data has been added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "User data could not be added");
                    }
                });
    }

    // Citation: OpenAI, 03-05-2024, ChatGPT, Saving the data as a list in an attribute called eventIds
    /* Chatgpt suggested to use Map<String, Object> data = new HashMap<>() for the list
    output was also about the oncompletelistener and document snapshots and tasks
    giving this code: ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
     and if (document.exists() && document.contains("eventIds")) to see if there is already a list for this user
     and List<String> eventIds = (List<String>) document.get("eventIds") to get it
    */

    /**
     * Saves the attendance data for a specific user and event.
     *
     * @param deviceId the unique identifier of the device
     * @param eventId  the unique identifier of the event
     */
    public void saveSignUpData(String deviceId, String eventId, Context context) {
        DocumentReference ref = db.collection("SignedUpEvents").document(deviceId);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> data = new HashMap<>();

                    // already has a list
                    if (document.exists() && document.contains("eventIds")) {
                        List<String> eventIds = (List<String>) document.get("eventIds");
                        if (!eventIds.contains(eventId)) {
                            eventIds.add(eventId);
                            data.put("eventIds", eventIds);
                            addSignUpData(data,ref, context);
                        }
                        // they've already signed up for this event
                        else{
                            Toast.makeText(context, "You are already signed up for this event", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // no list yet
                    else {
                        List<String> eventIds = new ArrayList<>();
                        eventIds.add(eventId);
                        data.put("eventIds", eventIds);
                        addSignUpData(data, ref, context);
                    }
                }
            }
        });
    }

    /**
     * Adds data to the Firestore database.
     *
     * @param data the data to be added
     * @param ref  the reference to the Firestore document
     */
    private void addSignUpData(Map<String, Object> data, DocumentReference ref, Context context){
        ref.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Signed up!", Toast.LENGTH_SHORT).show();
                        Log.d(ContentValues.TAG, "Event ID added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(ContentValues.TAG, "Error adding event ID", e);
                        Toast.makeText(context, "Could not sign up", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Callback interface used to notify when event data has been fetched.
     */
    interface EventListCallback
    {
        /**
         * Called when event data has been fetched successfully.
         *
         * @param eventList The list of events fetched.
         */
        public void onDataFetched(ArrayList<Event> eventList);
    }


    /**
     * Retrieves the list of events from the database and invokes the callback when data is fetched.
     *
     * @param eventList The list to store the fetched events.
     * @param callback  The callback to notify when event data is fetched.
     */
    public void getEventList(ArrayList<Event> eventList, EventListCallback callback)
    {
        CollectionReference eventCol = db.collection("events");
        eventCol.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                eventList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String eventTitle = (String) doc.getData().get("eventTitle");
                    String eventDescription = (String) doc.getData().get("eventDescription");
                    String eventLocation = (String) doc.getData().get("eventLocation");
                    String deviceId = (String) doc.getData().get("deviceId");
                    String eventImageUrl = (String) doc.getData().get("eventPosterURL");
                    String eventStartDate = (String) doc.getData().get("eventStartDate");
                    String eventStartTime = (String) doc.getData().get("eventStartTime");
                    String eventEndDate = (String) doc.getData().get("eventEndDate");
                    String eventEndTime = (String) doc.getData().get("eventEndTime");
                    String eventMaxAttendees = (String) doc.getData().get("eventMaxAttendees");
                    String eventCurrentAttendees = (String) doc.getData().get("eventCurrentAttendees");

                    com.example.swiftcheckin.organizer.Event event;

                    if (eventMaxAttendees != null && eventMaxAttendees.equals("-1")) {
                        event = new com.example.swiftcheckin.organizer.Event(eventTitle, eventDescription, eventLocation, deviceId,
                                eventImageUrl, eventStartDate, eventEndDate, eventStartTime, eventEndTime);
                    } else {
                        int maxAttendees = eventMaxAttendees != null ? Integer.parseInt(eventMaxAttendees) : 0;
                        event = new com.example.swiftcheckin.organizer.Event(eventTitle, eventDescription, eventLocation, deviceId,
                                eventImageUrl, String.valueOf(maxAttendees), eventStartDate, eventEndDate, eventStartTime, eventEndTime);
                    }

                    int currentAttendees = eventCurrentAttendees != null ? Integer.parseInt(eventCurrentAttendees) : 0;
                    event.setCurrentAttendees(currentAttendees);

                    eventList.add(event);
                }
                callback.onDataFetched(eventList);
            }
        });
    }

    /**
     * Fetches the event IDs of events that the user has signed up for from Firestore,
     * then fetches the corresponding event data and invokes the callback when data is fetched.
     *
     * @param myEventList The list to store the fetched events.
     * @param context     The context to access device-related information.
     * @param callback    The callback to notify when event data is fetched.
     */
    public void getMyEventIds(ArrayList<Event> myEventList, Context context,
                              EventListCallback callback) {

        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference deviceRef = db.collection("SignedUpEvents").document(deviceId);
        deviceRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                List<String> eventIds = new ArrayList<>();
                if (document.exists()) {
                    eventIds = (List<String>) document.get("eventIds");
                }
//                ArrayList<Event> myEventList = new ArrayList<>();
                fetchMyEventsData(eventIds, myEventList, callback);

            } else {
                Log.d("getData", "Error getting documents: ", task.getException());
            }
        });
    }


    /**
     * Fetches event data from Firestore based on event IDs and populates the provided list with the fetched events.
     *
     * @param eventIds     List of event IDs to fetch data for.
     * @param myEventList  The list to populate with fetched event data.
     * @param callback     The callback to notify when data fetching is complete.
     */

    // Citation: OpenAI, 04-06-2024,  ChatGPT, Had some synchronicity issues with the
    // fetchMyEventsData funtion.

    // So I had asked GPT if it could fix the potential issues - I'm getting myEvents list data on the app
    // with my already written functions. Below is the refactored function with the help of GPT

    private void fetchMyEventsData(List<String> eventIds, ArrayList<Event> myEventList, EventListCallback callback) {
        CollectionReference eventCol = db.collection("events");
        myEventList.clear(); // Clear the old list

        if (eventIds.isEmpty()) {
            callback.onDataFetched(myEventList); // If no event IDs, callback immediately
            return;
        }

        final int[] remainingFetches = {eventIds.size()}; // Track remaining fetches

        for (String eventId : eventIds) {
            eventCol.document(eventId).get().addOnCompleteListener(task -> {
                synchronized (remainingFetches) {
                    remainingFetches[0]--;
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Extract event data
                            String eventTitle = document.getString("eventTitle");
                            String eventDescription = document.getString("eventDescription");
                            String eventLocation = document.getString("eventLocation");
                            String deviceId = document.getString("deviceId");
                            String eventImageUrl = document.getString("eventPosterURL");
                            String eventStartDate = document.getString("eventStartDate");
                            String eventStartTime = document.getString("eventStartTime");
                            String eventEndDate = document.getString("eventEndDate");
                            String eventEndTime = document.getString("eventEndTime");
                            String eventMaxAttendees = document.getString("eventMaxAttendees");
                            String eventCurrentAttendees = document.getString("eventCurrentAttendees");

                            // Create the event object
                            Event event = new Event(eventTitle, eventDescription, eventLocation, deviceId, eventImageUrl, eventStartDate, eventEndDate, eventStartTime, eventEndTime);
                            if (eventMaxAttendees != null && !eventMaxAttendees.equals("-1")) {
                                event.setMaxAttendees(Integer.parseInt(eventMaxAttendees));
                            }
                            if (eventCurrentAttendees != null) {
                                event.setCurrentAttendees(Integer.parseInt(eventCurrentAttendees));
                            } else {
                                event.setCurrentAttendees(0);
                            }

                            // Add event to list
                            myEventList.add(event);
                        } else {
                            Log.d("fetchEventsData", "No such document");
                        }
                    } else {
                        Log.d("fetchEventsData", "get failed with ", task.getException());
                    }

                    if (remainingFetches[0] == 0) { // All fetches completed
                        callback.onDataFetched(myEventList);
                    }
                }
            });
        }
    }


    /**
     * to get the profile from firebase
     */
    public interface GetProfileCallback
    {
        public void onProfileFetched(Profile profile);
    }

    /**
     * getting profile from firebase
     * @param deviceid - deviceid of user
     * @param callback - callback
     */
    public void getProfileData(String deviceid, GetProfileCallback callback) {

        DocumentReference profileRef = db.collection("profiles").document(deviceid);

        // Citation: OpenAI, 02-28-2024, ChatGPT, Asking what options i have for snapshots and if i need a query snapshot
        // output was no i can use document snapshot as well, giving me addSnapshotListener(new EventListener<DocumentSnapshot>() and public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
        // I also searched up on stack overflow to see if im on the right track, led me to the following citation
        // Citation: Collecting a document from firebase, Stack Overflow, License: CC-BY-SA, user name Frank van Puffelen, "How to fix a null object reference on document snapshot", 2022-04-28, https://stackoverflow.com/questions/72042682/how-to-fix-a-null-object-reference-on-document-snapshot
        profileRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (value != null) {
                    // Citation: OpenAI, 02-28-2024, ChatGPT, Asking if i should convert the document to a profile object
                    // output was yes it can be done directly and told me about value.toObject.
                    // I then searched this up further on stack overflow to see if I was on the right track and the following citation also helped me incorporate this idea
                    // Citation: Converting document to class object, Stack Overflow, License: CC-BY-SA, user name Matheus Padovani (edited by Doug Stevenson), "How to directly convert Data Snapshot to object?", 2020-06-17 (edited 2020-06-17), https://stackoverflow.com/questions/62436421/how-to-directly-convert-data-snapshot-to-object
                    Profile profile = value.toObject(Profile.class);
                    callback.onProfileFetched(profile);
                }
            }
        });

    }

    /**
     * if user has location checkbox checked
     */
    public interface IsLocationPermission
    {
        public void GetLocationCallback(boolean bool);
    }

    // Citation: OpenAI, 04-04-2024, ChatGPT, How to find out if locationPermission is on by getting a document
    // gave me .addOnCompleteListener(task -> {
    //            if (task.isSuccessful()) {
    //                DocumentSnapshot document = task.getResult();
    //                if (document.exists()) {
    /**
     * to find out if user has location checkbox checked
     * @param deviceId - device id of user
     * @param context - context
     * @param callback - using the callback method
     */

    public void getLocation(String deviceId, Context context, IsLocationPermission callback) {

        DocumentReference profileRef = db.collection("profiles").document(deviceId);
        profileRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String locationPermission = document.getString("locationPermission");
                    if (locationPermission != null && locationPermission.equals("True")) {
                        callback.GetLocationCallback(true);

                    }
                }
            }
        });

    }

    /**
     * This method handles the scanned QR code's logic and redirects the user to either
     * PromoQR or checks them in depending on its type.
     * @param scannedQRCode
     * @param activity
     */
    public void handleScannedQRCode(String scannedQRCode, QRCodeScannerActivity activity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference qrCodeRef = db.collection("qrcodes").document(scannedQRCode);

        qrCodeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot qrDocument = task.getResult();
                if (qrDocument.exists()) {
                    String isPromoString = qrDocument.getString("isPromo");
                    boolean isPromo = "true".equalsIgnoreCase(isPromoString);

                    String eventId = qrDocument.getString("eventID");
                    if (eventId != null) {
                        if (isPromo) {
                            fetchEventDetailsAndRedirect(eventId, activity);
                        } else {
                            checkInAttendee(eventId, activity);
                        }
                    } else {
                        Log.e("QRCodeScannerActivity", "Event ID is missing in the QR code document.");
                        activity.showDialog("Error", "Event details cannot be found.");
                    }
                } else {
                    activity.showDialog("Error", "QR Code is not valid.");
                }
            } else {
                Log.e("FirestoreError", "Error fetching QR code document: ", task.getException());
                activity.showDialog("Error", "Failed to retrieve QR code information. Please try again.");
            }
        });
    }

    /**
     * Fetches event details from Firestore and redirects to the event details page.
     *
     * @param eventId  the ID of the event to fetch details for
     * @param activity the activity that initiated the fetch
     */
    private void fetchEventDetailsAndRedirect(String eventId, QRCodeScannerActivity activity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot eventSnapshot = task.getResult();
                if (eventSnapshot.exists()) {
                    Intent intent = new Intent(activity, AnnoucementActivity.class);
                    intent.putExtra("eventID", eventId);
                    intent.putExtra("eventTitle", eventSnapshot.getString("eventTitle"));
                    intent.putExtra("eventDescription", eventSnapshot.getString("eventDescription"));
                    intent.putExtra("eventLocation", eventSnapshot.getString("eventLocation"));
                    intent.putExtra("eventStartDate", eventSnapshot.getString("eventStartDate"));
                    intent.putExtra("eventEndDate", eventSnapshot.getString("eventEndDate"));
                    intent.putExtra("eventStartTime", eventSnapshot.getString("eventStartTime"));
                    intent.putExtra("eventEndTime", eventSnapshot.getString("eventEndTime"));
                    intent.putExtra("eventPosterURL", eventSnapshot.getString("eventPosterURL"));
                    intent.putExtra("eventMaxAttendees", eventSnapshot.getString("eventMaxAttendees"));
                    intent.putExtra("eventCurrentAttendees", eventSnapshot.getString("eventCurrentAttendees"));
                    activity.startActivity(intent);

                } else {
                    Log.e("QRCodeScannerActivity", "Event document does not exist.");
                    activity.showDialog("Error", "Event details cannot be found.");
                }
            } else {
                Log.e("FirestoreError", "Error fetching event details: ", task.getException());
                activity.showDialog("Error", "Failed to retrieve event details. Please try again.");
            }
        });

    }


    /**
     * Checks in an attendee to an event.
     *
     * @param scannedEventId the ID of the event to check in to
     * @param activity       the activity that initiated the check-in
     */
    public void checkInAttendee(String scannedEventId, QRCodeScannerActivity activity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceId = activity.retrieveDeviceId();

        DocumentReference deviceRef = db.collection("SignedUpEvents").document(deviceId);
        deviceRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> eventIds = (List<String>) document.get("eventIds");
                    if (eventIds != null && eventIds.contains(scannedEventId)) {
                        activity.eventSignUp.addCheckedIn(scannedEventId, deviceId);
                        activity.locationReceiver.getLocation(deviceId, activity);
                        activity.showDialog("Check-in Successful", "You have been checked in successfully!");
                    } else {
                        activity.showDialog("Check-in Failed", "You did not sign up for this event");
                    }
                } else {
                    activity.showDialog("Check-in Failed", "You did not sign up for this event");
                }
            }
            else if (task.getException() != null) {
                Log.e("FirestoreError", "Error fetching document: ", task.getException());
                activity.showDialog("Error", "No event exists");
            }
            else {
                Log.e("FirestoreError", "Error fetching document: ", task.getException());
                activity.showDialog("Error", "Failed to retrieve event information. Please try again.");
            }
        });
    }

    /**
     * Uploads an image to Firebase Storage under the path 'profileImages/{userId}.jpg'.
     * This method first retrieves the user's unique ID to use as part of the storage path.
     * Upon successful upload, it retrieves the download URL of the uploaded image and saves this URL in Firestore.
     * If the upload fails, it logs an error message.
     *
     * @param imageUri The URI of the image to be uploaded. This should not be null and must
     *                 point to a valid image file.
     * @param context  The context of the calling activity.
     */
    public void uploadImageToFirebaseStorage(Uri imageUri, Context context) {
        String userId = getUserId(context);

        StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("profileImages/" + userId + ".jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        saveProfileImageUrlToFirestore(downloadUri.toString(), context);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseAttendee", "Image upload failed", e);
                });
    }

    /**
     * Saves the profile image URL to Firestore.
     *
     * @param imageUrl The URL of the uploaded image.
     * @param context  The context of the calling activity.
     */
    private void saveProfileImageUrlToFirestore(String imageUrl, Context context) {
        String deviceId = getUserId(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles").document(deviceId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Image URL saved to Firestore, now load it into the ImageView
                    ImageView avatarImage = ((ProfileActivity) context).findViewById(R.id.avatarImage);
                    Glide.with(context)
                            .load(imageUrl)
                            .into(avatarImage);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseAttendee", "Error saving image URL to Firestore", e);
                });
    }

    /**
     * Retrieves the user's unique ID.
     *
     * @param context The context of the calling activity.
     * @return The user's unique ID.
     */
    private String getUserId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Removes the profile image from Firebase Storage and updates the Firestore document.
     *
     * @param context The context of the calling activity.
     */
    public void removePhoto(Context context) {
        String userId = getUserId(context);
        StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("profileImages/" + userId + ".jpg");
        profileImageRef.delete().addOnSuccessListener(aVoid -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("profiles").document(userId)
                    .update("profileImageUrl", "")
                    .addOnSuccessListener(aVoid1 -> {
                        // Generate the avatar image
                        TextView nameText = ((ProfileActivity) context).findViewById(R.id.nameText);
                        String profileName = nameText.getText().toString();
                        if (profileName != null && !profileName.isEmpty()) {
                            AvatarGenerator.generateAvatar(profileName, new AvatarGenerator.AvatarImageCallback() {
                                @Override
                                public void onAvatarLoaded(Bitmap avatar) {
                                    ((ProfileActivity) context).runOnUiThread(() -> {
                                        ImageView avatarImage = ((ProfileActivity) context).findViewById(R.id.avatarImage);
                                        avatarImage.setImageBitmap(avatar);
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseAttendee", "Error removing image URL from Firestore", e);
                    });
        }).addOnFailureListener(e -> {
            Log.e("FirebaseAttendee", "Error removing image from Storage", e);
        });
    }


}