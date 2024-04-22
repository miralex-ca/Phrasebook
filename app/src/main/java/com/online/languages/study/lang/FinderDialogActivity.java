package com.online.languages.study.lang;


import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

public class FinderDialogActivity extends FragmentActivity {
    // activity used to customize FilePickerActivity styles

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View container = findViewById(R.id.container);

        container.setBackgroundColor(getResources().getColor(R.color.colorFinderWindowBg));

    }

}
