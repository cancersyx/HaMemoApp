package com.example.administrator.hamemo.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.administrator.hamemo.constant.Constant;
import com.example.administrator.hamemo.db.DatabaseHelper;

import java.util.HashMap;

/**
 * Created by zsf on 2016/11/30.
 * 使用ContentProvider使得备忘录数据表具有通用的共享数据访问机制
 */
public class TaskListProvider extends ContentProvider {


    //查询列集合
    private static HashMap<String, String> mTaskListProjectionMap;
    //查询，更新条件
    private static final int TASKS = 1;
    private static final int TASK_ID = 2;
    //Uri工具类
    private static final UriMatcher mUriMatcher;
    //数据库工具类实例
    private DatabaseHelper mOpenHelper;

    //表名称常量
    private static final String TASK_LIST_TABLE_NAME = "taskLists";


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
        switch (mUriMatcher.match(uri)) {
            //查询所有
            case TASKS:
                qb.setTables(TASK_LIST_TABLE_NAME);
                qb.setProjectionMap(mTaskListProjectionMap);
                break;
            //查询ID
            case TASK_ID:
                qb.setTables(TASK_LIST_TABLE_NAME);
                qb.setProjectionMap(mTaskListProjectionMap);
                qb.appendWhere(Constant.Tasks._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Uri错误！" + uri);

        }

        //使用默认排序
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Constant.Tasks.DEFAULT_SORT_ORDER;
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
        switch (mUriMatcher.match(uri)) {
            case TASKS:
                return Constant.Tasks.CONTENT_TYPE;
            case TASK_ID:
                return Constant.Tasks.CONTENT_ITEM_TYPE;
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
        if (mUriMatcher.match(uri) != TASKS) {
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
        long rowId = db.insert(TASK_LIST_TABLE_NAME, Constant.Tasks.CONTENT, values);
        if (rowId > 0) {
            Uri taskUri = ContentUris.withAppendedId(Constant.Tasks.CONTENT_URI, rowId);
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
        switch (mUriMatcher.match(uri)) {
            //根据指定条件删除
            case TASKS:
                count = db.delete(TASK_LIST_TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(TASK_LIST_TABLE_NAME, Constant.Tasks._ID + "=" + noteId
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
        switch (mUriMatcher.match(uri)) {
            //根据指定条件更新
            case TASKS:
                count = db.update(TASK_LIST_TABLE_NAME, values, selection, selectionArgs);
                break;
            //根据指定条件和ID更新
            case TASK_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(TASK_LIST_TABLE_NAME, values, Constant.Tasks._ID + "=" +
                        noteId + (!TextUtils.isEmpty(selection) ? "AND(" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("错误的URI" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        //Uri 匹配工具类
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(Constant.AUTHORITY, "taskLists", TASKS);
        mUriMatcher.addURI(Constant.AUTHORITY, "taskLists/#", TASK_ID);
        //实例化查询列集合
        mTaskListProjectionMap = new HashMap<String, String>();
        //添加查询列
        mTaskListProjectionMap.put(Constant.Tasks._ID, Constant.Tasks._ID);
        mTaskListProjectionMap.put(Constant.Tasks.CONTENT, Constant.Tasks.CONTENT);
        mTaskListProjectionMap.put(Constant.Tasks.CREATED, Constant.Tasks.CREATED);
        mTaskListProjectionMap.put(Constant.Tasks.ALARM, Constant.Tasks.ALARM);
        mTaskListProjectionMap.put(Constant.Tasks.DATE1, Constant.Tasks.DATE1);
        mTaskListProjectionMap.put(Constant.Tasks.TIME1, Constant.Tasks.TIME1);
        mTaskListProjectionMap.put(Constant.Tasks.ON_OFF, Constant.Tasks.ON_OFF);
    }
}
