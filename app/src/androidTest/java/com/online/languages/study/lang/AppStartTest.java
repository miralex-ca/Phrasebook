package com.online.languages.study.lang;


import android.content.Context;
import android.support.annotation.IdRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.online.languages.study.lang.data.DataManager;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AppStartTest {

    @Rule
    public ActivityTestRule<AppStart> mActivityTestRule = new ActivityTestRule<>(AppStart.class);


    @Test
    public void appStartTest() {

        DataManager dataManager = new DataManager(mActivityTestRule.getActivity());
        dataManager.getParams();


        waitTime(250);

        if (dataManager.homecards) {
            onView(ViewMatchers.withId(R.id.recycler_view_cards))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        } else {
            onView(ViewMatchers.withId(R.id.recycler_view_home))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        }


        /// opens section review page
        waitTime(150);
        ViewInteraction openReviewBtn = onView(
                allOf(withId(R.id.openSectionReview),
                        isDisplayed()));
        openReviewBtn.perform(click());

        waitTime(150);
        pressBack(); /// back to section


        // opens section tests list page
        waitTime(100);
        ViewInteraction openTestListBtn = onView(
                allOf(withId(R.id.openSectionTest),
                        isDisplayed()));
        openTestListBtn.perform(click());


        // opens section test page
        waitTime(100);
        ViewInteraction openTest1Btn = onView(
                allOf(withId(R.id.testOne),
                        isDisplayed()));
        openTest1Btn.perform(click());
        pressBack(); // back to section tests list
        pressBack(); // back to section

        /// open first category
        waitTime(100);
        onView(ViewMatchers.withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        /*
        // open details
        waitTime(500);
        onView(ViewMatchers.withId(R.id.my_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        waitTime(300);


        ViewInteraction starBtn = onView(
                allOf(withId(R.id.fab),
                        isDisplayed()));
        starBtn.perform(click());

        waitTime(800);
        pressBack(); //  //back to category


        waitTime(100);
        onView(ViewMatchers.withId(R.id.my_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));

        waitTime(100);
        onView(ViewMatchers.withId(R.id.my_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, longClick()));

        waitTime(100);
        onView(ViewMatchers.withId(R.id.my_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2, longClick()));

        */

        /// swipe to exercises list
        waitTime(500);
        ViewInteraction viewPager = onView(
                allOf(withId(R.id.container),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        viewPager.perform(swipeLeft());


        // open flash cards page
        waitTime(200);
        onView(ViewMatchers.withId(R.id.ex_recycler_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        waitTime(1000);
        pressBack(); // back to category

        // open category test page
        waitTime(100);
        onView(ViewMatchers.withId(R.id.ex_recycler_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        waitTime(1000);
        pressBack(); // back to category

        pressBack(); // back to section

        pressBack(); // back to main


        Context context = mActivityTestRule.getActivity();
        String btmSetting = dataManager.appSettings.getString("btm_nav", context.getString(R.string.set_btm_nav_value_default));

        boolean display = btmSetting.equals(context.getString(R.string.set_btm_nav_value_1)) || btmSetting.equals(context.getString(R.string.set_btm_nav_value_2));

        if (display) { // bottom navigation displayed



            // open starred fragment
            waitTime(500);
            ViewInteraction bottomNavigationStarredView = onView(
                    allOf(withId(R.id.nav_starred),
                            isDisplayed()));
            bottomNavigationStarredView.perform(click());

            // open starred fragment
            waitTime(500);
            ViewInteraction bottomNavigationStatisticsView = onView(
                    allOf(withId(R.id.nav_statistic),
                            isDisplayed()));
            bottomNavigationStatisticsView.perform(click());

            waitTime(500);
            pressBack(); // back to home fragment

        }

        if (!display) {

            onView(withId(R.id.drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                    .perform(DrawerActions.open()); // Open Drawer

            waitTime(500);


            // open starred fragment in navigation
            waitTime(500);


            onView(withId(R.id.drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                    .perform(DrawerActions.open()); // Open Drawer
            waitTime(300);
            onView(withId(R.id.nav_view))
                    .perform(NavigationViewActions.navigateTo(R.id.nav_starred));


            // open stats fragment
            waitTime(500);
            onView(withId(R.id.drawer_layout))
                    .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                    .perform(DrawerActions.open()); // Open Drawer
            waitTime(300);
            onView(withId(R.id.nav_view))
                    .perform(NavigationViewActions.navigateTo(R.id.nav_statistic));

            pressBack(); // back to home fragment

        }


        // open settings fragment
        waitTime(500);
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        waitTime(300);
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_settings));

        // open desc fragment
        waitTime(500);
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        waitTime(300);
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_info));

        // open contacts fragment
        waitTime(500);
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        waitTime(500);
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_contact));


        pressBack(); // back to home fragment


        // open search activity
        waitTime(300);
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.search),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        // enter search "12"
        waitTime(500);
        ViewInteraction searchAutoComplete = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("12"), closeSoftKeyboard());

        waitTime(500);
        pressBack(); // back to main

        waitTime(2000);

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private void waitTime(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    private boolean waitForElementUntilDisplayed(ViewInteraction element) {
        int i = 0;
        while (i++ < 200) {
            try {
                element.check(matches(isDisplayed()));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(2);
                } catch (Exception e1) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static int getCountFromRecyclerView(@IdRes int RecyclerViewId) {
        final int[] COUNT = {0};
        Matcher matcher = new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                COUNT[0] = ((RecyclerView) item).getAdapter().getItemCount();
                return true;
            }
            @Override
            public void describeTo(Description description) {}
        };
        onView(allOf(withId(RecyclerViewId),isDisplayed())).check(matches(matcher));
        return COUNT[0];
    }
}
