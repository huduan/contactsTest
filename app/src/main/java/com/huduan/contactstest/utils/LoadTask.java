package com.huduan.contactstest.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.huduan.contactstest.dao.ContactsSyncDao;

import java.util.List;

/**
 * Created by huduan on 17-12-13.
 */

public class LoadTask extends AsyncTask<Void, Integer, Boolean> {
    private static final String TAG = "LoadTask";

    private Context mContext;

    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase db;

    public LoadTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dbHelper = new MyDatabaseHelper(mContext, "contacts.db", null, 1);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        ContentResolver resolver = mContext.getContentResolver();
        List<Integer> rawContactIdList = new ContactsSyncDao(db).query();
        Cursor cursor = null;
        try {
            cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    //联系人名称
                    String contactName = cursor.getString(cursor.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //读取电话号码
                    String phoneNumber = cursor.getString(cursor.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //读取sortKey
                    String sortKey = cursor.getString(cursor.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY));
                    //读取raw_contact_id
                    int rawContactId = cursor.getInt(cursor.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                    ContentValues values = new ContentValues();
                    //存入数据
                    values.put("name", contactName);
                    values.put("number", phoneNumber);
                    values.put("sortKey", sortKey);
                    values.put("rawContactId", rawContactId);

                    //查询是否存在此数据
                    if (!rawContactIdList.contains(rawContactId)) {
                        db.insert("Contacts", null, values);
                    }
                    Log.d(TAG, "doInBackground: " + values.get("name"));
                    Log.d(TAG, "doInBackground: " + values.get("number"));
                    values.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
