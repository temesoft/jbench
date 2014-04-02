package com.temesoft.jbench;

import java.io.Serializable;

/**
 * 
 */
public class JMethodMonitorStatistics
    implements Serializable
{
    private String className    = null;
    private String methodName   = null;
    private long minTime        = 0;
    private long maxTime        = 0;
    private long lastTime       = 0;
    private double avgTime      = 0;
    private long callCount      = 0;

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("JMethodMonitorStatistics");
        sb.append("{className='").append(className).append('\'');
        sb.append(", methodName='").append(methodName).append('\'');
        sb.append(", minTime=").append(minTime);
        sb.append(", maxTime=").append(maxTime);
        sb.append(", lastTime=").append(lastTime);
        sb.append(", avgTime=").append(avgTime);
        sb.append(", callCount=").append(callCount);
        sb.append('}');
        return sb.toString();
    }

    public JMethodMonitorStatistics(String className, String methodName, long minTime, long maxTime,
                                    double avgTime, long lastTime, long callCount)
    {
        this.className = className;
        this.methodName = methodName;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.avgTime = avgTime;
        this.callCount = callCount;
        this.lastTime = lastTime;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public long getMinTime()
    {
        return minTime;
    }

    public void setMinTime(long minTime)
    {
        this.minTime = minTime;
    }

    public long getMaxTime()
    {
        return maxTime;
    }

    public void setMaxTime(long maxTime)
    {
        this.maxTime = maxTime;
    }

    public double getAvgTime()
    {
        return avgTime;
    }

    public void setAvgTime(double avgTime)
    {
        this.avgTime = avgTime;
    }

    public long getCallCount()
    {
        return callCount;
    }

    public void setCallCount(long callCount)
    {
        this.callCount = callCount;
    }

    public long getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(long lastTime)
    {
        this.lastTime = lastTime;
    }
}
