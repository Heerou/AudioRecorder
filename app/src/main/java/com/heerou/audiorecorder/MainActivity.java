package com.heerou.audiorecorder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{

    Button btnStartRecord, btnStopRecord, btnStartPlay, btnStopPlay;
    String pathToSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init view
        btnStartRecord = findViewById(R.id.btnStartRecord);
        btnStopRecord = findViewById(R.id.btnStopRecord);
        btnStartPlay = findViewById(R.id.btnStartPlay);
        btnStopPlay = findViewById(R.id.btnStopPlay);

        //Requesting Permission
        if (checkPermissionFromDevice())
        {
            btnStopRecord.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    pathToSave = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"
                    + UUID.randomUUID().toString()+"_audio_record.3gp";
                    setUpMediaRecorder();
                    try
                    {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    btnStartPlay.setEnabled(false);
                    btnStopPlay.setEnabled(false);

                    Toast.makeText(MainActivity.this, "Recording the Audio...", Toast.LENGTH_SHORT).show();
                }
            });

            btnStopRecord.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mediaRecorder.stop();
                    btnStopRecord.setEnabled(false);
                    btnStartRecord.setEnabled(false);
                    btnStartPlay.setEnabled(true);
                    btnStopPlay.setEnabled(false);
                }
            });

            btnStartPlay.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    btnStopPlay.setEnabled(true);
                    btnStopRecord.setEnabled(false);
                    btnStartRecord.setEnabled(false);

                    mediaPlayer = new MediaPlayer();
                    try
                    {
                        mediaPlayer.setDataSource(pathToSave);
                        mediaPlayer.prepare();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    Toast.makeText(MainActivity.this, "Playing the Audio...", Toast.LENGTH_SHORT).show();
                }
            });

            btnStopPlay.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    btnStopRecord.setEnabled(false);
                    btnStartRecord.setEnabled(true);
                    btnStopPlay.setEnabled(false);
                    btnStartPlay.setEnabled(true);

                    if(mediaPlayer != null)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setUpMediaRecorder();
                    }
                }
            });
        }
        else 
        {
            requestPermissionGranted();
        }
    }

    private void setUpMediaRecorder()
    {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathToSave);
    }

    private void requestPermissionGranted()
    {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT);
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT);
            }
            break;
        }
    }

    //Check the permission in the device
    private boolean checkPermissionFromDevice()
    {
        int writeExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return  writeExternalStorage == PackageManager.PERMISSION_GRANTED && recordAudio == PackageManager.PERMISSION_GRANTED;
    }
}
