package com.online.languages.study.studymaster.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.studymaster.R;
import com.online.languages.study.studymaster.data.ViewCategory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

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



    public GalleryAdapter(Context _context, ArrayList<ViewCategory> _cats, int _type, String _theme) {
        context = _context;
        categoryArrayList = _cats;
        type = _type;
        theme = _theme;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_gallery_item, parent, false);

        if (type == 2 ) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_gallery_card, parent, false);
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


        holder.sectionItemBox.setTag(category.id);

        holder.taggedView.setTag(category.tag);


        String title = category.title;

        if (!category.desc.equals("")) {
            holder.sectionDesc.setText(category.desc);
            holder.sectionDesc.setVisibility(View.VISIBLE);
        }

        holder.title.setText(title);



        if (category.image.equals("")) category.image = "Slav2.png";


        holder.mapImage.setVisibility(View.VISIBLE);


            Picasso.with( context )
                    .load("file:///android_asset/pics/"+ category.image )
                    .fit()
                    .centerCrop()
                    .into(holder.mapImage);


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
