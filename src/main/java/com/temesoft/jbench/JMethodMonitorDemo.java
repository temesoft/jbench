package com.temesoft.jbench;

import java.util.Map;
import java.util.UUID;

/**
 * Demo executable of @JMethodMonitor functionality
 */
public class JMethodMonitorDemo
{
    public static void main(String [] args) throws InterruptedException
    {
        JMethodMonitorService service = new JMethodMonitorService();
        final JMethodMonitorDemo demo = new JMethodMonitorDemo();
        for (int i = 0; i < 10; i++)
        {
            demo.getUUID();
            demo.empty();
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
