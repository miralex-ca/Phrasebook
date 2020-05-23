package com.online.languages.study.studymaster.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.online.languages.study.studymaster.R;
import com.online.languages.study.studymaster.data.CatData;
import com.online.languages.study.studymaster.data.Category;
import com.online.languages.study.studymaster.data.CustomCatData;
import com.online.languages.study.studymaster.data.Section;

import java.util.ArrayList;


public class CustomSectionAdapter extends RecyclerView.Adapter<CustomSectionAdapter.MyViewHolder> {


    private Context context;

    private ArrayList<Category> sectionCustomData;

    private int color;
    int type = 1;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, progressBox;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.sectionTitle);
            progressBox = (TextView) view.findViewById(R.id.sectionItemsCount);
        }
    }


    public CustomSectionAdapter(Context _context, ArrayList<Category> _sectionCustomData, int _color, int _type) {
        context = _context;
        sectionCustomData = _sectionCustomData;
        color = _color;
        type = _type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_sections_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Category section = sectionCustomData.get(position);

        int count = dataCount(section);
        int allCount = section.allDataCount;


        holder.title.setText(section.title);
        holder.title.setTag(R.id.data_count, count);


        holder.progressBox.setText(count + "/" + allCount );
        if (count > 0) {
            holder.progressBox.setTypeface(null, Typeface.BOLD);
            if (color != -1) holder.progressBox.setTextColor(color);
        } else {

            holder.progressBox.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return sectionCustomData.size();
    }


    private int dataCount (Category section) {

        if (type == 0) {
            section.customItemsCount = section.studiedDataCount;
        } else if (type == 1) {
            section.customItemsCount = section.familiarDataCount;
        } else if (type == 2) {
            section.customItemsCount= section.unknownDataCount;
        }

        return section.customItemsCount;
    }


}
