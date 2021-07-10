package PMessageGroupActivity;


import Filters.Filter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public final class ConversationGroup extends Thread {

    private static String TEG_ConversationGroup = "TEG_ConversationGroup";
    private String url;
    private ArrayList<String> list_mess;
    private Filter filter;
    private boolean isWorkThread;

    public ConversationGroup(String url, ArrayList<String> list_mess){
        this.url = url;
        this.list_mess = list_mess;
        filter = new Filter();
        this.isWorkThread = true;
    }

    public void setWorkThread(boolean isWorkThread){
        this.isWorkThread = isWorkThread;
    }

    public void run(){

        while (this.isWorkThread) {
            try {
                URL url = new URL(this.url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String str = "";
                int code = httpURLConnection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    while ((str = reader.readLine()) != null) {
                        stringBuilder.append(str);
                    }
                    String strAllMess = stringBuilder.toString();
                    // filter.filterForMessages_TEST(strAllMess, list_mess);

                } else {
                    Log.d(TEG_ConversationGroup, "Список пользователей группы НЕ получен");
                }
            } catch (MalformedURLException e) {
                Log.d(TEG_ConversationGroup, "MalformedURLException : " + e.toString());
            } catch (IOException ex) {
                Log.d(TEG_ConversationGroup, "IOException : " + ex.toString());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
