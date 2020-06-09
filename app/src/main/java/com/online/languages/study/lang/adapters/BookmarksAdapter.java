package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.BookmarkItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.MyViewHolder> {

    private Context context;

    private ArrayList<BookmarkItem> imagesArrayList;

    private int color;
    private int type = 1;

    String dataSelect;
    private String theme;



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, sectionDesc;
        View sectionItemBox, setDivider, taggedView;
        ImageView mapImage;
        View star;


        MyViewHolder(View view) {
            super(view);

            sectionItemBox = view.findViewById(R.id.sectionItemBox);
            setDivider = view.findViewById(R.id.divider);

            title = view.findViewById(R.id.sectionTitle);
            sectionDesc = view.findViewById(R.id.sectionDesc);

            mapImage = view.findViewById(R.id.mapImage);
            taggedView = view.findViewById(R.id.tagged);

            star = view.findViewById(R.id.starIcon);

        }
    }



    public BookmarksAdapter(Context _context, ArrayList<BookmarkItem> _images, int _type, String _theme) {
        context = _context;
        imagesArrayList = _images;
        type = _type;
        theme = _theme;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bookmarks_list, parent, false);

        if (type == 2 ) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_img_item_card, parent, false);
        }

        if (type == 3 ) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_img_item_pic, parent, false);
        }


        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;
        if (imagesArrayList.get(position).type.equals("set")) type = 2;
        return type;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (position == 0 ) holder.setDivider.setVisibility(View.GONE);

        BookmarkItem dataItem = imagesArrayList.get(position);


        holder.sectionItemBox.setTag(dataItem.item);
        holder.taggedView.setTag(dataItem.item);


        String title = dataItem.title;

        if (!dataItem.desc.equals("")) {
            holder.sectionDesc.setText(dataItem.desc);
            if (type != 3 ) holder.sectionDesc.setVisibility(View.VISIBLE);
        }

        holder.title.setText(title);


        if (dataItem.starred > 0 ) {
            holder.star.setVisibility(View.VISIBLE);
        } else {
            holder.star.setVisibility(View.GONE);
        }


        if (dataItem.image.equals("")) dataItem.image = "battle_ledovoe.jpg";


        holder.mapImage.setVisibility(View.VISIBLE);


        Picasso.with( context )
                    .load("file:///android_asset/pics/"+ dataItem.image )
                    .fit()
                    .centerCrop()
                    .into(holder.mapImage);

    }

    @Override
    public int getItemCount() {
        return imagesArrayList.size();
    }


    public void remove(int position) {
        imagesArrayList.remove(position);
        notifyItemRemoved(position);
    }



}
