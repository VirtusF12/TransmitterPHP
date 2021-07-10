package com.example.virtus.transmitter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import HttpRequest.*;
import Parser.*;
import Robot.*;

/**
 * Активити для входа в систему и кнопкой для регистрации
 */
public final class MainActivity extends AppCompatActivity {

    private static final String TEG_MainActivity = "TEG_MainActivity";
    private EditText etLogin, etPassword;
    private Button btnEntry, btnCreateAccount;
    private TransmitterCash transmiterCash;
    private String login_db$cash, password_db$cash;
    private HttpRequest httpRequest;

    /**
     * Получение данных из БД
     */
    private void get_db$cash(){

        SQLiteDatabase db = transmiterCash.getWritableDatabase();

        try {
            Cursor cursor = db.query("cash", null, null, null, null, null, null);

            if (cursor.moveToFirst()){

                int id = cursor.getColumnIndex("id");
                int login = cursor.getColumnIndex("login");
                int password = cursor.getColumnIndex("password");

                do {

                    login_db$cash = cursor.getString(login);
                    password_db$cash = cursor.getString(password);
                    Log.d(TEG_MainActivity, "---id = " + cursor.getString(id) + ", login = " + login_db$cash + ", password = " + password_db$cash);

                } while (cursor.moveToNext());
            } else {
                Toast.makeText(getApplicationContext(), "В таблице нет строк.", Toast.LENGTH_SHORT).show();
                Log.d(TEG_MainActivity, "---в таблице нет строк");
            }
            cursor.close();

        } catch (Exception ex){
            Log.d(TEG_MainActivity, "---Возникли ошибки в cursor: " + ex.toString());
        }
    }

    /**
     * Инициализация компонентов: etLogin, etPassword, btnEntry, btnCreateAccount а так-же для работы с БД
     */
    private void initialize() {

        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnEntry = (Button) findViewById(R.id.btnEntry);
        btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
        // иниц. объектов для рабты с КЭШЕМ
        transmiterCash = new TransmitterCash(this); // выполнение создание БД
        login_db$cash = "";
        password_db$cash = "";
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
        setContentView(R.layout.activity_main);

        initialize();
        checkInternet();
        get_db$cash();

        // проверка данных на корректность (кэш)
        if (login_db$cash.equals("") || password_db$cash.equals("") || login_db$cash == null || password_db$cash == null){
            // переход не выполнять
        } else {
            // извлеч из кэша и выполнить переход
            transmiterCash.close();
            sendDateToSecondActivity(login_db$cash, password_db$cash);
        }
    }

    /**
     * Выполнить действие при нажатии назад (показать диалоговое сообщение о выходе)
     */
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        showDialogExit();
    }

    /**
     * Диалоговое окно, о подтверждении выхода
     */
    private void showDialogExit(){

        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
        adb.setTitle("Вы действительно хотите завершить?");

        adb.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        adb.show();
    }

    /**
     * Действия приводимые при нажатии на кнопку "Вход"
     * @param v
     */
    public void goToEntryActivity(View v){

        String login = etLogin.getText().toString();
        String password = etPassword.getText().toString();

        if (login.equals(null) || password.equals(null) || login.equals("") || password.equals("")){
            Toast.makeText(this, "Пожалуйста, заполните поля.", Toast.LENGTH_LONG).show();
        }else {
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            if (Robot.isOnline(getApplicationContext())){ // !Robot.isOnline(getApplicationContext()) проверка на подключение к сети
                Toast.makeText(getApplicationContext(), "Нет соединения с интернетом.",Toast.LENGTH_SHORT).show();
            } else {
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=users&action=get&detalAction=getID&login="+login+"&password="+password;
                httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
                httpRequest.execute();
                Log.d(TEG_MainActivity, "httpREQ = " + httpRequest.getRequest() + ", len = " + httpRequest.getRequest().length());
                // ~~~~~~~~~~~~~~~~~
                int id = -1;
                try {
                    String num = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getArrayList()[0];
                    Log.d(TEG_MainActivity, "strNum = " + num + ", len = " + num.length());
                    id = Integer.parseInt(num);
                    Log.d(TEG_MainActivity, "id = " + String.valueOf(id));
                } catch (Exception ex){
                    id = -1;
                    Log.d(TEG_MainActivity, "Ошибка: " + ex.toString());
                }
                // ~~~~~~~~~~~~~~~~~
                if (id > -1){
                    Log.d(TEG_MainActivity, "Пользователь есть");
                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db = transmiterCash.getWritableDatabase();
                    try {
                        Cursor cursor = db.query("cash", null, null, null, null, null, null);
                        if (cursor.moveToFirst()){
                            // когда в таблице есть строки
                            // тогда выполнить обновление
                            cv.put("login", login);
                            cv.put("password", password);
                            db.update("cash", cv, "id = ?", new String[]{"1"});
                            Log.d(TEG_MainActivity, "--- есть записи");
                            // выполнение перехода
                            transmiterCash.close();
                            sendDateToSecondActivity(login, password);
                        } else {
                            // когда в таблице нет строк
                            Toast.makeText(getApplicationContext(), "В таблице нет строк.", Toast.LENGTH_SHORT).show();
                            cv.put("login", login);
                            cv.put("password", password);
                            db.insert("cash", null, cv);
                            // выполнение перехода
                            transmiterCash.close();
                            sendDateToSecondActivity(login, password);
                        }
                        cursor.close();
                    } catch (Exception ex){
                        Log.d(TEG_MainActivity, "Возникли ошибки в cursor (при добавление новых данных): " + ex.toString());
                    }
                } else {
                    Toast.makeText(this, "Неверно введен логин или пароль", Toast.LENGTH_SHORT).show();
                }
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            }
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        }
    }

    /**
     * Действия приводимые при нажатии на кнопку "Регистрация"
     * @param v
     */
    public void goToRegisterActivity(View v){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    /**
     * Метод для отправки данных на SecondActivity
     * @param login строковый параметр логина
     * @param password строковый параметр пароля
     */
    private void sendDateToSecondActivity(String login, String password){

        Intent intent = new Intent(this, SecondMainActivity.class);
        Log.d(TEG_MainActivity, "login = " + login + " password = " + password);
        try {
            intent.putExtra("myLogin", login);
            intent.putExtra("myPass", password);
        }catch (Exception ex){
            Log.d(TEG_MainActivity, "Невозможно передать данные на активность: " + ex.toString());
        }
        startActivity(intent);
    }
}
