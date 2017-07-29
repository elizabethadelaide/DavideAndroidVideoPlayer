package com.example.elizabeth.myapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class AlarmMenu extends AppCompatActivity {

    View alarmView;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static AlarmMenu inst;
    private TextView alarmTextView;
    public static final int REQUEST_CODE = 1;

    public static AlarmMenu instance(){
        return inst;
    }

    @Override
    public void onStart(){
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_menu);

        alarmView = (View) findViewById(R.id.alarmText);

        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTextView = (TextView) findViewById(R.id.alarmText);
        Button alarmButton = (Button) findViewById(R.id.alarmButton);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Button exitButton = (Button) findViewById(R.id.exitButton);
        Button chooseNewVideo = (Button) findViewById(R.id.newVideoButton);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        //set timepicker to last alarm set:
        try{
            int hour = sharedPref.getInt(getString(R.string.saved_hour), 19);
            int minute = sharedPref.getInt(getString(R.string.saved_minute), 0);

            alarmTimePicker.setHour(hour);
            alarmTimePicker.setMinute(0);

        }
        catch(Exception e){}

        alarmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("ALARM", "ALARM SET");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

                //if alarm is set for next day:
                if (alarmTimePicker.getHour() < Calendar.getInstance().get(Calendar.HOUR_OF_DAY)){
                    calendar.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1);
                }
                Intent myIntent = new Intent(AlarmMenu.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(AlarmMenu.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                //Toast.makeText(getApplicationContext(), "Device can be turned off at " + calendar.toString(), Toast.LENGTH_LONG).show();

                //save alarm time:


                editor.putInt(getString(R.string.saved_hour), alarmTimePicker.getHour());
                editor.putInt(getString(R.string.saved_minute), alarmTimePicker.getMinute());

                //Launch Video Player:
                Intent i = new Intent();
                i.setClassName("com.example.elizabeth.myapplication", "com.example.elizabeth.myapplication.MainActivity");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(i);

            }
        });

        //exit button leaves to normal home screen allows edits to the traditional settings
        exitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //For different phones, this should go through default apps
                Intent launchIntent= new Intent(Intent.ACTION_MAIN);
                launchIntent.addCategory(Intent.CATEGORY_HOME);

                // Always use string resources for UI text.
                // This says something like "Share this photo with"
                String title = getResources().getString(R.string.chooser_title);
                // Create intent to show chooser
                Intent chooser = Intent.createChooser(launchIntent, title);

                // Verify the intent will resolve to at least one activity
                if (launchIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }

            }
        });

        chooseNewVideo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent();
                //launch video task:
                i.setClassName("com.example.elizabeth.myapplication", "com.example.elizabeth.myapplication.AddNewVideo");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(i);
            }
        });

        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!hasPermissions(this, PERMISSIONS)) {
                Log.d("PERMISSIONS", "Request Permissions");
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE);
        }
        else{
            Log.d("PERMISSIONS", "Has Permissions");
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    /*@Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus){
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
        if(hasFocus){
            alarmView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("PERMISSIONS", "PERMISSIONS GRANTED");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //disable volume and short power presses
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_POWER){
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //disable long power presses:
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_POWER){
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onBackPressed(){} //do nothing on back key pressed

}
