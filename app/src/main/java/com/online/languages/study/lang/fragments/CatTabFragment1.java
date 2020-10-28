package com.online.languages.study.lang.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.online.languages.study.lang.CatActivity;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.ContentAdapter;
import com.online.languages.study.lang.adapters.ContentCardAdapter;
import com.online.languages.study.lang.adapters.DividerItemDecoration;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_CARD;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_DEFAULT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_NORM;
import static com.online.languages.study.lang.Constants.SET_GALLERY_LAYOUT;
import static com.online.languages.study.lang.Constants.SET_GALLERY_LAYOUT_DEFAULT;


public class CatTabFragment1 extends Fragment {


    ArrayList<DataItem> data = new ArrayList<>();
    DataManager dataManager;
    SharedPreferences appSettings;

    ContentAdapter adapter, adapterCompact;
    ContentCardAdapter adapterCard;
    RecyclerView recyclerView, recyclerViewCompact, recyclerViewCard;
    View listWrapper, listWrapperCompact, listWrapperCard ;

    int showStatus;
    String theme;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cat_1, container, false);

        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        theme = appSettings.getString("theme", Constants.SET_THEME_DEFAULT);
        showStatus = Integer.valueOf(appSettings.getString("show_status", Constants.STATUS_SHOW_DEFAULT));

        dataManager = new DataManager(getActivity());


        String forceStatus = "no";
        if (getActivity().getIntent().hasExtra(Constants.EXTRA_FORCE_STATUS)) {
            forceStatus = getActivity().getIntent().getStringExtra(Constants.EXTRA_FORCE_STATUS);
        }

        if (forceStatus.equals("always")) showStatus = 2;


        listWrapper = rootView.findViewById(R.id.listContainer);
        listWrapperCompact = rootView.findViewById(R.id.listContainerCompact);
        listWrapperCard = rootView.findViewById(R.id.listContainerCard);


        recyclerView = rootView.findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerViewCompact = rootView.findViewById(R.id.my_recycler_view_compact);
        RecyclerView.LayoutManager mLayoutManagerCompact = new LinearLayoutManager(getActivity());
        recyclerViewCompact.setLayoutManager(mLayoutManagerCompact);


        recyclerViewCard = rootView.findViewById(R.id.my_recycler_view_card);
        RecyclerView.LayoutManager mLayoutManagerCard = new LinearLayoutManager(getActivity());
        recyclerViewCard.setLayoutManager(mLayoutManagerCard);



        updateLayoutStatus();

        openView(recyclerView);
        openView(recyclerViewCompact); // TODO improve
        openView(recyclerViewCard); // TODO improve

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        ((SimpleItemAnimator) recyclerViewCompact.getItemAnimator()).setSupportsChangeAnimations(false);
        ViewCompat.setNestedScrollingEnabled(recyclerViewCompact, false);

        ((SimpleItemAnimator) recyclerViewCard.getItemAnimator()).setSupportsChangeAnimations(false);
        ViewCompat.setNestedScrollingEnabled(recyclerViewCard, false);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                View animObj = view.findViewById(R.id.animObj);
                onItemClick(animObj, position);
            }
            @Override
            public void onLongClick(View view, int position) {
                changeStarred(position);
            }
        }));


        recyclerViewCompact.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewCompact, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                View animObj = view.findViewById(R.id.animObj);
                onItemClick(animObj, position);
            }
            @Override
            public void onLongClick(View view, int position) {
                changeStarred(position);
            }
        }));


        return rootView;
    }

    public void updateLayoutStatus() {

      String listType =  appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);

        if (listType.equals(CAT_LIST_VIEW_COMPACT)) {
            listWrapper.setVisibility(View.GONE);
            listWrapperCard.setVisibility(View.GONE);
            listWrapperCompact.setVisibility(View.VISIBLE);
        } else if (listType.equals(CAT_LIST_VIEW_NORM)) {
            listWrapperCompact.setVisibility(View.GONE);
            listWrapperCard.setVisibility(View.GONE);
            listWrapper.setVisibility(View.VISIBLE);
        } else {
            listWrapper.setVisibility(View.GONE);
            listWrapperCompact.setVisibility(View.GONE);
            listWrapperCard.setVisibility(View.VISIBLE);

        }

        updateList();
    }

    private void updateList() {

        getDataList();
        adapter = new ContentAdapter(getActivity(), data, showStatus, theme, false, CAT_LIST_VIEW_NORM);
        recyclerView.setAdapter(adapter);

        adapterCompact = new ContentAdapter(getActivity(), data, showStatus, theme, false, CAT_LIST_VIEW_COMPACT);
        recyclerViewCompact.setAdapter(adapterCompact);

        adapterCard = new ContentCardAdapter(getActivity(), data, showStatus, theme, false, CAT_LIST_VIEW_CARD, (CatActivity)getActivity());
        recyclerViewCard.setAdapter(adapterCard);
    }

    public void updateSort() {

        recyclerView.animate().alpha(0f).setDuration(100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateList();
                recyclerView.animate().alpha(1.0f).setDuration(150);
            }
        }, 150);

    }


    public void getDataList() {
        String id = CatActivity.categoryID;
        data = dataManager.getCatDBList(id);
        data = insertDivider(data);
    }


    public void changeStarred(String id, boolean vibe) {

        int position = -1;

        for (int i = 0; i<data.size(); i++) {
            DataItem dataItem = data.get(i);
            if (dataItem.id.equals(id)) position = i;
        }

        if (position > -1) changeStarred(position, true);

        if (vibe) vibrate(30);

    }


    public void changeStarred(int position) {
        changeStarred(position, false);
        vibrate(30);
    }

    public void changeStarred(int position, boolean card) {   /// check just one item

        String id = data.get(position).id;

        boolean starred = dataManager.checkStarStatusById(id );

        int status = dataManager.dbHelper.setStarred(id, !starred); // id to id

        if (status == 0) {
            Toast.makeText(getActivity(), R.string.starred_limit, Toast.LENGTH_SHORT).show();
            vibrate(300);
        }

        int delay = 100;
        if (card) delay = 0;
        checkStarred(position, delay);

    }


    private void vibrate(int duration) {
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        v.vibrate(duration);
    }

    private ArrayList<DataItem> insertDivider(ArrayList<DataItem> data) {

        ArrayList<DataItem> list = new ArrayList<>();

        for (DataItem dataItem: data) {

            if (!dataItem.divider.equals("no")) {

                DataItem item = new DataItem();

                item.item = dataItem.divider;
                item.type = "divider";

                list.add(item);
            }

            list.add(dataItem);
        }

        return list;
    }


    private void openView(final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        }, 70);
    }

    public void onItemClick(final View view, final int position) {
        ((CatActivity)getActivity()).openDetailDialog(view, position);
    }


    public void updateDataList() {   /// check all items

        updateList();
    }

    public void checkDataList() {   /// check all items

        data = dataManager.checkDataItemsData(data);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                adapterCompact.notifyDataSetChanged();
                adapterCard.notifyDataSetChanged();
            }
        }, 80);
    }



    public void checkStarred(final int result, int delay){   /// check just one item

        //Toast.makeText(getActivity(), "Delay: " + delay, Toast.LENGTH_SHORT).show();

        data = dataManager.checkDataItemsData(data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemChanged(result);
                adapterCompact.notifyItemChanged(result);
                adapterCard.notifyItemChanged(result);
            }
        }, delay);
    }


    public interface ClickListener {
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
