package com.online.languages.study.lang.adapters;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    Context context;

        public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
            super();
            this.context = context;
        }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        //child -> Floating Action Button
        if (dyConsumed > 0) {
            child.hide();
        } else if (dyUnconsumed > 0) {
            child.show();
            Toast.makeText(context, "H" + dyUnconsumed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}