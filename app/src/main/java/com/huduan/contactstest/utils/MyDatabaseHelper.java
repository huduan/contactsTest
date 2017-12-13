package com.huduan.contactstest.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by huduan on 17-12-12.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "MyDatabaseHelper";

    //private Context mContext;


    public static final String CREATE_CONTACTS = "create table Contacts ("
            + "id integer primary key autoincrement,"
            + "sortKey text,"
            + "name text,"
            + "number text,"
            + "rawContactId Integer,"
            + "lastUpdataTime Text)";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        //mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS);
        Log.d(TAG, "onCreate: create succeeded");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
