package szsofia.thesis.modellapp.tools;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import szsofia.thesis.modellapp.shader_programme.ObjectShader;
import szsofia.thesis.modellapp.shader_programme.ProgramBuilder;
import szsofia.thesis.modellapp.shader_programme.ShaderLoader;
import szsofia.thesis.modellapp.shader_programme.TextureLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Model {

    private int COUNT;
    private final int BYTES_PER_FLOAT = 4;
    private final int POS_SIZE = 3;
    private final int NORM_SIZE = 3;
    private final int TEXCOORD_SIZE = 2;

    private int texData;
    private final int bufferIndex;
    private float[] mMVPMatrix;
    private int mProgramID;

    public Model(Context context, String FILE, int TEX){
        mMVPMatrix = new float[16];

        OBJLoader mObjLoader = new OBJLoader(context, FILE);
        bufferIndex = loadToArrayBuffer(mObjLoader);

        final int mVsID = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ObjectShader.getOVS());
        final int mFsID = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ObjectShader.getOFS());

        mProgramID = ProgramBuilder.buildProgram(
                mVsID,
                mFsID,
                new String[] {
                        "vs_in_Position",
                        "vs_in_Normal",
                        "vs_in_Tex_coordinate"});

        texData = TextureLoader.loadTexture(context, TEX);
    }

    public void draw(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix){
        int STRIDE = (
                POS_SIZE +
                NORM_SIZE +
                TEXCOORD_SIZE) * BYTES_PER_FLOAT;

        GLES20.glUseProgram(mProgramID);

        int MVP = GLES20.glGetUniformLocation(mProgramID, "u_MVPMatrix");
        int mWorld = GLES20.glGetUniformLocation(mProgramID, "u_MVMatrix");
        int texture = GLES20.glGetUniformLocation(mProgramID, "s_Texture");
        int light = GLES20.glGetAttribLocation(mProgramID, "u_LightPos");
        int positions = GLES20.glGetAttribLocation(mProgramID, "vs_in_Position");
        int normals = GLES20.glGetAttribLocation(mProgramID, "vs_in_Normal");
        int texCoords = GLES20.glGetAttribLocation(mProgramID, "vs_in_Tex_coordinate");
/*      A kommentelt rész a demonstráció szempontjából szükséges: a deferred shading funkcionalítással összehasonlításhoz.
        int ambientContent = GLES20.glGetUniformLocation(mProgramID, "ambient");
        int diffuseContent = GLES20.glGetUniformLocation(mProgramID, "diffuse");
        int specularContent = GLES20.glGetUniformLocation(mProgramID, "specular");*/
/*
        GLES20.glUniform4f(ambientContent, 0.25f, 0.20725f, 0.20725f, 1.0f);
        GLES20.glUniform4f(diffuseContent, 1.0f, 0.829f, 0.829f, 1.0f);
        GLES20.glUniform4f(specularContent,0.296648f, 0.296648f, 0.296648f, 1.0f);*/
        GLES20.glUniform3f(light, 2.0f, 2.0f, 2.0f);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texData);
        GLES20.glUniform1i(texture, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferIndex);
        GLES20.glEnableVertexAttribArray(positions);
        GLES20.glVertexAttribPointer(
                positions,
                POS_SIZE, GLES20.GL_FLOAT,
                false,
                STRIDE,
                0);

        GLES20.glEnableVertexAttribArray(normals);
        GLES20.glVertexAttribPointer(
                normals,
                NORM_SIZE,
                GLES20.GL_FLOAT,
                false, STRIDE,
                POS_SIZE * BYTES_PER_FLOAT);

        GLES20.glEnableVertexAttribArray(texCoords);
        GLES20.glVertexAttribPointer(
                texCoords,
                TEXCOORD_SIZE,
                GLES20.GL_FLOAT,
                false,
                STRIDE,
                (POS_SIZE + NORM_SIZE) * BYTES_PER_FLOAT);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mWorld, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(MVP, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, COUNT);
        GLES20.glDisableVertexAttribArray(positions);
        GLES20.glDisableVertexAttribArray(normals);
        GLES20.glDisableVertexAttribArray(texCoords);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private int loadToArrayBuffer(OBJLoader mObjLoader){
        COUNT = mObjLoader.getNumFaces();
        final int dataLength = mObjLoader.getPositions().length +
                mObjLoader.getNormals().length +
                mObjLoader.getTextureCoordinates().length;

        int posOffset = 0;
        int normOffset = 0;
        int texOffset = 0;

        FloatBuffer objectBuffer = ByteBuffer.allocateDirect(dataLength * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        for (int v = 0; v < COUNT; v++) {
            objectBuffer.put(mObjLoader.getPositions(), posOffset, POS_SIZE);
            posOffset += POS_SIZE;
            objectBuffer.put(mObjLoader.getNormals(), normOffset, NORM_SIZE);
            normOffset += NORM_SIZE;
            objectBuffer.put(mObjLoader.getTextureCoordinates(), texOffset, TEXCOORD_SIZE);
            texOffset += TEXCOORD_SIZE;
        }
        objectBuffer.position(0);

        final int vertexBuffer[] = new int[1];
        GLES20.glGenBuffers(1, vertexBuffer, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0]);
        GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                objectBuffer.capacity() * BYTES_PER_FLOAT,
                objectBuffer,
                GLES20.GL_STATIC_DRAW);

        int bufferIndex = vertexBuffer[0];
        objectBuffer.limit(0);

        return bufferIndex;
    }

}
