package com.example.virtus.transmitter;

import HttpRequest.*;
import Parser.*;
import Robot.VAR;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public final class TabTwoMyGroupActivity extends Activity {

    private static final String TEG_TabTwoMyGroupActivity = "TEG_TabTwoMyGroupActivity";
    private static final int DIALOG_STATE_LIST = 1;
    private ListView lvMyG;
    private String loginAuthor;
    private ArrayList<String> list_myGroup;
    private CardArrayAdapter cardArrayAdapter;
    private HttpRequest httpRequest;

    // инициализация списка групп
    private void initialize(){
        lvMyG = (ListView) findViewById(R.id.lvMyG);
        list_myGroup = new ArrayList<>();
    }

    // получение логина
    private void getDate(){
        try {
            Intent intent = getIntent();
            this.loginAuthor = intent.getStringExtra("login_u");
        } catch (Exception ex){
        }
    }

    @Override
    protected void onCreate(Bundle setInstanceState){
        super.onCreate(setInstanceState);
        setContentView(R.layout.tab_two_my_group);

        getDate();
        initialize();
        showListMyGroup();

        if (list_myGroup.size() == 0)
            showDialog(DIALOG_STATE_LIST);

        listenerCardAdapter(lvMyG);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d(TEG_TabTwoMyGroupActivity, "Вызван onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showListMyGroup();

        if (list_myGroup.size() == 0)
            showDialog(DIALOG_STATE_LIST);
    }

    private void showListMyGroup(){

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://"+ VAR.IP_SERVER_GLOBAL +"/index.php?part=group_user&action=get&detalAction=getListMyGroups&login="+loginAuthor;
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        list_myGroup = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getList();
        Log.d(TEG_TabTwoMyGroupActivity, "list_myGroup.size() = " + list_myGroup.size());

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
        cardArrayAdapter = new CardArrayAdapter(getApplicationContext(), R.layout.list_item_card);

        for (int i = 0; i < list_myGroup.size(); i++) {
            Log.d(TEG_TabTwoMyGroupActivity, "моя группа: " + list_myGroup.get(i));
            Card card = new Card(list_myGroup.get(i));
            cardArrayAdapter.add(card);
        }
        lvMyG.setAdapter(cardArrayAdapter);
    }

    /**
     * Действия приводимые при нажатии на card и переход на MessageGroupActivity
     * @param listView
     */
    private void listenerCardAdapter(ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int position = i;
                Card card = cardArrayAdapter.getItem(position);

                String login_from = loginAuthor;
                String name_group_to = card.getLine1();

                Log.d(TEG_TabTwoMyGroupActivity, "loginFrom = " + login_from + ", nameGroup = " + name_group_to);

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


// старое
//        adapter = new ArrayAdapter<>(this, R.layout.my_list_item, list_myGroup);
//        /////adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list_myGroup);
//        adapter.notifyDataSetChanged();
//        lvMyG.setAdapter(adapter);
//        /////Log.d(TEG_TabTwoMyGroupActivity, "Окончил работу showListMyGroup()");