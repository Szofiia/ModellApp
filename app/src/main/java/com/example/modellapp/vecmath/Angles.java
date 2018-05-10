package com.example.modellapp.vecmath;

public class Angles{
    private float theta;
    private float fi;

    public Angles(){}

    public Angles(float _theta, float _fi){
        this.theta = _theta;
        this.fi = _fi;
    }

    public Angles(Angles _angles){
        this.theta = _angles.theta;
        this.fi = _angles.fi;
    }

    public float getTheta() {
        return theta;
    }

    public float getFi() {
        return fi;
    }

    public void setTheta(float theta) {
        this.theta = theta;
    }

    public void setFi(float fi) {
        this.fi = fi;
    }

    public Angles getAngles() {
        return new Angles(theta, fi);
    }
}