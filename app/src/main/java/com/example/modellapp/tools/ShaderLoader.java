package com.example.modellapp.tools;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderLoader {
    final static String TAG = "ShaderLoading";

    public static int loadShader(int type, String shaderCode){
        int loadedShader = GLES20.glCreateShader(type);
        int[] compiled = new int[1];

        if(loadedShader == 0){
            Log.e(TAG, "Error at the initialisation of" + shaderCode + ".");
            return 0;
        }

        GLES20.glShaderSource(loadedShader, shaderCode);
        GLES20.glCompileShader(loadedShader);

        GLES20.glGetShaderiv(loadedShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if(compiled[0] == 0){
            Log.e(TAG, "Could not compile shader" + loadedShader);
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(loadedShader));

            GLES20.glDeleteShader(loadedShader);
            loadedShader = 0;
        }
        if(loadedShader == 0){
            throw new RuntimeException("Error creating vertex shader.");
        }
        return loadedShader;
    }
}
