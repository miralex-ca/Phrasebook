package com.online.languages.study.lang.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.MainActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.DataModeDialog;
import com.online.languages.study.lang.adapters.StatsCatsAdapter;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.UserStats;

import java.util.ArrayList;
import java.util.List;


public class StatsFragment extends Fragment {


    UserStats userStats;

    StatsCatsAdapter adapter;
    RecyclerView recyclerView;

    View errorsCard;
    TextView recentErrorsTxt, recentErrorsTxt2;

    TextView studiedTxt, knownTxt, unknownTxt, studiedTxtProgress, knownTxtProgress, unknownTxtProgress;
    TextView studiedCount, knownCount;

    ArrayList<DataItem> errorsList;

    NavStructure navStructure;

    ProgressBar knownProgress;
    ProgressBar studiedProgress;

    Boolean easy_mode;
    MenuItem modeMenuItem;

    DataManager dataManager;

    DataModeDialog dataModeDialog;


    public StatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_stats, container, false);


        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        dataManager = new DataManager(getActivity());
        easy_mode = dataManager.easyMode();

        dataModeDialog = new DataModeDialog(getActivity());

        setHasOptionsMenu(true);

        assert getArguments() != null;
        navStructure = getArguments().getParcelable("structure");
        userStats = new UserStats(getActivity(), navStructure);

        recyclerView = rootview.findViewById(R.id.recycler_stats);

        errorsCard = rootview.findViewById(R.id.errorsCard);
        recentErrorsTxt = rootview.findViewById(R.id.recentErrors);
        recentErrorsTxt2 = rootview.findViewById(R.id.recentErrors2);

        studiedTxt = rootview.findViewById(R.id.studiedCountTxt);
        knownTxt = rootview.findViewById(R.id.knownCountTxt);
        unknownTxt = rootview.findViewById(R.id.unknownCountTxt);

        knownProgress = rootview.findViewById(R.id.knownProgress);
        studiedProgress = rootview.findViewById(R.id.studiedProgress);

        studiedTxtProgress = rootview.findViewById(R.id.studiedTxtProgress);
        knownTxtProgress = rootview.findViewById(R.id.knownTxtProgress);
        unknownTxtProgress = rootview.findViewById(R.id.unknownTxtProgress);

        studiedCount = rootview.findViewById(R.id.stats_studied_count);
        knownCount= rootview.findViewById(R.id.stats_known_count);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                onGridClick(view, position);

            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return rootview;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.stats_mode, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        modeMenuItem = menu.findItem(R.id.easy_mode);
        checkModeIcon();

    }

    private void checkModeIcon() {
        easy_mode = dataManager.easyMode();
        if (modeMenuItem != null) modeMenuItem.setVisible(easy_mode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.easy_mode)  dataModeDialog.openDialog();
        return false;

    }

    private void updateData() {
        checkModeIcon();
        userStats.updateData();
        MainActivity.allDataList = userStats.getAllDataFromJson();

        MainActivity.oldestDataList = userStats.getOldestLIst();
        errorsList = userStats.userStatsData.errorsWords;

        ArrayList<DataItem> errorsList = new ArrayList<>();

        if (userStats.userStatsData.errorsWords.size() >  30) {
            errorsList.addAll(userStats.userStatsData.errorsWords.subList(0, 30));
        } else {
            errorsList.addAll(userStats.userStatsData.errorsWords);
        }


        MainActivity.errorsList = errorsList;


    }


    private void setContent() {

        updateData();

        adapter = new StatsCatsAdapter(getActivity(), userStats.userStatsData.sectionsDataList);

        recyclerView.setAdapter(adapter);

        checkErrors();

        studiedTxt.setText(String.valueOf(  userStats.userStatsData.studiedDataCount  ));
        knownTxt.setText( String.valueOf(  userStats.userStatsData.familiarDataCount ) );
        unknownTxt.setText( String.valueOf(  userStats.userStatsData.unknownDataCount  ) );

        studiedCount.setText(String.valueOf(  userStats.userStatsData.studiedDataCount  ));
        knownCount.setText( String.valueOf(  userStats.userStatsData.familiarDataCount ) );


        int studiedProgressValue = (100 * userStats.userStatsData.studiedDataCount / userStats.userStatsData.allDataCount);
        int knownProgressValue = (100 * userStats.userStatsData.familiarDataCount / userStats.userStatsData.allDataCount);


        if (Constants.SCREEN_SHOW) {
          knownProgressValue = 74;
          studiedProgressValue = 67;

          int st = userStats.userStatsData.allDataCount * studiedProgressValue / 100;
          int kn = userStats.userStatsData.allDataCount * knownProgressValue / 100;

          studiedCount.setText(String.valueOf(st));
          knownCount.setText(String.valueOf(kn));
        }

        studiedTxtProgress.setText(String.format(getString(R.string.stats_studied_percent), studiedProgressValue));
        knownTxtProgress.setText(String.format(getString(R.string.stats_familier_percent), knownProgressValue));
        unknownTxtProgress.setText(String.format(getString(R.string.stats_unknown_percent), 100 - knownProgressValue));


        knownProgress.setAlpha(0);
        studiedProgress.setAlpha(0);

        knownProgress.setProgress(knownProgressValue);
        studiedProgress.setProgress(studiedProgressValue);

        final int finalKnownProgressValue = knownProgressValue;
        final int finalStudiedProgressValue = studiedProgressValue;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            knownProgress.setProgress(finalKnownProgressValue);
            studiedProgress.setProgress(finalStudiedProgressValue);

                knownProgress.animate().alpha(1).setDuration(600);
                studiedProgress.animate().alpha(1).setDuration(800);

            }
        }, 300);
    }


    @Override
    public void onResume() {
        super.onResume();

        setContent();
    }



    private void onGridClick(final View view, final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ((MainActivity)getActivity()).openSectionStats(view, position);

            }
        }, 80);
    }

    private void checkErrors() {


        if (userStats.userStatsData.errorsWords.size() > 0) {
            errorsCard.setVisibility(View.VISIBLE);
            String errors = "";

            String errors2 = "";

            int limit;

            if (userStats.userStatsData.errorsWords.size() > 6) {
                limit = 6;
            } else {
                limit = userStats.userStatsData.errorsWords.size();
            }

            List<DataItem> data = userStats.userStatsData.errorsWords.subList(0, limit);

            for (int i = 0; i< data.size(); i++) {

                if (i < 3) {
                    errors = errors +"\n"+ data.get(i).item ;
                } else {
                    errors2 = errors2 +"\n"+ data.get(i).item ;
                }
            }

            recentErrorsTxt.setText(errors);
            recentErrorsTxt2.setText(errors2);

        } else {
            errorsCard.setVisibility(View.GONE);
            recentErrorsTxt.setText("");
            recentErrorsTxt2.setText("");
        }

    }


    public ArrayList<DataItem> returnErrors() {
        return errorsList;
    }


    public interface ClickListener{
        void onClick(View view,int position);
        void onLongClick(View view,int position);
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
