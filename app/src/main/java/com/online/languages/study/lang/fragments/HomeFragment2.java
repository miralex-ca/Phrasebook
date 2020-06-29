package com.online.languages.study.lang.fragments;



import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.util.IOUtils;
import com.online.languages.study.lang.CatActivity;
import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.MyCatEditActivity;
import com.online.languages.study.lang.NoteActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.EditDataListAdapter;
import com.online.languages.study.lang.adapters.EditUCatsListAdapter;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.RoundedTransformation;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.DataObject;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.HOME_TAB_ACTIVE;
import static com.online.languages.study.lang.Constants.PARAM_EMPTY;
import static com.online.languages.study.lang.Constants.PARAM_UCAT_PARENT;
import static com.online.languages.study.lang.Constants.SAVED_IMG_LINK;


public class HomeFragment2 extends Fragment   {


    private static int RESULT_LOAD_IMAGE = 20;
    SharedPreferences appSettings;

    View rootView;

    DataManager dataManager;
    EditUCatsListAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<DataObject> catsList;

    TextView counts;

    OpenActivity openActivity;

    Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.home_page_item, container, false);


        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());


        ImageView placePicutre = rootView.findViewById(R.id.catImage);

        View newCat = rootView.findViewById(R.id.newCatBtn);

        counts = rootView.findViewById(R.id.userCounts);

        dataManager = new DataManager(getActivity());
        openActivity = new OpenActivity(getActivity());

        newCat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openNewCat();
            }
        });



        placePicutre.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });



        String str = appSettings.getString(SAVED_IMG_LINK, "");


        Picasso.with( getActivity() )
                .load("file:" + str )
                .placeholder(R.drawable.account)
                .transform(new RoundedTransformation(0,0))
                .fit()
                .centerCrop()
                .into(placePicutre);

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

        catsList  = dataManager.getUcatsList();
        adapter = new EditUCatsListAdapter(getActivity(), catsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);


        checkCounts();

    }


    private void checkCounts() {

        int count = 0;

        for (DataObject cat: catsList) {
            count = count + cat.count;
        }

        String str = "Добавлено тем:  "+ catsList.size() +"\nДобавлено записей: "+count;

        counts.setText(str);

    }



    public void openMyCat(int position) {

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



    public void openNewCat( ) {
        Intent i = new Intent(getActivity(), MyCatEditActivity.class);
        i.putExtra(EXTRA_CAT_ID, "new");
        startActivityForResult(i, 10);
    }


    public void openCatEdit(int position) {

        Intent i = new Intent(getActivity(), MyCatEditActivity.class);
        i.putExtra(EXTRA_CAT_ID, catsList.get(position).id);
        startActivityForResult(i, 10);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

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

        saveImageLink( path + "/" +imgName+".jpeg");

        //Toast.makeText(getActivity(), "P: "+ path + "/" +imgName+".jpeg" , Toast.LENGTH_LONG).show();

        File imageFile = new File(path, imgName+".jpeg"); // Imagename.png
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








}
