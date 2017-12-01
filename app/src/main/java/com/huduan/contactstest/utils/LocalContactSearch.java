package com.huduan.contactstest.utils;

import android.text.TextUtils;

import com.huduan.contactstest.model.ContactItem;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huduan on 17-12-2.
 */

public class LocalContactSearch {

    /**
     * 按拼音搜索
     *
     * @param str
     */
    public static ArrayList<ContactItem> searchContact(CharSequence str, ArrayList<ContactItem> allContacts) {

        ArrayList<ContactItem> contactItemList = new ArrayList<>();
        // 如果搜索条件以0 1 +开头则按号码搜索

        if (str.toString().startsWith("0") || str.toString().startsWith("1")
                || str.toString().startsWith("+")) {
            for (ContactItem contactItem : allContacts) {
                if (getContactItemName(contactItem) != null
                        && contactItem.getPhoneNumber() != null) {
                    if ((contactItem.getPhoneNumber()).contains(str)
                            || getContactItemName(contactItem).contains(str)) {
                        contactItemList.add(contactItem);
                    }
                }
            }
            return contactItemList;
        }
        CharacterParser finder = CharacterParser.getInstance();

        String result = "";
        for (ContactItem contactItem : allContacts) {
            // 先将输入的字符串转换为拼音
            finder.setResource(str.toString());
            result = finder.getSpelling();
            if (contains(contactItem, result)) {
                contactItemList.add(contactItem);
            } else if ((contactItem.getPhoneNumber()).contains(str)) {
                contactItemList.add(contactItem);
            }
        }
        return contactItemList;
    }

    /**
     * 根据拼音搜索
     *
     */
    private static boolean contains(ContactItem contactItem, String search) {
        if (TextUtils.isEmpty(contactItem.getPhoneNumber())
                && TextUtils.isEmpty(contactItem.getContactName())) {
            return false;
        }

        boolean flag = false;

        // 简拼匹配,如果输入在字符串长度大于6就不按首字母匹配了
        if (search.length() < 6) {
            String firstLetters = FirstLetterUtil
                    .getFirstLetter(getContactItemName(contactItem));
            // 不区分大小写
            Pattern firstLetterMatcher = Pattern.compile(search,
                    Pattern.CASE_INSENSITIVE);
            flag = firstLetterMatcher.matcher(firstLetters).find();
        }

        if (!flag) { // 如果简拼已经找到了，就不使用全拼了
            // 全拼匹配
            CharacterParser finder = CharacterParser.getInstance();
            finder.setResource(getContactItemName(contactItem));
            // 不区分大小写
            Pattern pattern2 = Pattern
                    .compile(search, Pattern.CASE_INSENSITIVE);
            Matcher matcher2 = pattern2.matcher(finder.getSpelling());
            flag = matcher2.find();
        }

        return flag;
    }

    private static String getContactItemName(ContactItem contactItem) {
        String strName = null;
        if (!TextUtils.isEmpty(contactItem.getContactName())) {
            strName = contactItem.getContactName();
        } else if (!TextUtils.isEmpty(contactItem.getPhoneNumber())) {
            strName = contactItem.getPhoneNumber();
        } else {
            strName = "";
        }

        return strName;
    }
}
