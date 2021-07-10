package com.example.virtus.transmitter;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Фрагмент - создание задачи для доски
 * @author Mihail Kovalenko
 */
public class FragmentIneTask extends Fragment {

    private String textTask = "";
    private CheckBox cbDelete, cbTask;
    private EditText etTextTask;
    // private boolean flag;

   public boolean isPressFlag(){

        if (cbDelete.isChecked())
            return true;
        else return false;
    }

    /**
     * Получение текста задания
     * @return текста задания
     */
    public String getIneText(){
        return this.textTask;
    }

    /**
     * Установка текста задания
     * @param textTask параметр текста
     */
    public void setIneTask(String textTask){
        this.textTask = textTask;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getActivity().getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setBackgroundColor(Color.DKGRAY);
        // флаг удаления фрагмента
        cbDelete = new CheckBox(context);
        cbDelete.setText("");
        layout.addView(cbDelete);
        // текст для задачи
        etTextTask = new EditText(context);
        etTextTask.setText(this.textTask);
        etTextTask.setTextColor(Color.WHITE);
        layout.addView(etTextTask);

        return layout;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
    }
}

//    public void setFlag(boolean flag){
//        this.flag = flag;
//    }

// было в onCreateView

// cbTask = new CheckBox(context);
// layout.addView(cbTask);

//        if (flag){
//            TextView text = new TextView(context);
//            SpannableString spanString = new SpannableString("               Задания");
//            //spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
//            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
//            //spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
//            text.setText(spanString);
//            text.setTextSize(20);
//            text.setTextColor(Color.DKGRAY);
//            layout.addView(text);
//        }
