package com.example.administrator.hamemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by zsf on 2016/12/12.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity",getClass().getSimpleName());
    }
}
