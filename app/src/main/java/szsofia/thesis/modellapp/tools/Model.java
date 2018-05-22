package szsofia.thesis.modellapp.tools;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import szsofia.thesis.modellapp.shaders.ObjectShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Model {

    private final int BYTES_PER_FLOAT = 4;
    private final int POS_DATA_SIZE = 3;
    private final int NORM_DATA_SIZE = 3;
    private final int TEXCOORD_DATA_SIZE = 2;

    private int texDataHandle;
    private final int bufferIndex;

    private float[] mMVPMatrix;

    private int mProgramID;
    private int COUNT;

    public Model(Context context, String FILE, int TEX){
        mMVPMatrix = new float[16];

        OBJLoader mObjLoader = new OBJLoader(context, FILE);
        bufferIndex = makeBuffer(mObjLoader);

        final int mVsID = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ObjectShader.getOVS());
        final int mFsID = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ObjectShader.getOFS());

        mProgramID = ProgramBuilder.buildProgram(
                mVsID,
                mFsID,
                new String[] {
                        "vs_in_Position",
                        "vs_in_Normal",
                        "vs_in_Tex_coordinate"});

        texDataHandle = TextureLoader.loadTexture(context, TEX);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
    }

    public void draw(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix){
        int STRIDE = (
                POS_DATA_SIZE +
                        NORM_DATA_SIZE +
                        TEXCOORD_DATA_SIZE) * BYTES_PER_FLOAT;

        GLES20.glUseProgram(mProgramID);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramID, "u_MVPMatrix");
        int mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramID, "u_MVMatrix");
        int mPositionHandle = GLES20.glGetAttribLocation(mProgramID, "vs_in_Position");
        int mNormalHandle = GLES20.glGetAttribLocation(mProgramID, "vs_in_Normal");
        int mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramID, "vs_in_Tex_coordinate");
        int ambientContent = GLES20.glGetUniformLocation(mProgramID, "ambient");
        int diffuseContent = GLES20.glGetUniformLocation(mProgramID, "diffuse");
        int specularContent = GLES20.glGetUniformLocation(mProgramID, "specular");

        GLES20.glUniform4f(ambientContent, 0.25f, 0.20725f, 0.20725f, 1.0f);
        GLES20.glUniform4f(diffuseContent, 1.0f, 0.829f, 0.829f, 1.0f);
        GLES20.glUniform4f(specularContent,0.296648f, 0.296648f, 0.296648f, 1.0f);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texDataHandle);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferIndex);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle,
                POS_DATA_SIZE, GLES20.GL_FLOAT,
                false,
                STRIDE,
                0);

        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(
                mNormalHandle,
                NORM_DATA_SIZE,
                GLES20.GL_FLOAT,
                false, STRIDE,
                POS_DATA_SIZE * BYTES_PER_FLOAT);

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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, COUNT);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private int makeBuffer(OBJLoader mObjLoader){
        COUNT = mObjLoader.numFaces;
        final int cubeDataLength = mObjLoader.positions.length +
                mObjLoader.normals.length +
                mObjLoader.textureCoordinates.length;

        int cubePositionOffset = 0;
        int cubeNormalOffset = 0;
        int cubeTextureOffset = 0;

        FloatBuffer mCubeBuffer = ByteBuffer.allocateDirect(cubeDataLength * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        for (int v = 0; v < COUNT; v++) {
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

        int bufferIndex = vertexBuffer[0];
        mCubeBuffer.limit(0);

        return bufferIndex;
    }

}
