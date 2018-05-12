package com.example.modellapp.shaders;

public class ObjectShader {
    private final static String objVertexShader =
            "uniform mat4 u_MVPMatrix;" +
            "uniform mat4 u_MVMatrix;" +

            "attribute vec4 a_Position;" +
            "attribute vec3 a_Normal;" +
            "attribute vec2 a_TexCoordinate;" +

            "varying vec3 v_Position;" +
            "varying vec3 v_Normal;" +
            "varying vec2 v_TexCoordinate;" +

            "void main(){" +
                "gl_Position = u_MVPMatrix * a_Position; " +

                "v_Position = vec3(u_MVMatrix * a_Position);" +
                "v_TexCoordinate = a_TexCoordinate;" +
                "v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));" +

            "}";

    private final static String objFragmentShader =
            "precision mediump float;" +
            "uniform vec3 u_LightPos; " +
            "uniform sampler2D u_Texture;" +

            "varying vec3 v_Position;" +
            "varying vec3 v_Normal;" +
            "varying vec2 v_TexCoordinate;" +
            "void main(){" +
                "float distance = length(u_LightPos - v_Position);" +
                "vec3 lightVector = normalize(u_LightPos - v_Position);" +

                "float diffuse = max(dot(v_Normal, lightVector), 0.0);" +
                "diffuse = diffuse * (1.0 / (0.2 + (0.10 * distance)));" +
                "diffuse = diffuse + 0.3;" +

                "gl_FragColor = (diffuse * texture2D(u_Texture, v_TexCoordinate));" +
            "}";

    private final static String pointVertexShader =
            "uniform mat4 u_MVPMatrix;" +
            "attribute vec4 a_Position;" +
            "void main(){" +
                "gl_Position = u_MVPMatrix * a_Position;" +
                "gl_PointSize = 10.0;" +
            "}";

    private final static String pointFragmentShader =
            "precision mediump float;" +
            "void main(){" +
                "gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);" +
            "}";

    public static String getOVS(){
        return objVertexShader;
    }

    public static String getOFS(){
        return objFragmentShader;
    }

    public static String getPVS(){
        return pointVertexShader;
    }

    public static String getPFS(){
        return pointFragmentShader;
    }
}
