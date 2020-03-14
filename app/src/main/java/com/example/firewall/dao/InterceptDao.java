package com.example.firewall.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firewall.bean.InterceptPhoneInfo;
import com.example.firewall.db.InterceptHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作InterceptData数据库（拦截）
 *      对应操作：增删查
 */
public class InterceptDao {
    private InterceptHelper helper;
    private SQLiteDatabase db;

    public InterceptDao(Context context){
        helper = new InterceptHelper(context, "interceptData", null, 1);
        db = helper.getWritableDatabase();
    }

    public void add(InterceptPhoneInfo info){
        db.execSQL("insert into interceptData (number, type) values (?,?)",
                new Object[]{info.getNumber(), info.getType()});
    }

    public List<InterceptPhoneInfo> getAll() {
        List<InterceptPhoneInfo> result = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from interceptData", null);
        while (cursor.moveToNext()){
            InterceptPhoneInfo info = new InterceptPhoneInfo();
            info.setNumber(cursor.getString(1));
            info.setType(cursor.getInt(2));
            result.add(info);
        }
        cursor.close();

        return  result;
    }

    public void delete(String number){
        db.execSQL("delete from interceptData where number=?",
                new Object[]{number});
    }

}
