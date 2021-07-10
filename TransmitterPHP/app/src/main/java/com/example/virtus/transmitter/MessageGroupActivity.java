package com.example.virtus.transmitter;

import android.app.Activity;

import HttpRequest.*;
import Parser.*;
import PCodeDecodeMessage.CDMessage;
import PMessageGroupActivity.*;
import Robot.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class MessageGroupActivity extends Activity {

    private static final String TEG_MessageGroupActivity = "TEG_MessageGroupActivity";
    private EditText etMessGroup;
    private Button btnSendGroup;
    private ListView lvGMess;
    private ListConversationGroup listConversationGroup;
    private ConversationGroup newConversationGroup;
    private ConversationGroup conversationGroup;
    private ArrayList<NewItemGroup> testListNewItemGroup;
    private CustomeAdapterGroup testCustomeAdapterGroup;
    private ButtonMessageSendGroup buttonMessageSendGroup;
    private ArrayList<String> listMessGroup;
    private String login_from, name_group_to;

    private HttpRequest httpRequest;

    private void getDate(){

        try {
            Intent intent = getIntent();
            login_from = intent.getStringExtra("login_from");
            name_group_to = intent.getStringExtra("name_group_to");
            //Log.d(TEG_MessageGroupActivity,"Данные MessageGroupActivity: " + login_from + " " + name_group_to);
        } catch (Exception ex){
            //Log.d(TEG_MessageGroupActivity, "Данные MessageGroupActivity не получены");
        }
    }

    private void initialize(){

        etMessGroup = (EditText) findViewById(R.id.etMessGroup);
        btnSendGroup = (Button) findViewById(R.id.btnSendGroup);
        lvGMess = (ListView) findViewById(R.id.lvGMess);
        listMessGroup = new ArrayList<>();
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
        setContentView(R.layout.activity_message_group);

        getDate();
        initialize();
        checkInternet();

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=group_user_mess&action=get&detalAction=getMessGroup&nameGroup="+name_group_to;
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        new Parser().filterForMessages(httpRequest.getRequest(), listMessGroup);

        if (listMessGroup.size() == 0){
            StringBuilder sb = new StringBuilder();
            sb.append("Введите сообщения...");
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        } else {
            Log.d(TEG_MessageGroupActivity, "listMessGroup.size() = " + listMessGroup.size());
        }

        //newConversationGroup = new ConversationGroup(urlConversationGroup, listMessGroup);
        //newConversationGroup.setWorkThread(false);

        testListNewItemGroup = getListItems(listMessGroup);
        testCustomeAdapterGroup = new CustomeAdapterGroup(this, testListNewItemGroup);
        lvGMess.setAdapter(testCustomeAdapterGroup);
        lvGMess.setSelection(testCustomeAdapterGroup.getCount() - 1);

        //conversationGroup = new ConversationGroup(urlConversationGroup, listMessGroup);
        //conversationGroup.start();
        //conversationGroup.setPriority(Thread.MAX_PRIORITY);
        testCustomeAdapterGroup.notifyDataSetChanged();

        /**
         * Кнопка по отправки сообщения в группу
         */
        btnSendGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        //Log.d(TEG_MessageGroupActivity, "MessageGroupActivity вызван onStart()");
    }

    @Override
    protected void onRestart() {

        super.onRestart();
        /*
        final String urlConversationGroup = "http://62.109.19.148:5000/getConvGroup_N/?name_group=\"" + name_group_to + "\"";
        newConversationGroup = new ConversationGroup(urlConversationGroup, listMessGroup);
        newConversationGroup.start();
        testCustomeAdapterGroup.notifyDataSetChanged();
        */
    }

    @Override
    protected void onPause() {

        super.onPause();
        //conversationGroup.setWorkThread(false);
        //newConversationGroup.setWorkThread(false);
    }

    private ArrayList<NewItemGroup> getListItems(ArrayList<String> list_mess){

        ArrayList<NewItemGroup> newItems = new ArrayList<>();

        for (int i = 0; i < list_mess.size(); i++ ) {

            String currentMessage = list_mess.get(i); // q18934> (привет) (05:44)
            String[] arrSplitterMessage = currentMessage.split("~");

            String name = arrSplitterMessage[0].substring(0, arrSplitterMessage[0].length()-1);
            String message =  arrSplitterMessage[1].substring(1, arrSplitterMessage[1].length()-1);
            String time =  arrSplitterMessage[2].substring(1, arrSplitterMessage[2].length()-1);

            newItems.add(new NewItemGroup(name, time, message));
        }
        return newItems;
    }

    /**
     * @return получение текущего времени
     */
    private String getCurrentTime(){

        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss");
        return formatForDateNow.format(dateNow).toString();
    }

    public void sendMessage(){

        String currentTime = getCurrentTime();
        String message = etMessGroup.getText().toString();

        String res_message = login_from + ">~(" + message + ")~("+currentTime+")";
        String newResultMessage = CDMessage.CodeMessage(res_message);

        String[] arrSplitterMessage = res_message.split("~");
        String name = arrSplitterMessage[0].substring(0, arrSplitterMessage[0].length()-1);
        String mess =  arrSplitterMessage[1].substring(1, arrSplitterMessage[1].length()-1);
        String time =  arrSplitterMessage[2].substring(1, arrSplitterMessage[2].length()-1);

        this.testListNewItemGroup.add(new NewItemGroup(name, time, mess));
        this.testCustomeAdapterGroup.notifyDataSetChanged();

        etMessGroup.post(new Runnable() {
            @Override
            public void run() {
                etMessGroup.setText("");
            }
        });

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=group_user_mess&action=insert&fromLogin="+login_from+"&nameGroup="+name_group_to+"&mess="+newResultMessage;
        httpRequest = new HttpRequest(url, StateRequest.COMMON_REQUEST, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        lvGMess.setSelection(this.testCustomeAdapterGroup.getCount() - 1);
    }

}
