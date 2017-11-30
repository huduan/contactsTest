package com.huduan.contactstest.utils;

import com.huduan.contactstest.model.ContactItem;

import java.util.Comparator;

/**
 * Created by huduan on 17-11-30.
 */

public class PinyinComparator implements Comparator<ContactItem> {
    @Override
    public int compare(ContactItem o1, ContactItem o2) {
        //根据首字母排序
        if (o1.getSortkey().equals("#") || o2.getSortkey().equals("#")) {

            return -1;
        } else {
            return (o1.getSortkey()).compareTo(o2.getSortkey());
        }
    }
}
