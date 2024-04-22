package com.online.languages.study.lang.adapters;


import static com.online.languages.study.lang.Constants.ACTION_ARCHIVE;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.Constants.PARAM_GROUP;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.presentation.UCatsArchiveActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.MyViewHolder>    {

    private Context context;
    private ArrayList<DataObject> dataList;
    private UCatsArchiveActivity activity;
    private PopupWindow popupwindow_obj;

    private boolean clickActive;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, itemsCount;
        View wrap, settings, mainWrap;


        MyViewHolder(View view) {
            super(view);

            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            itemsCount = itemView.findViewById(R.id.itemsCount);

            wrap = itemView.findViewById(R.id.wrap);
            settings = itemView.findViewById(R.id.settings);

            mainWrap = itemView.findViewById(R.id.cat_item_wrap);

        }
    }


    public ArchiveAdapter(Context context, ArrayList<DataObject> dataList, UCatsArchiveActivity activity) {

        this.context  = context;
        this.dataList = dataList;
        this.activity = activity;

        clickActive = true;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ucat_archive_item, parent, false);


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

        holder.desc.setText(String.format(context.getString(R.string.archive_item_date_text), formattedDate));

        holder.itemsCount.setText(String.format(context.getString(R.string.ucat_items_count), String.valueOf(dataObject.count)));



        if (dataObject.id.equals("last")) {
            manageMoreView(holder.mainWrap, dataObject);
        }


        if (dataObject.type != null && dataObject.type.equals(PARAM_GROUP)) {

            String description =
                    context.getResources().getQuantityString(R.plurals.topic_plurals, dataObject.count, dataObject.count);

            holder.desc.setText(description);

            holder.itemsCount.setText(PARAM_EMPTY);

        }



        holder.wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openMyCat(dataObject);
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




    private PopupWindow popupDisplay(final DataObject dataObject) { // disply designing your popoup window

        final PopupWindow popupWindow = new PopupWindow(context); // inflet your layout or diynamic add view

        View view;

        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.popup_actions_archive, null);


        View edit = view.findViewById(R.id.edit);
        View archive = view.findViewById(R.id.archive);

        if (dataObject.type != null && dataObject.type.equals(PARAM_GROUP)) {
            edit.setVisibility(View.GONE);
        }


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickActionPopup(dataObject, ACTION_UPDATE);
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

