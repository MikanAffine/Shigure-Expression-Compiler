package org.owari.shigure.util;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public final class DoubleArrayStack {
    private Object[] data;
    private int top = 0;

    public DoubleArrayStack() {
        this(8);
    }
    public DoubleArrayStack(int capacity){
        data = new Object[capacity];
    }

    public void push(double item) {
        if (top >= data.length) {
            data = Arrays.copyOf(data, data.length * 2);
        }
        data[top++] = item;
    }
    public double pop() {
        return (double) data[--top];
    }
    public int size() {
        return top;
    }

    public double[] popN(int n) {
        double[] result = new double[n];
        for(int i = 0; i < n; i++) {
            result[i] = pop();
        }
        return result;
    }
}
