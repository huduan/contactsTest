package com.huduan.contactstest;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by huduan on 17-11-23.
 */

public class AddActivity extends AppCompatActivity {
    private EditText bt_name;
    private EditText bt_number;
    Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_add);
        mContext = this;
        Button button1 = findViewById(R.id.add_btn1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                contactInsert();
                Intent intent1 = new Intent(mContext, MainActivity.class);
                startActivity(intent1);
            }
        });

        Button button2 = findViewById(R.id.add_btn2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, MainActivity.class);
                startActivity(intent2);
            }
        });
    }

    public void contactInsert() {
        //获取新增的联系人名称和电话
        bt_name = findViewById(R.id.add_name);
        bt_number = findViewById(R.id.add_number);
        String name = bt_name.getText().toString();
        String number = bt_number.getText().toString();

        //获取ContactId
        ContentValues values = new ContentValues();
        Uri rawContactUri = this.getBaseContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        //插入联系人
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        this.getBaseContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

        //插入电话号码
        values.clear();
        values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        this.getBaseContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
    }
}
