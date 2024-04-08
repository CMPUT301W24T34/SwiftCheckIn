package com.example.swiftcheckin;
// This checks to see if the activity switches to a different mode
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.swiftcheckin.attendee.MainActivity;

import org.junit.Rule;
import org.junit.Test;

/**
 * This checks to see if the switching modes works
 */
public class ModeTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    /**
     * This checks to see if we are able to switch to admin
     */
    @Test
    public void adminSwitch(){
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.admin_button)).perform(click());
        onView(withId(R.id.editTextTextPassword)).perform(ViewActions.clearText(), ViewActions.typeText("SwiftCheckIn"));
        onView(withId(R.id.editTextTextPassword)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.admin)).check(matches(isDisplayed()));
    }
    /**
     * This checks to see if we are able to switch to organizer
     */
    @Test
    public void organizerSwitch(){
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.organizer_button)).perform(click());
        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
    }
}
