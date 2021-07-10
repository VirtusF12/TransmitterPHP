package com.example.virtus.transmitter;

import HttpRequest.*;
import Parser.*;
import Robot.*;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.util.ArrayList;

/**
 * Активити - список всех групп с автодополнением текста для поиска
 * @author Mihail Kovalenko
 */
public final class GroupAllActivity extends AppCompatActivity {

    private static final String TEG_GroupAllActivity = "TEG_GroupAllActivity";
    private String login_from;
    private ListView lvListAllGroup;
    private EditText etSearchGroup;
    private String[] arrGroups;
    private ArrayList<String> list_myGroup;
    private  ArrayAdapter<String> adapter;
    private String currChLogin;
    private String passChooseGroup;
    private Context context = this;
    private Intent intentGoToMessageGroupActivity;
    private HttpRequest httpRequest;

    /**
     * Класс для автодополнения текста
     */
    class TextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            adapter.getFilter().filter(charSequence);
            Log.d(TEG_GroupAllActivity, "i = " + i + ", i1 = " + i1 + ", i2 = " + i2);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void setActionBar(){

        try {
            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setTitle("Все группы");
            setSupportActionBar(toolbar);

            // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ex){
            Log.d(TEG_GroupAllActivity, "Ошибка UserActivity ActionBar: " + ex.toString());
        }
    }

    private void getDate(){
        try {
            Intent getIn = getIntent();
            login_from = getIn.getStringExtra("myLogin");
            Log.d(TEG_GroupAllActivity,"Полученные данные: " + login_from);
        }  catch (Exception ex){
            Log.d(TEG_GroupAllActivity, "Данные не получены UserActivity: " + ex.toString() );
        }
    }

    private void initialize() {

        lvListAllGroup = (ListView) findViewById(R.id.lvListAllGroup);
        etSearchGroup = (EditText) findViewById(R.id.etSearchGroup);
        list_myGroup = new ArrayList<>();
        etSearchGroup.addTextChangedListener(new TextChangeListener());
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
        setContentView(R.layout.activity_list_group);

        setActionBar();
        getDate();
        initialize();
        checkInternet();

        getGroups$server();

        if (arrGroups != null){
            Log.d(TEG_GroupAllActivity, "arrGroups != null");
            adapter = new ArrayAdapter(this, R.layout.my_list_item_groups, arrGroups);
            lvListAllGroup.setAdapter(adapter);
            listenerOptionMenuSimpleAdapter(lvListAllGroup);
        }else {
            Log.d(TEG_GroupAllActivity, "arrGroups == null");
        }

        //overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        //setAnimationToEditText(etSearchGroup);
    }

    private void runAlertDialogText (){

        getMyGroup$server();

        LayoutInflater li = LayoutInflater.from(context);
        // получение View элемента от XML файла
        View promptView = li.inflate(R.layout.prompts, null);
        // построение настроечного alertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // установка View в alertDialogBuilder
        alertDialogBuilder.setView(promptView);
        // получение элемента с promts
        final EditText etPromt = (EditText) promptView.findViewById(R.id.editTextDialogUserInput);
        // настройка отображения
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String youEnterPass = etPromt.getText().toString();
                                Log.d(TEG_GroupAllActivity, "Вы вели пароль от группы " + youEnterPass);

                                if (youEnterPass.equals(passChooseGroup)){

                                    Toast.makeText(context, "Вы ввели верный пароль от группы", Toast.LENGTH_LONG).show();

                                    if (!isExistsThisGroupInMyGroups()){

                                        addGroupToMyList(login_from, currChLogin); // добавить в список моих групп
                                        Log.d(TEG_GroupAllActivity, "---> добавить");
                                        // перейти на активити переписки
                                        intentGoToMessageGroupActivity = new Intent(context, MessageGroupActivity.class);
                                        intentGoToMessageGroupActivity.putExtra("login_from", login_from);
                                        intentGoToMessageGroupActivity.putExtra("name_group_to", currChLogin);
                                        startActivity(intentGoToMessageGroupActivity);

                                    } else {

                                        Log.d(TEG_GroupAllActivity, "---> уже добавлена");
                                        intentGoToMessageGroupActivity = new Intent(context, MessageGroupActivity.class);
                                        intentGoToMessageGroupActivity.putExtra("login_from", login_from);
                                        intentGoToMessageGroupActivity.putExtra("name_group_to", currChLogin);
                                        startActivity(intentGoToMessageGroupActivity);
                                    }

                                } else {
                                    Toast.makeText(context, "Вы ввели НЕ верный пароль от группы", Toast.LENGTH_LONG).show();
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

    private void addGroupToMyList(String login, String nameGroup){

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=group_user&action=insert&login="+login+"&nameGroup="+nameGroup;
        httpRequest = new HttpRequest(url, StateRequest.COMMON_REQUEST, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    private boolean isExistsThisGroupInMyGroups(){

        if (list_myGroup.contains(currChLogin)){
            Log.d(TEG_GroupAllActivity, "вызван isExistsThisGroupInMyGroups = эта группа уже есть в моем списке");
            return true;
        } else
            return false;
    }

    private void getMyGroup$server(){

        this.list_myGroup.clear();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=group_user&action=get&detalAction=getListMyGroups&login="+this.login_from;
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        this.list_myGroup = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getList();
        Log.d(TEG_GroupAllActivity, "getAllMyGroup$server() _ this.list_myGroup.size() = " + this.list_myGroup.size());
    }

    private void getGroups$server(){
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=group_admin&action=get&detalAction=getListGroups";
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        arrGroups = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getArrayList();
        Log.d(TEG_GroupAllActivity, "arrGroup.size() = " + arrGroups.length);
    }

    private String getPassGroup(String nameGroup){

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://"+VAR.IP_SERVER_GLOBAL+"/index.php?part=group_admin&action=get&detalAction=getPassGroup&nameGroup="+nameGroup;
        httpRequest = new HttpRequest(url, StateRequest.GET, getApplicationContext());
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        String pass = new Parser(httpRequest.getRequest(), FormatTextServer.FORMAT5).getArrayList()[0];
        Log.d(TEG_GroupAllActivity, "пароль: " + pass);

        return pass;
    }

    /**
     * Действие при нажатии на группу
     * @param listView
     */
    private void listenerOptionMenuSimpleAdapter(ListView listView){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String login = (((TextView)view).getText()).toString();
                currChLogin = login;
                Log.d(TEG_GroupAllActivity, "Вы выбрали группу: " + currChLogin + " len = " + currChLogin.length());

                String passFromServer = getPassGroup(currChLogin);
                passChooseGroup = passFromServer;
                Log.d(TEG_GroupAllActivity, "Пароль группы которую Вы выбрали: " + passChooseGroup + " len = " + passChooseGroup.length());

                runAlertDialogText();
            }
        });
    }

    private void setAnimationToEditText(EditText etSearchUsers){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.my_rotate);
        etSearchUsers.setAnimation(animation);
    }

}
