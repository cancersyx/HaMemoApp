package com.example.administrator.hamemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.administrator.hamemo.constant.Constant;

/**
 * Created by zsf on 2017/2/20.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * 数据库名称常量
     */
    private static final String DATABASE_NAME = "task_list.db";
    //数据库版本
    private static final int DATABASE_VERSION = 1;

    //表名称常量
    private static final String TASK_LIST_TABLE_NAME = "taskLists";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);//这样都可以的~！！！
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TASK_LIST_TABLE_NAME + " ("
                + Constant.Tasks._ID + " INTEGER PRIMARY KEY,"
                + Constant.Tasks.DATE1 + " TEXT,"
                + Constant.Tasks.TIME1 + " TEXT,"
                + Constant.Tasks.CONTENT + " TEXT,"
                + Constant.Tasks.ON_OFF + " INTEGER,"
                + Constant.Tasks.ALARM + " INTEGER,"
                + Constant.Tasks.CREATED + " TEXT"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS taskLists");
        onCreate(db);
    }
}
