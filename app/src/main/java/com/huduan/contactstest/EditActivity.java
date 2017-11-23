package com.huduan.contactstest;

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

/**
 * Created by huduan on 17-11-23.
 */

public class EditActivity extends AppCompatActivity {
    private EditText bt_name;
    private EditText bt_number;
    Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_edit);
        mContext = this;
        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        String number = intent.getStringExtra("number");
        bt_name = findViewById(R.id.edit_name);
        bt_number = findViewById(R.id.edit_number);
        bt_name.setText(name);
        bt_number.setText(number);
        Button button1 = findViewById(R.id.edit_btn1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                contactDelete(name);
                contactInsert();
                Intent intent1 = new Intent(mContext, MainActivity.class);
                startActivity(intent1);
            }
        });
        Button button2 = findViewById(R.id.edit_btn2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, MainActivity.class);
                startActivity(intent2);
            }
        });
    }

    //删除当前被修改的联系人信息
    public void contactDelete(String name) {
        //获取Id
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = this.getBaseContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data._ID}, "display_name=?", new String[]{name}, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            //
            resolver.delete(uri, "display_name=?", new String[]{name});
            uri = Uri.parse("content://com.android.contacts/data");
            resolver.delete(uri, "raw_contact_id=?", new String[]{id + ""});
        }
    }

    //保存更改后的联系人信息
    public void contactInsert() {
        //
        bt_name = (EditText) findViewById(R.id.edit_name);
        bt_number = (EditText) findViewById(R.id.edit_number);
        String name = bt_name.getText().toString();
        String number = bt_number.getText().toString();

        //ContactId
        ContentValues values = new ContentValues();
        Uri rawContactUri = this.getBaseContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        //联系人
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        this.getBaseContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

        //电话号码
        values.clear();
        values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        this.getBaseContext().getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
    }
}
