package com.online.languages.study.lang.data;

import android.content.Context;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.practice.QuestData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

import static com.online.languages.study.lang.Constants.TEST_OPTIONS_NUM;

public class DetailFromJson {

    Context context;
    public ArrayList<DetailItem> data = new ArrayList<>();
    public DetailItem detail;
    private String detailFile;


    public DetailFromJson(Context _context, String _itemId) {
        context = _context;
        detailFile = context.getString(R.string.app_detail_file);

        fillData(_itemId);
    }

    public DetailFromJson(Context _context) {
        context = _context;
        detailFile = context.getString(R.string.app_detail_file);
    }



    private void fillData(String id) {
        try {

            String fileName = detailFile;
            JSONObject obj = new JSONObject(loadJSONFromAsset(fileName));
            JSONArray mainNode = obj.getJSONArray("details");

            String desc = "not found";
            String image = "";
            String title = "";
            String img_ifo = "";

            Boolean found = false;
            for (int i = 0; i < mainNode.length(); i++) {
                JSONObject eachObject = mainNode.getJSONObject(i);

                if (eachObject.getString("id").equals(id) ) {
                    desc = eachObject.getString("desc");
                    image = eachObject.getString("image");
                    if (eachObject.has("title")) title = eachObject.getString("title");
                    if (eachObject.has("img_info")) img_ifo = eachObject.getString("img_info");
                    detail = new DetailItem(id, title, desc, image, img_ifo);
                    found = true;
                    break;
                }
            }

            if (!found) detail = new DetailItem(id, "not found", desc, image, img_ifo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public ArrayList<DetailItem> getAllData() {
        ArrayList<DetailItem> dataList = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(detailFile));
            JSONArray mainNode = obj.getJSONArray("details");

            for (int i = 0; i < mainNode.length(); i++) {
                JSONObject eachObject = mainNode.getJSONObject(i);

                dataList.add( getDataInfoFromJson(eachObject) );

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataList;
    }


    private DetailItem getDataInfoFromJson(JSONObject detailInfo) {
        DetailItem detailItem = new DetailItem();

        try {

            detailItem.id = detailInfo.getString("id");
            detailItem.title = detailInfo.getString("title");
            detailItem.desc = detailInfo.getString("desc");
            detailItem.image = detailInfo.getString("image");
            detailItem.img_info = detailInfo.getString("img_info");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return detailItem;
    }



    private String loadJSONFromAsset(String file_name) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(file_name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }



    public ArrayList<ExerciseTask> getTest(String requestedTestId) {

        ArrayList<ExerciseTask> exerciseTasks = new ArrayList<>();


        try {

            JSONObject obj = new JSONObject(loadJSONFromAsset(context.getString(R.string.tests_file)));

            JSONArray dataList = obj.getJSONArray("data");

            for (int i = 0; i < dataList.length(); i++) {

                /// get the test
                JSONObject testObject = dataList.getJSONObject(i);
                String testID = testObject.has("test") ? testObject.getString("test") : "";


                if (testID.equals(requestedTestId)) {

                    // parse test and get tasks list
                    JSONArray tasksList = testObject.getJSONArray("tasks");

                    for (int n = 0; n < tasksList.length(); n++) {

                        JSONObject taskObject = tasksList.getJSONObject(n);
                        ExerciseTask exerciseTask = new ExerciseTask();
                        exerciseTask.options = new ArrayList<>();
                        //// parse task object

                        exerciseTask.quest = taskObject.has("quest") ? taskObject.getString("quest") : "";
                        exerciseTask.data = new DataItem();
                        exerciseTask.data.item = exerciseTask.quest;
                        exerciseTask.data.pronounce = exerciseTask.quest;

                        //JSONArray options1 = taskObject.getJSONArray("options");

                        String optString = taskObject.getString("options");

                        String[] options = optString.split("\\|");

                        Collections.addAll(exerciseTask.options, options);
                        Collections.shuffle(exerciseTask.options);

                        int optNum = TEST_OPTIONS_NUM - 1;
                        if (exerciseTask.options.size() > optNum ) {
                            exerciseTask.options = new ArrayList<>(exerciseTask.options.subList(0, optNum));
                        }

                        exerciseTask.options.add(0, taskObject.getString("correct"));

                        exerciseTask.savedInfo = testID +"_" + n;

                        DataItem dataItem = new DataItem(exerciseTask.quest, exerciseTask.questInfo);
                        dataItem.id = exerciseTask.savedInfo;

                        exerciseTasks.add(exerciseTask);

                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return exerciseTasks;

    }

    public ArrayList<QuestData> getAllQuests() {

        ArrayList<QuestData> questData = new ArrayList<>();

        try {

            JSONObject obj = new JSONObject(loadJSONFromAsset(context.getString(R.string.tests_file)));

            JSONArray tasksList = obj.getJSONArray("data");

            for (int i = 0; i < tasksList.length(); i++) {

                JSONObject taskObject = tasksList.getJSONObject(i);

                String question = taskObject.has("quest") ? taskObject.getString("quest") : "";
                String correct = taskObject.has("correct") ? taskObject.getString("correct") : "";
                String options = taskObject.has("options") ? taskObject.getString("options") : "";
                String id = taskObject.has("id") ? taskObject.getString("id") : "";
                String categoryId = taskObject.has("cat_id") ? taskObject.getString("cat_id") : "";
                String level = taskObject.has("level") ? taskObject.getString("level") : "0";
                String levelGlobal = taskObject.has("level_g") ? taskObject.getString("level_g") : "0";
                String mode = taskObject.has("mode") ? taskObject.getString("mode") : "0";

                String task = taskObject.has("task") ? taskObject.getString("task") : "";

                String pronounce = taskObject.has("pronounce") ? taskObject.getString("pronounce") : "";
                String image = taskObject.has("image") ? taskObject.getString("image") : "";
                String filter = taskObject.has("filter") ? taskObject.getString("filter") : "";

                QuestData quest = new QuestData(question, correct, options, id, categoryId, level, task, pronounce, image, filter);
                quest.setLevelGlobal(Integer.parseInt(levelGlobal));
                quest.setMode(Integer.parseInt(mode));

                questData.add(quest);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return questData;



    }
}
