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

public class ModeTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void adminSwitch(){
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.admin_button)).perform(click());
        onView(withId(R.id.admin)).check(matches(isDisplayed()));
    }
    @Test
    public void organizerSwitch(){
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
    }
}
