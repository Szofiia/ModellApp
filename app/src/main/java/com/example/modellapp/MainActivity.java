package com.example.modellapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.modellapp.tools.Camera;
import com.example.modellapp.tools.IO;
import com.example.modellapp.vecmath.Angles;
import com.example.modellapp.vecmath.Vector3f;

public class MainActivity extends AppCompatActivity {

    private Button openNewScene;
    private Button loadScene;

    static Camera[] mainCamera;

    static Vector3f mEye;
    static Vector3f mLookAt;
    static Vector3f mUp;
    static Angles mAngles;
    static boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainCamera = new Camera[8];
        mEye = new Vector3f();
        mLookAt = new Vector3f();
        mUp = new Vector3f();
        mAngles = new Angles();

        openNewScene= findViewById(R.id.newScene);
        openNewScene.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SceneActivity.class);
                startActivity(i);
            }
        });

        loadScene= findViewById(R.id.loadScene);
        loadScene.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SceneActivity.class);
                startActivity(i);
                onLoad();
            }
        });
    }

    public void onSave(){
        SceneActivity.mRenderer.onSave(this);
    }

    public void onLoad(){
        Camera mCamera = new Camera(IO.load(this));
        mEye = mCamera.getEye();
        mLookAt = mCamera.getLook();
        mUp = mCamera.getUp();
        mAngles = mCamera.getAngles();
        isLoaded = true;
        mainCamera[0] = mCamera;
    }

}
