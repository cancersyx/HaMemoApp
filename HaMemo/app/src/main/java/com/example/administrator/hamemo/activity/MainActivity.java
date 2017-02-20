package com.example.administrator.hamemo.activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.administrator.hamemo.R;
import com.example.administrator.hamemo.constant.Constant;

/**
 * Created by zsf on 2016/11/30.
 * 功能：1.通过前面定义的TaskListProvider查询备忘录列表，得到Cursor游标，通过Cursor构建SimpleCursorAdapter,
 * 并通过ListView显示
 * 2.响应ListView单击事件，用户单击某条备忘录信息时，显示该条备忘录的详细信息
 * 3.提供选项菜单添加和删除备忘录信息。
 */
public class MainActivity extends BaseActivity {

    private Toolbar mToolBar;
    private ListView mListView;
    private Intent mIntent;
    private SimpleCursorAdapter mAdapter;


    //查询列数组
    private static final String[] PROJECTION = new String[]{
            Constant.Tasks._ID,//0
            Constant.Tasks.CONTENT,//1
            Constant.Tasks.CREATED,//2
            Constant.Tasks.ALARM,//3
            Constant.Tasks.DATE1,//4
            Constant.Tasks.TIME1,//5
            Constant.Tasks.ON_OFF //6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();

        //查询所有备忘录信息
        final Cursor cursor = getContentResolver().query(getIntent().getData(),
                PROJECTION, null, null, Constant.Tasks.DEFAULT_SORT_ORDER);
        //创建Adapter
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2
                , cursor, new String[]{Constant.Tasks._ID, Constant.Tasks.CONTENT},
                new int[]{android.R.id.text1, android.R.id.text2});

        mListView.setAdapter(mAdapter);

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
            mIntent.setData(Constant.Tasks.CONTENT_URI); //设置Uri
        }

    }

    private void initEvent() {
        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.new_create:
                        startActivity(new Intent(MainActivity.this, MemoDetailsActivity.class));
                        break;
                }
                return false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(Constant.Tasks.CONTENT_URI, id);
                Cursor cursor1 = managedQuery(uri, PROJECTION, null, null, Constant.Tasks.DEFAULT_SORT_ORDER);
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
                    mIntent.setClass(MainActivity.this, MemoDetailsActivity.class);
                    startActivity(mIntent);

                }

            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position,
                                           final long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.warn);
                builder.setMessage("确认要删除该便签？");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //从数据库中删除掉该项item
                        getContentResolver().delete(getIntent().getData(),
                                "_id = ?", new String[]{Integer.toString((int) id)});
                        mAdapter.notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        initEvent();
    }
}
