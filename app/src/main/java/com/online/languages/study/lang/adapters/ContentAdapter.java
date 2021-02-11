package com.online.languages.study.lang.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataItem;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_DEFAULT;
import static com.online.languages.study.lang.Constants.SHOW_GRAMMAR;


public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {

    private ArrayList<DataItem> dataList;
    Context context;
    private int showStatus;
    private String theme;
    private String layoutType;
    private ColorProgress colorProgress;
    private boolean autoDivider;

    int grammarCharLimit = 10;

    public boolean charLayout = false;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt, translate, grammar;
        View helperView;

        View starIcon, statusView, divider;

        MyViewHolder(View view) {
            super(view);

            txt = itemView.findViewById(R.id.itemText);
            translate = itemView.findViewById(R.id.itemInfo);
            helperView =  itemView.findViewById(R.id.animObj);
            starIcon = itemView.findViewById(R.id.voclistStar);
            statusView = itemView.findViewById(R.id.status_wrap);
            divider = itemView.findViewById(R.id.catItemDivider);
            grammar = itemView.findViewById(R.id.itemGrammar);

        }
    }

    public ContentAdapter(Context _context, ArrayList<DataItem> _dataList,
                          int _show_status, String _theme, boolean divider, String _layoutType, boolean _charLayout) {

        this( _context, _dataList,_show_status, _theme,  divider,  _layoutType);
        charLayout = true;

    }

    public ContentAdapter(Context _context, ArrayList<DataItem> _dataList,
                          int _show_status, String _theme, boolean divider, String _layoutType) {


        dataList = _dataList;
        context = _context;
        showStatus = _show_status;
        theme = _theme;
        colorProgress = new ColorProgress(context);
        autoDivider = divider;
        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(_context);

        if (_layoutType.equals("auto")) {
            layoutType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);
        } else {
            layoutType = _layoutType;
        }

        grammarCharLimit = context.getResources().getInteger(R.integer.card_grammar_limit);
        if (layoutType.equals(CAT_LIST_VIEW_COMPACT)) grammarCharLimit = context.getResources().getInteger(R.integer.card_grammar_limit_compact);;

    }


    public ContentAdapter(Context _context, ArrayList<DataItem> _dataList, int _show_status, String _theme) {
        this(_context, _dataList, _show_status, _theme, false, "auto");
    }

    public ContentAdapter(Context _context, ArrayList<DataItem> _dataList, int _show_status, String _theme, boolean divider) {
        this(_context, _dataList, _show_status, _theme, divider, "auto");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        int layout = R.layout.category_list_item_norm;

        if (viewType == 2) {
            layout = R.layout.category_list_item_compact_2;
        } else if (viewType == 3) {
            layout =  R.layout.category_list_item_divider;
        } else if (viewType == 4) {
            layout = R.layout.category_list_item_norm_char;
        } else {
            if (charLayout) layout = R.layout.category_list_item_norm_char;
            else   layout = R.layout.category_list_item_norm;
        }

        itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

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

        if (position == 0 || autoDivider)
            holder.divider.setVisibility(View.INVISIBLE);

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

        String grammar = dataItem.grammar.replace("n. ", "").replace(".", "");

        if (SHOW_GRAMMAR && grammar.length() > 0 && grammar.length() < grammarCharLimit) {
            holder.grammar.setVisibility(View.VISIBLE);
            holder.grammar.setText(grammar);
        } else {
            holder.grammar.setVisibility(View.GONE);
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

    private void statusTxt(TextView txt, DataItem dataItem) {
        txt.setTextColor(colorProgress.getStatusColorFromAttr(dataItem.rate));
    }


    private void statusInfoDisplay(int displayStatus, View statusView, DataItem dataItem) {

        switch (displayStatus) {

            case (1): /// auto

                if (dataItem.rate > 0 || dataItem.errors > 0) {
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
        errorsTxt.setText( String.valueOf(errorsCount));
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

