package com.example.firewall.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

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
    private Context context;

    public InterceptDao(Context context){
        helper = new InterceptHelper(context, "interceptData", null, 1);
        db = helper.getWritableDatabase();
        this.context = context;
    }

    @SuppressLint("ShowToast")
    public void add(InterceptPhoneInfo info){
        if (getByNumber(info.getNumber()) != null){
            Toast.makeText(context, "该号码已经加入黑名单", Toast.LENGTH_SHORT);
        }else
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

    public InterceptPhoneInfo getByNumber(String number){
        List<InterceptPhoneInfo> result = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from interceptData where number=?",
                new String[]{number});
        while (cursor.moveToNext()){
            InterceptPhoneInfo info = new InterceptPhoneInfo();
            info.setNumber(cursor.getString(1));
            info.setType(cursor.getInt(2));
            result.add(info);
        }
        cursor.close();

        if (result.size() == 0)
            return null;
        else
            return result.get(0);
    }

}
