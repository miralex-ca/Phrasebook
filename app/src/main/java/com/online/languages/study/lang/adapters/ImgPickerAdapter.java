package com.online.languages.study.lang.adapters;


import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.online.languages.study.lang.R;
import com.squareup.picasso.Picasso;

import static com.online.languages.study.lang.Constants.FOLDER_PICS;


public class ImgPickerAdapter extends RecyclerView.Adapter<ImgPickerAdapter.MyViewHolder> {

    private Context context;
    private String[] pics;
    private String folder;
    int selected;


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        View selector, emptyTxt;

        MyViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.icon);
            selector = view.findViewById(R.id.imgSelector);
            emptyTxt = view.findViewById(R.id.emptyTxt);

        }
    }


    public ImgPickerAdapter(Context context, String[] pics, int selected) {
        this.context = context;
        this.pics = pics;
        this.selected = selected;
        folder = context.getString(R.string.notes_pics_folder);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_picker_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;

        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String pic = pics [position];

        holder.icon.setTag(position);

        if (position == 0) holder.emptyTxt.setVisibility(View.VISIBLE);

        if (selected == position) {

             holder.selector.setVisibility(View.VISIBLE);

        }
        else holder.selector.setVisibility(View.INVISIBLE);


        Picasso.with( context )
                .load(FOLDER_PICS + folder + pic)
                .fit()
                .centerCrop()
                .into(holder.icon);


    }

    @Override
    public int getItemCount() {
        return pics.length;
    }



}
