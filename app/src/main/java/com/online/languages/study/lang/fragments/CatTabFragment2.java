package com.online.languages.study.lang.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.ColorProgress;
import com.online.languages.study.lang.adapters.ExRecycleAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.presentation.category.CatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CatTabFragment2 extends Fragment {

    RecyclerView recyclerView;
    ExRecycleAdapter exAdapter;

    ArrayList<String> exLinkTitles;
    ArrayList<String> exLinkDesc;
    int[] exResults = {0,0,0,0};

    String catSpec;

    TextView catTotalCount, catKnownCount, catStudiedCount, catProgress;
    DataManager dataManager;
    DBHelper dbHelper;
    ColorProgress colorProgress;
    boolean speaking;

    Map<String, ArrayList<String>> catResults;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cat_2, container, false);

        exLinkTitles = new ArrayList<>();
        exLinkDesc = new ArrayList<>();

        catSpec = CatActivity.catSpec;

        dataManager = new DataManager(getActivity());
        dbHelper = new DBHelper(getActivity());
        colorProgress = new ColorProgress(getActivity());


        catTotalCount = rootView.findViewById(R.id.catTotalCount);
        catKnownCount = rootView.findViewById(R.id.catKnownCount);
        catStudiedCount = rootView.findViewById(R.id.catStudiedCount);
        catProgress = rootView.findViewById(R.id.catProgress);

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        speaking = appSettings.getBoolean("set_speak", true);


        fillData();

        recyclerView = rootView.findViewById(R.id.ex_recycler_list);

        exAdapter = new ExRecycleAdapter(getActivity(), exLinkTitles, exLinkDesc, exResults, true);
        if (!getResources().getBoolean(R.bool.tablet)) exAdapter.matchLines = true;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

       // recyclerView.addItemDecoration( new DividerItemDecoration(getActivity()) );

        recyclerView.setAdapter(exAdapter);


        View topStatsCard = rootView.findViewById(R.id.topStatsCard);
        View minCardHeight = rootView.findViewById(R.id.cardMinHeight);
        View divide = rootView.findViewById(R.id.carDivider);


        if (!dataManager.simplified) minCardHeight.setMinimumHeight(0);

        if (dataManager.simplified) {
            topStatsCard.setVisibility(View.GONE);
            divide.setVisibility(View.GONE);
        }

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                openExercise(position);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return rootView;
    }

    private void openExercise(final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((CatActivity)getActivity()).nextPage(position);
            }
        }, 80);
    }


    public void  fillData() {

        dataManager.dbHelper.checkMode();

        exResults = new int[]{0, 0, 0, 0};
        exLinkTitles = new ArrayList<>();
        exLinkDesc = new ArrayList<>();


        String catId = CatActivity.categoryID;


        ArrayList<String> results = new ArrayList<>(Arrays.asList("0", "0", "0"));

        catResults = dataManager.getExResults(new ArrayList<>(Arrays.asList(catId)));

        if (catResults != null && results.size()>0) results = catResults.get(catId);


        exResults[1] = Integer.parseInt(results.get(0));
        exResults[2] = Integer.parseInt(results.get(1));
        exResults[3] = Integer.parseInt(results.get(2));

        //CatData catData = dbHelper.getCatData(getActivity().getIntent().getStringExtra(Constants.EXTRA_CAT_TAG));


        exLinkTitles.add(getString(R.string.voc_ex_link_card_title));
        exLinkDesc.add(defineDesc (0, catSpec));

        exLinkTitles.add(getString(R.string.voc_ex_link_first_title));
        exLinkDesc.add(defineDesc (1, catSpec));

        exLinkTitles.add(getString(R.string.voc_ex_link_second_title));
        exLinkDesc.add(defineDesc (2, catSpec));


        if (speaking) {
            exLinkTitles.add(getString(R.string.voc_ex_link_third_title));
            exLinkDesc.add(getString(R.string.voc_ex_link_third_desc));
        }


       // exLinkDesc.add(getString(R.string.voc_ex_link_third_desc));
       // exLinkTitles.add(getString(R.string.voc_ex_link_third_title));

        setStats(results);

    }


    public void setStats(ArrayList<String> results) {

        String catId = CatActivity.categoryID;

        int dataCount = 0;
        int knownCount = 0;
        int studiedCount = 0;
        int progress = 0;


        ArrayList<DataItem> data = dataManager.getCatDBList(catId);

        progress = dataManager.calculateProgressByList(results, speaking);

        dataCount = data.size();

        for (DataItem item: data) {
            if (item.rate > 0) knownCount++;
            if (item.rate > 2) studiedCount++;
        }

        String totalCount = getString(R.string.cat_stats_total_items_text)+ String.valueOf(dataCount);
        catTotalCount.setText(totalCount);
        catKnownCount.setText(String.valueOf(knownCount));
        catStudiedCount.setText(String.valueOf(studiedCount));
        catProgress.setText(String.format(getString(R.string.number_percent), progress));

        catProgress.setTextColor(  colorProgress.getColorFromAttr(progress)  );
    }

    private String defineDesc (int order, String spec) {
        String desc = "";

        if (order == 0) {
            desc = getString(R.string.voc_ex_link_card_desc);
        } else if (order == 1) {
            desc = getString(R.string.voc_ex_link_first_desc);
        } else if (order == 2) {
            desc = getString(R.string.voc_ex_link_second_desc);
        }


        return desc;
    }




    @Override
    public void onResume() {
        super.onResume();

        updateResults();

    }

    public void updateResults() {

        fillData();
        exAdapter = new ExRecycleAdapter(getActivity(), exLinkTitles, exLinkDesc, exResults, true);
        if (!getResources().getBoolean(R.bool.tablet)) exAdapter.matchLines = true;

        recyclerView.setAdapter(exAdapter);
    }




    public interface ClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){
            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }



}
