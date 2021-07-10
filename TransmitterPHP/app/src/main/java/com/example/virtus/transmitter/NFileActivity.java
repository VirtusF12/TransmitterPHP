package com.example.virtus.transmitter;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

import Robot.Robot;

public final class NFileActivity extends TabActivity{


    private static final String TEG_NFileActivity = "TEG_NFileActivity";
    private Intent intent;
    private String myLogin, myID;

    private void getDate(){
        try{
            intent = getIntent();
            this.myLogin = intent.getStringExtra("myLogin");
            this.myID = intent.getStringExtra("myID");
            Log.d(TEG_NFileActivity, "Логин: " + this.myLogin + " id = " + this.myID);
        }catch (Exception ex){
            Log.d(TEG_NFileActivity, "Ошибка при передаче логина с SecondMainActivity");
        }
    }

    private void initialize() {

        TabHost tabHost = getTabHost();
        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("n_tag1");
        tabSpec.setIndicator("Отправить файл");
        // передача параметров на TabOneSendFile
        Intent intentSendFile = new Intent(this, TabOneSendFile.class);
        intentSendFile.putExtra("myLogin", this.myLogin);
        intentSendFile.putExtra("myID", this.myID);
        tabSpec.setContent(intentSendFile);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("n_tag2");
        tabSpec.setIndicator("Мои файлы");
        // передача параметров на TabTwoListFile
        Intent intentShowList = new Intent(this, TabTwoListFile.class);
        intentShowList.putExtra("myLogin", this.myLogin);
        intentShowList.putExtra("myID", this.myID);
        tabSpec.setContent(intentShowList);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("tag1");
    }

    private void checkInternet(){
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (!Robot.isOnline(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "Нет соединения с интернетом.",Toast.LENGTH_SHORT).show();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    @Override
    protected void onPostCreate(Bundle icicle) {
        super.onPostCreate(icicle);
        setContentView(R.layout.activity_n_file);

        getDate();
        initialize();
        checkInternet();

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
