package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.ViewCategory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.MyViewHolder> {

    private Context context;

    private ArrayList<ViewCategory> categoryArrayList;

    private int color;
    private int type = 1;

    String dataSelect;
    private String theme;



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, sectionDesc;
        View sectionItemBox, setDivider, taggedView;
        ImageView mapImage;


        MyViewHolder(View view) {
            super(view);


            sectionItemBox = view.findViewById(R.id.sectionItemBox);

            setDivider = view.findViewById(R.id.divider);

            title = view.findViewById(R.id.sectionTitle);
            sectionDesc = view.findViewById(R.id.sectionDesc);

            mapImage = view.findViewById(R.id.mapImage);
            taggedView = view.findViewById(R.id.tagged);

        }
    }



    public MapListAdapter(Context _context, ArrayList<ViewCategory> _cats, int _type, String _theme) {
        context = _context;
        categoryArrayList = _cats;
        type = _type;
        theme = _theme;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_map_item, parent, false);

        if (type == 2 ) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_map_item_card, parent, false);
        }




        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;
        if (categoryArrayList.get(position).type.equals("set")) type = 2;
        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (position == 0 ) holder.setDivider.setVisibility(View.GONE);

        ViewCategory category = categoryArrayList.get(position);


        holder.sectionItemBox.setTag(position);
        holder.taggedView.setTag(position);


        String title = category.title;

        if (!category.desc.equals("")) {
            holder.sectionDesc.setText(category.desc);
            holder.sectionDesc.setVisibility(View.VISIBLE);
        }

        holder.title.setText(title);


        if (category.image.equals("")) category.image = "Slav2.png";


        holder.mapImage.setVisibility(View.VISIBLE);



        if (type == 2 ) {
            Picasso.with( context )
                    .load("file:///android_asset/maps/"+ category.image )
                    .fit()
                    .centerCrop()
                    .into(holder.mapImage);
        } else {

            Picasso.with( context )
                    .load("file:///android_asset/maps/"+ category.image )
                    //.transform(new RoundedCornersTransformation(10,5))
                    .fit()
                    .centerCrop()
                    .into(holder.mapImage);
        }

        /// if (theme.equals("westworld")) holder.mapImage.setColorFilter(0xFFAEF7F7, PorterDuff.Mode.MULTIPLY);


        if (theme.equals("westworld")) {
            holder.mapImage.setColorFilter(Color.argb(255, 90, 255, 230), PorterDuff.Mode.MULTIPLY);
        }




    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }



}
