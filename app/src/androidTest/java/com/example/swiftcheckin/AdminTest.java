package com.example.swiftcheckin;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import static com.google.common.base.CharMatcher.is;
import static com.google.common.base.Predicates.equalTo;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.IdRes;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import org.junit.Before;
import org.junit.Test;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.CoreMatchers.is;

public class AdminTest {
    private FirebaseFirestore db;

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);


    @Before
    public void setUp() {
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    @Test
    public void testDeleteProfileInFirebase() {
        // Assuming you have a profile with the name "Admin Test" in Firebase before running the test

        // Perform the steps to navigate to the admin profile deletion screen

        onView(withId(R.id.profile_picture)).perform(click());
        onView(withId(R.id.settings_button)).perform(click());
        onView(withId(R.id.name)).perform(typeText("Admintest"));
        onView(withId(R.id.save_button)).perform(click());
        pressBack();
        pressBack();
        pressBack();
        onView(withId(R.id.switch_modes)).perform(click());
        onView(withId(R.id.admin_button)).perform(click());

        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            // Assuming your ListView has an ID, replace R.id.listView with the actual ID
            ListView listView = activity.findViewById(R.id.listView);
            ProfileArrayAdapter adapter = (ProfileArrayAdapter) listView.getAdapter();
            int listSize = adapter.getCount();
            onData(anything())
                    .inAdapterView(withId(R.id.listView))  // Replace R.id.listView with the actual ID
                    .atPosition(listSize - 1)
                    .perform(click());
            onView(withId(R.id.remove_tab_button)).perform(click());

            // Verify that the profile is no longer present in Firebase
            Query query = db.collection("profiles").whereEqualTo("name", "Admintest");
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    // Assert that the query result is empty, indicating the profile is deleted
                    assertTrue(querySnapshot.isEmpty());
                } else {
                    // Handle the error
                }
            });
        });
    }

//    }
//    public void testDeleteEventInFirebase() {
//        // Assuming you have a profile with the name "Admin Test" in Firebase before running the test
//        ListView listView = scenario.getActivity().findViewById(R.id.listView);
//
//        // Get the adapter and the size of the list
//        ProfileArrayAdapter adapter = (ProfileArrayAdapter) listView.getAdapter();
//        int listSize = adapter.getCount();
//
//
//        // Perform the steps to navigate to the admin profile deletion screen
//        onView(withId(R.id.switch_modes)).perform(click());
//        onView(withId(R.id.organizer_button)).perform(click());
//        onView(withId(R.id.organizer)).check(matches(isDisplayed()));
//        onView(withId(R.id.add_event_button)).check(matches(isDisplayed()));
//        onView(withId(R.id.eventName)).perform(typeText("Admin Test"));
//        onView(withId(R.id.eventPageSaveButton)).check(matches(isDisplayed()));
//        pressBack();
//        pressBack();
//        pressBack();
//        onView(withId(R.id.switch_modes)).perform(click());
//        onView(withId(R.id.admin_button)).perform(click());
//        onView(withId(R.id.event_button)).perform(click());
//        onData(anything())
//                .inAdapterView(withId(R.id.listView))  // Replace R.id.listView with the actual ID
//                .atPosition(listSize - 1)
//                .perform(click());
//        onView(withId(R.id.remove_tab_button)).perform(click());
//
//        // Verify that the profile is no longer present in Firebase
//        Query query = db.collection("events").whereEqualTo("eventTitle", "Admin Test");
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                QuerySnapshot querySnapshot = task.getResult();
//                // Assert that the query result is empty, indicating the profile is deleted
//                assertTrue(querySnapshot.isEmpty());
//            } else {
//                // Handle the error
//            }
//        });
//
//    }


}