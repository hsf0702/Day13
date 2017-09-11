package com.example.a25737;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 25737 on 2017/9/7.
 */

public class QueryContactUtils {
    public static List<Contact> getContact(Context context){
        List<Contact> lists = new ArrayList<>();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        Cursor cursor = context.getContentResolver().query(uri, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()){
            Contact contact = new Contact();
            String contact_id = cursor.getString(0);
            System.out.println(contact_id);
            //查询所有的列
            Cursor datacursor = context.getContentResolver().query(dataUri, new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{contact_id}, null);
//            Cursor datacursor = getContentResolver().query(dataUri, new String[]{"data1", "mimetype_id"}, "raw_contact_id=?", new String[]{contact_id}, null);
            while (datacursor.moveToNext()){
                String data1 = datacursor.getString(0);
                String mimetype = datacursor.getString(1);
                if("vnd.android.cursor.item/email_v2".equals(mimetype)){
                    System.out.println("邮箱:"+data1);
                    contact.setEmail(data1);
                }else if("vnd.android.cursor.item/name".equals(mimetype)){
                    System.out.println("姓名:"+data1);
                    contact.setName(data1);
                }else if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                    System.out.println("电话:"+ data1);
                    contact.setPhone(data1);
                }
            }
            lists.add(contact);
        }
        return lists;
    }
}
