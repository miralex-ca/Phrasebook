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

import java.util.ArrayList;


public class GroupPickerAdapter extends RecyclerView.Adapter<GroupPickerAdapter.MyViewHolder> {

    private Context context;
    private String[] pics;

    private ArrayList<DataObject> groups;

    private String folder;
    int selected;


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        View selector, emptyTxt;

        TextView title;

        MyViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.icon);
            title = view.findViewById(R.id.title);
            selector = view.findViewById(R.id.imgSelector);


        }
    }


    public GroupPickerAdapter(Context context, ArrayList<DataObject> groups, int selected) {
        this.context = context;
        this.groups = groups;
        this.selected = selected;
        folder = context.getString(R.string.group_pics_folder);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_picker_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;
        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        DataObject group = groups.get(position);

        holder.icon.setTag(position);

       // if (position == 0) holder.emptyTxt.setVisibility(View.VISIBLE);

        if (selected == position) {
            holder.selector.setVisibility(View.VISIBLE);
        }
        else {
            holder.selector.setVisibility(View.INVISIBLE);
        }


        holder.title.setText(group.title);


    }

    @Override
    public int getItemCount() {
        return groups.size();
    }



}
