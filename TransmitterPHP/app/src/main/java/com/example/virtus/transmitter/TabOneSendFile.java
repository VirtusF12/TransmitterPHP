package com.example.virtus.transmitter;

import HttpRequest.*;
import PCodeDecodeMessage.CDMessage;
import PFileActivity.FileUploadAsyncTask;
import Parser.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Активити - загрузка файла на сервер
 * (копия находится на E:/public_html)
 */
public final class TabOneSendFile extends Activity implements View.OnClickListener {

    private static final String TEG_TabOneSendFile = "TEG_TabOneSendFile";
    final int CODE_GALARY_REQUES = 999;
    private Button btnFCD, btnSendF;
    private EditText etLoginT;
    private TextView tvSCD;
    private String[] arrUsers;
    private Intent intentFile;
    private String myLogin, myID, loginTo;  // id и login
    String upLoadServerUri = "";            // адрес сервера для приема файла
    String pathFile = "";                   // путь к файлу
    FileUploadAsyncTask fileUploadAsyncTask;
    Handler handler;
    // НОВОЕ
    private ProgressDialog progressDialog;
    private Handler progressBarHandler = new Handler();
    private long fileSize = 0L;
    private int progressStatus = 0;
    private int increm;

    private HttpRequest httpRequest;

    private void getDate(){

        try {

            intentFile = getIntent();
            this.myLogin = intentFile.getStringExtra("myLogin");
            this.myID = intentFile.getStringExtra("myID");

            Log.d(TEG_TabOneSendFile, "Вызван метод getDate() myLogin = " + myLogin + " myID = " + myID);
        } catch (Exception ex){
            Log.d(TEG_TabOneSendFile, "Данные не были переданы ex = " + ex.toString());
        }
    }

    private void initialize() {

        btnFCD = (Button) findViewById(R.id.btnFCD);
        btnSendF = (Button) findViewById(R.id.btnSendF);
        etLoginT = (EditText) findViewById(R.id.etLoginT);
        tvSCD = (TextView) findViewById(R.id.tvSCD);

        arrUsers = getArrUsers();

        if (arrUsers != null){
            Log.d(TEG_TabOneSendFile, "Список пользователей не пуст!");
        }
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_one_send_file);

        getDate();
        initialize();
        // overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        Log.d(TEG_TabOneSendFile, "Мой логин: " + this.myLogin);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    /**
     * @return получение массива пользователей
     */
    private String[] getArrUsers(){

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://195.133.146.149/?part=users&action=get&detalAction=getAllUsers";
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        String[] arrUsers = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getArrayList();
        Log.d(TEG_TabOneSendFile, "arrUsers.length = " + arrUsers.length);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        /*
        final String urlGetListUsers = "http://62.109.19.148:5000/getListUsers";
        UserListThread = new UserListThread(urlGetListUsers);
        UserListThread.start();
        try {
            UserListThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        // return UserListThread.getLoginUsers();
        return arrUsers;
    }

    /**
     * @param login кому отправляете файл
     * @return флаг о наличие такого пользователя кому вы хотите отправить файл
     */
    private boolean isLogin(String login){

        boolean flag = false;

        for (int i = 0; i < arrUsers.length; i++){
            if (login.equals(arrUsers[i])){
                flag = true;
                break;
            }
        }
        return flag;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TEG_TabOneSendFile, "Вызван метод onActivityResult(int requestCode, int resultCode, Intent data)");

        if (requestCode == CODE_GALARY_REQUES && resultCode == RESULT_OK && data != null) {
            Uri filePath = data.getData();
            pathFile = filePath.getPath();
            Log.d(TEG_TabOneSendFile, "Путь к файлу: " + pathFile);

            /**
             * Для отображение пути к файлу
             */
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String currentText = tvSCD.getText().toString();
                    Log.d(TEG_TabOneSendFile, currentText);
                    tvSCD.setText(currentText + " " + pathFile);
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Выполнение загрузки файла на сервер.
     * Загрузка файла происходит асинхронно через FileUploadAsyncTask.
     * @param sourceFileUri адрес куда загружать файл
     * @param context
     */
    private void uploadFile(String sourceFileUri, Context context){

        // настрока диалогового окна загрузки
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Загрузка файла на сервер ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        String fileName = sourceFileUri;
        Log.d(TEG_TabOneSendFile, "file name = " + fileName);
        fileUploadAsyncTask = new FileUploadAsyncTask();
        // fileSize = new File(fileName).length();
        // Log.d(TEG_TabOneSendFile, "размер файла: " + fileSize);
        // fileUploadAsyncTask.setDate((int) fileSize, getApplicationContext());
        fileUploadAsyncTask.execute(fileName, upLoadServerUri);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        progressStatus = 0;  // сброс данных
        fileSize = new File(fileName).length();
        increm = 0;
        // создание потока для загрузки файла
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100){
                    // получение значения в %
                    progressStatus = executeTask();
                    // секундная задержка
                    try{
                        Thread.sleep(5_00);
                    } catch (InterruptedException iex){
                        Log.d("M_TEG", iex.toString());
                    }
                    // обновление прогрессного диалога
                    progressBarHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setProgress(progressStatus);
                        }
                    });
                    // проверка если величина загрузки равна 100
                    // если да тогда делаем паузу и закрываем окно
                    if (progressStatus >= 100){
                        try {
                            Thread.sleep(2_000);
                        } catch (InterruptedException iex){
                            Log.d("M_TEG", iex.toString());
                        }
                        // закрытие окна
                        progressDialog.dismiss();
                    }
                }
            }
        }).start();
    }

    private int executeTask(){

        while (increm <= fileSize){
            increm++;

            if (increm == (int) (fileSize / 90)){
                return 10;
            }else if(increm == (int) (fileSize / 80)){
                return 20;
            }else if(increm == (int) (fileSize / 70)){
                return 30;
            }else if(increm == (int) (fileSize / 60)){
                return 40;
            }else if(increm == (int) (fileSize / 50)){
                return 50;
            }else if(increm == (int) (fileSize / 40)){
                return 60;
            }else if(increm == (int) (fileSize / 30)){
                return 70;
            }else if(increm == (int) (fileSize / 20)){
                return 80;
            }else if(increm == (int) (fileSize / 10)){
                return 90;
            }
        }
        return 100;
    }

    /**
     * @return получение текущей даты
     */
    private String getCurrentTime(){

        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss");
        return formatForDateNow.format(dateNow).toString();
    }

    /**
     * Отправка сообщения пользователю о том что вам прислали файл
     */
    private void sendMessURL(){

        String currentTime = getCurrentTime();
        String message = "ФАЙЛ БЫЛ ОТПРАВЛЕН.";
        Log.d(TEG_TabOneSendFile, "mess = " + message);

        String res_message = this.myLogin + ">~(" + message + ")~("+currentTime+")";
        String newResultMessage = CDMessage.CodeMessage(res_message);
        Log.d(TEG_TabOneSendFile, "Message = (" + newResultMessage + ")");

        String[] arrSplitterMessage = res_message.split("~");
        String name = arrSplitterMessage[0].substring(0, arrSplitterMessage[0].length()-1);
        String mess =  arrSplitterMessage[1].substring(1, arrSplitterMessage[1].length()-1);
        String time =  arrSplitterMessage[2].substring(1, arrSplitterMessage[2].length()-1);
        Log.d(TEG_TabOneSendFile, "метод sendMessage() " + name + " " + mess  + " " + time);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // отправка сообщения на сервер
        final String url = "http://195.133.146.149/?part=messages&action=insert&fromLogin="+myLogin+"&toLogin="+loginTo+"&mess="+newResultMessage;
        httpRequest = new HttpRequest(url, StateRequest.COMMON_REQUEST, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    private boolean isEmptyRowLoginAndPathFile(){

        String loginTo = etLoginT.getText().toString();
        String pathFile = this.pathFile;

        if ( ((loginTo.equals("") || loginTo == null) || (pathFile.equals("") || pathFile == null)) ||
                ((pathFile.equals("") || pathFile == null) || (loginTo.equals("") || loginTo == null)) )
            return true;
        else return false;
    }

    /**
     * Обработчик нажатий на кнопки
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnFCD:
                Log.d(TEG_TabOneSendFile, "Вызван метод onClick(), ветвь R.id.btnFCD");

                Intent intent = new Intent(Intent.ACTION_PICK);
                // intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Выберите файл"), CODE_GALARY_REQUES);
                ;break;

            case R.id.btnSendF:
                Log.d(TEG_TabOneSendFile, "Вызван метод onClick(), ветвь R.id.btnSendF");
                try {
                    if (!isEmptyRowLoginAndPathFile()){

                        loginTo = etLoginT.getText().toString();

                        if (!isLogin(loginTo)){
                            Toast.makeText(this, "Введите правельный логин.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Логин введен верно!", Toast.LENGTH_LONG).show();

                            if (pathFile != null){

                                // получение логина кому отправлять файл
                                upLoadServerUri = "http://195.133.146.149/UploadToServerTest.php/?login="+loginTo;
                                Log.d(TEG_TabOneSendFile, "upLoadServerUri = " + upLoadServerUri);
                                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                                uploadFile(pathFile, view.getContext()); // выполнить загрузку файла
                                Toast.makeText(this, "Файл загружается...", Toast.LENGTH_LONG).show();
                                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                                sendMessURL(); // отправка сообщения пользователю
                                Toast.makeText(this, "Сообщение отправлено", Toast.LENGTH_LONG).show();

                            } else {
                                Log.d(TEG_TabOneSendFile, "pathFile == null");
                            }
                        }
                    } else {
                        Toast.makeText(this, "Обязательное поля к заполнению (путь к файлу, логин получателя)", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Log.d(TEG_TabOneSendFile, "Ошибка");
                    Toast.makeText(this, "Укажите путь к файлу", Toast.LENGTH_LONG).show();
                }
                ;break;

            default:
                Log.d(TEG_TabOneSendFile, "Вызван метод onClick(), ветвь default");
                break;
        }
    }
}