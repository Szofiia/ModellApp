package com.example.modellapp;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Shader;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.modellapp.shaders.ObjectShader;
import com.example.modellapp.tools.IO;
import com.example.modellapp.tools.Camera;
import com.example.modellapp.tools.Model;
import com.example.modellapp.tools.OBJLoader;
import com.example.modellapp.tools.ProgramBuilder;
import com.example.modellapp.tools.ShaderLoader;
import com.example.modellapp.vecmath.Angles;
import com.example.modellapp.vecmath.Vector3f;


public class OpenGLRenderer implements GLSurfaceView.Renderer
{
    private final Context mActivityContext;
    private final OpenGLSurfaceView mGlSurfaceView;

    int TEX;
    String FILE;
    private Model mModel;
    public Angles cAngles;

    Vector3f eye;
    Vector3f lookAt;
    Vector3f up;

    private float[] mMVPMatrix;
    private float[] mProjectionMatrix;
    private float[] mViewMatrix;

    private float[] mLightModelMatrix = new float[16];
    private final float[] lightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] lightPosInWorldSpace = new float[4];
    private final float[] lightPosInEyeSpace = new float[4];

    private int pointProgramID;

    public OpenGLRenderer(Context context, OpenGLSurfaceView glSurfaceView, String _FILE, int _TEX){
        mActivityContext = context;
        mGlSurfaceView = glSurfaceView;
        cAngles = new Angles(0.0f,0.0f);
        FILE = _FILE;
        TEX = _TEX;

        mMVPMatrix = new float[16];
        mProjectionMatrix = new float[16];
        mViewMatrix = new float[16];

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        mModel = new Model(mActivityContext, FILE, TEX);

        GLES20.glClearColor(0.5546875f, 0.3515625f, 0.3515625f, 1.0f);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        if (MainActivity.isLoaded == false) {
            eye = new Vector3f(0.0f, 0.0f, 4.0f);
            lookAt = new Vector3f(0.0f, 0.0f, 0.0f);
            up = new Vector3f(0.0f, 1.0f, 0.0f);
            MainActivity.isLoaded = false;
        }else{
            if(MainActivity.mEye != null && MainActivity.mLookAt != null &&
                MainActivity.mUp != null && MainActivity.mAngles != null){
                eye = new Vector3f(MainActivity.mEye);
                lookAt = new Vector3f(MainActivity.mLookAt);
                up = new Vector3f(MainActivity.mUp);
                cAngles = new Angles(MainActivity.mAngles);
            }
        }
        Matrix.setLookAtM(mModel.mViewMatrix, 0, eye.x, eye.y, eye.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
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

        Matrix.frustumM(mModel.mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 2.0f, -5.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(lightPosInWorldSpace, 0, mLightModelMatrix, 0, lightPosInModelSpace, 0);
        Matrix.multiplyMV(lightPosInEyeSpace, 0, mModel.mViewMatrix, 0, lightPosInWorldSpace, 0);

        // Kockak rajzolasa
        Matrix.setIdentityM(mModel.mModelMatrix, 0);
        Matrix.translateM(mModel.mModelMatrix, 0, 0.0f, 0.0f, 0.0f);
        Matrix.scaleM(mModel.mModelMatrix, 0, 0.8F,mScale * 0.8F,0.8F);
        mModel.draw();
        Matrix.setLookAtM(mModel.mViewMatrix, 0, eye.x, eye.y, eye.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);

        GLES20.glUseProgram(pointProgramID);

        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, -2.0f, 0.0f);
        mModel.drawLight();
    }

    private long time() {
        return SystemClock.uptimeMillis() % 10000L;
    }

    public volatile float mScale = 1.0f;


    public float getScale(){
        return mScale;
    }

    public void setScale(float scale){
        mScale = scale;
    }

    public void onSave(Context context){
        Camera mCamera = new Camera(eye, lookAt, up, cAngles);
        IO.save(context, mCamera.toString());
    }

    public void onLoad(Context context){
        Camera mCamera = new Camera(IO.load(context));
        eye = mCamera.getEye();
        lookAt = mCamera.getLook();
        up = mCamera.getUp();

    }


}
