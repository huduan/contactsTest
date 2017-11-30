package com.huduan.contactstest.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.huduan.contactstest.R;
import com.huduan.contactstest.model.ContactItem;
import com.huduan.contactstest.ui.adapter.MyListAdapter;
import com.huduan.contactstest.utils.PinyinComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context mContext = null;


    private MyListAdapter myAdapter;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        myAdapter = new MyListAdapter(mContext);
        ListView mListView = findViewById(R.id.lvPhones);//实例化listview
       // readContacts();
        mListView.setAdapter(myAdapter);


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

        //长按item删除选中的联系人
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("是否删除此联系人?");
                dialog.setMessage("删除后此联系人的相关信息将全部消失!");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = myAdapter.getItem(position).getContactName();
                        //根据姓名查询id
                        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
                        ContentResolver resolver = getContentResolver();
                        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data._ID}, "display_name=?", new String[]{name}, null);
                        if (cursor != null) {
                            while (cursor.moveToFirst()) {
                                int _id = cursor.getInt(0);

                                //根据id删除data中的相应数据
                                resolver.delete(uri, "display_name=?", new String[]{name});
                                uri = Uri.parse("content://com.android.contacts/data");
                                resolver.delete(uri, "raw_contact_id=?", new String[]{_id + ""});
                            }
                            cursor.close();
                        }
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        readContacts();

    }

    //读取联系人
    //Uri.parse("content://com.android.contacts/contacts") == ContactsContract.Contacts.CONTENT_URI
    //注意一个联系人多个号码及联系人姓名相同的情况(待完善)

    private void readContacts() {
        List<ContactItem> contactItems = new ArrayList<>();
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
                    String sortkey = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.SORT_KEY_PRIMARY));
                    contactItem.setContactName(contactName);
                    contactItem.setPhoneNumber(phoneNumber);
                    contactItem.setSortkey(sortkey);
                    contactItems.add(contactItem);
                }
                Collections.sort(contactItems, new PinyinComparator());
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