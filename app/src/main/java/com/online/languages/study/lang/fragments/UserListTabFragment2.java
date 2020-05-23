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

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.UserListActivity;
import com.online.languages.study.lang.adapters.ExRecycleAdapter;

import java.util.ArrayList;

public class UserListTabFragment2 extends Fragment {

    RecyclerView recyclerView;
    ExRecycleAdapter exAdapter;

    View notif;
    Boolean enableExercises = true;

    ArrayList<String> exLinkTitles;
    ArrayList<String> exLinkDesc;
    int[] exResults = {0,0,0,0};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cat_2, container, false);

        exLinkTitles = new ArrayList<>();
        exLinkDesc = new ArrayList<>();

        View divide = rootView.findViewById(R.id.carDivider);
        divide.setVisibility(View.GONE);

        fillData();

        recyclerView = rootView.findViewById(R.id.ex_recycler_list);

        exAdapter = new ExRecycleAdapter(getActivity(), exLinkTitles, exLinkDesc, exResults, true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(exAdapter);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        notif = rootView.findViewById(R.id.exStarNotif);

        View topStatsCard = rootView.findViewById(R.id.topStatsCard);
        topStatsCard.setVisibility(View.GONE);

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

        if (enableExercises) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((UserListActivity) getActivity()).nextPage(position);
                }
            }, 80);
        }
    }


    private void  fillData() {

        exResults = new int[]{0, 0, 0, 0};
        exLinkTitles = new ArrayList<>();
        exLinkDesc = new ArrayList<>();


        if (  UserListActivity.showRes  )   {

            DBHelper dbHelper = new DBHelper(getActivity());

            exResults[1] = dbHelper.getTestResult(Constants.STARRED_CAT_TAG+"_1");
            exResults[2] = dbHelper.getTestResult(Constants.STARRED_CAT_TAG+"_2");

        }


        exLinkTitles.add(getString(R.string.voc_ex_link_card_title));
        exLinkTitles.add(getString(R.string.voc_ex_link_first_title));
        exLinkTitles.add(getString(R.string.voc_ex_link_second_title));


        exLinkDesc.add(getString(R.string.voc_ex_link_card_desc));
        exLinkDesc.add(getString(R.string.ex_link_desc_star_1));
        exLinkDesc.add(getString(R.string.ex_link_desc_star_2));

       // exLinkTitles.add(getString(R.string.voc_ex_link_third_title));
       // exLinkDesc.add(getString(R.string.voc_ex_link_third_desc));

    }

    public void checkStarred(int size) {
        checkExEnable(size);
    }

    private void checkExEnable(int listSize) {
        if (listSize < Constants.LIMIT_STARRED_EX) {
            disableExercises();
        } else {
            setEnableExercises();
        }
    }

    private void disableExercises() {
        recyclerView.setAlpha(0.4f);
        notif.setVisibility(View.VISIBLE);
        enableExercises = false;
    }

    private void setEnableExercises() {
        recyclerView.setAlpha(1.0f);
        enableExercises = true;
        notif.setVisibility(View.GONE);
    }



    @Override
    public void onResume() {
        super.onResume();

        updateResults();
    }

    public void updateResults() {

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
