package com.huduan.contactstest.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.huduan.contactstest.R;
import com.huduan.contactstest.model.ContactItem;

import java.util.List;

/**
 * Created by huduan on 17-11-24.
 */

public class MyListAdapter extends BaseAdapter {
    private static final String TAG = "MyListAdapter";

    private Context mContext;
    private List<ContactItem> listItems;
    private LayoutInflater layoutInflater;

    public MyListAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void updateContactList(List<ContactItem> contactList) {
        this.listItems = contactList;
        notifyDataSetChanged();
    }

    public List<ContactItem> getListItems() {
        return listItems;
    }

    public void setListItems(List<ContactItem> listItems) {
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems == null ? 0 : listItems.size();
    }

    @Override
    public ContactItem getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView: " + convertView);

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.contact_item, parent, false);
            viewHolder.img = convertView.findViewById(R.id.imageView1);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHolder.tv_number = convertView.findViewById(R.id.tv_number);
            viewHolder.ib_phone = convertView.findViewById(R.id.imageButton1);
            viewHolder.ib_message = convertView.findViewById(R.id.imageButton2);
            convertView.setTag(viewHolder);//缓存
        } else {
            viewHolder = (ViewHolder) convertView.getTag();//从缓存读取
        }
        viewHolder.tv_name.setText(listItems.get(position).getContactName());//获取姓名
        viewHolder.tv_number.setText(listItems.get(position).getPhoneNumber());//获取电话号码
        viewHolder.img.setImageResource(R.mipmap.ic_launcher_round);//固定图片
        viewHolder.ib_phone.setImageResource(android.R.drawable.ic_menu_call);//固定图片
        viewHolder.ib_message.setImageResource(android.R.drawable.ic_menu_send);//固定图片

        //电话点击事件
        viewHolder.ib_phone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + listItems.get(position).getPhoneNumber()));
                mContext.startActivity(intent);
            }
        });

        //触发短信点击事件
        viewHolder.ib_message.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doSendSMSTo(listItems.get(position).getPhoneNumber());
            }

        });
        return convertView;
    }

    private void doSendSMSTo(String phoneNumber) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            Uri number = Uri.parse("smsto:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_SENDTO, number);
            mContext.startActivity(intent);
        }
    }


    class ViewHolder {
        ImageView img;
        TextView tv_name;
        TextView tv_number;
        ImageButton ib_phone;
        ImageButton ib_message;
    }

}