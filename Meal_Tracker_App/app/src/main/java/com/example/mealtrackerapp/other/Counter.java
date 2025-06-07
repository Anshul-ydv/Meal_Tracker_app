package com.example.mealtrackerapp.other;

/**
 * Counter class to handle water counter
 */
public class Counter {
    //count
    private int count;
    //starting value
    private int startingValue;
    //min value for the count
    private int minValue;
    //max value for the count
    private int maxValue;

    public Counter(int startingValue, int minValue, int maxValue) {
        this.startingValue = startingValue;
        this.count = startingValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Counter() {
        this(0, 0, 100);
    }

    public int getCount() {
        return count;
    }

    //get string value of the count
    public String getCountString() {
        return Integer.toString(count);
    }

    public void setCount(int count) {
        this.count = count;
    }

    //Funtion taaki paani MAX count se upar na jaae
    public void add(int value) {
        if ((count + value) > maxValue){
            this.count = maxValue;
        } else {
            this.count += value;
        }
    }

    //Funtion taaki paani Min count se niche na jaae
    public void remove(int value) {
        if ((count - value) < minValue){
            this.count = minValue;
        } else {
            this.count -= value;
        }
    }
}
