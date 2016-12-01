package com.example.administrator.hamemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.administrator.hamemo.activity.AlarmActivity;

/**
 * Created by zsf on 2016/12/1.
 */
public class TaskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context,AlarmActivity.class);
        context.startActivity(intent);
    }
}
