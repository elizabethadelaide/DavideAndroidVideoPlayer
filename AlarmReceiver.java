package com.example.elizabeth.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by elizabeth on 7/9/17.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
        AlarmMenu inst = AlarmMenu.instance();

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmMenu.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);



        //Launch Video Player:
        Intent i = new Intent();
        i.setClassName("com.example.elizabeth.myapplication", "com.example.elizabeth.myapplication.PowerOffNotification");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
