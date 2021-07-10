package PMessageActivity;

import Filters.Filter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import Filters.Filter;
import Parser.Parser;

public final class ListConversation extends Thread {

    private static String TEG_ListConversation = "TEG_ListConversation";
    private String url;
    private Parser parser;
    private ArrayList<String> list_mess;

    public ListConversation(String url, ArrayList<String> list_mess){
        this.url = url;
        this.list_mess = list_mess;
        parser = new Parser();
    }

    public void run(){
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
                parser.filterForMessages(strAllMess, list_mess);
            } else {
                Log.d(TEG_ListConversation, "Список пользователей НЕ получен.");
            }
        } catch (MalformedURLException e) {
            Log.d(TEG_ListConversation, "MalformedURLException : " + e.toString());
        } catch (IOException ex) {
            Log.d(TEG_ListConversation, "IOException : " + ex.toString());
        }
    }
    public ArrayList<String> getListAllMess(){
        return this.list_mess;
    }
}
