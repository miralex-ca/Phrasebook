package com.online.languages.study.lang.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.online.languages.study.lang.Constants.EX_AUDIO_TYPE;

public class CatData {

    public String title;
    public String desc;
    public String tag;

    public String id;


    // exercises results
    public int ex_item_info = 0;   // Ex 1
    public int ex_info_item = 0;   // Ex 2
    public int ex_audio = 0;       // Ex 3 audio


    public String ex_item_info_tag = "_1";
    public String ex_info_item_tag = "_2";
    public String ex_audio_tag = "_" + EX_AUDIO_TYPE;


    public int progress = 0;

    public int masteredNum = 0;
    public int familiarNum = 0;
    public int itemsNum = 0;

    public CatData() {}


    public void checkTests(Map<String, String>  tests) {







    }

}
