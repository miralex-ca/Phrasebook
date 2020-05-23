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

import com.online.languages.study.studymaster.R;
import com.online.languages.study.studymaster.data.DataItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class InfoDataListAdapter extends RecyclerView.Adapter<InfoDataListAdapter.MyViewHolder> {

    private ArrayList<DataItem> dataList;
    Context context;
    private int showStatus;
    private String theme;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt, translate;
        View helperView;

        ImageView image;
        View starIcon, statusView;;



        MyViewHolder(View view) {
            super(view);

            txt = itemView.findViewById(R.id.itemText);
            translate = itemView.findViewById(R.id.itemInfo);
            helperView =  itemView.findViewById(R.id.animObj);
            image = itemView.findViewById(R.id.itemImage);
            starIcon = itemView.findViewById(R.id.voclistStar);
            statusView = itemView.findViewById(R.id.status_wrap);

        }
    }


    public InfoDataListAdapter(Context _context, ArrayList<DataItem> _dataList, int _show_status, String _theme) {
        dataList = _dataList;
        context = _context;
        showStatus = _show_status;
        theme = _theme;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == 2) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item_nopic, parent, false);
        } else if (viewType == 3) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item_divider, parent, false);

        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;
        if (dataList.get(position).image.equals("none")) type = 2;
        if (dataList.get(position).type.equals("divider")) type = 3;

        return type;
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

        if (dataItem.type.equals("divider"))  {
            holder.helperView.setTag("divider");
            if (dataItem.item.equals("none")) {
                holder.txt.setVisibility(View.INVISIBLE);
                holder.txt.setTextSize(8);
            }
        }


        String pic = dataItem.image;


        Picasso.with(context )
                .load("file:///android_asset/pics/"+ pic )
                .transform(new RoundedTransformation(0,0))
                .fit()
                .centerCrop()
                .into(holder.image);


        if (theme.equals("westworld")) {
            holder.image.setColorFilter(Color.argb(255, 50, 250, 240), PorterDuff.Mode.MULTIPLY);
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
        errorsTxt.setText(String.format(context.getString(R.string.errors_label), errorsCount));

        if (errorsCount > 0) {
            errorsTxt.setVisibility(View.VISIBLE);
        } else {
            errorsTxt.setVisibility(View.GONE);
        }
    }


}

