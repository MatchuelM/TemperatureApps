package com.dvt.temperatureapps;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;


public class MyTestDvt {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void checkEmptyField() {
        onView(withId(R.id.find_place)).perform(ViewActions.clearText()).perform(ViewActions.typeText(""),closeSoftKeyboard());
        onView(withId(R.id.search_btn)).perform(click());
        //Testing a blank Search
        onView(withId(R.id.txtweathr)).check(matches(withText("Please Type Place!!")));
    }

    @Test
    public void checkIntentCall() {
        onView(withId(R.id.find_place)).perform(ViewActions.clearText()).perform(ViewActions.typeText(""),closeSoftKeyboard());

        //Testing that an Intent has been called
        Intents.init();
        Espresso.onView(ViewMatchers.withId(R.id.favourites)).perform(ViewActions.click());
        Intents.release();
    }






}
