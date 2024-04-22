package com.online.languages.study.lang.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.AdditionsListAdapter;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.databinding.FragmentAdditionsBinding;
import com.online.languages.study.lang.presentation.TextActivity;
import com.online.languages.study.lang.tools.AdditionsData;

import java.util.ArrayList;


public class AdditionsFragment extends Fragment {

    private FragmentAdditionsBinding binding;

    Context activityContext;
    OpenActivity openActivity;

    DataManager dataManager;

    SharedPreferences appSettings;

    public String categoryID = "";

    public AdditionsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAdditionsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        activityContext = getActivity();
        openActivity = new OpenActivity(activityContext);

        appSettings = PreferenceManager.getDefaultSharedPreferences(activityContext);

        dataManager = new DataManager(activityContext);

        ArrayList<DataObject> list = getData();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activityContext);
        binding.recyclerView.setLayoutManager(mLayoutManager);

        AdditionsListAdapter tAdapter = new AdditionsListAdapter(activityContext, list) {
            @Override
            public void callBack(DataItem dataitem) {
                if (dataitem != null) {
                    openTextPage(dataitem.id, dataitem.item);
                }
            }
        };

        binding.recyclerView.setAdapter(tAdapter);

        ViewCompat.setNestedScrollingEnabled(binding.recyclerView, false);

        return view;
    }



    private ArrayList<DataObject> getData() {

        String[] tags = activityContext.getResources().getStringArray(R.array.additions);

        AdditionsData additionsData = new AdditionsData();
        additionsData.setPagesTagsList(tags);
        additionsData.processData();

        return additionsData.getAdditionsList();

    }


    public void openTextPage(String catID, String title) {
        Intent i = new Intent(getActivity(), TextActivity.class);

        i.putExtra("source_id", catID);
        i.putExtra("page_title", title);

        startActivityForResult(i, 0);
        openActivity.pageTransition();
    }






}
