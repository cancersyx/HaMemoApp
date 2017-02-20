package com.example.administrator.hamemo.constant;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by zsf on 2016/11/30.
 * 操作备忘录数据表时，一些共用的属性，在该常量类中声明。
 */
public class Constant {

    public static final String AUTHORITY = "com.example.administrator.hamemo.constant.provider";//授权常量，权限
    private Constant(){};//构造方法

    /**
     * 内部类
     * 声明URI和列常量等
     * 继承了BaseColumns直接拥有了ID
     */
    public static final class Tasks implements BaseColumns{
        private Tasks(){};
        //访问Uri,表名称为taskLists
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/taskLists");//内容URI
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY +".taskLists";//MIME类型
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + ".taskLists";

        //默认排序常量（Desc是降序）
        public static final String DEFAULT_SORT_ORDER = "created DESC";
        //内容
        public static final String CONTENT = "content";
        //创建时间
        public static final String CREATED = "created";
        //日期
        public static final String DATE1 = "date1";
        //时间
        public static final String TIME1 = "time1";
        //是否开启
        public static final String ON_OFF = "on_off";
        //警告
        public static final String ALARM = "alarm";


    }



}
