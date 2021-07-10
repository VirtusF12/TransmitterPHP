package com.example.virtus.transmitter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import HttpRequest.*;
import PCodeDecodeMessage.CDMessage;

/**
 * Класс - адаптер вложенного списка
 * @author Mihail Kovalenko
 */
public class ListAdapter extends BaseExpandableListAdapter {

    private static final String TEG_ListAdapter = "TEG_ListAdapter";
    private Context context;
    private ArrayList<String> expListTitle;
    private HashMap<String, ArrayList<String>> expListDetail;
    private HashMap<String, ArrayList<Boolean>> expListFlag;
    private ArrayList<String> listTask, listExec;
    private HttpRequest httpRequest;
    private String myLogin;

    public ListAdapter(Context context, ArrayList<String> expListTitle,
                       HashMap<String, ArrayList<String>> expListDetail,
                       HashMap<String, ArrayList<Boolean>> expListFlag,
                       ArrayList<String> listTask,
                       ArrayList<String> listExec,
                       String myLogin) {

        this.context = context;
        this.expListTitle = expListTitle;
        this.expListDetail = expListDetail;
        this.expListFlag = expListFlag;

        this.listTask = listTask;
        this.listExec = listExec;
        this.myLogin = myLogin;
    }

    @Override
    public int getGroupCount() {
        return expListTitle.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return expListDetail.get(expListTitle.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return expListTitle.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {

        return expListDetail.get(expListTitle.get(i)).get(i1);
    }

    public Object getChild(int i, int i1, int state) {

        if (state == 1){

            return expListFlag.get(expListTitle.get(i)).get(i1);
        } else {

            return expListDetail.get(expListTitle.get(i)).get(i1);
        }
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        String listTitle = (String) getGroup(i);

        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) view.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return view;
    }


    CompoundButton.OnCheckedChangeListener myCheckChangeList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            String[] res = ((String) buttonView.getTag()).split(" ");

            int i1 = Integer.parseInt(res[0]);
            int i2 = Integer.parseInt(res[1]);
            Log.d(TEG_ListAdapter, "i1 = " + i1 + ", i2 = " + i2);
            String them = expListTitle.get(i1);
            Log.d(TEG_ListAdapter, "them = " + them);
            int start = 0, count = 0;

            for (int i = 0; i < listTask.size(); i++){

                if (listTask.get(i).equals("stop")){
                    count++;
                    if (count == i1){
                        i++;
                        start = i;
                        break;
                    }
                }
            }

            String text = listTask.get(start+i2);
            String exec = listExec.get(start+i2);
            Log.d(TEG_ListAdapter, "them = " + them + ", text = " + text + ", exec = " + exec);

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            String[] arr = them.split(" ");
            Log.d(TEG_ListAdapter, "arr.length = " + arr.length);
            Toast.makeText(context, "Подождите...", Toast.LENGTH_SHORT).show();

            if (exec.equals("0"))
                exec = "1";
            else
                exec = "0";
            String codeMess = CDMessage.CodeMessage(text);
            final String url = "http://195.133.146.149/?part=board_manage_task&action=update&author="+arr[0]+"&subject="+arr[1]+"&date="+arr[2]+"&user="+myLogin+"&taskUser="+codeMess+"&exec="+exec;
            Log.d(TEG_ListAdapter, "url = " + url);
            httpRequest = new HttpRequest(url, StateRequest.COMMON_REQUEST, context);
            httpRequest.execute();

            Toast.makeText(context, "Данные были сохранены на сервере", Toast.LENGTH_SHORT).show();
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        }
    };

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        String expListText = (String) getChild(i, i1);
        Boolean expListFlag = (Boolean) getChild(i, i1, 1);

        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.list_item, null);
        }

        CheckBox cbListAdapter = (CheckBox) view.findViewById(R.id.cbListAdapter);
        cbListAdapter.setChecked(expListFlag);
        cbListAdapter.setTag(i + " " + i1);
        cbListAdapter.setOnCheckedChangeListener(myCheckChangeList);

        TextView expListTextView = (TextView) view.findViewById(R.id.expandedListItem);
        expListTextView.setText(expListText);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
