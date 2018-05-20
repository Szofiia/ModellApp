package szsofia.thesis.modellapp.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureLoader{

    public static int loadTexture(final Context context, final int texID){
        final BitmapFactory.Options targetDensity = new BitmapFactory.Options();
        targetDensity.inScaled = false;
        final Bitmap decoded = BitmapFactory.decodeResource(context.getResources(), texID, targetDensity);

        final int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        if (texture[0] == 0){ throw new RuntimeException("Error: generating texture name failed."); }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

        GLUtils.texImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    decoded,
                    0);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        decoded.recycle();

        return texture[0];
    }
}

