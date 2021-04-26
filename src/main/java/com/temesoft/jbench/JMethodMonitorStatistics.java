package com.temesoft.jbench;

import java.io.Serializable;
import java.util.StringJoiner;

public class JMethodMonitorStatistics implements Serializable {

    private String className;
    private String methodName;
    private long minTime;
    private long maxTime;
    private long lastTime;
    private double avgTime;
    private long callCount;

    public JMethodMonitorStatistics(final String className,
                                    final String methodName,
                                    final long minTime,
                                    final long maxTime,
                                    final double avgTime,
                                    final long lastTime,
                                    final long callCount) {
        this.className = className;
        this.methodName = methodName;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.avgTime = avgTime;
        this.callCount = callCount;
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JMethodMonitorStatistics.class.getSimpleName() + "[", "]")
                .add("className='" + className + "'")
                .add("methodName='" + methodName + "'")
                .add("minTime=" + minTime)
                .add("maxTime=" + maxTime)
                .add("lastTime=" + lastTime)
                .add("avgTime=" + avgTime)
                .add("callCount=" + callCount)
                .toString();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getMinTime() {
        return minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public double getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(double avgTime) {
        this.avgTime = avgTime;
    }

    public long getCallCount() {
        return callCount;
    }

    public void setCallCount(long callCount) {
        this.callCount = callCount;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
}
