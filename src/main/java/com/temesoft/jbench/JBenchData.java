package com.temesoft.jbench;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * JBenchData - object holding the statistical metrics for the benchmark execution
 */
public class JBenchData implements Serializable {

    private String name;
    private double timePassedNs;
    private double timePassedMs;
    private long iterations;
    private double speedNs;
    private double speedMs;
    private double averageNs;
    private double averageMs;

    public JBenchData(final String name,
                      final double timePassedNs,
                      final double timePassedMs,
                      final long iterations,
                      final double speedNs,
                      final double speedMs,
                      final double averageNs,
                      final double averageMs) {
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
    public String toString() {
        return new StringJoiner(", ", JBenchData.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("timePassedNs=" + timePassedNs)
                .add("timePassedMs=" + timePassedMs)
                .add("iterations=" + iterations)
                .add("speedNs=" + speedNs)
                .add("speedMs=" + speedMs)
                .add("averageNs=" + averageNs)
                .add("averageMs=" + averageMs)
                .toString();
    }

    public String getName() {
        return name;
    }

    public double getTimePassedNs() {
        return timePassedNs;
    }

    public double getTimePassedMs() {
        return timePassedMs;
    }

    public long getIterations() {
        return iterations;
    }

    public double getSpeedNs() {
        return speedNs;
    }

    public double getSpeedMs() {
        return speedMs;
    }

    public double getAverageNs() {
        return averageNs;
    }

    public double getAverageMs() {
        return averageMs;
    }
}
