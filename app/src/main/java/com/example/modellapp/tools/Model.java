package com.example.modellapp.tools;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.modellapp.R;
import com.example.modellapp.TextureHelper;
import com.example.modellapp.shaders.ObjectShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Model {

    private final int BYTES_PER_FLOAT = 4;
    private final int POS_DATA_SIZE = 3;
    private final int NORM_DATA_SIZE = 3;
    private final int TEXCOORD_DATA_SIZE = 2;
    final int STRIDE = (
            POS_DATA_SIZE +
                    NORM_DATA_SIZE +
                    TEXCOORD_DATA_SIZE) * BYTES_PER_FLOAT;

    public OBJLoader mObjLoader;
    private FloatBuffer mCubeBuffer;
    private final int mCubeBufferIdx;
    private int mMVMatrixHandle;
    private int mMVPMatrixHandle;
    private int mTextureUniformHandle;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mTextureCoordinateHandle;
    private int mLightPosHandle;
    private int brickDataHandle;
    private int grassDataHandle;
    private int textureUniformHandle;
    private int textureCoordinateHandle;

    private int queuedMinFilter;
    private int queuedMagFilter;

    public float[] mViewMatrix = new float[16];
    public float[] mModelMatrix = new float[16];
    public float[] mProjectionMatrix = new float[16];
    public float[] mMVPMatrix = new float[16];

    private float[] mLightPosInEyeSpace = new float[16];
    private final float[] lightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] lightPosInWorldSpace = new float[4];
    private final float[] lightPosInEyeSpace = new float[4];

    private int mProgramID;
    private int pointProgramID;
    private float[] mLightModelMatrix = new float[16];

    public Model(Context context){

        mObjLoader = new OBJLoader(context, "BB8.obj");

        final int cubeDataLength = mObjLoader.positions.length +
                mObjLoader.normals.length +
                mObjLoader.textureCoordinates.length;

        int cubePositionOffset = 0;
        int cubeNormalOffset = 0;
        int cubeTextureOffset = 0;

        mCubeBuffer = ByteBuffer.allocateDirect(cubeDataLength * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        for (int v = 0; v < 36; v++) {
            mCubeBuffer.put(mObjLoader.positions, cubePositionOffset, POS_DATA_SIZE);
            cubePositionOffset += POS_DATA_SIZE;
            mCubeBuffer.put(mObjLoader.normals, cubeNormalOffset, NORM_DATA_SIZE);
            cubeNormalOffset += NORM_DATA_SIZE;
            mCubeBuffer.put(mObjLoader.textureCoordinates, cubeTextureOffset, TEXCOORD_DATA_SIZE);
            cubeTextureOffset += TEXCOORD_DATA_SIZE;
        }
        mCubeBuffer.position(0);

        final int vertexBuffer[] = new int[1];
        GLES20.glGenBuffers(1, vertexBuffer, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0]);
        GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                mCubeBuffer.capacity() * BYTES_PER_FLOAT,
                mCubeBuffer,
                GLES20.GL_STATIC_DRAW);

        mCubeBufferIdx = vertexBuffer[0];
        mCubeBuffer.limit(0);
        mCubeBuffer = null;


        final int mVsID = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ObjectShader.getOVS());
        final int mFsID = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ObjectShader.getOFS());

        mProgramID = ProgramBuilder.buildProgram(
                mVsID,
                mFsID,
                new String[] {
                        "a_Position",
                        "a_Normal",
                        "a_TexCoordinate"});
        final int pointVertexShaderHandle = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ObjectShader.getPVS());
        final int pointFragmentShaderHandle = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ObjectShader.getPFS());

        pointProgramID =  ProgramBuilder.buildProgram(
                pointVertexShaderHandle,
                pointFragmentShaderHandle,
                new String[] {"a_Position"});

        brickDataHandle = TextureHelper.loadTexture(context, R.drawable.scratched);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        grassDataHandle = TextureHelper.loadTexture(context, R.drawable.noisy_grass_public_domain);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        if (queuedMinFilter != 0)
        {
            setMinFilter(queuedMinFilter);
        }

        if (queuedMagFilter != 0)
        {
            setMagFilter(queuedMagFilter);
        }

    }

    public void draw(){
        GLES20.glUseProgram(mProgramID);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramID, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramID, "u_MVMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramID, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramID, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramID, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramID, "a_TexCoordinate");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, brickDataHandle);
        GLES20.glUniform1i(textureUniformHandle, 0);

        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubeBufferIdx);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle,
                POS_DATA_SIZE, GLES20.GL_FLOAT,
                false,
                STRIDE,
                0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubeBufferIdx);
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(
                mNormalHandle,
                NORM_DATA_SIZE,
                GLES20.GL_FLOAT,
                false, STRIDE,
                POS_DATA_SIZE * BYTES_PER_FLOAT);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mCubeBufferIdx);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle,
                TEXCOORD_DATA_SIZE,
                GLES20.GL_FLOAT,
                false,
                STRIDE,
                (POS_DATA_SIZE + NORM_DATA_SIZE) * BYTES_PER_FLOAT);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
    }


    public void drawLight()
    {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(pointProgramID, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(pointProgramID, "a_Position");

        // Atadjuk a poziciot
        GLES20.glVertexAttrib3f(pointPositionHandle, lightPosInModelSpace[0], lightPosInModelSpace[1], lightPosInModelSpace[2]);

        // nem hasznalunk buffer objectet: dobjuk el a vertex array-eket
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Adjuk at a transzf. mx-ot
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Rajzoljuk ki
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    public void setMinFilter(final int filter)
    {
        if (brickDataHandle != 0 && grassDataHandle != 0)
        {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, brickDataHandle);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, filter);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, grassDataHandle);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, filter);
        }
        else
        {
            queuedMinFilter = filter;
        }
    }

    public void setMagFilter(final int filter)
    {
        if (brickDataHandle != 0 && grassDataHandle != 0)
        {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, brickDataHandle);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, filter);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, grassDataHandle);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, filter);
        }
        else
        {
            queuedMagFilter = filter;
        }
    }
}