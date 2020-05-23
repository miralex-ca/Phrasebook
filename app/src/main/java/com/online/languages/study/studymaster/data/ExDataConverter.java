package com.online.languages.study.studymaster.data;


import android.content.Context;

import com.online.languages.study.studymaster.Constants;
import com.online.languages.study.studymaster.DBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExDataConverter {

    private Context context;
    int limit;

    private ArrayList<DataItem> words = new ArrayList<>();
    public ArrayList<ExDataItem> directData = new ArrayList<>();
    public ArrayList<ExDataItem> inverseData = new ArrayList<>();
    public ArrayList<ExDataItem> addData = new ArrayList<>();

    public ExDataConverter(ArrayList<DataItem> _words, Context _context, int _limit) {
        words = _words;
        limit = _limit;

        context = _context;

        checkProgress();

        convert();
    }



    private void checkProgress() {

        if (limit < words.size()) {

            DBHelper dbHelper = new DBHelper(context);
            words = dbHelper.checkStarredList(words);

            Collections.sort(words, new ScoreCountComparator());

            ArrayList<DataItem> known = new ArrayList<>();
            ArrayList<DataItem> unfamiliar = new ArrayList<>();

            for (DataItem dataItem: words) {

                if (dataItem.rate < Constants.RATE_INCLUDE) {
                    unfamiliar.add(dataItem);

                } else {
                    known.add(dataItem);
                }
            }

            if (unfamiliar.size() < limit) {
                int addNum = (limit - unfamiliar.size());

                Collections.shuffle(known);

                known = new ArrayList<>(known.subList(0, addNum));
            }

            unfamiliar.addAll(known);

            words = new ArrayList<>(unfamiliar);

        }


    }


    private class ScoreCountComparator implements Comparator<DataItem> {
        @Override
        public int compare(DataItem o1, DataItem o2) {
            return ( o1.rate - o2.rate);
        }
    }



    private void convert() {

        for (DataItem word: words) {
            String quest = word.info;
            String questInfo = word.item;
            String answer = word.item;
            String savedInfo = word.id;
            directData.add( new ExDataItem(quest, questInfo, answer, savedInfo) );
        }

        for (DataItem word: words) {
            String quest = word.item;
            String answer = word.info;
            String savedInfo = word.id;
            inverseData.add( new ExDataItem(quest, answer, savedInfo) );
        }

        for (DataItem word: words) {
            String quest = word.info;
            String answer = word.item;
            String savedInfo = word.id;
            addData.add( new ExDataItem(quest, answer, savedInfo) );
        }
    }
}
