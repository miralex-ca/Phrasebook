package com.online.languages.study.lang.constructor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.practice.PracticeFragment;
import com.online.languages.study.lang.presentation.ThemedActivity;


public class ConstructorActivity extends ThemedActivity {


    ConstructorFragment taskFragment;
    FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Constructor");

        taskFragment = new ConstructorFragment();

        Bundle bundle = new Bundle();

        // bundle.putParcelable("structure", navStructure);

        taskFragment.setArguments(bundle);
        mFragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.content_fragment, taskFragment, "practice").commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // updateContent();

        super.onActivityResult(requestCode, resultCode, data);

        PracticeFragment fragment = (PracticeFragment) mFragmentManager.findFragmentByTag("practice");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}
