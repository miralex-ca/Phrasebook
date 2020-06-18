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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.common.util.IOUtils;
import com.online.languages.study.lang.MyCatEditActivity;
import com.online.languages.study.lang.NoteActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.RoundedTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;
import static com.online.languages.study.lang.Constants.EXTRA_NOTE_ID;
import static com.online.languages.study.lang.Constants.HOME_TAB_ACTIVE;
import static com.online.languages.study.lang.Constants.SAVED_IMG_LINK;


public class HomeFragment2 extends Fragment   {


    private static int RESULT_LOAD_IMAGE = 1;
    SharedPreferences appSettings;

    View rootView;

    Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.home_page_item, container, false);


        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());


        ImageView placePicutre = rootView.findViewById(R.id.catImage);

        View newCat = rootView.findViewById(R.id.newCatBtn);



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


        return rootView;
    }



    public void openNewCat( ) {
        Intent i = new Intent(getActivity(), MyCatEditActivity.class);
        startActivityForResult(i, 10);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
















}
