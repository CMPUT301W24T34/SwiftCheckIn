package com.example.swiftcheckin;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * This class contains simple UI tests for MyEventActivity.
 */
public class MyEventActivityTest {

    @Rule
    public ActivityScenarioRule<MyEventActivity> scenarioRule = new ActivityScenarioRule<>(MyEventActivity.class);

    /**
     * Tests if the profile button click navigates to ProfileActivity.
     */
    @Test
    public void testProfileButton() {
        onView(withId(R.id.profile_picture)).perform(click());
    }

    /**
     * Tests if the switch mode button click shows the switch mode dialog.
     */
    @Test
    public void testSwitchModeButton() {
        onView(withId(R.id.switch_modes)).perform(click());
    }

    /**
     * Tests if clicking on the camera button navigates to QRCodeScannerActivity.
     */
    @Test
    public void testCameraButton() {
        onView(withId(R.id.camera_button)).perform(click());
    }

    /**
     * Tests if clicking on an event item navigates to AnnoucementActivity.
     */
    @Test
    public void testEventItemClick() {
        onView(withId(R.id.attendee_my_events_list)).perform(click());
    }
}
