package com.online.languages.study.studymaster.adapters;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;


public class ResizeHeight extends Animation {

    private final int startHeight;
    private final int targetHeight;
    View view;

    public ResizeHeight(View view, int targetHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
        startHeight = view.getHeight();
    }

    public ResizeHeight(View view, int targetHeight, int _startHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
        startHeight = _startHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
