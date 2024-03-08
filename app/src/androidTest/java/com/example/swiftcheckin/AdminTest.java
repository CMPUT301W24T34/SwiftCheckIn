package com.example.swiftcheckin;

import static android.content.ContentValues.TAG;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import org.junit.Before;
import org.junit.Test;


import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminTest {
    /**
     * This checks to see if admin updates properly
     */


    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * This checks to see if profile udpates properly in admin
     */
    @Test
    public void testProfileInFirebase() {

        onView(withId(R.id.profile_picture)).perform(click());
        onView(withId(R.id.settings_button)).perform(click());
        onView(withId(R.id.name)).perform(ViewActions.clearText(), ViewActions.typeText("Admintest"));
        onView(withId(R.id.word_name)).perform(click());
        onView(withId(R.id.save_button)).perform(click());
        pressBack();
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.admin_button)).perform(click());
        onView(withId(R.id.profile_button)).perform(click());
        onView(withText("Admintest")).check(matches(isDisplayed()));
    }

}