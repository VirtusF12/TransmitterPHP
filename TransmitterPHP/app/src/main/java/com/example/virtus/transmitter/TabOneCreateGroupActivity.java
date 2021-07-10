package com.example.virtus.transmitter;

import HttpRequest.*;
import Parser.*;
import Robot.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public final class TabOneCreateGroupActivity extends Activity {

    private static final String TEG_TabOneCreateGroupActivity = "TEG_TabOneCreateGroupActivity";
    private EditText etRowNameGroup, etPassGroup;
    private Button btnSendCreateGroup;
    private Intent intent;
    private String loginAuthor;
    private HttpRequest httpRequest;

    /**
     * Получение моего логина. Нужен для создания группы в качетсве автора
     */
    private void getDate(){

        try{
            intent = getIntent();
            this.loginAuthor = intent.getStringExtra("login_u");
            //Log.d(TEG_TabOneCreateGroupActivity, "Логин: " + this.loginAuthor);
        } catch (Exception ex){
            //Log.d(TEG_TabOneCreateGroupActivity, "Ошибка при передаче логина с SecondMainActivity");
        }
    }

    private void initialize(){

        etRowNameGroup = (EditText) findViewById(R.id.etRowNameGroup);
        etPassGroup = (EditText) findViewById(R.id.etPassGroup);
        btnSendCreateGroup = (Button) findViewById(R.id.btnSendCreateGroup);
    }

    @Override
    protected void onCreate(Bundle setInstanceState){
        super.onCreate(setInstanceState);
        setContentView(R.layout.tab_one_create_group);

        getDate();
        initialize();

        btnSendCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameGroup = etRowNameGroup.getText().toString();
                String passGroup = etPassGroup.getText().toString();

                if (nameGroup.equals("") || passGroup.equals("")){
                    Toast.makeText(getApplicationContext(), "Поля не заполнены", Toast.LENGTH_LONG).show();
                }else {

                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    final String urlGetId = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=group_admin&action=get&detalAction=getIDGroup&nameGroup="+nameGroup;
                    httpRequest = new HttpRequest(urlGetId, StateRequest.GET, getApplicationContext());
                    httpRequest.execute();
                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    // ~~~~~~~~~~~~~~~~~
                    int id = -1;
                    try {
                        String num = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getArrayList()[0];
                        Log.d(TEG_TabOneCreateGroupActivity, "strNum = " + num + ", len = " + num.length());
                        id = Integer.parseInt(num);
                        Log.d(TEG_TabOneCreateGroupActivity, "id = " + String.valueOf(id));
                    } catch (Exception ex){
                        id = -1;
                        Log.d(TEG_TabOneCreateGroupActivity, "Ошибка: " + ex.toString());
                    }
                    // ~~~~~~~~~~~~~~~~~
//                    String url = "http://62.109.19.148:5000/getIDGroup_N/?name_group=\"" + nameGroup + "\"";
//                    threadExistsGroup.start();
//                    try {
//                        threadExistsGroup.join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    if (id != -1){
                        Toast.makeText(getApplicationContext(), "Такая группа уже есть", Toast.LENGTH_LONG).show();
                    } else {

                        createNewGroupAdmin(loginAuthor, nameGroup, passGroup); // добавление группы в (админскую группу)
                        Toast.makeText(getApplicationContext(), "Группа добавлена в (админ. гр. табл.)", Toast.LENGTH_LONG).show();

                        addGroupToMyList(loginAuthor, nameGroup); // добавление группы в (список моих групп)
                        Toast.makeText(getApplicationContext(), "Группа добавлена в (список моих групп)", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /**
     *
     * @param author автор группы
     * @param nameGroup название для группы
     * @param passGroup пароль от группы
     */
    private void createNewGroupAdmin(String author, String nameGroup, String passGroup){

        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=group_admin&action=insert&author="+author+"&pass="+passGroup+"&nameGroup="+nameGroup;
        httpRequest = new HttpRequest(url, StateRequest.COMMON_REQUEST, getApplicationContext());
        httpRequest.execute();
        /*
        String urlAddNewGroup = "http://62.109.19.148:5000/addNewGroupNew_N/?author=\"" + author + "\"&password=\"" + passGroup + "\"&name_group=\"" + nameGroup + "\"";
        threadCreateGroup = new T(urlAddNewGroup);
        threadCreateGroup.start();
        try {
            threadCreateGroup.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

    private void addGroupToMyList(String login, String nameGroup){

        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=group_user&action=insert&login="+login+"&nameGroup="+nameGroup;
        httpRequest = new HttpRequest(url, StateRequest.COMMON_REQUEST, getApplicationContext());
        httpRequest.execute();

        // group_user
        /*
        final String urlAddMyGroup = "http://62.109.19.148:5000/addGroupToMyGroupNew_N/?login=\"" + login + "\"&n_group=\"" + nameGroup + "\"";
        threadAddGroupToMy = new ThreadAddGroupToMy(urlAddMyGroup);
        threadAddGroupToMy.start();
        try {
            threadAddGroupToMy.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }
}
