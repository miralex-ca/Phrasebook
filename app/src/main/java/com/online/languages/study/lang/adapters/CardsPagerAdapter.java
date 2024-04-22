package com.online.languages.study.lang.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.online.languages.study.lang.R;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.presentation.CardsActivity;

import java.util.ArrayList;


public class CardsPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<DataItem> wordList = new ArrayList<>();

    private Boolean showTranslate;
    private Boolean mixWords;
    private Boolean showTranscript;
    private Boolean reverseData;
    private DataManager dataManager;

    int count = 4;
    private boolean speaking;


    public CardsPagerAdapter(Context _context, ArrayList<DataItem> words) {
        context = _context;
        wordList = words;
        dataManager = new DataManager(context);

        SharedPreferences appSettings = PreferenceManager.getDefaultSharedPreferences(context);
        speaking = appSettings.getBoolean("set_speak", true);

    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    public void setCount(int count) {
        if(count < 10){
            this.count = count;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.flashcard_item, container, false);

        DataItem wordData = wordList.get(position);
        showTranslate = CardsActivity.fShowTranslate;
        mixWords = CardsActivity.fMixWords;
        showTranscript = CardsActivity.fShowTranscript;
        reverseData = CardsActivity.fRevertData;


        LinearLayout content = itemView.findViewById(R.id.fCardContent);
        LinearLayout contentMirror = itemView.findViewById(R.id.fCardContentMirror);

        if (reverseData) {
            content.setVisibility(View.GONE);
            contentMirror.setVisibility(View.VISIBLE);
        } else {
            content.setVisibility(View.VISIBLE);
            contentMirror.setVisibility(View.GONE);
        }


        TextView text = itemView.findViewById(R.id.fCardText);
        TextView transcript = itemView.findViewById(R.id.fCardTrascript);

        RelativeLayout answerBox = itemView.findViewById(R.id.fAnswerBox);

        final TextView showMsg = itemView.findViewById(R.id.showMsg);
        final TextView answer = itemView.findViewById(R.id.fCardAnswer);

        TextView textMirror = itemView.findViewById(R.id.fCardTextMirror);
        final LinearLayout fTextMirrorBox  = itemView.findViewById(R.id.fTextMirrorBox);

        RelativeLayout answerBoxMirror = itemView.findViewById(R.id.fAnswerBoxMirror);
        final TextView showMsgMirror = itemView.findViewById(R.id.showMsgMirror);
        final TextView answerMirror = itemView.findViewById(R.id.fCardAnswerMirror);
        TextView transcriptMirror = itemView.findViewById(R.id.fCardTrascriptMirror);

        if (showTranslate){
            showMsg.setVisibility(View.GONE);
            answer.setVisibility(View.VISIBLE);

            showMsgMirror.setVisibility(View.GONE);
            fTextMirrorBox.setVisibility(View.VISIBLE);

        }

        String transcriptTxt = dataManager.getTranscriptFromData(wordData) ;

        if (context.getResources().getBoolean(R.bool.small_height)) transcriptTxt = "";

        if (transcriptTxt.equals("")) {

            transcript.setVisibility(View.GONE);
            transcriptMirror.setVisibility(View.GONE);

        } else {
            transcript.setVisibility(View.VISIBLE);
            transcriptMirror.setVisibility(View.VISIBLE);

            transcriptTxt = String.format("[ %s ]", transcriptTxt) ;
        }

        text.setText( wordData.item );
        text.setTextSize(itemTextSize(wordData.item));
        transcript.setText(transcriptTxt);


        int infoSize = textSize( wordData.info );
        answer.setText(wordData.info);
        answer.setTextSize( infoSize );


        View speakBtn = itemView.findViewById(R.id.speakBtn);

        View speakBtnMirror = itemView.findViewById(R.id.speakBtnMirror);

        if (speaking) {
            speakBtn.setVisibility(View.VISIBLE);
            speakBtnMirror.setVisibility(View.VISIBLE);
        } else {
            speakBtn.setVisibility(View.GONE);
            speakBtnMirror.setVisibility(View.GONE);
        }

        final String speakTxt = dataManager.getPronounce(wordData);


        speakBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             CardsActivity.speak(speakTxt);
                                         }
                                     }

        );

        speakBtnMirror.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CardsActivity.speak(speakTxt);
                                        }
                                    }

        );




        answerBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMsg.setVisibility(View.GONE);
                answer.setVisibility(View.VISIBLE);
            }
        }

        );


        textMirror.setText( wordData.item );
        textMirror.setTextSize(itemMirrorTextSize( wordData.item ));



        int infoSizeMirror = textSizeMirror( wordData.info );
        answerMirror.setText(wordData.info);
        answerMirror.setTextSize( infoSizeMirror );



        transcriptMirror.setText(transcriptTxt);

        answerBoxMirror.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                 showMsgMirror.setVisibility(View.GONE);
                  fTextMirrorBox.setVisibility(View.VISIBLE);

                  CardsActivity.speak(speakTxt);
              }
        }

        );


        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }



    private int itemTextSize(String text) {
        int textLength = text.length();
        int tSize = context.getResources().getInteger(R.integer.f_item_txt_size_norm);
        if ( textLength > 20) tSize = context.getResources().getInteger(R.integer.f_item_txt_size_medium);
        if ( textLength > 60) tSize = context.getResources().getInteger(R.integer.f_item_txt_size_small);
        if ( textLength > 75) tSize = context.getResources().getInteger(R.integer.f_item_txt_size_smallest);

        return tSize;
    }


    private int itemMirrorTextSize(String text) {
        int textLength = text.length();
        int tSize = context.getResources().getInteger(R.integer.f_item_txt_size_medium);

        if ( textLength > 30) tSize = context.getResources().getInteger(R.integer.f_item_txt_size_small);
        if ( textLength > 75) tSize = context.getResources().getInteger(R.integer.f_item_txt_size_smallest);

        return tSize;
    }


    private int textSize(String text) {
        int textLength = text.length();
        int tSize = context.getResources().getInteger(R.integer.f_info_txt_size_norm);

        if ( textLength > 20) tSize = context.getResources().getInteger(R.integer.f_info_txt_size_mid);
        if ( textLength > 40) tSize = context.getResources().getInteger(R.integer.f_info_txt_size_small);
        if ( textLength > 50 ) tSize = context.getResources().getInteger(R.integer.f_info_txt_size_smallest);
        return tSize;
    }

    private int textSizeMirror(String text) {
        int textLength = text.length();
        int tSize = context.getResources().getInteger(R.integer.f_info_txt_size_norm_opt);

        if ( textLength > 150) tSize = context.getResources().getInteger(R.integer.f_info_txt_size_mid_opt);
        if ( textLength > 200) tSize = context.getResources().getInteger(R.integer.f_info_txt_size_small);
        if ( textLength > 240 ) tSize = context.getResources().getInteger(R.integer.f_info_txt_size_smallest);

        return tSize;
    }



}
