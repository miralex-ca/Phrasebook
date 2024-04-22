package com.online.languages.study.lang.practice;

import static com.online.languages.study.lang.Constants.PRACTICE_AUTOLEVEL_SETTING;
import static com.online.languages.study.lang.Constants.PRACTICE_EXCLUDED_SETTING;
import static com.online.languages.study.lang.Constants.PRACTICE_LEVEL_SETTING;
import static com.online.languages.study.lang.Constants.PRACTICE_LIMIT_DEFAULT;
import static com.online.languages.study.lang.Constants.PRACTICE_LIMIT_SETTING;
import static com.online.languages.study.lang.practice.PracticeParamsDialog.LEVEL_UNKNOWN;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.HintDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.constructor.ExerciseBuildActivity;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.Section;
import com.online.languages.study.lang.databinding.FragmentPracticeBinding;
import com.online.languages.study.lang.presentation.exercise.ExerciseActivity;

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

    ArrayList<String> sectionPhraseCatIds;
    ArrayList<String> sectionStudiedPhraseIds;
    ArrayList<String> sectionUnStudiedPhraseIds;

    ArrayList<String> sectionIdsNoExcluded;


    int excludedCount = 0;

    int testLevel = 1;

    private final String TEST_PRACTICE = "practice_";
    private final String TEST_TYPE_VOCAB = "vocab_";
    private final String TEST_TYPE_AUDIO = "audio_";
    private final String TEST_TYPE_PHRASES = "phrases_";
    private final String TEST_TYPE_BUILD = "build_";

    private final int STUDIED_PROGRESS = 10;

    String autoLevelValue = LEVEL_UNKNOWN;

    SharedPreferences appSettings;

    ArrayList<String[]> sectionStudiedCats;
    ArrayList<String[]> sectionCatsList;




    private final int DIALOG_OPEN = 1;
    private final int DIALOG_CLOSED = 0;

    private int paramDialogStatus = DIALOG_CLOSED;

    public PracticeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPracticeBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        activityContext = getActivity();

        appSettings = PreferenceManager.getDefaultSharedPreferences(activityContext);

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
        binding.cardTestPhrases.setOnClickListener(v -> openPhraseTest());
        binding.cardTestAudio.setOnClickListener(v -> openAudioTest());
        binding.cardTestConstructor.setOnClickListener(v -> openBuildTest(1));

        binding.paramsPracticeEdit.setOnClickListener(v -> openPracticeParams());
        binding.practiceParaamsIcon.setOnClickListener(v -> openPracticeParams());
        binding.paramsPracticeInfo.setOnClickListener(v -> openPracticeParamsInfo());

    }



    public void getData() {

        String savedList =  appSettings.getString(PRACTICE_EXCLUDED_SETTING + sectionID, "");

        catIds = new ArrayList<>();

        sectionCatIds = new ArrayList<>();
        sectionStudiedIds = new ArrayList<>();
        sectionUnStudiedIds = new ArrayList<>();

        sectionWordCatIds = new ArrayList<>();
        sectionStudiedWordsIds = new ArrayList<>();
        sectionUnStudiedWordsIds = new ArrayList<>();


        sectionPhraseCatIds = new ArrayList<>();
        sectionStudiedPhraseIds = new ArrayList<>();
        sectionUnStudiedPhraseIds = new ArrayList<>();

        sectionStudiedCats = new ArrayList<>();
        sectionCatsList = new ArrayList<>();

        sectionIdsNoExcluded = new ArrayList<>();


        final NavSection navSectionByID = navStructure.getNavSectionByID(sectionID);

        Section section = new Section(navSectionByID, activityContext);

        sectionCatIds.addAll(section.checkCatIds);

        Map<String, String> mapCategoriesResult = dataManager.getCatProgress(sectionCatIds);
        excludedCount = 0;

        for (String catId : sectionCatIds) {

           boolean excludedTopic =  savedList.contains(catId);
           if (excludedTopic) excludedCount++;

            String categoryResult = mapCategoriesResult.get(catId);
            if (categoryResult == null) categoryResult = "0";

            String catTitle = "";

            for (NavCategory navCategory : navSectionByID.navCategories) {
                if (navCategory.id.equals(catId)) {

                    catTitle = navCategory.title;

                    if (!excludedTopic) {

                        if (navCategory.spec.equals("phrases")) {

                            sectionPhraseCatIds.add(catId);

                            if (Integer.parseInt(categoryResult) > STUDIED_PROGRESS)
                                sectionStudiedPhraseIds.add(catId);
                            else sectionUnStudiedPhraseIds.add(catId);


                        } else {

                            sectionWordCatIds.add(catId);
                            if (Integer.parseInt(categoryResult) > STUDIED_PROGRESS)
                                sectionStudiedWordsIds.add(catId);
                            else sectionUnStudiedWordsIds.add(catId);

                        }

                    }
                }
            }

            sectionCatsList.add(new String[]{catId, catTitle});

            if (!excludedTopic) {

                sectionIdsNoExcluded.add(catId);

                if (Integer.parseInt(categoryResult) > STUDIED_PROGRESS) {
                    sectionStudiedIds.add(catId);
                    sectionStudiedCats.add(new String[]{catId, catTitle});
                } else sectionUnStudiedIds.add(catId);

            }




        }

        checkPracticeParams();

        getTestsData();

    }

    private void checkPracticeParams() {

        checkTopicsParamsDesc();

        checkLevelParamsDesc();

        checkLimitPramsDesc();

    }

    private void checkLimitPramsDesc() {

        String limit = appSettings.getString(PRACTICE_LIMIT_SETTING, String.valueOf(PRACTICE_LIMIT_DEFAULT));

        String desc = getString(R.string.practice_tasks_limit) + limit;

        binding.tvParamsLimit.setText(desc);

    }

    private void checkLevelParamsDesc() {

        int checkedLevel = checkRequiredLevel();

        //Toast.makeText(activityContext, "Level: " +checkedLevel, Toast.LENGTH_SHORT).show();

        autoLevelValue = String.valueOf(checkedLevel);

        setTestLevel(checkedLevel);

        String level = getLevelParamText();

        binding.tvTestLevel.setText(level);
    }

    private void checkTopicsParamsDesc() {
        String topics = getString(R.string.studied_topcs_count) + sectionStudiedIds.size() + "/" + sectionCatIds.size();

        if (excludedCount > 0) topics += "     Исключено: " +excludedCount;

        binding.tvParamsTopics.setText(topics);
    }


    private String getLevelParamText() {

        String[] levelValues = activityContext.getResources().getStringArray(R.array.practice_level_array_values);
        String[] levelTitles = activityContext.getResources().getStringArray(R.array.practice_level_array);

        int valueIndexInTitles = -1;

        for (int i = 0; i < levelValues.length; i++) {
            String value = levelValues[i];
            String lv = String.valueOf(testLevel);

            if (lv.equals(value)) valueIndexInTitles = i;
        }

        String level = "";

        if (valueIndexInTitles >= 0 && valueIndexInTitles < levelValues.length) {
            level = getString(R.string.difficulty_level) + levelTitles[valueIndexInTitles];
        } else {
            binding.tvTestLevel.setVisibility(View.GONE);
        }


        return level;
    }

    private void setTestLevel(int checkedLevel) {

        boolean autoLevel = appSettings.getBoolean(PRACTICE_AUTOLEVEL_SETTING+sectionID, true);

        if (autoLevel) {
            testLevel = checkedLevel;
        } else {

            String customLevelSaved = appSettings.getString(PRACTICE_LEVEL_SETTING + sectionID, String.valueOf(testLevel));

            String[] levelValues = activityContext.getResources().getStringArray(R.array.practice_level_array_values);

            for (String value : levelValues) {

                if (customLevelSaved.equals(value)) {
                    testLevel = Integer.parseInt(value);
                }
            }
        }

    }

    private int checkRequiredLevel() {

        return dataManager.checkPracticeLevel(sectionIdsNoExcluded);


    }

    private void openPhraseTest() {
        openPracticeTest(TEST_PRACTICE + TEST_TYPE_PHRASES + sectionID,  1);
    }

    private void openAudioTest() {
        openPracticeTest(TEST_PRACTICE + TEST_TYPE_AUDIO + sectionID,  3);
    }

    public void openPracticeTest(String cat_id, int testType) {

        String title = getString(R.string.title_test_txt);

        String[] stringArray = sectionStudiedIds.toArray(new String[0]);

        Intent intent = new Intent(activityContext, ExerciseActivity.class);

        intent.putExtra(Constants.EXTRA_CAT_TAG, cat_id);

        intent.putExtra(Constants.EXTRA_SECTION_ID, sectionID);

        intent.putExtra("ex_type", testType);
        intent.putExtra("cat_title", title);
        intent.putExtra("practice", true);
        intent.putExtra("level", testLevel);

        intent.putExtra("ids", stringArray);
        intent.putExtra("unstudied_ids", sectionUnStudiedIds.toArray(new String[0]));

        intent.putParcelableArrayListExtra("dataItems", new ArrayList<DataItem>());

        startActivityForResult(intent, 1);

        openActivity.pageTransition();
    }

    private void openVocabularyTest() {

        String[] studiedArray = sectionStudiedWordsIds.toArray(new String[0]);
        String[] unStudiedArray = sectionUnStudiedWordsIds.toArray(new String[0]);

        if (studiedArray.length == 0 && unStudiedArray.length == 0) {
            studiedArray = sectionStudiedPhraseIds.toArray(new String[0]);
            unStudiedArray = sectionUnStudiedPhraseIds.toArray(new String[0]);
        }

        Intent i = new Intent(activityContext, ExerciseActivity.class);

        i.putExtra("ex_type", 1);

        i.putExtra(Constants.EXTRA_SECTION_ID, sectionID);

        i.putExtra(Constants.EXTRA_CAT_TAG, TEST_PRACTICE + TEST_TYPE_VOCAB + sectionID);

        i.putExtra("ids", studiedArray);

        i.putExtra("unstudied_ids", unStudiedArray);

        i.putParcelableArrayListExtra("dataItems", new ArrayList<>());

        startActivityForResult(i, 1);

        openActivity.pageTransition();
    }

    public void openBuildTest(int testType) {
        String title = getString(R.string.title_test_txt);


        String[] stringArray = sectionStudiedIds.toArray(new String[0]);

        Intent intent = new Intent(activityContext, ExerciseBuildActivity.class);

        intent.putExtra(Constants.EXTRA_CAT_TAG, TEST_PRACTICE + TEST_TYPE_BUILD + sectionID);

        intent.putExtra(Constants.EXTRA_SECTION_ID, sectionID);

        intent.putExtra("ex_type", testType);
        intent.putExtra("cat_title", title);
        intent.putExtra("practice", true);
        intent.putExtra("level", testLevel);

        intent.putExtra("ids", stringArray);

        intent.putExtra("unstudied_ids", sectionUnStudiedIds.toArray(new String[0]));

        intent.putParcelableArrayListExtra("dataItems", new ArrayList<DataItem>());

        startActivityForResult(intent, 1);

        openActivity.pageTransition();

    }

    private void getTestsData() {

        String[] testIds = new String[]{
                TEST_PRACTICE + TEST_TYPE_VOCAB + sectionID,
                TEST_PRACTICE + TEST_TYPE_PHRASES + sectionID,
                TEST_PRACTICE + TEST_TYPE_AUDIO + sectionID,
                TEST_PRACTICE + TEST_TYPE_BUILD + sectionID
        };


        ArrayList<String[]> testsData = dataManager.getPracticeTests(testIds);

        replaceResultDesc(testsData.get(0), binding.prVocabResult);
        replaceResultDesc(testsData.get(1), binding.prPhrasesResult);
        replaceResultDesc(testsData.get(2), binding.prAudioResult);
        replaceResultDesc(testsData.get(3), binding.prBuildResult);

    }


    private void replaceResultDesc(String[] params, TextView textView) {
        if (params.length > 1 && params[2].equals("replace")) {
            textView.setText(params[1]);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        getTestsData();

    }

    private void openPracticeParams() {

        if (paramDialogStatus == DIALOG_OPEN) return;

        PracticeParamsDialog paramsDialog = new PracticeParamsDialog(activityContext) {
            @Override
            public void practiceDialogCloseCallback() {
                paramsDialogClose();
            }
        };

        paramsDialog.setSectionId(sectionID);

        paramsDialog.setAutoLevelValue(autoLevelValue);

        paramsDialog.setSectionStudiedIds(sectionStudiedCats);
        paramsDialog.setSectionTopicsList(sectionCatsList);

        paramsDialog.showParams();

        paramDialogStatus = DIALOG_OPEN;

    }


    private void openPracticeParamsInfo() {

        HintDialog infoDialog = new HintDialog(activityContext);
        infoDialog.showCustomDialog(getString(R.string.practice_info_title), getString(R.string.practice_info_content));

    }


    private void paramsDialogClose() {

        getData();

        paramDialogStatus = DIALOG_CLOSED;
    }


}
