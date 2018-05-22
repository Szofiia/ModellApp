package szsofia.thesis.modellapp.shader_tools;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
import static android.opengl.GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
import static android.opengl.GLES20.GL_FRAMEBUFFER_UNSUPPORTED;
import static android.opengl.GLES20.GL_RGB;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glDeleteRenderbuffers;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;

public class GBuffer {
    private int frameBuffer[];
    int texBuffer[];
    int depthBuffer[];
    private boolean created;
    private int[] posBuffer;
    private int[] normBuffer;
    private int[] lightBuffer;

    String TAG ;

    public GBuffer(){
        TAG = "GBuffer: ";
        created = false;
        frameBuffer = new int[1];
        depthBuffer = new int[1];
        posBuffer = new int[1];
        normBuffer = new int[1];
        texBuffer = new int[1];
        lightBuffer = new int[1];
    }
    public void cleanGBuffer(){}

    public void DrawInto(){
        GLES20.glClearColor(0.5546875f, 0.3515625f, 0.3515625f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

    }

    public void drawLights(){

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean startGBuffer(int _width, int _height){
        if(created){
            GLES30.glDeleteTextures(1, posBuffer, 0);
            GLES30.glDeleteTextures(1, normBuffer, 0);
            GLES30.glDeleteTextures(1, texBuffer, 0);
            GLES30.glDeleteTextures(1, lightBuffer, 0);
            GLES30.glDeleteTextures(1, depthBuffer, 0);
        }

        glGenFramebuffers(1, frameBuffer, 0);
        glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer[0]);

        glGenTextures(1, posBuffer, 0);
        glBindTexture(GL_TEXTURE_2D, posBuffer[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GLES30.GL_RGB32F, _width, _height, 0, GL_RGB, GL_FLOAT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, posBuffer[0], 0);

        glGenTextures(1, normBuffer, 0);
        glBindTexture(GL_TEXTURE_2D, normBuffer[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GLES30.GL_RGB32F, _width, _height, 0, GL_RGB, GL_FLOAT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, normBuffer[0], 0);

        glGenTextures(1, texBuffer, 0);
        glBindTexture(GL_TEXTURE_2D, texBuffer[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GLES30.GL_RGB32F, _width, _height, 0, GL_RGB, GL_FLOAT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, texBuffer[0], 0);

        glGenTextures(1, lightBuffer, 0);
        glBindTexture(GL_TEXTURE_2D, lightBuffer[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GLES30.GL_RGB32F, _width, _height, 0, GL_RGB, GL_FLOAT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, lightBuffer[0], 0);

        GLES30.glGenTextures(1, depthBuffer, 0);
        GLES30.glBindTexture(GL_TEXTURE_2D, depthBuffer[0]);
        GLES30.glTexImage2D(GL_TEXTURE_2D, 0, GLES30.GL_DEPTH_COMPONENT32F, _width, _height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthBuffer[0], 0);

        int drawBuffers[] = {
                GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_COLOR_ATTACHMENT1,
                GLES30.GL_COLOR_ATTACHMENT2,
                GLES30.GL_COLOR_ATTACHMENT3};

        GLES30.glDrawBuffers(4, drawBuffers, 0);
        glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, 0);

        int status = GLES30.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if(status != GL_FRAMEBUFFER_COMPLETE){
            Log.e(TAG, "Framebuffer incomplete: ");

            switch (status){
                case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    Log.e(TAG, "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
                    break;
                case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                    Log.e(TAG, "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
                    break;
                case GL_FRAMEBUFFER_UNSUPPORTED:
                    Log.e(TAG, "GL_FRAMEBUFFER_UNSUPPORTED");
                    break;
            }
            return false;
        }

        created = true;
        return created;
    }

    public void bindFBOForWriting(){
        GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, frameBuffer[0]);
    }

    public void bindFBOForReading(){
        GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, frameBuffer[0]);
    }

}
