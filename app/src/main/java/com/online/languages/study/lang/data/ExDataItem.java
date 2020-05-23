package com.online.languages.study.lang.data;

public class ExDataItem {
    public String quest;
    public String questInfo;
    public String answer;
    public String savedInfo;

    public ExDataItem(String _quest, String _answer, String _savedInfo) {
        quest = _quest;
        answer = _answer;
        savedInfo = _savedInfo;
    }

    public ExDataItem(String _quest, String _questInfo, String _answer, String _savedInfo) {
        quest = _quest;
        questInfo = _questInfo;
        answer = _answer;
        savedInfo = _savedInfo;
    }



}
