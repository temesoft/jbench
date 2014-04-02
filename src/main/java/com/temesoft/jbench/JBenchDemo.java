package com.temesoft.jbench;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demo executable of @JBench functionality
 */
@JBench
public class JBenchDemo
{
    public static final Random rnd = new Random();

    public static void main(String [] args) throws InterruptedException
    {
        JMethodMonitorService service = new JMethodMonitorService();
        JBenchRunner.executeAll(true);
        for (Map.Entry<String, JMethodMonitorStatistics> stats : service.getAllStats().entrySet())
        {
            System.out.println(stats.getValue());
        }
        System.out.println("\n\nFinished\n\n");
    }

    @JBench(maxIterations =  10000)
    public final void nextRandomDouble()
    {
        double d = rnd.nextDouble();
    }

    @JBench(maxIterations =  10000)
    public void sqrtOfRandom()
    {
        double d = rnd.nextDouble();
        Math.sqrt(d);
    }

    @JBench(maxIterations =  10000)
    public void collectionsSingletonList()
    {
        List<Double> doubleList = Collections.singletonList(rnd.nextDouble());
    }

    @JBench(maxIterations =  10000)
    public void newArrayList()
    {
        List<Double> doubleList = new ArrayList<Double>();
    }

    @JBench(maxIterations =  10000)
    public void newArrayListSynchronized()
    {
        List<Double> doubleList = Collections.synchronizedList(new ArrayList<Double>());
    }

    @JBench(maxIterations =  10000)
    public void newHashMap()
    {
        Map<Double, Double> doubleList = new HashMap<Double, Double>();
    }

    @JBench(maxIterations =  10000)
    public void newHashMap_Synchronized()
    {
        Map<Double, Double> doubleList = Collections.synchronizedMap(new HashMap<Double, Double>());
    }

    @JBench(maxIterations =  10000)
    public void newHashMap_Concurrent()
    {
        Map<Double, Double> doubleList = new ConcurrentHashMap<Double, Double>();
    }

    @JBench(maxIterations =  10000)
    public void uuid()
    {
        String s = UUID.randomUUID().toString();
    }
}
