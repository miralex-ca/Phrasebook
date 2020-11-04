package com.online.languages.study.lang.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.online.languages.study.lang.R;

import java.util.ArrayList;


public class ExRecycleAdapter extends RecyclerView.Adapter<ExRecycleAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> exLinkTitles;
    private ArrayList<String> exLinkDesc;
    private int[] results = {0,0,0,0};
    private Boolean exercises;
    public Boolean matchLines;


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc;
        ImageView icon;
        TextView progressTxt;
        View progressBox;



        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.exLinkTitle);
            desc = (TextView) view.findViewById(R.id.exLinkDesc);
            icon = (ImageView) view.findViewById(R.id.exLinksIconRight);
            progressTxt = (TextView) view.findViewById(R.id.exProgress);
            progressBox = view.findViewById(R.id.exProgressBox);
        }
    }


    public ExRecycleAdapter(Context _context, ArrayList<String> _exLinkTitles, ArrayList<String> _exLinkDesc, int[] _results, Boolean _exercises) {
        context = _context;
        exLinkTitles = _exLinkTitles;
        exLinkDesc = _exLinkDesc;
        exercises = _exercises;
        results = _results;
        matchLines = false;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ex_links_item, parent, false);

        if (viewType == 2) itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ex_links_item_inline, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int type = 1;
        if (matchLines) type = 2;

        return type;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        holder.title.setText( exLinkTitles.get(position));
        holder.desc.setText( exLinkDesc.get(position));

        if (position == 0 && exercises) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.iconCarousel, typedValue, true);
            int drawableRes = typedValue.resourceId;
            holder.icon.setImageResource(drawableRes);

        } else {
            int result = results[ position ] ;
            if (results[0] > 0 || results[1] > 0 || results[2] > 0 || results[3] > 0) {

                String resultTxt = String.valueOf(result) + "%";
                holder.progressBox.setVisibility(View.VISIBLE);
                holder.progressTxt.setText(String.valueOf(resultTxt));
            }
        }


    }

    @Override
    public int getItemCount() {
        return exLinkTitles.size();
    }
}
