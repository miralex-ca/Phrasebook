package com.online.languages.study.lang.adapters;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.NoteData;
import com.online.languages.study.lang.presentation.main.fragments.NotesFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.FOLDER_PICS;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<NoteData> notes;
    private String[] pics_list;
    private String picsFolder;
    NotesFragment fragment;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, content;
        ImageView noteIcon;
        View mainWrap;


        MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.noteTitle);
            content = view.findViewById(R.id.noteContent);
            noteIcon = view.findViewById(R.id.noteIcon);
            mainWrap = itemView.findViewById(R.id.cat_item_wrap);

        }
    }



    public NotesAdapter(Context _context, ArrayList<NoteData> notes, NotesFragment fragment) {
        context = _context;
        this.notes = notes;
        pics_list = context.getResources().getStringArray(R.array.note_pics_list);
        picsFolder = context.getString(R.string.notes_pics_folder);
        this.fragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);

        if (viewType==2) itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_nopic, parent, false);

        if (viewType==3) itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_more, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;

        NoteData note = notes.get(position);
        if (emptyImage(validatedPic(note.image))) type= 2;

        if (note.id.equals("last")) type = 3;

        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final NoteData note = notes.get(position);
        String noteImage = validatedPic(note.image);

        holder.title.setText(note.title);
        holder.content.setText(note.content);

        if (note.id.equals("last")) {
            manageMoreView(holder.mainWrap, note);
        }


        if (note.title.equals("")) {
            holder.title.setVisibility(View.GONE);
            holder.content.setMaxLines(4);
        } else {

            holder.title.setVisibility(View.VISIBLE);
            holder.content.setMaxLines(2);
        }


        if ( emptyImage( noteImage ) ) {
            holder.noteIcon.setVisibility(View.GONE);
        }

        Picasso.with( context )
                .load(FOLDER_PICS + picsFolder + noteImage)
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation(20,0))
                .into(holder.noteIcon);


        holder.mainWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! note.id.equals("last"))  fragment.onNoteClick(note);
            }
        });



        holder.mainWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! note.id.equals("last"))  fragment.onNoteClick(note);
            }
        });

        attachLongClickToCat(holder.mainWrap, note);



    }

    private void attachLongClickToCat(final View view, final NoteData note) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                fragment.onNoteLongClick(note);
                return true;    // <- set to true
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private boolean emptyImage(String picName) {

        boolean noImage = false;

        if (picName.equals("none") || picName.equals("empty.png") || picName.equals("")) {
            noImage = true;
        }

        return noImage;
    }

    private String validatedPic(String picName) {

        boolean found = false;

        if (picName == null) picName = "";

        for (String name: pics_list) {
            if (picName.equals(name)) found = true;
        }

        if (! found) picName = "none";

        return picName;
    }


    private void manageMoreView(View view, NoteData note) {

        View wrapper = view.findViewById(R.id.openMoreWrap);
        TextView moreTitle = view.findViewById(R.id.openMoreTxt);



        moreTitle.setText(note.title);

        if (note.info.equals("hide")) {
            wrapper.setVisibility(View.GONE);
        } else {
            wrapper.setVisibility(View.VISIBLE);

        }


        View openMore = view.findViewById(R.id.openMore);


        openMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.openCompleteList();
            }
        });

    }

}
