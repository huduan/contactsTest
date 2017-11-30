package com.huduan.contactstest.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
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
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {

                Intent intent1 = new Intent(mContext, MainActivity.class);
                startActivity(intent1);
                Editable phoneNumber = edit_number.getText();
                updateContactPhoneNumber(name, phoneNumber);
                finish();
            }
        });

        Button button2 = findViewById(R.id.edit_btn2);
        //取消编辑
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, MainActivity.class);
                startActivity(intent2);
                finish();
            }
        });
    }

    private void updateContactPhoneNumber(String contactName, Editable phoneNumber) {
        Uri uri = ContactsContract.Data.CONTENT_URI;//对data表的所有数据操作
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.DATA1, String.valueOf(phoneNumber));
        resolver.update(uri, values, ContactsContract.Data.MIMETYPE + "=? and " + ContactsContract.PhoneLookup.DISPLAY_NAME + "=?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, contactName});

    }
}
/*

//根据号码获取联系人的姓名
    public void testContactNameByNumber() throws Exception{
        String number = "110";
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/"+number);
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{android.provider.ContactsContract.Data.DISPLAY_NAME}, null, null, null);
        if(cursor.moveToFirst()){
            String name = cursor.getString(0);
            Log.i(TAG, name);
        }
        cursor.close();
    }


    //删除编辑前的信息
    public void contactDelete(String name) {
        //根据name查询到对应的id
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void contactInsert() {
        //获取新增的联系人姓名和电话
        String name = edit_name.getText().toString();
        String number = edit_number.getText().toString();

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
    */
