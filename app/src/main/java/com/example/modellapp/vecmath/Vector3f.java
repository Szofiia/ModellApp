package com.example.modellapp.vecmath;

public class Vector3f{
    public float x;
    public float y;
    public float z;

    public Vector3f() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3f(float _x, float _y, float _z) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
    }

    public Vector3f(float v[]) {
        this.x = v[0];
        this.y = v[1];
        this.z = v[2];
    }

    public Vector3f(double v[]) {
        this.x = (float) v[0];
        this.y = (float) v[1];
        this.z = (float) v[2];
    }

    public Vector3f(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public final float lengthSquared() {
        return x*x + y*y + z*z;
    }

    public final float length() {
        return (float)Math.sqrt(lengthSquared());
    }

    public final void cross(Vector3f u, Vector3f v) {
        this.x = u.y * v.z - u.z * v.y;
        this.y = u.z * v.x - u.x * v.z;
        this.z = u.x * v.y - u.y * v.x;
    }

    public final float dot(Vector3f v) {
        return x*v.x + y*v.y + z*v.z;
    }

    public final void normalize() {
        double d = length();
        if(d != 0) {
            this.x /= d;
            this.y /= d;
            this.z /= d;
        }
    }

}
