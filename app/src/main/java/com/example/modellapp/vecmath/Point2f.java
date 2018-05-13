package com.example.modellapp.vecmath;

public class Point2f {
    public float x;
    public float y;

    public Point2f(){
        int x = 0;
        int y = 0;
    }

    public Point2f(float _x, float _y) {
        this.x = _x;
        this.y = _y;
    }

    public Point2f(Point2f p) {
        this.x = p.x;
        this.y = p.y;
    }

    public Point2f(float p[]) {
        this.x = p[0];
        this.y = p[1];
    }

    public final float lengthSquared() {
        return x*x + y*y;
    }

    public final float length() {
        return (float)Math.sqrt(lengthSquared());
    }

}
