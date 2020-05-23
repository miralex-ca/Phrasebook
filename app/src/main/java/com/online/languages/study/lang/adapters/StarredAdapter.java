package com.online.languages.study.lang.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class StarredAdapter extends RecyclerView.Adapter<StarredAdapter.MyViewHolder> {

    ArrayList<DataItem> dataList = new ArrayList<>();
    String catTag;
    Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txt;
        public TextView translate;
        public View helperView;
        public RelativeLayout catItemWrap;
        ImageView image;
        View starIcon;



        MyViewHolder(View view) {
            super(view);

            txt = itemView.findViewById(R.id.itemText);
            translate = itemView.findViewById(R.id.itemInfo);
            helperView =  itemView.findViewById(R.id.animObj);
            catItemWrap = itemView.findViewById(R.id.cat_item_wrap);
            image = itemView.findViewById(R.id.itemImage);
            starIcon = itemView.findViewById(R.id.voclistStar);
        }
    }


    public StarredAdapter(Context _context, ArrayList<DataItem> _dataList, String _catTag) {
        dataList = _dataList;
        catTag = _catTag;
        context = _context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.starred_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        DataItem dataItem = dataList.get(position);

        holder.txt.setText( dataItem.item);
        holder.translate.setText( dataItem.info);
        holder.helperView.setTag(dataItem.id);

        if (dataItem.starred == 1) {

            holder.starIcon.setVisibility(View.VISIBLE);
        } else {

            holder.starIcon.setVisibility(View.INVISIBLE);
        }


        String pic = dataItem.image;


        Picasso.with(context )
                //.load(R.drawable.f)
                //.load(R.raw.e)
                .load("file:///android_asset/pics/"+ pic )
                .transform(new RoundedTransformation(0,0))
                .fit()
                .centerCrop()
                .into(holder.image);



        if ( !catTag.equals("dates"))  holder.catItemWrap.setBackground(null);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    private void checkStarred (ImageView starIcon, int starType) {

        if (starType > 0 ) {
            starIcon.setVisibility(View.VISIBLE);
        } else {
            starIcon.setVisibility(View.INVISIBLE);
        }
    }

    public void remove(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }


}

