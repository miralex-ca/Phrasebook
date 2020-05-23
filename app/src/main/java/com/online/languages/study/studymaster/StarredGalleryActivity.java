package com.online.languages.study.studymaster;


import com.online.languages.study.studymaster.data.DataItem;

import java.util.ArrayList;

public class StarredGalleryActivity extends ImageListActivity {

    @Override
    public ArrayList<DataItem> getDataItems() {
        return dataManager.getStarredWords(2, true);
    }

    @Override
    public String getListType () {
        return STARRED;
    }

}
