package com.example.themoviedatabase

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.themoviedatabase.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Test for search view widget
 */
@RunWith(AndroidJUnit4::class)
class SearchViewTests {

    //Rule that executes main activity before test
    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    // Check no results found message, is displayed when searching for a movie
    // that doesn't exist.
    @Test
    fun no_results_found_message_is_displayed() {

        //Click on search view bar
      onView(withId(R.id.action_search))
          .perform(click())

       //Type "aaaaa" on search view text
       onView(withId(com.google.android.material.R.id.search_src_text)).perform(typeText("aaaaa"))

        //Close soft keyboard
            .perform(closeSoftKeyboard())

        //Check no results found textview is displayed
       onView(withId(R.id.no_results_tv))
            .check(matches(isDisplayed()))
    }
}