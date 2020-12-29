package com.online.languages.study.lang.data;

import android.content.Context;

import com.online.languages.study.lang.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static com.online.languages.study.lang.Constants.DATA_MODE_DEFAULT;
import static com.online.languages.study.lang.Constants.SET_DATA_LEVELS_DEFAULT;

public class DataFromJson {

    Context context;
    public ArrayList<DataItem> data = new ArrayList<>();

    private String categoryFile;

    private String navStructureFile;
    private String dataNode;

    public DataFromJson(Context _context) {
        context = _context;
        categoryFile = context.getString(R.string.app_data_file);

        navStructureFile = context.getString(R.string.app_structure_file);

        dataNode = "data";

    }


    public ArrayList<Section> getSectionsList() {
        ArrayList<Section> dataList = new ArrayList<>();

        try {

            JSONArray sections = new JSONArray(loadJSONFromAsset(navStructureFile));

            for (int i = 0; i < sections.length(); i++) {
                JSONObject sectionObject = sections.getJSONObject(i);
                dataList.add( getSectionInfoFromJson(sectionObject) );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public Map<String, Boolean> getParams() {

        Map<String, Boolean> paramsList = new HashMap<>();

        try {

            JSONObject structure = new JSONObject(loadJSONFromAsset(navStructureFile));
            JSONObject params = structure.getJSONObject("params");

            boolean simplified = false;
            boolean homecards = false;
            boolean gallery = false;
            boolean stats = true;
            boolean dataLevels = SET_DATA_LEVELS_DEFAULT;


            if (params.has("simplified")) simplified = params.getBoolean("simplified");
            if (params.has("homecards")) homecards = params.getBoolean("homecards");
            if (params.has("gallery")) gallery = params.getBoolean("gallery");
            if (params.has("stats")) stats = params.getBoolean("stats");
            if (params.has("dataLevels")) dataLevels = params.getBoolean("dataLevels");

            paramsList.put("simplified", simplified);
            paramsList.put("homecards", homecards);
            paramsList.put("gallery", gallery);
            paramsList.put("stats", stats);
            paramsList.put("dataLevels", dataLevels);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paramsList;

    }




    public NavStructure getStructure() {

        NavStructure navStructure = new NavStructure(context);

        try {

            JSONObject structure = new JSONObject(loadJSONFromAsset(navStructureFile));

            JSONArray sections = structure.getJSONArray("sections");

            for (int i = 0; i < sections.length(); i++) {
                JSONObject sectionObject = sections.getJSONObject(i);
                navStructure.sections.add( getNavSectionInfoFromJson( sectionObject ) );

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return navStructure;
    }


    public ArrayList<NavCategory> getAllUniqueCats() {

        ArrayList<NavCategory> uniqueCats = new ArrayList<>();

        try {

            JSONObject structure = new JSONObject(loadJSONFromAsset(navStructureFile));
            JSONArray sections = structure.getJSONArray("sections");
            uniqueCats = getUniqueCatsFromJson(sections);


        } catch (JSONException e) {
            e.printStackTrace();
        }



        return uniqueCats;
    }


    private ArrayList<NavCategory> getUniqueCatsFromJson(JSONArray sections) {

        ArrayList<NavCategory> uniqueCats = new ArrayList<>();
        HashSet<String> set = new HashSet<>();

        try {

        for (int i = 0; i < sections.length(); i++) {
            JSONObject sectionObject = sections.getJSONObject(i);

            JSONArray categories = sectionObject.getJSONArray("categories");


            for (int n = 0; n < categories.length(); n++) {

                JSONObject category = categories.getJSONObject(n);

                NavCategory cat = new NavCategory();

                cat.type = category.getString("type");
                cat.id = category.getString("id");
                cat.title = category.getString("title");

                cat.review = !category.has("review") || !category.getString("review").equals("false");


                if (!cat.type.equals("group")) {
                    if (!set.contains(cat.id)) {
                        uniqueCats.add(cat);
                        set.add(cat.id);
                    }
                }
            }


        }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return uniqueCats;
    }



    private NavSection getNavSectionInfoFromJson(JSONObject itemInfo) {

        NavSection section = new NavSection();

        try {

            section.title = itemInfo.getString("title");
            section.desc = itemInfo.getString("desc");
            section.title_short = itemInfo.getString("title_short");
            section.desc_short = itemInfo.getString("desc_short");
            section.id = itemInfo.getString("id");
            section.image = itemInfo.getString("image");
            section.unlocked = !itemInfo.has("unlocked") || !itemInfo.getString("unlocked").equals("false");

            if (itemInfo.has("spec"))   section.spec = itemInfo.getString("spec");
            else   section.spec = "";


            if (itemInfo.has("type"))  section.type = itemInfo.getString("type");
            else  section.type = "";


            if (itemInfo.has("categories")) {

                JSONArray categories = itemInfo.getJSONArray("categories");
                HashSet<String> set = new HashSet<>();
                for (int i = 0; i < categories.length(); i++) {

                    JSONObject category = categories.getJSONObject(i);

                    NavCategory cat = new NavCategory();

                    cat.parent = category.getString("parent");
                    cat.type = category.getString("type");
                    cat.id = category.getString("id");
                    cat.title = category.getString("title");

                    cat.review = !category.has("review") || !category.getString("review").equals("false");

                    section.navCategories.add(cat);

                    cat.unlocked = section.unlocked;

                    if (category.has("desc")) cat.desc = category.getString("desc");
                    else cat.desc = "";

                    if (category.has("spec")) cat.spec = category.getString("spec");
                    else cat.spec = "";

                    if (category.has("image")) cat.image = category.getString("image");
                    else cat.image = "";

                    if (category.has("param")) cat.param = category.getString("param");
                    else cat.param = "";

                    if (!cat.type.equals("group") && !cat.type.equals("set") && !cat.type.equals("page")) {
                        if (!set.contains(cat.id)) {
                            section.uniqueCategories.add(cat);
                            section.catIdList.add(cat.id);
                            set.add(cat.id);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return section;
    }



    private Section getSectionInfoFromJson(JSONObject itemInfo) {
        Section section = new Section();

        try {
            section.title = itemInfo.getString("title");
            section.desc = itemInfo.getString("desc");
            section.title_short = itemInfo.getString("title_short");
            section.desc_short = itemInfo.getString("desc_short");
            section.id = itemInfo.getString("id");
            section.tag = itemInfo.getString("tag");
            section.image = itemInfo.getString("image");

            if (itemInfo.has("categories")) {

                JSONArray categories = itemInfo.getJSONArray("categories");

                for (int i = 0; i < categories.length(); i++) {

                    JSONObject category = categories.getJSONObject(i);

                    section.categories.add(

                            new Category( category.getString("id"), category.getString("title"))) ;

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return section;
    }


    ArrayList<DataItem> getCatDataByTag(String cat) {

        ArrayList<DataItem> dataList = new ArrayList<>();
        String file = categoryFile;

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(categoryFile));

            JSONArray mainNode = obj.getJSONArray(dataNode);


            for (int i = 0; i < mainNode.length(); i++) {
                JSONObject eachObject = mainNode.getJSONObject(i);

                String id = eachObject.getString("id");

                if (id.matches(cat+".*") ) {

                    dataList.add( getDataInfoFromJson(eachObject) );
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public DataItem getDataItemById(String id) {

        DataItem dataItem = new DataItem();

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(categoryFile));

            JSONArray mainNode = obj.getJSONArray(dataNode);


            for (int i = 0; i < mainNode.length(); i++) {

                JSONObject eachObject = mainNode.getJSONObject(i);

                String tId = eachObject.getString("id");

                if (id.equals(tId)) {
                    dataItem =  getDataInfoFromJson(eachObject);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataItem;
    }


    private DataItem getDataInfoFromJson(JSONObject itemInfo) {
        DataItem dataItem = new DataItem();

        try {

            dataItem.id = itemInfo.getString("id");
            dataItem.item = itemInfo.getString("item");
            dataItem.info = itemInfo.getString("info");

            if (itemInfo.has("divider")) {
                dataItem.divider =  itemInfo.getString("divider");
            } else {
                dataItem.divider =  "no";
            }

            if (itemInfo.has("filter")) {
                dataItem.filter =  itemInfo.getString("filter");
            } else {
                dataItem.filter =  "";
            }

            if (itemInfo.has("image")) {
                if (itemInfo.getString("image").equals("none")) {
                    dataItem.image = "none";
                } else {
                    dataItem.image = itemInfo.getString("image");
                }
            } else {
                dataItem.image = dataItem.id+".jpg";
            }

            if (itemInfo.has("item_info_1")) {
                dataItem.item_info_1 = itemInfo.getString("item_info_1");
            } else {
                dataItem.item_info_1 = "";
            }

            if (itemInfo.has("pronounce")) {
                dataItem.pronounce = itemInfo.getString("pronounce");
            } else {
                dataItem.pronounce = dataItem.item;
            }

            if (itemInfo.has("grammar")) {
                dataItem.grammar = itemInfo.getString("grammar");
            } else {
                dataItem.grammar = "";
            }

            if (itemInfo.has("base")) {
                dataItem.base = itemInfo.getString("base");
            } else {
                dataItem.base = "";
            }

            if (itemInfo.has("ipa")) {
                dataItem.trans1 =  itemInfo.getString("ipa");
            } else {
                dataItem.trans1 =  "";
            }

            if (itemInfo.has("tr_ru")) {
                dataItem.trans2 =  itemInfo.getString("tr_ru");
            } else {
                dataItem.trans2 =  "";
            }


            if (itemInfo.has("mode")) {
                dataItem.mode = Integer.parseInt(itemInfo.getString("mode"));
            } else {
                dataItem.mode = DATA_MODE_DEFAULT;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataItem;
    }

    public ArrayList<DataItem> getAllData() {
        ArrayList<DataItem> dataList = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(categoryFile));
            JSONArray mainNode = obj.getJSONArray(dataNode);

            for (int i = 0; i < mainNode.length(); i++) {
                JSONObject eachObject = mainNode.getJSONObject(i);
                dataList.add( getDataInfoFromJson(eachObject) );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataList;
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



    public ArrayList<DataItem> getAllItemsList() {
        return getListFromFile(categoryFile);
    }

    public ArrayList<DataItem> getItemsFromAllFiles() {

        ArrayList<DataItem> items = new ArrayList<>();

        String[] myArrayList = context.getResources().getStringArray(R.array.data_files);


        for (String file: myArrayList) {
            ArrayList<DataItem> itemArrayList  = getListFromFile(file);
            items.addAll(itemArrayList);
        }

       // Toast.makeText(context, "Items: " + items.size(), Toast.LENGTH_SHORT).show();


        return items;
    }


    private ArrayList<DataItem> getListFromFile(String file) {

        ArrayList<DataItem> allItems = new ArrayList<>();

        try {
            JSONObject categories = new JSONObject(loadJSONFromAsset(file));


            Iterator<String> iter = categories.keys();

            while (iter.hasNext()) {
                String category = iter.next();

                JSONArray catWords = categories.getJSONArray(category);



                for (int i = 0; i < catWords.length(); i ++) {
                    JSONObject tWord = catWords.getJSONObject(i);

                    String idData = tWord.getString("id");

                    DataItem w = new DataItem();
                    w.id = idData;
                    allItems.add( w );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allItems;
    }


}
