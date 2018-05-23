package szsofia.thesis.modellapp.openGL;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import szsofia.thesis.modellapp.tools.IO;
import szsofia.thesis.modellapp.tools.SavedStage;
import szsofia.thesis.modellapp.tools.Model;
import szsofia.thesis.modellapp.vecmath.Angles;
import szsofia.thesis.modellapp.vecmath.Vector3f;

public class OpenGLRenderer implements GLSurfaceView.Renderer
{
    private final Context mActivityContext;

    private int TEX;
    private String FILE;
    private String newLoadedFile;
    private boolean isLoaded;
    private Model mModel;
    public Angles cAngles;
    public float scale;

    private Vector3f eye;
    private Vector3f lookAt;
    public Vector3f up;

    private float[] modelMatrix;
    private float[] viewMatrix;
    private float[] projectionMatrix;


    public OpenGLRenderer(Context context, String _FILE, int _TEX, String loadedFile){
        mActivityContext = context;
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
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config){
        mModel = new Model(mActivityContext, FILE, TEX);

        GLES20.glClearColor(0.5546875f, 0.3515625f, 0.3515625f, 1.0f);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        if (isLoaded == false) {
            newView();
        }else{
            loadView(); }

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
        transformEvents();
        mModel.draw(modelMatrix, viewMatrix, projectionMatrix);
    }

    void newView(){
        eye = new Vector3f(0.0f, 0.0f, 4.0f);
        lookAt = new Vector3f(0.0f, 0.0f, 0.0f);
        up = new Vector3f(0.0f, 1.0f, 0.0f);
    }

    void loadView(){
        SavedStage loadedSavedStage = new SavedStage(newLoadedFile);
        eye = new Vector3f(loadedSavedStage.getEye());
        lookAt = new Vector3f(loadedSavedStage.getLookAt());
        up = new Vector3f(loadedSavedStage.getUp());
        cAngles = new Angles(loadedSavedStage.getAngles());
        scale = loadedSavedStage.getScale();
        isLoaded = false;
    }

    void transformEvents(){
        float theta = cAngles.getTheta();
        float fi = cAngles.getFi();
        float R = 4.0f;

        eye.setValues(
                (R * scale) * Math.sin(theta) * Math.cos(fi),
                (R * scale) * Math.cos(theta),
                (R * scale) * Math.sin(theta) * Math.sin(fi)
        );
        up.cross(eye.multipyBy(-1), new Vector3f((float)Math.sin(fi), 0.0f, (-1)*(float)Math.cos(fi)));

        Matrix.setLookAtM(viewMatrix, 0, eye.x, eye.y, eye.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public void onSave(Context context,  boolean[] lights){
        SavedStage mSavedStage = new SavedStage(eye, lookAt, up, cAngles, scale, lights,FILE, TEX);
        IO.save(context, mSavedStage.toString());
    }

    public void translateOnYX(){
        cAngles.setFi(2 * Math.PI - cAngles.getFi());
    }

    public void translateOnZX(){
        cAngles.setTheta(2 * Math.PI - cAngles.getTheta());
    }
}
