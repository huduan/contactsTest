package com.huduan.contactstest.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.huduan.contactstest.R;
import com.huduan.contactstest.model.ContactItem;
import com.huduan.contactstest.service.FloatService;
import com.huduan.contactstest.service.LauncherService;
import com.huduan.contactstest.ui.adapter.MyListAdapter;
import com.huduan.contactstest.utils.LoadTask;
import com.huduan.contactstest.utils.LocalContactSearch;
import com.huduan.contactstest.utils.MyDatabaseHelper;
import com.huduan.contactstest.utils.PinyinComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper databaseHelper;

    private ListView mListView;
    private ListView shListView;
    private MyListAdapter myAdapter;
    private MyListAdapter shAdapter;

    private EditText et_search;
    private Button bt_add;

    //private LocalSearchView mSearchView;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAdapter = new MyListAdapter(this);
        mListView = findViewById(R.id.lvPhones);//实例化正常listview
        mListView.setAdapter(myAdapter);

        shAdapter = new MyListAdapter(this);
        shListView = findViewById(R.id.lvPhones_search);
        shListView.setAdapter(shAdapter);

//        mSearchView = new LocalSearchView(this);
//
//        mSearchView.setTextChangedCallback(new com.huduan.contactstest.ui.view.LocalSearchView.ITextChanged() {
//            @Override
//            public void onTextChanged() {
//
//            }
//        });

        //搜索
        et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    List<ContactItem> listG = LocalContactSearch.searchContact(s, myAdapter.getListItems());
                    shAdapter.updateContactList(listG);
                    setSearchListVisibility(true);
                } else {
                    setSearchListVisibility(false);
                }
            }

            private void setSearchListVisibility(boolean isVisible) {
                if (isVisible) {
                    shListView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    shListView.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //新建联系人点击事件
        bt_add = findViewById(R.id.btnAdd);
        bt_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });


        //点击item进入编辑联系人界面
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
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
                        ContentResolver resolver = getContentResolver();
                        Cursor cursor = resolver.query(ContactsContract.RawContacts.CONTENT_URI, new String[]{ContactsContract.RawContacts._ID}, "display_name=?", new String[]{name}, null);
                        if (cursor != null) {
                            while (cursor.moveToFirst()) {
                                int _id = cursor.getInt(0);

                                //根据contacts_id删除data中的相应数据
                                resolver.delete(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI,
                                        _id), null, null);
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
        //异步读取
        new LoadTask(this).execute();
        //createDatabase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //启动服务
        Intent foreIntent = new Intent(this, LauncherService.class);
        startService(foreIntent);
        Intent floatIntent = new Intent(this, FloatService.class);
        startService(floatIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
        //停止服务
        Intent foreIntent = new Intent(this, LauncherService.class);
        stopService(foreIntent);
        Intent floatIntent = new Intent(this, FloatService.class);
        floatIntent.putExtra("extra", "action_1");
        stopService(floatIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent foreIntent = new Intent(this, LauncherService.class);
        startService(foreIntent);
        Intent floatIntent = new Intent(this, FloatService.class);
        startService(floatIntent);

    }

    //读取联系人
    //Uri.parse("content://com.android.contacts/contacts") == ContactsContract.Contacts.CONTENT_URI
    //注意一个联系人多个号码及联系人姓名相同的情况(待完善)

    private void loadContacts() {
        databaseHelper = new MyDatabaseHelper(this, "contacts.db", null, 1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ArrayList<ContactItem> contactItems = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query("Contacts", null, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ContactItem contactItem = new ContactItem();

                    //联系人名称
                    String contactName = cursor.getString(cursor.
                            getColumnIndex("name"));
                    //读取电话号码
                    String phoneNumber = cursor.getString(cursor.
                            getColumnIndex("number"));
                    //读取sortKey
                    String sortKey = cursor.getString(cursor.
                            getColumnIndex("sortKey"));
                    contactItem.setContactName(contactName);
                    contactItem.setPhoneNumber(phoneNumber);
                    contactItem.setSortkey(sortKey);
                    contactItems.add(contactItem);
                }
                Collections.sort(contactItems, new PinyinComparator());
                myAdapter.updateContactList(contactItems);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //contacts数据库
//    private SQLiteDatabase createDatabase() {
//        databaseHelper = new MyDatabaseHelper(this, "contacts.db", null, 1);
//        return databaseHelper.getWritableDatabase();
//    }

    //批量添加联系人数据
//    private void batchAddContacts(List<ContactItem> list) throws RemoteException, OperationApplicationException {
//
//        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//        int rawContactInsertIndex = 0;
//        for (ContactItem contact : list) {
//            rawContactInsertIndex = ops.size(); // 有了它才能给真正的实现批量添加
//
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
//                    .withYieldAllowed(true).build());
//
//            // 添加姓名
//            ops.add(ContentProviderOperation
//                    .newInsert(
//                            android.provider.ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
//                            rawContactInsertIndex)
//                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getContactName())
//                    .withYieldAllowed(true).build());
//            // 添加号码
//            ops.add(ContentProviderOperation
//                    .newInsert(
//                            android.provider.ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
//                            rawContactInsertIndex)
//                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhoneNumber())
//                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "").withYieldAllowed(true).build());
//        }
//        if (ops != null) {
//            // 真正添加
//            ContentProviderResult[] results = getContentResolver()
//                    .applyBatch(ContactsContract.AUTHORITY, ops);
//
//        }
//    }
}