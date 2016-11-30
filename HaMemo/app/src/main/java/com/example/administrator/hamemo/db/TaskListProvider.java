package com.example.administrator.hamemo.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.administrator.hamemo.constant.TaskList;

import java.util.HashMap;

/**
 * Created by zsf on 2016/11/30.
 * 使用ContentProvider使得备忘录数据表具有通用的共享数据访问机制
 */
public class TaskListProvider extends ContentProvider {

    /**
     * 数据库名称常量
     */
    private static final String DATABASE_NAME = "task_list.db";
    //数据库版本
    private static final int DATABASE_VERSION = 1;
    //表名称常量
    private static final String TASK_LIST_TABLE_NAME = "taskLists";
    //查询列集合
    private static HashMap<String, String> sTaskListProjecttionMap;
    //查询，更新条件
    private static final int TASKS = 1;
    private static final int TASK_ID = 2;
    //Uri工具类
    private static final UriMatcher sUriMatcher;
    //数据库工具类实例
    private DatabaseHelper mOpenHelper;

    //内部工具类，创建或打开数据库，创建或删除表
    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);//这样都可以的~！！！
        }

        //创建表
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE" + TASK_LIST_TABLE_NAME + "("
                    + TaskList.Tasks._ID + "INTEGER PRIMARY KEY,"
                    + TaskList.Tasks.DATE1 + "TEXT,"
                    + TaskList.Tasks.TIME1 + "TEXT,"
                    + TaskList.Tasks.CONTENT + "TEXT,"
                    + TaskList.Tasks.ON_OFF + "INTEGER,"
                    + TaskList.Tasks.ALARM + "INTEGER,"
                    + TaskList.Tasks.CREATED + "TEXT"
                    + ");"
            );
        }

        //删除表
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS taskLists");
            onCreate(db);
        }
    }

    /**
     * 创建或打开数据库
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * 查询
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            //查询所有
            case TASKS:
                qb.setTables(TASK_LIST_TABLE_NAME);
                qb.setProjectionMap(sTaskListProjecttionMap);
                break;
            //查询ID
            case TASK_ID:
                qb.setTables(TASK_LIST_TABLE_NAME);
                qb.setProjectionMap(sTaskListProjecttionMap);
                qb.appendWhere(TaskList.Tasks._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Uri错误！" + uri);

        }

        //使用默认排序
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = TaskList.Tasks.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        //获得数据库实例
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        //返回游标集合
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /**
     * 就获得类型
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                return TaskList.Tasks.CONTENT_TYPE;
            case TASK_ID:
                return TaskList.Tasks.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Uri错误！" + uri);
        }
    }

    /**
     * 保存数据
     *
     * @param uri
     * @param initialValues
     * @return
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != TASKS) {
            throw new IllegalArgumentException("Uri错误！" + uri);
        }
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        //获得数据库实例
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //保存数据返回行ID
        long rowId = db.insert(TASK_LIST_TABLE_NAME, TaskList.Tasks.CONTENT, values);
        if (rowId > 0) {
            Uri taskUri = ContentUris.withAppendedId(TaskList.Tasks.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(taskUri, null);
            return taskUri;
        }
        throw new SQLException("插入数据失败" + uri);
    }

    /**
     * 删除数据
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //获得数据库实例
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            //根据指定条件删除
            case TASKS:
                count = db.delete(TASK_LIST_TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(TASK_LIST_TABLE_NAME, TaskList.Tasks._ID + "=" + noteId
                        + (!TextUtils.isEmpty(selection) ? "AND(" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("错误的URI" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * 更新数据
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //获得数据库实例
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            //根据指定条件更新
            case TASKS:
                count = db.update(TASK_LIST_TABLE_NAME, values, selection, selectionArgs);
                break;
            //根据指定条件和ID更新
            case TASK_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(TASK_LIST_TABLE_NAME, values, TaskList.Tasks._ID + "=" +
                        noteId + (!TextUtils.isEmpty(selection) ? "AND(" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("错误的URI" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
    static {
        //Uri 匹配工具类
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TaskList.AUTHORITY,"taskLists",TASKS);
        sUriMatcher.addURI(TaskList.AUTHORITY,"taskLists/#",TASK_ID);
        //实例化查询列集合
        sTaskListProjecttionMap = new HashMap<String,String>();
        //添加查询列
        sTaskListProjecttionMap.put(TaskList.Tasks._ID, TaskList.Tasks._ID);
        sTaskListProjecttionMap.put(TaskList.Tasks.CONTENT,TaskList.Tasks.CONTENT);
        sTaskListProjecttionMap.put(TaskList.Tasks.CREATED,TaskList.Tasks.CREATED);
        sTaskListProjecttionMap.put(TaskList.Tasks.ALARM,TaskList.Tasks.ALARM);
        sTaskListProjecttionMap.put(TaskList.Tasks.DATE1, TaskList.Tasks.DATE1);
        sTaskListProjecttionMap.put(TaskList.Tasks.TIME1, TaskList.Tasks.TIME1);
        sTaskListProjecttionMap.put(TaskList.Tasks.ON_OFF,TaskList.Tasks.ON_OFF);
    }
}
