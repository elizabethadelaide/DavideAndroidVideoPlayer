package com.example.elizabeth.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class AddNewVideo extends AppCompatActivity {

    static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_video);

        Button selectVideo = (Button) findViewById(R.id.selectVideoButton);
        Button launchBrowser = (Button) findViewById(R.id.launchBrowserButton);

        selectVideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //launch file browser from within app
                //result is handled later
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        launchBrowser.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(browserIntent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String FilePath = getPath(data.getData());
                    //filepath is your file's path
                    //FilePath is your file as a string
                    if (FilePath.substring(FilePath.lastIndexOf(".") + 1, FilePath.length()) == "mp4") {
                        moveFile(FilePath);
                    } else if (FilePath.substring(FilePath.lastIndexOf(".") + 1, FilePath.length()) != "MP4") {
                        moveFile(FilePath);
                    } else {
                        Toast.makeText(this.getApplicationContext(), "File needs to be mp4", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(this.getApplicationContext(), "Video updated", Toast.LENGTH_LONG).show();
                }
            default:
                return;
        }
    }

    //move file into defined video location
    public void moveFile(String FilePath){
        String to = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath();
        //to = to.concat(File.separator + "video.mp4");

        Log.d("File Names  to ", to);
        File fromFile = new File(FilePath);
        Log.d("File names from ", fromFile.toString());

        File toFile = new File(to);

        try {
            moveFileChannel(fromFile, toFile);
        }
        catch (Exception e){
            Log.d("FILE", e.getMessage());
        }
        
        //change file name:

        //get filename
        String path= FilePath;
        String filename=path.substring(path.lastIndexOf("/")+1);

        //rename file
        path = to.concat(File.separator + filename);
        String toPath = to.concat(File.separator + "video.mp4");
        fromFile = new File(path);
        toFile = new File(toPath);
        fromFile.renameTo(toFile);


    }
    private void moveFileChannel(File file, File dir) throws IOException {
        File newFile = new File(dir, file.getName());
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
        }
        catch(FileNotFoundException e){
            Log.d("FILE", e.getMessage());
        }
        catch(Exception e) {
            Log.d("FILE", e.getMessage());
        }
        if (inputChannel != null) inputChannel.close();
        if (outputChannel != null) outputChannel.close();
    }
    private String getPath(Uri uri){
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =  cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
}
