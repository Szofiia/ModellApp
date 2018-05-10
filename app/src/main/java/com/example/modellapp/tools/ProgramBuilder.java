package com.example.modellapp.tools;

import android.opengl.GLES20;
import android.util.Log;

public class ProgramBuilder {
    final static String TAG = "ProgramBuilding";

    public static int buildProgram(final int vsID, final int fsID, final String[] attributes){
        int[] result = new int[1];
        int mProgramID = GLES20.glCreateProgram();

        if(mProgramID == 0){
            throw new RuntimeException("Error creating program.");
        }
        //Hozzaadom a programhoz a shadereket
        GLES20.glAttachShader(mProgramID, vsID);
        GLES20.glAttachShader(mProgramID, fsID);
        //Bindolás
        if(attributes != null){
            final int size = attributes.length;
            for (int i = 0; i < size; i++)
            {
                GLES20.glBindAttribLocation(mProgramID, i, attributes[i]);
            }
        }
        //Shaderek osszeillesztese
        GLES20.glLinkProgram(mProgramID);

        //TAG irasa a program betolteshez
        GLES20.glGetProgramiv(mProgramID, GLES20.GL_LINK_STATUS, result, 0);
        if(result[0] == 0){
            Log.e(TAG, " " + GLES20.glGetProgramInfoLog(mProgramID));
            //toroljuk a programot
            GLES20.glDeleteProgram(mProgramID);
            mProgramID = 0;
        }
        if(mProgramID == 0){
            throw new RuntimeException("Hiba a program elkesziteseben.");
        }
        return mProgramID;
    }
}
