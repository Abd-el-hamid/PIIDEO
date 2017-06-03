package com.abdel.dell.piideo.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.abdel.dell.piideo.app.MyApplication;
import com.abdel.dell.piideo.service.PiideoService;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

/**
 * Created by DELL on 16/04/2017.
 */

public class PiideoHelper {

    public static void createPiideo(String image, String audio, final Context context) {

        /* Piideo creation *************/
        final String outVideo = getExternalDirectoryPath() + "/Download/piideo.mp4";

        String[] cmd = {"-loop", "1", "-i", image, "-i", audio, "-c:v", "libx264", "-tune", "stillimage", "-c:a", "aac", "-b:a", "64k", "-s", "cif", "-shortest", "-y", outVideo};

        final FFmpeg ffmpeg = MyApplication.getInstance().getFfmpegInstance();
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.w(null, "Cut started");
                    System.out.println("cut started");
                    Toast.makeText(context, "Creation started", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(String message) {
                    Log.w(null, message);
                    System.out.println("progress");

                }

                @Override
                public void onFailure(String message) {
                    Log.w(null, message);
                    System.out.println("failure");
                    Toast.makeText(context, "Creation failure", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onSuccess(String message) {
                    Log.w(null, message);
                    System.out.println("cut success");
                    Toast.makeText(context, "Creation success", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFinish() {
                    Log.w(null, "Cutting video finished");
                    System.out.println("cut finished");
                    Toast.makeText(context, "Creation finished", Toast.LENGTH_SHORT).show();
                    MyApplication.getInstance().stopService(outVideo);
                    //addAudio(ffmpeg, audio, outVideo, context);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
            Log.w(null, e.toString());
            System.out.println("Problem" + e.getMessage());
            Toast.makeText(context, "Creation Problem", Toast.LENGTH_SHORT).show();

        }
    }


    private static String getExternalDirectoryPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
