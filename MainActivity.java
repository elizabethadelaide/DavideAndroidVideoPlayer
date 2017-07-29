package com.example.elizabeth.myapplication;

import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //disable lockscreen:
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        //getWindow().addFlags(WindowManager.LayoutParams.PREVENT_POWER_KEY);
        // set the main layout of the activity
        setContentView(R.layout.activity_main);

        OnScreenOffReceiver onScreenOffReceiver = new OnScreenOffReceiver();


        //*****************************
        //Lock task in kiosk mode, app will be pinned
        //get policy manager:
        DevicePolicyManager myDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        //get this app package name
        ComponentName mDPM = new ComponentName(this, MyAdmin.class);

        //lock task:
        if (myDevicePolicyManager.isDeviceOwnerApp(this.getPackageName())){
            //get this app package name
            String[] packages = {this.getPackageName()};
            //mDOM is the admin package, and allow the specificied packages to lock task
            myDevicePolicyManager.setLockTaskPackages(mDPM, packages);
            startLockTask();
        }
        else{
                Toast.makeText(getApplicationContext(), "App requires root access", Toast.LENGTH_LONG).show();
        }


        //********************************
        //setup video player
        //set the media controller buttons
        if (mediaControls == null) {
            mediaControls = new MediaController(MainActivity.this);
        }

        //initialize the VideoView
        myVideoView = (VideoView) findViewById(R.id.video_view);

        // create a progress bar while the video file is loading
        progressDialog = new ProgressDialog(MainActivity.this);
        // set a title for the progress bar
        progressDialog.setTitle("Wait for Video to Load");
        // set a message for the progress bar
        progressDialog.setMessage("Loading...");
        //set the progress bar not cancelable on users' touch
        progressDialog.setCancelable(false);
        // show the progress bar
        progressDialog.show();

        try {
            //set the media controller in the VideoView
            //myVideoView.setMediaController(mediaControls);

            //set the uri of the video to be played
            //myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));

            //Play video from external storage
            //(Video is too large for apk
            Log.d("DIRECTORY", (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/video.mp4").toString());
            //Log.d("DIRECTORY", (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).listFiles()).toString());
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                myVideoView.setVideoURI(Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/video.mp4"));
            }
            else{
                Log.d("PERMISSION", "NOT PERMITTED");
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        myVideoView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        //myVideoView.setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE);

        myVideoView.requestFocus();
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        myVideoView.setOnPreparedListener(new OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                // close the progress bar and play the video
                progressDialog.dismiss();
                mediaPlayer.setLooping(true);
                mediaPlayer.start();

            }
        });

    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we use onSaveInstanceState in order to store the video playback position for orientation change
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        myVideoView.start();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //we use onRestoreInstanceState in order to play the video playback from the stored position
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus){
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
        if(hasFocus){
            myVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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
