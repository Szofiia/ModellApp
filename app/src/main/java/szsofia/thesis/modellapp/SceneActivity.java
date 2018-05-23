package szsofia.thesis.modellapp;

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

import com.thesis.modellapp.R;

import szsofia.thesis.modellapp.openGL.OpenGLRenderer;
import szsofia.thesis.modellapp.openGL.OpenGLSurfaceView;
import szsofia.thesis.modellapp.tools.SavedStage;
import szsofia.thesis.modellapp.vecmath.Point2f;
//TODO: light button at rotation gets blurry and stupid. Set the orientation only horizontal.
public class SceneActivity extends AppCompatActivity {
    private OpenGLSurfaceView openGLSurfaceView;
    private OpenGLRenderer mRenderer;
    boolean isLoaded;
    boolean[] lightsTurned;

    int width;
    int height;
    private final float MIN = 0.7f;
    private final float MAX = 2.5f;
    private Point2f actual;
    private Point2f old;
    private float lastScale;
    private boolean SINGLE_TOUCH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);

        final Button[] lightButtons = {
                findViewById(R.id.lighButton1),
                findViewById(R.id.lighButton2),
                findViewById(R.id.lighButton3),
                findViewById(R.id.lighButton4),
                findViewById(R.id.lighButton5),
                findViewById(R.id.lighButton6),
                findViewById(R.id.lighButton7)};

        actual = new Point2f(0,0);
        old = new Point2f(0,0);
        lastScale = 0;
        SINGLE_TOUCH = false;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        int TEXNAME;
        String FILENAME;
        String loadedFile;
        Intent i = getIntent();
        isLoaded = i.getBooleanExtra("_ISLOADED", false);
        if (isLoaded) {
            loadedFile = i.getStringExtra("_LOADEDFILE");
            SavedStage tempSavedStage = new SavedStage(loadedFile);
            FILENAME = tempSavedStage.getFILE();
            TEXNAME = tempSavedStage.getTEXTURE();
            lightsTurned = tempSavedStage.getLights();
            for(int l = 0; l < 7; ++l){
                if(lightsTurned[l]){
                    changeLightButton(lightButtons[l], lightsTurned[l]);
                }
            }
        } else {
            FILENAME = i.getStringExtra("_FILENAME");
            TEXNAME = i.getIntExtra("_TEXNAME", 0);
            loadedFile = "";
            lightsTurned = new boolean[]{false, false, false, false, false, false, false};
        }

        openGLSurfaceView = findViewById(R.id.openGLView);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        final ConfigurationInfo confInfo = activityManager.getDeviceConfigurationInfo();
        if (confInfo.reqGlEsVersion >= 0x30000){
            openGLSurfaceView.setEGLContextClientVersion(3);
            mRenderer = new OpenGLRenderer(this, FILENAME, TEXNAME, loadedFile);
            openGLSurfaceView.setRenderer(mRenderer);
        }else if(confInfo.reqGlEsVersion >= 0x20000){
           openGLSurfaceView.setEGLContextClientVersion(2);
            mRenderer = new OpenGLRenderer(this, FILENAME, TEXNAME, loadedFile);
            openGLSurfaceView.setRenderer(mRenderer);
        }else return;

        final Button flipYX = findViewById(R.id.flipYX);
        flipYX.setOnClickListener(v -> mRenderer.translateOnYX());
        final Button flipZX = findViewById(R.id.flipZX);
        flipZX.setOnClickListener(v -> mRenderer.translateOnZX());
        final Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> onSave(lightsTurned));

        for (int k = 0; k < 7; ++k) {
            final int l = k;
            lightButtons[l].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lightsTurned[l] = !lightsTurned[l];
                    changeLightButton(lightButtons[l], lightsTurned[l]);
                }
            });
        }
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
        switch (e.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                actual.x = e.getX();
                actual.y = e.getY();
                SINGLE_TOUCH = true;
                break;

            case MotionEvent.ACTION_MOVE:
                old.x = actual.x;
                old.y = actual.y;
                actual.x = e.getX();
                actual.y = e.getY();

                if(e.getPointerCount() >= 2){
                    SINGLE_TOUCH = false;
                    float TOUCH_SCALE =  Math.abs(old.x - actual.x) / 100.0f;
                    float scale = distance(e);

                    if(scale > lastScale){
                        if (mRenderer.scale - TOUCH_SCALE < MIN) {
                            mRenderer.scale = MIN;
                        }else mRenderer.scale -= TOUCH_SCALE;
                    }else{
                        if (mRenderer.scale + TOUCH_SCALE > MAX) {
                            mRenderer.scale = MAX;
                        }else mRenderer.scale += TOUCH_SCALE;
                    }
                    lastScale = scale;
                }
                if(SINGLE_TOUCH) {
                    float distanceX = (float) ((actual.x - old.x) / width * 2 * Math.PI);
                    float distanceY = (float) ((old.y - actual.y) / height * 2 * Math.PI);

                    mRenderer.cAngles.setTheta(mRenderer.cAngles.getTheta() + distanceY);
                    mRenderer.cAngles.setFi(mRenderer.cAngles.getFi() + distanceX);
                }
                break;

            case MotionEvent.ACTION_UP:
                old.x = 0;
                old.y = 0;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                old.setValues(0,0);
                actual.setValues(0,0);
                break;
        }
        return true;
    }

    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void changeLightButton(Button button, boolean turned){
        if(turned){
            button.setBackgroundResource(R.drawable.bulb_light);
        }else button.setBackgroundResource(R.drawable.bulb_norm);
    }

    public void onSave( boolean[] lights){
        mRenderer.onSave(this, lights);
    }
}
