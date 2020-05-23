package com.online.languages.study.lang.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataItem;

import java.util.ArrayList;

import static com.online.languages.study.lang.App.getAppContext;


public class CustomDataListAdapter extends RecyclerView.Adapter<CustomDataListAdapter.MyViewHolder> {

    private ArrayList<DataItem> dataList;
    private int showStatus;
    int listType = -1;

    String errorsLabel;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt, translate;
        View helperView, starIcon, statusView;

        MyViewHolder(View view) {
            super(view);

            txt = itemView.findViewById(R.id.itemText);
            translate = itemView.findViewById(R.id.itemInfo);
            helperView =  itemView.findViewById(R.id.animObj);

            starIcon = itemView.findViewById(R.id.voclistStar);
            statusView = itemView.findViewById(R.id.status_wrap);
        }
    }


    public CustomDataListAdapter(ArrayList<DataItem> _dataList) {

        errorsLabel = getAppContext().getString(R.string.errors_label);
        dataList = _dataList;
        showStatus = 2;
    }

    public CustomDataListAdapter(ArrayList<DataItem> _dataList, int _listType) {
        errorsLabel = getAppContext().getString(R.string.errors_label);
        dataList = _dataList;
        showStatus = 2;
        listType = _listType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_item, parent, false);

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

        statusInfoDisplay(showStatus, holder.statusView, dataItem);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public void remove(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    private void statusInfoDisplay(int displayStatus, View statusView, DataItem dataItem) {

        switch (displayStatus) {

            case (1): /// auto

                if (dataItem.errors > 0 || dataItem.rate > 0) {
                    openStatus(true, statusView, dataItem);
                } else {
                    openStatus(false, statusView, dataItem);
                }
                break;

            case (2):  /// always
                openStatus(true, statusView, dataItem);
                break;

            case (0):  /// never
                openStatus(false, statusView, dataItem);
                break;

        }

    }

    private void openStatus(Boolean show, View statusView, DataItem dataItem) {

        if (show) {
            statusView.setVisibility(View.VISIBLE);
            manageStatusView(statusView, dataItem.rate);
            manageErrorsView(statusView, dataItem.errors);

        } else {
            statusView.setVisibility(View.GONE);
        }
    }

    private void manageStatusView(View statusBox, int result) {
        View unknown = statusBox.findViewById(R.id.statusUnknown);
        View known = statusBox.findViewById(R.id.statusKnown);
        View studied = statusBox.findViewById(R.id.statusStudied);

        unknown.setVisibility(View.GONE);
        known.setVisibility(View.GONE);
        studied.setVisibility(View.GONE);

        if (result > 2) {
            studied.setVisibility(View.VISIBLE);
        } else if ( result > 0) {
            known.setVisibility(View.VISIBLE);
        } else {
            unknown.setVisibility(View.VISIBLE);
        }
    }

    private void manageErrorsView(View statusBox, int errorsCount) {
        TextView errorsTxt = statusBox.findViewById(R.id.errorsCount);
        errorsTxt.setText(String.format(errorsLabel, errorsCount));

        if (errorsCount > 0) {
            errorsTxt.setVisibility(View.VISIBLE);
        } else {
            errorsTxt.setVisibility(View.GONE);
        }

        if (listType == 0) errorsTxt.setVisibility(View.VISIBLE);
    }


}

