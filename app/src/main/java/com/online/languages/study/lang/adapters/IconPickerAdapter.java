package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.fragments.HomeFragment2;
import com.squareup.picasso.Picasso;


public class IconPickerAdapter extends RecyclerView.Adapter<IconPickerAdapter.MyViewHolder> {

    private Context context;
    private String[] pics;
    private String folder;
    int selected;

    HomeFragment2 fragment;

    boolean clickAction;


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        View selector, emptyTxt;
        View wrap;

        MyViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.icon);
            selector = view.findViewById(R.id.imgSelector);
            emptyTxt = view.findViewById(R.id.emptyTxt);
            wrap = view.findViewById(R.id.imgWrap);

        }
    }


    public IconPickerAdapter(Context context, String[] pics, int selected, HomeFragment2 fragment) {
        this.context = context;
        this.pics = pics;
        this.selected = selected;
        folder = context.getString(R.string.notes_pics_folder);
        this.fragment = fragment;
        clickAction = true;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_picker_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;

        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        String pic = pics [position];

        holder.icon.setTag(position);

        if (position == (pics.length -1) ) holder.emptyTxt.setVisibility(View.VISIBLE);

        if (selected == position) {
             holder.selector.setVisibility(View.VISIBLE);
        }
        else holder.selector.setVisibility(View.INVISIBLE);


        Picasso.with( context )
                .load("file:" + pic)
                .fit()
                .centerCrop()
                .into(holder.icon);

        holder.wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAction(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return pics.length;
    }


    private void clickAction(final int position) {

        if (!clickAction) return;

        clickAction = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fragment.iconSelect(position);
                clickAction = true;
            }
        }, 300);


    }



}
