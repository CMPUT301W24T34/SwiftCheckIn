package com.example.swiftcheckin;

import static android.app.PendingIntent.getActivities;
import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.greaterThan;

import android.view.View;
import android.widget.ListView;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.swiftcheckin.attendee.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

/**
 * This is a test to test the basic tasks of an Organizer, which is to add events and view who has signed up as of now.
 * Prerequisite to running these tests is that the organizer must start with no events.
 */
public class OrganizerTest {


    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);   // To keep consistent and start at main


//    public void clearEvents(){
//        onView(withId(R.id.switch_modes)).perform(click());
//        onView(withId(R.id.organizer_button)).perform(click());
//        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
//
//        ListView eventList = getContext()
//
//
//        }

    /**
     * Test meant to see if the user is able to switch to organizer mode.
     */
    @Test
    public void organizerSwitch() {
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
    }

    /**
     * Test to see if the user can add an event. This test in particular assumes that the user has created no events.
     */
    @Test
    public void testAddEvent() {

        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
        // Citation: OpenAI, 03-07-2024, ChatGPT, How to get items in a listview
        // output was onView(withId(R.id.list_view_id)).check(matches(hasChildCount(0)));

//        onView(withId(R.id.event_list)).check(matches(hasChildCount(1)));

        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.eventName)).perform(ViewActions.clearText(), ViewActions.typeText("Espresso Test for Event Creation"));
        onView(withId(R.id.eventPageAddressEditText)).perform(ViewActions.clearText(), ViewActions.typeText("10000 1000"));
        onView(withId(R.id.eventAddActivity_StartDate_EditText)).perform(ViewActions.clearText(), replaceText("Apr 5 2024"));
        onView(withId(R.id.eventAddActivity_eventStartTime_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("7:00"));
        onView(withId(R.id.eventAddActivity_eventEndDate_EditText)).perform(ViewActions.clearText(), replaceText("Apr 5 2024"));
        onView(withId(R.id.eventAddActivity_eventEndTime_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("9:00"));
        onView(withId(R.id.editMaxAttendeeText)).perform(ViewActions.clearText(), ViewActions.typeText("9"));
        // Citation: How to close the keyboard using espressso, Stack Overflow, License: CC-BY-SA, user name Tonny Tonny, "I can't make ViewActions.closeSoftKeyboard() work in Espresso 2.2.2", 2013-09-08,
        // https://stackoverflow.com/questions/39580415/i-cant-make-viewactions-closesoftkeyboard-work-in-espresso-2-2-2
        onView(withId(R.id.editMaxAttendeeText)).perform(closeSoftKeyboard());
        onView(withId(R.id.eventPageDescriptionEditText)).perform(ViewActions.clearText(), ViewActions.typeText("This is to be tested."));
        onView(withId(R.id.eventPageDescriptionEditText)).perform(closeSoftKeyboard());
        onView(withId(R.id.eventPageSaveButton)).perform(click());
        onView(withId(R.id.fragmentQrCodeMenu1NewButton)).perform(click());
        onView(withId(R.id.qrCodeSelectionSuccessLayout_saveButton)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
//        onView(withId(R.id.event_list)).check(matches(hasChildCount(2)));

        // Citation: Check if a ListView has an specific a number of items, and scroll to last one with Espresso, Stack Overflow, License: CC-BY-SA, user name Anatolii, "Check if a ListView has an specific a number of items, and scroll to last one with Espresso",
        // 2015-03-15
        // https://stackoverflow.com/questions/21604351/check-if-a-listview-has-an-specific-a-number-of-items-and-scroll-to-last-one-wi
        final int[] counts = new int[1];
        onView(withId(R.id.event_list)).check(matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;

                counts[0] = listView.getCount();

                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));

        assertThat(counts[0], greaterThan(0));


    }

    /**
     * Tests to see if the user can view a sign up.
     * By this point, an event has been added, so we can see if that activity has been opened.
     */
    @Test
    public void testViewSignup() {
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
        // Citation: OpenAI, 03-07-2024, ChatGPT, How to click on a listview item
        // output onData(anything())
        //                .inAdapterView(withId(R.id.your_list_view_id))
        //                .atPosition(0)
        //                .perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(longClick());

        // Citation: OpenAI, 03-07-2024, ChatGPT, How to resolve RootViewWithoutFocusException
        // Output: UI needed to be stabilized and some time was needed for things to load.
        /* Provided me with this snippet
        try {
            Thread.sleep(1000); // Adjust the sleep duration as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

         */
        try {
            Thread.sleep(1000); // Adjust the sleep duration as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.view_sign_up_attendees_button)).perform(ViewActions.click());
        onView(withId(R.id.viewAttendeeBackButton)).check(matches(isDisplayed()));

    }

    @Test
    public void testViewAddAnnouncementActivity() {
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
//        addEvent();


        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(longClick());
        try {
            Thread.sleep(1000); // Adjust the sleep duration as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //onView(withId(R.id.send_notifications_button)).perform(ViewActions.click());
        onView(withId(R.id.addAnnouncementCancelButton)).check(matches(isDisplayed()));
    }
}

//    public void addEvent(){
//        // Citation: OpenAI, 03-07-2024, ChatGPT, How to get items in a listview
//        // output was onView(withId(R.id.list_view_id)).check(matches(hasChildCount(0)));
//
//        onView(withId(R.id.add_event_button)).perform(click());
//        onView(withId(R.id.eventName)).perform(ViewActions.clearText(), ViewActions.typeText("Preset"));
//        onView(withId(R.id.eventPageAddressEditText)).perform(ViewActions.clearText(), ViewActions.typeText("10000 1000"));
//        onView(withId(R.id.eventAddActivity_StartDate_EditText)).perform(ViewActions.clearText(), replaceText("Apr 5 2024"));
//        onView(withId(R.id.eventAddActivity_eventStartTime_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("7:00"));
//        onView(withId(R.id.eventAddActivity_eventEndDate_EditText)).perform(ViewActions.clearText(),replaceText("Apr 5 2024"));
//        onView(withId(R.id.eventAddActivity_eventEndTime_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("9:00"));
//        onView(withId(R.id.editMaxAttendeeText)).perform(ViewActions.clearText(), ViewActions.typeText("9"));
//        // Citation: How to close the keyboard using espressso, Stack Overflow, License: CC-BY-SA, user name Tonny Tonny, "I can't make ViewActions.closeSoftKeyboard() work in Espresso 2.2.2", 2013-09-08,
//        // https://stackoverflow.com/questions/39580415/i-cant-make-viewactions-closesoftkeyboard-work-in-espresso-2-2-2
//        onView(withId(R.id.editMaxAttendeeText)).perform(closeSoftKeyboard());
//        onView(withId(R.id.eventPageDescriptionEditText)).perform(ViewActions.clearText(), ViewActions.typeText("This is to be tested."));
//        onView(withId(R.id.eventPageDescriptionEditText)).perform(closeSoftKeyboard());
//        onView(withId(R.id.eventPageSaveButton)).perform(click());
//        onView(withId(R.id.fragmentQrCodeMenu1NewButton)).perform(click());
//        onView(withId(R.id.qrCodeSelectionSuccessLayout_saveButton)).perform(click());
//        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
//
//    }
//
//    @Test
//    public void testCheckedIn(){
//        onView(withId(R.id.switch_modes)).perform(click());
//        onView(withId(R.id.organizer_button)).perform(click());
//        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
//
//
//        onData(anything())
//                .inAdapterView(withId(R.id.event_list))
//                .atPosition(0)
//                .perform(longClick());
//
//        try {
//            Thread.sleep(1000); // Adjust the sleep duration as needed
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        onView(withId(R.id.view_check_in_attendees_button)).perform(ViewActions.click());
//        onView(withId(R.id.viewCheckInBackButton)).check(matches(isDisplayed()));

//    @Test
//    public void viewMap(){
//        onView(withId(R.id.switch_modes)).perform(click());
//        onView(withId(R.id.organizer_button)).perform(click());
//        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
//
//        onData(anything())
//                .inAdapterView(withId(R.id.event_list))
//                .atPosition(0)
//                .perform(longClick());
//
//        try {
//            Thread.sleep(1000); // Adjust the sleep duration as needed
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        onView(withId(R.id.geolocation_switch)).perform(ViewActions.click());
//        onView(withId(R.id.geolocation_switch)).check(matches(isChecked()));
//
//        try {
//            Thread.sleep(1000); // Adjust the sleep duration as needed
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        onView(withId(R.id.view_map_button)).perform(ViewActions.click());
//
//        try {
//            Thread.sleep(3000);  // To give some time to let the map load.
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        onView(withId(R.id.map)).check(matches(isDisplayed()));
//    }