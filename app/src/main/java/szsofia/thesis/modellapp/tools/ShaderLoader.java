package szsofia.thesis.modellapp.tools;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderLoader {
    final static String TAG = "ShaderLoading";

    public static int loadShader(int _shaderType, String _shaderCode){
        int loadedShader = GLES20.glCreateShader(_shaderType);

        if(loadedShader == 0){
            Log.e(TAG, "Error at the initialisation of" + _shaderCode + ".");
            return 0;
        }

        GLES20.glShaderSource(loadedShader, _shaderCode);
        GLES20.glCompileShader(loadedShader);

        int[] compiled = new int[1];
        GLES20.glGetShaderiv(loadedShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if(compiled[0] == 0){
            Log.e(TAG, "Error: Could not compile shader " + loadedShader + " with infoLog:" + GLES20.glGetShaderInfoLog(loadedShader));

            GLES20.glDeleteShader(loadedShader);
            loadedShader = 0;
        }
        if(loadedShader == 0){
            throw new RuntimeException("Error: generating shader failed.");
        }

        return loadedShader;
    }
}
