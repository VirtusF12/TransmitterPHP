package com.example.virtus.transmitter;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentUser extends Fragment{

    private String login;
    private boolean flag;

    public void setFlag(boolean flag){
        this.flag = flag;
    }

    public void setLogin(String login){
        this.login = login;
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

        if (flag){
            TextView text = new TextView(context);
            SpannableString spanString = new SpannableString("        Участники группы");
            //spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            //spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            text.setText(spanString);
            text.setTextSize(20);
            text.setTextColor(Color.BLACK);
            layout.addView(text);
        }


        EditText et = new EditText(context);
        et.setText(this.login);
        et.setTextColor(Color.BLACK);
        et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(et);

        return layout;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
    }

}
