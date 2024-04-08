package com.example.swiftcheckin;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.swiftcheckin.attendee.MainActivity;

import org.junit.Rule;
import org.junit.Test;

public class OrgSignUpCheck {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);   // To keep consistent and start at main


    @Test
    public void viewSignedUp() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject allowButton = uiDevice.findObject(new UiSelector().text("Allow").className("android.widget.Button"));
        if (allowButton.waitForExists(3000)) {
            try {
                allowButton.click();

            } catch (UiObjectNotFoundException e) {

                e.printStackTrace();
            }
        }
            onView(withId(R.id.switch_modes)).perform(click());
            onView(withId(R.id.organizer_button)).perform(click());
            onView(withId(R.id.add_event_button)).perform(click());
            onView(withId(R.id.eventName)).perform(ViewActions.clearText(), ViewActions.typeText("Espresso Test For Event Creation"));
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
            // Citation: OpenAI, 03-07-2024, ChatGPT, How to click on a listview item
            // output onData(anything())
            //                .inAdapterView(withId(R.id.your_list_view_id))
            //                .atPosition(0)
            //                .perform(click());
            onData(anything())
                    .inAdapterView(withId(R.id.event_list))
                    .atPosition(0)
                    .perform(longClick());
            // Citation: OpenAI, 03-07-2024, ChatGPT, How to resolve RootViewWithoutFocusException
            // Output: UI needed to be stabilized and some time was needed for things to load.
            /* Provided me with this snippet
            try {
                Thread.sleep(1000); // Adjust the sleep duration as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

             */
            try {
                Thread.sleep(1000); // Adjust the sleep duration as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            onView(withId(R.id.view_sign_up_attendees_button)).perform(ViewActions.click());
            onView(withId(R.id.organizerEventInfo_SignedUpTitle)).check(matches(isDisplayed()));
            onView(withId(R.id.organizerEventInfo_SignedUpTitle)).perform(click());
            onView(withId(R.id.organizerEventInfo_SignedUpTitle)).check(matches(isDisplayed()));
        }

}
