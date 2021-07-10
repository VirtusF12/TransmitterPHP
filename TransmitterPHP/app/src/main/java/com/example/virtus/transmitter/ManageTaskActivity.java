package com.example.virtus.transmitter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import PCodeDecodeMessage.CDMessage;
import PGroupAllActivity.GroupListThread;
import Parser.*;
import HttpRequest.*;
import Robot.Robot;

import static Parser.FormatTextServer.FORMAT2;

/**
 * Активити - создание задачи для доски
 * @author Mihail Kovalenko
 */
public final class ManageTaskActivity extends AppCompatActivity {

    private static final String TEG_ManageTaskActivity = "TEG_ManageTaskActivity";
    // получение данных от BoardTaskActivity
    private String myLogin, curr_board;
    //////////////////////////////////////
    private String name_board, name_task, date_dialog;
    private ArrayList<String> tasks;
    private ListView listViewTask;
    private ArrayAdapter<String> adapter;
    // компоненты формы
    private Button btnSendTaskN, btnDel;
    // фабы
    private TextView textNFab1, textNFab2, textNFab3, textNFab4;
    private FloatingActionButton fabN1, fabN2, fabN3, fabN4;
    private boolean isVisCompMainFab = true;
    private boolean isVisButtons = true; // показ кнопок (удаление и добавления)
    private String textLableC, textLogin, textTaskG; // метки
    // пользователи // задания
    private boolean flagUserText = true;
    private boolean flagUserTaskText = true;
    FragmentManager fm;
    private ArrayList<FragmentIneTask> frgInTask;
    private ArrayList<FragmentTransaction> frgTransaction;
    private LinearLayout layoutTest, container_date, container_user, container_task;
    private FragmentDate fd; // работа с датой
    int DIALOG_DATE = 1;
    private int year = 2018;
    private int month = 01;
    private int day = 01;
    private int one_date = 1;
    private ArrayList<String> listTasks, listUsers; // список задач и списка пользователей
    private String date_from, date_to;
    // private ButtonTaskSend buttonTaskSend; // удалить класс
    private HttpRequest httpRequest;
    private GroupListThread groupListThread; // поток на получение списка всех групп
    private String nameGroup = ""; // название группы
    private String[] arrGroups; // массив всех групп
    private ProgressDialog pd;
    private int totalProgressBar = 0;

    /**
     * Получение данных от BoardTaskActivity: curr_board, myLogin
     */
    private void getDate(){
        try {
            Intent intent = getIntent();
            this.curr_board = intent.getStringExtra("curr_board");
            this.myLogin = intent.getStringExtra("myLogin");

        } catch (Exception ex){
            Log.d(TEG_ManageTaskActivity, "Ошибка: " + ex.toString());
        }
    }

    /**
     * Инициализация: btnDel, btnSendTaskN, fm    textNFab1...4   fabN1...4   container_date
     */
    private void initialize(){

        btnDel = (Button) findViewById(R.id.btnDel);
        btnSendTaskN = (Button) findViewById(R.id.btnSendTaskN);
        fm = getFragmentManager();

        // иниц меток
        textNFab1 = (TextView) findViewById(R.id.textNFab1);
        textNFab2 = (TextView) findViewById(R.id.textNFab2);
        textNFab3 = (TextView) findViewById(R.id.textNFab3);
        textNFab4 = (TextView) findViewById(R.id.textNFab4);

        // иниц фабов
        fabN1 = (FloatingActionButton) findViewById(R.id.fabN1);
        fabN2 = (FloatingActionButton) findViewById(R.id.fabN2);
        fabN3 = (FloatingActionButton) findViewById(R.id.fabN3);
        fabN4 = (FloatingActionButton) findViewById(R.id.fabN4);

        // контейнеры
        container_date = (LinearLayout) findViewById(R.id.container_date);
        container_user = (LinearLayout) findViewById(R.id.container_user);
        container_task = (LinearLayout) findViewById(R.id.container_task);

        // иниц. списка задач
        listTasks = new ArrayList<>();
        listUsers = new ArrayList<>();

        date_from = "";
        date_to = "";
    }

    private void checkInternet(){
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (!Robot.isOnline(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "Нет соединения с интернетом.",Toast.LENGTH_SHORT).show();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    /**
     * Действия приводимые при нажатии на кнопку назад
     */
    @Override
    public void onBackPressed() {

        Log.d(TEG_ManageTaskActivity, "нажата кнопка назад");
        // написать код по сохранению данных на сервере
        super.onBackPressed();
    }

    /**
     * Выполнение отправки данных на сервер, при нажатии на btnSendTaskN
     */
    private void send_date$onserver(){
        Log.d(TEG_ManageTaskActivity, "\t\tОТПРАВЛЯЕМЫЕ ДАННЫЕ НА СЕРВЕР" + " listUsers.size() = " + listUsers.size() + " listTasks.size() = " + listTasks.size());
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        totalProgressBar = listUsers.size() * listTasks.size();
        Log.d(TEG_ManageTaskActivity, "totalProgressBar = " + totalProgressBar);
        pd = new ProgressDialog(this);
        pd.setTitle("Title");
        // getResources().getString(R.string.executing_task)
        pd.setMessage("Message");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //pd.setProgress(0);
        pd.setMax(totalProgressBar);
        //pd.setIndeterminate(true);
        pd.show();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        new Thread(new Runnable() {
            @Override
            public void run() {

                int counter = 0;

                for (int i = 0; i < listUsers.size(); i++){

                    String author = myLogin;
                    String subject = curr_board;
                    String user = listUsers.get(i);

                    for (int j = 0; j < listTasks.size(); j++){
                        // author, subject, date, user, task_user, exec
                        String task = listTasks.get(j);
                        String codeTask = CDMessage.CodeMessage(task);
                        String date = date_from + "_" + date_to;
                        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                        // выполнить отправку заданий на сервер
                        final String url = "http://195.133.146.149/?part=board_manage_task&action=insert&author="+author+"&subject="+subject+"&date="+date+"&user="+user+"&taskUser="+codeTask+"&exec=0";
                        // final String url$insert = "http://62.109.19.148:5000/manageproject_InsertTask/?author=\""+ author + "\"&subject=\"" + subject + "\"&date=\"" + date + "\"&user=\"" + user + "\"&task_user=\"" + codeTask + "\"";
                        Log.d(TEG_ManageTaskActivity, ":::::::::ссылка = " + url);
                        httpRequest = new HttpRequest(url, StateRequest.COMMON_REQUEST, getApplicationContext());
                        httpRequest.execute();
                        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                        counter++;
                        // pd.incrementProgressBy(counter);
                        pd.setProgress(counter);
                    }
                    Log.d(TEG_ManageTaskActivity, "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
                pd.dismiss();
            }
        }).start();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_task);

        setTitle("Форма создания задач");
        getDate();
        Toast.makeText(this, "NEW: " + this.curr_board + " " + this.myLogin, Toast.LENGTH_SHORT).show(); // проверка какие данные пришли
        initialize();
        checkInternet();
        /**
         * Получение всех групп и заполнение их в массив arrGroups
         */
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://195.133.146.149/?part=group_admin&action=get&detalAction=getListGroups";
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        this.arrGroups  = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getArrayList();
        Log.d(TEG_ManageTaskActivity, "arrGroup.size() = " + arrGroups.length);
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        layoutTest = (LinearLayout) findViewById(R.id.linLayoutTest);
        frgInTask = new ArrayList<>();
        frgTransaction = new ArrayList<>();
        /**
         * Выполнение отправки данных на сервер
         */
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        btnSendTaskN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if (listTasks.size() == 0 || nameGroup.equals("")){ // выполение проверки перед отправкой
                    Toast.makeText(getApplicationContext(), "Заполните поля задач или группу.", Toast.LENGTH_SHORT).show();
               } else {

                    if (date_from.equals("") || date_to.equals("") || date_from == null || date_to == null){
                        Toast.makeText(getApplicationContext(), "Поля даты не заполнены.", Toast.LENGTH_SHORT).show();
                    } else {
                        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                        final String url = "http://195.133.146.149/?part=group_user&action=get&detalAction=getListUser&nameGroup="+nameGroup;
                        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
                        httpRequest.execute();
                        listUsers = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getList();
                        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                        if (listUsers.size() == 0){
                            Toast.makeText(getApplicationContext(), "Данные не могут быть отправлены.(Список пользователей не получен)", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Список получен.", Toast.LENGTH_SHORT).show();
                            send_date$onserver(); // выполнение отправки
                            Toast.makeText(getApplicationContext(), "Данные отправлены на сервер.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        /**
        * Выполнение удаление отмечанной задачи
        */
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction fragmentTransaction;

                for (int i = 0; i < frgInTask.size(); i++){

                    fragmentTransaction = fm.beginTransaction();

                    if (frgInTask.get(i).isPressFlag()){
                        Log.d(TEG_ManageTaskActivity, "индексы: " + i);

                        try {
                            FragmentIneTask fragmTemp = frgInTask.get(i);
                            fragmentTransaction.remove(fragmTemp);
                            fragmentTransaction.commit();
                        } catch (Exception ex){
                            Log.d(TEG_ManageTaskActivity, "Ошибка удаления: " + ex.toString());
                        }
                        frgInTask.remove(i);
                    }
                }

                String result_text = "";
                for (int i = 0; i < frgInTask.size(); i++){
                    result_text += frgInTask.get(i).getIneText() + " ";
                }
                Toast.makeText(getApplicationContext(), result_text, Toast.LENGTH_SHORT).show();
            }
        });
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (savedInstanceState == null){
            Log.d(TEG_ManageTaskActivity, ">(>(>(>(> savedInstanceState == null");
        } else {
            Log.d(TEG_ManageTaskActivity, ">)>)>)>)> savedInstanceState != null");
        }
        // список задач
        tasks = new ArrayList<>();
        listViewTask = (ListView) findViewById(R.id.listViewTask);
        listViewTask.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, tasks);
        listViewTask.setAdapter(adapter);

        // установление невидимости кнопкам
        btnDel.setVisibility(View.INVISIBLE);
        btnSendTaskN.setVisibility(View.INVISIBLE);
        //
        setVisibleFabLable(StateComponent.INVISIBLE_TEXT_VIEW);
        setVisibleFabLable(StateComponent.INVISIBLE_FAB);
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // все фабы
        final FloatingActionButton fabN = (FloatingActionButton) findViewById(R.id.fabN);
        fabN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isVisCompMainFab){

                    layoutTest.setBackgroundColor(Color.WHITE);
                    layoutTest.setVisibility(View.VISIBLE);

                    setVisibleFabLable(StateComponent.VISIBLE);
                    // добавить задачу
                    fabN1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            setVisibleFabLable(StateComponent.INVISIBLE);
                            //runAlertDialogAddListCheck(); // вызв. диал. окна для добавл в чек-лист
                            runAlertDialogAddIneTask();
                            isVisCompMainFab = true;
                            layoutTest.setVisibility(View.INVISIBLE);
                        }
                    });
                    // срок выполнения
                    fabN2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            setVisibleFabLable(StateComponent.INVISIBLE);
                            showDialog(DIALOG_DATE); // запуск диалога для выбора даты
                            isVisCompMainFab = true;
                            layoutTest.setVisibility(View.INVISIBLE);
                        }
                    });
                    // группа
                    fabN3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            setVisibleFabLable(StateComponent.INVISIBLE);
                            runAlertDialogAddUser();
                            isVisCompMainFab = true;
                            layoutTest.setVisibility(View.INVISIBLE);
                        }
                    });
                    // кнопки
                    fabN4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            setVisibleFabLable(StateComponent.INVISIBLE);
                            // runAlertDialogAddLable(); // вызв. диал. окна для добавления метки
                            isVisCompMainFab = true;
                            layoutTest.setVisibility(View.INVISIBLE);

                            if (isVisButtons){
                                btnDel.setVisibility(View.VISIBLE);
                                btnSendTaskN.setVisibility(View.VISIBLE);
                                isVisButtons = false;
                            } else {
                                btnDel.setVisibility(View.INVISIBLE);
                                btnSendTaskN.setVisibility(View.INVISIBLE);
                                isVisButtons = true;
                            }
                        }
                    });

                    isVisCompMainFab = false;

                } else {
                    setVisibleFabLable(StateComponent.INVISIBLE);
                    layoutTest.setVisibility(View.INVISIBLE);
                    isVisCompMainFab = true;
                }
            }
        });
    }

    /**
     * Метод по установки видимости фабов и подписей к ним
     * @param sc параметр перечисления (StateComponent) по установки состояния
     */
    private void setVisibleFabLable(StateComponent sc){

        switch(sc){
            case VISIBLE:
                fabN1.setVisibility(View.VISIBLE);
                fabN2.setVisibility(View.VISIBLE);
                fabN3.setVisibility(View.VISIBLE);
                fabN4.setVisibility(View.VISIBLE);

                textNFab1.setVisibility(View.VISIBLE);
                textNFab2.setVisibility(View.VISIBLE);
                textNFab3.setVisibility(View.VISIBLE);
                textNFab4.setVisibility(View.VISIBLE);
                ;break;

            case INVISIBLE:
                fabN1.setVisibility(View.INVISIBLE);
                fabN2.setVisibility(View.INVISIBLE);
                fabN3.setVisibility(View.INVISIBLE);
                fabN4.setVisibility(View.INVISIBLE);

                textNFab1.setVisibility(View.INVISIBLE);
                textNFab2.setVisibility(View.INVISIBLE);
                textNFab3.setVisibility(View.INVISIBLE);
                textNFab4.setVisibility(View.INVISIBLE);
                ;break;

            case VISIBLE_FAB: ; break;
            case INVISIBLE_FAB:
                fabN1.setVisibility(View.INVISIBLE);
                fabN2.setVisibility(View.INVISIBLE);
                fabN3.setVisibility(View.INVISIBLE);
                fabN4.setVisibility(View.INVISIBLE);
                ;break;


            case VISIBLE_TEXT_VIEW:
                textNFab1.setVisibility(View.VISIBLE);
                textNFab2.setVisibility(View.VISIBLE);
                textNFab3.setVisibility(View.VISIBLE);
                textNFab4.setVisibility(View.VISIBLE);
                ; break;
            case INVISIBLE_TEXT_VIEW:
                textNFab1.setVisibility(View.INVISIBLE);
                textNFab2.setVisibility(View.INVISIBLE);
                textNFab3.setVisibility(View.INVISIBLE);
                textNFab4.setVisibility(View.INVISIBLE);
                ;break;
        }
    }

    /**
     * Функция проверки строки на пробельный символ
     * @param str строка для проверки, на наличия в ней пробела
     * @return boolean значение true если в строке есть пробел
     */
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
     * Диалоговое окно при добавлении нового задания
     */
    private void runAlertDialogAddIneTask(){
        LayoutInflater li = LayoutInflater.from(this);
        // получение View элемента от XML файла
        View promptView = li.inflate(R.layout.prompts_ine_task, null);
        // построение настроечного alertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // установка View в alertDialogBuilder
        alertDialogBuilder.setView(promptView);
        // получение элемента с promts
        final EditText etPromtN = (EditText) promptView.findViewById(R.id.etDUIITask);
        // настройка отображения
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String textTask = etPromtN.getText().toString();

                                if (isNull(textTask))
                                    Toast.makeText(getApplicationContext(), "Пожалуйста, введите название задания.", Toast.LENGTH_SHORT).show();
                                else {
                                    textTaskG = textTask.trim();
                                    listTasks.add(textTask.trim()); // запись в список задач
                                    Log.d(TEG_ManageTaskActivity, "задача: " +  textTask.trim() + " был добавлен (" + textTask.trim().length() + ")");

                                    container_task.setBackground(getResources().getDrawable(R.drawable.reg_button));
                                    // создание фрагмента для задния текста
                                    FragmentIneTaskText ft = new FragmentIneTaskText();
//                                    if (flagUserTaskText){
//                                        ft = new FragmentIneTaskText();
//                                    }
                                    // создание фрагмента
                                    FragmentIneTask fragment = new FragmentIneTask();
                                    fragment.setIneTask(textTaskG);
                                    frgInTask.add(fragment);

                                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                    if (flagUserTaskText){
                                        fragmentTransaction.add(R.id.container_task, ft);
                                    }
                                    fragmentTransaction.add(R.id.container_task, fragment);
                                    fragmentTransaction.commit();
                                    //frgTransaction.add(fragmentTransaction);
                                    flagUserTaskText = false;
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

    // вызовется при добавлении нового участника
    /**
     * Диалоговое окно при добавлении группы (учасникам которой, будут разосланы задания)
     */
    private void runAlertDialogAddUser(){
        LayoutInflater li = LayoutInflater.from(this);
        // получение View элемента от XML файла
        View promptView = li.inflate(R.layout.prompts_user, null);
        // построение настроечного alertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // установка View в alertDialogBuilder
        alertDialogBuilder.setView(promptView);
        // получение элемента с promts
        final EditText etPromtN = (EditText) promptView.findViewById(R.id.etDUIUser);
        // настройка отображения
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String textGroup = etPromtN.getText().toString();

                                if (isNull(textGroup))
                                    Toast.makeText(getApplicationContext(), "Пожалуйста, введите название группы", Toast.LENGTH_LONG).show();
                                else {

                                    if (nameGroup.equals("")){

                                        ArrayList<String> listGroup = new ArrayList<String>();
                                        for (String g : arrGroups)
                                            listGroup.add(g);
                                        if (listGroup.contains(textGroup)){
                                            container_user.setBackground(getResources().getDrawable(R.drawable.reg_button));
                                            // создание фрагмента
                                            FragmentUser fu = new FragmentUser();
                                            nameGroup = textGroup;
                                            fu.setLogin(nameGroup);
                                            fu.setFlag(flagUserText);
                                            flagUserText = false;
                                            // определение в какой контейнер добавлять
                                            FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                            fragmentTransaction.add(R.id.container_user, fu);
                                            fragmentTransaction.commit();

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Такой группы нет", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Группа уже добавлена", Toast.LENGTH_SHORT).show();
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

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG_DATE){
            Log.d(TEG_ManageTaskActivity, "id = 1");
            DatePickerDialog dpd = new DatePickerDialog(this, onDateSetListener, year, month, day);
            return dpd;
        }
        return super.onCreateDialog(id);
    }

    /**
     * Диалоговое окно по установке даты
     */
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

            year = i;
            month = i1;
            day = i2;

            if (one_date == 1){

                container_date.setBackground(getResources().getDrawable(R.drawable.reg_button));

                fd = new FragmentDate(); // создание фрагмента
                fd.setDay(day);
                fd.setMonth(month);
                fd.setYear(year);

                date_from = String.valueOf(day) + ":" + String.valueOf(month) + ":" + String.valueOf(year);
                // определение в какой контейнер добавлять
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.add(R.id.container_date, fd);
                fragmentTransaction.commit();

                one_date = 0;
            } else {
                Log.d(TEG_ManageTaskActivity,"^^^ новый фрагмент даты 2 раз создан быть не может");

                container_date.setBackground(getResources().getDrawable(R.drawable.reg_button));

                fd.setDay(day);
                fd.setMonth(month);
                fd.setYear(year);

                date_to = String.valueOf(day) + ":" + String.valueOf(month) + ":" + String.valueOf(year);
                fd.setDate(day + ":" + month + ":" + year);
                Log.d(TEG_ManageTaskActivity,"^^^ установка новых данных");

            }

            Log.d(TEG_ManageTaskActivity, year + " " + month + " " + day);
        }
    };
}