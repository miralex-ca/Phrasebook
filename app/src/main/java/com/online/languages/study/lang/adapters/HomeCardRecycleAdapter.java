package com.online.languages.study.lang.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.NavSection;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeCardRecycleAdapter extends RecyclerView.Adapter<HomeCardRecycleAdapter.MyViewHolder> {

    private ArrayList<NavSection> sections;
    Context context;
    private Boolean showWorld;
    private String theme;
    int type;
    int count = 5;
    boolean shortDesc = false;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, desc;
        public ImageView icon;
        public LinearLayout imageWrapper;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            desc = view.findViewById(R.id.desc);
            icon = view.findViewById(R.id.grid_image);
            imageWrapper = view.findViewById(R.id.image_wrapper);
        }
    }

    public HomeCardRecycleAdapter(Context _context, ArrayList<NavSection> _sections, String _theme, int _type) {
        context  = _context;

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        showWorld = appSettings.getBoolean("world_section", false);
        type = _type;
        sections = checkSections(_sections);
        theme = _theme;
        if (type == 2) count = context.getResources().getInteger(R.integer.home_card_pics_count);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_grid_item, parent, false);
        if (viewType == 2) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card_item, parent, false);
        }

        if (viewType == 3) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_grid_item_image, parent, false);
        }



        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 1;

        if (type == 2)  viewType = 2;
        if (type == 3)  viewType = 3;


        return viewType;
    }




    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        NavSection section = sections.get(position);

        holder.title.setText(section.title);

        if (shortDesc) holder.desc.setText(section.desc_short);
        else holder.desc.setText(section.desc);


        if (type == 1) {

            Picasso.with( context )
                    .load("file:///android_asset/pics/"+ section.image )
                    .fit()
                    .centerCrop()
                    .transform(new RoundedCornersTransformation(5,5))
                    .into(holder.icon);
        }

        if (type == 3) {

            Picasso.with( context )
                    .load("file:///android_asset/pics/"+ section.image )
                    .fit()
                    .centerCrop()
                    //.transform(new RoundedCornersTransformation(5,5))
                    .transform(new RoundedTransformation(0,0))
                    .into(holder.icon);
        }



        if (type == 2) {
            holder.imageWrapper.removeAllViews();

            String[] imgArray = context.getResources().getStringArray(R.array.home_card_1);
            if (position == 1) imgArray = context.getResources().getStringArray(R.array.home_card_2);
            if (position == 2) imgArray = context.getResources().getStringArray(R.array.home_card_3);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            if (section.desc.equals("") || section.desc.equals("none")) holder.desc.setVisibility(View.GONE);

            for (int i = 0; i < count; i++) {

                View item = inflater.inflate(R.layout.home_card_image, holder.imageWrapper, false);

                float weight = 1.0f / count;

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        weight
                );

                item.setLayoutParams(param);


                ImageView image = item.findViewById(R.id.image);

                Picasso.with( context )
                        .load("file:///android_asset/pics/"+ imgArray[i] )
                        .transform(new RoundedCornersTransformation(16,0))
                        .fit()
                        .centerCrop()
                        .into(image);

             //   if (theme.equals("westworld"))  image.setColorFilter(Color.argb(255, 50, 240, 240), PorterDuff.Mode.MULTIPLY);

                holder.imageWrapper.addView(item);
            }



        }


        if (theme.equals("westworld")) {
           // holder.icon.setColorFilter(Color.argb(255, 50, 240, 240), PorterDuff.Mode.MULTIPLY);
        }

    }

    @Override
    public int getItemCount() {
        return sections.size();
    }


    private ArrayList<NavSection> checkSections(ArrayList<NavSection> sections) {

        ArrayList<NavSection> list = new ArrayList<>();

        for (NavSection section: sections) {
            if (section.spec.equals("world")) {
                if (showWorld) list.add(section);
            } else {
                list.add(section);
            }
        }

        return list;

    }

}
