package com.online.languages.study.lang.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.MainActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.HomeCardRecycleAdapter;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.NAV_GALLERY_SPEC;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewCards;

    private HomeCardRecycleAdapter mAdapter;

    SharedPreferences appSettings;

    DataManager dataManager;
    boolean tablet;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        dataManager = new DataManager(getActivity());

        NavStructure navStructure = dataManager.getNavStructure();

        ArrayList<NavSection> navSections = checkSections(navStructure.sections);

        tablet = getResources().getBoolean(R.bool.tablet);

        recyclerView = rootView.findViewById(R.id.recycler_view_home);
        recyclerViewCards = rootView.findViewById(R.id.recycler_view_cards);

        String theme = appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        DataManager dataManager = new DataManager(getActivity(), true);

        int recycleType = 1;

        int spanCount = 1;

        if (dataManager.homecards) {
            recyclerView.setVisibility(View.GONE);
            recyclerViewCards.setVisibility(View.VISIBLE);
            recycleType = 2;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerViewCards.setVisibility(View.GONE);

            recycleType = 3;
            spanCount = 2;

        }


        if (tablet) {
            spanCount = 3;
            recycleType = 3;

            if (getResources().getBoolean(R.bool.tablet_land)) {
                spanCount = 4;
               // recycleType = 1;
            }
        }


        mAdapter = new HomeCardRecycleAdapter(getActivity(), navSections, theme, recycleType);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), spanCount, LinearLayoutManager.VERTICAL, false);


        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(mAdapter);

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



        RecyclerView.LayoutManager mLayoutManagerCards = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerViewCards.setLayoutManager(mLayoutManagerCards);
        recyclerViewCards.setAdapter(mAdapter);

        ViewCompat.setNestedScrollingEnabled(recyclerViewCards, false);

        recyclerViewCards.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewCards, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                onGridClick(view, position);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return rootView;
    }

    private ArrayList<NavSection> checkSections(ArrayList<NavSection> navSections) {
        ArrayList<NavSection> sections = new ArrayList<>();

        for (NavSection section: navSections) {
            if (!section.spec.equals(NAV_GALLERY_SPEC)) sections.add(section);
        }

        return sections;
    }



    private void onGridClick(final View view, final int position) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // String id =  vocab.sectionTags.get(act);

                ((MainActivity)getActivity()).openCatActivity(view, position);

            }
        }, 80);
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
