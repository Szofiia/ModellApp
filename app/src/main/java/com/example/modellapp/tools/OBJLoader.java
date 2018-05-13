package com.example.modellapp.tools;

import android.content.Context;
import android.util.Log;

import com.example.modellapp.vecmath.Point2f;
import com.example.modellapp.vecmath.Vector3f;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class OBJLoader {
    public final int numFaces;
    public final float[] normals;
    public final float[] textureCoordinates;
    public final float[] positions;
    public boolean hasNoTex = false;

    public String TAG = "OBJ File Loader";

    public OBJLoader(Context context, String file){
        Vector<Vector3f> vertCoords = new Vector<>();
        Vector<Point2f> textCoords = new Vector<>();
        Vector<Vector3f> normCoords = new Vector<>();
        Vector<Short> vIndBuf = new Vector<>();
        Vector<Short> tIndBuf = new Vector<>();
        Vector<Short> nIndBuf = new Vector<>();

        try {
            List<String> fileContent = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(file)));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                fileContent.add(mLine);
            }

            for (String row: fileContent) {
                List<String> contents = Arrays.asList(row.replaceAll("(^\\s+|\\s+$)", "").split("\\s+"));
                switch (contents.get(0)){
                    case "v":
//                        Log.i(TAG, "OBJLoader: " + contents);
                        vertCoords.add(new Vector3f(Float.valueOf(contents.get(1)), Float.valueOf(contents.get(2)), Float.valueOf(contents.get(3))));
                        break;
                    case "vt":
//                        Log.i(TAG, "OBJLoader: " + contents);
                        textCoords.add(new Point2f(Float.valueOf(contents.get(1)), Float.valueOf(contents.get(2))));
                        break;
                    case "vn":
//                        Log.i(TAG, "OBJLoader: " + contents);
                        normCoords.add(new Vector3f(Float.valueOf(contents.get(1)), Float.valueOf(contents.get(2)), Float.valueOf(contents.get(3))));
                        break;
                    case "f":
//HA NEM TEXTURED, LEGYEN COLOR ATADVA
                        if(contents.size() == 4) {
                            for (int i = 1; i < 4; ++i) {
                                String[] parts = contents.get(i).split("/");
                                //EZ
                                if (parts[1] == "") {
                                    vIndBuf.add(Short.valueOf(parts[0]));
                                    nIndBuf.add(Short.valueOf(parts[2]));
                                    hasNoTex = true;
                                } else {
                                    vIndBuf.add(Short.valueOf(parts[0]));
                                    tIndBuf.add(Short.valueOf(parts[1]));
                                    nIndBuf.add(Short.valueOf(parts[2]));
                                }
                            }
                        }else{
                            for (int i = 1; i < 4; ++i) {
                                String[] parts = contents.get(i).split("/");
                                //EZ
                                if (parts[1] == "") {
                                    vIndBuf.add(Short.valueOf(parts[0]));
                                    nIndBuf.add(Short.valueOf(parts[2]));
                                    hasNoTex = true;
                                } else {
                                    vIndBuf.add(Short.valueOf(parts[0]));
                                    tIndBuf.add(Short.valueOf(parts[1]));
                                    nIndBuf.add(Short.valueOf(parts[2]));
                                }
                            }
                            String[] parts = contents.get(1).split("/");
                            vIndBuf.add(Short.valueOf(parts[0]));
                            tIndBuf.add(Short.valueOf(parts[1]));
                            nIndBuf.add(Short.valueOf(parts[2]));

                            parts = contents.get(3).split("/");
                            vIndBuf.add(Short.valueOf(parts[0]));
                            tIndBuf.add(Short.valueOf(parts[1]));
                            nIndBuf.add(Short.valueOf(parts[2]));

                            parts = contents.get(4).split("/");
                            vIndBuf.add(Short.valueOf(parts[0]));
                            tIndBuf.add(Short.valueOf(parts[1]));
                            nIndBuf.add(Short.valueOf(parts[2]));
                        }
                        break;
                    default: break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int posX = 0;
        numFaces = vIndBuf.size();
        this.normals = new float[numFaces * 3];
        this.textureCoordinates = new float[numFaces * 2];
        this.positions = new float[numFaces * 3];

        for(Short index: vIndBuf){
            positions[posX++] = vertCoords.get( index - 1 ).x;
            positions[posX++] = vertCoords.get( index - 1 ).y;
            positions[posX++] = vertCoords.get( index - 1 ).z;
        }
        posX = 0;
        for(Short index: tIndBuf){
            textureCoordinates[posX++] = textCoords.get( index - 1 ).x;
            textureCoordinates[posX++] = 1 - textCoords.get( index - 1 ).y;
        }
        posX = 0;
        for(Short index: nIndBuf){
            normals[posX++] = normCoords.get( index - 1 ).x;
            normals[posX++] = normCoords.get( index - 1 ).y;
            normals[posX++] = normCoords.get( index - 1 ).z;
        }
    }
}
