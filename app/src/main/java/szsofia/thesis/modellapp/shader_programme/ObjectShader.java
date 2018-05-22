package szsofia.thesis.modellapp.shader_programme;

public class ObjectShader {
    private final static String objVertexShader =
            "uniform mat4 u_MVPMatrix;" +
            "uniform mat4 u_MVMatrix;" +

            "attribute vec4 vs_in_Position;" +
            "attribute vec3 vs_in_Normal;" +
            "attribute vec2 vs_in_Tex_coordinate;" +

            "varying vec3 fs_in_Position;" +
            "varying vec3 fs_in_Normal;" +
            "varying vec2 fs_in_Tex_coordinate;" +

            "void main(){" +
                "gl_Position = u_MVPMatrix * vs_in_Position; " +

                "fs_in_Position = vec3(u_MVMatrix * vs_in_Position);" +
                "fs_in_Tex_coordinate = vs_in_Tex_coordinate;" +
                "fs_in_Normal = vec3(u_MVMatrix * vec4(vs_in_Normal, 0.0));" +
            "}";

    private final static String objFragmentShader =
            "precision mediump float;" +
            "uniform vec4 ambient;" +
            "uniform vec4 diffuse;" +
            "uniform vec4 specular;" +

            "uniform vec3 u_LightPos; " +
            "uniform sampler2D s_Texture;" +

            "varying vec3 fs_in_Position;" +
            "varying vec3 fs_in_Normal;" +
            "varying vec2 fs_in_Tex_coordinate;" +

            "varying vec4 fs_out_Position;" +
            "varying vec4 fs_out_Normal;" +
            "varying vec4 fs_out_Ambient;" +
            "varying vec4 fs_out_Diffuse;" +
            "varying vec4 fs_out_Specular;" +

            "void main(){" +
                "float distance = length(u_LightPos - fs_in_Position);" +
                "vec3 lightVector = normalize(u_LightPos - fs_in_Position);" +

                "float diff = max(dot(fs_in_Normal, lightVector), 0.0);" +
                "diff = diff * (1.0 / (0.2 + (0.2 * distance)));" +
                "diff = diff + 0.3;" +

                "gl_FragColor = (diff  *  texture2D(s_Texture, fs_in_Tex_coordinate));" +
            "}";

    public static String getOVS(){
        return objVertexShader;
    }

    public static String getOFS(){
        return objFragmentShader;
    }

}
