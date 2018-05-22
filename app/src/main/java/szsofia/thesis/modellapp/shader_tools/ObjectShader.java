package szsofia.thesis.modellapp.shader_tools;

public class ObjectShader {
    private final static String objVertexShader =
/*            "#version 300 es \n"+"" +
            "void main(){" +
            "}"*/
            "#version 300 es \n" +
            "uniform mat4 u_MVPMatrix;" +
            "uniform mat4 u_MVMatrix;" +

            "layout (location = 0) in vec4 vs_in_Position;" +
            "layout (location = 1) in vec3 vs_in_Normal;" +
            "layout (location = 2) in vec2 vs_in_Tex_coordinate;" +

            "out vec3 fs_in_Position;" +
            "out vec3 fs_in_Normal;" +
            "out vec2 fs_in_Tex_coordinate;" +

            "void main(){" +
                "gl_Position = u_MVPMatrix * vs_in_Position; " +

                "fs_in_Position = vec3(u_MVMatrix * vs_in_Position);" +
                "fs_in_Tex_coordinate = vs_in_Tex_coordinate;" +
                "fs_in_Normal = vec3(u_MVMatrix * vec4(vs_in_Normal, 0.0));" +
            "}";

    private final static String objFragmentShader =
/*            "#version 300 es \n"+
                    "" +
            "void main(){" +
            "}"*/
            "#version 300 es \n" +
            "uniform vec4 ambient;" +
            "uniform vec4 diffuse;" +
            "uniform vec4 specular;" +

            "uniform vec3 u_LightPos; " +
            "uniform sampler2D s_Texture;" +

            "out vec3 fs_in_Position;" +
            "out vec3 fs_in_Normal;" +
            "out vec2 fs_in_Tex_coordinate;" +

            "layout (location = 0) out vec4 fs_out_Position;" +
            "layout (location = 1) out vec4 fs_out_Normal;" +
            "layout (location = 3) out vec4 fs_out_Ambient;" +
            "out vec4 fs_out_Diffuse;" +
            "out vec4 fs_out_Specular;" +

            "void main(){" +
                "float distance = length(u_LightPos - fs_in_Position);" +
                "vec3 lightVector = normalize(u_LightPos - fs_in_Position);" +

                "float diff = max(dot(fs_in_Normal, lightVector), 0.0);" +
                "diff = diff * (1.0 / (0.2 + (0.10 * distance)));" +
                "diff = diff + 0.3;" +

                "gl_FragColor = (diffuse * texture2D(s_Texture, fs_in_Tex_coordinate));" +
            "}";

    public static String getOVS(){
        return objVertexShader;
    }

    public static String getOFS(){
        return objFragmentShader;
    }

}
