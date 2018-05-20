package szsofia.thesis.modellapp.tools;

import szsofia.thesis.modellapp.vecmath.Angles;
import szsofia.thesis.modellapp.vecmath.Vector3f;

import java.util.Arrays;
import java.util.List;

public class SavedStage {
    String FILE;
    int TEXTURE;
    Vector3f eye;
    Vector3f lookAt;
    Vector3f up;
    Angles angles;
    float scale;
    boolean[] lights;

    public SavedStage(Vector3f _eye, Vector3f _lookAt, Vector3f _up, Angles _angles, float _scale, boolean[] _lights, String _FILE, int _TEXTURE){
        this.eye = _eye;
        this.lookAt = _lookAt;
        this.up = _up;
        this.angles = _angles;
        this.scale = _scale;
        this.lights = _lights;
        this.FILE = _FILE;
        this.TEXTURE = _TEXTURE;
    }

    public SavedStage(String rawStr){
        List<String> data = Arrays.asList(rawStr.split(","));
        FILE = new String();
        TEXTURE = 0;
        eye = new Vector3f();
        lookAt = new Vector3f();
        up = new Vector3f();
        angles = new Angles();
        lights = new boolean[7];
        scale = 0;

        eye.x = Float.parseFloat((data.get(0)));
        eye.y = Float.parseFloat((data.get(1)));
        eye.z = Float.parseFloat((data.get(2)));

        lookAt.x = Float.parseFloat((data.get(3)));
        lookAt.y = Float.parseFloat((data.get(4)));
        lookAt.z = Float.parseFloat((data.get(5)));

        up.x = Float.parseFloat((data.get(6)));
        up.y = Float.parseFloat((data.get(7)));
        up.z = Float.parseFloat((data.get(8)));

        angles.setTheta( Float.parseFloat((data.get(9))) );
        angles.setFi( Float.parseFloat((data.get(10))) );
        scale = Float.parseFloat((data.get(11)));

        for(int i = 12; i < 19; ++i){
            lights[i - 12] = Boolean.parseBoolean(data.get(i));}

        FILE = data.get(19);
        TEXTURE = Integer.parseInt(data.get(20));

    }

    @Override
    public String toString() {
        String result = "";

        result += eye.x + ",";
        result += eye.y + ",";
        result += eye.z + ",";

        result += lookAt.x + ",";
        result += lookAt.y + ",";
        result += lookAt.z + ",";

        result += up.x + ",";
        result += up.y + ",";
        result += up.z + ",";

        result += angles.getTheta() + ",";
        result += angles.getFi() + ",";
        result += scale + ",";

        for(int i = 0; i < 7; ++i){
            result += lights[i] + ",";}

        result += FILE + ",";
        result += TEXTURE;


        return result;
    }

    public Vector3f getEye() {
        return new Vector3f(eye.x, eye.y, eye.z);
    }
    public Vector3f getLookAt() {
        return new Vector3f(lookAt.x, lookAt.y, lookAt.z);
    }
    public Vector3f getUp() {
        return new Vector3f(up.x, up.y, up.z);
    }
    public Angles getAngles() {
        return angles;
    }
    public float getScale() {
        return scale;
    }
    public int getTEXTURE() {
        return TEXTURE;
    }
    public String getFILE() {
        return FILE;
    }
    public boolean[] getLights() {
        return lights;
    }
}
