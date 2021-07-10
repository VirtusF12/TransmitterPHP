package com.example.virtus.transmitter;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TransmitterCash extends SQLiteOpenHelper{

    private static final String TEG_TransmitterCash = "TEG_TransmitterCash";

    public TransmitterCash(Context context){

        // db_cash - название БД; null - работа с курсором; 1 - версия БД
        super(context,"db_cash", null, 1);
        Log.d(TEG_TransmitterCash, "*** БД с названием db_cash была создана.");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE cash ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "login TEXT,"
                + "password TEXT);");

        Log.d(TEG_TransmitterCash, "*** таблица была создана.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
