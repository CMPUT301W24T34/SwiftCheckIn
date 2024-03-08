package com.example.swiftcheckin;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * This class contains simple UI tests for MyEventActivity.
 */
public class MyEventActivityTest {

    @Rule
    public ActivityScenarioRule<MyEventActivity> scenarioRule = new ActivityScenarioRule<>(MyEventActivity.class);

    /**
     * Tests if clicking on the profile button navigates to ProfileActivity.
     */
    @Test
    public void testProfileButton() {
        onView(withId(R.id.profile_picture)).perform(click());
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
    }

    /**
     * Tests if clicking on the switch mode button shows the switch mode dialog.
     */
    @Test
    public void testSwitchModeButton() {
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_button)).check(matches(isDisplayed()));
    }

    /**
     * Tests if clicking on the camera button navigates to QRCodeScannerActivity.
     */
    @Test
    public void testCameraButton() {
        onView(withId(R.id.camera_button)).perform(click()).check(matches(isDisplayed()));
    }

    /**
     * Tests if clicking on an event item in the list navigates to AnnoucementActivity.
     * Note: This assumes there are items in the list.
     */
    @Test
    public void testEventItemClick() {
        onView(withId(R.id.attendee_my_events_list)).perform(click());
        onView(withId(R.id.attendee_my_events_list)).check(matches(isDisplayed()));
    }
}
