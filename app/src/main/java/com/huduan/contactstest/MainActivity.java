package com.huduan.contactstest;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Context mContext = null;
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    private static final int PHONES_NUMBER_INDEX = 1;

    private ArrayList<String> mContactsName = new ArrayList<String>();

    private ArrayList<String> mContactsNumber = new ArrayList<String>();

    ListView mListView = null;
    MyListAdapter myAdapter = null;


    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        readContacts();

        //Adapter
        mListView = (ListView) findViewById(R.id.lvPhones);
        myAdapter = new MyListAdapter(this);
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /*
                ContentResolver cr = mContext.getContentResolver();

                Cursor Cursor = cr.query(ContactsContract.Data.CONTENT_URI, null,
                        null, null, null);
                */
                Intent intent = new Intent(mContext, EditActivity.class);
                String name = mContactsName.get(position);
                String number = mContactsNumber.get(position);
                intent.putExtra("name", name);
                intent.putExtra("number", number);
                startActivity(intent);
            }
        });
    }

    //读取联系人(权限获取)
    private void readContacts() {
        ContentResolver resolver = mContext.getContentResolver();

        //
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //读取电话号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                //联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
            }
            phoneCursor.close();
        }
    }


    //适配器
    class MyListAdapter extends BaseAdapter {

        MyListAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return mContactsName.size();
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView name;
            TextView number;
            ImageButton phone;
            ImageButton message;

            //将xml文件转变成view
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_item, null);
            }

            name = convertView.findViewById(R.id.tv_name);
            number = convertView.findViewById(R.id.tv_number);
            phone = convertView.findViewById(R.id.imageButton1);
            message = convertView.findViewById(R.id.imageButton2);

            //联系人名称
            name.setText(mContactsName.get(position));
            //联系人电话
            number.setText(mContactsNumber.get(position));

            //电话点击事件
            phone.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mContactsNumber.get(position)));
                    //获取打电话权限
                    startActivity(intent);
                }
            });


            //发信息点击事件
            message.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    doSendSMSTo(mContactsNumber.get(position), "输入信息");
                }

            });
            return convertView;
        }

        public void doSendSMSTo(String phoneNumber, String message) {
            if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
                Uri a = Uri.parse("smsto:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, a);
                intent.putExtra("sms_body", message);
                startActivity(intent);
            }
        }

    }
}