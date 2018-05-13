package com.example.modellapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.modellapp.tools.SavedStage;
import com.example.modellapp.tools.IO;

public class MainActivity extends AppCompatActivity {
    private Button openNewScene;
    private Button loadScene;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openNewScene= findViewById(R.id.newScene);
        openNewScene.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ModelMenuActivity.class);
            startActivity(i);
        });

        loadScene= findViewById(R.id.loadScene);
        loadScene.setOnClickListener(v -> onLoad());
    }

    public void onLoad(){
        String loaded = IO.load(this);

        Intent i = new Intent(MainActivity.this, SceneActivity.class);
        i.putExtra("_LOADEDFILE", loaded);
        i.putExtra("_ISLOADED", true);
        startActivity(i);
    }
}
