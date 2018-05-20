package szsofia.thesis.modellapp.vecmath;
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
    public Angles(double _theta, double _fi){
        this.theta = (float) _theta;
        this.fi = (float) _fi;
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
    public void setTheta(double theta) {
        this.theta = (float)theta;
    }
    public void setFi(float fi) {
        this.fi = fi;
    }
    public void setFi(double fi) {
        this.fi = (float)fi;
    }
    public void setAngles(Angles _angles){
        setTheta(_angles.getTheta());
        setFi(_angles.getFi());
    }

    public void add(float _theta, float _fi){
        this.theta = _theta;
        this.fi = _fi;
    }
    public void add(Angles _angles){
        this.theta += _angles.theta;
        this.fi += _angles.fi;
    }

    public void transformSpheric(){
        if (theta >= 2 * Math.PI){
            theta -= 2 * Math.PI;
        }
        if( theta < 0){
            theta += Math.PI;
        }
        if (fi > 2 * Math.PI ){
            fi -= 2 * Math.PI;
        }
        if (fi < 0){
            fi += 2 * Math.PI;
        }
    }
}