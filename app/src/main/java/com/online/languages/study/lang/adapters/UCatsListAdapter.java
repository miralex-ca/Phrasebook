package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.UCatsListActivity;
import com.online.languages.study.lang.data.DataObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.online.languages.study.lang.Constants.ACTION_ARCHIVE;
import static com.online.languages.study.lang.Constants.ACTION_CHANGE_ORDER;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.ACTION_VIEW;
import static com.online.languages.study.lang.Constants.UCAT_PARAM_BOOKMARK_ON;


public class UCatsListAdapter extends RecyclerView.Adapter<UCatsListAdapter.MyViewHolder>    {

    private Context context;
    private ArrayList<DataObject> dataList;
    private UCatsListActivity activity;
    private PopupWindow popupwindow_obj;
    private String layout;

    private boolean clickActive;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, itemsCount, familiarCount, masteredCount;
        View wrap, settings, bookmark, bookmarkOn, bookmarkOff, edit, bookmarkWrap, mainWrap;


        MyViewHolder(View view) {
            super(view);

            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            itemsCount = itemView.findViewById(R.id.itemsCount);
            wrap = itemView.findViewById(R.id.wrap);
            settings = itemView.findViewById(R.id.settings);
            familiarCount = itemView.findViewById(R.id.familiarCount);
            masteredCount = itemView.findViewById(R.id.masteredCount);

            bookmark = itemView.findViewById(R.id.ucatBookmark);
            bookmarkOn = itemView.findViewById(R.id.ucatBookmarkOn);
            bookmarkOff = itemView.findViewById(R.id.ucatBookmarkOff);
            bookmarkWrap = itemView.findViewById(R.id.ucatBookmarkWrap);

            edit = itemView.findViewById(R.id.ucatEdit);

            mainWrap = itemView.findViewById(R.id.cat_item_wrap);

        }
    }


    public UCatsListAdapter(Context context, ArrayList<DataObject> dataList, UCatsListActivity activity) {
        this.context  = context;
        this.dataList = dataList;
        this.activity = activity;
        clickActive = true;

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        layout = appSettings.getString("set_ucat_list", "normal");

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        if (layout.equals("compact")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ucat_list_item_compact, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ucat_list_item, parent, false);
        }

        if (viewType == 2) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ucat_list_item_more, parent, false);
        }


        return new MyViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        int type = 1;
        if (dataList.get(position).id.equals("last")) type = 2;

        return type;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DataObject dataObject = dataList.get(position);

        holder.title.setText( dataObject.title);

        Locale current = context.getResources().getConfiguration().locale;

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG,  current);

        String formattedDate = dateFormat.format(new Date());

        holder.desc.setText("Дата:  " + formattedDate );

        holder.itemsCount.setText("Записей: " + dataObject.count );


        if (dataObject.id.equals("last")) {

            manageMoreView(holder.mainWrap, dataObject);

        }


        holder.familiarCount.setText("" + dataObject.progress_1 );
        holder.masteredCount.setText("" + dataObject.progress );


        if (dataObject.info.contains(UCAT_PARAM_BOOKMARK_ON)) {
            holder.bookmarkOn.setVisibility(View.VISIBLE);
            holder.bookmarkOff.setVisibility(View.GONE);
        } else {
            holder.bookmarkOn.setVisibility(View.GONE);
            holder.bookmarkOff.setVisibility(View.VISIBLE);

        }

        if (dataObject.count < 1) {
            holder.bookmarkWrap.setVisibility(View.GONE);
        }

        holder.wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openMyCat(dataObject);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAction(dataObject, ACTION_UPDATE);
            }
        });



        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBookmark(dataObject, holder.bookmarkOn, holder.bookmarkOff);
            }
        });



        final  View v = holder.settings;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = v.findViewById(R.id.position);
                popupwindow_obj = popupDisplay(dataObject);
                popupwindow_obj.showAsDropDown(view,0, 0);
                clickActive = true;
            }
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    private void setBookmark (DataObject dataObject, View bookmarkOn, View bookmarkOff) {

        boolean bookmarked = activity.bookmarkCat(dataObject);

            if (bookmarked) {
                bookmarkOn.setVisibility(View.VISIBLE);
                bookmarkOff.setVisibility(View.GONE);
            } else {
                bookmarkOn.setVisibility(View.GONE);
                bookmarkOff.setVisibility(View.VISIBLE);
            }

    }

    private PopupWindow popupDisplay(final DataObject dataObject) { // disply designing your popoup window

        final PopupWindow popupWindow = new PopupWindow(context); // inflet your layout or diynamic add view

        View view;

        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.popup_actions_ucat, null);


        View moveToTop = view.findViewById(R.id.moveToTop);
        View archive = view.findViewById(R.id.archive);


        moveToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickActionPopup(dataObject, ACTION_CHANGE_ORDER);
            }
        });

        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickActionPopup(dataObject, ACTION_ARCHIVE);
            }
        });



        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        popupWindow.setFocusable(true);

        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(view.getMeasuredHeight());


        popupWindow.setContentView(view);


        return popupWindow;
    }




    private void clickAction(final DataObject dataObject, final String type) {

        clickActive = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // String id =  vocab.sectionTags.get(act);

                activity.performAction(dataObject, type);

                clickActive = true;


            }
        }, 40);

    }


    private void clickActionPopup(final DataObject dataObject, final String type) {

        clickActive = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // String id =  vocab.sectionTags.get(act);

                activity.performAction(dataObject, type);

                popupwindow_obj.dismiss();
                clickActive = true;

            }
        }, 80);

    }


    private void manageMoreView(View view, DataObject dataObject) {

       View wrapper = view.findViewById(R.id.openMoreWrap);
       TextView moreTitle = view.findViewById(R.id.openMoreTxt);

       moreTitle.setText(dataObject.title);

        if (dataObject.info.equals("hide")) {
            wrapper.setVisibility(View.GONE);
        } else {
            wrapper.setVisibility(View.VISIBLE);

        }

    }




}

