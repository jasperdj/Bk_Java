package com.testing;

import java.util.Arrays;

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

    Double mean() {
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

    public Double median()
    {
        Arrays.sort(data);

        if (data.length % 2 == 0)
            return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
        else
            return new Double(data[data.length / 2]);
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

    public Double max() {
        Double max = 0.0;
        for (int value : data)
            max = Double.max(max, value);

        return max;
    }
}
