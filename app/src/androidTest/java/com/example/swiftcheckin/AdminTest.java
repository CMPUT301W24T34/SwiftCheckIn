package com.example.swiftcheckin;
import androidx.test.espresso.IdlingResource;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;


import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.ListView;

import com.example.swiftcheckin.admin.AdminActivity;
import com.example.swiftcheckin.admin.ProfileArrayAdapter;
import com.example.swiftcheckin.attendee.MainActivity;
import com.example.swiftcheckin.attendee.Profile;
import com.example.swiftcheckin.organizer.Event;

import java.util.List;

public class AdminTest {
    /**
     * This checks to see if admin updates properly
     */

    private int i;
    private UiDevice device;


    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);


    /**
     * Test if a user can delete an event
     */
    @Test
    public void testDeleteEvent(){
        //Citation: For the following ui device lines, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to click allow to a pop up in an android test
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject allowButton = uiDevice.findObject(new UiSelector().text("Allow").className("android.widget.Button"));


        if (allowButton.waitForExists(9000)) {
            try {
                allowButton.click();


            } catch (UiObjectNotFoundException e) {

                e.printStackTrace();
            }

            onView(withId(R.id.switch_modes)).perform(click());
            onView(withId(R.id.organizer_button)).perform(click());
            onView(withId(R.id.add_event_button)).perform(click());
            onView(withId(R.id.eventName)).perform(ViewActions.clearText(), ViewActions.typeText("Delete Event Test"));
            onView(withId(R.id.eventPageAddressEditText)).perform(ViewActions.clearText(), ViewActions.typeText("10000 1000"));onView(withId(R.id.eventAddActivity_StartDate_EditText)).perform(click());
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
            pressBack();
            onView(withId(R.id.switch_modes)).perform(click());
            onView(withId(R.id.admin_button)).perform(click());

            onView(withId(R.id.editTextTextPassword)).perform(ViewActions.clearText(), ViewActions.typeText("SwiftCheckIn"));
            onView(withId(R.id.editTextTextPassword)).perform(closeSoftKeyboard());
            onView(withId(R.id.login_button)).perform(click());

            //Citation: For the following activity lines, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to get a list from another activity
            ActivityScenario<AdminActivity> activityScenario = ActivityScenario.launch(AdminActivity.class);
            activityScenario.onActivity(activity -> {
                List<Event> eventList = activity.getEventList();
                assertNotNull(eventList);


                i = 0;
                boolean eventFound = false;
                for (Event event : eventList) {
                    if (event.getEventTitle().equals("Delete Event Test")) {
                        eventFound = true;
                        break;
                    }
                    i++;
                }

                assertTrue("Expected event not found in the event list", eventFound);
            });

            //Citation: For the following data lines, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to click a list at a certain index
            onData(anything())
                    .inAdapterView(withId(R.id.listView))
                    .atPosition(i)
                    .onChildView(withId(R.id.event_name))
                    .check(matches(withText("Delete Event Test")))
                    .perform(click());

            onView(withId(R.id.remove_tab_button))
                    .check(matches(isDisplayed()))
                    .perform(click());


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            onView(withId(R.id.remove_tab_button)).perform(click());


            activityScenario.onActivity(activity -> {
                List<Event> eventList = activity.getEventList();
                assertNotNull(eventList);

                boolean eventFound = false;
                for (Event event : eventList) {
                    if (event.getEventTitle().equals("Delete Event Test")) {

                        eventFound = true;
                        break;
                    }
                }

                assertFalse("Expected event found in the event list", eventFound);
            });

        }else{

            onView(withId(R.id.switch_modes)).perform(click());
            onView(withId(R.id.organizer_button)).perform(click());
            onView(withId(R.id.add_event_button)).perform(click());
            onView(withId(R.id.eventName)).perform(ViewActions.clearText(), ViewActions.typeText("Delete Event Test"));
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
            pressBack();
            onView(withId(R.id.switch_modes)).perform(click());
            onView(withId(R.id.admin_button)).perform(click());

            onView(withId(R.id.editTextTextPassword)).perform(ViewActions.clearText(), ViewActions.typeText("SwiftCheckIn"));
            onView(withId(R.id.editTextTextPassword)).perform(closeSoftKeyboard());
            onView(withId(R.id.login_button)).perform(click());

            //Citation: For the following activity lines, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to get a list from another activity
            ActivityScenario<AdminActivity> activityScenario = ActivityScenario.launch(AdminActivity.class);
            activityScenario.onActivity(activity -> {
                List<Event> eventList = activity.getEventList();
                assertNotNull(eventList);


                i = 0;
                boolean eventFound = false;
                for (Event event : eventList) {
                    if (event.getEventTitle().equals("Delete Event Test")) {
                        eventFound = true;
                        break;
                    }
                    i++;
                }

                assertTrue("Expected event not found in the event list", eventFound);
            });

            //Citation: For the following data lines, Licensing: Creative Commons, OpenAI, 2024, ChatGPT, Prompt: How to click a list at a certain index
            onData(anything())
                    .inAdapterView(withId(R.id.listView))
                    .atPosition(i)
                    .onChildView(withId(R.id.event_name))
                    .check(matches(withText("Delete Event Test")))
                    .perform(click());

            onView(withId(R.id.remove_tab_button))
                    .check(matches(isDisplayed()))
                    .perform(click());


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            onView(withId(R.id.remove_tab_button)).perform(click());


            activityScenario.onActivity(activity -> {
                List<Event> eventList = activity.getEventList();
                assertNotNull(eventList);

                boolean eventFound = false;
                for (Event event : eventList) {
                    if (event.getEventTitle().equals("Delete Event Test")) {

                        eventFound = true;
                        break;
                    }
                }

                assertFalse("Expected event found in the event list", eventFound);
            });
        }
    }

}
