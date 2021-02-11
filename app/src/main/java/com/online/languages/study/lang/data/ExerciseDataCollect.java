package com.online.languages.study.lang.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static com.online.languages.study.lang.Constants.DEFAULT_TEST_RELACE_CAT;
import static com.online.languages.study.lang.Constants.ITEM_FILTER_DIVIDER;
import static com.online.languages.study.lang.Constants.ITEM_TAG;
import static com.online.languages.study.lang.Constants.TAG_STRICT_FILTER;
import static com.online.languages.study.lang.Constants.TEST_CATS_MAX_FOR_BEST;
import static com.online.languages.study.lang.Constants.TEST_NEIGHBORS_RANGE;
import static com.online.languages.study.lang.Constants.TEST_OPTIONS_NUM;
import static com.online.languages.study.lang.Constants.UD_PREFIX;


public class ExerciseDataCollect {

    final ArrayList<DataItem> data;
    public ArrayList<ExerciseTask> tasks = new ArrayList<>();

    ArrayList<DataItem> dataTest = new ArrayList<>();

    private Context context;

    private DataFromJson dataFromJson;

    private  DBHelper dbHelper;

    ArrayList<DataItem> replaceList;

    private ArrayList<Section> sections;


    private String[] replaceCatIds = new String[]{DEFAULT_TEST_RELACE_CAT};

    public ArrayList<DataItem> optionsData = new ArrayList<>();
    public ArrayList<String> options = new ArrayList<>();

    ArrayList<OptionsCatData> optionsCatData = new ArrayList<>();


    ArrayList<String> sectionsIds = new ArrayList<>();
    ArrayList<ArrayList<DataItem>> optionsList = new ArrayList<>();


    private ArrayList<String> answersList = new ArrayList<>();

    int exType = 1;

    private int sectionTagSize;



    public ExerciseDataCollect(Context _context, ArrayList<DataItem> _data, int _exType) {
        context = _context;
        exType = _exType;

        sectionTagSize = context.getResources().getInteger(R.integer.section_id_length);

        dataFromJson = new DataFromJson(context);
        dbHelper = new DBHelper(context);

        data = new ArrayList<>(_data);


        String[] ids = context.getResources().getStringArray(R.array.test_replace_cats);

        if (ids.length > 0) replaceCatIds = ids;


        getCatIdsFromDataItems(data);

       // collect(data);

    }


    public void generateTasks(ArrayList<DataItem> dataItems) {
        tasks = new ArrayList<>();
        collect(dataItems);

    }


    public void shuffleTasks() {
        Collections.shuffle(tasks);
    }



    private void collect(ArrayList<DataItem> dataItemsList) {


        for (DataItem dataItem: dataItemsList) {

            ArrayList<DataItem> options =  getDataItemOptions(dataItem); // we're getting unique options

            tasks.add ( createTaskWithOption(dataItem, options) );
        }


/*

        if (dataItemsList.size() == 1) {

            ArrayList<DataItem> options =  getDataItemOptions( dataItemsList.get(0) );

            tasks.add ( createTaskWithOption(dataItemsList.get(0), options) );

            ArrayList<DataItem> dataItems = pickAddData(dataItemsList.get(0), dataItemsList);

            for (int i = 0; i < dataItems.size(); i++) {

                tasks.add ( createTaskWithOptions(dataItems.get(i), options, dataItemsList.get(0)) );

                if (i > 3) break;

            }


            //Toast.makeText(context, "One", Toast.LENGTH_SHORT).show();


        } else if (dataItemsList.size() == 2)
        {


            ArrayList<DataItem> options =  getDataItemOptions( dataItemsList.get(0) ); // options for task 1 and 2
            ArrayList<DataItem> dataItems = pickAddData(dataItemsList.get(0), dataItemsList);

            tasks.add ( createTaskWithOption(dataItemsList.get(0), options) );

            dataItemsList.add(dataItems.get(0));
            tasks.add ( createTaskWithOptions(dataItems.get(0), options, dataItemsList.get(0)) );


            options =  getDataItemOptions( dataItemsList.get(1) ); // options for task 3 and 4
            dataItems = pickAddData(dataItemsList.get(1), dataItemsList);

            tasks.add ( createTaskWithOption(dataItemsList.get(1), options) );
         if (dataItems.size()>0)   tasks.add ( createTaskWithOptions(dataItems.get(0), options, dataItemsList.get(1)) );



        } else if (dataItemsList.size() == 3) {

            Collections.shuffle(dataItemsList);

            ArrayList<DataItem> options =  getDataItemOptions( dataItemsList.get(0) ); // options for task 1 and 2
            ArrayList<DataItem> dataItems = pickAddData(dataItemsList.get(0), dataItemsList);

             tasks.add ( createTaskWithOption(dataItemsList.get(0), options) );

            if (dataItems.size()>0) tasks.add ( createTaskWithOptions(dataItems.get(0), options, dataItemsList.get(0)) );

            options =  getDataItemOptions( dataItemsList.get(1) ); // options for task 3
            tasks.add ( createTaskWithOption(dataItemsList.get(1), options) );

            options =  getDataItemOptions( dataItemsList.get(2) ); // options for task 4
            tasks.add ( createTaskWithOption(dataItemsList.get(2), options) );


        } else {

        }
*/

    }


    private ArrayList<DataItem> pickAddData (DataItem dataItem, ArrayList<DataItem> presentItems) {

        ArrayList<DataItem> addItems  = new ArrayList<>();


        DataItem addItem = new DataItem();

        ArrayList<DataItem> dataItems = getDataItemOptions(dataItem);

        Collections.shuffle(dataItems);


        for (DataItem dataItem1: dataItems) {

            Boolean oneOfThem = false;
            for (DataItem presentItem: presentItems) {
                if (presentItem.id.equals(dataItem1.id)) oneOfThem = true;

            }


            if ( !oneOfThem) {

                addItems.add(dataItem1);

                if ( addItems.size() >= 3 ) break;

            }
        }

        return addItems;

    }


    private ExerciseTask createTaskWithOptions (DataItem dataItem, ArrayList<DataItem> _optionsList, DataItem neededOption) {
        ArrayList<DataItem> options = new ArrayList<>();
        options.add(dataItem);
        options.add(neededOption);

        return createTask (dataItem,  _optionsList, options);
    }



    private ExerciseTask createTaskWithOption (DataItem dataItem, ArrayList<DataItem> _optionsList) {

        ArrayList<DataItem> options = new ArrayList<>();
        options.add(dataItem);


        return createTask (dataItem,  _optionsList, options);
    }



    private ExerciseTask createTask (DataItem dataItem, ArrayList<DataItem> _optionsList, ArrayList<DataItem> options) {

        // We get the required item with all info, list of possible options and preparatory options list

        Collections.shuffle(_optionsList);

        int optionsLength = TEST_OPTIONS_NUM - options.size();

        int possibleOptions = _optionsList.size();

        if (possibleOptions > TEST_OPTIONS_NUM ) optionsLength = TEST_OPTIONS_NUM - options.size();
        if (possibleOptions <= TEST_OPTIONS_NUM  && possibleOptions > 0) optionsLength = possibleOptions - options.size();



        for (int i = 0; i < optionsLength; i++) {

            DataItem option = getOption(dataItem, options, _optionsList);
            options.add(option);

        }


        ArrayList<String> optionsTxt = new ArrayList<>();


        for (DataItem dataItem1: options) {
            String txt = getTextByExType(dataItem1, 2);
            optionsTxt.add(txt);
        }

        int correctOptionIndex = 0;

        String quest = getTextByExType(dataItem, 1);

        String questInfo = dataItem.image;

        ExerciseTask exerciseTask = new ExerciseTask(quest, questInfo, optionsTxt, correctOptionIndex, dataItem.id);
        exerciseTask.data = dataItem;


        return exerciseTask ;

    }


    private String getValueForUnique(DataItem dataItem) {

        /// checking data for unique options

        String value = dataItem.info;

        if (exType == 2) {
            value = dataItem.item;
        }


        return sanitized(value);
    }

    private String sanitized(String value) {
        value = value.trim();
        value = value.toUpperCase().replaceAll("[,!.]", "");
        return value;
    }



    private String getTextByExType(DataItem dataItem, int taskDataType) {

        /// taskDataType: (case) 1 - quest, 2 - options
        /// exerciseType - general index for exercise type

        String txt = "";

        if (exType == 2) {

            switch (taskDataType) {  ///
                case 1: //
                    txt = dataItem.info;
                    break;
                case 2: // option text
                    txt = dataItem.item;
                    break;
            }

        } else {

            switch (taskDataType) {
                case 1:
                    txt = dataItem.item;
                    break;
                case 2: // option text
                    txt = dataItem.info;
                    break;
            }
        }


        return txt;

    }

    private DataItem getOption(DataItem dataItem, ArrayList<DataItem> options, ArrayList<DataItem> optionsList) {


        DataItem option = new DataItem();
         if (optionsList.size()> 0) option = optionsList.get(0);


        if (option.item.equals(dataItem.item)) {
           // option = optionsList.get(1);
        }

        //Toast.makeText(context, "Option: "+ dataItem.item +": - "+ option.item , Toast.LENGTH_SHORT).show();


        for (int i = 0; i< optionsList.size(); i++) {

            if ( ! checkOptions(optionsList.get(i), options ) ) {

                option = optionsList.get(i);

                break;
            }
        }

        return option;
    }


    private Boolean checkOptions(DataItem option, ArrayList<DataItem> optionsList) {

        boolean foundSame = false;


        for (int i=0; i<optionsList.size(); i++) {

            if (

              getValueForUnique(option).equals(getValueForUnique(optionsList.get(i)))

            ) {
                foundSame = true;

                break;
            }
        }

        return foundSame;
    }




    private void getCatIdsFromDataItems(ArrayList<DataItem> dataItems) { // TODO optimization


        ArrayList<String> ids = new ArrayList<>();

        optionsCatData = new ArrayList<>();

        HashSet<String> set = new HashSet<>();


        //// getting an empty list of options related to items categories
        for (DataItem item: dataItems) {

            String catId;

            if (item.id.contains(UD_PREFIX)) {

                catId = item.cat;


            } else {
                catId = item.id.substring(0, sectionTagSize);
            }

            if (!set.contains(catId)) {
                ids.add(catId);
                optionsCatData.add(new OptionsCatData(catId));
                set.add(catId);

            }
        }


        /// getting all items from that match for options by category id

        ArrayList<DataItem> items = dbHelper.getDataItemsByCatIds(ids);

        getReplacement();

        //// distributing received items into exercises items options

        for (DataItem item: items) {

            for (OptionsCatData optionsCat: optionsCatData) {

                //Toast.makeText(context, "Option: " + item.cat, Toast.LENGTH_SHORT).show();

                 if ( item.id.matches(optionsCat.id + ".*")  ||  item.cat.equals(optionsCat.id)) {

                     //Toast.makeText(context, "Matches ", Toast.LENGTH_SHORT).show();

                      optionsCat.options.add(item);

                      break;
                 }

            }

         }
    }


    private void getReplacement() {

        ArrayList<String> ids = new ArrayList<>(Arrays.asList(replaceCatIds));

        replaceList = dbHelper.getDataItemsByCatIds(ids);

        //Toast.makeText(context, "Len: "+ replaceList.size(), Toast.LENGTH_SHORT).show();

    }






    // getting options list from a category

    private ArrayList<DataItem> getDataItemOptions(DataItem dataItem) {


        ArrayList<DataItem> tOptions = new ArrayList<>();

        String selection = "base";

        for (OptionsCatData optionsCat: optionsCatData) {


            /// identifying the category of the item and category
            if (dataItem.id.matches(optionsCat.id + ".*") || dataItem.cat.equals(optionsCat.id)) {


                // checking all options to make sure they are unique
                tOptions = checkUniqueData( verifiedDiffer( optionsCat.options, dataItem ));
                String tagFilter = ITEM_FILTER_DIVIDER + ITEM_TAG;

                selection= "category";

                // check items and options for tag
               // String select = ""; String info = ""; boolean show = false;


                // filter options by tag
                if (dataItem.filter.contains(ITEM_TAG) ) {
                    String[] tags = getTagsFromFilter(dataItem.filter);

                    if (tags.length > 0) {
                        ArrayList<DataItem> taggedOptions = new ArrayList<>();
                        ArrayList<DataItem> untaggedOptions = new ArrayList<>();

                        String foundTag = "";

                        for ( DataItem tOption: tOptions) {

                            boolean optionHasTag = false;

                            for (String tag : tags) {

                                String checkTag = ITEM_TAG + tag;

                                if (tOption.filter.contains(checkTag)) {
                                    optionHasTag = true;
                                    foundTag = checkTag;
                                    break;
                                }
                            }

                            if (optionHasTag) taggedOptions.add(tOption);
                            else untaggedOptions.add(tOption);

                        }

                        // reducing all options to only tagged
                        if (taggedOptions.size() > 0 ) {

                            if (taggedOptions.size() < TEST_OPTIONS_NUM && !foundTag.contains(TAG_STRICT_FILTER)) {

                                Collections.shuffle(untaggedOptions);

                                // define the size of required items list
                                int required = TEST_OPTIONS_NUM - taggedOptions.size();

                                // reduce untagged to required count
                                if (untaggedOptions.size() > required)
                                    untaggedOptions = new ArrayList<>(untaggedOptions.subList(0, required));

                                // add options and finish selection
                                    taggedOptions.addAll(untaggedOptions);
                                    tOptions = new ArrayList<>(taggedOptions);
                                    selection = "tagged plus added";

                            } else {

                                if (taggedOptions.size() == 1 ) {
                                    int required = 2;
                                    if (untaggedOptions.size() > required)
                                        untaggedOptions = new ArrayList<>(untaggedOptions.subList(0, required));

                                    taggedOptions.addAll(untaggedOptions);

                                    tOptions = new ArrayList<>(taggedOptions);

                                    selection = "tagged restricted + 2";
                                } else {

                                    tOptions = new ArrayList<>(taggedOptions);
                                    selection = "tagged restricted";
                                }


                            }
                        }
                    }
                }


                // getting neighbor options
                if (tOptions.size() > TEST_NEIGHBORS_RANGE && optionsCatData.size() <= TEST_CATS_MAX_FOR_BEST) {
                    tOptions = getNeighborOptions(tOptions, dataItem.id);
                    selection = "neighbors";
                }


                /// add options from the collection
                if (tOptions.size() < 3 && data.size() > 1) {
                    if (!dataItem.filter.contains(TAG_STRICT_FILTER)) {
                        tOptions = addOption (dataItem);
                        selection = "test data";
                    }
                }

                if (tOptions.size() < 2) {
                    ArrayList<DataItem> replacement = getReplaceOptions(dataItem);
                    if ( replacement.size() > 0) tOptions = replacement;
                    selection = "replace";
                    //select = "replace";
                }

                break;
            }

        }

       // Toast.makeText(context, "Selection: " + selection, Toast.LENGTH_SHORT).show();

        //Log.d("TSelect", dataItem.item + ": " + selection);

        return tOptions;
    }


    private ArrayList<DataItem> addOption (DataItem dataItem) {

        ArrayList<DataItem> options = new ArrayList<>(data);

        return checkUniqueData( verifiedDiffer( options, dataItem) );
    }


    private  ArrayList<DataItem> getReplaceOptions(DataItem dataItem) {

        Collections.shuffle(replaceList);

        ArrayList<DataItem> candidates = new ArrayList<>(replaceList);

        candidates = checkUniqueData( verifiedDiffer( candidates, dataItem));

        ArrayList<DataItem> options = new ArrayList<>();

        for (int i = 0; i < candidates.size(); i ++) {

            if (i < 3) {

                options.add(candidates.get(i));
            }

        }

        return options;

    }


    private ArrayList<DataItem> checkUniqueData(ArrayList<DataItem> items) {

        ArrayList<DataItem> newData = new ArrayList<>();

        HashSet<String> set = new HashSet<>();


        for (DataItem item: items) {

            String checkedValue = getValueForUnique(item);

            if (!set.contains(checkedValue )) {

                newData.add(item);

                set.add(checkedValue);
            }
        }

        return newData;
    }



    private ArrayList<DataItem> getNeighborOptions (ArrayList<DataItem> options, String id) {


        /// define index of the item and form a list of indexes
        int target = -1;
        ArrayList<Integer> indexesArray = new ArrayList<>();


        for (int i = 0; i < options.size(); i ++ ) {
            if (options.get(i).id.equals(id)) target = i;
            indexesArray.add(i);
        }

        ArrayList<Integer> targetArray = new ArrayList<>();
        boolean toLeft = true;

        int range = TEST_NEIGHBORS_RANGE;

        for (int i = 0; i < range; i ++) {  /// getting neighbor indexes

            int targetIndex = getIndexByValue(indexesArray, target);

            if (indexesArray.size() < 2) break;

            if (toLeft)  {
                if ((targetIndex-1) > -1 && (targetIndex-1) < (indexesArray.size()) ) {
                    targetArray.add(indexesArray.get(targetIndex-1));
                    targetArray.add(indexesArray.remove(targetIndex-1));

                } else {
                    if ((targetIndex+1) > -1 && (targetIndex+1) < (indexesArray.size())) {
                        targetArray.add(indexesArray.get(targetIndex+1));
                        targetArray.add(indexesArray.remove(targetIndex+1));
                    }
                }
                toLeft = false;

            } else {
                if ((targetIndex+1) > -1 && (targetIndex+1) < (indexesArray.size())) {
                    targetArray.add(indexesArray.get(targetIndex+1));
                    targetArray.add(indexesArray.remove(targetIndex+1));

                } else {
                    if ((targetIndex-1) > -1 && (targetIndex-1) < (indexesArray.size())) {
                        targetArray.add(indexesArray.get(targetIndex-1));
                        targetArray.add(indexesArray.remove(targetIndex-1));
                    }
                }
                toLeft = true;
            }
        }

        ArrayList<DataItem> newOptions = new ArrayList<>();

        for (Integer index: targetArray) {
            newOptions.add(options.get(index));
        }


        newOptions = checkUniqueData(newOptions);

       // if (newOptions.size() < 1) Toast.makeText(context, "0: " + id, Toast.LENGTH_SHORT).show();

        return newOptions;

    }

    private int getIndexByValue(ArrayList<Integer> indexes, int value) {

        int index = -1;

        for (int i = 0; i < indexes.size(); i ++ ) {
            if ( indexes.get(i) == value) index = i;
        }

        return index;
    }


    private ArrayList<DataItem> verifiedDiffer(ArrayList<DataItem> dataItems, DataItem itemToVerify) {

        ArrayList<DataItem> newList = new ArrayList<>();

        for (DataItem dataItem: dataItems) {

            boolean similar = false;

            String valueVerify = sanitized(itemToVerify.info);
            if (itemToVerify.info.contains(";")) valueVerify = sanitized(itemToVerify.info.split(";")[0]);
            if (itemToVerify.info.contains("/")) valueVerify = sanitized(itemToVerify.info.split("/")[0]);

            String valueItem = sanitized(dataItem.info);
            if (dataItem.info.contains(";")) valueItem = sanitized(dataItem.info.split(";")[0]);
            if (dataItem.info.contains("/")) valueItem = sanitized(dataItem.info.split("/")[0]);

            boolean isEqualInfo = valueVerify.equals(valueItem);
            boolean isEqualItem = sanitized(itemToVerify.item).equals(sanitized(dataItem.item));

            boolean isEqualBase = sanitized(dataItem.base).length()>1 && itemToVerify.base.equals(dataItem.base);


            if ( isEqualBase || isEqualItem  || isEqualInfo ) {
                if (!itemToVerify.id.equals(dataItem.id))
                similar = true;
            }

            if (!similar) newList.add(dataItem);
        }

        //Toast.makeText(context, "Data: " + itemToVerify.item + " - " + newList.size(), Toast.LENGTH_SHORT).show();

        return newList;
    }


    public String[] getTagsFromFilter(String filterString) {

        String[] filters = filterString.split(ITEM_FILTER_DIVIDER);
        ArrayList<String> tags = new ArrayList<>();

        for (String filter: filters) {
            if (filter.contains(ITEM_TAG)) {
                String tag = filter.replace(ITEM_TAG, "");
                tags.add(tag);
            }
        }

        return tags.toArray(new String[0]);

    }





}
