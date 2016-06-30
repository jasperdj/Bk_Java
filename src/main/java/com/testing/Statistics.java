package com.testing;

import java.util.Arrays;

import static java.lang.Math.toIntExact;

/**
 * Created by a623557 on 2-6-2016.
 */
public class Statistics
{
    int[] data;
    int size;

    public Statistics(int[] data) {
        this.data = data;
        size = data.length;
    }

    double mean() {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    double variance()
    {
        double mean = mean();
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        return temp/size;
    }

    double stdDev()
    {
        return Math.sqrt(variance());
    }

    public double median()
    {
        Arrays.sort(data);

        if (data.length % 2 == 0)
            return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
        else
            return data[data.length / 2];
    }

    public Double stdError() {
        return stdDev() / Math.sqrt(size);
    }

    public Double min() {
        Double min = Double.MAX_VALUE;
        for(int value : data)
            min = Double.min(min, value);

        return min;
    }

    public int get25percentile() {
        Arrays.sort(data);
        int item = toIntExact(Math.round(Double.valueOf((double)data.length) * 0.25));
        return data[item];
    }

    public int get75percentile() {
        Arrays.sort(data);
        int item = toIntExact(Math.round(Double.valueOf((double)data.length) * 0.75))-1;
        return data[item];
    }

    public int get99percentile() {
        Arrays.sort(data);
        int item = toIntExact(Math.round(Double.valueOf((double)data.length) * 0.99))-1;
        return data[item];
    }

    public Double max() {
        Double max = 0.0;
        for (int value : data)
            max = Double.max(max, value);

        return max;
    }
}
