package com.example.virtus.transmitter;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public final class GroupActivity extends TabActivity {

    private static final String TEG_GroupActivity = "TEG_GroupActivity";
    private String login;

    // инициализация табов
    private void initialize(){

        TabHost tabHost = getTabHost();
        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Создать группу");
        Intent intCreateGroup = new Intent(this, TabOneCreateGroupActivity.class);// таб Создания группы
        intCreateGroup.putExtra("login_u", login);
        tabSpec.setContent(intCreateGroup);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Мои группу");
        Intent intMyGroup = new Intent(this, TabTwoMyGroupActivity.class);// таб Мои группы
        intMyGroup.putExtra("login_u", login);
        tabSpec.setContent(intMyGroup);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("tag1");
    }

    // получение логина
    private void getDate(){
        try {
            Intent intent = getIntent();
            this.login = intent.getStringExtra("myLogin");
            Log.d(TEG_GroupActivity, "Логин: " + this.login);
        } catch (Exception ex){
            Log.d(TEG_GroupActivity, "Ошибка при передаче логина с SecondMainActivity");
        }
    }

    @Override
    protected void onCreate(Bundle setInstanceState){
        super.onCreate(setInstanceState);
        setContentView(R.layout.activity_group);

        getDate();
        initialize();
    }
}
