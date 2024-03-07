package com.example.swiftcheckin;
// This checks to see if the activity switches
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

/**
 * This checks to see if we are able to different activities properly
 */
public class ProfileTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    /**
     * This checks to see if we are able to get to profile and settings
     */
    @Test
    public void activitySwitch(){
        onView(withId(R.id.profile_picture)).perform(click());
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_button)).perform(click());
        onView(withId(R.id.settings)).check(matches(isDisplayed()));
    }

}
