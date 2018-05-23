package szsofia.thesis.modellapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.thesis.modellapp.R;

public class ModelMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_menu);

        Button appleButt = findViewById(R.id.Apple);
        appleButt.setOnClickListener(v -> {
            Intent i = new Intent(ModelMenuActivity.this, SceneActivity.class);
            i.putExtra("_FILENAME","Apple.obj");
            i.putExtra("_TEXNAME", R.drawable.red_wax);
            startActivity(i);
        });

        Button handButt = findViewById(R.id.Hand);
        handButt.setOnClickListener(v -> {
            Intent i = new Intent(ModelMenuActivity.this, SceneActivity.class);
            i.putExtra("_FILENAME","Hand.obj");
            i.putExtra("_TEXNAME", R.drawable.skin);
            startActivity(i);
        });

        Button cubeButt = findViewById(R.id.Pot);
        cubeButt.setOnClickListener(v -> {
            Intent i = new Intent(ModelMenuActivity.this, SceneActivity.class);
            i.putExtra("_FILENAME","TeaPot.obj");
            i.putExtra("_TEXNAME", R.drawable.herend);
            startActivity(i);
        });
    }
}
