package com.temesoft.jbench;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * JBenchData - object haling the statistical metrics for the benchmark execution
 */
public class JBenchData
    implements Serializable
{

    private String name;
    private double timePassedNs;
    private double timePassedMs;
    private long iterations;
    private double speedNs;
    private double speedMs;
    private double averageNs;
    private double averageMs;

    public JBenchData(String name, double timePassedNs, double timePassedMs, long iterations, double speedNs,
                      double speedMs, double averageNs, double averageMs)
    {
        this.name = name;
        this.timePassedNs = timePassedNs;
        this.timePassedMs = timePassedMs;
        this.iterations = iterations;
        this.speedNs = speedNs;
        this.speedMs = speedMs;
        this.averageNs = averageNs;
        this.averageMs = averageMs;
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("JBenchData");
        sb.append("{name='").append(name).append('\'');
        sb.append(", timePassedNs=").append(timePassedNs);
        sb.append(", timePassedMs=").append(timePassedMs);
        sb.append(", iterations=").append(iterations);
        sb.append(", speedNs=").append(speedNs);
        sb.append(", speedMs=").append(speedMs);
        sb.append(", averageNs=").append(averageNs);
        sb.append(", averageMs=").append(averageMs);
        sb.append('}');
        return sb.toString();
    }

    public String getName()
    {
        return name;
    }

    public double getTimePassedNs()
    {
        return timePassedNs;
    }

    public double getTimePassedMs()
    {
        return timePassedMs;
    }

    public long getIterations()
    {
        return iterations;
    }

    public double getSpeedNs()
    {
        return speedNs;
    }

    public double getSpeedMs()
    {
        return speedMs;
    }

    public double getAverageNs()
    {
        return averageNs;
    }

    public double getAverageMs()
    {
        return averageMs;
    }
}
