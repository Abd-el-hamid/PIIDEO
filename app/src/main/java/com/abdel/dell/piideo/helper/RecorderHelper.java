package com.abdel.dell.piideo.helper;

import android.media.MediaRecorder;
import android.os.Handler;

import java.io.File;

/**
 * Created by DELL on 18/04/2017.
 */

public class RecorderHelper {

    private static final String TAG = "RecorderHelper";
    private String audioPath;
    private MediaRecorder mMediaRecorder;
    private static volatile RecorderHelper sInst = null;
    private onRecorderListener mListener;

    public static RecorderHelper getInstance() {
        RecorderHelper inst = sInst;
        if (inst == null) {
            synchronized (RecorderHelper.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new RecorderHelper();
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    public RecorderHelper setPath(String audioPath) {
        this.audioPath = audioPath;
        return this;
    }

    private RecorderHelper() {
    }

    public void startRecord() {
        try {
            File file = new File(audioPath);
            if (!file.exists()) {
                file.createNewFile();
            }

            mMediaRecorder = new MediaRecorder();
            // 设置录音文件的保存位置
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            // 设置录音的来源（从哪里录音）
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录音的保存格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            // 设置录音的编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            if (null != mListener) {
                mListener.recorderStart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAndRelease() {
        if (null != mMediaRecorder) {
            stopRecording();
            if (null != mListener) {
                mListener.recorderStop();
            }
        }
    }

    public void stopAndDelete() {
        if (null != mMediaRecorder) {
            stopRecording();
            File file = new File(audioPath);
            file.delete();
        }
    }

    private void stopRecording() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void setRecorderListener(onRecorderListener listener) {
        this.mListener = listener;
    }

    public interface onRecorderListener {
        void recorderStart();

        void recorderStop();

    }


}