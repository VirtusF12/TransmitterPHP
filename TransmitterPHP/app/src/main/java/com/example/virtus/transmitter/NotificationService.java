package com.example.virtus.transmitter;

import Filters.Filter;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public final class NotificationService extends Service {



    private String myLogin;
    private Intent myIntent, intent_N;
    private ArrayList<String> currListChanel, listUserSending, listUserNotify;
    private boolean isWorkService = true;
    private HashMap<String, Integer> oldHM, newHM;

    private int i;

    Notification noti;
    private InearThreadListChanel inearThreadListChanel;
    private InearThreadCountMess inearThreadCountMess;


    final class InearThreadListChanel extends Thread {

        private static final String TEG_IListChanelThread = "TEG_IListChanelThread";
        private String url;
        private ArrayList<String> listChanel;
        private Filter filter;

        public InearThreadListChanel(String url){

            this.url = url;
            this.listChanel = new ArrayList<>();
            filter = new Filter();
        }

        public ArrayList<String> getListChanel(){
            return this.listChanel;
        }

        @Override
        public void run (){
            try {
                URL url = new URL(this.url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String str = "";
                int code = httpURLConnection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    str = reader.readLine();
                    stringBuilder.append(str);
                    String my_str = stringBuilder.toString();
                    // filter.filterStringChanel(my_str, this.listChanel);
                } else {
                    Log.d(TEG_IListChanelThread, "IListChanelThread Список каналов НЕ получен");
                }
            } catch (MalformedURLException e) {
                Log.d(TEG_IListChanelThread, "MalformedURLException : " + e.toString());
            } catch (IOException ex) {
                Log.d(TEG_IListChanelThread, "IOException : " + ex.toString());
            }
        }
    }

    final class InearThreadCountMess extends Thread {

        private final static String TEG_InearThreadCountMess = "TEG_InearThreadCountMess";
        private String url;
        private int countMess;

        public InearThreadCountMess (String url){

            this.url = url;
            countMess = 0;
        }

        public int getCountMess(){
            return this.countMess;
        }

        @Override
        public void run(){

            try {
                URL url = new URL(this.url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String str = "";
                int code = httpURLConnection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    str = reader.readLine();

                    try {

                        int num = Integer.parseInt(str);
                        this.countMess = num;
                        //Log.d(TEG_InearThreadCountMess, "---- Число: " + this.countMess);

                    } catch (Exception ex){
                        //Log.d(TEG_InearThreadCountMess, "---- Возникла ошибка в приобразовании типа");
                        countMess = 0;
                    }

                } else {
                    //Log.d(TEG_InearThreadCountMess, "TEG_InearThreadCountMess HTTP != 200");
                }
            } catch (MalformedURLException e) {
                //Log.d(TEG_InearThreadCountMess, "MalformedURLException : " + e.toString());
            } catch (IOException ex) {
                //Log.d(TEG_InearThreadCountMess, "IOException : " + ex.toString());
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        myIntent = intent;

        new Thread(new Runnable() {
            @Override
            public void run() {

                myLogin = myIntent.getStringExtra("myLogin");
                taskNotificationMess();

            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private synchronized void taskNotificationMess(){

        i = 0;

        while (isWorkService) {

            if (i == 0){

                currListChanel = getListChanelServer();
                listUserSending = getListUserSending(currListChanel, this.myLogin);
                oldHM = setHashMap(listUserSending);
            }

            i++;

            if (i == 4){

                currListChanel = getListChanelServer();
                listUserSending = getListUserSending(currListChanel, this.myLogin);
                newHM = setHashMap(listUserSending);
                // получние списка пользователей которые прислали сообщение
                listUserNotify = getListUsersNotify(oldHM, newHM);
                // уведомить
                showNotifyMess();

                oldHM = null;
                newHM = null;
                currListChanel = null;
                listUserSending = null;
                i = 0;
            }
            pause(5);
        }
    }

    private  void showNotifyMess() {

        for (i = 0; i < listUserNotify.size(); i++) {

            Thread inThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    intent_N = new Intent(getApplicationContext(), MessageActivity.class);
                    intent_N.putExtra("login_from", listUserNotify.get(i));
                    intent_N.putExtra("login_to", myLogin);

                    // разница в правах доступа к приложению
                    //  позволяет сторннему приложению (т.е тому которому передали) запустить
                    // в нем переданный intent (от его имени с теми-же полномочиями)
                    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent_N, 0);
                    noti = new Notification.Builder(getApplicationContext())
                            .setContentTitle(listUserNotify.get(i))
                            .setContentText("Вам прислал личное сообщение").setSmallIcon(android.R.drawable.ic_input_add)
                            .setContentIntent(pIntent)
                            .addAction(R.drawable.ic_launcher, "Call", pIntent)
                            .addAction(R.drawable.ic_launcher, "More", pIntent)
                            .addAction(R.drawable.ic_launcher, "And more", pIntent).build();

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;

                    notificationManager.notify(i, noti);
                }
            });

            inThread.start();
            try {
                inThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1_00);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<String> getListUsersNotify(HashMap<String, Integer> oldHM, HashMap<String, Integer> newHM){

        ArrayList<String> listUsersNotify = new ArrayList<>();

        for (String oldLogin : oldHM.keySet()){

            int oldCountMess = oldHM.get(oldLogin);
            int newCountMess = newHM.get(oldLogin);

            if (oldCountMess != newCountMess)
                listUsersNotify.add(oldLogin);
        }

        return listUsersNotify;
    }

    private HashMap<String, Integer> setHashMap (ArrayList<String> listSender){

        HashMap<String, Integer> hm = new HashMap<>();

        for (String from_login : listSender){

            int countMess = getCountMess(from_login, this.myLogin);
            hm.put(from_login, countMess);
        }
        return hm;
    }

    private int getCountMess (String from_login, String to_login){

        final String urlGetCountMess = "http://62.109.19.148:5000/getCountMess_N/?from_login=\"" + from_login +"\"&to_login=\"" + to_login + "\"";
        inearThreadCountMess = new InearThreadCountMess(urlGetCountMess);
        inearThreadCountMess.start();
        try {
            inearThreadCountMess.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return inearThreadCountMess.getCountMess();
    }



    private ArrayList<String> getListUserSending(ArrayList<String> currListChanel, String myLogin){

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < currListChanel.size(); i++){

            String nameChanel = currListChanel.get(i);
            String[] names = nameChanel.split("`");
            String name1 = names[0];
            String name2 = names[1];

            if (!name1.equals(myLogin))
                list.add(name1);

            if (!name2.equals(myLogin))
                list.add(name2);
        }

        return list;
    }

    private ArrayList<String> getListChanelServer(){

        final String urlGetChanel = "http://62.109.19.148:5000/getChanels_Login_N/?login=\"" + myLogin + "\"";
        inearThreadListChanel = new InearThreadListChanel(urlGetChanel);
        inearThreadListChanel.start();
        try {
            inearThreadListChanel.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return inearThreadListChanel.getListChanel();
    }

    private void pause(int second){

        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
