package com.temesoft.jbench;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Component
public class JMethodMonitorService {
    private static final String DOT = ".";

    static {
        System.out.println(String.format(
                "%s - started",
                JMethodMonitorService.class.getSimpleName()));
    }

    private static final Map<String, JMethodMonitorStatistics> methodMonitorStats =
            new ConcurrentHashMap<String, JMethodMonitorStatistics>();

    /**
     * This is the method to retrieve all available method monitoring statistics data
     */
    public Map<String, JMethodMonitorStatistics> getAllStats() {
        return methodMonitorStats;
    }

    /**
     * Clear all available method monitoring statistics data
     */
    public void clear() {
        methodMonitorStats.clear();
    }

    /**
     * Clear specific method monitoring statistics data
     */
    public void remove(String className, String methodName) {
        methodMonitorStats.remove(createKey(className, methodName));
    }

    /**
     * Adds / calculates avg, min, max for the new value (timeInMs) for
     * provided className and methodName. All params are required.
     */
    public JMethodMonitorStatistics addNewValues(final String className,
                                                 final String methodName,
                                                 final long timeInMs) {
        final JMethodMonitorStatistics result;
        final String key = createKey(className, methodName);
        if (!methodMonitorStats.containsKey(key)) {
            result = new JMethodMonitorStatistics(className, methodName, timeInMs, timeInMs, timeInMs, timeInMs, 1);
            methodMonitorStats.put(key, result);
        } else {
            result = methodMonitorStats.get(key);
            if (timeInMs < result.getMinTime()) {
                result.setMinTime(timeInMs);
            }
            if (timeInMs > result.getMaxTime()) {
                result.setMaxTime(timeInMs);
            }
            result.setLastTime(timeInMs);
            result.setAvgTime(((result.getAvgTime() * (double) result.getCallCount()) + (double) timeInMs)
                    / (double) (result.getCallCount() + 1));
            result.setCallCount(result.getCallCount() + 1);
        }
        return result;
    }

    private String createKey(final String className, final String methodName) {
        return className + DOT + methodName;
    }
}
