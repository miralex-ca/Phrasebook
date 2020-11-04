package com.online.languages.study.lang.adapters;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataObject;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.online.languages.study.lang.Constants.FOLDER_PICS;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.Constants.PARAM_GROUP;


public class EditUCatsListAdapter extends RecyclerView.Adapter<EditUCatsListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DataObject> dataList;
    private String folder;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, itemsCount;
        ImageView icon;

        View textWrapper;

        MyViewHolder(View view) {
            super(view);

            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            itemsCount = itemView.findViewById(R.id.itemsCount);
            icon = itemView.findViewById(R.id.icon);
            textWrapper = itemView.findViewById(R.id.text_wrapper);

        }
    }


    public EditUCatsListAdapter(Context context, ArrayList<DataObject> dataList) {
        this.context  = context;
        this.dataList = dataList;
        folder = context.getString(R.string.group_pics_folder);
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_cat_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        DataObject dataObject = dataList.get(position);

        holder.title.setText( dataObject.title);

        Locale current = context.getResources().getConfiguration().locale;

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT,  current);

        String formattedDate = dateFormat.format(  new Date( dataObject.time_created) );

        holder.desc.setText(context.getString(R.string.home_ucat_date) + formattedDate );

        holder.itemsCount.setText(String.format(context.getString(R.string.home_ucat_list_progress), String.valueOf(dataObject.progress), String.valueOf(dataObject.count)));

        if (dataObject.type.equals(PARAM_GROUP)) {

            holder.itemsCount.setVisibility(View.GONE);

            if (dataObject.desc.equals(PARAM_EMPTY)) {

                String desc =
                        context.getResources().getQuantityString(R.plurals.topic_plurals, dataObject.count, dataObject.count);

                holder.desc.setText(desc);
            } else {
                holder.desc.setText(dataObject.desc);
            }

        }

        if (emptyImage(dataObject.image)) {

            holder.icon.setVisibility(View.GONE);
            removeMargins (holder.title);
            removeMargins(holder.desc);

        } else {

            holder.icon.setVisibility(View.VISIBLE);

            Picasso.with( context )
                    .load(FOLDER_PICS + folder + dataObject.image)
                    .fit()
                    .centerCrop()
                    .transform(new RoundedCornersTransformation(10,0))
                    .into(holder.icon);
        }

    }

    private void removeMargins (View v) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(0, 0, 0, 0);
            v.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private boolean emptyImage(String picName) {

        boolean noImage = false;

        if (picName.equals("none") || picName.equals("empty.png") || picName.equals("")) {
            noImage = true;
        }

        return noImage;
    }



}

