package com.huduan.contactstest.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huduan on 17-12-13.
 */

public class ContactsSyncDao {
    private SQLiteDatabase db;

    public ContactsSyncDao(SQLiteDatabase db) {
        this.db = db;
    }

    public List<Integer> query() {
        List<Integer> rawContactIdList = new ArrayList<>();
        Cursor cursor = db.query
                ("Contacts", null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int rawContactId = cursor.getInt(cursor.getColumnIndex("rawContactId"));
                rawContactIdList.add(rawContactId);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return rawContactIdList;
    }

}
