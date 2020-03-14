package com.example.firewall.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/***
 * 添加拦截电话短信的Sqlite数据库
 *  字段:自增id，电话号码，拦截类型
 */
public class InterceptHelper extends SQLiteOpenHelper {
    private static final String CREATE_INPTERCEPTDATA = "create table interceptData" +
            "(id INTEGER primary key AUTOINCREMENT," +
            "number varchar(30) not null," +
            "type int not null)";
    private Context context;

    public InterceptHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version) {
        super(context, name, cursorFactory, version);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_INPTERCEPTDATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
