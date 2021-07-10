package com.example.virtus.transmitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import HttpRequest.*;
import Robot.Robot;

public class GraphActivity extends AppCompatActivity {

    private static final String TEG_GraphActivity = "TEG_GraphActivity";
    private ListView lvTheme;
    private String myLogin;
    private HttpRequest httpRequest;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listDate;

    /**
     * Получение данных от SecondMainActivity: myLogin
     */
    private void getDate(){
        try {

            myLogin = getIntent().getStringExtra("myLogin");
            Log.d(TEG_GraphActivity,"Полученные данные: " + myLogin);
        }   catch (Exception ex){
            Log.d(TEG_GraphActivity, "Данные не получены UserActivity: " + ex.toString() );
        }
    }

    private void initialize(){

        lvTheme = (ListView) findViewById(R.id.lvTheme);
        listDate = new ArrayList<>();
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
        setContentView(R.layout.activity_graph);

        getDate();
        initialize();
        checkInternet();

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://195.133.146.149/?part=board_manage_task&action=get&detalAction=getAuthorSubjectDate&author="+myLogin;
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        Log.d(TEG_GraphActivity, "httpRequest.getRequest() = " + httpRequest.getRequest());
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        innerParserListDate(httpRequest.getRequest(), listDate);
        Log.d(TEG_GraphActivity, "listDate.size() = " + listDate.size());


        for (int i = 0; i < listDate.size(); i++){
            Log.d(TEG_GraphActivity, "listDate ---- " + listDate.get(i));
        }

        String[] arrUserJoin = new String[listDate.size() / 3];
        Log.d(TEG_GraphActivity, "arrUserJoin.length = " + arrUserJoin.length);

        int index = 0;
        for (int i = 2; i < listDate.size(); i+=3){

            String author = listDate.get(i-2);
            String subject = listDate.get(i-1);
            String date = listDate.get(i);

            arrUserJoin[index] = author + " " + subject + " " + date;
            index++;
        }

        for (String t : arrUserJoin)
            Log.d(TEG_GraphActivity, "---- " + t);

        if (arrUserJoin != null){

            Log.d(TEG_GraphActivity, "arrUsers != null");
            adapter = new ArrayAdapter(this, R.layout.my_list_item_users, arrUserJoin);
            lvTheme.setAdapter(adapter);
            listenerOptionMenuSimpleAdapter(lvTheme);

        } else {
            Log.d(TEG_GraphActivity, "arrUsers == null");
        }
    }

    /**
     * Внутренний парсер
     * @param text httpRequest от сервера
     * @param innerList список куда заносить данные
     */
    private void innerParserListDate(String text, ArrayList<String> innerList){

        innerList.clear();
        StringBuilder sb;
        char[] arr = text.toCharArray();
        for (int i = 0; i < text.length(); i++){
            char current = arr[i];
            if (current == '\''){
                sb = new StringBuilder();
                int j = i + 1;
                while (arr[j] != '\'' && j < text.length()){
                    sb.append(arr[j]);
                    j++;
                }
                i = j;
                innerList.add(sb.toString());
            }
        }
    }

    /**
     * При нажатии на элемент списка
     * @param listView
     */
    private void listenerOptionMenuSimpleAdapter(ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String dateBoard = (((TextView)view).getText()).toString();
                String[] arr = dateBoard.split(" ");
                Log.d(TEG_GraphActivity, "Данные для передачи: " + arr[0] + ", " + arr[1] + ", " + arr[2]);

                Intent intent = new Intent(getApplicationContext(), HorizontalBarChartActivity.class);
                intent.putExtra("author", arr[0]);
                intent.putExtra("subject", arr[1]);
                intent.putExtra("date", arr[2]);
                intent.putExtra("myLogin", myLogin);

                startActivity(intent);
            }
        });
    }


    /*
    @Override
    protected void onResume() {

        checkInternet();
        super.onResume();
    }
    */
}
