package com.example.administrator.hamemo.activity;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.administrator.hamemo.R;
import com.example.administrator.hamemo.constant.TaskList;

/**
 * Created by zsf on 2016/11/30.
 * 功能：1.通过前面定义的TaskListProvider查询备忘录列表，得到Cursor游标，通过Cursor构建SimpleCursorAdapter,
 * 并通过ListView显示
 * 2.响应ListView单击事件，用户单击某条备忘录信息时，显示该条备忘录的详细信息
 * 3.提供选项菜单添加和删除备忘录信息。
 */
public class TaskListActivity extends ListActivity {

    //菜单项常量
    private static final int NEW = 1;
    private static final int DEL = 2;
    //查询列数组
    private static final String[] PROJECTION = new String[]{
            TaskList.Tasks._ID,//0
            TaskList.Tasks.CONTENT,//1
            TaskList.Tasks.CREATED,//2
            TaskList.Tasks.ALARM,//3
            TaskList.Tasks.DATE1,//4
            TaskList.Tasks.TIME1,//5
            TaskList.Tasks.ON_OFF //6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获得Intent
        final Intent intent = getIntent();
        //设置Uri
        if (intent.getData() == null) {
            intent.setData(TaskList.Tasks.CONTENT_URI);
        }
        //获得ListView
        ListView listView = getListView();
        //查询所有备忘录信息
        final Cursor cursor = managedQuery(getIntent().getData(),
                PROJECTION, null, null, TaskList.Tasks.DEFAULT_SORT_ORDER);
        //创建Adapter
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2
                , cursor, new String[]{TaskList.Tasks._ID, TaskList.Tasks.CONTENT},
                new int[]{android.R.id.text1, android.R.id.text2});
        setListAdapter(adapter);
        //为ListView 添加单击事件监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //通过ID查询备忘录信息
                Uri uri = ContentUris.withAppendedId(TaskList.Tasks.CONTENT_URI,id);
                Cursor cursor1 = managedQuery(uri,PROJECTION,null,null, TaskList.Tasks.DEFAULT_SORT_ORDER);
                if (cursor1.moveToNext()){
                    int id1 = cursor1.getInt(0);
                    String content = cursor1.getString(1);
                    String created = cursor1.getString(2);
                    int alarm = cursor1.getInt(3);
                    String date1 = cursor1.getString(4);
                    String time1 = cursor1.getString(5);
                    int on_off = cursor1.getInt(6);
                    Bundle b = new Bundle();
                    b.putInt("id",id1);
                    b.putString("content",content);
                    b.putString("created",created);
                    b.putInt("alarm",alarm);
                    b.putString("date1",date1);
                    b.putString("time1",time1);
                    b.putInt("on_off",on_off);

                    //将备忘录信息添加到Intent
                    intent.putExtra("b",b);
                    //启动备忘录详细信息Activity
                    intent.setClass(TaskListActivity.this,TaskDetailActivity.class);
                    startActivity(intent);

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,NEW,0,"新建");
        menu.add(0,DEL,0,"删除");
        return true;
    }

    //现象菜单项单击方法

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case NEW:
                //启动备忘录详细信息Activity
                Intent intent = new Intent();
                intent.setClass(this,TaskDetailActivity.class);
                startActivity(intent);
                return true;
            case DEL:
                return true;
        }

        return false;
    }
}
