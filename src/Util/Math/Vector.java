package Util.Math;

import java.util.List;
import java.util.Arrays;

public class Vector<T> {
    private T[] data;

    private final Class type;

    private final int size;

    public Vector(List<T> data) {
        size = data.size();
        type = data.get(0).getClass();
        for (int i = 0; i < data.size(); i++) {
            this.data[i] = data.get(i);
        }
    }

    public Vector(T[] data) {
        this(Arrays.asList(data));
    }

    public T at(int index) {
        return data[index];
    }

    public static double dotProduct(Vector a, Vector b) {
        if (a.size != b.size)
            throw new RuntimeException("Vector: dot product attempted on vectors of different sizes.");
        if (a.type != b.type || (b.type != Double.class && b.type != Integer.class && b.type != Long.class))
            throw new RuntimeException("Vector: current element type " + a.type + " is not supported by dotProduct(..)");
        double sum = 0;
        for (int i = 0; i < a.size; i++) {
            sum += (double)a.data[i] * (double)b.data[i];
        }
        return sum;
    }

    public double dotProduct(Vector b) {
        return dotProduct(this, b);
    }

    public static double getNorm(Vector v){
       return dotProduct(v, v); //The norm of a vector is equal to the dot product of itself by itself
    }

    public double getNorm(){
        return getNorm(this);
    }

    public Vector<T> clone(){
        return new Vector<T>(data);
    }

}
