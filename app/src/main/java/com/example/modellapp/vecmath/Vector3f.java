package com.example.modellapp.vecmath;

import java.io.Serializable;

public class Vector3f extends Tuple3f implements Serializable{

    /**
     * Constructs and initializes a Vector3f from the specified xyz coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public Vector3f(float x, float y, float z) {
        super(x, y, z);
    }

    /**
     * Constructs and initializes a Vector3f from the specified array of length 3.
     * @param v the array of length 3 containing xyz in order
     */
    public Vector3f(float v[]) {
        super(v);
    }

    /**
     * Constructs and initializes a Vector3f from the specified Vector3f.
     * @param v1 the Vector3f containing the initialization x y z data
     */
    public Vector3f(Vector3f v1) {
        super(v1);
    }

    /**
     * Constructs and initializes a Vector3f from the specified Vector3d.
     * @param v1 the Vector3d containing the initialization x y z data
     */
    public Vector3f(Vector3d v1) {
        super(v1);
    }

    /**
     * Constructs and initializes a Vector3f from the specified Tuple3d.
     * @param t1 the Tuple3d containing the initialization x y z data
     */
    public Vector3f(Tuple3d t1) {
        super(t1);
    }

    /**
     * Constructs and initializes a Vector3f from the specified Tuple3f.
     * @param t1 the Tuple3f containing the initialization x y z data
     */
    public Vector3f(Tuple3f t1) {
        super(t1);
    }

    /**
     * Constructs and initializes a Vector3f to (0,0,0).
     */
    public Vector3f() {
        super();
    }

    /**
     * Returns the squared length of this vector.
     * @return the squared length of this vector
     */
    public final float lengthSquared() {
        return x*x + y*y + z*z;
    }

    /**
     * Returns the length of this vector.
     * @return the length of this vector
     */
    public final float length() {
        return (float)Math.sqrt(lengthSquared());
    }

    /**
     * Sets this vector to be the vector cross product of vectors v1 and v2.
     * @param v1 the first vector
     * @param v2 the second vector
     */
    public final void cross(Vector3f v1, Vector3f v2) {
        // store in tmp once for aliasing-safty
        // i.e. safe when a.cross(a, b)
        set(
                v1.y*v2.z - v1.z*v2.y,
                v1.z*v2.x - v1.x*v2.z,
                v1.x*v2.y - v1.y*v2.x
        );
    }


    /**
     * Computes the dot product of the this vector and vector v1.
     * @param  v1 the other vector
     */
    public final float dot(Vector3f v1) {
        return x*v1.x + y*v1.y + z*v1.z;
    }

    /**
     * Sets the value of this vector to the normalization of vector v1.
     * @param v1 the un-normalized vector
     */
    public final void normalize(Vector3f v1) {
        set(v1);
        normalize();
    }

    /**
     * Normalizes this vector in place.
     */
    public final void normalize() {
        double d = length();

        // zero-div may occur.
        x /= d;
        y /= d;
        z /= d;
    }

    /**
     * Returns the angle in radians between this vector and
     * the vector parameter; the return value is constrained to the
     * range [0,PI].
     * @param v1  the other vector
     * @return the angle in radians in the range [0,PI]
     */
    public final float angle(Vector3f v1) {
        // return (double)Math.acos(dot(v1)/v1.length()/v.length());
        // Numerically, near 0 and PI are very bad condition for acos.
        // In 3-space, |atan2(sin,cos)| is much stable.

        double xx = y*v1.z - z*v1.y;
        double yy = z*v1.x - x*v1.z;
        double zz = x*v1.y - y*v1.x;
        double cross = Math.sqrt(xx*xx + yy*yy + zz*zz);

        return (float)Math.abs(Math.atan2(cross, dot(v1)));
    }
}
