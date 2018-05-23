package szsofia.thesis.modellapp.shader_programme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureLoader{

    public static int loadTexture(final Context context, final int texID) {

        final int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);

        if(texture[0] != 0){
            final BitmapFactory.Options targetDensity = new BitmapFactory.Options();
            targetDensity.inScaled = false;
            final Bitmap decoded = BitmapFactory.decodeResource(context.getResources(), texID, targetDensity);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLUtils.texImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    decoded,
                    0);

            decoded.recycle();
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
        if (texture[0] == 0){ throw new RuntimeException("Error: generating texture name failed."); }

        return texture[0];
    }
}

