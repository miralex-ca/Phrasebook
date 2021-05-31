package com.online.languages.study.lang.constructor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.online.languages.study.lang.Constants;
import com.online.languages.study.lang.ExerciseActivity;
import com.online.languages.study.lang.R;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.data.Section;
import com.online.languages.study.lang.databinding.FragmentConstructorBinding;
import com.online.languages.study.lang.databinding.FragmentPracticeBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class ConstructorFragment extends Fragment {


    private FragmentConstructorBinding binding;

    OpenActivity openActivity;

    Context activityContext;

    String CurrentString = "how much does it cost  cost 2 ?";


    public ConstructorFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentConstructorBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        activityContext = getActivity();

        openActivity = new OpenActivity(activityContext);
        openActivity.setOrientation();

        ArrayList<String> options = getOptions(CurrentString);

        for (int i = 0; i < options.size(); ++i) {
            View btn = getLayoutInflater().inflate(R.layout.tc_tv_template, null);
            TextView txtBtn = (TextView) btn.findViewById(R.id.txtOrig);
            txtBtn.setText(options.get(i));
            btn.setTag("t"+i);

            btn.setOnClickListener(this::click);

            binding.flow.addView(btn);

        }


        return view;

    }

    private ArrayList<String> getOptions(String string) {
        ArrayList<String> options = new ArrayList<>();
        String str = string.trim();
        str = str.replaceAll("[.,;?!]", "").replaceAll(" {2,}", " ");

        String[] separated = str.split(" ");
        Collections.addAll(options, separated);
        Collections.shuffle(options);
        return options;
    }



    public void click(View view) {

        // Toast.makeText(this, "Click ", Toast.LENGTH_SHORT).show();

        if  (view.getAlpha() == 1.0f) {

            TextView t = (TextView) view.findViewById(R.id.txtOrig);

            RelativeLayout tvBox = (RelativeLayout) getLayoutInflater().inflate(R.layout.tc_tv_target, null);

            ///////  text
            TextView tv = (TextView) tvBox.findViewById(R.id.txtT);
            tv.setText( t.getText().toString());
            ////////////

            tvBox.setAlpha(0.0f);

            Vibrator vibe = (Vibrator) activityContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(6);

            tvBox.setOnClickListener(this::clickTarget);

            binding.flowTarget.addView(tvBox);
            capitalizeString();


            tvBox.animate().alpha(1.0f);

            //Toast.makeText(this, "Tag: "+t.getTag(), Toast.LENGTH_SHORT).show();

            tvBox.setTag(view.getTag());

            view.animate()
                    .setDuration(200)
                    .alpha(0.0f);
        }

    }


    public void clickTarget(View view) {

        if  (view.getAlpha() == 1.0f) {

            View t = view;
            View tvr = binding.flow.findViewWithTag(t.getTag());


            final View tf = t;
            Runnable endAction = new Runnable() {
                public void run() {
                    targetEnd(tf);
                }
            };


            tvr.animate().alpha(1.0f);

            Vibrator vibe = (Vibrator) activityContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(6);

            t.animate()
                    .alpha(0.0f)
                    .setDuration(150)
                    .withEndAction(endAction);

        }
    }



    private void capitalizeString() {

        View v =  binding.flowTarget.getChildAt(1);

        if (v != null ) {
            TextView t = (TextView) v.findViewById(R.id.txtT);
            String str = t.getText().toString();
            String cap = str.substring(0, 1).toUpperCase() + str.substring(1);

            t.setText(cap);

            //Toast.makeText(this, "Text: "+ cap, Toast.LENGTH_SHORT).show();
        }


    }


    private void targetEnd(final View view) {
        view.getLayoutParams().height = 0;
        view.setLayoutParams(view.getLayoutParams());
        ResizeAnimation resizeAnimation = new ResizeAnimation(view, 0);
        resizeAnimation.setDuration(250);
        view.startAnimation(resizeAnimation);
        resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                //Functionality here
                ((ViewGroup) view.getParent()).removeView(view);
                capitalizeString();
            }
            public void onAnimationRepeat(Animation arg0) {}
            public void onAnimationStart(Animation arg0) {}
        });
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


    }



    public static class ResizeAnimation extends Animation {
        final int startWidth;
        final int targetWidth;
        View view;

        public ResizeAnimation(View view, int targetWidth) {
            this.view = view;
            this.targetWidth = targetWidth;
            startWidth = view.getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth = (int) (startWidth + (targetWidth - startWidth) * interpolatedTime);
            view.getLayoutParams().width = newWidth;
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



}
