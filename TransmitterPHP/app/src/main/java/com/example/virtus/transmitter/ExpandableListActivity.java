package com.example.virtus.transmitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import Robot.Robot;

/**
 * Активити - вложенный список моих задач
 * @author Mihail Kovalenko
 */
public final class ExpandableListActivity extends AppCompatActivity {

    private static final String TEG_ExpandableListActivity = "TEG_ExpandableListActivity";
    private ExpandableListView expListView;
    private ExpandableListAdapter expListAdapter;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private HashMap<String, ArrayList<String>> expListDetail;
    private HashMap<String, ArrayList<Boolean>> expListFlag;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private String myLogin;
    private ListDate listDate;

    private void setActionBar(){

        try {

            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setTitle("Мои задачи");
            setSupportActionBar(toolbar);
            // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ex){
            Log.d(TEG_ExpandableListActivity, "Ошибка TEG_ExpandableListActivity ActionBar: " + ex.toString());
        }
    }

    /**
     * Получение моего логина
     */
    private void getDate(){
        try {
            Intent intent = getIntent();
            this.myLogin = intent.getStringExtra("myLogin");
            Log.d(TEG_ExpandableListActivity, "Данные с SecondMainActivity: " + this.myLogin);
        } catch (Exception ex){
            Log.d(TEG_ExpandableListActivity, "Ошибка: " + ex.toString());
        }
    }

    private void initialize(){

        // btnSendSubmTask = (Button) findViewById(R.id.btnSendSubmTask);
        expListView = (ExpandableListView) findViewById(R.id.expListView);
        expListDetail = new HashMap<>();
        expListFlag = new HashMap<>();
        /**
         * данные от сервера на основе объекта
         */
        try {
            listDate = new ListDate(myLogin, getApplicationContext());
        } catch (Exception ex){
            Log.d(TEG_ExpandableListActivity, "Ошибка ex = " + ex.toString());
        }
        listDate.setExpendableList(expListDetail, expListFlag);

        expListAdapter = new ListAdapter(this, listDate.getListThemJoin(), expListDetail, expListFlag, listDate.getListTask(), listDate.getListExec(), myLogin); // иниц. кастомного адаптера (списком ключей и всем списком)
        expListView.setAdapter(expListAdapter);
    }

    private void checkInternet(){
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (!Robot.isOnline(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "Нет соединения с интернетом.",Toast.LENGTH_SHORT).show();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_list);

        setActionBar();
        getDate();
        initialize();
        checkInternet();

        /**
         *  Выполнение действий при нажатии на title (при раскрытии списка)
         */
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(),
                  //      listDate.getListThemJoin().get(groupPosition) + " Список раскрыт.",
                    //    Toast.LENGTH_SHORT).show();
            }
        });
        /**
        *  Выполнение действий при нажатии на title (при скрытии списка)
        */
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

                //Toast.makeText(getApplicationContext(),
                  //      listDate.getListThemJoin().get(groupPosition) + " Список скрыт.",
                    //    Toast.LENGTH_SHORT).show();
            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Log.d(TEG_ExpandableListActivity, "setOnChildClickListener");

                Toast.makeText(getApplicationContext(),
                        listDate.getListThemJoin().get(groupPosition)
                                + " : " + expListDetail.get(listDate.getListThemJoin().get(groupPosition))
                                .get(childPosition), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
