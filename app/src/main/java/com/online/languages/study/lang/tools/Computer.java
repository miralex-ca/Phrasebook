package com.online.languages.study.lang.tools;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Computer {

    public Computer() {

    }

    public Map<String, String> getCatProgress(
            ArrayList<String> catIds,
            boolean speaking,
            Map<String, ArrayList<String>> catMapWithTests) {


        String mode = "sound";
        if (!speaking) mode = "nosound";

        Map<String, String> catMapWithProgress = new HashMap<>();


        for (String catId : catIds) {

            ArrayList<String> results = catMapWithTests.get(catId);
            assert results != null;
            int progress = calculateProgressByList(results, mode);

            catMapWithProgress.put(catId, String.valueOf(progress));
        }

        return catMapWithProgress;
    }



    public Map<String, ArrayList<String>> getCatExResults(ArrayList<String> catIdsList, Map<String, String> testsMap) {
        /// create tests results map by categories from test results map

        Map<String, ArrayList<String>> catsResultsMap = new HashMap<>();

        for (String catId : catIdsList) {

            ArrayList<String> results = new ArrayList<>();

            String[] catTests = new String[3];
            catTests[0] = catId + "_1";
            catTests[1] = catId + "_2";
            catTests[2] = catId + "_3";

            for (int i = 0; i < catTests.length; i++) {

                String result = "0";
                if (testsMap.containsKey(catTests[i])) {
                    result = testsMap.get(catTests[i]);
                }
                results.add(result);
            }

            catsResultsMap.put(catId, results);
        }

        return catsResultsMap;
    }


    public int calculateProgressByList(ArrayList<String> results, String mode) {
        //

        int ex1 = 0;
        if (results.get(0) != null) ex1 = Integer.parseInt(results.get(0));

        int ex2 = 0;
        if (results.get(1) != null) ex2 = Integer.parseInt(results.get(1));

        int ex3 = 0;
        if (results.get(2) != null) ex3 = Integer.parseInt(results.get(2));

        int result = (ex1 + ex2 + ex3) / 3;
        if (mode.equals("nosound")) result = (ex1 + ex2) / 2;

        return result;
    }

    public static int calculatePercent(int partCount, int totalCount) {

        int percent = 0;

        if (totalCount > 0)  {

            percent = (partCount*100) / totalCount;
        }

        return percent;
    }


}
