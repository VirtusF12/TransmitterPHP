package com.example.virtus.transmitter;

import HttpRequest.*;
import PCodeDecodeMessage.CDMessage;
import PMessageActivity.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Parser.Parser;
import Robot.*;

/**
 * Активити - старая версия окна переписки
 */
public final class MessageActivity extends Activity{

    private static final String TEG_MessageActivity = "TEG_MessageActivity";
    private static final int CCM_DELETE_ID = 1;
    private Button btnSendMess;
    private EditText etMess;
    private ListView listView1;
    private Conversation conversation, newConversation;
    private ArrayList<String> list_mess;
    private ArrayList<Integer> list_id_mess;
    private Intent my_intent;
    private String login_from, login_to;
    // private ListConversation listConversation; // поток на получения списка переговоров
    private CustomeAdapter testCustomeAdapter;
    private ArrayList<NewItem> testListNewItem;
    private Parser parser;
    private HttpRequest httpRequest;

    /**
     * Получение данных: login_from, login_to
     */
    private void getDate(){

        try {
            my_intent = getIntent();
            login_from = my_intent.getStringExtra("login_from");
            login_to = my_intent.getStringExtra("login_to");
            Log.d(TEG_MessageActivity, "login_from = " + login_from + " login_to = " + login_to);
        }catch (Exception ex){
            Log.d(TEG_MessageActivity, "Данные MessageActivity не получены");
        }
    }

    private void initialize(){

        btnSendMess = (Button) findViewById(R.id.btnSendMess);
        etMess = (EditText) findViewById(R.id.etMess);
        listView1 = (ListView) findViewById(R.id.listView1);
        list_mess = new ArrayList<>();
        parser = new Parser();
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
        setContentView(R.layout.activity_message);
        Log.d(TEG_MessageActivity, "MessageActivity вызван onCreate()");

        getDate();
        initialize();
        checkInternet();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // получение списка сообщений
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=messages&action=get&detalAction=getConversation&fromLogin="+login_from+"&toLogin="+login_to;
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        parser.filterForMessages(httpRequest.getRequest(), list_mess);
        Log.d(TEG_MessageActivity, "list_mess.length = " + list_mess.size());
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //newConversation = new Conversation(urlConversation, list_mess);
        //newConversation.setWorkThread(false);
        testListNewItem = getListItems(list_mess);
        testCustomeAdapter = new CustomeAdapter(this, testListNewItem);
        listView1.setAdapter(testCustomeAdapter);
        listView1.setSelection(testCustomeAdapter.getCount() - 1);
        // подписка listView на вызов контекстного меню
        registerForContextMenu(listView1);
        //conversation = new Conversation(urlConversation, list_mess); // обновление списка
        //conversation.start();
        //conversation.setPriority(Thread.MAX_PRIORITY);
        testCustomeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {

        super.onRestart();
        Log.d(TEG_MessageActivity, "Вызван onRestart()");
        // перезагрузить список сообщений (при Restart)
//        final String urlConversation = "http://62.109.19.148:5000/getConversation_N/?from_u=\"" + login_from + "\"&to_u=\"" + login_to + "\"";
//        newConversation = new Conversation(urlConversation, list_mess);
//        newConversation.start();
        testCustomeAdapter.notifyDataSetChanged();
        Log.d(TEG_MessageActivity, "MessageActivity --> Запуск потока (обновления сообщений) вызван onRestart() <--");
    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.d(TEG_MessageActivity, "Вызван onPause()");
//        conversation.setWorkThread(false);
//        newConversation.setWorkThread(false);
        Log.d(TEG_MessageActivity, "-- Остановка потока (обновления сообщений) вызван onPause() --");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CCM_DELETE_ID, 0, "Удалить запись");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == CCM_DELETE_ID){
            // получение инфу о пункте меню
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // удаляем по текущей позиции
            Log.d(TEG_MessageActivity, "Позиция сообщения в списке: " + adapterContextMenuInfo.position);
            testListNewItem.remove(adapterContextMenuInfo.position);

            testCustomeAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * @return текущего времени
     */
    private String getCurrentTime(){

        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss");
        return formatForDateNow.format(dateNow).toString();
    }

    private ArrayList<NewItem> getListItems(ArrayList<String> list_mess){

        Log.d(TEG_MessageActivity, "Метод addNewItems начал работать, размер списка = " + list_mess.size());
        ArrayList<NewItem> newItems = new ArrayList<>();

        for (int i = 0; i < list_mess.size(); i++ ){

            String currentMessage = list_mess.get(i); // q18934> (привет) (05:44)
            String[] arrSplitterMessage = currentMessage.split("~");

            String name = arrSplitterMessage[0].substring(0, arrSplitterMessage[0].length()-1);
            String message =  arrSplitterMessage[1].substring(1, arrSplitterMessage[1].length()-1);
            String time =  arrSplitterMessage[2].substring(1, arrSplitterMessage[2].length()-1);
            Log.d(TEG_MessageActivity, "СРАБОТАЛ МЕТОД getListItems() " + name + " " + message + " " + time + " i = " + i);

            newItems.add(new NewItem(name, time, message));
        }
        Log.d(TEG_MessageActivity, "Метод addNewItems отработал");

        return newItems;
    }

    /**
     * Кнопка - отправка сообщения на сервер
     * @param v
     */
    public void sendMessage(View v){

        String currentTime = getCurrentTime();
        String message = etMess.getText().toString();

        String res_message = login_from + ">~(" + message + ")~("+currentTime+")";
        String newResultMessage = CDMessage.CodeMessage(res_message);
        Log.d(TEG_MessageActivity, "Message = (" + newResultMessage + ")");

        String[] arrSplitterMessage = res_message.split("~");
        String name = arrSplitterMessage[0].substring(0, arrSplitterMessage[0].length()-1);
        String mess =  arrSplitterMessage[1].substring(1, arrSplitterMessage[1].length()-1);
        String time =  arrSplitterMessage[2].substring(1, arrSplitterMessage[2].length()-1);

        Log.d(TEG_MessageActivity, "метод sendMessage() " + name + " " + mess  + " " + time);
        this.testListNewItem.add(new NewItem(name, time, mess));
        this.testCustomeAdapter.notifyDataSetChanged();

        etMess.post(new Runnable() {
            @Override
            public void run() {
                etMess.setText("");
            }
        });
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // отправка сообщения на сервер
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=messages&action=insert&fromLogin="+login_from+"&toLogin="+login_to+"&mess="+newResultMessage;
        httpRequest = new HttpRequest(url, StateRequest.COMMON_REQUEST, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        listView1.setSelection(this.testCustomeAdapter.getCount() - 1);
    }
}
