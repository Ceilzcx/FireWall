package com.example.firewall.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firewall.bean.VirusBean;
import com.example.firewall.util.FileUtils;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * antivirus database operation
 * Need database assets/antivirus.db
 */
public class AntivirusDao {
    private static final String DB_NAME = "antivirus.db";
    private static final String TB_DATABLE = "datable";
    private static final String TB_VERSION = "version";
    private static final String COL_MD5 = "md5";
    private static final String COL_NAME = "name";

    private Context context;

    public AntivirusDao(Context context) {
        this.context = context;
    }

    /**
     * get a SQLiteDatabase object from assets/phone_location.db
     * the db will be copied to files dir at first run.
     * @return return SQLiteDatabase object if success, null otherwise
     */
    private SQLiteDatabase getWritableDatabase() {
        try {
            // check db whether exist
            File file = new File(context.getFilesDir(), DB_NAME);
            if(!file.exists()) {
                // file not exists, need copy
                InputStream in = context.getAssets().open(DB_NAME);
                FileUtils.saveFileWithStream(file, in);
            }
            // open database
            return SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public VirusBean selectByMd5(String md5) {
        SQLiteDatabase db = getWritableDatabase();

        // query by md5
        Cursor cursor = db.query(TB_DATABLE, new String[]{COL_NAME},
                COL_MD5 + " = ?", new String[]{md5},
                null, null, null);

        VirusBean bean = null;
        // get values
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            bean = new VirusBean(md5, name);
        }

        // never forget closing
        cursor.close();
        db.close();
        return bean;
    }

}
