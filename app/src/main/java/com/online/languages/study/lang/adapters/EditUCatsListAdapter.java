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

import java.util.ArrayList;


public class EditUCatsListAdapter extends RecyclerView.Adapter<EditUCatsListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DataObject> dataList;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc;

        MyViewHolder(View view) {
            super(view);

            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);

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
        holder.desc.setText( dataObject.desc);


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



}

