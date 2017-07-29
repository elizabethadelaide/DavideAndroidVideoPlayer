package com.example.elizabeth.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class PowerOffNotification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_off_notification);

        Button exitButton = (Button) findViewById(R.id.powerExitButton);

        //exit button leaves to normal home screen allows edits to the traditional settings
        exitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //For different phones, this should go through default apps
                //The home app is changed this application so there is no shortcut for it.
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

    @Override
    public void onBackPressed(){} //do nothing on back key pressed
}
