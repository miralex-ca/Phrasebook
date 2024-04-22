package com.online.languages.study.lang.fragments;


import static android.app.Activity.RESULT_OK;
import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.Constants.PARAM_GROUP;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_PARENT;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_ROOT;
import static com.online.languages.study.lang.Constants.SAVED_IMG_LINK;
import static com.online.languages.study.lang.Constants.UCATS_UNPAID_LIMIT;
import static com.online.languages.study.lang.Constants.UCAT_WIDGET_LIMIT;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.EditUCatsListAdapter;
import com.online.languages.study.lang.adapters.IconPickerAdapter;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.RoundedTransformation;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;
import com.online.languages.study.lang.presentation.category.CatActivity;
import com.online.languages.study.lang.presentation.usercategories.MyCatEditActivity;
import com.online.languages.study.lang.presentation.usercategories.UCatsListActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class HomeFragment2 extends Fragment   {


    private static int RESULT_LOAD_IMAGE = 20;
    SharedPreferences appSettings;

    View rootView;

    DataManager dataManager;
    EditUCatsListAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<DataObject> catsList;

    TextView counts, itemsCount;

    OpenActivity openActivity;

    String[] icons;
    AlertDialog alert;

    View openMoreWrap;
    View newCat;

    View openPlusBtn;
    View emptyMsg;

    Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.home_page_item, container, false);


        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());


        newCat = rootView.findViewById(R.id.newCatBtn);

        counts = rootView.findViewById(R.id.userCounts);
        itemsCount = rootView.findViewById(R.id.itemsCounts);

        dataManager = new DataManager(getActivity());
        openActivity = new OpenActivity(getActivity());

        dataManager.plus_Version = dataManager.checkPlusVersion();


        newCat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openNewCat();
            }
        });


        View openUcatList = rootView.findViewById(R.id.extToList);
        openMoreWrap = rootView.findViewById(R.id.openMoreWrap);
        openPlusBtn = rootView.findViewById(R.id.openUnpaidWrap);

        View openMore = rootView.findViewById(R.id.openMore);

        emptyMsg = rootView.findViewById(R.id.empty_list_msg);

        openUcatList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openUcatList();
            }
        });

        openMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openUcatList();
            }
        });


        icons = getResources().getStringArray(R.array.icon_pics_list);
        showSavedIcon();


        View selectPic = rootView.findViewById(R.id.selectImg);


        selectPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                buildDialog();
            }
        });


        recyclerView = rootView.findViewById(R.id.recycler_view);



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openMyCat(position);
                    }
                }, 50);


            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));



        updateList();

        return rootView;
    }

    public void updateList() {

        catsList  = checkLimits( getList() );

        adapter = new EditUCatsListAdapter(getActivity(), catsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        checkCounts();

    }


    private ArrayList<DataObject> getList() {

        ArrayList<DataObject> list = dataManager.getUcatsList();

        if (!dataManager.plus_Version) {
            list = dataManager.getUcatsListForUnpaid("root");

        }

        return list;
    }


    private ArrayList<DataObject> checkLimits(ArrayList<DataObject> list) {

        int limit = UCAT_WIDGET_LIMIT;

        if (list.size() > limit) {
            list = new ArrayList<>(list.subList(0, limit));

            displayOpenMore(true);
        } else {
            displayOpenMore(false);
        }

        return  list;

    }



    private void displayOpenMore(boolean show) {

        if (show) {
            openMoreWrap.setVisibility(View.VISIBLE);
        } else {
            openMoreWrap.setVisibility(View.GONE);
        }


    }




    private void checkCounts() {

        String[] countsVaules = dataManager.getTotalCounts();

        counts.setText(String.format("%s%s", getString(R.string.user_topics_count), countsVaules[0]));
        itemsCount.setText(String.format("%s%s", getString(R.string.user_items_count), countsVaules[1]));


        manageEmptyMsg(Integer.parseInt(countsVaules[0]), emptyMsg);

        if (!dataManager.plus_Version) {
            manageUnpaidLimit(Integer.parseInt(countsVaules[0]));
        }

    }



    private void manageUnpaidLimit(int listSize) {

        if (listSize >= UCATS_UNPAID_LIMIT) {
            newCat.setVisibility(View.GONE);
            openPlusBtn.setVisibility(View.VISIBLE);
        } else {
            newCat.setVisibility(View.VISIBLE);
            openPlusBtn.setVisibility(View.GONE);
        }
    }

    private void manageEmptyMsg(int listSize, View text) {

        boolean display = catsList.size() == 0 && listSize == 0;

        if (display) {
            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.GONE);
        }
    }


    public void openMyCat(int position) {

        if (catsList.get(position).type.equals(PARAM_GROUP)) {
            openGroup(catsList.get(position));
            return;
        }

        if (catsList.get(position).count > 0 ) {

            String id = catsList.get(position).id;
            String title = catsList.get(position).title;

            Intent i = new Intent(getActivity(), CatActivity.class);

            i.putExtra(EXTRA_SECTION_ID, PARAM_UCAT_PARENT);
            i.putExtra(Constants.EXTRA_CAT_ID, id);
            i.putExtra("cat_title", title);
            i.putExtra(Constants.EXTRA_CAT_SPEC, PARAM_EMPTY);

            startActivityForResult(i, 10);

            openActivity.pageTransition();


        } else {

            openCatEdit(position);
        }

    }

    public void openGroup(DataObject dataObject) {

        Intent i = new Intent(getActivity(), UCatsListActivity.class);

        i.putExtra(EXTRA_CAT_ID, dataObject.id);

        startActivityForResult(i, 10);

        openActivity.pageTransition();
    }



    public void openNewCat( ) {
        Intent i = new Intent(getActivity(), MyCatEditActivity.class);
        i.putExtra(EXTRA_CAT_ID, "new");
        startActivityForResult(i, 10);
    }

    public void openUcatList( ) {

        Intent i = new Intent(getActivity(), UCatsListActivity.class);

        i.putExtra(EXTRA_CAT_ID, PARAM_UCAT_ROOT);

        startActivityForResult(i, 10);

        openActivity.pageTransition();
    }


    public void openCatEdit(int position) {

        Intent i = new Intent(getActivity(), MyCatEditActivity.class);
        i.putExtra(EXTRA_CAT_ID, catsList.get(position).id);
        startActivityForResult(i, 10);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //Toast.makeText(getActivity(), "Update: home 1" , Toast.LENGTH_SHORT).show();

        updateList();

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {


            /// Make sure we have permission for external files
            uri = data.getData();
            String selecteadImage = getRealPathFromURI(getActivity(), data.getData());
            // Toast.makeText(getActivity(), "Image path " + selecteadImage, Toast.LENGTH_LONG).show();


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                saveImageToExternal("icon", bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }


            ImageView placePicutre = rootView.findViewById(R.id.catImage);


            //String str = data.getData().toString();

            String str = appSettings.getString(SAVED_IMG_LINK, "");

            Picasso.with( getActivity() )
                    .load( "file:"+str )
                    .transform(new RoundedTransformation(0,0))
                    .fit()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .centerCrop()

                    .into(placePicutre);

        }

    }


    private void saveImageLink(String link) {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putString(SAVED_IMG_LINK, link);
        editor.apply();
    }



    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    public void saveImageToExternal(String imgName, Bitmap bm) throws IOException {


        //Creates app specific external folder
        // File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ "/endata");
        // path.mkdirs();

        //Creates app local folder
        ContextWrapper cw = new ContextWrapper(getActivity());
        File path = cw.getDir("images", Context.MODE_PRIVATE);

        saveImageLink( path + "/" +imgName+".jpg");

        //Toast.makeText(getActivity(), "P: "+ path + "/" +imgName+".jpeg" , Toast.LENGTH_LONG).show();

        File imageFile = new File(path, imgName+".jpg"); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try{
            bm.compress(Bitmap.CompressFormat.JPEG, 60, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(getActivity(),new String[] { imageFile.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch(Exception e) {
            throw new IOException();
        }
    }


    public interface ClickListener{
        void onClick(View view,int position);
        void onLongClick(View view,int position);
    }
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){
            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }



    public void buildDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View content = inflater.inflate(R.layout.picker_dialog, null);

        RecyclerView iconRecycler = content.findViewById(R.id.recycler_view);

        int picIndex = -1;

        IconPickerAdapter imgPickerAdapter = new IconPickerAdapter(getActivity(), icons, picIndex, HomeFragment2.this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);

        iconRecycler.setLayoutManager(mLayoutManager);

        iconRecycler.setAdapter(imgPickerAdapter);

        ViewCompat.setNestedScrollingEnabled(iconRecycler, false);

        dialog.setView(content);

        dialog.setNegativeButton(R.string.cancel_txt,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });



        alert = dialog.create();

        alert.show();






    }

    private static int dpToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }


    public void iconSelect(int position) {

        String icon = icons[position];

        alert.dismiss();

        if (position == (icons.length -1)) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);


        } else {
            saveImageLink( icon );
            showSavedIcon();
        }



    }


    private void showSavedIcon() {

        ImageView placePicutre = rootView.findViewById(R.id.catImage);

        String str = appSettings.getString(SAVED_IMG_LINK, "///android_asset/pics/cat/account.png" );


        Picasso.with( getActivity() )
                .load("file:" + str )
                //.placeholder(R.drawable.account)
                .transform(new RoundedTransformation(0,0))
                .fit()
                .centerCrop()
                .into(placePicutre);

    }








}
