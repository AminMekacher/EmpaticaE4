package com.example.android.empaticae4;

/**
 * Created by aminmekacher on 26.07.18.
 */

// Class used to store the timestamp and the value of the accelerometer on Firebase

public class AccelValue {

    private int count;
    private int value;

    public AccelValue(int count, int value) {
        this.count = count;
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public int getValue() {
        return value;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
