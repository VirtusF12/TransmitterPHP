package PGroupAllActivity;

import Filters.Filter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public final class GroupListThread extends Thread {

    private static final String TEG_GroupListThread = "TEG_GroupListThread";
    private String url;
    private Filter filter;
    private String[] name_groups;

    public GroupListThread(String url){
        this.url = url;
        filter = new Filter();
    }

    public String[] getMassGroup(){
        return this.name_groups;
    }

    public void run(){
        try {
            URL url = new URL(this.url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String str = "";
            int code = httpURLConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                Log.d(TEG_GroupListThread , "HTTP == 200");
                while((str =reader.readLine()) != null){
                    stringBuilder.append(str);
                }
                String strFromServer = stringBuilder.toString();
                String[] mas_group = filter.filterRegExprForUserName(strFromServer);
                this.name_groups = mas_group;
            }else{
                Log.d(TEG_GroupListThread, "HTTP != 200");
            }
        } catch (MalformedURLException e) {
            Log.d(TEG_GroupListThread, "MalformedURLExceptiotn : " + e.toString());
        } catch(IOException ex){
            Log.d(TEG_GroupListThread, "IOException : " + ex.toString());
        }
    }
}
