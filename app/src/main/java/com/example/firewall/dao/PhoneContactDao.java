package com.example.firewall.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.firewall.bean.PhoneContactInfo;

import java.util.ArrayList;
import java.util.List;

public class PhoneContactDao {

    public static List<PhoneContactInfo> queryContactPhoneNumber(Context context) {
        //获得ContentResolver对象，用于开provider的锁
        ContentResolver resolver=context.getContentResolver();
        Uri uri= Uri.parse("content://com.android.contacts/raw_contacts"); //路径全部小写，csdn编辑器原因有时候会显示错误的大写
        Cursor cursor=resolver.query(uri, new String[]{"_id"}, null, null, null);
        List<PhoneContactInfo> infos=new ArrayList<>();
        while(cursor.moveToNext()){
            String _id=cursor.getString(0);
            Uri dataUri=Uri.parse("content://com.android.contacts/data");
            Cursor dataCursor=resolver.query(dataUri, new String[]{"mimetype","data1"}, "raw_contact_id=?",new String[]{_id} , null);
            PhoneContactInfo info=new PhoneContactInfo();
            while(dataCursor.moveToNext()){
                String mimetype=dataCursor.getString(0);
                if(mimetype.equals("vnd.android.cursor.item/phone_v2")){
                    String phone=dataCursor.getString(1);
                    info.setNumber(phone);
                }
                if(mimetype.equals("vnd.android.cursor.item/name")){
                    String name=dataCursor.getString(1);
                    info.setUsername(name);
                }
            }
            dataCursor.close();
            infos.add(info);
        }
        cursor.close();
        return infos;

    }

}
