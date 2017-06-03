package com.abdel.dell.piideo.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.abdel.dell.piideo.activity.PiideoPlayerActivity;
import com.abdel.dell.piideo.service.PiideoService;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Ravi on 13/05/15.
 */

public class MyApplication extends Application {

    public static final String TAG = MyApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    private static MyApplication mInstance;
    private static FFmpeg ffmpegInstance;

    public void stopService(String outVideo, Context context) {
        stopService(new Intent(mInstance.getApplicationContext(), PiideoService.class));

        Intent videoIntent = new Intent(context, PiideoPlayerActivity.class);
        videoIntent.putExtra("videoPath", outVideo);
        videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(videoIntent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        ffmpegInstance = FFmpeg.getInstance(this);
        try {
            ffmpegInstance.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public FFmpeg getFfmpegInstance() {
        return ffmpegInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        System.out.println("requesting server");
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}