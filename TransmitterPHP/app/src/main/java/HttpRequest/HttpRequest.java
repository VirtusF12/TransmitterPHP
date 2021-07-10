package HttpRequest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Класс - Запросы на обработку manage_task
 * добавлена поддержка лямбд выражений в app -> (build.gradle)
 * @author Mihail Kovalenko
 */
public class HttpRequest {

    private final static String TEG_HttpRequest = "TEG_HttpRequest";

    private Context context;
    private String url;
    private StateRequest state;
    int globalCode = 0;
    String text = "";

    private Commonable commonable;
    private Getable getable;

    public HttpRequest(String url, StateRequest state, Context context){

        this.url = url;
        this.state = state;
        this.context = context;

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        commonable = (url_) -> {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url1 = new URL(url_);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
                        httpURLConnection.setRequestMethod("GET");

                        int code = httpURLConnection.getResponseCode();
                        if (code == HttpURLConnection.HTTP_OK) {
                            Log.d(TEG_HttpRequest, "Код: " + code);
                        } else {
                            Log.d(TEG_HttpRequest, "Код ошибки: " + code);
                        }
                        globalCode = code;
                    } catch (MalformedURLException e) {
                        Log.d(TEG_HttpRequest, "MalformedURLException : " + e.toString());
                    } catch (IOException ex) {
                        Log.d(TEG_HttpRequest, "IOException : " + ex.toString());
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return globalCode;
        };
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        getable = (url_) -> {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url2 = new URL(url_);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url2.openConnection();
                        httpURLConnection.setRequestMethod("GET");

                        BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String str = "";
                        int code = httpURLConnection.getResponseCode();
                        if (code == HttpURLConnection.HTTP_OK) {
                            while((str = reader.readLine()) != null){
                                sb.append(str);
                            }
                            String myStr = sb.toString();
                            // parser.parse(myStr, this.listBoard);
                            text = myStr;
                        } else {
                            Log.d(TEG_HttpRequest, "Список досок НЕ получен");
                        }
                    } catch (MalformedURLException e) {
                        Log.d(TEG_HttpRequest, "MalformedURLException : " + e.toString());
                    } catch (IOException ex) {
                        Log.d(TEG_HttpRequest, "IOException : " + ex.toString());
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return text;
        };
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    public void execute(){

        switch (state){
            case COMMON_REQUEST:
                commonable.execRequest(this.url);
                break;
            case GET:
                getable.get(this.url);
                break;
            default:
                Toast.makeText(context, "Запроса неверный.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public String getRequest(){
        return this.text;
    }
}
