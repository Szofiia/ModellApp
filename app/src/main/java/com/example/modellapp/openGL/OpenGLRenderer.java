package com.example.modellapp.openGL;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.modellapp.tools.IO;
import com.example.modellapp.tools.SavedStage;
import com.example.modellapp.tools.Model;
import com.example.modellapp.vecmath.Angles;
import com.example.modellapp.vecmath.Vector3f;


public class OpenGLRenderer implements GLSurfaceView.Renderer
{
    private final Context mActivityContext;
    private final OpenGLSurfaceView mGlSurfaceView;

    private int TEX;
    private String FILE;
    private String newLoadedFile;
    private boolean isLoaded;
    private Model mModel;
    public Angles cAngles;

    public Vector3f eye;
    public Vector3f lookAt;
    public Vector3f up;

    private float[] modelMatrix;
    private float[] viewMatrix;
    private float[] projectionMatrix;

    public OpenGLRenderer(Context context, OpenGLSurfaceView glSurfaceView, String _FILE, int _TEX, String loadedFile){
        mActivityContext = context;
        mGlSurfaceView = glSurfaceView;
        cAngles = new Angles(0.0f,0.0f);
        FILE = _FILE;
        TEX = _TEX;
        modelMatrix = new float[16];
        projectionMatrix = new float[16];
        viewMatrix = new float[16];

        if (loadedFile != ""){
            isLoaded = true;
            newLoadedFile = loadedFile;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config){
        mModel = new Model(mActivityContext, FILE, TEX);

        GLES20.glClearColor(0.5546875f, 0.3515625f, 0.3515625f, 1.0f);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        if (isLoaded == false) {
            eye = new Vector3f(0.0f, 0.0f, 4.0f);
            lookAt = new Vector3f(0.0f, 0.0f, 0.0f);
            up = new Vector3f(0.0f, 1.0f, 0.0f);
        }else{
            SavedStage loadedSavedStage = new SavedStage(newLoadedFile);
            eye = new Vector3f(loadedSavedStage.getEye());
            lookAt = new Vector3f(loadedSavedStage.getLook());
            up = new Vector3f(loadedSavedStage.getUp());
            cAngles = new Angles(loadedSavedStage.getAngles());
            isLoaded = false;
        }
        Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 20.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
//TRANSZFORMÁCIÓK
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, 0.0f, 0.0f);
        Matrix.scaleM(modelMatrix, 0, 1.0F,mScale * 1.0F,1.0F);
        mModel.draw(modelMatrix, viewMatrix, projectionMatrix);
        Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
    }

    public void onSave(Context context){
        SavedStage mSavedStage = new SavedStage(eye, lookAt, up, cAngles, FILE, TEX);
        IO.save(context, mSavedStage.toString());
    }
    private long time() {
        return SystemClock.uptimeMillis() % 10000L;
    }



//TRANSZFORMÁCIÓK

    public volatile float mScale = 1.0f;

    public float getScale(){
        return mScale;
    }
    public void setScale(float scale){
        mScale = scale;
    }
}
