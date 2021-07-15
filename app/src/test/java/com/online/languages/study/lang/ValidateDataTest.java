package com.online.languages.study.lang;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.online.languages.study.lang.data.CheckData;
import com.online.languages.study.lang.data.DataManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class ValidateDataTest {

    private Context mockContext;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private final String PLAIN_TEXT = "\033[0;0m";
    private final String BOLD_TEXT = "\033[0;1m";

    @Before
    public void setUp() {
        mockContext = RuntimeEnvironment.application.getApplicationContext();
    }


    @Test
    public void validateData() {

        int findings = checkData();

        //assertEquals(0, findings );

    }


    public int checkData() {

        CheckData checkData = new CheckData.Builder(mockContext)
                //.onlyGrammar()
                .checkFilter(true)
                .checkBase(true)
                .checkGrammar(true)
                .checkMode(true)
                .checkTranscript(true)
                .checkValue(true)
                //.checkBase(false)
                //.onlyValue()
                .all()

                .build();

        //checkData.checkData();

        String text = "";

        for (String findingText : checkData.findingsData) {
            text += findingText + "\n";
        }

        System.out.println(ANSI_BLUE + "DATA VALIDATED:" + ANSI_RESET + "\n"+ text);

        if (checkData.findingsData.size() == 0) {
            System.out.println(BOLD_TEXT + ANSI_GREEN +  "No issues found" + ANSI_RESET + PLAIN_TEXT);
        }

        return checkData.findingsData.size();

    }


}