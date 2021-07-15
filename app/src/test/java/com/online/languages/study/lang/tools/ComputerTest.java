package com.online.languages.study.lang.tools;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ComputerTest {

    Computer SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new Computer();
    }


    //

    @Test
    public void  calculateCatProgressByTestsResultsList_withNoSound_returnResultBy2Tests() {

        ArrayList<String> results = new ArrayList<>();
        results.add("50");
        results.add("50");
        results.add("100");

        int result = SUT.calculateProgressByList(results, "nosound");
        Assert.assertThat(result, is(50));

    }

    @Test
    public void  calculateCatProgressByTestsResultsList_withSound_returnResultBy3Tests() {

        ArrayList<String> results = new ArrayList<>();
        results.add("50");
        results.add("50");
        results.add("100");

        int result = SUT.calculateProgressByList(results, "sound");
        Assert.assertThat(result, is(66));

    }

    @Test
    public void computer_getCatExResults_returnResults() {

        Map<String, ArrayList<String>> expectedResults = new HashMap<>();
        ArrayList<String> testResults = new ArrayList<>();
        testResults.add("30");
        testResults.add("0");
        testResults.add("0");

        expectedResults.put("cat_id", testResults);

        ArrayList<String> idsList = new ArrayList<>();  // list of categories by id
        idsList.add("cat_id");
        Map<String, String> testsMap = new HashMap<>();  // list of tests (not sorted)
        testsMap.put("cat_id_1", "30");

        Map<String, ArrayList<String>> results = SUT.getCatExResults(idsList, testsMap);

        Assert.assertThat(results, is(expectedResults));

    }


    @Test
    public void computer_getCatProgressWithSound_returnResults() {

        Map<String, String> expectedResultsMap = new HashMap<>();
        expectedResultsMap.put("cat_id", "40");

        Map<String, ArrayList<String>> inputResults = new HashMap<>();
        ArrayList<String> testResults = new ArrayList<>();
        testResults.add("30");
        testResults.add("30");
        testResults.add("60");
        inputResults.put("cat_id", testResults);

        ArrayList<String> idsList = new ArrayList<>();
        idsList.add("cat_id");

        Map<String, String> resultsMap = SUT.getCatProgress(idsList, true, inputResults);

        Assert.assertThat(resultsMap, is(expectedResultsMap));

    }

    @Test
    public void computer_getCatProgressWithNoSound_returnResults() {

        Map<String, String> expectedResultsMap = new HashMap<>();
        expectedResultsMap.put("cat_id", "30");

        Map<String, ArrayList<String>> inputResults = new HashMap<>();
        ArrayList<String> testResults = new ArrayList<>();
        testResults.add("30");
        testResults.add("30");
        testResults.add("60");
        inputResults.put("cat_id", testResults);

        ArrayList<String> idsList = new ArrayList<>();
        idsList.add("cat_id");

        Map<String, String> resultsMap = SUT.getCatProgress(idsList, false, inputResults);

        Assert.assertThat(resultsMap, is(expectedResultsMap));

    }


    @Test
    public void computer_calculatePercent_returnPercent() {

        int percent = SUT.calculatePercent(20, 100);

        Assert.assertThat(percent, is(20));

        percent = SUT.calculatePercent(20, 0);

        Assert.assertThat(percent, is(0));

        percent = SUT.calculatePercent(20, 40);

        Assert.assertThat(percent, is(50));

    }


}