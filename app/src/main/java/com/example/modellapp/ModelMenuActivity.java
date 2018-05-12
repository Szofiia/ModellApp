package com.example.modellapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class ModelMenuActivity extends AppCompatActivity {

    Button appleButt;
    Button suzanneButt;
    Button cubeButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_menu);


        appleButt = findViewById(R.id.Apple);
        appleButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ModelMenuActivity.this, SceneActivity.class);
                i.putExtra("_FILENAME","Apple_smooth.obj");
                i.putExtra("_TEXNAME", R.drawable.wax);

                startActivity(i);
            }
        });

        suzanneButt = findViewById(R.id.Suzanne);
        suzanneButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ModelMenuActivity.this, SceneActivity.class);
                i.putExtra("_FILENAME","Hand.obj");
                i.putExtra("_TEXNAME", R.drawable.fur_brown);

                startActivity(i);
            }
        });

        cubeButt = findViewById(R.id.Cube);
        cubeButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(ModelMenuActivity.this, SceneActivity.class);
                i.putExtra("_FILENAME","Square.obj");
                i.putExtra("_TEXNAME", R.drawable.random);

                startActivity(i);
            }
        });
    }

}
