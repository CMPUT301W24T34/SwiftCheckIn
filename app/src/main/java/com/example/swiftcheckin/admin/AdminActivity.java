package com.example.swiftcheckin.admin;


import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;


import com.example.swiftcheckin.organizer.Event;

import com.example.swiftcheckin.attendee.Profile;
import com.example.swiftcheckin.R;




import java.util.ArrayList;
import java.util.List;

/**
 * This is the administration activity
 */
public class AdminActivity extends AppCompatActivity {
    private FirestoreAdmin firebaseHelper;

    ListView dataList;
    Button deleteButton;
    Button eventButton;
    Button profileButton;
    Button imageButton;
    ArrayList<Profile> profileList;
    ArrayList<Event> eventList;
    ArrayList<Event> imageList;
    private int selectedPosition = -1;


    String tab;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        deleteButton = findViewById(R.id.remove_tab_button);
        eventButton = findViewById(R.id.event_button);
        profileButton = findViewById(R.id.profile_button);
        imageButton = findViewById(R.id.images_button);
        dataList = findViewById(R.id.listView);
        firebaseHelper = new FirestoreAdmin();
        profileList = new ArrayList<>();
        eventList = new ArrayList<>();
        imageList = new ArrayList<>();
        tab = "Event";
        searchView = findViewById(R.id.searchView);
        ProfileArrayAdapter profileArrayAdapter = new ProfileArrayAdapter(this, profileList);
        AdminEventArrayAdapter eventArrayAdapter = new AdminEventArrayAdapter(this, eventList);
        ImageArrayAdapter imageArrayAdapter = new ImageArrayAdapter(this, imageList);
        //Make the default view the events tab
        displayEventsTab(eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);
        TextView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        //Citation: For the following code idea to use the search bar and filter searches, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to use a search bar to filter profile and event queries
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (tab.equals("Event")) {

                    filterEventList(newText, eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);
                } if (tab.equals("Profile")) {

                    filterProfileList(newText, profileArrayAdapter,eventArrayAdapter,imageArrayAdapter);
                }
                if (tab.equals("Image")) {

                    filterImageList(newText, imageArrayAdapter,eventArrayAdapter,profileArrayAdapter);
                }
                return true;
            }
        });


        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayEventsTab(eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);
            }
        });


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayProfilesTab(profileArrayAdapter,eventArrayAdapter,imageArrayAdapter);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {displayImagesTab(imageArrayAdapter,eventArrayAdapter,profileArrayAdapter);
            }
        });


        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    if (tab.equals("Event")) {
                        deleteEvent(eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);

                    } if (tab.equals("Profile")) {
                        deleteProfile(profileArrayAdapter,eventArrayAdapter,imageArrayAdapter);
                    }
                    if (tab.equals("Image")) {
                        deleteImage(imageArrayAdapter,eventArrayAdapter,profileArrayAdapter);
                    }

                }
            }
        });
    }

    /**
     * This displays the events tab
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */

    private void displayEventsTab(AdminEventArrayAdapter eventArrayAdapter,ImageArrayAdapter imageArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        tab = "Event";
        dataList.setAdapter(eventArrayAdapter);
        firebaseHelper.queryEvents(eventList, eventArrayAdapter, imageArrayAdapter, profileArrayAdapter);
    }
    /**
     * This displays the profiles tab
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */

    private void displayProfilesTab(ProfileArrayAdapter profileArrayAdapter,AdminEventArrayAdapter eventArrayAdapter, ImageArrayAdapter imageArrayAdapter) {
        tab = "Profile";
        dataList.setAdapter(profileArrayAdapter);
        firebaseHelper.queryProfiles(profileList,profileArrayAdapter,eventArrayAdapter,  imageArrayAdapter);
    }
    /**
     * This displays the images tab
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */

    private void displayImagesTab(ImageArrayAdapter imageArrayAdapter,AdminEventArrayAdapter eventArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        tab = "Image";
        dataList.setAdapter(imageArrayAdapter);
        firebaseHelper.queryImages(imageList,  imageArrayAdapter,eventArrayAdapter, profileArrayAdapter);
    }


    /**
     * This deletes the profile and all associated events
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    private void deleteProfile(ProfileArrayAdapter profileArrayAdapter,AdminEventArrayAdapter eventArrayAdapter,ImageArrayAdapter imageArrayAdapter){
        //delete not just profile but all events associated with that profile
        String nameToDelete = profileList.get(selectedPosition).getName();
        firebaseHelper.deleteFirebaseProfile( eventList, nameToDelete, profileList,  selectedPosition, profileArrayAdapter,eventArrayAdapter, imageArrayAdapter);
        selectedPosition = -1;
    }

    /**
     * This deletes the events
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    private void deleteEvent(AdminEventArrayAdapter eventArrayAdapter,ImageArrayAdapter imageArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        tab = "Event";
        firebaseHelper.deleteFirebaseEvent(eventList, selectedPosition,  eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);
        selectedPosition = -1;

    }
    /**
     * This deletes the images
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    private void deleteImage(ImageArrayAdapter imageArrayAdapter,AdminEventArrayAdapter eventArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        tab = "Image";
        firebaseHelper.deleteFirebaseImage(eventList, selectedPosition, imageArrayAdapter, eventArrayAdapter,profileArrayAdapter);
        selectedPosition = -1;
    }

    /**
     * This filters through the events using search
     *@param query the searched word - String
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */
    private void filterEventList(String query, AdminEventArrayAdapter eventArrayAdapter,ImageArrayAdapter imageArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        if (query == null || query.isEmpty()) {
            displayEventsTab(eventArrayAdapter,imageArrayAdapter,profileArrayAdapter);
            return;
        }
        firebaseHelper.filterFirebaseEvents(eventList,query, eventArrayAdapter, imageArrayAdapter,profileArrayAdapter);

    }
    /**
     * This filters through the images using search
     *@param query the searched word - String
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */

    private void filterImageList(String query, ImageArrayAdapter imageArrayAdapter, AdminEventArrayAdapter eventArrayAdapter,ProfileArrayAdapter profileArrayAdapter) {
        if (query == null || query.isEmpty()) {
            displayImagesTab(imageArrayAdapter,eventArrayAdapter,profileArrayAdapter);
            return;
        }
        firebaseHelper.filterFirebaseImageList( imageList, query,  imageArrayAdapter,  eventArrayAdapter, profileArrayAdapter);

    }





    /**
     * This filters through the profiles using search
     * @param query the searched word - String
     *   @param eventArrayAdapter the event array adapter - AdminEventArrayAdapter
     *   @param imageArrayAdapter the image array adapter - ImageArrayAdapter
     *   @param profileArrayAdapter the profile array adapter - ProfileArrayAdapter
     */

    private void filterProfileList(String query, ProfileArrayAdapter profileArrayAdapter, AdminEventArrayAdapter eventArrayAdapter, ImageArrayAdapter imageArrayAdapter) {
        if (query == null || query.isEmpty()) {
            displayProfilesTab(profileArrayAdapter, eventArrayAdapter, imageArrayAdapter);
            return;
        }
        firebaseHelper.filterProfileList(profileList,query,  profileArrayAdapter,  eventArrayAdapter,  imageArrayAdapter);

    }

    /**
     * Return the event list
     */

    public List<Event> getEventList() {
        return eventList;
    }





}




