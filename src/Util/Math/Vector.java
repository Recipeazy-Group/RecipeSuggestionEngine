package Util.Math;

import java.util.Collection;

public class Vector<T> {
    private T[] data;

    private final Class type;

    private final int size;

    public Vector(Collection<T> data) {
        this((T[])data.toArray());
    }

    public Vector(T[] data) {
        size = data.length;
        type = data[0].getClass();
        this.data = (T[]) new Object[size];
        for(int i=0; i<size; i++){
            this.data[i]=data[i];
        }
    }

    public Vector(int size) {
        this((T[]) new Object[size]);
    }

    private boolean operationalTypeCheck() {
        return (type != Double.class && type != Integer.class && type != Long.class);
        //Used to check if arithmetic can be done on a vector of this type
    }

    public T at(int index) {
        return data[index];
    }

    public static double dotProduct(Vector a, Vector b) {
        if (a.size != b.size)
            throw new RuntimeException("Vector: dot product attempted on vectors of different sizes.");
        if (a.type != b.type || a.operationalTypeCheck())
            throw new RuntimeException("Vector: current element type " + a.type + " is not supported by dotProduct(..)");
        double sum = 0;
        for (int i = 0; i < a.size; i++) {
            sum += (double) a.data[i] * (double) b.data[i];
        }
        return sum;
    }

    public double dotProduct(Vector b) {
        return dotProduct(this, b);
    }

    public static double getNorm(Vector v) {
        return Math.sqrt(dotProduct(v, v)); //The norm of a vector is equal to the sqrt of dot product of itself by itself
    }

    public double getNorm() {
        return getNorm(this);
    }

    public Vector<T> clone() {
        return new Vector<T>(data);
    }

    public void plusEquals(Vector<T> toAdd) {
        if (toAdd.operationalTypeCheck())
            throw new RuntimeException("Vector: current element type " + toAdd.type + " is not supported by plusEquals(..)");
        if (toAdd.size != size)
            throw new RuntimeException("Vector: dimension mismatch on plusEquals(..)");
        for (int i = 0; i < size; i++) {
            data[i] = (T) ((Double) ((double) data[i] + (double) toAdd.data[i]));
        }
    }

    public void normalize() {
        if (operationalTypeCheck())
            throw new RuntimeException("Vector: current element type " + type + " is not supported by normalize(..)");
        double mag = getNorm();
        for (int i = 0; i < size; i++) {
            Double normalizedVal = (Double) data[i] / mag;
            data[i] = (T) normalizedVal;
        }
    }

    public Vector<T> getNormalized() {
        if (operationalTypeCheck())
            throw new RuntimeException("Vector: current element type " + type + " is not supported by getNormalized(..)");
        Vector<T> normalized = new Vector<T>(size);
        double mag = normalized.getNorm();
        for (int i = 0; i < size; i++) {
            Double normalizedVal = (Double) normalized.data[i] / mag;
            normalized.data[i] = (T) normalizedVal;
        }
        return normalized;
    }

    public static Vector getNormalized(Vector toNorm) {
        return toNorm.getNormalized();
    }

    public void add(Vector<T> v){
        if(operationalTypeCheck())
            throw new RuntimeException("Vector: current element type " + type + " is not supported by add(..)");
        for(int i=0; i<size; i++){
            Double elSum = (Double)data[i] + (Double)v.data[i];
            data[i] = (T)elSum;
        }
    }

    public int getSize(){
        return data.length;
    }

}
