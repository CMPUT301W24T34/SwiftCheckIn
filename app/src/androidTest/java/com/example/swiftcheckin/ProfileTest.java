package com.example.swiftcheckin;
// This checks to see if the activity switches and info updates
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
 * This checks to see if we are able to switch to different activities properly and update profile info
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
    // Citation: OpenAI, 03-07-2024, ChatGPT, How to clear edittext field
    // output was ViewActions.clearText()
    /**
     * This checks to see if the inputted values in settings get updated on the profile page
     */
    @Test
    public void infoUpdates(){
        onView(withId(R.id.profile_picture)).perform(click());
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_button)).perform(click());
        onView(withId(R.id.settings)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).perform(ViewActions.clearText(), ViewActions.typeText("John"));
        onView(withId(R.id.word_name)).perform(click());
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
        onView(withId(R.id.nameText)).check(matches(withText("John")));

    }

}
