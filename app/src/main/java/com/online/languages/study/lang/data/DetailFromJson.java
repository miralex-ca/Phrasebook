package com.online.languages.study.lang.data;

import android.content.Context;

import com.online.languages.study.lang.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

}
