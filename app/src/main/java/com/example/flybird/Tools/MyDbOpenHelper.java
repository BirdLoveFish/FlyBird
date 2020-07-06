package com.example.flybird.Tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.flybird.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyDbOpenHelper extends SQLiteOpenHelper {
    public MyDbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE SYSTEM(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME VARCHAR(32), VALUE VARCHAR(64));");
        db.execSQL("CREATE TABLE BANKSAFEBOX(ID INTEGER PRIMARY KEY AUTOINCREMENT, FULLNAME VARCHAR(32), SIMPLENAME VARCHAR(32), PASSWORD VARCHAR(32), OWNER VARCHAR(32), OTHER VARCHAR(32));");
        db.execSQL("CREATE TABLE ACCOUNTSAFEBOX(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME VARCHAR(32), ACCOUNT VARCHAR(32), PASSWORD VARCHAR(32), OTHER VARCHAR(32));");
        db.execSQL("INSERT INTO SYSTEM (NAME) VALUES('PASSWORD');");
        db.execSQL("INSERT INTO SYSTEM (NAME) VALUES('SAFE_BOX_VALIDATE_TIME')");
        db.execSQL("INSERT INTO SYSTEM (NAME) VALUES('PASSWORD_TIPS')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("INSERT INTO SYSTEM (NAME) VALUES('PASSWORD_TIPS')");
    }
}
