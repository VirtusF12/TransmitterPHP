package com.example.virtus.transmitter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.util.ArrayList;

import GPSPosition.GPS$Position;
import HttpRequest.*;
// import PSecondMainActivity.SListChanelThread;
import Parser.Parser;
import Robot.*;

public final class SecondMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TEG_SecondMainActivity = "TEG_SecondMainActivity";
    private final int DIALOG_VERSION = 1;
    private final int DIALOG_INFORMATION = 2;
    private String myLogin, myPass;
    private ArrayList<String> list_chanel, newListWithoutC;
    private ListView listView;                 // список card
    private CardArrayAdapter cardArrayAdapter; // card adapter
    private boolean defaultChat = false; // новое добавление
    private TransmitterCash transmiterCash; // работа с кэшем
    private LinearLayout layoutTest; // для оформления
    private TextView textSFab1, textSFab2, textSFab3;
    private FloatingActionButton fabS1, fabS2, fabS3;
    private boolean isVisCompMainFab = true;
    GPS$Position gps$Position;
    private FloatingActionButton fab;

    private HttpRequest httpRequest;
    // было
    //private ListView listViewChanel;
    //private ArrayAdapter<String> adapter;

    private void setVisibleFabLable(StateComponent sc){

        switch(sc){
            case VISIBLE:
                fabS1.setVisibility(View.VISIBLE);
                fabS2.setVisibility(View.VISIBLE);
                fabS3.setVisibility(View.VISIBLE);

                textSFab1.setVisibility(View.VISIBLE);
                textSFab2.setVisibility(View.VISIBLE);
                textSFab3.setVisibility(View.VISIBLE);
                ;break;

            case INVISIBLE:
                fabS1.setVisibility(View.INVISIBLE);
                fabS2.setVisibility(View.INVISIBLE);
                fabS3.setVisibility(View.INVISIBLE);

                textSFab1.setVisibility(View.INVISIBLE);
                textSFab2.setVisibility(View.INVISIBLE);
                textSFab3.setVisibility(View.INVISIBLE);
                ;break;


            case VISIBLE_FAB: ; break;
            case INVISIBLE_FAB:
                fabS1.setVisibility(View.INVISIBLE);
                fabS2.setVisibility(View.INVISIBLE);
                fabS3.setVisibility(View.INVISIBLE);
                ;break;


            case VISIBLE_TEXT_VIEW:
                textSFab1.setVisibility(View.VISIBLE);
                textSFab2.setVisibility(View.VISIBLE);
                textSFab3.setVisibility(View.VISIBLE);
                ; break;
            case INVISIBLE_TEXT_VIEW:
                textSFab1.setVisibility(View.INVISIBLE);
                textSFab2.setVisibility(View.INVISIBLE);
                textSFab3.setVisibility(View.INVISIBLE);
                ;break;
        }
    }

    private void getDate(){

        try {
            Intent mIntent = getIntent();
            myLogin = mIntent.getStringExtra("myLogin");
            myPass = mIntent.getStringExtra("myPass");
            Log.d(TEG_SecondMainActivity, "Полученные данные: login = " + myLogin + " password = " + myPass);
        } catch (Exception ex){
            Log.d(TEG_SecondMainActivity, "Error (невозможно принять данные): " + ex.toString());
        }
    }

    private void initialize(){

        listView = (ListView) findViewById(R.id.listViewChanel);

        listView.addHeaderView(new View(this));
        listView.addFooterView(new View(this));

        // listViewChanel = (ListView) findViewById(R.id.listViewChanel);
        list_chanel = new ArrayList<>();

        // иниц. объектов для рабты с КЭШЕМ
        transmiterCash = new TransmitterCash(this); // выполнение создание БД
        //////////////////////////////////////////////////////////////////////
        // иниц меток
        textSFab1 = (TextView) findViewById(R.id.textSFab1);
        textSFab2 = (TextView) findViewById(R.id.textSFab2);
        textSFab3 = (TextView) findViewById(R.id.textSFab3);

        textSFab1.setTextColor(Color.BLACK);
        textSFab2.setTextColor(Color.BLACK);
        textSFab3.setTextColor(Color.BLACK);

        // иниц фабов
        fabS1 = (FloatingActionButton) findViewById(R.id.fabS1);
        fabS2 = (FloatingActionButton) findViewById(R.id.fabS2);
        fabS3 = (FloatingActionButton) findViewById(R.id.fabS3);
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
        setContentView(R.layout.activity_second_main);

        getDate();
        initialize();
        checkInternet();

        layoutTest = (LinearLayout) findViewById(R.id.linLayoutTest1);
        layoutTest.setVisibility(View.INVISIBLE);
        gps$Position = new GPS$Position(this); // определение моих координат

        showListUserFromTo();

        if (list_chanel.size() == 0)
            showDialog(DIALOG_INFORMATION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(31083);
        setSupportActionBar(toolbar);

        setVisibleFabLable(StateComponent.INVISIBLE_TEXT_VIEW);
        setVisibleFabLable(StateComponent.INVISIBLE_FAB);

        setFloatingBar();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTextOnNavHeaderSecondMain(navigationView, "Добро пожаловать", myLogin);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setAnimationToFloatingBar(fab);
        // listenerOptionMenu(listViewChanel);
    }


    private void setAnimationToFloatingBar(FloatingActionButton fab){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.my_rotate);
        fab.setAnimation(animation);
    }

    private void setTextOnNavHeaderSecondMain(NavigationView navigationView, final String headOneTitle, final String headTwoTitle){

        View viewHeadOneTitle = navigationView.getHeaderView(0);
        TextView textViewHeadOneTitle = (TextView) viewHeadOneTitle.findViewById(R.id.tvHeadOneTitle);
        textViewHeadOneTitle.setText(headOneTitle);

        View viewHeadTwoTitle = navigationView.getHeaderView(0);
        TextView textViewHeadTwoTitle = (TextView) viewHeadTwoTitle.findViewById(R.id.tvHeadTwoTitle);
        textViewHeadTwoTitle.setText(headTwoTitle);
    }

    private void setFloatingBar(){

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isVisCompMainFab){
                    // layoutTest.setBackgroundColor(Color.WHITE);
                    layoutTest.setVisibility(View.VISIBLE);
                    setVisibleFabLable(StateComponent.VISIBLE);
                    // группы
                    fabS1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            setVisibleFabLable(StateComponent.INVISIBLE);
                            Toast.makeText(SecondMainActivity.this, "Группы", Toast.LENGTH_SHORT).show();

                            // переход на активити группы
                            Intent intentNew = new Intent(getApplicationContext(), GroupActivity.class);
                            Log.d(TEG_SecondMainActivity, myLogin);
                            intentNew.putExtra("myLogin", myLogin);
                            startActivity(intentNew);

                            isVisCompMainFab = true;
                            layoutTest.setVisibility(View.INVISIBLE);

                        }
                    });
                    // пользователи
                    fabS2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setVisibleFabLable(StateComponent.INVISIBLE);
                            Toast.makeText(SecondMainActivity.this, "Пользователи", Toast.LENGTH_SHORT).show();

                            // переход на активити пользователей
                            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                            intent.putExtra("myLogin", myLogin);
                            startActivity(intent);

                            isVisCompMainFab = true;
                            layoutTest.setVisibility(View.INVISIBLE);
                        }
                    });
                    // мои координаты
                    fabS3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setVisibleFabLable(StateComponent.INVISIBLE);
                            Toast.makeText(SecondMainActivity.this, "Мои координаты: " + gps$Position.getLongitude() + " " + gps$Position.getLattitude(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            intent.putExtra("lat", gps$Position.getLattitude());
                            intent.putExtra("lon", gps$Position.getLongitude());
                            startActivity(intent);

                            isVisCompMainFab = true;
                            layoutTest.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TEG_SecondMainActivity, "Вызван onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TEG_SecondMainActivity, "Вызван onResume()");
        showListUserFromTo();

        if (list_chanel.size() == 0)
            showDialog(DIALOG_INFORMATION);
    }

    private ArrayList<String> newListWithoutChar(ArrayList<String> list_chanel){

        ArrayList<String> newList = new ArrayList<>();
        for (String chanel : list_chanel)
            newList.add(chanel.replace("`", " "));

        return newList;
    }

    private void showListUserFromTo(){

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // получение списка каналов
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=chanel&action=get&detalAction=getMyChanel&login="+myLogin;
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        String request = httpRequest.getRequest();
        new Parser().parseChanelGroup(request, list_chanel);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        newListWithoutC = newListWithoutChar(list_chanel);
        /////////////////////////////////////////////////////////////
        cardArrayAdapter = new CardArrayAdapter(getApplicationContext(), R.layout.list_item_card);

        for (int i = 0; i < newListWithoutC.size(); i++) {
            Card card = new Card(newListWithoutC.get(i));
            cardArrayAdapter.add(card);
        }
        listView.setAdapter(cardArrayAdapter);
        listenerCardAdapter(listView);
    }

    private void clear_cash(){
        //////////////////////////////////////////////////////////
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = transmiterCash.getWritableDatabase();

        try {
            Cursor cursor = db.query("cash", null, null, null, null, null, null);

            if (cursor.moveToFirst()){
                // когда в таблице есть строки
                // тогда выполнить обновление
                cv.put("login", "");
                cv.put("password", "");
                db.update("cash", cv, "id = ?", new String[]{"1"});

                Log.d(TEG_SecondMainActivity, "--- есть записи");
                // выполнение перехода
                transmiterCash.close();
            } else {
                // когда в таблице нет строк
            }
            cursor.close();

        } catch (Exception ex){
            Log.d(TEG_SecondMainActivity, "Возникли ошибки в cursor (при добавление новых данных): " + ex.toString());
        }
        /////////////////////////////////////////////////////////
    }

    @Override
    public void onBackPressed() {

        clear_cash(); // очистить кэш

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.d(TEG_SecondMainActivity, "Вызван onBackPressed()");
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second_main, menu);
        Log.d(TEG_SecondMainActivity, "Вызван onCreateOptionsMenu()");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.menuMyGroups:
                Log.d(TEG_SecondMainActivity, "Вызван onOptionsItemSelected() -> menuMyGroups");
                Intent intentNew = new Intent(this, TabTwoMyGroupActivity.class);
                intentNew.putExtra("login_u", myLogin);
                startActivity(intentNew);
                return true;

            case R.id.menuSettings:
                Log.d(TEG_SecondMainActivity, "Вызван onOptionsItemSelected() -> menuSettings");
                startActivity(new Intent(this, TestDelete.class));
                return true;

            case R.id.menuAboutProg:
                Log.d(TEG_SecondMainActivity, "Вызван onOptionsItemSelected() -> menuAboutProg");
                showDialog(DIALOG_VERSION);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getVersionTransmitter(){
        PackageManager manager = this.getPackageManager();
        int versionCode = 0;
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String packageName = info.packageName;
            int vCode = info.versionCode;
            String versionName = info.versionName;

            versionCode = vCode;
        } catch (Exception e) {
            Log.d(TEG_SecondMainActivity, "Ошибка getVersionTransmitter(): " + e.toString());
        }
        return versionCode;
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG_VERSION){

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Информация");

            int verCode = getVersionTransmitter();
            adb.setMessage("Версия программы " + verCode);
            adb.setIcon(android.R.drawable.ic_dialog_info);
            adb.setPositiveButton("Ok", myClickListener);

            return adb.create();
        }

        if (id == DIALOG_INFORMATION){

            AlertDialog.Builder adbInfo = new AlertDialog.Builder(this);
            adbInfo.setTitle("Нет приватных каналов");
            adbInfo.setMessage("Для добавления, перейдите к списку всех пользователей и выберите нужный");
            adbInfo.setIcon(android.R.drawable.ic_dialog_info);
            // кнопка положительного ответа
            adbInfo.setPositiveButton("Ok", myClickListenerInfo);

            return adbInfo.create();
        }
        return super.onCreateDialog(id);
    }
    DialogInterface.OnClickListener myClickListenerInfo = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    break;
            }
        }
    };

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    break;
            }
        }
    };

    /**
     * Навигационное меню слева
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        Log.d(TEG_SecondMainActivity, "Вызван onNavigationItemSelected() idItem = " + id);

        if (id == R.id.nav_file) {

            Intent intentFile = new Intent(this, NFileActivity.class);// переход на активики файл
            intentFile.putExtra("myLogin", myLogin);
            startActivity(intentFile);

        } /*else if (id == R.id.nav_list_task) {

            Intent intentExpandableList = new Intent(this, ExpandableListActivity.class);// переход на активики управление проектами (тестирование)
            intentExpandableList.putExtra("myLogin", myLogin);
            startActivity(intentExpandableList);

        } else if (id == R.id.nav_list_manage_task) {

            Intent intentBoardTask = new Intent(this, BoardTaskActivity.class);// переход на активики управление проектами (тестирование)
            intentBoardTask.putExtra("myLogin", myLogin);
            startActivity(intentBoardTask);

        }*/  else if (id == R.id.nav_group) {

            Intent intentNew = new Intent(this, GroupActivity.class);// переход на активити группы
            Log.d(TEG_SecondMainActivity, this.myLogin);
            intentNew.putExtra("myLogin", myLogin);
            startActivity(intentNew);

        } else if (id == R.id.nav_list_group) {

            Intent intentNew = new Intent(this, GroupAllActivity.class);// переход на активити списка всех групп
            Log.d(TEG_SecondMainActivity, this.myLogin);
            intentNew.putExtra("myLogin", myLogin);
            startActivity(intentNew);

        } else if (id == R.id.nav_exit) {

            clear_cash(); // очистка кэша
            Intent intent = new Intent(this, MainActivity.class);// выход на главное активити
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_list_user) {

            Intent intent = new Intent(this, UserActivity.class);// показ всех пользователей
            intent.putExtra("myLogin", myLogin);
            startActivity(intent);

        } /*else if (id == R.id.nav_statistica) {

            Intent intent = new Intent(this, GraphActivity.class);// показ всех пользователей
            intent.putExtra("myLogin", myLogin);
            startActivity(intent);

        } */else if (id == R.id.nav_setting) {

           chooseChat(); // переход на активити с настройками

        } else if (id == R.id.nav_list_mygroup) {
            // MyGroupActivity
            //Intent intMyGroup = new Intent(this, TabTwoMyGroupActivity.class);
            Intent intMyGroup = new Intent(this, MyGroupActivity.class);// переход на список моих групп
            intMyGroup.putExtra("login_u", myLogin);
            startActivity(intMyGroup);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Диалоговое окно по выбору варианта чата
     */
    private void chooseChat(){

        AlertDialog.Builder adb = new AlertDialog.Builder(SecondMainActivity.this);
        adb.setTitle("Перейти к старой версии оформления?");

        adb.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                defaultChat = false;
            }
        });

        adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                defaultChat = true;
            }
        });
        adb.show();
    }

    // новая релизация
    private void listenerCardAdapter(ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // добавлено новое
                //////////////////
                // chooseChat();
                //////////////////
                Log.d(TEG_SecondMainActivity, defaultChat ? "стандартный чат" : "bubble chat");

                if (defaultChat) {
                    int position = i - 1;
                    Card card = cardArrayAdapter.getItem(position);
                    // переход на чат по умолчанию
                    // newListWithoutC.get(i)
                    String nameChanel = card.getLine1();
                    String[] names = nameChanel.split(" ");
                    //String nameChanel = list_chanel.get(i);
                    //String[] names = nameChanel.split("`");
                    String name1 = names[0];
                    String name2 = names[1];
                    Log.d(TEG_SecondMainActivity, name1  + " " + name2);
                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);

                    if (myLogin.equals(name1)){
                        Log.d(TEG_SecondMainActivity, "myLogin == name1");
                        intent.putExtra("login_to", name2);
                        intent.putExtra("login_from", name1);
                    } else {
                        intent.putExtra("login_from", name2);
                        intent.putExtra("login_to", name1);
                    }
                    startActivity(intent);

                } else {
                    int position = i - 1;
                    Card card = cardArrayAdapter.getItem(position);
                    // переход на Bubble Chat
                    // newListWithoutC.get(i)
                    String nameChanel = card.getLine1();
                    String[] names = nameChanel.split(" ");
                    //String nameChanel = list_chanel.get(i);
                    //String[] names = nameChanel.split("`");
                    String name1 = names[0];
                    String name2 = names[1];
                    Log.d(TEG_SecondMainActivity, name1  + " " + name2);
                    Intent intent = new Intent(getApplicationContext(), BubbleChatActivity.class);

                    if (myLogin.equals(name1)){
                        Log.d(TEG_SecondMainActivity, "myLogin == name1");
                        intent.putExtra("login_to", name2);
                        intent.putExtra("login_from", name1);
                    } else {
                        intent.putExtra("login_from", name2);
                        intent.putExtra("login_to", name1);
                    }
                    startActivity(intent);
                }
            }
        });
    }

    // старая реализация
    private void listenerOptionMenu(ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // добавлено новое
                //////////////////
                // chooseChat();
                //////////////////
                Log.d(TEG_SecondMainActivity, defaultChat ? "стандартный чат" : "bubble chat");

                if (defaultChat) {
                    // переход на чат по умолчанию
                    String nameChanel = newListWithoutC.get(i);
                    String[] names = nameChanel.split(" ");
                    //String nameChanel = list_chanel.get(i);
                    //String[] names = nameChanel.split("`");
                    String name1 = names[0];
                    String name2 = names[1];
                    Log.d(TEG_SecondMainActivity, name1  + " " + name2);
                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);

                    if (myLogin.equals(name1)){
                        Log.d(TEG_SecondMainActivity, "myLogin == name1");
                        intent.putExtra("login_to", name2);
                        intent.putExtra("login_from", name1);
                    } else {
                        intent.putExtra("login_from", name2);
                        intent.putExtra("login_to", name1);
                    }
                    startActivity(intent);

                } else {
                    // переход на Bubble Chat
                    String nameChanel = newListWithoutC.get(i);
                    String[] names = nameChanel.split(" ");
                    //String nameChanel = list_chanel.get(i);
                    //String[] names = nameChanel.split("`");
                    String name1 = names[0];
                    String name2 = names[1];
                    Log.d(TEG_SecondMainActivity, name1  + " " + name2);
                    Intent intent = new Intent(getApplicationContext(), BubbleChatActivity.class);

                    if (myLogin.equals(name1)){
                        Log.d(TEG_SecondMainActivity, "myLogin == name1");
                        intent.putExtra("login_to", name2);
                        intent.putExtra("login_from", name1);
                    } else {
                        intent.putExtra("login_from", name2);
                        intent.putExtra("login_to", name1);
                    }
                    startActivity(intent);
                }
            }
        });
    }
}
