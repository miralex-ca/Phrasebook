package com.online.languages.study.lang.data;

import android.content.Context;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.DBHelper;
import com.online.languages.study.lang.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static com.online.languages.study.lang.Constants.EX_IMG_TYPE;
import static com.online.languages.study.lang.Constants.TEST_OPTIONS_RANGE;


public class ExerciseDataCollect {

    ArrayList<DataItem> data = new ArrayList<>();
    public ArrayList<ExerciseTask> tasks = new ArrayList<>();

    ArrayList<DataItem> dataTest = new ArrayList<>();

    private Context context;

    private DataFromJson dataFromJson;

    private  DBHelper dbHelper;

    private ArrayList<Section> sections;


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

        data = _data;
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


        if (dataItemsList.size() == 1) {

            ArrayList<DataItem> options =  getDataItemOptions( dataItemsList.get(0).id );

            tasks.add ( createTaskWithOption(dataItemsList.get(0), options) );

            ArrayList<DataItem> dataItems = pickAddData(dataItemsList.get(0), dataItemsList);

            for (int i = 0; i < dataItems.size(); i++) {

                tasks.add ( createTaskWithOptions(dataItems.get(i), options, dataItemsList.get(0)) );

                if (i > 3) break;

            }


            //Toast.makeText(context, "One", Toast.LENGTH_SHORT).show();


        } else if (dataItemsList.size() == 2) {


            ArrayList<DataItem> options =  getDataItemOptions( dataItemsList.get(0).id ); // options for task 1 and 2
            ArrayList<DataItem> dataItems = pickAddData(dataItemsList.get(0), dataItemsList);

            tasks.add ( createTaskWithOption(dataItemsList.get(0), options) );

            dataItemsList.add(dataItems.get(0));
            tasks.add ( createTaskWithOptions(dataItems.get(0), options, dataItemsList.get(0)) );


            options =  getDataItemOptions( dataItemsList.get(1).id ); // options for task 3 and 4
            dataItems = pickAddData(dataItemsList.get(1), dataItemsList);

            tasks.add ( createTaskWithOption(dataItemsList.get(1), options) );
         if (dataItems.size()>0)   tasks.add ( createTaskWithOptions(dataItems.get(0), options, dataItemsList.get(1)) );



        } else if (dataItemsList.size() == 3) {

            Collections.shuffle(dataItemsList);

            ArrayList<DataItem> options =  getDataItemOptions( dataItemsList.get(0).id ); // options for task 1 and 2
            ArrayList<DataItem> dataItems = pickAddData(dataItemsList.get(0), dataItemsList);

             tasks.add ( createTaskWithOption(dataItemsList.get(0), options) );

            if (dataItems.size()>0) tasks.add ( createTaskWithOptions(dataItems.get(0), options, dataItemsList.get(0)) );

            options =  getDataItemOptions( dataItemsList.get(1).id ); // options for task 3
            tasks.add ( createTaskWithOption(dataItemsList.get(1), options) );

            options =  getDataItemOptions( dataItemsList.get(2).id ); // options for task 4
            tasks.add ( createTaskWithOption(dataItemsList.get(2), options) );


        } else {

            for (DataItem dataItem: dataItemsList) {

                ArrayList<DataItem> options =  getDataItemOptions(dataItem.id);

                tasks.add ( createTaskWithOption(dataItem, options) );
            }

        }

    }


    private ArrayList<DataItem> pickAddData (DataItem dataItem, ArrayList<DataItem> presentItems) {

        ArrayList<DataItem> addItems  = new ArrayList<>();


        DataItem addItem = new DataItem();

        ArrayList<DataItem> dataItems = getDataItemOptions(dataItem.id);

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


        Collections.shuffle(_optionsList);

        int optionsLength = Constants.TEST_OPTIONS_NUM - options.size();


        for (int i = 0; i < optionsLength; i++) {
            DataItem option = getOption(dataItem, options, _optionsList);
            options.add(option);
        }

        ////// check for length


        Boolean haveLong = false;

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
        String value = dataItem.item;
        if (exType == 2) value = dataItem.info;
        if (exType == EX_IMG_TYPE) value = dataItem.item_info_1;
        return value;
    }



    private String getTextByExType(DataItem dataItem, int taskDataType) {

        /// data type 1 - quest, 2 - options

        String txt = "";

        if (exType == 2) {
            switch (taskDataType) {
                case 1:
                    txt = dataItem.info;
                    break;
                case 2:
                    txt = dataItem.item;
                    break;
            }
        } else if (exType == EX_IMG_TYPE) {

            switch (taskDataType) {
                case 1:
                    txt = dataItem.image;
                    break;
                case 2: // option text
                    txt = dataItem.item_info_1;
                    break;
            }

        } else {

            switch (taskDataType) {
                case 1:
                    txt = dataItem.item;
                    break;
                case 2:
                    txt = dataItem.info;
                    break;
            }
        }


        return txt;

    }

    private DataItem getOption(DataItem dataItem, ArrayList<DataItem> options, ArrayList<DataItem> optionsList) {

      //  Collections.shuffle(optionsList);

        //if (optionsList.size() < 1) Toast.makeText(context, "No item " + dataItem.item, Toast.LENGTH_SHORT ).show();

        DataItem option = optionsList.get(0);

        if (option.item.equals(dataItem.item)) {

          //  Toast.makeText(context, "Same: "+ option.item , Toast.LENGTH_SHORT).show();

            option = optionsList.get(1);
        }


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
             option.item.equals(optionsList.get(i).item)
            ||  option.info.equals(optionsList.get(i).info)
             ||  option.item_info_1.equals(optionsList.get(i).item_info_1)
            ) {
                foundSame = true;

                break;
            }
        }

        return foundSame;
    }




    private void getCatIdsFromDataItems(ArrayList<DataItem> dataItems) {


        ArrayList<String> ids = new ArrayList<>();

        optionsCatData = new ArrayList<>();

        HashSet<String> set = new HashSet<>();

        for (DataItem item: dataItems) {
            String catId = item.id.substring(0, sectionTagSize);
            if (!set.contains(catId)) {
                ids.add(catId);
                optionsCatData.add(new OptionsCatData(catId));
                set.add(catId);
            }
        }


        ArrayList<DataItem> items = dbHelper.getDataItemsByCatIds(ids);

            for (DataItem item: items) {

                for (OptionsCatData optionsCat: optionsCatData) {

                    if (item.id.matches(optionsCat.id + ".*")) {
                        optionsCat.options.add(item);
                        break;
                    }
                }
            }
    }



    private ArrayList<DataItem> getDataItemOptions(String id) {

        ArrayList<DataItem> tOptions = new ArrayList<>();

        for (OptionsCatData optionsCat: optionsCatData) {
            if (id.matches(optionsCat.id + ".*")) {

                tOptions = getNeighborOptions(optionsCat.options, id);

              //  if (tOptions.size() < 1) Toast.makeText(context, "0: " + id, Toast.LENGTH_SHORT).show();

                if (tOptions.size() < 4)  tOptions = checkUniqueData(optionsCat.options); // TODO improve to collect neighbors

               //

                break;
            }

        }

        return tOptions;
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

        int range = TEST_OPTIONS_RANGE;

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




}
