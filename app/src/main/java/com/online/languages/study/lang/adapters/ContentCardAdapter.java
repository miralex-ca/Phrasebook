package com.online.languages.study.lang.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.online.languages.study.lang.CatActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.UserListActivity;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_COMPACT;
import static com.online.languages.study.lang.Constants.CAT_LIST_VIEW_DEFAULT;
import static com.online.languages.study.lang.Constants.SHOW_GRAMMAR;


public class ContentCardAdapter extends RecyclerView.Adapter<ContentCardAdapter.MyViewHolder> {

    private ArrayList<DataItem> dataList;
    Context context;
    private int showStatus;
    private String theme;
    private String layoutType;
    private ColorProgress colorProgress;
    private boolean autoDivider;

    private DataManager dataManager;

    boolean speaking;

    CatActivity catActivity;
    UserListActivity starredActivity;

    int activityType;
    private static final int CAT_ACTIVITY = 1;
    private static final int STARRED_ACTIVITY = 2;

    int grammarCharLimit = 10;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt, translate, transcribe, grammar;
        View helperView;

        View starIcon, statusView, divider, itemWrap, playButton, playHolder, starHolder, changeStar;

        MyViewHolder(View view) {
            super(view);

            txt = itemView.findViewById(R.id.itemText);
            translate = itemView.findViewById(R.id.itemInfo);
            transcribe = itemView.findViewById(R.id.itemTrans);
            helperView =  itemView.findViewById(R.id.animObj);
            itemWrap =  itemView.findViewById(R.id.itemWrap);
            starIcon = itemView.findViewById(R.id.voclistStar);
            statusView = itemView.findViewById(R.id.status_wrap);
            divider = itemView.findViewById(R.id.catItemDivider);

            grammar = itemView.findViewById(R.id.itemGrammar);

            playButton = itemView.findViewById(R.id.playButton);
            starHolder = itemView.findViewById(R.id.starHolder);
            playHolder = itemView.findViewById(R.id.playHolder);
            changeStar = itemView.findViewById(R.id.changeStar);

        }
    }

    public ContentCardAdapter(Context _context, ArrayList<DataItem> _dataList,
                              int _show_status, String _theme, boolean divider, String _layoutType, CatActivity activity) {

        this(_context, _dataList,_show_status, _theme, divider, _layoutType);

        activityType = CAT_ACTIVITY;
        catActivity = activity;

    }

    public ContentCardAdapter(Context _context, ArrayList<DataItem> _dataList,
                              int _show_status, String _theme, boolean divider, String _layoutType, UserListActivity activity) {

        this(_context, _dataList,_show_status, _theme, divider, _layoutType);

        activityType = STARRED_ACTIVITY;
        starredActivity = activity;
    }


    public ContentCardAdapter(Context _context, ArrayList<DataItem> _dataList,
                              int _show_status, String _theme, boolean divider, String _layoutType) {

        dataList = _dataList;
        context = _context;
        showStatus = _show_status;
        theme = _theme;
        colorProgress = new ColorProgress(context);
        autoDivider = divider;

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(_context);

        speaking = appSettings.getBoolean("set_speak", true);

        if (_layoutType.equals("auto")) {
            layoutType = appSettings.getString(CAT_LIST_VIEW, CAT_LIST_VIEW_DEFAULT);
        } else {
            layoutType = _layoutType;
        }

        dataManager = new DataManager(context);

        grammarCharLimit = context.getResources().getInteger(R.integer.card_grammar_limit);

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == 2) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item_compact_2, parent, false);
        } else if (viewType == 3) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item_divider, parent, false);

        }else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item_card, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;

        return type;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        DataItem dataItem = dataList.get(position);

        holder.txt.setText( dataItem.item);
        holder.translate.setText( dataItem.info);
        holder.helperView.setTag(dataItem.id);

        holder.itemWrap.setTag(position);

        holder.changeStar.setTag(dataItem);
        holder.playButton.setTag(dataItem);

        String  transcript = dataManager.getTranscriptFromData(dataItem);
        boolean transcription = transcript.trim().equals("");

        if (transcription) holder.transcribe.setVisibility(View.GONE);
        else holder.transcribe.setVisibility(View.VISIBLE);

        holder.transcribe.setText( String.format("[ %s ]", transcript) );

        String grammar = dataItem.grammar.replace("n. ", "").replace(".", "");

        if (SHOW_GRAMMAR && grammar.length() > 0 && grammar.length() < grammarCharLimit) {
            holder.grammar.setVisibility(View.VISIBLE);
            holder.grammar.setText(grammar);
        }

        if (position == 0 || autoDivider) holder.divider.setVisibility(View.INVISIBLE);

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

        if (!speaking) {
            holder.playHolder.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.starHolder.getLayoutParams();
            params.removeRule(RelativeLayout.ABOVE);
        }

        if (activityType == CAT_ACTIVITY) {
            attachLongClickToCat(holder.itemWrap, holder.changeStar);
        }

        if (activityType == STARRED_ACTIVITY) {
            attachLongClickToStarred(holder.itemWrap, holder.changeStar);
        }

    }

    private void attachLongClickToCat(final View view, final View infoView) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                catActivity.changeStarred(infoView, true);
                return true;    // <- set to true
            }
        });
    }

    private void attachLongClickToStarred(final View view, final View infoView) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                starredActivity.changeStarred(infoView, true);
                return true;    // <- set to true
            }
        });
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
        errorsTxt.setText( String.format(context.getString(R.string.errors_count), errorsCount));
        View statuses = statusBox.findViewById(R.id.itemStatus);


        if (errorsCount > 0) {
            errorsTxt.setVisibility(View.VISIBLE);
           statuses.setVisibility(View.VISIBLE);

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

