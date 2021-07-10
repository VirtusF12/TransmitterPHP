package com.example.virtus.transmitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import HttpRequest.*;
import PCodeDecodeMessage.CDMessage;
import PMessageBubbleChatActivity.ChatArrayAdapter;
import PMessageBubbleChatActivity.ChatMessage;
import Parser.*;
import Robot.*;

/**
 * Активити - новая версия окна переписки (пузырьковый чат)
 */
public final class BubbleChatActivity extends Activity {

    private static final String TEG_BubbleChatActivity = "TEG_BubbleChatActivity";
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private String login_from, login_to;
    private ArrayList<String> list_mess;
    private HttpRequest httpRequest;

    private UpdateAsyncTask updateAsyncTask;

    /**
     * Класс - для обновления списка сообщений
     */
    class UpdateAsyncTask extends AsyncTask<Void, Boolean, Void> {

       private boolean isWork = false;

       public void setIsWork(boolean isWork){
           this.isWork = isWork;
       }

       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           Log.d(TEG_BubbleChatActivity, "Сработал onPreExecute()");
       }

        @Override
       protected Void doInBackground(Void... voids) {

            Log.d(TEG_BubbleChatActivity, "Сработал doInBackground()");
            while (isWork){
               // synchronized (this){
                   Log.d(TEG_BubbleChatActivity, "@@@@@@@@@@@@@@@@@@@ Работает start -------> UpdateMess");
                   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                   final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=messages&action=get&detalAction=getCountConversation&fromLogin="+login_from+"&toLogin="+login_to;
                   httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
                   httpRequest.execute();
                   String countText = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getArrayList()[0];
                   Log.d(TEG_BubbleChatActivity, "UpdateMess -> countText = " + countText);
                   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                   if (Integer.parseInt(countText) != list_mess.size()){

                       Log.d(TEG_BubbleChatActivity,"Integer.parseInt(countText) = " + Integer.parseInt(countText));
                       Log.d(TEG_BubbleChatActivity, "list_mess.size() = " + list_mess.size());
                       Log.d(TEG_BubbleChatActivity, "ПРИШЛО СООБЩЕНИЕ");

                       updateListMessage();
                       setListItems(list_mess, chatArrayAdapter);

                       publishProgress(true); // сообщаем что нужно обновить список
                   }
                   try {
                       Thread.sleep(1_000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   Log.d(TEG_BubbleChatActivity, "@@@@@@@@@@@@@@@@@@@ Работает end -------> UpdateMess");
               //}
            }
            Log.d(TEG_BubbleChatActivity, "END END  END  END  END  END ЗАВЕРШЕНО ОБНОВЛЕНИЕ");
            return null;
       }

       @Override
       protected void onProgressUpdate(Boolean... values) {
           super.onProgressUpdate(values);

           if (values[0] == true){

               Log.d(TEG_BubbleChatActivity, "values[0] == true");
               chatArrayAdapter.notifyDataSetChanged();
               Log.d(TEG_BubbleChatActivity, "обновление было выполнено");
           }
       }

       @Override
       protected void onPostExecute(Void aVoid) {
           super.onPostExecute(aVoid);
           Log.d(TEG_BubbleChatActivity, "Сработал onPostExecute()");
       }
   }

    private void initialize(){

        buttonSend = (Button) findViewById(R.id.btnSendBubbleChat); // кнопка для отправки сообщения
        listView = (ListView) findViewById(R.id.listViewBubbleChat); // список всех сообщений
        chatText = (EditText) findViewById(R.id.chatTextBubbleChat); // поле для отправки сообщения
        //listView.setDivider(null);
        list_mess = new ArrayList<>();
    }

    private void checkInternet(){
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (!Robot.isOnline(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "Нет соединения с интернетом.",Toast.LENGTH_SHORT).show();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    /**
     * Получение данных от SecondMainActivity login_from и login_to
     */
    private void getDate(){

        try {
            Intent my_intent = getIntent();
            login_from = my_intent.getStringExtra("login_from");
            login_to = my_intent.getStringExtra("login_to");
            Log.d(TEG_BubbleChatActivity, "login_from = " + login_from + " login_to = " + login_to);
        }catch (Exception ex){
            Log.d(TEG_BubbleChatActivity, "Данные MessageActivity не получены");
        }


//        try {
//            Intent mIntent = getIntent();
//            this.rellLogin = mIntent.getStringExtra("myLogin");
//            Log.d(TEG_BubbleChatActivity, "Полученные данные: login = " + this.rellLogin);
//        }catch (Exception ex){
//            Log.d(TEG_BubbleChatActivity, "Error (невозможно принять данные): " + ex.toString());
//        }
    }

    private void updateListMessage(){

        list_mess.clear();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // запрос на получение сообщения
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=messages&action=get&detalAction=getConversation&fromLogin="+login_from+"&toLogin="+login_to;
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        String request = httpRequest.getRequest();
        new Parser().filterForMessages(request, list_mess);
        Log.d(TEG_BubbleChatActivity, "list_mess.size() = " + list_mess.size());
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble_chat);
        getDate();
        initialize();
        checkInternet();
        /**
         * Запуск при старте и заполнение list_mess
         */
        updateListMessage();
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage); // настройка кастомного адаптера на вход передаем 1. текущий контекст 2. слой формы для отображения элемента списка
        setListItems(list_mess, chatArrayAdapter);
        listView.setAdapter(chatArrayAdapter);

        // view.setOnKeyListener() вызывается при нажатии на charText
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });

        // установить что-бы список всегда прокручивался вниз
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //опуститься в низ списка
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        updateAsyncTask = new UpdateAsyncTask();
        updateAsyncTask.setIsWork(true);
        updateAsyncTask.execute();
    }

    /**
     * @return текущего времени
     */
    private String getCurrentTime(){

        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss");
        return formatForDateNow.format(dateNow).toString();
    }

    /**
     * Кнопка по отправке сообщения на сервер
     * @param v
     */
    public void sendMessageBubble(View v){

        String currentTime = getCurrentTime();
        String message = chatText.getText().toString();

        String res_message = this.login_from + ">~(" + message + ")~("+currentTime+")";
        String newResultMessage = CDMessage.CodeMessage(res_message);
        Log.d(TEG_BubbleChatActivity, "Message = (" + newResultMessage + ")");

        String[] arrSplitterMessage = res_message.split("~");
        String name = arrSplitterMessage[0].substring(0, arrSplitterMessage[0].length()-1);
        String mess =  arrSplitterMessage[1].substring(1, arrSplitterMessage[1].length()-1);
        String time =  arrSplitterMessage[2].substring(1, arrSplitterMessage[2].length()-1);

        Log.d(TEG_BubbleChatActivity, "метод sendMessage() " + name + " " + mess  + " " + time);
        this.chatArrayAdapter.add(new ChatMessage(name, time, mess, this.login_from));
        this.chatArrayAdapter.notifyDataSetChanged();

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // отправка сообщения на сервер
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=messages&action=insert&fromLogin="+login_from+"&toLogin="+login_to+"&mess="+newResultMessage;
        Log.d(TEG_BubbleChatActivity, "~~~~~ url = " + url);
        httpRequest = new HttpRequest(url, StateRequest.COMMON_REQUEST, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        listView.setSelection(this.chatArrayAdapter.getCount() - 1);
        chatText.setText(""); // очистака текста после передачи его на сервер
    }

    /**
     * Получение от сервера списка сообщений и преобразование его в нужный формат
     * @param list_mess
     * @param chatArrayAdapter
     */
    private void setListItems(ArrayList<String> list_mess, ChatArrayAdapter chatArrayAdapter){

        Log.d(TEG_BubbleChatActivity, "Метод addNewItems начал работать, размер списка = " + list_mess.size());
        ArrayList<ChatMessage> newItems = new ArrayList<>();

        for (int i = 0; i < list_mess.size(); i++ ){

            String currentMessage = list_mess.get(i); // q18934> (привет) (05:44)
            String[] arrSplitterMessage = currentMessage.split("~");

            String name = arrSplitterMessage[0].substring(0, arrSplitterMessage[0].length()-1);
            String message =  arrSplitterMessage[1].substring(1, arrSplitterMessage[1].length()-1);
            String time =  arrSplitterMessage[2].substring(1, arrSplitterMessage[2].length()-1);
            Log.d(TEG_BubbleChatActivity, "СРАБОТАЛ МЕТОД getListItems() " + name + " " + message + " " + time + " i = " + i);

            newItems.add(new ChatMessage(name,  time,  message,  this.login_from));
        }

        Log.d(TEG_BubbleChatActivity, "Метод addNewItemsBubble отработал");
        chatArrayAdapter.setChatMessageList(newItems);
    }

    private boolean sendChatMessage(){

        // chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        chatArrayAdapter.add(new ChatMessage("lex", "23.0.10", chatText.getText().toString(), "lex"));
        chatText.setText("");
        //side = !side;

        return true;
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Log.d(TEG_BubbleChatActivity, "onBackPressed()");
    }

    @Override
    protected void onStart() {

        updateAsyncTask.setIsWork(true);
        super.onStart();
        Log.d(TEG_BubbleChatActivity, "onStart()");
    }

    @Override
    protected void onResume() {

        updateAsyncTask.setIsWork(true);
        super.onResume();
        Log.d(TEG_BubbleChatActivity, "onResume()");
    }

    @Override
    protected void onPause() {

        updateAsyncTask.setIsWork(false);
        super.onPause();
        Log.d(TEG_BubbleChatActivity, "onPause()");
    }

    @Override
    protected void onStop() {

        updateAsyncTask.setIsWork(false);
        super.onStop();
        Log.d(TEG_BubbleChatActivity, "onStop()");
    }

    @Override
    protected void onRestart() {

        updateAsyncTask.setIsWork(true);
        super.onRestart();
        Log.d(TEG_BubbleChatActivity, "onRestart()");
    }

    @Override
    protected void onDestroy() {

        updateAsyncTask.setIsWork(false);
        super.onDestroy();
        Log.d(TEG_BubbleChatActivity, "onDestroy()");
    }
}