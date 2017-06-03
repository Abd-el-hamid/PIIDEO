package com.abdel.dell.piideo.helper;

/**
 * Created by DELL on 10/04/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

import java.io.ByteArrayOutputStream;

/**
 * Created by MyInnos on 06-03-2017.
 */

public class PermissionHelper {

    public static boolean hasPermissions(Activity activity, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestPermissions(Activity activity, int PERMISSION_CODE, String... permissions) {
        if (!hasPermissions(activity, permissions)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                activity.requestPermissions(permissions, PERMISSION_CODE);
        }
    }

//    public static boolean checkPermissionForExternalStorage(Activity activity) {
//        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED);
//    }
//
//    public static boolean requestStoragePermission(Activity activity, int READ_STORAGE_PERMISSION) {
//        if (!checkPermissionForExternalStorage(activity)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        READ_STORAGE_PERMISSION);
//            }
//        }
//        return false;
//    }
//
//    public static boolean checkPermissionForCamer(Activity activity) {
//        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
//                == PackageManager.PERMISSION_GRANTED);
//    }
//
//    public static boolean requestCameraPermission(Activity activity, int CAMERA_PERMISSION) {
//        if (!checkPermissionForCamer(activity)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
//                        CAMERA_PERMISSION);
//            }
//        }
//        return false;
//    }


}