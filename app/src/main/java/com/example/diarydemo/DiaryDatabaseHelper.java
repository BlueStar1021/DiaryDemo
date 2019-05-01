package com.example.diarydemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by dell on 2019/3/31.
 */

public class DiaryDatabaseHelper extends SQLiteOpenHelper {

    //建表语句
    public static final String CREATE_TABLE_FOR_DIARY = "create table diary ("
            + "title text,"
            + "year integer,"
            + "month integer,"
            + "day integer,"
            + "mood text,"
            + "detail text,"
            + "PRIMARY KEY(title,year,month,day));";

    private Context mContext;

    public DiaryDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FOR_DIARY);
        Toast.makeText(mContext,"success",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
