package com.example.elizabeth.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by elizabeth on 7/9/17.
 */

public class OnScreenOffReceiver extends BroadcastReceiver {
    private static final String PREF_KIOSK_MODE = "pref_kiosk_mode";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RECEIVE", intent.getAction());
        if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
            Log.d("SCREEN", "SCREEN OFF");
            AppContext ctx = (AppContext) context.getApplicationContext();
            // is Kiosk Mode active?
            if(isKioskModeActive(ctx)) {
                wakeUpDevice(ctx);
            }
        }
    }
    private void wakeUpDevice(AppContext context) {
        PowerManager.WakeLock wakeLock = context.getWakeLock(); // get WakeLock reference via AppContext
        if (wakeLock.isHeld()) {
            wakeLock.release(); // release old wake lock
        }
        // create a new wake lock...
        wakeLock.acquire();
        // ... and release again
        wakeLock.release();
    }
    private boolean isKioskModeActive(final Context context) {
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return true;
    }
}
