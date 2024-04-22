package com.online.languages.study.lang.adapters;


import static com.online.languages.study.lang.Constants.ACTION_ARCHIVE;
import static com.online.languages.study.lang.Constants.ACTION_CHANGE_ORDER;
import static com.online.languages.study.lang.Constants.ACTION_EDIT_GROUP;
import static com.online.languages.study.lang.Constants.ACTION_MOVE;
import static com.online.languages.study.lang.Constants.ACTION_UPDATE;
import static com.online.languages.study.lang.Constants.FOLDER_PICS;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.Constants.PARAM_GROUP;
import static com.online.languages.study.lang.Constants.UCAT_PARAM_BOOKMARK_ON;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.presentation.usercategories.UCatsListActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class UCatsListAdapter extends RecyclerView.Adapter<UCatsListAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<DataObject> dataList;
    private final UCatsListActivity activity;
    private PopupWindow popupwindow_obj;
    private final String layout;

    private boolean clickActive;
    private final String folder;
    private boolean displayStatus;



    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc, itemsCount, familiarCount, masteredCount, description;
        View wrap, settings, bookmark, bookmarkOn, bookmarkOff, edit,
                bookmarkWrap, mainWrap, progressWrap, rightEditWrap, editMenuItem;

        ImageView icon;


        MyViewHolder(View view) {
            super(view);

            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            description = itemView.findViewById(R.id.description);
            itemsCount = itemView.findViewById(R.id.itemsCount);
            wrap = itemView.findViewById(R.id.wrap);
            settings = itemView.findViewById(R.id.settings);
            familiarCount = itemView.findViewById(R.id.familiarCount);
            masteredCount = itemView.findViewById(R.id.masteredCount);

            bookmark = itemView.findViewById(R.id.ucatBookmark);
            bookmarkOn = itemView.findViewById(R.id.ucatBookmarkOn);
            bookmarkOff = itemView.findViewById(R.id.ucatBookmarkOff);
            bookmarkWrap = itemView.findViewById(R.id.ucatBookmarkWrap);

            edit = itemView.findViewById(R.id.ucatEdit);

            mainWrap = itemView.findViewById(R.id.cat_item_wrap);
            progressWrap = itemView.findViewById(R.id.progress_wrap);
            rightEditWrap = itemView.findViewById(R.id.rightEditWrap);

            icon = itemView.findViewById(R.id.icon);

        }
    }


    public UCatsListAdapter(Context context, ArrayList<DataObject> dataList, UCatsListActivity activity) {
        this.context = context;
        this.dataList = dataList;
        this.activity = activity;
        clickActive = true;

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        layout = appSettings.getString("set_ucat_list", "normal");

        displayStatus = !appSettings.getString("show_status", Constants.STATUS_SHOW_DEFAULT).equals("0");

        folder = context.getString(R.string.group_pics_folder);

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        if (viewType == 3) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ucat_list_item_more, parent, false);
        } else if (viewType == 2) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ucat_list_item_compact, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ucat_list_item, parent, false);
        }

        return new MyViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        int type = 1;

        DataObject dataObject = dataList.get(position);

        if (layout.equals("compact")) type = 2;
        if (layout.equals("mixed") && !dataObject.type.equals(PARAM_GROUP)) type = 2;

        if (dataObject.id.equals("last")) type = 3;


        return type;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DataObject dataObject = dataList.get(position);

        holder.title.setText(dataObject.title);

        Locale current = context.getResources().getConfiguration().locale;

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, current);

        String formattedDate = dateFormat.format(new Date(dataObject.time_created));

        holder.desc.setText(String.format(context.getString(R.string.ucat_created_time), formattedDate));


        if (dataObject.id.equals("last")) {
            manageMoreView(holder.mainWrap, dataObject);
        }


        if (dataObject.info.contains(UCAT_PARAM_BOOKMARK_ON)) {
            holder.bookmarkOn.setVisibility(View.VISIBLE);
            holder.bookmarkOff.setVisibility(View.GONE);
        } else {
            holder.bookmarkOn.setVisibility(View.GONE);
            holder.bookmarkOff.setVisibility(View.VISIBLE);
        }

        if (dataObject.count < 1) {
            holder.bookmarkWrap.setVisibility(View.GONE);
        } else {
            holder.bookmarkWrap.setVisibility(View.VISIBLE);
        }


        if (dataObject.type != null && dataObject.type.equals(PARAM_GROUP)) {

            holder.progressWrap.setVisibility(View.GONE);
            holder.rightEditWrap.setVisibility(View.GONE);
            holder.description.setVisibility(View.VISIBLE);

            String desc =
                    context.getResources().getQuantityString(R.plurals.topic_plurals, dataObject.count, dataObject.count);

            if (dataObject.desc.equals(PARAM_EMPTY)) {
                holder.description.setText(desc);

            } else {
                holder.description.setText(dataObject.desc);
                holder.desc.setText(desc);
            }

            if (emptyImage(dataObject.image)) {
                holder.icon.setVisibility(View.GONE);
            } else {

                holder.icon.setVisibility(View.VISIBLE);

                Picasso.with(context)
                        .load(FOLDER_PICS + folder + dataObject.image)
                        .fit()
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(10, 0))
                        .into(holder.icon);

            }


        } else {

            holder.rightEditWrap.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(View.GONE);

            manageCatDescText(holder.itemsCount, holder.desc, dataObject);
        }


        holder.wrap.setOnClickListener(v -> activity.openMyCat(dataObject));

        holder.edit.setOnClickListener(v -> clickAction(dataObject, ACTION_UPDATE));

        holder.bookmark.setOnClickListener(v ->
                setBookmark(dataObject, holder.bookmarkOn, holder.bookmarkOff));


        final View v = holder.settings;

        v.setOnClickListener(v1 -> {
            View view = v1.findViewById(R.id.position);
            popupwindow_obj = popupDisplay(dataObject);
            popupwindow_obj.showAsDropDown(view, 0, 0);
            clickActive = true;
        });


    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }


    private void manageCatDescText(TextView description, TextView details, DataObject category) {

        String count = String.format(context.getString(R.string.ucat_items_count), category.count + "");


        if (displayStatus && category.progress_1 > 0) {

            String progressCount;

            if (category.progress > category.count/5) { /// info about mastered if 20% are mastered

                progressCount = context.getString(R.string.ucat_mastered_items) + category.progress;

            } else {
                progressCount = context.getString(R.string.ucat_familiar_items) + category.progress_1;
            }

            count = count + "     " + progressCount;
        }

        if (category.desc.trim().equals(PARAM_EMPTY)) {
            description.setText(count);
        } else {
            description.setText(category.desc);
            details.setText(count);
        }
    }

    private boolean emptyImage(String picName) {

        boolean noImage = false;

        if (picName.equals("none") || picName.equals("empty.png") || picName.equals("")) {
            noImage = true;
        }

        return noImage;
    }

    private void setBookmark(DataObject dataObject, View bookmarkOn, View bookmarkOff) {

        boolean bookmarked = activity.bookmarkCat(dataObject);

        if (bookmarked) {
            bookmarkOn.setVisibility(View.VISIBLE);
            bookmarkOff.setVisibility(View.GONE);
        } else {
            bookmarkOn.setVisibility(View.GONE);
            bookmarkOff.setVisibility(View.VISIBLE);
        }

    }

    private PopupWindow popupDisplay(final DataObject dataObject) { // disply designing your popoup window

        final PopupWindow popupWindow = new PopupWindow(context); // inflet your layout or diynamic add view

        View view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.popup_actions_ucat, null);


        View moveToTop = view.findViewById(R.id.moveToTop);
        View moveToGroup = view.findViewById(R.id.moveToGroup);
        View edit = view.findViewById(R.id.edit_from_menu);
        View archive = view.findViewById(R.id.archive);


        moveToTop.setOnClickListener(v -> clickActionPopup(dataObject, ACTION_CHANGE_ORDER));

        moveToGroup.setOnClickListener(v -> clickActionPopup(dataObject, ACTION_MOVE));


        archive.setOnClickListener(v -> clickActionPopup(dataObject, ACTION_ARCHIVE));

        String editAction = dataObject.type.equals("group") ? ACTION_EDIT_GROUP : ACTION_UPDATE;

        edit.setOnClickListener(v -> clickActionPopup(dataObject, editAction));

        if (layout.equals("compact") || layout.equals("mixed")) {
            edit.setVisibility(View.VISIBLE);
        }

        if (dataObject.type.equals("group")) {
            moveToGroup.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
        }

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


    private void clickAction(final DataObject dataObject, final String type) {

        clickActive = false;

        new Handler().postDelayed(() -> {
            // String id =  vocab.sectionTags.get(act);
            activity.performAction(dataObject, type);
            clickActive = true;


        }, 40);

    }


    private void clickActionPopup(final DataObject dataObject, final String type) {

        clickActive = false;

        new Handler().postDelayed(() -> {
            // String id =  vocab.sectionTags.get(act);

            activity.performAction(dataObject, type);

            popupwindow_obj.dismiss();
            clickActive = true;

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

