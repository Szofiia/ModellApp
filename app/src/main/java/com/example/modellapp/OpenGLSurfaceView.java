package com.example.modellapp;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * OPENGL SurfaceView, tehát, létrehozok egy View-et, amire aztán
 * létrehozok egy contextet: itt fogok rajzolni
 */

public class OpenGLSurfaceView extends GLSurfaceView{

    //private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    //private float mPreviousX;
    //private float mPreviousY;

    public OpenGLSurfaceView(Context context) {
        super(context);
    }

    public OpenGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


}
