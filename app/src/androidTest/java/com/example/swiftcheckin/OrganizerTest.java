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

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

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

    /**
     * Test meant to see if the user is able to switch to organizer mode.
     */
    @Test
    public void organizerSwitch() {

        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject allowButton = uiDevice.findObject(new UiSelector().text("Allow").className("android.widget.Button"));
        if (allowButton.waitForExists(3000)) {
            try {
                allowButton.click();

            } catch (UiObjectNotFoundException e) {

                e.printStackTrace();
            }
        } else {
            onView(withId(R.id.switch_modes)).perform(click());
            onView(withId(R.id.organizer_button)).perform(click());
            onView(withId(R.id.add_event_button)).perform(click());
        }
    }

    @Test
    public void testAddEvent(){
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject allowButton = uiDevice.findObject(new UiSelector().text("Allow").className("android.widget.Button"));
        if (allowButton.waitForExists(3000)) {
            try {
                allowButton.click();

            } catch (UiObjectNotFoundException e) {

                e.printStackTrace();
            }
        }
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.add_event_button)).perform(click());
        onView(withId(R.id.eventName)).perform(ViewActions.clearText(), ViewActions.typeText("Espresso Test For Event Creation"));
        onView(withId(R.id.eventPageAddressEditText)).perform(ViewActions.clearText(), ViewActions.typeText("10000 1000"));
        onView(withId(R.id.eventAddActivity_StartDate_EditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.eventAddActivity_eventStartTime_EditText)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.eventAddActivity_eventEndDate_EditText)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.eventAddActivity_eventEndTime_EditText)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.editMaxAttendeeText)).perform(ViewActions.clearText(), ViewActions.typeText("9"));
        onView(withId(R.id.editMaxAttendeeText)).perform(closeSoftKeyboard());
        onView(withId(R.id.eventPageDescriptionEditText)).perform(ViewActions.clearText(), ViewActions.typeText("This is to be tested."));
        onView(withId(R.id.eventPageDescriptionEditText)).perform(closeSoftKeyboard());
        onView(withId(R.id.eventPageSaveButton)).perform(click());
        onView(withId(R.id.fragmentQrCodeMenu1NewButton)).perform(click());
        onView(withId(R.id.qrCodeSelectionSuccessLayout_saveButton)).perform(click());
            onView(withId(R.id.organizer)).check(matches(isDisplayed()));
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
    }