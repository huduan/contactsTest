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

import static android.content.ContentValues.TAG;

/**
 * Created by huduan on 17-11-24.
 */

public class MyListAdapter extends BaseAdapter {

    private Context mContext;

    private List<ContactItem> listItems;
    private LayoutInflater layoutInflater;

    public MyListAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
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

        final ListItemView myListItemView;

        if (convertView == null) {
            myListItemView = new ListItemView();
            convertView = layoutInflater.inflate(R.layout.contact_item, null);
            myListItemView.setImg((ImageView) convertView.findViewById(R.id.imageView1));
            myListItemView.setTv_name((TextView) convertView.findViewById(R.id.tv_name));
            myListItemView.setTv_number((TextView) convertView.findViewById(R.id.tv_number));
            myListItemView.setIb_phone((ImageButton) convertView.findViewById(R.id.imageButton1));
            myListItemView.setIb_message((ImageButton) convertView.findViewById(R.id.imageButton2));
            convertView.setTag(myListItemView);
        } else {
            myListItemView = (ListItemView) convertView.getTag();
        }
        myListItemView.getTv_name().setText(listItems.get(position).getContactName());//获取姓名
        myListItemView.getTv_number().setText(listItems.get(position).getPhoneNumber());//获取电话号码
        myListItemView.getImg().setImageResource(R.mipmap.ic_launcher_round);//固定图片
        myListItemView.getIb_phone().setImageResource(android.R.drawable.stat_sys_phone_call);//固定图片
        myListItemView.getIb_message().setImageResource(android.R.drawable.ic_menu_send);//固定图片

        //电话点击事件
        myListItemView.getIb_phone().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + listItems.get(position).getPhoneNumber()));
                mContext.startActivity(intent);
            }
        });

        //触发短信点击事件
        myListItemView.getIb_message().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doSendSMSTo(listItems.get(position).getPhoneNumber());
            }

        });
        return convertView;
    }

    private void doSendSMSTo(String phoneNumber) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            Log.d(TAG, "doSendSMSTo: "+phoneNumber);
            Uri number = Uri.parse("smsto:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_SENDTO, number);
            mContext.startActivity(intent);
        }
    }


    class ListItemView {
        private ImageView img;
        private TextView tv_name;
        private TextView tv_number;
        private ImageButton ib_phone;
        private ImageButton ib_message;

        public ImageButton getIb_phone() {
            return ib_phone;
        }

        public ImageView getImg() {
            return img;
        }

        public void setImg(ImageView img) {
            this.img = img;
        }

        public void setIb_phone(ImageButton ib_phone) {
            this.ib_phone = ib_phone;
        }

        public ImageButton getIb_message() {
            return ib_message;
        }

        public void setIb_message(ImageButton ib_message) {
            this.ib_message = ib_message;
        }

        public TextView getTv_name() {
            return tv_name;
        }

        public void setTv_name(TextView tv_name) {
            this.tv_name = tv_name;
        }

        public TextView getTv_number() {
            return tv_number;
        }

        public void setTv_number(TextView tv_number) {
            this.tv_number = tv_number;
        }
    }

}