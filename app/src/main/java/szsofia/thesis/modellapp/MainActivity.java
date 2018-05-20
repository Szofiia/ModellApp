package szsofia.thesis.modellapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.thesis.modellapp.R;

import szsofia.thesis.modellapp.tools.IO;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button openNewScene= findViewById(R.id.newScene);
        openNewScene.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ModelMenuActivity.class);
            startActivity(i);
        });

        final Button loadScene= findViewById(R.id.loadScene);
        loadScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!onLoad()){
                    Intent i = new Intent(MainActivity.this, LoadActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    public boolean onLoad(){
        String loaded = IO.load(this);
        if(loaded == null || loaded.isEmpty()) {
            return false;
        }else{
            Intent i = new Intent(MainActivity.this, SceneActivity.class);
            i.putExtra("_LOADEDFILE", loaded);
            i.putExtra("_ISLOADED", true);
            startActivity(i);
        }
        return true;
    }
}
