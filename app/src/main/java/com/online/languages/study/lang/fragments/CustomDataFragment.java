package com.online.languages.study.lang.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.CustomDataActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.CustomDataListAdapter;
import com.online.languages.study.lang.adapters.DividerItemDecoration;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;

import java.util.ArrayList;


public class CustomDataFragment extends Fragment {

    ArrayList<DataItem> data = new ArrayList<>();
    CustomDataListAdapter adapter;
    DataManager dataManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_custom_cat, container, false);

        dataManager = new DataManager(getActivity());

        getDataList();

        RecyclerView recyclerView = rootView.findViewById(R.id.my_recycler_view);
        adapter = new CustomDataListAdapter(data, 1);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration( new DividerItemDecoration(getActivity()) );
        recyclerView.setAdapter(adapter);


        View empty = rootView.findViewById(R.id.empty_txt);

        if (data.size() > 0) {
            openView(recyclerView);
        } else {
            empty.setVisibility(View.VISIBLE);
        }


        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


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

        return rootView;
    }


    public Constants.Status getTabName () {
        return Constants.Status.STUDIED;
    }

    public int getTabNum () {

        Constants.Status status = getTabName ();
        int tab = 0;
        if (status == Constants.Status.FAMILIAR) tab = 1;
        if (status == Constants.Status.UNKNOWN) tab = 2;

        return tab;
    }


    private void getDataList() {
         data = dataManager.getCatCustomList(CustomDataActivity.catId, getTabNum () );
    }




    public void changeStarred(int position) {   /// check just one item

        String id = data.get(position).id;
        Boolean starred = dataManager.checkStarStatusById(id );


        int status = dataManager.dbHelper.setStarred(id, !starred); // id to id


        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        int vibLen = 30;

        if (status == 0) {
            Toast.makeText(getActivity(), R.string.starred_limit, Toast.LENGTH_SHORT).show();
            vibLen = 300;
        }

        checkStarred(position);

        assert v != null;
        v.vibrate(vibLen);
    }


    private void openView(final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        }, 80);
    }

    private void onItemClick(final View view, final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((CustomDataActivity)getActivity()).showDetailDialog(view, position);
            }
        }, 50);
    }



    public void checkDataList() {   /// check all items
        data = dataManager.checkDataItemsData(data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        }, 80);
    }


    public void checkStarred(final int result){   /// check just one item
        data = dataManager.checkDataItemsData(data);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemChanged(result);
            }
        }, 200);


        int origin = getTabNum()+1;
        ((CustomDataActivity)getActivity()).updateLists(origin);

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
