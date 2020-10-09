package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class EditUCatsListAdapter extends RecyclerView.Adapter<EditUCatsListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DataObject> dataList;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, itemsCount;

        MyViewHolder(View view) {
            super(view);

            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            itemsCount = itemView.findViewById(R.id.itemsCount);

        }
    }


    public EditUCatsListAdapter(Context context, ArrayList<DataObject> dataList) {
        this.context  = context;
        this.dataList = dataList;
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

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



}

