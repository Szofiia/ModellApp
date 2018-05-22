package szsofia.thesis.modellapp.tools;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class IO {

    private static final String FILE_NAME = "save.txt";

    public static  void save(Context context, String text){
        try{
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String load(Context context){
        String result = "";
        try{
            FileInputStream fis = context.openFileInput(FILE_NAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            result = new String(buffer);
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
