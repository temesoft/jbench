package com.temesoft.jbench;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demo spring bean displaying @JMethodMonitor functionality
 */
@Component
@JBench
@JMethodMonitor
public class SomeServiceBean
{
    private final Random rnd = new Random();

    public String getUUID()
    {
        return UUID.randomUUID().toString();
    }

    @JBench(maxIterations =  10000)
    public final void nextRandomDouble()
    {
        rnd.nextDouble();
    }

    @JBench(maxIterations =  10000)
    public void sqrtOfRandom()
    {
        Math.sqrt(rnd.nextDouble());
    }

    @JBench(maxIterations =  10000)
    public void collectionsSingletonList()
    {
        Collections.singletonList(rnd.nextDouble());
    }

    @JBench(maxIterations =  10000)
    public void newArrayList()
    {
        new ArrayList<Double>();
    }

    @JBench(maxIterations =  10000)
    public void newArrayListSynchronized()
    {
        Collections.synchronizedList(new ArrayList<Double>());
    }

    @JBench(maxIterations =  10000)
    public void newHashMap()
    {
        new HashMap<Double, Double>();
    }

    @JBench(maxIterations =  10000)
    public void newHashMap_Synchronized()
    {
        Collections.synchronizedMap(new HashMap<Double, Double>());
    }

    @JBench(maxIterations =  10000)
    public void newHashMap_Concurrent()
    {
        new ConcurrentHashMap<Double, Double>();
    }

    @JBench(maxIterations =  10000)
    public void uuid()
    {
        UUID.randomUUID().toString();
    }
}
