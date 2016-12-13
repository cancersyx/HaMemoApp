package com.example.administrator.hamemo.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.administrator.hamemo.R;
import com.example.administrator.hamemo.constant.TaskList;

/**
 * Created by zsf on 2016/11/30.
 * 功能：1.通过前面定义的TaskListProvider查询备忘录列表，得到Cursor游标，通过Cursor构建SimpleCursorAdapter,
 * 并通过ListView显示
 * 2.响应ListView单击事件，用户单击某条备忘录信息时，显示该条备忘录的详细信息
 * 3.提供选项菜单添加和删除备忘录信息。
 */
public class TaskListActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, AdapterView.OnItemClickListener {

    private final String LOG_TAG = TaskListActivity.class.getSimpleName();
    private Toolbar mToolBar;
    private ListView mListView;
    private Intent mIntent;
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
        setContentView(R.layout.activity_task_list);
        initView();
        initData();
        initEvent();


        //查询所有备忘录信息
        final Cursor cursor = getContentResolver().query(getIntent().getData(),
                PROJECTION, null, null, TaskList.Tasks.DEFAULT_SORT_ORDER);
        //创建Adapter
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2
                , cursor, new String[]{TaskList.Tasks._ID, TaskList.Tasks.CONTENT},
                new int[]{android.R.id.text1, android.R.id.text2});
        mListView.setAdapter(adapter);

    }


    private void initView() {
        mIntent = getIntent();
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        mListView = (ListView) findViewById(R.id.listView);
        mToolBar.setTitle("备忘录");
        mToolBar.inflateMenu(R.menu.main);

    }

    private void initData() {

        if (mIntent.getData() == null) {
            mIntent.setData(TaskList.Tasks.CONTENT_URI); //设置Uri
        }

    }

    private void initEvent() {
        mToolBar.setOnMenuItemClickListener(this);
        mListView.setOnItemClickListener(this);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_create:
                Intent intent = new Intent();
                intent.setClass(this, TaskDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.delte:
                Toast.makeText(this, "选择了删除选项", Toast.LENGTH_SHORT).show();
                break;
            case R.id.change_style:

                break;
        }


        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //通过ID查询备忘录信息
        Uri uri = ContentUris.withAppendedId(TaskList.Tasks.CONTENT_URI, id);
        Cursor cursor1 = managedQuery(uri, PROJECTION, null, null, TaskList.Tasks.DEFAULT_SORT_ORDER);
        if (cursor1.moveToNext()) {
            int id1 = cursor1.getInt(0);
            String content = cursor1.getString(1);
            String created = cursor1.getString(2);
            int alarm = cursor1.getInt(3);
            String date1 = cursor1.getString(4);
            String time1 = cursor1.getString(5);
            int on_off = cursor1.getInt(6);
            Bundle b = new Bundle();
            b.putInt("id", id1);
            b.putString("content", content);
            b.putString("created", created);
            b.putInt("alarm", alarm);
            b.putString("date1", date1);
            b.putString("time1", time1);
            b.putInt("on_off", on_off);

            //将备忘录信息添加到Intent
            mIntent.putExtra("b", b);
            //启动备忘录详细信息Activity
            mIntent.setClass(TaskListActivity.this, TaskDetailActivity.class);
            startActivity(mIntent);

        }
    }

}
