package com.example.android.empaticae4;

/**
 * Created by aminmekacher on 19.07.18.
 */

// Class used to store the timestamp and any data sent by the wristband (except the acceleration) to Firebase

public class FirebaseValue {

    private int count;
    private float value;

    public FirebaseValue(int count, float value) {
        this.count = count;
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
