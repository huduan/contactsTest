package com.huduan.contactstest.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huduan.contactstest.R;

import java.util.Objects;

/**
 * Created by huduan on 17-11-23.
 */

public class AddActivity extends AppCompatActivity {
    private EditText add_name;
    private EditText add_number;
    Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_add);
        add_name = findViewById(R.id.add_name);
        add_number = findViewById(R.id.add_number);
        mContext = this;

        Button button1 = findViewById(R.id.add_btn1);
        //确认添加
        button1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                contactInsert();
                Intent intent1 = new Intent(mContext, MainActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        Button button2 = findViewById(R.id.add_btn2);
        //取消添加
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, MainActivity.class);
                startActivity(intent2);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void contactInsert() {
        //获取新增的联系人姓名和电话
        String name = add_name.getText().toString();
        String number = add_number.getText().toString();

        //先插入空值,获取对应的rawContactId
        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);


        //根据contactId新增联系人
        if (!Objects.equals(name, "")) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
        }

        //插入电话号码
        if (!Objects.equals(number, "")) {
            values.clear();
            values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }
    }
}
