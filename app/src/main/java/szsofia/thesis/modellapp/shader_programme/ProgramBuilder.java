package szsofia.thesis.modellapp.shader_programme;

import android.opengl.GLES20;
import android.util.Log;

public class ProgramBuilder {
    final static String TAG = "ProgramBuilding";

    public static int buildProgram(final int vsID, final int fsID, final String[] attributes){
        int[] result = new int[1];
        int mProgramID = GLES20.glCreateProgram();
        if(mProgramID == 0){
            throw new RuntimeException("Error: creating new program failed.");
        }
        GLES20.glAttachShader(mProgramID, vsID);
        GLES20.glAttachShader(mProgramID, fsID);

        if(attributes != null){
            int size = attributes.length;
            for (int i = 0; i < size; i++){
                GLES20.glBindAttribLocation(mProgramID, i, attributes[i]);
            }
        }
        GLES20.glLinkProgram(mProgramID);

        GLES20.glGetProgramiv(mProgramID, GLES20.GL_LINK_STATUS, result, 0);
        if(result[0] == 0){
            Log.e(TAG, " " + GLES20.glGetProgramInfoLog(mProgramID));

            GLES20.glDeleteProgram(mProgramID);
            mProgramID = 0;
        }
        if(mProgramID == 0){
            throw new RuntimeException("Hiba a program elkesziteseben.");
        }

        return mProgramID;
    }
}
