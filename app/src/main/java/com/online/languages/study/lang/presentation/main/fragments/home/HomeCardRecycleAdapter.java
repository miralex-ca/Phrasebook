package com.online.languages.study.lang.presentation.main.fragments.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.utils.ImageExtKt;
import com.online.languages.study.lang.utils.ListItemDataClickListener;

import java.util.List;

public class HomeCardRecycleAdapter extends RecyclerView.Adapter<HomeCardRecycleAdapter.MyViewHolder> {
    private final List<HomeSectionUiItem> sections;
    private final ListItemDataClickListener itemClickListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView icon;
        public View card;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            icon = view.findViewById(R.id.grid_image);
            card = view.findViewById(R.id.image_wrapper);
        }
    }

    public HomeCardRecycleAdapter(List<HomeSectionUiItem> sections, ListItemDataClickListener itemClickListener) {
        this.sections = sections;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_grid_item_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HomeSectionUiItem section = sections.get(position);
        holder.title.setText(section.getTitle());
        ImageExtKt.loadAssetPicCircle(holder.icon, section.getImage());
        holder.card.setOnClickListener(v -> itemClickListener.onItemClick(section.getSectionId()));
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

}
