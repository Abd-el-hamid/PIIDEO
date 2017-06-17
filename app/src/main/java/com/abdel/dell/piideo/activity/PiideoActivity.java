package com.abdel.dell.piideo.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abdel.dell.piideo.R;
import com.abdel.dell.piideo.helper.ImageCompressHelper;
import com.abdel.dell.piideo.helper.PermissionHelper;
import com.abdel.dell.piideo.helper.RecorderHelper;
import com.abdel.dell.piideo.service.PiideoService;
import com.bumptech.glide.Glide;
import com.dewarder.holdinglibrary.HoldingButtonLayout;
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;
import thebat.lib.validutil.ValidUtils;

public class PiideoActivity extends AppCompatActivity implements HoldingButtonLayoutListener, RecorderHelper.onRecorderListener {

    private static final String TAG = PiideoActivity.class.getSimpleName();

    ValidUtils validUtils;

    private ImageButton btnCamera, btnGallery;

    private static final DateFormat mFormatter = new SimpleDateFormat("mm:ss:SS");
    private static final float SLIDE_TO_CANCEL_ALPHA_MULTIPLIER = 2.5f;
    private static final long TIME_INVALIDATION_FREQUENCY = 50L;

    private HoldingButtonLayout mHoldingButtonLayout;
    private TextView mTime;
    private View mSlideToCancel;

    private int mAnimationDuration;
    private ViewPropertyAnimator mTimeAnimator;
    private ViewPropertyAnimator mSlideToCancelAnimator;

    private long mStartTime;
    private Runnable mTimerRunnable;

    private static final int READ_STORAGE_PERMISSION = 4000;
    private static final int CAMERA_PERMISSION = 4001;
    private static final int CAMERA_REQUEST = 4002;

    private String audioPath;

    private String imgPath;
    private static final int IMAGES_LIMIT = 5;
    private ImageView imageView;
    ArrayList<Image> images;
    private ContentValues cValues;
    private Uri imgUri;

    static final int RECORD_AUDIO_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piideo);

        init();
        initView();

    }

    public void init() {
        validUtils = new ValidUtils();

        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        btnGallery = (ImageButton) findViewById(R.id.btnGallery);

        mHoldingButtonLayout = (HoldingButtonLayout) findViewById(R.id.input_holder);
        mHoldingButtonLayout.addListener(this);

        mTime = (TextView) findViewById(R.id.time);
        mSlideToCancel = findViewById(R.id.slide_to_cancel);

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        imageView = (ImageView) findViewById(R.id.imageView);

        audioPath = getExternalCacheDir() + "/hand.amr";
        RecorderHelper.getInstance().setPath(audioPath).setRecorderListener(this);

        cValues = new ContentValues();
        cValues.put(MediaStore.Images.Media.TITLE, "New Picture");
        cValues.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
    }

    void initView() {
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!PermissionHelper.hasPermissions(PiideoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        PermissionHelper.requestPermissions(PiideoActivity.this, READ_STORAGE_PERMISSION,
                                Manifest.permission.READ_EXTERNAL_STORAGE);
                    } else {

                        startPickingImg();
                    }
                } else {
                    startPickingImg();
                }
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!PermissionHelper.hasPermissions(PiideoActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
                        PermissionHelper.requestPermissions(PiideoActivity.this, CAMERA_PERMISSION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                    } else {
                        startCapturingImg();
                    }
                } else {
                    startCapturingImg();
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean canceled = grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED;
        if (canceled) {
            Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();
        } else {
            switch (requestCode) {
                case CAMERA_PERMISSION:
                case RECORD_AUDIO_PERMISSION:
                default:
                    break;
            }
        }
    }


    public void startPickingImg() {
        Intent intent = new Intent(this, AlbumSelectActivity.class);
        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, IMAGES_LIMIT); // set limit for image selection
        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
    }

    public void startCapturingImg() {
        imgUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case ConstantsCustomGallery.REQUEST_CODE:
                    images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);

                    for (int i = 0; i < images.size(); i++) {

                        new ImageCompressionAsyncTask().execute(images.get(i).path);

                        Uri uri = Uri.fromFile(new File(images.get(i).path));

                        showImage(uri, imageView);

//                        txImageSelects.setText(String.valueOf(i + 1) + ". " + String.valueOf(uri));
                    }
                    break;
                case CAMERA_REQUEST:

                    //imgPath = getRealPathFromURI(PiideoActivity.this, imgUri);

                    new ImageCompressionAsyncTask().execute(imgUri.toString());
                    showImage(imgUri, imageView);
                    break;
            }
        }
    }

    private void showImage(Uri uri, ImageView imgView) {
        Glide.with(this).load(uri)
                .placeholder(R.color.colorAccent)
                .override(400, 400)
                .crossFade()
                .centerCrop()
                .into(imgView);
    }

    private class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            return ImageCompressHelper.compressImage(params[0], PiideoActivity.this);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            imgPath = s;
        }
    }


    @Override
    public void onBeforeExpand() {
        cancelAllAnimations();

        mSlideToCancel.setTranslationX(0f);
        mSlideToCancel.setAlpha(0f);
        mSlideToCancel.setVisibility(View.VISIBLE);
        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(1f).setDuration(mAnimationDuration);
        mSlideToCancelAnimator.start();


        mTime.setTranslationY(mTime.getHeight());
        mTime.setAlpha(0f);
        mTime.setVisibility(View.VISIBLE);
        mTimeAnimator = mTime.animate().translationY(0f).alpha(1f).setDuration(mAnimationDuration);
        mTimeAnimator.start();


    }

    @Override
    public void onExpand() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            this.mHoldingButtonLayout.cancel();
            ActivityCompat.requestPermissions(PiideoActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION);
        } else {

            RecorderHelper.getInstance().startRecord();
            mStartTime = System.currentTimeMillis();
            invalidateTimer();
        }

    }

    private void invalidateTimer() {
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                mTime.setText(getFormattedTime());
                invalidateTimer();
            }
        };

        mTime.postDelayed(mTimerRunnable, TIME_INVALIDATION_FREQUENCY);
    }

    private String getFormattedTime() {
        return mFormatter.format(new Date(System.currentTimeMillis() - mStartTime));
    }

    @Override
    public void onBeforeCollapse() {
        cancelAllAnimations();

        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(0f).setDuration(mAnimationDuration);
        mSlideToCancelAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSlideToCancel.setVisibility(View.INVISIBLE);
                mSlideToCancelAnimator.setListener(null);
            }
        });
        mSlideToCancelAnimator.start();

        mTimeAnimator = mTime.animate().translationY(mTime.getHeight()).alpha(0f).setDuration(mAnimationDuration);
        mTimeAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTime.setVisibility(View.INVISIBLE);
                mTimeAnimator.setListener(null);
            }
        });
        mTimeAnimator.start();
    }

    @Override
    public void onCollapse(boolean isCancel) {

        stopTimer();
        if (isCancel) {
            Toast.makeText(this, "Action canceled!", Toast.LENGTH_SHORT).show();
            RecorderHelper.getInstance().stopAndDelete();
        } else {
            Toast.makeText(this, "Action submitted! Time " + getFormattedTime(), Toast.LENGTH_SHORT).show();
            RecorderHelper.getInstance().stopAndRelease();

        }
    }

    private void cancelAllAnimations() {

        if (mSlideToCancelAnimator != null) {
            mSlideToCancelAnimator.cancel();
        }

        if (mTimeAnimator != null) {
            mTimeAnimator.cancel();
        }
    }

    private void stopTimer() {
        if (mTimerRunnable != null) {
            mTime.getHandler().removeCallbacks(mTimerRunnable);
        }
    }

    @Override
    public void onOffsetChanged(float offset, boolean isCancel) {
        mSlideToCancel.setTranslationX(-mHoldingButtonLayout.getWidth() * offset);
        mSlideToCancel.setAlpha(1 - SLIDE_TO_CANCEL_ALPHA_MULTIPLIER * offset);
    }


    @Override
    public void recorderStart() {

    }

    @Override
    public void recorderStop() {

        validUtils.showProgressDialog(getApplicationContext(), this);

        ResultReceiver piideoServiceResultReceiver = new PiideoServiceResultReceiver(null);

        Intent serviceIntent = new Intent(PiideoActivity.this, PiideoService.class);
        serviceIntent.putExtra("receiver", piideoServiceResultReceiver);

        serviceIntent.putExtra("imgPath", imgPath);
        serviceIntent.putExtra("audioPath", audioPath);
        startService(serviceIntent);

    }


    @Override
    protected void onPause() {
        if (validUtils.progress != null && validUtils.progress.isShowing())
            validUtils.hideProgressDialog();
        super.onPause();
    }


    private class PiideoServiceResultReceiver extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        PiideoServiceResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == 18 && resultData != null) {
                boolean finished = resultData.getBoolean("finished", false);
                String videoPath = resultData.getString("videoPath");
                if (finished) {
                    validUtils.hideProgressDialog();

                    Intent videoIntent = new Intent(PiideoActivity.this, PiideoPlayerActivity.class);
                    videoIntent.putExtra("videoPath", videoPath);
                    videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(videoIntent);
                }
            }
        }
    }

    /**************** Menu ********/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.piideo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.github:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/myinnos")));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}