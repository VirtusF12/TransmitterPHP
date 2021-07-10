package com.example.virtus.transmitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import Parser.*;
import HttpRequest.*;
import Robot.Robot;

import java.util.ArrayList;

import static HttpRequest.StateRequest.COMMON_REQUEST;
import static HttpRequest.StateRequest.GET;

/**
 * Активити - список досок (задач)
 * @author Mihail Kovalenko
 */
public final class BoardTaskActivity extends AppCompatActivity {

    private static final String TEG_BoardTaskActivity = "TEG_BoardTaskActivity";
    private Intent intent;
    private String myLogin, name_board;
    ArrayList<String> boards;
    ArrayAdapter<String> adapter;
    private HttpRequest httpRequest; // сеть

    private boolean isNull(String str){

        boolean flag = true;
        for (int i = 0; i < str.length(); i++){
            char simbol = str.charAt(i);
            if (simbol != ' '){
                flag = false;
                return flag;
            }
        }
        return flag;
    }

    /**
     * Диалоговое окно по созданию новой доски с переходом на ManageTaskActivity
     * и переносом параметров curr_board и myLogin
     */
    private void runAlertDialogCreateBoard(){

        LayoutInflater li = LayoutInflater.from(this);
        // получение View элемента от XML файла
        View promptView = li.inflate(R.layout.prompts_board, null);
        // построение настроечного alertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // установка View в alertDialogBuilder
        alertDialogBuilder.setView(promptView);
        // получение элемента с promts
        final EditText etPromt = (EditText) promptView.findViewById(R.id.editTextDialogUserInputBoard);
        // настройка отображения
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String nameBoard = etPromt.getText().toString();

                                if (isNull(nameBoard))
                                    Toast.makeText(getApplicationContext(), "Пожалуйста, введите название доски.", Toast.LENGTH_SHORT).show();
                                else {
                                    name_board = nameBoard.trim();

                                    if (boards.contains(nameBoard)){
                                        Toast.makeText(getApplicationContext(), "Пожалуйста, введите другое название доски.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                                        final String url = "http://195.133.146.149/?part=board_author&action=insert&login="+myLogin+"&nameBoard="+name_board;
                                        // final String URL = "http://62.109.19.148:5000/manageprojectInsertBoard/?login=\""+myLogin+"\"&nameboard=\""+name_board+"\"";
                                        httpRequest = new HttpRequest(url, COMMON_REQUEST, getApplication());
                                        httpRequest.execute();
                                        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                                        boards.add(nameBoard.trim());
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(),"Доска была добавлена: " + nameBoard,Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getApplicationContext(), ManageTaskActivity.class);
                                        intent.putExtra("curr_board", name_board);
                                        intent.putExtra("myLogin", myLogin);
                                        startActivity(intent);
                                    }
                                }
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Диалогое окно на удаление доски
     */
    private void runAlertDialogDeleteBoard(final String nameBoard, final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(BoardTaskActivity.this);
        builder.setTitle("Удаление доски");
        builder.setMessage("Вы действительно хотите удалить доску?");
        builder.setCancelable(false);

        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                final String url = "http://195.133.146.149/?part=board_author&action=delete&nameBoard="+nameBoard;
                // final String URL = "http://62.109.19.148:5000/manageprojectDeleteBoard/?nameboard=\""+nameBoard+"\"";
                httpRequest = new HttpRequest(url, COMMON_REQUEST, getApplicationContext());
                httpRequest.execute();
                // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                boards.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Доска была удалена: " + nameBoard, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toast.makeText(getApplicationContext(), "Нажата NO", Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Получение данных от SecondMainActivity this.myLogin
     */
    private void getDate(){
        try {
            intent = getIntent();
            this.myLogin = intent.getStringExtra("myLogin");
            Log.d(TEG_BoardTaskActivity, "Данные с SecondMainActivity: " + this.myLogin);
        } catch (Exception ex){
            Log.d(TEG_BoardTaskActivity, "Ошибка: " + ex.toString());
        }
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
        setContentView(R.layout.activity_board_task);

        setTitle("Добавление новой доски");
        getDate();
        checkInternet();
        boards = new ArrayList<>();
        final ListView listView = (ListView) findViewById(R.id.listViewBoard);
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://195.133.146.149/?part=board_author&action=get&detalAction=getMyBoard&login="+myLogin;
        httpRequest = new HttpRequest(url, GET, getApplicationContext());
        httpRequest.execute();
        // new Parser().parse(httpRequest.getRequest(), this.boards);   ///// изменить
        // new Parser().filterForMessages(httpRequest.getRequest(), this.boards);
        this.boards = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getList();
        Log.d(TEG_BoardTaskActivity, "кол-во моих досок: " + this.boards.size());
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, boards);
        listView.setAdapter(adapter);

        // выполнение при нажатии на нужную доску
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                name_board = boards.get(i);
                Log.d(TEG_BoardTaskActivity, "curr_board = " + name_board);
                runAlertDialogDeleteBoard(name_board, i);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runAlertDialogCreateBoard();
            }
        });
    }
}
