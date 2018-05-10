package com.example.modellapp.tools;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class IO {

    private static final String FILE_NAME = "save.txt";

    public static  void save(Context cx, String text){
        try{
            FileOutputStream fos = cx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static  void add(Context cx, String text){
        String old = load(cx);

        old += text;

        try{
            FileOutputStream fos = cx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(old.getBytes());
            fos.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String load(Context cx){
        String toReturn = "";
        try{
            FileInputStream fis = cx.openFileInput(FILE_NAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            toReturn = new String(buffer);
        } catch (Exception e){
            e.printStackTrace();
        }
        return toReturn;

    }
}
