package com.huduan.contactstest.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huduan.contactstest.R;

/**
 * Created by huduan on 17-11-23.
 */

public class EditActivity extends AppCompatActivity {
    private EditText edit_name;
    private EditText edit_number;
    Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_edit);
        mContext = this;
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        String number = intent.getStringExtra("number");
        edit_name = findViewById(R.id.edit_name);
        edit_number = findViewById(R.id.edit_number);
        edit_name.setText(name);
        edit_number.setText(number);

        Button button1 = findViewById(R.id.edit_btn1);
        //确认编辑
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                contactDelete(name);
                contactInsert();
                Intent intent1 = new Intent(mContext, MainActivity.class);
                startActivity(intent1);
            }
        });

        Button button2 = findViewById(R.id.edit_btn2);
        //取消编辑
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, MainActivity.class);
                startActivity(intent2);
            }
        });
    }

    //删除编辑前的信息
    public void contactDelete(String name) {
        //
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data._ID}, "display_name=?", new String[]{name}, null);
        if (cursor != null) {
            while (cursor.moveToFirst()) {
                int id = cursor.getInt(0);

                //根据id删除对应的联系人数据
                resolver.delete(uri, "display_name=?", new String[]{name});
                uri = Uri.parse("content://com.android.contacts/data");
                resolver.delete(uri, "raw_contact_id=?", new String[]{id + ""});
            }
            cursor.close();
        }
    }

    //保存更改后的联系人信息
    public void contactInsert() {
        //
        String name = edit_name.getText().toString();
        String number = edit_number.getText().toString();

        //先插入空值,获取contactId
        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        //根据contactId新增联系人
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
        values.clear();

        //插入电话号码
        values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();
    }
}
