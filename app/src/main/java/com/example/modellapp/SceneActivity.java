package com.example.modellapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class SceneActivity extends AppCompatActivity {

    String FILENAME;
    int TEXNAME;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private float mMidPointX;
    private float mMidPointY;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int m_Gesture = NONE;

    private OpenGLSurfaceView openGLSurfaceView;
    static OpenGLRenderer mRenderer ;

    private Button saveButton;
    private Button loadButton;
    int width;
    int height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        FILENAME = i.getStringExtra("_FILENAME");
        TEXNAME = i.getIntExtra("_TEXNAME", 0);

        setContentView(R.layout.activity_scene);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //Megekressuk a SurfaceView-t
        openGLSurfaceView = (OpenGLSurfaceView) findViewById(R.id.openGLView);

        //Megnezzuk, hogy a rendszer tamogatja-e az ES 2.0-t
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo confInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsES3 = confInfo.reqGlEsVersion >= 0x30000;

        if (supportsES3){
            // Keszitsunk OpenGL ES 3.0 context-et
            openGLSurfaceView.setEGLContextClientVersion(3);
            mRenderer = new OpenGLRenderer(this, openGLSurfaceView, FILENAME, TEXNAME);
            openGLSurfaceView.setRenderer(mRenderer);
        }else if(confInfo.reqGlEsVersion >= 0x20000){
            // Keszitsunk OpenGL ES 2.0 context-et
            openGLSurfaceView.setEGLContextClientVersion(2);
            mRenderer = new OpenGLRenderer(this, openGLSurfaceView, FILENAME, TEXNAME);
            openGLSurfaceView.setRenderer(mRenderer);
        }else{
            return;
        }

        final Button flipB= findViewById(R.id.flipButton);
        flipB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mRenderer.setScale(
                        mRenderer.getScale() * -1 );
            }
        });

        saveButton = findViewById(R.id.saveButton);
        loadButton = findViewById(R.id.loadButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSave();
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onLoad();
            }
        });
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

    public void onLoad(){
        mRenderer.onLoad(this);
    }
}
