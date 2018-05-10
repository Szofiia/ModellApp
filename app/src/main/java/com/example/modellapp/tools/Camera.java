package com.example.modellapp.tools;

import com.example.modellapp.vecmath.Angles;
import com.example.modellapp.vecmath.Vector3f;

import java.util.Arrays;
import java.util.List;

public class Camera {

    Vector3f eye;
    Vector3f lookAt;
    Vector3f up;
    Angles angles;

    public Camera( Vector3f _eye, Vector3f _lookAt, Vector3f _up, Angles _angles){
        this.eye = _eye;
        this.lookAt = _lookAt;
        this.up = _up;
        this.angles = _angles;
    }

    public Camera(String rawStr){
        List<String> data = Arrays.asList(rawStr.split(","));
        eye = new Vector3f();
        lookAt = new Vector3f();
        up = new Vector3f();
        angles = new Angles();

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
        result += angles.getFi();

        return result;
    }

    public Vector3f getEye() {
        return new Vector3f(eye.x, eye.y, eye.z);
    }
    public Vector3f getLook() {
        return new Vector3f(lookAt.x, lookAt.y, lookAt.z);
    }
    public Vector3f getUp() {
        return new Vector3f(up.x, up.y, up.z);
    }
    public Angles getAngles() {
        return angles;
    }
}
