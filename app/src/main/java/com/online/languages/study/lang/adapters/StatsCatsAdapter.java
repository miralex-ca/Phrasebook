package com.online.languages.study.lang.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.Section;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class StatsCatsAdapter extends RecyclerView.Adapter<StatsCatsAdapter.MyViewHolder> {

    private ArrayList<Section> sections = new ArrayList<>();
    Context context;

    private ColorProgress colorProgress;

    private Boolean simplified = false;


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, progress, desc;

        ImageView image;


        MyViewHolder(View view) {
            super(view);

            title = itemView.findViewById(R.id.titleText);
            progress = itemView.findViewById(R.id.progressText);
            desc = itemView.findViewById(R.id.descText);
            image = itemView.findViewById(R.id.itemImage);
        }
    }


    public StatsCatsAdapter(Context _context, ArrayList<Section> _sections) {
        sections = _sections;
        context = _context;
        colorProgress = new ColorProgress(context);
        DataManager d = new DataManager(context);
        d.getParams();
        simplified = d.simplified;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_cat_list_small, parent, false);

        if (sections.size() < 4) itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_cat_list_item_big, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Section section = sections.get(position);

        holder.title.setText(section.title_short);

        int catsNum = section.stadiedCatsCount;
        int errors = section.errorsCount;

        int progress = section.progress;

        if (Constants.SCREEN_SHOW) {

            if (position == 0) {progress = 100; errors = 0; catsNum = 3;}
            if (position == 1) {progress = 92; errors = 1; catsNum = 3;}
            if (position == 2) {progress = 96; errors = 0; catsNum = 4;}
            if (position == 3) {progress = 84; errors = 2; catsNum = 3;}
            if (position == 4) {progress = 69; errors = 0; catsNum = 3;}
            if (position == 5) {progress = 100; errors = 0; catsNum = 3;}
            if (position == 6) {progress = 69; errors = 0; catsNum = 3;}
            if (position == 7) {progress = 0; errors = 0; catsNum = 0;}
            if (position == 8) {progress = 0; errors = 0; catsNum = 0;}
        }


        String desc = context.getString(R.string.cats_studied)+ catsNum+" &nbsp;&nbsp;&nbsp;";

        if (errors > 0) {
            desc = desc + "<font color=\"red\">Ошибок: "+errors+"</font>";
        } else {

            desc = desc + "Ошибок: "+ errors;
        }

        holder.desc.setText(Html.fromHtml(desc));
        if (simplified) {
            holder.desc.setText(section.desc_short);
        }

        // */

        holder.progress.setText(progress +"%");


        holder.progress.setTextColor( colorProgress.getColorFromAttr(progress) );

        Picasso.with(context )
                .load("file:///android_asset/pics/"+section.image)
                .transform(new RoundedTransformation(0,0))
                .fit()
                .centerCrop()
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return sections.size();
    }


}

