package com.online.languages.study.lang.practice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.ExerciseActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.constructor.ConstructorActivity;
import com.online.languages.study.lang.constructor.ExerciseBuildActivity;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.Section;
import com.online.languages.study.lang.databinding.FragmentPracticeBinding;

import java.util.ArrayList;
import java.util.Map;


public class PracticeFragment extends Fragment {



    private FragmentPracticeBinding binding;

    OpenActivity openActivity;

    Context activityContext;

    NavStructure navStructure;
    String sectionID = "01010";

    DataManager dataManager;

    ArrayList<String> catIds;

    ArrayList<String> sectionCatIds;

    ArrayList<String> sectionStudiedIds;
    ArrayList<String> sectionUnStudiedIds;

    ArrayList<String> sectionWordCatIds;
    ArrayList<String> sectionStudiedWordsIds;
    ArrayList<String> sectionUnStudiedWordsIds;


    ArrayList<String> sectionIdsForTest;

    public PracticeFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPracticeBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        activityContext = getActivity();

        openActivity = new OpenActivity(activityContext);
        openActivity.setOrientation();

        navStructure = getActivity().getIntent().getParcelableExtra(Constants.EXTRA_NAV_STRUCTURE);
        sectionID = getActivity().getIntent().getStringExtra(Constants.EXTRA_SECTION_ID);
        dataManager = new DataManager(activityContext);

        getData();

        setClicks();

        return view;

    }

    private void setClicks() {

        binding.cardTestVocabulary.setOnClickListener(v -> openVocabularyTest());

        binding.cardTestPhrases.setOnClickListener(v-> openPhraseTest ());

        binding.cardTestAudio.setOnClickListener(v-> openAudioTest ());

        binding.cardTestConstructor.setOnClickListener(v-> {
           // openConstructor();

            openBuildTest("test_1", "Тест", 1);

        });

    }

    private void openConstructor() {
        Intent intent = new Intent(activityContext, ConstructorActivity.class);

        startActivityForResult(intent, 1);

        openActivity.pageTransition();
    }

    private void getData() {

        catIds = new ArrayList<>();

        sectionCatIds = new ArrayList<>();
        sectionStudiedIds = new ArrayList<>();
        sectionUnStudiedIds = new ArrayList<>();

        sectionWordCatIds = new ArrayList<>();
        sectionStudiedWordsIds = new ArrayList<>();
        sectionUnStudiedWordsIds = new ArrayList<>();


        final NavSection navSectionByID = navStructure.getNavSectionByID(sectionID);

        Section section = new Section(navSectionByID, activityContext); /// TODO initialize once

        sectionCatIds.addAll(section.checkCatIds);

        Map<String, String> mapCategoriesResult = dataManager.getCatProgress( sectionCatIds );

        for (String catId: sectionCatIds) {

            String categoryResult = mapCategoriesResult.get(catId);

            for (NavCategory navCategory: navSectionByID.navCategories) {
                if (navCategory.id.equals(catId) && !(navCategory.spec.equals("phrases"))) {

                    sectionWordCatIds.add(catId);

                    if (Integer.parseInt(categoryResult) > 20)   sectionStudiedWordsIds.add(catId);
                    else sectionUnStudiedWordsIds.add(catId);

                }
            }

            if (Integer.parseInt(categoryResult) > 20)  sectionStudiedIds.add(catId);
            else sectionUnStudiedIds.add(catId);

        }

    }


    private void openPhraseTest () {
        openPracticeTest("test_1", "Тест", 1);
    }

    private void openAudioTest () {
        openPracticeTest("test_1", "Тест", 3);
    }

    public void openPracticeTest(String cat_id, String title, int testType) {

        String[] stringArray = sectionStudiedIds.toArray(new String[0]);

        Intent intent = new Intent(activityContext, ExerciseActivity.class);

        intent.putExtra(Constants.EXTRA_CAT_TAG, cat_id);

        intent.putExtra("ex_type", testType);
        intent.putExtra("cat_title", title);
        intent.putExtra("practice", true);

        intent.putExtra("ids", stringArray);

        intent.putParcelableArrayListExtra("dataItems", new ArrayList<DataItem>());

        startActivityForResult(intent, 1);

        openActivity.pageTransition();
    }


    private void openVocabularyTest() {

        String[] stringArray = sectionStudiedWordsIds.toArray(new String[0]);

        stringArray = sectionStudiedWordsIds.toArray(new String[0]);

        Intent i = new Intent(activityContext, ExerciseActivity.class) ;

        i.putExtra("ex_type", 1);

        i.putExtra(Constants.EXTRA_SECTION_ID, sectionID);
        i.putExtra(Constants.EXTRA_CAT_TAG, "rev_pr_"+sectionID);

        i.putExtra("ids", stringArray);
        i.putExtra("unstudied_ids", sectionUnStudiedWordsIds.toArray(new String[0]));

        i.putParcelableArrayListExtra("dataItems", new ArrayList<>());

        startActivityForResult(i, 1);

        openActivity.pageTransition();
    }


    public void openBuildTest(String cat_id, String title, int testType) {

        String[] stringArray = sectionStudiedIds.toArray(new String[0]);

        Intent intent = new Intent(activityContext, ExerciseBuildActivity.class);

        intent.putExtra(Constants.EXTRA_CAT_TAG, cat_id);

        intent.putExtra("ex_type", testType);
        intent.putExtra("cat_title", title);
        intent.putExtra("practice", true);

        intent.putExtra("ids", stringArray);

        intent.putParcelableArrayListExtra("dataItems", new ArrayList<DataItem>());

        startActivityForResult(intent, 1);

        openActivity.pageTransition();

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


    }



}
