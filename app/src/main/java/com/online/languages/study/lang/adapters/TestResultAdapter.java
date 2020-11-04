package com.online.languages.study.lang.adapters;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.TestResult;

import java.util.ArrayList;


public class TestResultAdapter extends RecyclerView.Adapter<TestResultAdapter.MyViewHolder> {


    private Context context;

    private ArrayList<TestResult.ResultCategory> categoryArrayList;

    private boolean expand;



    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, taskCount, errorsCount;
        LinearLayout contentBox;
        ImageView icon;


        MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.sectionTitle);
            contentBox = view.findViewById(R.id.sectionContent);
            taskCount = view.findViewById(R.id.tasksCount);
            errorsCount = view.findViewById(R.id.errorsCount);
            icon = view.findViewById(R.id.expIcon);
        }
    }



    public TestResultAdapter(Context _context, ArrayList<TestResult.ResultCategory> _cats) {
        context = _context;
        categoryArrayList = _cats;
        expand = categoryArrayList.size()==1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_cat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        TestResult.ResultCategory category = categoryArrayList.get(position);

        holder.title.setText(category.title);
        holder.taskCount.setText(String.format(context.getString(R.string.result_task_count), category.dataItems.size()));
        holder.errorsCount.setText(String.format(context.getString(R.string.result_incorrect_count), category.errors.size()));


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.result_item, null);

        TextView txt = item.findViewById(R.id.content);
        txt.setText(Html.fromHtml(category.content));


        holder.contentBox.addView(item);


        holder.contentBox.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){

                    @Override
                    public void onGlobalLayout() {

                        int mHeight = holder.contentBox.getHeight();

                        holder.contentBox.getViewTreeObserver().removeOnGlobalLayoutListener( this );
                        holder.contentBox.setTag(R.integer.view_tag_2, mHeight);
                        if (!expand) holder.contentBox.setVisibility( View.GONE );
                    }

                });


        holder.contentBox.setTag(R.integer.view_tag_1, "closed");


        if (expand) {
            holder.contentBox.setTag(R.integer.view_tag_1, "open");
            holder.contentBox.setAlpha(1);
            holder.icon.setImageResource(R.drawable.ic_chevron_up);
            holder.contentBox.setVisibility( View.VISIBLE );
            int mHeight = holder.contentBox.getHeight();
            holder.contentBox.setTag(R.integer.view_tag_2, mHeight);
        }




    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }



}
