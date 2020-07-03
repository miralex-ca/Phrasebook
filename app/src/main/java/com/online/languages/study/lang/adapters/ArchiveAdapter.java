package com.online.languages.study.lang.adapters;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.UCatsArchiveActivity;
import com.online.languages.study.lang.UCatsListActivity;
import com.online.languages.study.lang.data.DataObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.online.languages.study.lang.Constants.ACTION_CHANGE_ORDER;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.ACTION_VIEW;
import static com.online.languages.study.lang.Constants.UCAT_PARAM_BOOKMARK_ON;


public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.MyViewHolder>    {

    private Context context;
    private ArrayList<DataObject> dataList;
    private UCatsArchiveActivity activity;
    private PopupWindow popupwindow_obj;

    private boolean clickActive;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc;
        View wrap, settings, edit;


        MyViewHolder(View view) {
            super(view);

            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);

            wrap = itemView.findViewById(R.id.wrap);
            settings = itemView.findViewById(R.id.settings);

            edit = itemView.findViewById(R.id.ucatEdit);

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

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DataObject dataObject = dataList.get(position);

        holder.title.setText( dataObject.title);

        Locale current = context.getResources().getConfiguration().locale;

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG,  current);

        String formattedDate = dateFormat.format(new Date());

        holder.desc.setText("Создано:  " + formattedDate );



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

                activity.unarchive(dataObject);
            }
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }






}

