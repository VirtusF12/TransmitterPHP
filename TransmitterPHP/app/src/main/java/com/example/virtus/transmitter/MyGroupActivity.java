package com.example.virtus.transmitter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import HttpRequest.*;
import Parser.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import Robot.*;

public class MyGroupActivity extends AppCompatActivity {

    private static final String TEG_MyGroupActivity= "TEG_MyGroupActivity";
    private static final int DIALOG_STATE_LIST = 1;
    private ListView lvMyG;
    private String loginAuthor;
    private ArrayList<String> list_myGroup;
    private CardArrayAdapter cardArrayAdapter;
    private HttpRequest httpRequest;

    private void setActionBar(){

        try {
            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setTitle("Мои группы");
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (Exception ex){
            // Log.d(TEG_GroupAllActivity, "Ошибка UserActivity ActionBar: " + ex.toString());
        }
    }

    // получение логина
    private void getDate(){
        try {
            Intent intent = getIntent();
            this.loginAuthor = intent.getStringExtra("login_u");
            //Log.d(TEG_TabTwoMyGroupActivity, "Логин: " + this.loginAuthor);
        } catch (Exception ex){
            //Log.d(TEG_TabTwoMyGroupActivity, "Ошибка при передаче логина с SecondMainActivity.");
        }
    }

    // инициализация списка групп
    private void initialize(){
        lvMyG = (ListView) findViewById(R.id.listViewMyGroup);
        list_myGroup = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        setActionBar();
        getDate();
        initialize();
        showListMyGroup();

        if (list_myGroup.size() == 0)
            showDialog(DIALOG_STATE_LIST);

        listenerCardAdapter(lvMyG);

//        lvMyG.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                TextView textView = (TextView) view;
//                String login_from = loginAuthor;
//                String name_group_to = textView.getText().toString();
//
//                Intent intentMessageGroupActivity = new Intent(getApplicationContext(), MessageGroupActivity.class);
//                intentMessageGroupActivity.putExtra("login_from", login_from);
//                intentMessageGroupActivity.putExtra("name_group_to", name_group_to);
//                startActivity(intentMessageGroupActivity);
//            }
//        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d(TEG_TabTwoMyGroupActivity, "Вызван onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d(TEG_TabTwoMyGroupActivity, "Вызван onResume()");
        showListMyGroup();

        if (list_myGroup.size() == 0)
            showDialog(DIALOG_STATE_LIST);
    }

    private void showListMyGroup(){

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=group_user&action=get&detalAction=getListMyGroups&login=test";
        httpRequest = new HttpRequest(url, StateRequest.COMMON_REQUEST, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        list_myGroup = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getList();
        Log.d(TEG_MyGroupActivity,  "list_myGroup.size() = " + list_myGroup.size());
        /*
        final String urlGetMyGroup = "http://62.109.19.148:5000/getListMyGroupsNew_N/?login=\"" + loginAuthor + "\"" ;
        sListMyGroup = new SListMyGroup(urlGetMyGroup, list_myGroup);
        sListMyGroup.start();
        try {
            sListMyGroup.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        /////////////////////////////////////////////////////////////////////////////
        // новое
        cardArrayAdapter = new CardArrayAdapter(getApplicationContext(), R.layout.list_item_card);

        for (int i = 0; i < list_myGroup.size(); i++) {
            Card card = new Card(list_myGroup.get(i));
            cardArrayAdapter.add(card);
        }
        lvMyG.setAdapter(cardArrayAdapter);
        // старое
//        adapter = new ArrayAdapter<>(this, R.layout.my_list_item, list_myGroup);
//        /////adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list_myGroup);
//        adapter.notifyDataSetChanged();
//        lvMyG.setAdapter(adapter);
//        /////Log.d(TEG_TabTwoMyGroupActivity, "Окончил работу showListMyGroup()");
    }

    // новая релизация
    private void listenerCardAdapter(ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int position = i - 1;
                Card card = cardArrayAdapter.getItem(position);
//                String nameChanel = card.getLine1();
//                String[] names = nameChanel.split(" ");
//                String nameGroup = names[0];

                String login_from = loginAuthor;
                String name_group_to = card.getLine1();

                Intent intentMessageGroupActivity = new Intent(getApplicationContext(), MessageGroupActivity.class);
                intentMessageGroupActivity.putExtra("login_from", login_from);
                intentMessageGroupActivity.putExtra("name_group_to", name_group_to);
                startActivity(intentMessageGroupActivity);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG_STATE_LIST){

            AlertDialog.Builder adbInfo = new AlertDialog.Builder(this);
            adbInfo.setTitle("Список Ваших групп пуст!");
            StringBuilder sb = new StringBuilder();
            sb.append("Выберите один из двух вариантов: \r\n");
            sb.append("1. Создайте свою группу \r\n");
            sb.append("2. Перейдите к списку всех групп");
            adbInfo.setMessage(sb.toString());
            adbInfo.setIcon(android.R.drawable.ic_dialog_info);
            // кнопка положительного ответа
            adbInfo.setPositiveButton("Ok", myClickListenerInfo);

            return adbInfo.create();
        }

        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListenerInfo = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    //finish();
                    break;
//                // негативная кнопка
//                case Dialog.BUTTON_NEGATIVE:
//                    finish();
//                    break;
//                // нейтральная кнопка
//                case Dialog.BUTTON_NEUTRAL:
//                    break;
            }
        }
    };
}
