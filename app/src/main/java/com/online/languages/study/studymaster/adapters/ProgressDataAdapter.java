package com.online.languages.study.studymaster.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.online.languages.study.studymaster.R;
import com.online.languages.study.studymaster.data.DataItem;
import com.online.languages.study.studymaster.data.Section;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProgressDataAdapter extends RecyclerView.Adapter<ProgressDataAdapter.MyViewHolder> {


    ArrayList<Section> data;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, knownTxt, sudiedTxt;
        ProgressBar  progressBar, progressBarKnown;

        public MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            progressBar = view.findViewById(R.id.pb);
            progressBarKnown = view.findViewById(R.id.pbKnown);

            knownTxt = view.findViewById(R.id.knownTxtProgress);
            sudiedTxt = view.findViewById(R.id.studiedTxtProgress);
        }
    }


    public ProgressDataAdapter(Context _context, ArrayList<Section> _data) {
        data = _data;
        context  = _context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Section section = data.get(position);


        holder.title.setText(section.title);
        holder.progressBar.setProgress(section.knownPart);
        holder.progressBarKnown.setProgress(section.studiedPart);


        String known = section.knownPart + "%  (" + section.familiarDataCount +"/" + section.allDataCount +")";
        String studied = section.studiedPart + "% (" + section.studiedDataCount +"/" + section.allDataCount +")";

        holder.knownTxt.setText(known );
        holder.sudiedTxt.setText(studied);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }



}
