package com.example.modellapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.Button;

import com.example.modellapp.openGL.OpenGLRenderer;
import com.example.modellapp.openGL.OpenGLSurfaceView;
import com.example.modellapp.tools.SavedStage;

public class SceneActivity extends AppCompatActivity {
    boolean isLoaded;
    String loadedFile;
    String FILENAME;
    int TEXNAME;
    int width;
    int height;

    private OpenGLSurfaceView openGLSurfaceView;
    static OpenGLRenderer mRenderer ;
    private Button saveButton;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private float mMidPointX;
    private float mMidPointY;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int m_Gesture = NONE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        {
            Intent i = getIntent();
            isLoaded = i.getBooleanExtra("_ISLOADED", false);
            if (isLoaded) {
                loadedFile = i.getStringExtra("_LOADEDFILE");
                SavedStage tempSavedStage = new SavedStage(loadedFile);
                FILENAME = tempSavedStage.getFILE();
                TEXNAME = tempSavedStage.getTEXTURE();
            } else {
                FILENAME = i.getStringExtra("_FILENAME");
                TEXNAME = i.getIntExtra("_TEXNAME", 0);
                loadedFile = "";
            }
        }

        openGLSurfaceView = (OpenGLSurfaceView) findViewById(R.id.openGLView);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo confInfo = activityManager.getDeviceConfigurationInfo();

        if (confInfo.reqGlEsVersion >= 0x30000){
            openGLSurfaceView.setEGLContextClientVersion(3);
            mRenderer = new OpenGLRenderer(this, openGLSurfaceView, FILENAME, TEXNAME, loadedFile);
            openGLSurfaceView.setRenderer(mRenderer);
        }else if(confInfo.reqGlEsVersion >= 0x20000){
           openGLSurfaceView.setEGLContextClientVersion(2);
            mRenderer = new OpenGLRenderer(this, openGLSurfaceView, FILENAME, TEXNAME, loadedFile);
            openGLSurfaceView.setRenderer(mRenderer);
        }else{
            return;
        }

        final Button flipX = findViewById(R.id.flipX);
        final Button flipY = findViewById(R.id.flipY);
        final Button flipZ = findViewById(R.id.flipZ);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> onSave());
    }

    @Override
    protected void onResume() {
        super.onResume();
        openGLSurfaceView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        openGLSurfaceView.onPause();
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        boolean retVal = true;

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                mRenderer.cAngles.setTheta(
                        mRenderer.cAngles.getTheta() -
                                ((x - mPreviousX) / width * 2 * (float) Math.PI));
                mRenderer.cAngles.setFi(
                        mRenderer.cAngles.getFi() -
                                ((y - mPreviousY) / width * 2 * (float) Math.PI));

                float theta = mRenderer.cAngles.getTheta();
                float fi = mRenderer.cAngles.getFi();

                mRenderer.eye.x = 4.0f * (float) Math.sin(fi) * (float) Math.sin(theta);
//                if (Math.floor(fi / Math.PI) == 0) {
                mRenderer.eye.y = -4.0f * (float) Math.sin(fi) * (float) Math.cos(theta);
//                } else {
//                    mRenderer.eye.y = 4.0f * (float) Math.cos(fi);
//                }
                mRenderer.eye.z = 4.0f * (float) (float) Math.cos(fi);
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
       // return retVal;
    }

    public void onSave(){
        mRenderer.onSave(this);
    }
}
