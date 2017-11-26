package com.huduan.contactstest.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.huduan.contactstest.R;
import com.huduan.contactstest.model.ContactItem;
import com.huduan.contactstest.ui.adapter.MyListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context mContext = null;


    private MyListAdapter myAdapter;

    List<ContactItem> contactItems = new ArrayList<>();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        myAdapter = new MyListAdapter(mContext);
        ListView mListView = findViewById(R.id.lvPhones);//实例化listview
        mListView.setAdapter(myAdapter);
        readContacts();

        //新建联系人点击事件
        Button button = findViewById(R.id.btnAdd);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddActivity.class);
                startActivity(intent);
            }
        });

        //点击item进入编辑联系人界面
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, EditActivity.class);
                ContactItem contactItem = myAdapter.getItem(position);
                String name = contactItem.getContactName();
                String number = contactItem.getPhoneNumber();

                intent.putExtra("name", name);
                intent.putExtra("number", number);
                startActivity(intent);
            }
        });
    }

    //读取联系人
    private void readContacts() {

        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ContactItem contactItem = new ContactItem();

                    //联系人名称
                    String contactName = cursor.getString(cursor.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //读取电话号码
                    String phoneNumber = cursor.getString(cursor.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactItem.setContactName(contactName);
                    contactItem.setPhoneNumber(phoneNumber);
                    Log.d("MainActivity", " contact " + contactItem);
                    contactItems.add(contactItem);

                }

                myAdapter.setListItems(contactItems);
                myAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}