package com.online.languages.study.lang.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataItem;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_NORM;


public class SectionReviewListAdapter extends RecyclerView.Adapter<SectionReviewListAdapter.MyViewHolder> implements OnListItemCallback {

    private ArrayList<DataItem> dataList;
    String layoutType;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt, translate;
        View helperView, itemWrap;
        View starIcon, statusView;

        MyViewHolder(View view) {
            super(view);
            txt = itemView.findViewById(R.id.itemText);
            translate = itemView.findViewById(R.id.itemInfo);
            helperView =  itemView.findViewById(R.id.animObj);
            starIcon = itemView.findViewById(R.id.voclistStar);
            statusView = itemView.findViewById(R.id.status_wrap);
            itemWrap = itemView.findViewById(R.id.cat_item_wrap);


        }
    }


    public SectionReviewListAdapter(ArrayList<DataItem> _dataList, String sectionListLayout) {
        dataList = _dataList;

        layoutType  = sectionListLayout;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        if (viewType == 2){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_review_list_item_bigger, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_review_list_item, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;
        if (layoutType.equals(CAT_LIST_VIEW_NORM))  type = 2;
        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        DataItem dataItem = dataList.get(position);

        holder.txt.setText( dataItem.item);
        holder.translate.setText( dataItem.info);

        holder.helperView.setTag(R.id.item_id, dataItem.id);
        holder.helperView.setTag(R.id.item_type, dataItem.type);

        holder.itemWrap.setOnClickListener(v -> callBack(dataItem));

        checkStarred(holder, dataItem);

    }

    private void checkStarred(MyViewHolder holder, DataItem dataItem) {
        if (dataItem.starred == 1) {
            holder.starIcon.setVisibility(View.VISIBLE);
        } else {
            holder.starIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    @Override
    public void callBack(DataItem dataitem) {

    }



}

