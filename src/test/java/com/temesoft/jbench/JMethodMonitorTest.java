package com.temesoft.jbench;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

/**
 * Demo executable of @JMethodMonitor functionality
 */
public class JMethodMonitorTest
    extends TestCase
{
    @Test
    public void testJMethodMonitor()
    {
        JMethodMonitorService service = new JMethodMonitorService();
        final JMethodMonitorTest test = new JMethodMonitorTest();
        for (int i = 0; i < 10; i++)
        {
            test.getUUID();
            test.empty();
        }
        for (Map.Entry<String, JMethodMonitorStatistics> stats : service.getAllStats().entrySet())
        {
            System.out.println(stats.getValue());
        }
        System.out.println("\n\nFinished\n\n");
    }

    @JMethodMonitor
    public String getUUID()
    {
        return UUID.randomUUID().toString();
    }

    @JMethodMonitor
    public String empty()
    {
        return null;
    }
}
