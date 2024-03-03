package com.example.swiftcheckin;
// This checks to see if the activity switches
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class ProfileTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void activitySwitch(){
        onView(withId(R.id.main)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_button)).perform(click());
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_button)).perform(click());
        onView(withId(R.id.settings)).check(matches(isDisplayed()));
    }
}
