package com.example.firewall.dao;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import com.example.firewall.bean.PhoneCallLogInfo;
import com.example.firewall.bean.PhoneContactInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 获得电话信息的两种类型：
 *      联系人和通话记录
 */
public class PhoneContactDao {

    public static List<PhoneContactInfo> queryContactPhoneNumber(Context context) {
        //获得ContentResolver对象，用于开provider的锁
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts"); //路径全部小写，csdn编辑器原因有时候会显示错误的大写
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        List<PhoneContactInfo> infos = new ArrayList<>();
        while (cursor.moveToNext()) {
            String _id = cursor.getString(0);
            Uri dataUri = Uri.parse("content://com.android.contacts/data");
            Cursor dataCursor = resolver.query(dataUri, new String[]{"mimetype", "data1"}, "raw_contact_id=?", new String[]{_id}, null);
            PhoneContactInfo info = new PhoneContactInfo();
            while (dataCursor.moveToNext()) {
                String mimetype = dataCursor.getString(0);
                if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                    String phone = dataCursor.getString(1);
                    info.setNumber(phone);
                }
                if (mimetype.equals("vnd.android.cursor.item/name")) {
                    String name = dataCursor.getString(1);
                    info.setUsername(name);
                }
            }
            dataCursor.close();
            infos.add(info);
        }
        cursor.close();
        return infos;

    }

    //获取通话记录
    public static List<PhoneCallLogInfo> getContentCallLog(Context context) {
        List<PhoneCallLogInfo> infos = new ArrayList<>();
        String[] columns = {CallLog.Calls.CACHED_NAME// 通话记录的联系人
                , CallLog.Calls.NUMBER// 通话记录的电话号码
                , CallLog.Calls.DATE// 通话记录的日期
                , CallLog.Calls.DURATION// 通话时长
                , CallLog.Calls.TYPE};// 通话类型}
        if (context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return infos;
        }

        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, // 查询通话记录的URI
                columns
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        assert cursor != null;
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));  //号码
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)); //获取通话日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new java.util.Date(dateLong));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)); //获取通话类型：1.呼入2.呼出3.未接
            PhoneCallLogInfo info = new PhoneCallLogInfo();
            info.setType(type);
            info.setNumber(number);
            info.setDate(date);
            infos.add(info);
        }

        return infos;
    }

}
