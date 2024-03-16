package com.example.swiftcheckin;

import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.swiftcheckin.attendee.MainActivity;

import org.junit.Rule;
import org.junit.Test;

/**
 * This is a test to test the basic tasks of an Organizer, which is to add events and view who has signed up as of now.
 */
public class OrganizerTest {


    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);   // To keep consistent and start at main

    /**
     * Test meant to see if the user is able to switch to organizer mode.
     */
    @Test
    public void organizerSwitch(){
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
    }

    /**
     * Test to see if the user can add an event. This test in particular assumes that the user has created no events.
     */
    @Test
    public void testAddEvent(){

        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
        // Citation: OpenAI, 03-07-2024, ChatGPT, How to get items in a listview
        // output was onView(withId(R.id.list_view_id)).check(matches(hasChildCount(0)));
        onView(withId(R.id.event_list)).check(matches(hasChildCount(0)));
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.eventName)).perform(ViewActions.clearText(), ViewActions.typeText("Test 1"));
        onView(withId(R.id.eventPageAddressEditText)).perform(ViewActions.clearText(), ViewActions.typeText("10000 1000"));
        onView(withId(R.id.eventAddActivity_StartDate_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("10182024"));
        onView(withId(R.id.eventAddActivity_eventStartTime_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("7:00"));
        onView(withId(R.id.eventAddActivity_eventEndDate_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("10182024"));
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
        onView(withId(R.id.event_list)).check(matches(hasChildCount(1)));
    }

    /**
     * Tests to see if the user can view a sign up.
     * By this point, an event has been added, so we can see if that activity has been opened.
     */
    @Test
    public void testViewSignup(){
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
                .perform(click());
        onView(withId(R.id.viewAttendeeBackButton)).check(matches(isDisplayed()));

    }
}
