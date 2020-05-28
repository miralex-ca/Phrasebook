package com.online.languages.study.lang.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataItem;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_NORM;


public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {

    private ArrayList<DataItem> dataList;
    Context context;
    private int showStatus;
    private String theme;
    private String layoutType;
    ColorProgress colorProgress;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt, translate, status;
        View helperView;

        View starIcon, statusView, divider;;


        MyViewHolder(View view) {
            super(view);

            txt = itemView.findViewById(R.id.itemText);
            translate = itemView.findViewById(R.id.itemInfo);
            helperView =  itemView.findViewById(R.id.animObj);
            starIcon = itemView.findViewById(R.id.voclistStar);
            statusView = itemView.findViewById(R.id.status_wrap);
            divider = itemView.findViewById(R.id.catItemDivider);
            status = itemView.findViewById(R.id.itemStatus);
        }
    }


    public ContentAdapter(Context _context, ArrayList<DataItem> _dataList, int _show_status, String _theme) {
        dataList = _dataList;
        context = _context;
        showStatus = _show_status;
        theme = _theme;
        colorProgress = new ColorProgress(context);

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(_context);
        layoutType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_NORM);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == 2) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item_compact_2, parent, false);
        } else if (viewType == 3) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item_divider, parent, false);

        }else {

            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item_norm, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;

        if (layoutType.equals(CAT_LIST_VIEW_COMPACT)) type = 2;
        if (dataList.get(position).type.equals("divider")) type = 3;
        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        DataItem dataItem = dataList.get(position);

        holder.txt.setText( dataItem.item);
        holder.translate.setText( dataItem.info);
        holder.helperView.setTag(dataItem.id);

        if (position == 0) holder.divider.setVisibility(View.INVISIBLE);

        if (dataItem.starred == 1) {
            holder.starIcon.setVisibility(View.VISIBLE);
        } else {
            holder.starIcon.setVisibility(View.INVISIBLE);
        }

        if (dataItem.type.equals("divider"))  {
            holder.helperView.setTag("divider");
            if (dataItem.item.equals("none")) {
                holder.txt.setVisibility(View.INVISIBLE);
                holder.txt.setTextSize(8);
            }
        }


        statusInfoDisplay(showStatus, holder.statusView, dataItem);
        statusTxt(holder.status, dataItem);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public void remove(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    private void statusTxt(TextView txt, DataItem dataItem) {
        txt.setTextColor(colorProgress.getStatusColorFromAttr(dataItem.rate));
    }


    private void statusInfoDisplay(int displayStatus, View statusView, DataItem dataItem) {

        switch (displayStatus) {

            case (1): /// auto

                if (dataItem.rate > 0) {
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
           // manageStatusView(statusView, dataItem.rate);
           // manageErrorsView(statusView, dataItem.errors);

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
        errorsTxt.setText(String.format(context.getString(R.string.errors_label), errorsCount));

        if (errorsCount > 0) {
            errorsTxt.setVisibility(View.VISIBLE);
        } else {
            errorsTxt.setVisibility(View.GONE);
        }
    }


}

