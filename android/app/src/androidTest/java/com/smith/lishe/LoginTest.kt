package com.smith.lishe

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    // Start activity
    @get:Rule()
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testEmailValidation() {
        onView(withId(R.id.login_button)).perform(click())
        onView(withId(R.id.login_email_edit_text)).check(matches(hasErrorText("Enter a valid email address")))

    }

    @Test
    fun testPasswordValidation() {
        onView(withId(R.id.login_button)).perform(click())
        onView(withId(R.id.login_password_edit_text)).check(matches(hasErrorText("Password cannot be less than 8")))

    }
}