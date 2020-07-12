package com.online.languages.study.lang.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.NoteActivity;
import com.online.languages.study.lang.NoteEditActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.NotesAdapter;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ResizeHeight;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.data.NoteData;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.ACTION_CREATE;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ACTION;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ID;
import static com.online.languages.study.lang.Constants.NOTES_LIST_ANIMATION;
import static com.online.languages.study.lang.Constants.NOTES_LIST_LIMIT;
import static com.online.languages.study.lang.Constants.SET_GALLERY_LAYOUT;
import static com.online.languages.study.lang.Constants.SET_GALLERY_LAYOUT_DEFAULT;
import static com.online.languages.study.lang.Constants.STATUS_DELETED;
import static com.online.languages.study.lang.Constants.STATUS_NEW;
import static com.online.languages.study.lang.Constants.STATUS_NORM;
import static com.online.languages.study.lang.Constants.STATUS_UPDATED;
import static com.online.languages.study.lang.Constants.UCAT_LIST_LIMIT;


public class NotesFragment extends Fragment {

    SharedPreferences appSettings;
    public String themeTitle;

    OpenActivity openActivity;


    String title = "Заметка ";
    String content = "Содержание заметки ";


    ArrayList<NoteData> notes;
    NotesAdapter adapter;
    RecyclerView recyclerView;

    RelativeLayout helperView;
    boolean cutList;


    DataManager dataManager;



    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_notes, container, false);

        // setHasOptionsMenu(true);

        openActivity = new OpenActivity(getActivity());
        dataManager = new DataManager(getActivity());

        recyclerView = rootview.findViewById(R.id.recycler_view);
        helperView = rootview.findViewById(R.id.list_wrapper);


        cutList = true;

        notes = getNotes();


        adapter = new NotesAdapter(getActivity(), notes, NotesFragment.this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adapter);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


        openListView();

        return rootview;

    }

    private void openListView() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                helperView.setVisibility(View.VISIBLE);

            }
        }, 30);

    }

    public void onNoteClick(NoteData note) {

        Intent i = new Intent(getActivity(), NoteActivity.class);
        i.putExtra(EXTRA_NOTE_ID, note.id );

        startActivityForResult(i, 20);
        openActivity.pageTransition();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            updateListNoAnimation();
        } else {
            updateList();
        }

    }

    private void updateList() {

        checkList();

    }


    public void openCompleteList() {

        cutList = false;

        helperView.clearAnimation();
        setWrapContentHeight(helperView);


        updateListNoAnimation();



    }


    private ArrayList<NoteData> getNotes() {


        setWrapContentHeight(helperView);

        ArrayList<NoteData> completeList = dataManager.getNotes();

        ArrayList<NoteData> displayList = new ArrayList<>(completeList);


        int limit = NOTES_LIST_LIMIT;

        if (completeList.size() > limit) {
            if (cutList) displayList = new ArrayList<>(completeList.subList(0, limit));
        }

        return addLast(displayList, completeList);
    }


    private ArrayList<NoteData> addLast(ArrayList<NoteData> displayList, ArrayList<NoteData> completeList) {

        NoteData lastObject = checkMoreItem(displayList, completeList);

        displayList.add( lastObject );


        return displayList;
    }


    private NoteData checkMoreItem(ArrayList<NoteData> displayList, ArrayList<NoteData> completeList) {

        NoteData lastObject = new NoteData();
        lastObject.id = "last";


        int dif = completeList.size() - displayList.size();

        if (dif > 0) {
            lastObject.title = "Загрузить ещё " + dif;
            lastObject.info = "show";
        } else {
            lastObject.info = "hide";
        }

        return  lastObject;

    }


    private void updateMoreIem() {
        adapter.notifyItemChanged(notes.size()-1);
    }



    private void checkList() {

        if (!NOTES_LIST_ANIMATION) {

            updateListNoAnimation();

        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkListAnimation();
                }
            }, 50);

        }

    }

    private void updateListNoAnimation() {

        notes = getNotes();
        adapter = new NotesAdapter(getActivity(), notes, NotesFragment.this);
        recyclerView.setAdapter(adapter);
    }


    private void checkListAnimation() {


            ArrayList<NoteData> newNotes = getNotes();


            for (NoteData noteData: notes) noteData.status = STATUS_DELETED;
            for (NoteData newNote: newNotes)  newNote.status = STATUS_NEW;


            for (NoteData noteData: notes) {

                for (NoteData newNote: newNotes) {

                    if (newNote.id.equals(noteData.id)) {

                        newNote.status = STATUS_NORM;
                        noteData.status = STATUS_NORM;

                        if ( noteData.time_updated != newNote.time_updated) {

                            noteData.title = newNote.title;
                            noteData.content = newNote.content;
                            noteData.image = newNote.image;
                            noteData.time_updated = newNote.time_updated;

                            noteData.status = STATUS_UPDATED;

                        }

                        if (noteData.id.equals("last")) {
                            noteData.title  = newNote.title;
                            noteData.info = newNote.info;
                        }


                        break;
                    }
                }
            }



            for(int i = 0; i < notes.size(); i++) {
                NoteData noteData = notes.get(i);

                if (noteData.status.equals(STATUS_UPDATED)) {

                    adapter.notifyItemChanged(i); /// normal

                }
                if (noteData.status.equals(STATUS_DELETED)) {


                    setHR( recyclerView, helperView);

                    notes.remove(i);
                    adapter.notifyItemRemoved(i);

                }
            }

            for(int i = 0; i < newNotes.size(); i++) {
                NoteData newNote = newNotes.get(i);

                if (newNote.status.equals(STATUS_NEW)) {
                    if (i > (notes.size()-1 )) {
                        notes.add(newNote);
                        adapter.notifyItemInserted(notes.size() -1 );

                    } else {
                        notes.add(i,newNote);
                        adapter.notifyItemInserted(i);

                    }
                }
            }


          updateMoreIem();

    }







    // Deletion process views handling
    /// Set minheight to recycle and helper view
    //// Remove item which happens with animation
    ///// Change recycleview minheight (no animation) in about 400 ms (item removal animation must have finished)
    ////// Play height animation for helper view

    /// - make sure to return wrap content to helper view (before or after all act)



    private void setHR(final RecyclerView recycler, final RelativeLayout helper) {

        recycler.setMinimumHeight(recycler.getHeight());
        helper.setMinimumHeight(recycler.getHeight());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recycler.setMinimumHeight(0);

            }
        }, 400);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int h = recycler.getHeight();
                ResizeHeight resizeHeight = new ResizeHeight(helper, h);
                resizeHeight.setDuration(300);

                helper.clearAnimation();
                helper.startAnimation(resizeHeight);

            }
        }, 450);

    }

    private void setWrapContentHeight(View view) { //// should aply to the target view parent

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        view.setLayoutParams(params);

    }



    public void fabClick() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                newNote();
            }
        }, 50);

    }

    private void newNote() {
        Intent i = new Intent(getActivity(), NoteEditActivity.class);
        i.putExtra(EXTRA_NOTE_ID, "" );
        i.putExtra(EXTRA_NOTE_ACTION, ACTION_CREATE );

        startActivityForResult(i, 20);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_notes_list, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_note) {
            newNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
