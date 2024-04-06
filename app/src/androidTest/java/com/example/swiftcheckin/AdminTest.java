package com.example.swiftcheckin;
import androidx.test.espresso.IdlingResource;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;


import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.ListView;

import com.example.swiftcheckin.admin.AdminActivity;
import com.example.swiftcheckin.attendee.MainActivity;
import com.example.swiftcheckin.attendee.Profile;
import com.example.swiftcheckin.organizer.Event;

import java.util.List;

public class AdminTest {
    /**
     * This checks to see if admin updates properly
     */

    private int i;


    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);   // To keep consistent and start at main


    /**
     * Test meant to see if the user is able to switch to organizer mode.
     */
    @Test
    public void testDeleteEvent(){
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.eventName)).perform(ViewActions.clearText(), ViewActions.typeText("Delete Event Test"));
        onView(withId(R.id.eventPageAddressEditText)).perform(ViewActions.clearText(), ViewActions.typeText("10000 1000"));
        onView(withId(R.id.eventAddActivity_StartDate_EditText)).perform(ViewActions.clearText(), replaceText("Apr 5 2024"));
        onView(withId(R.id.eventAddActivity_eventStartTime_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("7:00"));
        onView(withId(R.id.eventAddActivity_eventEndDate_EditText)).perform(ViewActions.clearText(),replaceText("Apr 5 2024"));
        onView(withId(R.id.eventAddActivity_eventEndTime_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("9:00"));
        onView(withId(R.id.editMaxAttendeeText)).perform(ViewActions.clearText(), ViewActions.typeText("9"));
        onView(withId(R.id.editMaxAttendeeText)).perform(closeSoftKeyboard());
        onView(withId(R.id.eventPageDescriptionEditText)).perform(ViewActions.clearText(), ViewActions.typeText("This is to be tested."));
        onView(withId(R.id.eventPageDescriptionEditText)).perform(closeSoftKeyboard());
        onView(withId(R.id.eventPageSaveButton)).perform(click());
        onView(withId(R.id.fragmentQrCodeMenu1NewButton)).perform(click());
        onView(withId(R.id.qrCodeSelectionSuccessLayout_saveButton)).perform(click());
        pressBack();
        onView(withId(R.id.admin_button)).perform(click());

        onView(withId(R.id.editTextTextPassword)).perform(ViewActions.clearText(), ViewActions.typeText("SwiftCheckIn"));
        onView(withId(R.id.login_button )).perform(click());


        ActivityScenario<AdminActivity> activityScenario = ActivityScenario.launch(AdminActivity.class);
        activityScenario.onActivity(activity -> {
            List<Event> eventList = activity.getEventList(); assertNotNull(eventList);

            // Assert that eventList contains the expected event
            i = 0;
            boolean eventFound = false;
            for (Event event : eventList) {
                if (event.getEventTitle().equals("Delete Event Test")) { // Assuming 'getName()' returns the event name
                    // Event found, set flag to true
                    eventFound = true;
                    break;
                }
                i++;
            }
            // Assert that the event was found in the list
            assertTrue("Expected event not found in the event list", eventFound);
        });

        // call the event list from admin activity and check to see if the event "Delete Event Test"
        // then get the index of the event
        // click the list at that index
        //then click remove_button
        //assert that the event is no longer in event list
        onData(anything())
                .inAdapterView(withId(R.id.listView))
                .atPosition(i) // Replace 0 with the position of the item if it's not the first one
                .onChildView(withId(R.id.event_name))
                .check(matches(withText("Delete Event Test")))
                .perform(click());

        onView(withId(R.id.remove_tab_button))
                .check(matches(isDisplayed())) // Check if the button is visible
                .perform(click()); // Try clicking the button

// Optionally, you can introduce a delay before clicking
        try {
            Thread.sleep(5000); // 1 second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// Retry clicking the button
        onView(withId(R.id.remove_tab_button)).perform(click());




 activityScenario.onActivity(activity -> {
List<Event> eventList = activity.getEventList(); assertNotNull(eventList);
//
//            // Assert that eventList contains the expected event
boolean eventFound = false;
for (Event event : eventList) {
if (event.getEventTitle().equals("Delete Event Test")) { // Assuming 'getName()' returns the event name
//                    // Event found, set flag to true
eventFound = true;
          break;
             }
            }
//            // Assert that the event was found in the list
           assertFalse("Expected event found in the event list", eventFound);
        });

    }
    @Test
    public void testDeleteProfile(){
        onView(withId(R.id.profile_picture)).perform(click());
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_button)).perform(click());
        onView(withId(R.id.settings)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).perform(ViewActions.clearText(), ViewActions.typeText("DeleteProfileTest"));
        onView(withId(R.id.word_name)).perform(click());
        onView(withId(R.id.save_button)).perform(click());

        onView(withId(R.id.admin_button)).perform(click());

        onView(withId(R.id.editTextTextPassword)).perform(ViewActions.clearText(), ViewActions.typeText("SwiftCheckIn"));
        onView(withId(R.id.login_button )).perform(click());
        onView(withId(R.id.profile_button)).perform(click());


        ActivityScenario<AdminActivity> activityScenario = ActivityScenario.launch(AdminActivity.class);
        activityScenario.onActivity(activity -> {
            List<Profile> profileList = activity.getProfileList(); assertNotNull(profileList);

            // Assert that eventList contains the expected event
            i = 0;
            boolean profileFound = false;
            for (Profile profile : profileList) {
                if (profile.getName().equals("DeleteProfileTest")) { // Assuming 'getName()' returns the event name
                    // Event found, set flag to true
                    profileFound = true;
                    break;
                }
                i++;
            }
            // Assert that the event was found in the list
            assertTrue("Expected profile not found in the event list", profileFound);
        });

        // call the event list from admin activity and check to see if the event "Delete Event Test"
        // then get the index of the event
        // click the list at that index
        //then click remove_button
        //assert that the event is no longer in event list
        onData(anything())
                .inAdapterView(withId(R.id.listView))
                .atPosition(i) // Replace 0 with the position of the item if it's not the first one
                .onChildView(withId(R.id.name_text))
                .check(matches(withText("DeleteProfileTest")))
                .perform(click());

        onView(withId(R.id.remove_tab_button))
                .check(matches(isDisplayed())) // Check if the button is visible
                .perform(click()); // Try clicking the button

// Optionally, you can introduce a delay before clicking
        try {
            Thread.sleep(5000); // 1 second delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

// Retry clicking the button
        onView(withId(R.id.remove_tab_button)).perform(click());




        activityScenario.onActivity(activity -> {
            List<Profile> profileList = activity.getProfileList(); assertNotNull(profileList);
//
//            // Assert that eventList contains the expected event
            boolean profileFound = false;
            for (Profile profile : profileList) {
                if (profile.getName().equals("DeleteProfileTest")) { // Assuming 'getName()' returns the event name
                    // Event found, set flag to true
                    profileFound = true;
                    break;
                }
            }
//            // Assert that the event was found in the list
            assertFalse("Expected event found in the event list",profileFound);
        });

    }

}


