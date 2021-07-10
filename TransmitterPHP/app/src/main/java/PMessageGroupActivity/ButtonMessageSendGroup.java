package PMessageGroupActivity;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public final class ButtonMessageSendGroup extends Thread {

    private static String TEG_ButtonMessageSendGroup = "TEG_ButtonMessageSendGroup";
    private String url;

    public ButtonMessageSendGroup(String url){
        this.url = url;
    }

    public void run(){
        try {
            URL url = new URL(this.url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            int code = httpURLConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                //Log.d(TEG_ButtonMessageSendGroup, "HTTP_OK ButtonMessageSendGroup");
            }else{
                //Log.d(TEG_ButtonMessageSendGroup, "NO HTTP_OK ButtonMessageSendGroup");
            }
        } catch (MalformedURLException e) {
            //Log.d(TEG_ButtonMessageSendGroup, "MalformedURLException : " + e.toString());
        } catch(IOException ex){
            //Log.d(TEG_ButtonMessageSendGroup, "IOException : " + ex.toString());
        }
    }
}
