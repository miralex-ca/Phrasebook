package com.online.languages.study.lang.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataManager;


public class MenuListAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater lInflater;
    private String[] titles;
    private int activeItem;
    int hideItem;
    DataManager dataManager;
    boolean tasksVisible = false;

    private int activeItemResourceId;

    private int[] icons = {
            R.drawable.ic_nav_home,
            R.drawable.ic_nav_gallery,
            R.drawable.ic_nav_star,
            R.drawable.ic_nav_stats,
            R.drawable.ic_nav_tasks,
            R.drawable.ic_nav_notes,
            R.drawable.ic_nav_settings,
            R.drawable.ic_nav_info,
            R.drawable.ic_nav_send,
    };


    public MenuListAdapter(Context context, String[] _titles, int _activeItem) {
        ctx = context;
        titles = _titles;
        activeItem = _activeItem;

        dataManager = new DataManager(context, true);

        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(ctx);
        ThemeAdapter themeAdapter = new ThemeAdapter(ctx, appSettings.getString(Constants.SET_THEME_TXT, Constants.SET_THEME_DEFAULT), true );

        TypedArray a = context.getTheme().obtainStyledAttributes(themeAdapter.styleTheme, new int[] {R.attr.multipane_menu_item_active_bg});
        activeItemResourceId = a.getResourceId(0, 0);
        a.recycle();

        String tasksNavSetting = appSettings.getString("set_tasks_nav", context.getString(R.string.set_tasks_nav_default));

        tasksVisible = tasksNavSetting.equals("menu");

    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        view = lInflater.inflate(R.layout.menu_list_item, parent, false);
        view.setBackground( null );


        if (position == activeItem) {
            view.setBackgroundResource(activeItemResourceId);
        }

        ((ImageView) view.findViewById(R.id.menu_list_icon)).setImageResource(icons[position]);
        ((TextView) view.findViewById(R.id.menuItemTitle)).setText(titles[position]);

        View divider;
        divider = view.findViewById(R.id.menu_list_divider);

        if ( position == 1)  {  // hide gallery if false in params
           if (!dataManager.gallerySection) view = lInflater.inflate(R.layout.null_item, null);
        }

        if ( position == 3)  {  // hide stats if false in params
            if (!dataManager.statsSection) view = lInflater.inflate(R.layout.null_item, null);
        }

        if ( position == 4)  {  // hide tasks if false in params
            if (!tasksVisible) view = lInflater.inflate(R.layout.null_item, null);
        }

        if ( position == 5 && !tasksVisible)  {  // hide tasks if false in params
            divider.setVisibility(View.VISIBLE);
        }

        if (position==4 || position==9) {
            divider.setVisibility(View.VISIBLE);
        }

        return view;
    }



}
