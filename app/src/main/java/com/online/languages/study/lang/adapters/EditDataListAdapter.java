package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.online.languages.study.lang.MyCatEditActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataItem;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.ACTION_CHANGE_ORDER;
import static com.online.languages.study.lang.Constants.ACTION_DELETE;
import static com.online.languages.study.lang.Constants.ACTION_MOVE;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.ACTION_VIEW;


public class EditDataListAdapter extends RecyclerView.Adapter<EditDataListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DataItem> dataList;
    private PopupWindow popupwindow_obj;
    MyCatEditActivity activity;

    private boolean clickActive;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt, translate;
        View settings, wrap;

        MyViewHolder(View view) {
            super(view);

            txt = itemView.findViewById(R.id.itemText);
            translate = itemView.findViewById(R.id.itemInfo);
            settings = itemView.findViewById(R.id.settings);
            wrap = itemView.findViewById(R.id.wrap);
        }
    }


    public EditDataListAdapter(Context context, ArrayList<DataItem> dataList, MyCatEditActivity activity) {
        this.context  = context;
        this.dataList = dataList;
        this.activity = activity;
        clickActive = true;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_dat_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final DataItem dataItem = dataList.get(position);

        holder.txt.setText( dataItem.item);
        holder.translate.setText( dataItem.info);


        final  View v = holder.settings;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = v.findViewById(R.id.position);
                popupwindow_obj = popupDisplay(dataItem, position);
                popupwindow_obj.showAsDropDown(view,0, 0);
                clickActive = true;
            }
        });

        holder.wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.editClick(dataItem, ACTION_UPDATE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }




    private PopupWindow popupDisplay(final DataItem dataItem, int position) { // disply designing your popoup window

        final PopupWindow popupWindow = new PopupWindow(context); // inflet your layout or diynamic add view

        View view;

        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.popup_edit, null);



        View viewItem = view.findViewById(R.id.view);
        View editItem = view.findViewById(R.id.edit);
        View deleteItem  = view.findViewById(R.id.delete);

        View moveItem  = view.findViewById(R.id.moveToTop);

        if (position == 0) {
            moveItem.setVisibility(View.GONE);
        } else {
            moveItem.setVisibility(View.VISIBLE);
        }

        moveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClick(dataItem, ACTION_CHANGE_ORDER);
            }
        });

        viewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClick(dataItem, ACTION_VIEW);
            }
        });

        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClick(dataItem, ACTION_UPDATE);
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClick(dataItem, ACTION_DELETE);
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

    private void editClick(final DataItem dataItem, final String type) {

        clickActive = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // String id =  vocab.sectionTags.get(act);
                activity.editClick(dataItem, type);
                popupwindow_obj.dismiss();

            }
        }, 100);

    }



}

