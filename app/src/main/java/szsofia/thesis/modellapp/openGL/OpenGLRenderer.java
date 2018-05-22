package szsofia.thesis.modellapp.openGL;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.transition.Scene;
import android.util.Log;

import szsofia.thesis.modellapp.SceneActivity;
import szsofia.thesis.modellapp.tools.IO;
import szsofia.thesis.modellapp.tools.SavedStage;
import szsofia.thesis.modellapp.vecmath.Angles;
import szsofia.thesis.modellapp.vecmath.Vector3f;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
import static android.opengl.GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
import static android.opengl.GLES20.GL_FRAMEBUFFER_UNSUPPORTED;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RGB;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;
//TODO: make more save files
//TODO: saving the scale
//TODO: sAVE WITH FLIP also changing the flip direction

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
    public float scale;

    public Vector3f eye;
    public Vector3f lookAt;
    public Vector3f up;

    private float[] modelMatrix;
    private float[] viewMatrix;
    private float[] projectionMatrix;


    public OpenGLRenderer(Context context, OpenGLSurfaceView glSurfaceView, String _FILE, int _TEX, String loadedFile){
        mActivityContext = context;
        mGlSurfaceView = glSurfaceView;
        cAngles = new Angles((float)-Math.PI /2,(float) Math.PI/2);
        scale = 1;
        FILE = _FILE;
        TEX = _TEX;
        modelMatrix = new float[16];
        projectionMatrix = new float[16];
        viewMatrix = new float[16];

        if (loadedFile != ""){
            isLoaded = true;
            newLoadedFile = loadedFile;
        }

        TAG = "GBuffer: ";
        created = false;
        frameBuffer = new int[1];
        depthBuffer = new int[1];
        posBuffer = new int[1];
        normBuffer = new int[1];
        texBuffer = new int[1];
        lightBuffer = new int[1];
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config){
        mModel = new Model(mActivityContext, FILE, TEX);

        GLES20.glClearColor(0.5546875f, 0.3515625f, 0.3515625f, 1.0f);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        startGBuffer(SceneActivity.width, SceneActivity.height);

        if (isLoaded == false) {
            eye = new Vector3f(0.0f, 0.0f, 4.0f);
            lookAt = new Vector3f(0.0f, 0.0f, 0.0f);
            up = new Vector3f(0.0f, 1.0f, 0.0f);
        }else{
            SavedStage loadedSavedStage = new SavedStage(newLoadedFile);
            eye = new Vector3f(loadedSavedStage.getEye());
            lookAt = new Vector3f(loadedSavedStage.getLookAt());
            up = new Vector3f(loadedSavedStage.getUp());
            cAngles = new Angles(loadedSavedStage.getAngles());
            scale = loadedSavedStage.getScale();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

//TRANSZFORMÁCIÓK
        //gömbi koordináták:
        // theta és fi felcserélve (theta eleme 0-pi)
        // mivel a felfele mutató irány mindig az y, ezért a setlookat függvénynél a kamera koordinátarendszere
        //aképp fog zámolódni
        //A kamera "jobb oldali" tengelye ezért elfordul, így a képet mindig tükrösen látjuk, nem pedig egyenesen előre
        //ezért az up irányt nem az y-ba fogjuk tenni, hanem mingig a z-x tengely síkjűban lévő kör befele mutató iránya
        //illetve az eye befele mutató vektorának vektoriális szorzatába.
        //ezzel a kamera felfele mutató iránya is jó lesz

//        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);
        DrawInto();
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        //LightPass();
    }

    public void onSave(Context context,  boolean[] lights){
        SavedStage mSavedStage = new SavedStage(eye, lookAt, up, cAngles, scale, lights,FILE, TEX);
        IO.save(context, mSavedStage.toString());
    }

    private long time() {
        return SystemClock.uptimeMillis() % 10000L;
    }

    public void translateOnYX(){
        cAngles.setFi(2 * Math.PI - cAngles.getFi());
    }

    public void translateOnZX(){
        cAngles.setTheta(2 * Math.PI - cAngles.getTheta());
    }

    private int frameBuffer[];
    int texBuffer[];
    int depthBuffer[];
    private boolean created;
    private int[] posBuffer;
    private int[] normBuffer;
    private int[] lightBuffer;

    String TAG ;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean startGBuffer(int _width, int _height){
        if(created){
            GLES30.glDeleteTextures(1, posBuffer, 0);
            GLES30.glDeleteTextures(1, normBuffer, 0);
            GLES30.glDeleteTextures(1, texBuffer, 0);
            GLES30.glDeleteTextures(1, lightBuffer, 0);
            GLES30.glDeleteTextures(1, depthBuffer, 0);
        }

        glGenFramebuffers(1, frameBuffer, 0);
        glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer[0]);

        glGenTextures(1, posBuffer, 0);
        glBindTexture(GL_TEXTURE_2D, posBuffer[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GLES30.GL_RGB32F, _width, _height, 0, GL_RGB, GL_FLOAT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, posBuffer[0], 0);

        glGenTextures(1, normBuffer, 0);
        glBindTexture(GL_TEXTURE_2D, normBuffer[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GLES30.GL_RGB32F, _width, _height, 0, GL_RGB, GL_FLOAT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, normBuffer[0], 0);

        glGenTextures(1, texBuffer, 0);
        glBindTexture(GL_TEXTURE_2D, texBuffer[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GLES30.GL_RGB32F, _width, _height, 0, GL_RGB, GL_FLOAT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, texBuffer[0], 0);

        glGenTextures(1, lightBuffer, 0);
        glBindTexture(GL_TEXTURE_2D, lightBuffer[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GLES30.GL_RGB32F, _width, _height, 0, GL_RGB, GL_FLOAT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, lightBuffer[0], 0);

        GLES30.glGenTextures(1, depthBuffer, 0);
        GLES30.glBindTexture(GL_TEXTURE_2D, depthBuffer[0]);
        GLES30.glTexImage2D(GL_TEXTURE_2D, 0, GLES30.GL_DEPTH_COMPONENT32F, _width, _height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthBuffer[0], 0);

        int drawBuffers[] = {
                GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_COLOR_ATTACHMENT1,
                GLES30.GL_COLOR_ATTACHMENT2,
                GLES30.GL_COLOR_ATTACHMENT3};

        GLES30.glDrawBuffers(4, drawBuffers, 0);
        glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, 0);

        int status = GLES30.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if(status != GL_FRAMEBUFFER_COMPLETE){
            Log.e(TAG, "Framebuffer incomplete: ");

            switch (status){
                case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    Log.e(TAG, "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
                    break;
                case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                    Log.e(TAG, "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
                    break;
                case GL_FRAMEBUFFER_UNSUPPORTED:
                    Log.e(TAG, "GL_FRAMEBUFFER_UNSUPPORTED");
                    break;
            }
            return false;
        }

        created = true;
        return created;
    }

    public void DrawInto(){
        GLES20.glClearColor(0.5546875f, 0.3515625f, 0.3515625f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        glEnable(GLES20.GL_DEPTH_TEST);

        float theta = cAngles.getTheta();
        float fi = cAngles.getFi();

        eye.setValues(
                (4.0f* scale) * Math.sin(theta) * Math.cos(fi),
                (4.0f* scale) * Math.cos(theta),
                (4.0f* scale) * Math.sin(theta) * Math.sin(fi)
        );
        up.cross(eye.multipyBy(-1), new Vector3f((float)Math.sin(fi), 0.0f, (-1)*(float)Math.cos(fi)));

        Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
        Matrix.setIdentityM(modelMatrix, 0);
        mModel.draw(modelMatrix, viewMatrix, projectionMatrix);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void LightPass(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);



        GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, frameBuffer[0]);



        int halfHeight = SceneActivity.height / 2;
        int halfWidth = SceneActivity.width / 2;

        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT0);
        GLES30.glBlitFramebuffer(0,0, SceneActivity.width, SceneActivity.height,
                0, 0, halfWidth, halfHeight, GLES30.GL_COLOR_BUFFER_BIT, GL_LINEAR);

        GLES20.glUseProgram(mModel.mProgramID);

        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT1);/*
        GLES30.glBlitFramebuffer(0,0, SceneActivity.width, SceneActivity.height,
                0, halfHeight, halfWidth, SceneActivity.height, GLES30.GL_COLOR_BUFFER_BIT, GL_LINEAR);
        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT2);
        GLES30.glBlitFramebuffer(0,0, SceneActivity.width, SceneActivity.height,
                halfWidth, halfHeight, SceneActivity.width, SceneActivity.height, GLES30.GL_COLOR_BUFFER_BIT, GL_LINEAR);
        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT3);
        GLES30.glBlitFramebuffer(0,0, SceneActivity.width, SceneActivity.height,
                halfWidth, 0,  SceneActivity.width, halfHeight, GLES30.GL_COLOR_BUFFER_BIT, GL_LINEAR);*/
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    void setReadBuffer(int texType){
        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT0 + texType);

    }


}
