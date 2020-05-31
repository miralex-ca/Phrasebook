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
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;


public class SectionListAdapter extends RecyclerView.Adapter<SectionListAdapter.MyViewHolder> {

    private ArrayList<DataItem> dataList;
    private int showStatus;
    private String errorsLabel;
    String layoutType;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt, translate;
        View helperView;
        View starIcon, statusView;

        MyViewHolder(View view) {
            super(view);
            txt = itemView.findViewById(R.id.itemText);
            translate = itemView.findViewById(R.id.itemInfo);
            helperView =  itemView.findViewById(R.id.animObj);
            starIcon = itemView.findViewById(R.id.voclistStar);
            statusView = itemView.findViewById(R.id.status_wrap);

        }
    }


    public SectionListAdapter(ArrayList<DataItem> _dataList, int _show_status) {
        dataList = _dataList;
        showStatus = _show_status;
        errorsLabel = getAppContext().getString(R.string.section_errors_label);
        layoutType  = CAT_LIST_VIEW_COMPACT;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == 2 ) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_list_title, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_list_item_cols, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;
        if (dataList.get(position).type.equals("group_title")) type = 2;
        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        DataItem dataItem = dataList.get(position);

        holder.txt.setText( dataItem.item);
        holder.translate.setText( dataItem.info);
        holder.helperView.setTag(R.id.item_id, dataItem.id);
        holder.helperView.setTag(R.id.item_type, dataItem.type);


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
        errorsTxt.setText(String.valueOf(errorsCount));

        View statuses = statusBox.findViewById(R.id.itemStatus);


        if (errorsCount > 0) {
            errorsTxt.setVisibility(View.VISIBLE);
            statuses.setVisibility(View.GONE);


            if (layoutType.equals(CAT_LIST_VIEW_COMPACT)) {
                View errorIcon = statusBox.findViewById(R.id.errorIcon);
                errorsTxt.setVisibility(View.GONE);
                errorIcon.setVisibility(View.VISIBLE);
            }

        } else {
            errorsTxt.setVisibility(View.GONE);
            statuses.setVisibility(View.VISIBLE);


            if (layoutType.equals(CAT_LIST_VIEW_COMPACT)) {
                View errorIcon = statusBox.findViewById(R.id.errorIcon);
                errorIcon.setVisibility(View.GONE);
            }
        }


    }


}

