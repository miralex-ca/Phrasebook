package com.online.languages.study.lang.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.RoundedCornersTransformation;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class StarredTabOne extends Fragment {

    ArrayList<DataItem> words;

    DataManager dataManager;
    TextView text;
    TextView starredCount;
    View zero;
    View infoBox;
    TextView countZero, desc;
    LinearLayout previewList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_starred_tab_1, container, false);

        if (getTabType() == 2) rootView = inflater.inflate(R.layout.fragment_starred_tab_2, container, false);


        text = rootView.findViewById(R.id.starredWords);
        dataManager = new DataManager(getActivity(), 1);
        starredCount = rootView.findViewById(R.id.starred_count);

        zero = rootView.findViewById(R.id.starred_zero);
        countZero = rootView.findViewById(R.id.starred_count_preview);
        desc = rootView.findViewById(R.id.starred_zero_desc);

        infoBox = rootView.findViewById(R.id.starred_info);

        previewList = rootView.findViewById(R.id.starred_preview_list);

        words = updateTitle(words);

        createPreviewList(words);


        return rootView;
    }


    private ArrayList<DataItem> updateTitle(ArrayList<DataItem> words) {

        if (getTabType() == 2) words = dataManager.getStarredWords(2,false);
        else words = dataManager.getStarredWords(false);

        zero.setVisibility(View.GONE);
        infoBox.setVisibility(View.GONE);


        int total = words.size();

        if (total < 1) {
            zero.setVisibility(View.VISIBLE);
            infoBox.setVisibility(View.GONE);
        } else {
            zero.setVisibility(View.GONE);
            infoBox.setVisibility(View.VISIBLE);
        }

        StarredFragment parentFrag = ((StarredFragment)StarredTabOne.this.getParentFragment());

        if (parentFrag!= null) {
            if (getTabType () == 1 )parentFrag.updateTabName(1, total);
            if (getTabType () == 2 )parentFrag.updateTabName(2, total);
        }


        int limit = Constants.STARRED_LIMIT;

        String descTxt = String.format(getResources().getString(R.string.starred_words_info),  limit);

        String count = String.format("%d / %d", words.size(), limit);

        if (words.size()  > limit) count = String.valueOf(words.size());

        String zero = "0 / " + limit;

        countZero.setText(zero);
        desc.setText(descTxt);

        starredCount.setText(count);

        int displayLimit = 4;

        if (words.size() < displayLimit) displayLimit = words.size();

        words = new ArrayList<>(words.subList(0, displayLimit));

        return words;

    }

    public int getTabType () {
        return 1;
    }



    private void createPreviewList(ArrayList<DataItem> dataItems) {


        previewList.removeAllViews();

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (DataItem dataItem: dataItems) {

            View item;

            if (getTabType () == 2 ) item = inflater.inflate(R.layout.starred_gallery_item, previewList, false);
            else item = inflater.inflate(R.layout.starred_list_item, null);

            TextView txt = item.findViewById(R.id.itemText);
            TextView  desc = item.findViewById(R.id.itemInfo);
            txt.setText( dataItem.item);
            desc.setText( dataItem.info);

            ImageView image = item.findViewById(R.id.itemImage);

            Picasso.with(getActivity() )
                    .load("file:///android_asset/pics/"+ dataItem.image )
                    .transform(new RoundedCornersTransformation(20,0))
                    .fit()
                    .centerCrop()
                    .into(image);

            previewList.addView(item);
        }

    }


    @Override
    public void onResume() {

        super.onResume();

        words = updateTitle(words);

        createPreviewList(words);

    }









}
