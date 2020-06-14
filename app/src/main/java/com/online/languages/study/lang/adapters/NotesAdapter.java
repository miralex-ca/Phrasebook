package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.NoteData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.FOLDER_PICS;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<NoteData> notes;
    private String[] pics_list;
    private String picsFolder;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, content;
        ImageView noteIcon;


        MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.noteTitle);
            content = view.findViewById(R.id.noteContent);
            noteIcon = view.findViewById(R.id.noteIcon);

        }
    }



    public NotesAdapter(Context _context, ArrayList<NoteData> notes) {
        context = _context;
        this.notes = notes;
        pics_list = context.getResources().getStringArray(R.array.note_pics_list);
        picsFolder = context.getString(R.string.notes_pics_folder);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);

        if (viewType==2) itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_nopic, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;

        NoteData note = notes.get(position);
        if (emptyImage(validatedPic(note.image))) type= 2;

        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        NoteData note = notes.get(position);
        String noteImage = validatedPic(note.image);

        holder.title.setText(note.title);
        holder.content.setText(note.content);


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

}
