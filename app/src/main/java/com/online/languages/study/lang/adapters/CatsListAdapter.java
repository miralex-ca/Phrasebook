package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.ViewCategory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CatsListAdapter extends RecyclerView.Adapter<CatsListAdapter.MyViewHolder> {


    private Context context;

    private ArrayList<ViewCategory> categoryArrayList;

    private int color;
    int type = 1;

    String dataSelect;

    ColorProgress colorProgress;


    Boolean full_version;
    Boolean show;


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, progressTxt, catGroupCount, sectionDesc;
        View progressBox, catGroupCountBox, sectionItemBox, setDivider, catLockedBox;
        ImageView progressCircle, image;


        MyViewHolder(View view) {
            super(view);

            sectionItemBox = view.findViewById(R.id.sectionItemBox);

            setDivider = view.findViewById(R.id.divider);

            title = view.findViewById(R.id.sectionTitle);
            sectionDesc = view.findViewById(R.id.sectionDesc);
            progressTxt = view.findViewById(R.id.catProgress);
            catGroupCount = view.findViewById(R.id.catGroupCount);
            progressBox = view.findViewById(R.id.catProgressBox);
            catGroupCountBox = view.findViewById(R.id.catGroupCountBox);
            progressCircle = view.findViewById(R.id.catProgressCircle);
            catLockedBox = view.findViewById(R.id.catLockedBox);
            image = itemView.findViewById(R.id.itemImage);

        }
    }





    public CatsListAdapter(Context _context, ArrayList<ViewCategory> _cats, Boolean version) {
        context = _context;
        categoryArrayList = _cats;

        full_version = version;

        show = Constants.SCREEN_SHOW;

        colorProgress = new ColorProgress(context);

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        dataSelect = appSettings.getString("data_select", "dates");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == 2 ) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_cat_set_title, parent, false);
        } else if (viewType == 3 ) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_cat_item_pic, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_cat_item, parent, false);
        }


        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;
        if (categoryArrayList.get(position).type.equals("set")) type = 2;
        if (!categoryArrayList.get(position).image.equals("none") && !categoryArrayList.get(position).image.equals("")) type = 3;
        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ViewCategory category = categoryArrayList.get(position);


        holder.sectionItemBox.setTag(position);


        String title = category.title;

        if (!category.desc.equals("")) {
            holder.sectionDesc.setText(category.desc);
            holder.sectionDesc.setVisibility(View.VISIBLE);
        }




        if (category.subgroup > 0) {
            holder.catGroupCountBox.setVisibility(View.VISIBLE);
            holder.catGroupCount.setText(""+category.subgroup);
        }


        if (category.type.equals("set")) {
            if (position == 0) holder.setDivider.setVisibility(View.GONE);
            if (category.title.equals("none")) holder.title.setVisibility(View.GONE);

        }


        int progress = category.progress;

        if (show) {
            if (position == 0) progress = 100;
            if (position == 1) progress = 100;
            if (position == 2) progress = 95;
            if (position == 3) progress = 76;
            if (position == 4) progress = 48;
            if (position == 5) progress = 19;
        }



        if (progress > 0) {
            holder.progressBox.setVisibility(View.VISIBLE);
            holder.progressTxt.setText(progress + "%");

            holder.progressTxt.setTextColor(colorProgress.setCatColorFromAttr(progress));

            if (progress > 95 ) {
                holder.progressCircle.setImageResource(R.drawable.ic_cat_progress_perfect);
            } else if (progress > 79) {
                holder.progressCircle.setImageResource(R.drawable.ic_cat_progress_great);
            } else if (progress > 49) {
                holder.progressCircle.setImageResource(R.drawable.ic_cat_progress_good);
            } else if (progress > 20) {
                holder.progressCircle.setImageResource(R.drawable.ic_cat_progress_bad);
            } else {
                holder.progressCircle.setImageResource(R.drawable.ic_cat_progress_low);
            }
        }



        if (!dataSelect.equals("all")) {
            if (category.type.equals(Constants.CAT_TYPE_EXTRA) ) {
                holder.progressBox.setVisibility(View.GONE);
            }
        }


        if (!full_version) {
            if (!category.unlocked) {
                holder.catGroupCountBox.setVisibility(View.GONE);
                holder.progressBox.setVisibility(View.GONE);
                holder.catLockedBox.setVisibility(View.VISIBLE);
            }
        }

        if (category.type.equals("set")) {
            holder.progressBox.setVisibility(View.GONE);
            holder.catLockedBox.setVisibility(View.GONE);

        }


           // title += " [" + category.progress + "]";

        holder.title.setText(title);



        Picasso.with(context )
                .load("file:///android_asset/pics/"+ category.image)
                // .transform(new RoundedTransformation(0,0))
                .fit()
                .centerCrop()
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }



}
