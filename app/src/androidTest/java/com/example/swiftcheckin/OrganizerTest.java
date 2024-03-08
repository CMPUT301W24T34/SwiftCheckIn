package com.example.swiftcheckin;

import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.ListView;

import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class OrganizerTest {

    // What tests will I add?
    // Need to see if I can access the organizer Activity.
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /*
    Switch activity
     */
    @Test
    public void organizerSwitch(){
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddEvent(){
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.eventName)).perform(ViewActions.clearText(), ViewActions.typeText("Test 1"));
        onView(withId(R.id.eventPageAddressEditText)).perform(ViewActions.clearText(), ViewActions.typeText("10000 1000"));
        onView(withId(R.id.eventAddActivity_StartDate_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("10182024"));
        onView(withId(R.id.eventAddActivity_eventStartTime_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("7:00"));
        onView(withId(R.id.eventAddActivity_eventEndDate_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("10182024"));
        onView(withId(R.id.eventAddActivity_eventEndTime_EditText)).perform(ViewActions.clearText(), ViewActions.typeText("9:00"));
        onView(withId(R.id.editMaxAttendeeText)).perform(ViewActions.clearText(), ViewActions.typeText("9"));
        onView(withId(R.id.editMaxAttendeeText)).perform(closeSoftKeyboard());
        onView(withId(R.id.eventPageDescriptionEditText)).perform(ViewActions.clearText(), ViewActions.typeText("This is to be tested."));
        onView(withId(R.id.eventPageDescriptionEditText)).perform(closeSoftKeyboard());
        onView(withId(R.id.eventPageSaveButton)).perform(click());
        onView(withId(R.id.fragmentQrCodeMenu1NewButton)).perform(click());
        onView(withId(R.id.qrCodeSelectionSuccessLayout_saveButton)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));

    }

    // Need to see if I can add an event.
    /*
    See if smth pops up. Some sign
     */
    // Need to see if I can view the event.


}
