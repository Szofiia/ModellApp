package com.example.modellapp.vecmath;

public class Angles{
    private float theta;
    private float fi;

    public Angles(){
        theta = 0;
        fi = 0;
    }

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
    public Angles getAngles() {
        return new Angles(theta, fi);
    }

    public void setTheta(float theta) {
        this.theta = theta;
    }
    public void setFi(float fi) {
        this.fi = fi;
    }
    public void setAngles(Angles _angles){
        setTheta(_angles.getTheta());
        setFi(_angles.getFi());
    }
}