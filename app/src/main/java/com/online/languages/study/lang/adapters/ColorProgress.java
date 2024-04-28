package com.online.languages.study.lang.adapters;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.ColorInt;
import android.util.TypedValue;
import android.widget.TextView;

import com.online.languages.study.lang.R;


public class ColorProgress {
    Context context;

    private final int[] progressColors = {
            R.attr.colorMainText,
            R.attr.colorVeryBadText,
            R.attr.colorBadText,
            R.attr.colorGoodText,
            R.attr.colorVeryGoodText,
            R.attr.colorGreatText
    };

    private final int[] progressColorsTxt = {
            R.attr.colorMainText,
            R.attr.colorVeryBadTextCat,
            R.attr.colorBadTextCat,
            R.attr.colorGoodTextCat,
            R.attr.colorVeryGoodTextCat,
            R.attr.colorGreatTextCat
    };


    private final int[] statusColors = {
            R.attr.colorUnknown,
            R.attr.colorKnown,
            R.attr.colorStudied
    };

    private final int[] colorStatus = {
            R.attr.colorStatusUnknown,
            R.attr.colorStatusKnown,
            R.attr.colorStatusStudied
    };


    public int[] statusBg = {
            R.drawable.text_round_bg,
            R.drawable.text_round_blue_bg,
            R.drawable.text_round_green_bg
    };




    public ColorProgress() {

    }

    public ColorProgress(Context _context) {
        context = _context;
    }

    public int getColorFromAttr(int result) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute( defineColorByResult(result), typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }


    private int defineColorByResult(int result) {
        int color;

        if (result < 1) {
            color = progressColors[0];
        } else if (result > 0 && result < 30) {
            color = progressColors[1];
        } else if (result > 29 && result < 50) {
            color = progressColors[2];
        } else if (result > 49 && result < 80) {
            color = progressColors[3];
        } else if (result > 79 && result < 96) {
            color = progressColors[4];
        } else {
            color = progressColors[5];
        }
        return color;
    }


    public int setCatColorFromAttr(int result) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute( defineCatColorByResult(result), typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }


    private int defineCatColorByResult(int result) {
        int color;

        if (result < 1) {
            color = progressColorsTxt[0];
        } else if (result > 0 && result < 30) {
            color = progressColorsTxt[1];
        } else if (result > 29 && result < 50) {
            color = progressColorsTxt[2];
        } else if (result > 49 && result < 80) {
            color = progressColorsTxt[3];
        } else if (result > 79 && result < 96) {
            color = progressColorsTxt[4];
        } else {
            color = progressColorsTxt[5];
        }
        return color;
    }


    public int getStatusColorFromAttr(int result) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute( getStatusColorByResult(result), typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }

    private int getStatusColorByResult(int result) {
        int color = colorStatus[0];
        if (result > 0) color = colorStatus[1];
        if (result > 2) color = colorStatus[2];
        return color;
    }



    public int setStatusColorFromAttr(int result) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute( defineStatusColorByResult(result), typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }


    public void setStatusColors(TextView textView, int result) {
        textView.setTextColor(  setStatusColorFromAttr(result) );
        //textView.setBackgroundResource(defineStatusBGByResult(result));

    }

    private int defineStatusColorByResult(int result) {
        int color = statusColors[0];
        if (result > 0) color = statusColors[1];
        if (result > 2) color = statusColors[2];
        return color;
    }

    public int defineStatusBGByResult(int result) {
        int bg = statusBg[0];

        if (result > 2) {
            bg = statusBg[2];
        } else if (result > 0) {
            bg = statusBg[1];
        }
        return bg;
    }







}
