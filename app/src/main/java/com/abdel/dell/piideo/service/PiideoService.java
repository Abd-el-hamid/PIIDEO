package com.abdel.dell.piideo.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.abdel.dell.piideo.app.MyApplication;
import com.abdel.dell.piideo.helper.PiideoHelper;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

/**
 * Created by DELL on 16/04/2017.
 */

public class PiideoService extends Service {

    private static String TAG = PiideoService.class.getSimpleName();
    private ResultReceiver resultReceiver;
    private String videoPath;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final boolean stop = intent.getBooleanExtra("stop", false);

        if(stop){
            videoPath = intent.getStringExtra("videoPath");
            stopSelf();
        }else {

            resultReceiver = intent.getParcelableExtra("receiver");

            String imgPath = intent.getStringExtra("imgPath");
            String audioPath = intent.getStringExtra("audioPath");

            PiideoHelper.createPiideo(imgPath, audioPath, PiideoService.this);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("finished", true);
        bundle.putString("videoPath", videoPath);
        resultReceiver.send(18, bundle);
        super.onDestroy();
    }
}
