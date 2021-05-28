package com.online.languages.study.lang.practice;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.online.languages.study.lang.CatActivity;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.ExerciseActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.SectionStatsActivity;
import com.online.languages.study.lang.adapters.CatsListAdapter;
import com.online.languages.study.lang.adapters.InfoDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.RoundedTransformation;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.Section;
import com.online.languages.study.lang.data.ViewCategory;
import com.online.languages.study.lang.data.ViewSection;
import com.online.languages.study.lang.databinding.FragmentPracticeBinding;
import com.online.languages.study.lang.databinding.FragmentSectionBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;


public class SectionFragment extends Fragment {


    RecyclerView recyclerView;

    CatsListAdapter mAdapter;

    NavStructure navStructure;

    NavSection navSection;

    String parent = "root";
    String tSectionID = "01010";

    ViewSection viewSection;

    Boolean full_version;

    Boolean easy_mode;
    InfoDialog dataModeDialog;
    OpenActivity openActivity;
    DataManager dataManager;
    Context activityContext;
    private SharedPreferences appSettings;

    private FragmentSectionBinding binding;

    public SectionFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentSectionBinding.inflate(inflater, container, false);

        View rootview = binding.getRoot();

        activityContext = getActivity();
        openActivity = new OpenActivity(getActivity()) {

            @Override
            public void callActivityWithIntent(@NotNull Intent intent) {
                openActivityWithIntent(intent);
            }
        };
        openActivity.callCustomAct = true;

        appSettings = PreferenceManager.getDefaultSharedPreferences(activityContext);
        full_version = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);

        dataManager = new DataManager(activityContext);

        navStructure = getActivity().getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        parent = getActivity().getIntent().getStringExtra(Constants.EXTRA_SECTION_PARENT);

        tSectionID = getActivity().getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);

        navSection = navStructure.getNavSectionByID(tSectionID);

        viewSection = new ViewSection(activityContext, navSection, parent);


        recyclerView = rootview.findViewById(R.id.recycler_view);

        viewSection.getProgress();

        mAdapter = new CatsListAdapter(activityContext, viewSection.categories, full_version) {
            @Override
            public void clickOnListItem(int position) {
                openCatActivity(position);
            }
        };

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activityContext);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setSelected(true);
        recyclerView.setAdapter(mAdapter);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        binding.sectionTitle.setText(navSection.title_short);
        binding.sectionDesc.setText(navSection.desc);

        inserSectionImage();

        binding.sectionTopInfo.setOnClickListener(view -> openSectionStats());


        checkDisplayReview();


        return rootview;

    }

    private void openActivityWithIntent(Intent intent) {

        startActivityForResult(intent, 1);
    }

    private void inserSectionImage() {
        Picasso.with(activityContext)
                .load("file:///android_asset/pics/"+navSection.image)
                .transform(new RoundedTransformation(0,0))
                .fit()
                .centerCrop()
                .into(binding.catImage);
    }


    private void updateContent() {

        viewSection.getProgress();

        mAdapter = new CatsListAdapter(activityContext, viewSection.categories, full_version) {
            @Override
            public void clickOnListItem(int position) {
                openCatActivity(position);
            }
        };

        recyclerView.setAdapter(mAdapter);


    }

    private void checkDisplayReview() {

        boolean displayReviewCard = appSettings.getBoolean("set_review_card", getResources().getBoolean(R.bool.showReview));

        View card = binding.reviewTestsCard;

        if (displayReviewCard) card.setVisibility(View.VISIBLE);
        else card.setVisibility(View.GONE);

    }


    public void openCatActivity(int position) {

        ViewCategory viewCategory = viewSection.categories.get(position);

        if (viewCategory.type.equals("set")) return;

        if (!full_version) {
            if (!viewCategory.unlocked) {
                notifyLocked();
                return;
            }
        }

        openActivity.openFromViewCat(navStructure, tSectionID, viewCategory);

       // openCats(viewCategory.title, viewCategory.id, viewCategory.spec);



    }

    public void openCats(String title, String cat_id, String spec) {

        Intent i = new Intent(activityContext, CatActivity.class);

        i.putExtra(EXTRA_SECTION_ID, tSectionID);

        i.putExtra(Constants.EXTRA_CAT_ID, cat_id);
        i.putExtra("cat_title", title);
        i.putExtra(Constants.EXTRA_CAT_SPEC, spec);

        startActivityForResult(i, 1);

        openActivity.pageTransition();

    }


    public void notifyLocked() {
        String proContent = getString(R.string.pro_content);
        Snackbar.make(recyclerView, Html.fromHtml("<font color=\"#ffffff\">"+proContent+"</font>"), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

    }


    public void openSectionStats() {

        Intent i = new Intent(activityContext, SectionStatsActivity.class);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, tSectionID);
        i.putExtra(Constants.EXTRA_SECTION_NUM, 0);
        i.putExtra("from_section", PARAM_EMPTY);

        startActivityForResult(i, 1);

        openActivity.pageTransition();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        updateContent();
    }








}
