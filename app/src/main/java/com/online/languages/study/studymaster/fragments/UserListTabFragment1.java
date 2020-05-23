package com.online.languages.study.studymaster.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.online.languages.study.studymaster.CatActivity;
import com.online.languages.study.studymaster.Constants;
import com.online.languages.study.studymaster.DBHelper;
import com.online.languages.study.studymaster.R;
import com.online.languages.study.studymaster.UserListActivity;
import com.online.languages.study.studymaster.adapters.ContentAdapter;
import com.online.languages.study.studymaster.adapters.DividerItemDecoration;
import com.online.languages.study.studymaster.adapters.ResizeHeight;
import com.online.languages.study.studymaster.data.DataFromJson;
import com.online.languages.study.studymaster.data.DataItem;


import java.util.ArrayList;
import java.util.Iterator;


public class UserListTabFragment1 extends Fragment {

    ArrayList<DataItem> data = new ArrayList<>();
    SharedPreferences appSettings;
    Boolean compactLayout;


    RecyclerView recyclerView;
    DBHelper dbHelper;

    ContentAdapter vAdapter;

    Boolean showDialog = true;
    Boolean comeBack = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cat_1, container, false);

        dbHelper = new DBHelper(getActivity());

        data = ((UserListActivity) getActivity()).topicData;

        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        int showStatus = Integer.valueOf(appSettings.getString("show_status", Constants.STATUS_SHOW_DEFAULT));

        String theme = appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        recyclerView = rootView.findViewById(R.id.my_recycler_view);
        vAdapter = new ContentAdapter(getActivity(), data, showStatus, theme);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration( new DividerItemDecoration(getActivity()) );
        recyclerView.setAdapter(vAdapter);

        openView(recyclerView);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                View animObj = view.findViewById(R.id.animObj);

                // Toast.makeText(getActivity(), "Dialog OnClick", Toast.LENGTH_SHORT).show();

                onItemClick(animObj, position);

            }
            @Override
            public void onLongClick(View view, int position) {
                confirmChange(position);
            }
        }));


        return rootView;
    }


    public void confirmChange(int position) {

        boolean confirm = appSettings.getBoolean("set_starred_confirm", true);


        if (confirm) {
            openConfirmDialog(position);
        } else {
            changeStarred(position);
        }

        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        int vibLen = 30;
        assert v != null;
        v.vibrate(vibLen);
    }

    public void openConfirmDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_confirm_remove, null);


        CheckBox checkBox = (CheckBox) content.findViewById(R.id.checkbox);
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

                changeStarred(position);

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

        Boolean starred = dbHelper.checkStarred(id);

        dbHelper.setStarred(id, !starred); // id to id

        checkStarred();

    }




    private void onItemClick(final View view, final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((UserListActivity)getActivity()).showAlertDialog(view);
            }
        }, 50);
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
            vAdapter.notifyDataSetChanged();
        } else {
            comeBack = true;
        }

    }



    public void checkStarred(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findRemoved(data);
            }
        }, 150);

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


        if (wordList == null) {

           // Toast.makeText(getActivity(), "Null", Toast.LENGTH_SHORT).show();

        } else {


            wordList = dbHelper.checkStarredList(wordList);

            recyclerView.setMinimumHeight(recyclerView.getHeight());

            for (int i = 0; i < wordList.size(); i++) {
                if (wordList.get(i).starred < 1) {

                    try {
                        int count = recyclerView.getChildCount();
                        int height = 0;

                        if (count > 0) {
                            for (int r = 0; r < count; r++) {
                                if (recyclerView.getChildAt(r) != null) {
                                    height = height + recyclerView.getChildAt(r).getHeight();
                                }
                            }

                            setHR(height);
                        }
                    } finally {

                        vAdapter.remove(i);

                    }
                }
            }
        }

        checkWordsLength();
    }


    private void setHR(final int height) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ResizeHeight resizeHeight = new ResizeHeight(recyclerView, height);
                resizeHeight.setDuration(400);
                recyclerView.startAnimation(resizeHeight);
                recyclerView.setMinimumHeight(recyclerView.getHeight());

            }
        }, 600);

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
