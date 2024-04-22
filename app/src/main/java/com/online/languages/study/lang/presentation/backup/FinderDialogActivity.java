package com.online.languages.study.lang.presentation.backup;


import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.online.languages.study.lang.R;

public class FinderDialogActivity extends FragmentActivity {
    // activity used to customize FilePickerActivity styles

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View container = findViewById(R.id.container);

        container.setBackgroundColor(getResources().getColor(R.color.colorFinderWindowBg));

    }

}
