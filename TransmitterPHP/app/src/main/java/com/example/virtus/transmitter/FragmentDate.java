package com.example.virtus.transmitter;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentDate extends Fragment {


    private static final String TEG_FragmentDate = "TEG_FragmentDate";

    private int year;
    private int month;
    private int day;

    private EditText et_from;
    private EditText et_to;

    private int curr_state = 1;


    public void setDate(String date){

        int num = curr_state;

        switch (num){

            case 0:
                et_from.setText(date);
                curr_state = 1;
                Log.d(TEG_FragmentDate, "редактирование 1-ой даты");
                ;break;
            case 1:
                et_to.setText(date);
                curr_state = 0;
                Log.d(TEG_FragmentDate, "редактирование 2-ой даты");
                ;break;
            default:
                Log.d(TEG_FragmentDate, "сработал default в setDate()");
                ;break;
        }
    }

    public void setYear(int year){
        this.year = year;
    }

    public void setMonth(int month){
        this.month = month;
    }

    public void setDay(int day){
        this.day = day;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context context = getActivity().getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.WHITE);

        TextView text = new TextView(context);
        SpannableString spanString = new SpannableString("                     Дата");
        //spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        //spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
        text.setText(spanString);
        text.setTextSize(20);
        text.setTextColor(Color.BLACK);
        layout.addView(text);

        et_from = new EditText(context);
        // et_from.setHint("Установите дату начала");

        et_from.setTextSize(16);
        et_from.setTextColor(Color.BLACK);
        SpannableString spanString_date_from = new SpannableString(this.day + ":" + this.month + ":" + this.year);
        //spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        //spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
        et_from.setText(spanString_date_from);
        et_from.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        et_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TEG_FragmentDate, "   вызван слушатель et_from");
            }
        });
        layout.addView(et_from);

        et_to = new EditText(context);
        et_to.setTextSize(16);
        et_to.setTextColor(Color.BLACK);
        // et_to.setHint("Установите дату окончания");
        SpannableString spanString_date_to = new SpannableString("");
        //spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        //spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
        et_to.setText(spanString_date_to);
        et_to.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        et_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TEG_FragmentDate, "   вызван слушатель et_to");
            }
        });
        layout.addView(et_to);

        return layout;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
    }

}
