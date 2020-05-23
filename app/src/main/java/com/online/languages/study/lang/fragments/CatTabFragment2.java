package com.online.languages.study.lang.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.online.languages.study.lang.CatActivity;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.ColorProgress;
import com.online.languages.study.lang.adapters.ExRecycleAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;

import java.util.ArrayList;
import java.util.Map;

public class CatTabFragment2 extends Fragment {

    RecyclerView recyclerView;
    ExRecycleAdapter exAdapter;

    ArrayList<String> exLinkTitles;
    ArrayList<String> exLinkDesc;
    int[] exResults = {0,0,0,0};


    String catSpec;

    TextView catTotalCount, catKnownCount, catStudiedCount, catProgress;
    DataManager dataManager; /// TODO optimize
    DBHelper dbHelper;
    ColorProgress colorProgress;


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


        fillData();

        recyclerView = rootView.findViewById(R.id.ex_recycler_list);

        exAdapter = new ExRecycleAdapter(getActivity(), exLinkTitles, exLinkDesc, exResults, true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

       // recyclerView.addItemDecoration( new DividerItemDecoration(getActivity()) );

        recyclerView.setAdapter(exAdapter);

        View topStatsCard = rootView.findViewById(R.id.topStatsCard);
        View minCardHeight = rootView.findViewById(R.id.cardMinHeight);
        View divide = rootView.findViewById(R.id.carDivider);


        if (  getActivity().getResources().getBoolean(R.bool.small_height ))  {
            topStatsCard.setVisibility(View.GONE);
            divide.setVisibility(View.GONE);
        } else {
            if (!dataManager.simplified) minCardHeight.setMinimumHeight(0);
        }

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


    private void  fillData() {


        exResults = new int[]{0, 0, 0, 0};
        exLinkTitles = new ArrayList<>();
        exLinkDesc = new ArrayList<>();


        exResults[1] = dbHelper.getTestResult(CatActivity.categoryID+"_1");
        exResults[2] = dbHelper.getTestResult(CatActivity.categoryID+"_2");

        //CatData catData = dbHelper.getCatData(getActivity().getIntent().getStringExtra(Constants.EXTRA_CAT_TAG));


        exLinkTitles.add(getString(R.string.voc_ex_link_card_title));
        exLinkDesc.add(defineDesc (0, catSpec));

        exLinkTitles.add(getString(R.string.voc_ex_link_first_title));
        exLinkDesc.add(defineDesc (1, catSpec));

        exLinkTitles.add(getString(R.string.voc_ex_link_second_title));
        exLinkDesc.add(defineDesc (2, catSpec));

       // exLinkDesc.add(getString(R.string.voc_ex_link_third_desc));
       // exLinkTitles.add(getString(R.string.voc_ex_link_third_title));

        setStats();

    }

    public void setStats() {

        String catId = CatActivity.categoryID;

        int dataCount = 0;
        int knownCount = 0;
        int studiedCount = 0;
        int progress = 0;


        ArrayList<DataItem> data = dataManager.getCatDBList(catId);


        ArrayList<String> catIdList = new ArrayList<>();
        catIdList.add(catId);

        Map<String, String> map = dataManager.getCatProgress( catIdList ); //

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String id = entry.getKey();
            String result = entry.getValue();
            if (id.matches(catId)) {
                progress = Integer.valueOf(result);
            }
        }

        dataCount = data.size();

        for (DataItem item: data) {
            if (item.rate > 0) knownCount++;
            if (item.rate > 2) studiedCount++;
        }

        String totalCount = getString(R.string.cat_stats_total_items)+ dataCount;
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
            if (spec.equals(Constants.CAT_SPEC_PERS)) desc = getString(R.string.ex_link_desc_pers_1);
            if (spec.equals(Constants.CAT_SPEC_TERM)) desc = getString(R.string.ex_link_desc_term_1);
            if (spec.equals(Constants.CAT_SPEC_MISC)) desc = getString(R.string.ex_link_desc_misc_1);
        } else if (order == 2) {
            desc = getString(R.string.voc_ex_link_second_desc);
            if (spec.equals(Constants.CAT_SPEC_PERS)) desc = getString(R.string.ex_link_desc_pers_2);
            if (spec.equals(Constants.CAT_SPEC_TERM)) desc = getString(R.string.ex_link_desc_term_2);
            if (spec.equals(Constants.CAT_SPEC_MISC)) desc = getString(R.string.ex_link_desc_misc_2);
        }


        return desc;
    }




    @Override
    public void onResume() {
        super.onResume();

        fillData();
        exAdapter = new ExRecycleAdapter(getActivity(), exLinkTitles, exLinkDesc, exResults, true);
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
