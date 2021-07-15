package com.online.languages.study.lang;

import android.content.Context;
import android.os.Build;

import com.online.languages.study.lang.data.DataManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


import static org.junit.Assert.*;

@Config(sdk = {Build.VERSION_CODES.O_MR1})
@RunWith(RobolectricTestRunner.class)
public class PreReleaseTest {

    private Context mockContext;



    @Before
    public void setUp() {
        mockContext = RuntimeEnvironment.application.getApplicationContext();
    }


    @Test
    public void pro_isCorrect() throws Exception {
        assertFalse(Constants.PRO);
    }

    @Test
    public void debug_isCorrect() throws Exception {
        assertFalse(Constants.DEBUG);
    }

    @Test
    public void jsonParams_areCorrect() throws Exception {
        DataManager dataManager = new DataManager(mockContext);
        dataManager.getParamsFromJSON();
        assertFalse(dataManager.simplified);
        assertFalse(dataManager.homecards);

    }


    @Test
    public void limits_areCorrect() throws Exception {
        assertEquals(100, Constants.UDATA_LIMIT);
        assertEquals(20, Constants.UDATA_LIMIT_UNPAID);
        assertEquals(4, Constants.UCATS_UNPAID_LIMIT);
        assertEquals(2, Constants.GROUPS_UNPAID_LIMIT);
        assertEquals(10, Constants.LAUNCHES_BEFORE_RATE);
        assertTrue(Constants.SHOW_GRAMMAR);

    }



}