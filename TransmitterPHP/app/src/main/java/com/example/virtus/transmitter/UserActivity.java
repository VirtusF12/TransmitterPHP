package com.example.virtus.transmitter;

import HttpRequest.*;
import Parser.*;
import Robot.*;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.support.v7.widget.Toolbar;
import java.util.ArrayList;

/**
 * Активити - всех пользователей
 */
public final class UserActivity extends AppCompatActivity {

    private static final String TEG_UserActivity = "TEG_UserActivity";
    private String myLogin;
    private String[] listWithoutMyLogin;
    private EditText etSearchUsers;
    private ListView lvSimple;
    private ArrayAdapter<String> adapter;
    private HttpRequest httpRequest;

    /**
     * Класс - для автодополнения логина пользоваеля
     */
    class TextChangeListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            adapter.getFilter().filter(charSequence);
            //         cardArrayAdapter.getFilter().filter(charSequence);
            Log.d(TEG_UserActivity, "i = " + i + ", i1 = " + i1 + ", i2 = " + i2);
        }

        @Override
        public void afterTextChanged(Editable editable) { }
    }

    private void setActionBar(){

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setTitle("Все пользователи");
            setSupportActionBar(toolbar);
            // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ex){
            Log.d(TEG_UserActivity, "Ошибка UserActivity ActionBar: " + ex.toString());
        }
    }

    /**
     * Получение данных от SecondMainActivity: myLogin
     */
    private void getDate(){
        try{

            myLogin = getIntent().getStringExtra("myLogin");
            Log.d(TEG_UserActivity,"Полученные данные: " + myLogin);
        }   catch (Exception ex){
            Log.d(TEG_UserActivity, "Данные не получены UserActivity: " + ex.toString() );
        }
    }

    private void initialize(){

        lvSimple = (ListView) findViewById(R.id.listVMy);
        etSearchUsers = (EditText) findViewById(R.id.etSearchUsers);
        etSearchUsers.addTextChangedListener(new TextChangeListener());
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
        setContentView(R.layout.activity_user);

        setActionBar();
        getDate();
        initialize();
        checkInternet();

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=users&action=get&detalAction=getAllUsers";
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        String[] arrUsers = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getArrayList();
        Log.d(TEG_UserActivity, "arrUsers.length = " + arrUsers.length);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (arrUsers != null){

            Log.d(TEG_UserActivity, "arrUsers != null");
            listWithoutMyLogin = getListWithoutMyLogin(arrUsers);
            adapter = new ArrayAdapter(this, R.layout.my_list_item_users, listWithoutMyLogin);
            lvSimple.setAdapter(adapter);
            listenerOptionMenuSimpleAdapter(lvSimple);

        } else {
            Log.d(TEG_UserActivity, "arrUsers == null");
        }
        // overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        // setAnimationToEditText(etSearchUsers);
    }

    /**
     * Анимация для etSearchUsers
     * @param etSearchUsers
     */
    private void setAnimationToEditText(EditText etSearchUsers){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.my_rotate);
        etSearchUsers.setAnimation(animation);
    }

    /**
     * @param arrUsers массив всех пользователей
     * @return массива без моего лигина
     */
    private String[] getListWithoutMyLogin(String[] arrUsers){

        ArrayList<String> list = new ArrayList<>();

        for (String login : arrUsers){

            if (!login.equals(myLogin))
                list.add(login);
        }

        return list.toArray(new String[list.size()]);
    }

    private void listenerOptionMenuSimpleAdapter(ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String login_to = (((TextView)view).getText()).toString();
                Log.d(TEG_UserActivity, "Логин кому отправлять данные: " + login_to);
                // данные для отправление в MessageActivity
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra("login_from", myLogin);
                intent.putExtra("login_to", login_to);
                Log.d(TEG_UserActivity,"Данные отправляемые на MessageActivity: login_from = " + myLogin + " login_to = " + login_to);
                String name_chanel = myLogin+"`"+login_to;
                Log.d(TEG_UserActivity,"Имя канала = " + name_chanel);
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=chanel&action=get&detalAction=getIDMyChanel&fromLogin="+myLogin+"&toLogin="+login_to;
                httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
                httpRequest.execute();
                // ~~~~~~~~~~~~~~~~~
                int id = -1;
                try {
                    String num = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getArrayList()[0];
                    Log.d(TEG_UserActivity, "strNum = " + num + ", len = " + num.length());
                    id = Integer.parseInt(num);
                    Log.d(TEG_UserActivity, "id = " + String.valueOf(id));
                } catch (Exception ex){
                    id = -1;
                    Log.d(TEG_UserActivity, "Ошибка: " + ex.toString());
                }
                // ~~~~~~~~~~~~~~~~~
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                if (id != -1){

                    Log.d(TEG_UserActivity,"Такой канал есть");

                } else {

                    Log.d(TEG_UserActivity,"Такого канала нет");
                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    final String urlInsert = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=chanel&action=insert&chanelName="+name_chanel+"&fromLogin="+myLogin+"&toLogin="+login_to;
                    httpRequest = new HttpRequest(urlInsert, StateRequest.COMMON_REQUEST, getApplicationContext());
                    httpRequest.execute();
                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                }
                startActivity(intent);
            }
        });
    }
}
