package com.online.languages.study.lang.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.online.languages.study.lang.CatActivity;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.UserListActivity;
import com.online.languages.study.lang.adapters.ContentAdapter;
import com.online.languages.study.lang.adapters.ContentCardAdapter;
import com.online.languages.study.lang.adapters.DividerItemDecoration;
import com.online.languages.study.lang.adapters.ImageListAdapter;
import com.online.languages.study.lang.adapters.ResizeHeight;
import com.online.languages.study.lang.data.DataItem;


import java.util.ArrayList;
import java.util.Iterator;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_CARD;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_DEFAULT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_NORM;


public class UserListTabFragment1 extends Fragment {

    ArrayList<DataItem> data = new ArrayList<>();
    SharedPreferences appSettings;
    DBHelper dbHelper;


    ContentAdapter adapter, adapterCompact;
    ContentCardAdapter adapterCard;
    RecyclerView recyclerView, recyclerViewCompact, recyclerViewCard;
    View listWrapper, listWrapperCompact, listWrapperCard;

    RelativeLayout  itemsList, itemsListCompact, itemsListCards;

    int showStatus;
    String theme;

    Boolean showDialog = true;
    Boolean comeBack = true;

    boolean open;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cat_1, container, false);


        dbHelper = new DBHelper(getActivity());
        data = ((UserListActivity) getActivity()).topicData;
        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());


        showStatus = Integer.valueOf(appSettings.getString("show_status", Constants.STATUS_SHOW_DEFAULT));

        theme = appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        open = true;

        listWrapper = rootView.findViewById(R.id.listContainer);
        listWrapperCompact = rootView.findViewById(R.id.listContainerCompact);
        listWrapperCard = rootView.findViewById(R.id.listContainerCard);

        itemsList = rootView.findViewById(R.id.itemListWrap);
        itemsListCompact = rootView.findViewById(R.id.itemListWrapCompact);
        itemsListCards = rootView.findViewById(R.id.itemListWrapCard);

        recyclerView = rootView.findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration( new DividerItemDecoration(getActivity()) );

        recyclerViewCompact = rootView.findViewById(R.id.my_recycler_view_compact);
        RecyclerView.LayoutManager mLayoutManagerCompact = new LinearLayoutManager(getActivity());
        recyclerViewCompact.setLayoutManager(mLayoutManagerCompact);
        recyclerViewCompact.addItemDecoration( new DividerItemDecoration(getActivity()) );


        recyclerViewCard = rootView.findViewById(R.id.my_recycler_view_card);
        RecyclerView.LayoutManager mLayoutManagerCard = new LinearLayoutManager(getActivity());
        recyclerViewCard.setLayoutManager(mLayoutManagerCard);


        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        ViewCompat.setNestedScrollingEnabled(recyclerViewCompact, false);
        ViewCompat.setNestedScrollingEnabled(recyclerViewCard, false);

        updateLayoutStatus();

        openView(recyclerView);
        openView(recyclerViewCompact); // TODO improve
        openView(recyclerViewCard); // TODO improve


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                View animObj = view.findViewById(R.id.animObj);
                onItemClick(animObj, position);
            }
            @Override
            public void onLongClick(View view, int position) {
                confirmChange(position);
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
                confirmChange(position);
            }
        }));

        return rootView;
    }


    public void confirmChange(int position) { /// TODO check

        boolean confirm = appSettings.getBoolean("set_starred_confirm", true);
        String id = data.get(position).id;

        if (confirm) {
            openConfirmDialog(id);
        } else {
            changeStarred(id);
        }

        vibrate(30);
    }


    private void vibrate(int duration) {
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        v.vibrate(duration);
    }


    public void updateLayoutStatus() {

        String listType =  appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);


        setWrapContentHeight(itemsList);
        setWrapContentHeight(itemsListCompact);

        itemsList.setMinimumHeight(0);
        itemsListCompact.setMinimumHeight(0);


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


    public void openConfirmDialog(final String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_confirm_remove, null);


        CheckBox checkBox = content.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeConfirmStatus(!isChecked);
            }
        });


        builder.setView(content);
        builder.setTitle(R.string.confirmation_txt);

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.continue_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                changeStarred(id);

            }
        });

        builder.setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }

    private void changeConfirmStatus(Boolean checked) {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean("set_starred_confirm", checked);
        editor.apply();
    }

    public void changeStarred(int position) {   /// check just one item
        String id = data.get(position).id;
        changeStarred(id);
    }

    public void changeStar(String id, boolean vibe) {

        boolean confirm = appSettings.getBoolean("set_starred_confirm", true);

        if (confirm) {
            openConfirmDialog(id);
            vibrate(30);

        } else {
            changeStarred(id, 80);
            if (vibe) vibrate(30);
        }

    }

    public void changeStarred(String id) {
        Boolean starred = dbHelper.checkStarred(id);
        dbHelper.setStarred(id, !starred);
        checkStarred();
    }

    public void changeStarred(String id, int delay) {
        Boolean starred = dbHelper.checkStarred(id);
        dbHelper.setStarred(id, !starred);
        checkStarred(delay);
    }


    private void onItemClick(final View view, final int position) {

        if (open) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((UserListActivity)getActivity()).showAlertDialog(view);
                }
            }, 50);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    open = true;
                }
            }, 200);
        }
    }


    private void openView(final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        }, 40);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (comeBack) {
            data = checkList(data);
            adapter.notifyDataSetChanged();
            adapterCompact.notifyDataSetChanged();
            adapterCard.notifyDataSetChanged();
        } else {
            comeBack = true;
        }

    }



    public void checkStarred(){
        checkStarred(150);
    }

    public void checkStarred(int delay){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findRemoved(data);
            }
        }, delay);
        comeBack = false;
    }




    private ArrayList<DataItem> checkList(ArrayList<DataItem> wordList) {
        DBHelper dbHelper = new DBHelper(getActivity());
        wordList = dbHelper.checkStarredList(wordList);
        Iterator<DataItem> i = wordList.iterator();

        while (i.hasNext()) {
            DataItem w = i.next(); // must be called before you can call i.remove()
            if (w.starred < 1) {
                i.remove();
            }
        }

        checkWordsLength();

        return wordList;
    }


    private void checkWordsLength() {
        int size = data.size();

        ((UserListActivity)getActivity()).setPageTitle(size);
    }


    private void findRemoved(ArrayList<DataItem> wordList) {

        //Toast.makeText(getActivity() , "Remove", Toast.LENGTH_SHORT).show();

        String listType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);


        if (wordList != null) {

            wordList = dbHelper.checkStarredList(wordList);

            if (listType.equals(CAT_LIST_VIEW_COMPACT)) {

                removeFromList(wordList, recyclerViewCompact, itemsListCompact, adapterCompact);

                adapter.notifyDataSetChanged();
                adapterCard.notifyDataSetChanged();

                setWrapContentHeight(itemsList);
            }


            if (listType.equals(CAT_LIST_VIEW_NORM)) {

                removeFromList(wordList, recyclerView, itemsList, adapter);
                adapterCompact.notifyDataSetChanged();
                adapterCard.notifyDataSetChanged();

                setWrapContentHeight(itemsListCompact);
            }

            if (listType.equals(CAT_LIST_VIEW_CARD)) {

                removeFromList(wordList, recyclerViewCard, itemsListCards, adapter);

                adapter.notifyDataSetChanged();
                adapterCompact.notifyDataSetChanged();

                setWrapContentHeight(itemsListCards);
            }


            checkWordsLength();
        }
    }


    private void removeFromList(ArrayList<DataItem> wordList, RecyclerView recyclerView, View helper, ContentAdapter adapter) {

        helper.setMinimumHeight(recyclerView.getHeight());  /// TODO list

        String listType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);

        for (int i = 0; i < wordList.size(); i++) {
            if (wordList.get(i).starred < 1) {

                try {
                    int count = recyclerView.getChildCount();

                    if (count > 0) {
                        setHR(recyclerView, helper);
                    }
                } finally {

                    if (listType.equals(CAT_LIST_VIEW_CARD)) {

                        adapterCard.remove(i);

                    } else {
                        adapter.remove(i);
                    }

                }
            }
        }
    }

    private void setHR(final RecyclerView recycler, final View helper) {

        recycler.setMinimumHeight(recycler.getHeight());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setMinimumHeight(0);
                recyclerViewCompact.setMinimumHeight(0);
                recyclerViewCard.setMinimumHeight(0);
            }
        }, 450);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int h = recycler.getHeight();
                ResizeHeight resizeHeight = new ResizeHeight(helper, h);
                resizeHeight.setDuration(300);
                helper.startAnimation(resizeHeight);

            }
        }, 550);

    }


    public void updateList() {

        adapter = new ContentAdapter(getActivity(), data, showStatus, theme, true, CAT_LIST_VIEW_NORM);
        adapterCompact = new ContentAdapter(getActivity(), data, showStatus, theme, true, CAT_LIST_VIEW_COMPACT);
        adapterCard = new ContentCardAdapter(getActivity(), data, showStatus, theme, false, CAT_LIST_VIEW_NORM, (UserListActivity) getActivity());

        setWrapContentHeight(itemsList);
        setWrapContentHeight(itemsListCompact);
        setWrapContentHeight(itemsListCards);

        itemsList.setMinimumHeight(0);
        itemsListCompact.setMinimumHeight(0);
        itemsListCards.setMinimumHeight(0);

        recyclerView.setMinimumHeight(0);

        recyclerView.setAdapter(adapter);
        recyclerViewCompact.setAdapter(adapterCompact);

        recyclerViewCard.setAdapter(adapterCard);

    }

    private void setWrapContentHeight(View view) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        view.setLayoutParams(params);

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
