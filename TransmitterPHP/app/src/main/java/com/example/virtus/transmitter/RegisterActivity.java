package com.example.virtus.transmitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import HttpRequest.*;
import Parser.*;
import Robot.*;

public final class RegisterActivity extends Activity {

    private static final String TEG_RegisterActivity = "TEG_RegisterActivity";
    private EditText etRegLogin, etRegPassword, etRegRepPassword;
    private HttpRequest httpRequest;

    /**
     * Инициализация: etRegLogin, etRegPassword, etRegRepPassword
     */
    private void initialize(){

        etRegLogin = (EditText) findViewById(R.id.etRegLogin);
        etRegPassword = (EditText) findViewById(R.id.etRegPassword);
        etRegRepPassword = (EditText) findViewById(R.id.etRegRepPassword);
    }

    private void checkInternet(){
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (!Robot.isOnline(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "Нет соединения с интернетом.",Toast.LENGTH_SHORT).show();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialize();
        checkInternet();
    }

    /**
     * Кнопка - отправить запрос на регистрацию на сервере
     * @param v
     */
    public void startBtnReg(View v){

        final String login = etRegLogin.getText().toString();
        final String password = etRegPassword.getText().toString();
        final String repPassword = etRegRepPassword.getText().toString();

        if (login.equals(null) || password.equals(null) || repPassword.equals(null) ||
                login.equals("") || password.equals("") || repPassword.equals("")){
            Toast.makeText(this, "Поля не заполнены!", Toast.LENGTH_LONG).show();
        } else {

            if (!password.equals(repPassword)){ // проверка на сопостовимость паролей
                Toast.makeText(this, "Пароли не эквивалентны", Toast.LENGTH_LONG).show();
            } else {

                if (Robot.isOnline(getApplicationContext())){ // !Robot.isOnline(getApplicationContext()) проверка на подключение к сети
                    Toast.makeText(getApplicationContext(), "Нет соединения с интернетом.",Toast.LENGTH_SHORT).show();
                } else {
                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    final String urlID = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=users&action=get&detalAction=checkExistUser&login="+login;
                    Log.d(TEG_RegisterActivity, "urlID = " + urlID);
                    httpRequest = new HttpRequest(urlID, StateRequest.GET, getApplicationContext());
                    httpRequest.execute();
                    Log.d(TEG_RegisterActivity, "t: " + httpRequest.getRequest());
                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    int id = -1;
                    try {
                        String num = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getArrayList()[0];
                        Log.d(TEG_RegisterActivity, "strNum = " + num + ", len = " + num.length());
                        id = Integer.parseInt(num);
                        Log.d(TEG_RegisterActivity, "id = " + String.valueOf(id));
                    } catch (Exception ex){
                        id = -1;
                        Log.d(TEG_RegisterActivity, "Ошибка: " + ex.toString());
                    }
                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    if (id == -1) {
                        sendRegRequest(login, password);
                        Log.d(TEG_RegisterActivity, " httpRequest = " + httpRequest.getRequest() + " size = " + httpRequest.getRequest().length());
                    } else {
                        Log.d(TEG_RegisterActivity, "else httpRequest = " + httpRequest.getRequest() + " size = " + httpRequest.getRequest().length());
                        Toast.makeText(this, "Такой пользователь уже зарегистрирован", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }


    private void sendRegRequest(String login, String password){

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=users&action=insert&login="+login+"&password="+password;
        HttpRequest httpRequestInsert = new HttpRequest(url, StateRequest.COMMON_REQUEST, getApplicationContext());
        httpRequestInsert.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        clearRowsLoginPass();
        Toast.makeText(this, "Вы успешно зарегистрировались", Toast.LENGTH_LONG).show();
        goToBackActivity();
    }

    private void clearRowsLoginPass(){

        this.etRegLogin.setText("");
        this.etRegPassword.setText("");
    }

    private void goToBackActivity(){
        startActivity(new Intent(this,MainActivity.class));
    }
}
